/*******************************************************************************
 * Copyright 2017
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
package de.tudarmstadt.ukp.alignment.framework.clustering;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class CreateClusteringFromAlignment
{

    public static void main(String[] args)
        throws ClassNotFoundException, SQLException, IOException
    {
        /* GLOBAL SETTINGS */

        Global.init();
        String language = ELanguageIdentifier.ENGLISH;

        /* RESOURCE 1 */

        boolean synset1 = true;
        boolean usePos1 = true;
        int prefix1 = Global.WN_Synset_prefix;

        OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0",
                "root", "fortuna", prefix1, language, synset1, usePos1);
        String alignment_file = "/home/local/UKP/matuschek/ClusterEvaluationTM/WN_OW_full_joint.csv";

        splitSynsetsToSenses(bg_1, alignment_file, true);
        produceClustersFromSenseAlignment(alignment_file + "_sense");
        splitClustersByLemma(bg_1, alignment_file + "_sense_cluster", true);
        mergeClustersWithSharedSenses(alignment_file + "_sense_cluster_cleaned");
        checkPOSpurity(bg_1, alignment_file + "_sense_cluster_cleaned_merged");
        checkForDupesInSameCluster(alignment_file
                + "_sense_cluster_cleaned_merged_POScleaned");
        filterByLexemeList(bg_1, alignment_file
                + "_sense_cluster_cleaned_merged_POScleaned_noDupes",
                "/home/local/UKP/matuschek/ClusterEvaluationTM/lemmas.tsv",
                true);
        bringWNextRefClustersIntoTMFormat("/home/local/UKP/matuschek/ClusterEvaluationTM/WN_WP_full_alignment_DijkstaWSA_best_corrected_senses.txt_cluster_cleaned_merged_POScleaned_noDupes_lexemeFiltered");

    }

    /**
     * This method splits sense clusters which contain senses of different POS.
     * This might occur if you align against a resource which lacks POS
     * information, such as OmegaWiki.
     *
     *
     * @param bg_1
     *            The resource
     * @param infile
     *            The sense cluster file
     */
    public static void checkPOSpurity(OneResourceBuilder bg_1, String infile)
    {

        try {
            Connection connection = bg_1.connection;
            FileReader in = new FileReader(infile);
            BufferedReader input = new BufferedReader(in);
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(infile + "_POScleaned");
            p = new PrintStream(outstream);
            Statement statement = connection.createStatement();
            HashMap<String, String> idPosMap = new HashMap<String, String>();
            ResultSet rs = statement
                    .executeQuery("SELECT externalReference,partOfSpeech FROM MonolingualExternalRef join LexicalEntry join  Sense where Sense.senseId like '"
                            + bg_1.prefix_string
                            + "%' and MonolingualExternalRef.senseID = Sense.senseId and Sense.lexicalEntryId = LexicalEntry.lexicalEntryId");
            while (rs.next()) {
                idPosMap.put(rs.getString(1), rs.getString(2));
            }

            String line;
            while ((line = input.readLine()) != null) {
                String[] elements = line.split("\t");
                String results = "";
                HashSet<String> pos_count = new HashSet<String>();
                HashMap<String, HashSet<String>> pos_map = new HashMap<String, HashSet<String>>();
                for (String s : elements) {
                    String pos = idPosMap.get(s);
                    if (!pos_map.containsKey(pos)) {
                        pos_map.put(pos, new HashSet<String>());
                    }
                    pos_map.get(pos).add(s);
                    pos_count.add(idPosMap.get(s));
                }
                for (String x : pos_map.keySet()) {
                    results = "";
                    HashSet<String> posCluster = pos_map.get(x);
                    if (posCluster.size() == 1) {
                        continue;
                    }
                    for (String id : posCluster) {
                        results += id + "\t";
                    }
                    p.println(results.trim());
                }

                if (pos_count.size() > 1) { // For debugging purposes
                    System.out.println(line);
                }

            }
            p.close();
            input.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method separates clusters with mixed lemmas
     *
     *
     * @param bg_1
     *            the resource
     * @param infile
     *            the (mixed lemma) sense clusters
     * @param extRef
     *            are we using external References or not?
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void splitClustersByLemma(OneResourceBuilder bg_1,
            String infile, boolean extRef)
        throws SQLException, IOException, ClassNotFoundException
    {
        FileOutputStream outstream;
        PrintStream p;
        outstream = new FileOutputStream(infile + "_cleaned");
        p = new PrintStream(outstream);
        FileReader in = new FileReader(infile);
        String line;
        Statement statement = bg_1.connection.createStatement();
        HashMap<String, String> extRefLemmaMap = new HashMap<String, String>();

        BufferedReader input = new BufferedReader(in);
        ResultSet rs;

        if (extRef) {
            rs = statement
                    .executeQuery("SELECT externalReference, writtenForm FROM LexicalEntry join FormRepresentation_Lemma join Sense join MonolingualExternalRef where FormRepresentation_Lemma.lemmaId = LexicalEntry.lemmaId and Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and MonolingualExternalRef.senseId = Sense.senseId and Sense.senseId like '"
                            + bg_1.prefix_string + "%'");

        }
        else {
            rs = statement
                    .executeQuery("SELECT  senseId, writtenForm FROM  LexicalEntry     join   FormRepresentation_Lemma      join  Sense where FormRepresentation_Lemma.lemmaId = LexicalEntry.lemmaId  and Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and Sense.senseId like '"
                            + bg_1.prefix_string + "%'");
        }
        while (rs.next()) {
            extRefLemmaMap.put(rs.getString(1), rs.getString(2));
        }
        while ((line = input.readLine()) != null) {
            line = line.trim();
            String[] sensekeys = line.split("\t");
            HashMap<String, HashSet<String>> lemmaIDMap = new HashMap<String, HashSet<String>>();
            for (String key : sensekeys) {
                String lemma = extRefLemmaMap.get(key);
                if (lemmaIDMap.get(lemma) == null) {
                    lemmaIDMap.put(lemma, new HashSet<String>());
                }
                lemmaIDMap.get(lemma).add(key);
            }
            for (String l : lemmaIDMap.keySet()) {
                String output = "";
                if (lemmaIDMap.get(l).size() > 1) {
                    for (String k : lemmaIDMap.get(l)) {
                        output += k + "\t";
                    }
                    p.println(output.trim());
                }

            }
        }
        input.close();
        p.close();
    }

    /**
     * This method takes a given SYNSET alignment file (as produced by the
     * framework) and splits it into sense alignments
     *
     *
     *
     * @param bg_1
     *            The resource to be clustered
     * @param infile
     *            The alignment file
     * @param extRef
     *            Is the alignment file given in external References or UBY ids?
     */

    public static void splitSynsetsToSenses(OneResourceBuilder bg_1,
            String infile, boolean extRef)
    {
        try {
            Connection connection = bg_1.connection;
            FileReader in = new FileReader(infile);
            BufferedReader input = new BufferedReader(in);
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(infile + "_sense");
            p = new PrintStream(outstream);
            Statement statement = connection.createStatement();
            HashMap<String, String> idMapSense = new HashMap<String, String>();
            HashMap<String, String> idMapSynset = new HashMap<String, String>();
            HashMap<String, HashSet<String>> senseSynsetMapping = new HashMap<String, HashSet<String>>();
            ResultSet rs;
            if (extRef) {
                rs = statement
                        .executeQuery("SELECT externalReference, senseId FROM uby_release_1_0.MonolingualExternalRef where senseId like  '"
                                + bg_1.prefix_string + "%'");
                while (rs.next()) {
                    idMapSense.put(rs.getString(2), rs.getString(1));
                }
                rs = statement
                        .executeQuery("SELECT externalReference, synsetId FROM uby_release_1_0.MonolingualExternalRef where synsetId like '"
                                + bg_1.prefix_string + "%'");
                while (rs.next()) {
                    idMapSynset.put(rs.getString(1), rs.getString(2));
                }
            }
            rs = statement
                    .executeQuery("SELECT synsetId, senseId FROM uby_release_1_0.Sense where synsetId like '"
                            + bg_1.prefix_string + "%'");
            while (rs.next()) {
                if (!senseSynsetMapping.containsKey(rs.getString(1))) {
                    senseSynsetMapping.put(rs.getString(1),
                            new HashSet<String>());
                }
                senseSynsetMapping.get(rs.getString(1)).add(rs.getString(2));
            }

            String line;
            while ((line = input.readLine()) != null) {

                String[] ids = line.split("\t");
                String synsetId = ids[0];
                String synsetUbyId;
                if (extRef) {
                    synsetUbyId = idMapSynset.get(synsetId);
                }
                else {
                    synsetUbyId = synsetId;
                }
                HashSet<String> ss = senseSynsetMapping.get(synsetUbyId);

                for (String s : ss) {
                    if (extRef) {
                        p.println(idMapSense.get(s) + "\t" + ids[1]);
                    }
                    else {
                        p.println(s + "\t" + ids[1]);
                    }
                }

            }
            p.close();
            input.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method naively clusters those senses together which have the same
     * alignment target. Further downstream cleaning will be necessary
     *
     *
     * @param infile
     *            The sense alignment file
     *
     */

    public static void produceClustersFromSenseAlignment(String infile)
    {

        try {
            HashMap<String, HashSet<String>> clusters = new HashMap<String, HashSet<String>>();
            FileReader in = new FileReader(infile);
            BufferedReader input = new BufferedReader(in);
            String line = "";
            while ((line = input.readLine()) != null) {
                String[] ids = line.split("\t");
                if (!clusters.containsKey(ids[1])) {
                    clusters.put(ids[1], new HashSet<String>());
                }
                (clusters.get(ids[1])).add(ids[0]);
            }

            FileOutputStream outstream = new FileOutputStream(infile
                    + "_cluster");
            PrintStream p = new PrintStream(outstream);
            for (String key : clusters.keySet()) {
                HashSet<String> sss = clusters.get(key);
                if (sss.size() > 1) {
                    for (String s : sss) {
                        p.print(s + "\t");
                    }
                    p.println();
                }
            }
            input.close();

            p.close();
            in.close();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     *
     * This simple method just removes the POS information as given in the
     * External References for WordNet senses
     *
     * E.g. [POS: noun] acid%1:06:00:: -> acid%1:06:00::
     *
     * @param input_file
     *            The sense clustering
     */

    public static void bringWNextRefClustersIntoTMFormat(String input_file)
    {
        try {
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(input_file + "_TM");
            p = new PrintStream(outstream);
            FileReader in = new FileReader(input_file);
            BufferedReader input = new BufferedReader(in);
            String line = "";
            while ((line = input.readLine()) != null) {
                String out = "";
                String[] ids = line.split("\t");
                for (String id : ids) {
                    out += id.split("] ")[1] + "\t";

                }
                p.println(out.trim());
            }
            p.close();
            input.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method filters out senses clusters accorind to a given,
     * tab-separated lemma/POS list, where one lexeme per line is given, e.g.
     *
     * fish noun swim verb
     *
     *
     * @param bg_1
     *            The resource
     * @param input_file
     *            The sense clustering
     * @param lexeme_file
     *            The tab-separated lexeme list
     * @param extRef
     *            Do we have external references or UBY IDs?
     */
    public static void filterByLexemeList(OneResourceBuilder bg_1,
            String input_file, String lexeme_file, boolean extRef)
    {

        try {
            Statement statement = bg_1.connection.createStatement();
            HashMap<String, String> extRefLemmaMap = new HashMap<String, String>();
            ResultSet rs;
            if (extRef) {
                rs = statement
                        .executeQuery("SELECT externalReference, writtenForm, partOfSpeech FROM LexicalEntry join FormRepresentation_Lemma join Sense join MonolingualExternalRef where FormRepresentation_Lemma.lemmaId = LexicalEntry.lemmaId and Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and MonolingualExternalRef.senseId = Sense.senseId and Sense.senseId like '"
                                + bg_1.prefix_string + "%'");

            }
            else {
                rs = statement
                        .executeQuery("SELECT  senseId, writtenForm, partOfSpeech FROM  LexicalEntry     join   FormRepresentation_Lemma      join  Sense where FormRepresentation_Lemma.lemmaId = LexicalEntry.lemmaId  and Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and Sense.senseId like '"
                                + bg_1.prefix_string + "%'");
            }
            while (rs.next()) {
                extRefLemmaMap.put(rs.getString(1),
                        rs.getString(2) + "\t" + rs.getString(3));
            }
            FileReader in = new FileReader(lexeme_file);
            BufferedReader input = new BufferedReader(in);
            HashSet<String> lexemes = new HashSet<String>();
            String line = "";
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(input_file + "_lexemeFiltered");
            p = new PrintStream(outstream);
            while ((line = input.readLine()) != null) {
                lexemes.add(line.replace(" ", "_")); // Assuming spaces are
                                                     // represented by
                                                     // underscores in the
                                                     // lexeme list
            }
            in.close();
            in = new FileReader(input_file);
            input = new BufferedReader(in);
            while ((line = input.readLine()) != null) {

                String[] ids = line.split("\t");
                String lemmaPos = extRefLemmaMap.get(ids[0]);
                if (lexemes.contains(lemmaPos)) {
                    p.println(line);
                }

            }
            p.close();
            input.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method checks for duplicate senses in the same cluster and removes
     * them
     *
     *
     * @param input_file
     *            the clustering
     */
    public static void checkForDupesInSameCluster(String input_file)
    {
        try {
            FileReader in = new FileReader(input_file);
            BufferedReader input = new BufferedReader(in);
            HashSet<String> alreadySeen2 = new HashSet<String>();
            String line = "";
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(input_file + "_noDupes");
            p = new PrintStream(outstream);
            while ((line = input.readLine()) != null) {
                String out = "";
                String[] ids = line.split("\t");
                int c = 0;
                for (String id : ids) {
                    if (alreadySeen2.contains(id)) {
                        continue;
                    }
                    alreadySeen2.add(id);
                    out += id + "\t";
                    c++;
                }
                if (c < 2) {
                    continue;
                }

                p.println(out.trim());
            }
            p.close();
            input.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method iteratively merges clusters which share senses, until the
     * number of clusters stays stable
     *
     * @param input_file
     *            the sense cluster file
     *
     *
     */
    public static void mergeClustersWithSharedSenses(String input_file)
    {

        try {
            HashSet<String> input_clusters = new HashSet<String>();
            HashSet<String> output_clusters = new HashSet<String>();
            FileReader in = new FileReader(input_file);
            BufferedReader input = new BufferedReader(in);
            String l = "";
            while ((l = input.readLine()) != null) {
                input_clusters.add(l);
            }
            input.close();
            int count = 0;
            int cluster_size = input_clusters.size();
            while (output_clusters.size() < cluster_size) {
                output_clusters = new HashSet<String>();
                cluster_size = input_clusters.size();
                HashMap<String, HashSet<String>> reverse_candidates = new HashMap<String, HashSet<String>>();
                HashMap<String, HashSet<String>> candidates = new HashMap<String, HashSet<String>>();
                HashSet<String> alreadySeen2 = new HashSet<String>();
                for (String line : input_clusters) {
                    if (alreadySeen2.contains(line)) {
                        continue;
                    }
                    alreadySeen2.add(line);
                    count++;
                    String[] elements = line.split("\t");
                    for (String id : elements) {
                        if (!candidates.containsKey(id)) {
                            candidates.put(id, new HashSet<String>());
                        }
                        if (!reverse_candidates.containsKey(count + "")) {
                            reverse_candidates.put(count + "",
                                    new HashSet<String>());
                        }
                        reverse_candidates.get(count + "").add(id);
                        candidates.get(id).add(count + "");
                    }
                }

                HashSet<String> alreadySeen = new HashSet<String>();
                HashMap<String, HashSet<String>> reverse_candidates_merged = new HashMap<String, HashSet<String>>();

                for (String id_mirror : reverse_candidates.keySet()) {
                    if (alreadySeen.contains(id_mirror)) {
                        continue;
                    }
                    reverse_candidates_merged.put(id_mirror,
                            new HashSet<String>());
                    alreadySeen.add(id_mirror);
                    HashSet<String> rev_cluster = reverse_candidates
                            .get(id_mirror);
                    for (String sense : rev_cluster) {
                        HashSet<String> cluster = candidates.get(sense);
                        reverse_candidates_merged.get(id_mirror).add(sense);

                        cluster.remove(id_mirror);
                        if (rev_cluster.size() == 0) {
                            // do nothing
                        }
                        else {
                            for (String remaining : cluster) {
                                reverse_candidates_merged.get(id_mirror)
                                        .addAll(reverse_candidates
                                                .get(remaining));
                                alreadySeen.add(remaining);
                            }
                        }

                    }
                }

                for (String id_mirror : reverse_candidates_merged.keySet()) {

                    HashSet<String> cluster = reverse_candidates_merged
                            .get(id_mirror);
                    if (cluster.size() == 1) {
                        continue;
                    }
                    String output_cluster = "";
                    for (String sense : cluster) {
                        output_cluster += sense + "\t";
                    }
                    output_cluster = output_cluster.trim();
                    if (alreadySeen.contains(output_cluster)) {
                        continue;
                    }
                    else {
                        alreadySeen.add(output_cluster);
                    }
                    output_clusters.add(output_cluster);
                }
                input_clusters = output_clusters;

            }
            FileOutputStream outstream;
            PrintStream p;
            outstream = new FileOutputStream(input_file + "_merged");
            p = new PrintStream(outstream);
            for (String s : output_clusters) {
                p.println(s);
            }
            p.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
