package de.tudarmstadt.ukp.alignment.framework.graph;

public class CalculateDijkstraWSA
{
	/**
	 *
	 * Surprise!
	 *
	 * For maximum efficiency, the actual calculation of the shortest paths is not done in Java, but C++.
	 *
	 * In the src/main/resources folder you will find an executable which has been compiled on 64-Bit Ubuntu Linux, but also
	 * an archive containing the sources if you need to recompile. The program takes three arguments:
	 *
	 *  1) The graph constructed in JointGraphBuilder
	 *  2) The candidate file constructed in CandidateExtractor
	 *  3) An output file, which can in turn be read by CreateAlignmentFromGraphOutput
	 *
	 * A shell script with an example execution is also provided.
	 *
	 *
	 *
	 *
	 */
}
