/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.alignment.framework.example;

import java.io.IOException;
import java.sql.SQLException;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.candidates.CandidateExtractor;
import de.tudarmstadt.ukp.alignment.framework.gloss.GlossSimilarityCalculator;
import de.tudarmstadt.ukp.alignment.framework.graph.CalculateDijkstraWSA;
import de.tudarmstadt.ukp.alignment.framework.graph.CreateAlignmentFromGraphOutput;
import de.tudarmstadt.ukp.alignment.framework.graph.JointGraphBuilder;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

/*Monolingual Sense Alignments between

Wikipedia EN and WordNet
Wiktionary EN and WordNet
WordNet and VerbNet
WordNet and FrameNet
FrameNet and VerbNet
FrameNet and Wiktionary EN
Wiktionary EN and OmegaWiki EN
OmegaWiki DE and Wikipedia DE
OmegaWiki EN and Wikipedia EN

Crosslingual Sense Alignments between

OmegaWiki DE and WordNet
OmegaWiki DE and OmegaWiki EN
Wikipedia EN and Wikipedia DE

*/

public class SemanticLexicalResourceAligner
{


    /* UBY database connection parameters */
    private static final String DB_NAME ="uby_open_0_6_0";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    static {

        /* GLOBAL SETTINGS */
        Global.init();
    }


    /**
     * Building the resource representation
     * @param prefix The prefix of LSR, e.g. Global.WKT_EN_prefix, Global.WN_Sense_prefix ,...
     * @param lang The language of the resource
     * @param sysnset If true the synset of the resource will be used for the alignment
     * @param usePos If true POS will be considered. Note some resources like OmegaWiki don't have POS for all senses
     * @return
     */
    private static OneResourceBuilder createResource(int prefix, String lang, boolean sysnset,
                                                                                    boolean usePos){

        // Build the resource by using the appropriate databases

        OneResourceBuilder bg_1 = new OneResourceBuilder(DB_NAME, DB_USER, DB_PASSWORD,
                prefix, lang, sysnset, usePos);

        return bg_1;


    }


