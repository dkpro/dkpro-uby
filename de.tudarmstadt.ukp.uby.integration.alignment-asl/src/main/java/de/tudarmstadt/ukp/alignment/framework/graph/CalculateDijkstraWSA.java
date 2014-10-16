/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.alignment.framework.graph;

public class CalculateDijkstraWSA
{
	/**
	 *
	 *
	 * For maximum efficiency, the actual calculation of the shortest paths is not done in Java, but C. An efficient reimplementation in Java is pending
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
