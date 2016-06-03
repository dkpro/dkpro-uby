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
package de.tudarmstadt.ukp.alignment.framework.graph;

public class NodeWithDistance implements Comparable
{
	public int id;
	public int path_length;

	public NodeWithDistance(int id, int path_length)
	{
		this.id = id;
		this.path_length = path_length;
	}

	@Override
	public int compareTo(Object o)
	{
		NodeWithDistance nwd = (NodeWithDistance) o;
		if(nwd.path_length<this.path_length) {
			return 1;
		}
		else if(nwd.path_length>this.path_length) {
			return -1;
		}
		else if(nwd.id > this.id) {
			return -1;
		}
		else if(nwd.id < this.id) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return id+" "+path_length;

	}


}