    /**
     * Extract the information needed for gloss- and graph-based alignment methods
     * @param bg The resource presentation
     * @param chunksize  Specifies how many lines of the gloss files should be processed at once -
     *                   this mainly depends on the memory of your machine, but 2000 seems to be a reasonable value
     * @param monoLinkThreshold specifies the maximum frequency a lemma may have in the resource to be considered
     *                          avoid an "explosion of edges" by linking too frequent and thus meaningless lexemes
     *                          experience shows that 1/100 or 1/200 of the amount of senses/synsets in a resource
     *                          is reasonable value
     */
    private static void prepareResouce(OneResourceBuilder bg, int chunksize, int monoLinkThreshold){

        try {
            //extracts the glosses for each sense/synset from the DB and writes them to a file
            //If the parameter set to true then in case of a missing gloss, an "artificial gloss"
            //from targets of semantic relations (synonyms, antonyms etc.) should be created (good for GermaNet)
            bg.createGlossFile(false);


            // Apply lemmatization and POS tagging for the extracted gloss files
            bg.lemmatizePOStagGlossFileInChunks(chunksize);


            // Fill the index, build graphs from the relations and the monosemous linking - merge in the
            // end

            //loads some database information into memory. This is required to get a reasonable computation time
            bg.fillIndexTables();

            //uses the semantic relations contained in UBY to create edges for the graph representation
            //If the parameter set to true then only the edges to lexemes which are contained in the gloss
            //are used to create the graph (good for Wikipedia due to the large number of relations
            //so we can only consider the links in the first paragraph)
            bg.builtRelationGraphFromDb(false);

            //creates edges between senses/synsets based on monosemous lexemes in the glosses
            //for the monosemous linking to work, the POS-tagged gloss files need to be created
            bg.createMonosemousLinks(monoLinkThreshold);


            //merging the relation graph and monosemous link graph and writing the results to a file
            String prefix_string1 = Global.prefixTable.get(bg.prefix);
            boolean synset= bg.synset;
            boolean usePos = bg.pos;
            Global.mergeTwoGraphs(prefix_string1 + "_" + (synset ? "synset" : "sense")
                    + "_relationgraph.txt", prefix_string1 + "_" + (synset ? "synset" : "sense") + "_"
                    + (usePos ? "Pos" : "noPos") + "_monosemousLinks" + "_" + monoLinkThreshold
                    + ".txt", prefix_string1 + "_" + (synset ? "synset" : "sense") + "_"
                    + (usePos ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold
                    + ".txt");

        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    /**
     * Merge graph representations for both resources
     * @param bg_1 the graph of the first resource
     * @param monoLinkThreshold1 maximum lemmma frequency for the first resource
     * @param bg_2 the graph of the second resource
     * @param monoLinkThreshold2 maximum lemmma frequency for the second resource
     */
    private static void mergeResourcesGraphs(OneResourceBuilder bg_1, int monoLinkThreshold1,
                                            OneResourceBuilder bg_2, int monoLinkThreshold2){

        String prefix_string1 = Global.prefixTable.get(bg_1.prefix);
        boolean synset1= bg_1.synset;
        boolean usePos1 = bg_1.pos;


        String prefix_string2 = Global.prefixTable.get(bg_2.prefix);
        boolean synset2= bg_2.synset;
        boolean usePos2 = bg_2.pos;


        /* Merge the two graphs */

        try {
            Global.mergeTwoGraphs(prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
                    + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold1
                    + ".txt", prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
                    + (usePos2 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold2
                    + ".txt",
            // prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph"+(filter ?
            // "_filtered":"")+".txt",
                    prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
                            + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_"
                            + monoLinkThreshold1 + "_MERGED_" + prefix_string2 + "_"
                            + (synset2 ? "synset" : "sense") + "_" + (usePos2 ? "Pos" : "noPos")
                            + "_relationMLgraph" + "_" + monoLinkThreshold2 + ".txt");

            /* Create trivial alignments between the two LSRs */
            /* Index tables must be filled at this point!!! */

            JointGraphBuilder.createTrivialAlignments(bg_1, bg_2);

            /* Merge the joint graphs and trivial alignments */

            Global.mergeTwoGraphs(prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
                    + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold1
                    + "_MERGED_" + prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
                    + (usePos2 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold2
                    + ".txt", prefix_string1 + "_" + prefix_string2 + "_trivial_"
                    + (usePos2 ? "Pos" : "noPos") + ".txt", prefix_string1 + "_"
                    + (synset1 ? "synset" : "sense") + "_" + (usePos1 ? "Pos" : "noPos")
                    + "_relationMLgraph" + "_" + monoLinkThreshold1 + "_MERGED_" + prefix_string2 + "_"
                    + (synset2 ? "synset" : "sense") + "_" + (usePos2 ? "Pos" : "noPos")
                    + "_relationMLgraph" + "_" + monoLinkThreshold2 + "_trivial.txt");

            // Done! We now have two linked graphs which are connected via monosemous links



        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }



    /**
     *
     * @param bg_1 the graph of the first resource
     * @param monoLinkThreshold1 maximum lemmma frequency for the first resource
     * @param bg_2 the graph of the second resource
     * @param monoLinkThreshold2 maximum lemmma frequency for the first resource
     * @param graphFile The path to the file representing the merged resources graph
     * @param candidateAlignmentsFile The path to the file where candidate alignments are stored
     */
    private static void generateAlignments(OneResourceBuilder bg_1, int monoLinkThreshold1,
            OneResourceBuilder bg_2, int monoLinkThreshold2){


        try {

            //In order to know which senses to align to which, you first have to know the candidates,
            //i.e. those senses in either resource with matching lemma and (optionally) POS

            //creates a list for all potentially matching senses, without further restrictions. Used for full alignment.
            CandidateExtractor.createCandidateFileFull(bg_1, bg_2);

            //Creating graph based alignments
            //takes the graph and candidate files as input, and outputs a list of distances in another file,
            //where infinite distance means that no connection could be found

            String candidateAlignmentsFile = "target/"+bg_1.prefix_string+"_"+bg_2.prefix_string+"_candidates_"+(bg_2.pos ? "Pos": "noPos")+".txt";

            String graphFile ="target/"+ bg_1.prefix_string + "_"
                    + (bg_1.synset ? "synset" : "sense") + "_" + (bg_1.pos ? "Pos" : "noPos")
                    + "_relationMLgraph" + "_" + monoLinkThreshold1 + "_MERGED_" + bg_2.prefix_string + "_"
                    + (bg_2.synset ? "synset" : "sense") + "_" + (bg_2.pos ? "Pos" : "noPos")
                    + "_relationMLgraph" + "_" + monoLinkThreshold2 + "_trivial.txt" ;



            CalculateDijkstraWSA.calculateDijkstraWSAdistances(graphFile, candidateAlignmentsFile);


            // Creating gloss cosine similarity between the alignments and write the result in a file
            GlossSimilarityCalculator.calculateSimilarityForCandidates(bg_1, bg_2, true, false,  candidateAlignmentsFile);

            //we can straightforwardly create an alignment. This method takes the naive approach of
            //always aligning the candidate with the highest similarity, regardless of the absolute value.
            GlossSimilarityCalculator.createAlignmentFromSimilarityFileUnsupervised(bg_1, bg_2, true, false, true);

            //Create the final alignments
            CreateAlignmentFromGraphOutput.createAlignment(bg_1, bg_2, monoLinkThreshold1, monoLinkThreshold2, 3, false, false,false,null);
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    /**
     * Create alignments between two LSRs
     * @param prefix1 The prefix of the first LSR, e.g. Global.WKT_EN_prefix, Global.WN_Sense_prefix ,...
     * @param lang1 The language of the first resource
     * @param sysnset1 If true the synset of the first resource will be used for the alignment
     * @param usePos1 If true POS will be considered for the first resouces
     * @param chunksize1 Specifies how many lines of the gloss files of the first resource should be processed at once (e.g. 2000)
     * @param monoLinkThreshold1 maximum lemmma frequency for the first resource
     * @param prefix2 The prefix of the second LSR, e.g. Global.WKT_EN_prefix, Global.WN_Sense_prefix ,...
     * @param lang2 The language of the second resource
     * @param sysnset2 If true the synset of the second resource will be used for the alignment
     * @param usePos2 If true POS will be considered for the second resouces
     * @param chunksize2 Specifies how many lines of the gloss files of the second resource should be processed at once (e.g. 2000)
     * @param monoLinkThreshold2 maximum lemmma frequency for the second resource
     */
    public static void alignTwoSLRs(int prefix1, String lang1, boolean sysnset1,boolean usePos1,int chunksize1, int monoLinkThreshold1,
                                    int prefix2, String lang2, boolean sysnset2,boolean usePos2,int chunksize2, int monoLinkThreshold2){


        /* create OneResourceBuilder for each SLR */
        OneResourceBuilder firstSLR = createResource(prefix1, lang1, sysnset1, usePos1);
        OneResourceBuilder secondSLR = createResource(prefix2, lang2, sysnset2, usePos2);

        /* Prepare the resources*/
        prepareResouce(firstSLR, chunksize1, monoLinkThreshold1);
        prepareResouce(secondSLR, chunksize2, monoLinkThreshold2);


        /* Merge the corresponding LSR graphs */
        mergeResourcesGraphs(firstSLR, monoLinkThreshold1, secondSLR, monoLinkThreshold2);


        /* Create the alignments between the two LSRs */
        generateAlignments(firstSLR, monoLinkThreshold1, secondSLR, monoLinkThreshold2);


    }

    /**
     * Align WN with English Wiktionary
     */
    public static void alignWN_WKTen(){

        String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now

        /* WordNet  */

        boolean synset1 = true;
        boolean usePos1 = true;
        int prefix1 = Global.WN_Synset_prefix;

        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold1 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize1 = 2000;

        /* Wkitionary EN 2 */

        boolean synset2 = false;
        boolean usePos2 = true;
        final int prefix2 = Global.WKT_EN_prefix;
        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold2 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize2 = 2000;

        alignTwoSLRs(prefix1, language, synset1, usePos1, chunksize1, monoLinkThreshold1,
                    prefix2,  language, synset2, usePos2, chunksize2, monoLinkThreshold2
                    );

    }


    /**
     * Align WordNet with VerbNet
     */
    public static void alignWN_VN(){

        String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now

        /* WordNet  */

        boolean synset1 = true;
        boolean usePos1 = true;
        int prefix1 = Global.WN_Synset_prefix;

        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold1 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize1 = 2000;

        /* Wkitionary EN 2 */

        boolean synset2 = false;
        boolean usePos2 = true;
        final int prefix2 = Global.VN_prefix;
        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold2 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize2 = 2000;

        alignTwoSLRs(prefix1, language, synset1, usePos1, chunksize1, monoLinkThreshold1,
                    prefix2,  language, synset2, usePos2, chunksize2, monoLinkThreshold2);

    }

    /**
     * Align WordNet with VP en
     */
    public static void alignWN_WPen(){


        System.out.println(".............. Aligning WN with WP English ....................");
        String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now

        /* WordNet  */

        boolean synset1 = true;
        boolean usePos1 = true;
        int prefix1 = Global.WN_Synset_prefix;

        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold1 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize1 = 2000;

        /* WP EN 2 */

        boolean synset2 = false;
        boolean usePos2 = true;
        final int prefix2 = Global.WP_EN_prefix;
        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold2 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize2 = 2000;

        alignTwoSLRs(prefix1, language, synset1, usePos1, chunksize1, monoLinkThreshold1,
                    prefix2,  language, synset2, usePos2, chunksize2, monoLinkThreshold2);

    }

    /**
     * Align WordNet with VerbNet
     */
    public static void alignFM_VN(){

        String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now

        /* FrameNet  */

        boolean synset1 = false;
        boolean usePos1 = true;
        int prefix1 = Global.FN_prefix;

        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold1 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize1 = 2000;

        /* VerbNet */

        boolean synset2 = true;
        boolean usePos2 = true;
        final int prefix2 = Global.VN_prefix;
        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold2 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize2 = 2000;

        alignTwoSLRs(prefix1, language, synset1, usePos1, chunksize1, monoLinkThreshold1,
                    prefix2,  language, synset2, usePos2, chunksize2, monoLinkThreshold2);

    }


    /**
     * Align WordNet with FrameNet
     */
    public static void alignWN_FN(){

        String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now

        /* WordNet  */

        boolean synset1 = true;
        boolean usePos1 = true;
        int prefix1 = Global.WN_Synset_prefix;

        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold1 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize1 = 2000;

        /* Wkitionary EN 2 */

        boolean synset2 = false;
        boolean usePos2 = true;
        final int prefix2 = Global.FN_prefix;
        // Frequency threshold for the monosemous linking
        final int monoLinkThreshold2 = 1000;
        // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
        // values are faster, but might lead to crashes
        final int chunksize2 = 2000;

        alignTwoSLRs(prefix1, language, synset1, usePos1, chunksize1, monoLinkThreshold1,
                    prefix2,  language, synset2, usePos2, chunksize2, monoLinkThreshold2
                    );

    }
    public static void main(String[] args)
    {

        alignWN_FN();
//        alignWN_WKTen();
//        alignFM_VN();

//        alignWN_WPen();
    }

//    /**
//    *
//    * This method is the "starting point" of the alignment framework, encoding the process from
//    * creation of the graphs to their merging into one big graph using monosemous linking
//    *
//    *
//    */
//   public static void createSLRAlignments()
//       throws Exception
//   {
//
//       /* GLOBAL SETTINGS */
//
//       Global.init();
//
//
//       String language = ELanguageIdentifier.ENGLISH; // We cover only the monolingual case for now
//
//       /* RESOURCE 1 */
//
//       boolean synset1 = true;
//       boolean usePos1 = true;
//
//       // Chose the resource we want to align by selecting the appropriate prefixes
//
//       int prefix1 = Global.WN_Synset_prefix;
//       String prefix_string1 = Global.prefixTable.get(prefix1);
//
//       // Frequency threshold for the monosemous linking
//       final int monoLinkThreshold1 = 1000;
//
//       // Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher
//       // values are faster, but might lead to crashes
//       final int chunksize1 = 2000;
//
//       // Build the resource by using the appropriate databases
//
//       OneResourceBuilder bg_1 = new OneResourceBuilder("uby_medium_0_6_0", "root", "admin",
//               prefix1, language, synset1, usePos1);
//
//       // Create text files with glosses for the two resources, and do POS tagging
//
//       bg_1.createGlossFile(false);
//       bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
//
//       // Fill the index, build graphs from the relations and the monosemous linking - merge in the
//       // end
//       bg_1.fillIndexTables();
//       bg_1.builtRelationGraphFromDb(false);
//       bg_1.createMonosemousLinks(monoLinkThreshold1);
//       Global.mergeTwoGraphs(prefix_string1 + "_" + (synset1 ? "synset" : "sense")
//               + "_relationgraph.txt", prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
//               + (usePos1 ? "Pos" : "noPos") + "_monosemousLinks" + "_" + monoLinkThreshold1
//               + ".txt", prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
//               + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold1
//               + ".txt");
//
//       /* RESOURCE 2 */
//
//       boolean synset2 = false;
//       boolean usePos2 = true;
//       final int prefix2 = Global.WKT_EN_prefix;
//       final String prefix_string2 = Global.prefixTable.get(prefix2);
//       final int monoLinkThreshold2 = 2000;
//       final int chunksize2 = 2000;
//       OneResourceBuilder bg_2 = new OneResourceBuilder("uby_medium_0_6_0", "root", "admin",
//               prefix2, language, synset2, usePos2);
//
//       bg_2.createGlossFile(false);
//       bg_2.lemmatizePOStagGlossFileInChunks(chunksize2);
//       bg_2.fillIndexTables();
//       boolean filter = false;
//       bg_2.builtRelationGraphFromDb(filter);
//       bg_2.createMonosemousLinks(monoLinkThreshold2);
//
//
//       Global.mergeTwoGraphs(prefix_string2 + "_" + (synset2 ? "synset" : "sense")
//               + "_relationgraph.txt", prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
//               + (usePos2 ? "Pos" : "noPos") + "_monosemousLinks" + "_" + monoLinkThreshold2
//               + ".txt", prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
//               + (usePos2 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold2
//               + ".txt");
//
//       /* Merge the two graphs */
//
//       Global.mergeTwoGraphs(prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
//               + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold1
//               + ".txt", prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
//               + (usePos2 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold2
//               + ".txt",
//       // prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph"+(filter ?
//       // "_filtered":"")+".txt",
//               prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
//                       + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_"
//                       + monoLinkThreshold1 + "_MERGED_" + prefix_string2 + "_"
//                       + (synset2 ? "synset" : "sense") + "_" + (usePos2 ? "Pos" : "noPos")
//                       + "_relationMLgraph" + "_" + monoLinkThreshold2 + ".txt");
//
//
//       /* Create trivial alignments between the two LSRs */
//       /* Index tables must be filled at this point!!! */
//
//       JointGraphBuilder.createTrivialAlignments(bg_1, bg_2);
//
//       /* Merge the joint graphs and trivial alignments */
//
//       Global.mergeTwoGraphs(prefix_string1 + "_" + (synset1 ? "synset" : "sense") + "_"
//               + (usePos1 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold1
//               + "_MERGED_" + prefix_string2 + "_" + (synset2 ? "synset" : "sense") + "_"
//               + (usePos2 ? "Pos" : "noPos") + "_relationMLgraph" + "_" + monoLinkThreshold2
//               + ".txt", prefix_string1 + "_" + prefix_string2 + "_trivial_"
//               + (usePos2 ? "Pos" : "noPos") + ".txt", prefix_string1 + "_"
//               + (synset1 ? "synset" : "sense") + "_" + (usePos1 ? "Pos" : "noPos")
//               + "_relationMLgraph" + "_" + monoLinkThreshold1 + "_MERGED_" + prefix_string2 + "_"
//               + (synset2 ? "synset" : "sense") + "_" + (usePos2 ? "Pos" : "noPos")
//               + "_relationMLgraph" + "_" + monoLinkThreshold2 + "_trivial.txt");
//
//       // Done! We now have two linked graphs which are connected via monosemous links
//
//
//       CandidateExtractor.createCandidateFileFull(bg_1, bg_2);
//
//       CalculateDijkstraWSA.calculateDijkstraWSAdistances("target/WN_synset_Pos_relationMLgraph_1000_MERGED_WktEn_sense_Pos_relationMLgraph_2000.txt", "target/WN_WktEn_candidates_Pos.txt");
//
//
//       GlossSimilarityCalculator.calculateSimilarityForCandidates(bg_1, bg_2, true, false,  "target/WN_WktEn_candidates_Pos.txt");
//
//       GlossSimilarityCalculator.createAlignmentFromSimilarityFileUnsupervised(bg_1, bg_2, true, false, true);
//
//
//       CreateAlignmentFromGraphOutput.createAlignment(bg_1, bg_2, monoLinkThreshold1, monoLinkThreshold2, 3, false, false,false,null);
//
//
//       //Evaluator.performEvaluation(alignment, goldstandard, pos);
//   }
}
