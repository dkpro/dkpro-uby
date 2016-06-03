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

package de.tudarmstadt.ukp.alignment.framework.combined;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class WekaMachineLearning {

	public static void main(String[] args)
	{
		/* GLOBAL SETTINGS */

		Global.init();
		final String language = ELanguageIdentifier.ENGLISH;
		try
		{

				/*RESOURCE 1*/

				boolean synset1 = true;
				boolean usePos1 = true;
				final int prefix1 = Global.WN_Synset_prefix;
				OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);

				/*RESOURCE 2*/
				boolean synset2 = true;
				boolean usePos2 = true;

				final int prefix2 = Global.OW_EN_Synset_prefix;
				OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);

//	Global.processExtRefGoldstandardFile(bg_1,bg_2,"target/WN_OW_alignment_gold_standard.csv",true);

//	createArffFile("target/ijcnlp2011-meyer-dataset_graph.csv","target/WN_WKT_dwsa_cos_gs.arff", "target/WN_synset_Pos_relationMLgraph_1000_MERGED_WktEn_sense_Pos_relationMLgraph_2000_trivial_result.txt","target/WN_WktEn_glossSimilarities_tagged_tfidf.txt");
//	createModelFromGoldstandard("target/WN_WKT_dwsa_cos_gs.arff", "target/WN_WKT_dwsa_cos_model", true);
//	applyModelToUnlabeledArff("target/WN_OW_dwsa_cos_unlabeled_full.arff", "target/WN_OW_dwsa_cos_model", "target/WN_OW_dwsa_cos_labeled_full.arff");
//				createFinalAlignmentFile("target/WN_OW_dwsa_cos_labeled_full.arff", "target/WN_OW_dwsa_cos_ML_alignment.tsv");
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  This method creates an arff file (readable by WEKA) from the earlier produced distance/similarity files
	 *
	 *
	 *
	 * @param goldstandard if not null, this is used as a filter, i.e. only instances present in the gold standard are written to the output
	 * @param output The output file
	 * @param filenames the (variable number of) files which hold similarities, DWSA distances and so on, as created by the other methods of this framework
	 */
	public static void createArffFile(String goldstandard, String output, String... filenames)
	{
		PrintStream p = null;
		FileOutputStream outstream;
		try {
			outstream = new FileOutputStream(output);
			p = new PrintStream(outstream);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StringBuilder arffFile = new StringBuilder();
		String[] attNames = filenames;
		arffFile.append("@RELATION "+output+Global.LF+Global.LF);
		arffFile.append("@ATTRIBUTE "+"Pair_ID"+" STRING"+Global.LF);
		for(String attribute : attNames)
		{
			arffFile.append("@ATTRIBUTE "+attribute+" NUMERIC"+Global.LF);
		}
		arffFile.append("@ATTRIBUTE class {0,1}"+Global.LF+Global.LF+"@DATA"+Global.LF);

		HashMap<String, String[]> entities = new HashMap<String, String[]>();
		HashMap<String, String> classes = new HashMap<String, String>();
		int filecount = 0;
		for(String file : filenames)
		{
			FileReader in;
			try {
				in = new FileReader(file);
				BufferedReader input =  new BufferedReader(in);
				String line;
				while ((line = input.readLine())!=null)
				{
					if(line.startsWith("f")) {
                        continue;
                    }
					String ids = line.split("\t")[0]+"###"+line.split("\t")[1];
					System.out.println(ids);
					String value = line.split("\t")[2];
					if(!entities.containsKey(ids))
					{
						entities.put(ids, new String[attNames.length]);
					}
					String[] temp = entities.get(ids);
					temp[filecount] = value;
					entities.put(ids,temp);
					if(ids.equals("1034749###1273021"))
					{
						System.out.println(value);
					}
				}
				input.close();
				filecount++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if(goldstandard != null)
		{
			FileReader in;
			try {
				in = new FileReader(goldstandard);
				BufferedReader input =  new BufferedReader(in);
				String line;
				while ((line = input.readLine())!=null)
				{
					if(line.startsWith("f")) {
                        continue;
                    }
					String ids = line.split("\t")[0]+"###"+line.split("\t")[1];

					String value = line.split("\t")[2];

					classes.put(ids, value);

				}
				input.close();
				filecount++;
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		for(String key : entities.keySet())
		{
			if(classes.containsKey(key) || goldstandard == null)
			{
			String[] values= entities.get(key);
			arffFile.append(key+",");
			for(String v : values)
			{
				//System.out.println(v);
				arffFile.append(v+",");
			}
			if(classes.containsKey(key))
			{
				arffFile.append(classes.get(key)+Global.LF);
			}
			else
			{
				arffFile.append("?"+Global.LF);
			}
		}
			}
		p.println(arffFile);
		p.close();
	}

	/**
	 *
	 * This method creates a serialized WEKA model file from an .arff file containing the annotated gold standard
	 *
	 *
	 * @param gs_arff the annotated gold standard in an .arff file
	 * @param model output file for the model
	 * @param output_eval if true, the evaluation of the trained classifier is printed (10-fold cross validation)
	 * @throws Exception
	 */

	public static void createModelFromGoldstandard(String gs_arff, String model, boolean output_eval) throws Exception
	{
		 DataSource source = new DataSource(gs_arff);
		 Instances data = source.getDataSet();
		 if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }

		 Remove rm = new Remove();
		 rm.setAttributeIndices("1");  // remove ID  attribute

		 BayesNet bn = new BayesNet();	 //Standard classifier; BNs proved most robust, but of course other classifiers are possible
		 // meta-classifier
		 FilteredClassifier fc = new FilteredClassifier();
		 fc.setFilter(rm);
		 fc.setClassifier(bn);
		 fc.buildClassifier(data);   // build classifier
		 SerializationHelper.write(model, fc);
		 if(output_eval)
		 {
			 Evaluation eval = new Evaluation(data);
			 eval.crossValidateModel(fc, data, 10, new Random(1));
			 System.out.println(eval.toSummaryString());
			 System.out.println(eval.toMatrixString());
			 System.out.println(eval.toClassDetailsString());
		 }

	}

	/**
	 *
	 * This method applies a serialized WEKA model file to an unlabeld .arff file for classification
	 *
	 *
	 * @param input_arff the annotated gold standard in an .arff file
	 * @param model output file for the model
	 * @param output output file for evaluation of trained classifier (10-fold cross validation)
	 * @throws Exception
	 */

	public static void applyModelToUnlabeledArff(String input_arff, String model, String output) throws Exception
	{
		 DataSource source = new DataSource(input_arff);
		 Instances unlabeled = source.getDataSet();
		 if (unlabeled.classIndex() == -1) {
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        }

		 Remove rm = new Remove();
		 rm.setAttributeIndices("1");  // remove ID  attribute

		 ObjectInputStream ois = new ObjectInputStream(
           new FileInputStream(model));
		Classifier cls = (Classifier) ois.readObject();
		ois.close();
		 // create copy
		 Instances labeled = new Instances(unlabeled);

		 // label instances
		 for (int i = 0; i < unlabeled.numInstances(); i++) {
		   double clsLabel = cls.classifyInstance(unlabeled.instance(i));
		   labeled.instance(i).setClassValue(clsLabel);
		 }
		 // save labeled data
		 BufferedWriter writer = new BufferedWriter(
		                           new FileWriter(output));
		 writer.write(labeled.toString());
		 writer.newLine();
		 writer.flush();
		 writer.close();


	}

	public static void createFinalAlignmentFile(String input_arff, String output) throws Exception
	{

		FileReader in = new FileReader(input_arff);
		BufferedReader input =  new BufferedReader(in);
		String line;
		 BufferedWriter writer = new BufferedWriter(
                 new FileWriter(output));
		 writer.write("f "+input_arff+" ML Alignment");
		while ((line = input.readLine())!=null)
		{
			if(!line.endsWith(",1")) {
                continue;
            }
			String[] fields = line.split(",");
			String ids = fields[0];
			String id1 = ids.split("###")[0];
			String id2 = ids.split("###")[1];
			String values ="";
			for(int i = 1; i< fields.length-1;i++) {
                values+=fields[i]+"###";
            }
			writer.write(id1+"\t"+id2+"\t"+values.subSequence(0, values.length()-3));
			writer.newLine();

		}
		writer.flush();
		writer.close();
		input.close();
		in.close();
		 // label instances


	}
}
