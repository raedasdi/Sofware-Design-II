/* WORD LADDER Node.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * Raed Asdi
 * raa3426
 * 16300
 * Slip days used: <1>
 * Git URL:
 * Summer 2019
 */

package assignment3;

import java.util.*;

//import project3.Node;

public class Node {
		private String word;
		private ArrayList<String> edges;
		private int distance;
		
		public int getDistance() {
			return distance;
		}

		public void setDistance(int distance) {
			this.distance = distance;
		}
		
		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public ArrayList<String> getEdges() {
			return edges;
		}

		public void setEdges(ArrayList<String> edges) {
			this.edges = edges;
		}
		
		public void addEdges(String s) {
			edges.add(s);
		}

		public Node(String s) {
			word = s;
			edges = new ArrayList<String>();
			distance = -1;
		}
		
		public Node() {
			word = "";
			edges = new ArrayList<String>();
			distance = -1;
		}
		
		public Node(Node other) {
			this.word = other.getWord();
			this.edges = other.getEdges();
			this.distance = other.getDistance();
		}
		
		public void generateEdgeList(Set<String> dict) {
			ArrayList<String> newEdges = new ArrayList<String>();
			int wordLength = this.word.length();
			Iterator<String> dIterator = dict.iterator();
			
			while(dIterator.hasNext()) {
				String temp = dIterator.next();
				int charCount = 0;
				/*
				 * iterates through word and counts similar characters
				 * adds if all characters similar except one, meaning word
				 * is in ladder/connected to current word
				 */
				if(temp.length() == wordLength) {
					for(int i = 0; i < wordLength; i++) {
						if (word.charAt(i) == temp.charAt(i)) {
							charCount++;
						}
					}
					if (charCount == wordLength-1) {
						newEdges.add(temp);
					}
				}
			}
			this.setEdges(newEdges);
		}
		
//<<<<<<< HEAD
		
//=======
		public boolean isConnected(Node a) {
			return edges.contains(a.getWord());
		}
//>>>>>>> branch 'master' of https://github.com/EE422C/assignment3-spring-20-sp20-pr3-pair-6.git
		
		@Override
		public String toString() {
			String retString = this.getWord() + ": ";
			ArrayList<String> adjList = this.getEdges();
			Iterator<String> adjIter = adjList.iterator();
			
			while(adjIter.hasNext()) {
				retString += adjIter.next() + ", ";
			}
			
			return retString;
		}
		
		public boolean equals(Node n) {
			return ((this.getWord()).matches(n.getWord()));
		}
}
