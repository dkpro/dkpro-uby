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