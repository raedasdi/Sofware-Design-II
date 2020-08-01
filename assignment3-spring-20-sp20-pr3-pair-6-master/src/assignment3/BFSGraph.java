package assignment3;

import java.util.*;

public class BFSGraph {
	private ArrayList<Node> adjList;
	
	public BFSGraph() {
		adjList = new ArrayList<Node>();
	}
	
	public BFSGraph(ArrayList<Node> graph) {
		this.adjList = graph;
	}
	
	public int indexOf(String word) {
		int index = -1;
		
		for (int i = 0; i < this.adjList.size(); i++) {
			if (word.matches(this.adjList.get(i).getWord())) index = i;
		}
		
		return index;
	}
	
	public Node nodeOf(String word) {
		return this.adjList.get(this.indexOf(word));
	}
	
	public void setNodeDist(Node a, int n) {
		ArrayList<Node> temp = this.getAdjList();
		Node change = new Node(temp.get(temp.indexOf(a)));
		temp.remove(a);
		change.setDistance(n);
		temp.add(change);
		this.setAdjList(temp);
	}
	
	public void add(Node a) {
		this.adjList.add(a);
	}

	public ArrayList<Node> getAdjList() {
		return adjList;
	}

	public void setAdjList(ArrayList<Node> adjList) {
		this.adjList = adjList;
	}
	
	
}
