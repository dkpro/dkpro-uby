package de.tudarmstadt.ukp.uby.uima.writer;


import static de.tudarmstadt.ukp.uby.resource.UbyResourceUtils.getMostFrequentSense;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticField;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;

/**
 * @author Eckle-Kohler
 *
 */
public class SemanticTagWriter
    extends org.apache.uima.fit.component.JCasAnnotator_ImplBase
{
    /**
     * Name of the output file
     */
    public static final String PARAM_TARGET_LOCATION = "outputParam";
    @ConfigurationParameter(name = PARAM_TARGET_LOCATION, mandatory = true)
    private String outputParam;

    public static final String RES_UBY = "uby";
    @ExternalResource(key = RES_UBY)
    private Uby uby;

    private BufferedWriter writer;

    private static ArrayList<String> auxiliariesAndModals = new ArrayList<String>(
    	    Arrays.asList("be", "do", "have", "can", "will", "should", "must"));
    

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        try {
            writer = new BufferedWriter(new FileWriter(outputParam));
        }
        catch (IOException ex) {
            throw new ResourceInitializationException(ex);
		}

    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {

            List<Token> sentenceTokens = JCasUtil.selectCovered(jcas, Token.class, sentence);
            for (int i = 0; i < sentenceTokens.size(); i++) {
                Token token = sentenceTokens.get(i);
                Sense mfs = null;
                
            	// Uby provides lexical information mainly for content words: nouns, main verbs, adjectives;
            	// auxiliary and modal verbs are contained in Uby, but in running text they are rarely used as main verbs,
            	// but mostly as function words (to form particular tense and voice constructions) or as modality markers
                if (token.getPos().getType().getShortName().equals("V") || 			
                		token.getPos().getType().getShortName().matches("N.*") || 
                		token.getPos().getType().getShortName().equals("ADJ")  ) {
                    if ((!auxiliariesAndModals.contains(token.getLemma().getValue()))) {
                        mfs = getMostFrequentSense(uby.getLexicalEntries(token.getLemma().getValue(), null, null));
                    }
                }                

                // write lemma, POS annotations and results of Uby lookup to the output file:
                String syntacticBehaviour = getSyntacticBehaviour(token.getPos().getType().getShortName(),uby.getLexicalEntries(token.getLemma().getValue(), 
                		EPartOfSpeech.verb, null));              
                List<SemanticField> semanticFieldAnnotations = JCasUtil.selectCovering(jcas,
                        SemanticField.class, token.getBegin(), token.getEnd());
                for (int j = 0; j < semanticFieldAnnotations.size(); j++) {
                    SemanticField semanticField = semanticFieldAnnotations.get(j);
                    String semFieldValue = "---";
                    
                    if (semanticField.getValue().equals("UNKNOWN")) {
                    	semFieldValue = "---";
                    } else {
                    	semFieldValue = semanticField.getValue();
                    }
                    
                	if  (!(mfs == null) && !(mfs.getSynset() == null) 
                    			&& (!auxiliariesAndModals.contains(token.getLemma().getValue()))) {
                   		
                        writeTokenAndSemanticField(token.getCoveredText() + "\t"
                                + token.getLemma().getValue() + "\t"
                                + token.getPos().getType().getShortName() + "\n"
                                + "\t syntax: " +syntacticBehaviour + "\n"
                                
                                // for retrieving semantic field, synonyms and semantically related words, the word is disambiguated
                                // according to the MFS heuristic
                                + "\t semantic field: " +semFieldValue + "\n"                                
                                + "\t synonyms: " +getSynonymousWords(token.getLemma().getValue(), mfs.getSynset()) + "\n"
                                + "\t related: " +getSemanticallyRelatedWords(mfs.getSynset()) + "\n"
                                
                                // "associated topics" means something like creatively associating topics with a given word
                                // for constructing creative associations, disambiguation is not necessary (it actually limits association links)
                                + "\t associated: " +getSemanticLabels(uby.getLexicalEntries(token.getLemma().getValue(), null, null)) + "\n"                                
                        		);
                    } else {
                        writeTokenAndSemanticField(token.getCoveredText() + "\t"
                                + token.getLemma().getValue() + "\t"
                                + token.getPos().getType().getShortName() + "\n"
                                );
                    }
                }
            }
        }
    }

    /*
     * This method groups the complex subcat frames into four classes: 
     * transitive, intransitive, transitive with to-infinitive, intransitive with to-infinitive 
     * this four-way classification could be useful in many (linguistic or text classification) contexts, 
     * because all four classes have a distinct lexical semantics
     */
    private String getSyntacticBehaviour(String pos, List<LexicalEntry> lexicalEntries) {
    	String result = "---";
    	int numberOfTransitiveFrames = 0;
    	int numberOfIntransitiveFrames = 0;
    	boolean withToInfinitive = false;
    	if (pos.equals("V")) {
	        for (LexicalEntry lexicalEntry : lexicalEntries) {
	            for (SyntacticBehaviour sb : lexicalEntry.getSyntacticBehaviours()) {
	        		try {
		            	SubcategorizationFrame scf = sb.getSubcategorizationFrame();
		            	List<SyntacticArgument> synArgs = scf.getSyntacticArguments();
	            		for (SyntacticArgument synArg : synArgs) {
	            			if (synArg.getSyntacticCategory().equals(ESyntacticCategory.verbPhrase) &&
	            					synArg.getVerbForm().equals(EVerbForm.toInfinitive)) {
	            				withToInfinitive = true;
	            			}
	            		}	

		            	if (synArgs.size() == 1) {
		            		numberOfIntransitiveFrames++;
		            	} 
		            	if (synArgs.size() >= 2) {
			            	if (synArgs.get(0).getSyntacticCategory().equals(ESyntacticCategory.nounPhrase) &&
			            			synArgs.get(1).getSyntacticCategory().equals(ESyntacticCategory.nounPhrase)) {
			            		numberOfTransitiveFrames++;			            		
			            	} else if (synArgs.get(0).getSyntacticCategory().equals(ESyntacticCategory.nounPhrase) &&
			            			synArgs.get(1).getSyntacticCategory().equals(ESyntacticCategory.prepositionalPhrase)) {
			            		numberOfIntransitiveFrames++;
			            	}
		            	} 
	    			} catch (NullPointerException e) {
	    				// sth wrong with subcat frame
	    			}
	            }
	        }  
    	}
        if (numberOfTransitiveFrames == 0 && numberOfIntransitiveFrames == 0) {
        	result = "---";
        } else if (numberOfTransitiveFrames > 0) {
        	if (withToInfinitive) {
        		result = "transitive/with_to-infinitive";
        	} else {
        		result = "transitive";
        	}
        } else if (numberOfTransitiveFrames == 0 && numberOfIntransitiveFrames > 0) {
        	if (withToInfinitive) {
        		result = "intransitive/with_to-infinitive";
        	} else {
        		result = "intransitive";
        	}

        }        
		return result;
	}

    
	private void writeTokenAndSemanticField(String string)
    {
        try {
            writer.write(string);
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getSemanticallyRelatedWords(Synset synset)
    {
    	String result = null;
    	HashSet<String> semanticallyRelatedWords = new HashSet<String>();
    	for (SynsetRelation synsetRel : synset.getSynsetRelations()) {
    		try {
    			for (Sense s : synsetRel.getTarget().getSenses()) {   				
    				if (s.getIndex() == 1) {
    					semanticallyRelatedWords.add(s.getLexicalEntry().getLemmaForm());
    				}
    			}
			} catch (NullPointerException e) {
				// sth wrong with target of synset relation
			}
    	}
    	if (semanticallyRelatedWords.isEmpty()) {
    		result = "---";
    	} else {
    		result = semanticallyRelatedWords.toString().replaceAll("\\[", "").replaceAll("\\]", "");
    	}
        return result;
    }
    
    private String getSynonymousWords(String lemma, Synset synset)
    {
    	String result = null;
    	HashSet<String> synonymousWords = new HashSet<String>();
    	for (Sense sense : synset.getSenses()) {
       		try {
	    		if (!lemma.equals(sense.getLexicalEntry().getLemmaForm())) {
	    			synonymousWords.add(sense.getLexicalEntry().getLemmaForm());
	    		}
			} catch (NullPointerException e) {
				// sth wrong with target of synset relation
			}
    	}
    	if (synonymousWords.isEmpty()) {
    		result = "---";
    	} else {
    		result = synonymousWords.toString().replaceAll("\\[", "").replaceAll("\\]", "");
    	}
        return result;
    }


    private String getSemanticLabels(List<LexicalEntry> lexicalEntries)
    {
       	String result = null;
    	HashSet<String> semanticLabelValues = new HashSet<String>();
        for (LexicalEntry lexicalEntry : lexicalEntries) {
            for (Sense s : lexicalEntry.getSenses()) {
            	try {
	                for (SemanticLabel sl : s.getSemanticLabels()) {
	                	if (!sl.getType().equals(ELabelTypeSemantics.verbnetClass) && !sl.getType().equals(ELabelTypeSemantics.semanticField)) {
	                		semanticLabelValues.add(sl.getLabel());
	                	}               	
	                }	           		
	                for (PredicativeRepresentation pr : s.getPredicativeRepresentations()) {
	                	semanticLabelValues.add(pr.getPredicate().getLabel().toLowerCase());
	                }
    			} catch (NullPointerException e) {
    				// no SemanticLabel type or label of SemanticPredicate is missing
    			}
            }
        }
    	if (semanticLabelValues.isEmpty()) {
    		result = "---";
    	} else {
    		result = semanticLabelValues.toString().replaceAll("\\[", "").replaceAll("\\]", "");
    	}
        return result;
    }

}
