/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Git URL:
 * Summer 2019
 */


package assignment3;
import java.util.*;

import assignment3.Node;
import assignment3.BFSGraph;

import java.io.*;

public class Main {
	
	public static ArrayList<String> ladder;
	public static ArrayList<String> noLadder;
	public static ArrayList<Integer> index;
	public static ArrayList<Node> graph;
	public static ArrayList<Node> alreadySearchedGraph;
	public static int wordLength;
	public static String startWord;
	public static String endWord;
	public static int foundFlag;
	public static int distance;
	public static int efficiency;
	
	public static boolean ladderFound;
	public static ArrayList<String> input;

	// static variables and constants only here.
	
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default input from Stdin
			ps = System.out;			// default output to Stdout
		}
		initialize();
		ArrayList<String> input = parse(kb);
		if (input.isEmpty() == false) {
		printLadder(getWordLadderDFS(input.get(0), input.get(1)));
		input = parse(kb);
		printLadder(getWordLadderBFS(input.get(0), input.get(1)));
		}
		// TODO methods to read in words, output ladder
	}
	
	public static void initialize() {
		foundFlag = 0;
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
		graph = new ArrayList<Node>();
		index = new ArrayList<Integer>();
		alreadySearchedGraph = new ArrayList<Node>();
		ladder = new ArrayList<String>();
		noLadder = new ArrayList<String>();
		input = new ArrayList<String>();
		ladderFound = false;
		distance = 0;
		efficiency = 0;
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		ArrayList<String> searchlist = new ArrayList<String>();// TO DO
		String input = keyboard.nextLine();
		String [] inputarray = input.split(" ", 2);
		startWord = inputarray[0];
		if (startWord.matches("/quit")){
			return searchlist;
		}
		else {
			searchlist.add(startWord);
			endWord = inputarray[1];
			searchlist.add(endWord);
			wordLength = startWord.length();
			noLadder.add(startWord);
			noLadder.add(endWord);
			return searchlist;
		}
		//return null;
	}
	
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
		Set<String> dict = makeDictionary();
		Iterator<String> dictIterator = dict.iterator();
		//Queue<Node> discOrder = new LinkedList<Node>();
		Node startNode = new Node(start);
		ArrayList<String> edges = new ArrayList <String>();
		startNode.setDistance(distance);
		while(dictIterator.hasNext() && foundFlag!=1 && startNode.getDistance()!=-1 ) {//while still have words to go through dictionary 
			String nextWord = dictIterator.next();
			
			int flag = compareWords(nextWord, start);
			index.clear();
			if (nextWord.contentEquals(end.toUpperCase()) && flag>=1 ) {//reached endword
				ladder.add(startWord);
				for (int i = 0; i<graph.size(); i++) {
					ladder.add(graph.get(i).getWord());
				}
				ladder.add(endWord);
				foundFlag = 1;
				
				for (int j = 0; j<ladder.size(); j++) {
					ladder.set(j, ladder.get(j).toLowerCase());
				}
				
				System.out.println("a " + ladder.size() + "-rungword ladder exists between " + startWord + " and " + endWord + ".");
			}
			else {
				int flag2 = compareWords(nextWord, start);
				index.clear();
				String stword = startWord.toUpperCase();
				if (flag2 == 1 && inGraph(nextWord)== false && !nextWord.contentEquals(stword)) {//word is similar
					edges.add(nextWord);
					startNode.setEdges(edges);
					Node temp = new Node(nextWord);
					graph.add(temp);
					distance++;
					temp.setDistance(distance);
					temp.setEdges(getWordLadderDFS(nextWord, end));
					if (foundFlag == 1) {
						return ladder;
					}
				}
			}
			
		}
		if (distance == 0) {
			ladder.clear();
			ladder.add(startWord);
			ladder.add(endWord);
			return ladder;
		}
		else{
			int size = graph.size();
			String discardWord = graph.get(size-1).getWord();
			Node discard = new Node(discardWord);
			graph.remove(size-1);
			alreadySearchedGraph.add(discard);
			distance--;
			dictIterator = dict.iterator();
			return ladder;
		}
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		// TODO some code
		
	}
	
	public static ArrayList<String> getWordLadderBFS(String start, String end) {
		startWord = start.toUpperCase();
		endWord = end.toUpperCase();
		int wordLength = start.length();
		int endLength = end.length();
		ladder.clear();
		Node startNode = new Node(startWord);
		Set<String> dict = makeDictionary();
		Node endNode = new Node(endWord);
		
		if(wordLength != endLength) {
			ladderFound = false;
			ladder.add(start);
			ladder.add(end);
			return ladder;
		}
		
		int charCount = 0;
		
		for(int i = 0; i < wordLength; i++) {
			if(startWord.charAt(i) == endWord.charAt(i))
				charCount++;
		}
		
		if(charCount == wordLength-1) {
			ladderFound = true;
			ladder.add(start);
			ladder.add(end);
			return ladder;
		}
		
		ladderFound = false;
		
		BFSGraph graphBFS = new BFSGraph();
    	ArrayList<Node> discovered = new ArrayList<Node>();
    	
	
		Iterator<String> dIterator = dict.iterator();
		Queue<Node> discQueue = new LinkedList<Node>();
		
		if(!dict.contains(startNode.getWord())) {
			startNode.generateEdgeList(dict);
			startNode.setDistance(0);
			graphBFS.add(startNode);
		}
		if(!dict.contains(endNode.getWord())) {
			endNode.generateEdgeList(dict);
			graphBFS.add(endNode);
		}
		
		while(dIterator.hasNext()) {
			Node temp = new Node(dIterator.next());
			temp.generateEdgeList(dict);
			graphBFS.add(temp);
		}
		
		dict.add(startWord);
		dict.add(endWord);
			
		discQueue.add(graphBFS.nodeOf(startWord));
		int level = 1;
		
		while (!discQueue.isEmpty()) {
			Node s = new Node(discQueue.peek());
			s.generateEdgeList(dict);
			ArrayList<String> sEdges = s.getEdges();
			
			if ((s.getWord()).equals(endNode.getWord())) {
				discovered.add(s);
				ladderFound = true;
			}

			for(int i = 0; i < sEdges.size(); i++) {
				Node temp = new Node(graphBFS.nodeOf(sEdges.get(i))); 
				if(!discQueue.contains(temp) || !discovered.contains(temp)) {
					graphBFS.setNodeDist(temp, level);
					temp.setDistance(level);
					discQueue.add(temp);
				}
			}
			
			discovered.add(s);
			level++;
			discQueue.remove();
		}
		
		if(!ladderFound) {
			ladder.add(startWord.toLowerCase());
			ladder.add(endWord.toLowerCase());
			return ladder;
		}
		
		ArrayList<String> ladderTemp = new ArrayList<String>();
		ladderTemp.add(endNode.getWord());
		level = endNode.getDistance();
		
		int endNodeIndex = discovered.indexOf(endNode);
		boolean searchComplete = false;
		
		Node prev = new Node(endNode);
		for(int i = endNodeIndex-1; i >= 0; i--) {
			Node temp = new Node(discovered.get(i));
			if(temp.getDistance() < level && temp.isConnected(prev) && !searchComplete) {
				ladderTemp.add(temp.getWord());
				level--;
				prev = new Node(temp);
				if (level == 0) searchComplete = true;
			}
		}
		
		for(int i = ladderTemp.size()-1; i >= 0; i++) {
			ladder.add(ladderTemp.get(i).toLowerCase());
		}
		
		return ladder; // replace this line later with real return
	}
    
    public static Node graphContains(Node a) {
    	Node temp = new Node();
    	for(int i = 0; i < graph.size(); i++) {
    		if (graph.get(i).equals(a)) return graph.get(i);
    	}
    	return temp;
    }
    
	
	public static void printLadder(ArrayList<String> ladder) {
		if(ladder.equals(noLadder)) {
			System.out.println("no word ladder can be found between " + noLadder.get(0) + " and " + noLadder.get(1));
		}
		else if(!ladder.isEmpty()) {
			System.out.println(ladder.get(0));
			ladder.remove(0);
			printLadder(ladder);
		}
	}
	// TODO
	// Other private static methods here
	public static int compareWords(String wordInput, String originalWord) {//return 1 if similar word, return 2 if reached endword
		int wordLength = wordInput.length();
		boolean similar = true;
		wordInput = wordInput.toUpperCase();
		originalWord = originalWord.toUpperCase();
		String end = endWord.toUpperCase();
		String start = startWord.toUpperCase();
		for (int a = 0; a<wordLength; a++) {
			if (start.charAt(a) == end.charAt(a)) {
				index.add(a);
			}
		}
			int charCount = 0;
			/*
			 * iterates through word and counts similar characters
			 * adds if all characters similar except one, meaning word
			 * is in ladder/connected to current word
			 */
			for(int i = 0; i < wordLength; i++) {
				if (wordInput.charAt(i) == originalWord.charAt(i)) {
					charCount++;
				}
			}
			
			
			for (int j = 0; j<index.size(); j++) {
				if ( wordInput.charAt(index.get(j)) != originalWord.charAt(index.get(j))) {
					similar = false;
				}
			}
			
			if (charCount == wordLength-1 & similar==true) {
				return 1;	
			}
			if (charCount == wordLength && wordInput == end) {
				return 2;	
			}
			else {
				return 0;
			}
		
		}
			

		
		
		//setEdges(newEdges);
	

	public static boolean inGraph(String checkWord) {
		for(int i = 0; i < graph.size(); i++) {
    		if (graph.get(i).getWord().contentEquals(checkWord)) {
    			return true;
    		}
    	}
		for(int j = 0; j < alreadySearchedGraph.size(); j++) {
    		if (alreadySearchedGraph.get(j).getWord().contentEquals(checkWord)) {
    			return true;
    		}
    	}
		return false;
	}
	
	
	
	/* Do not modify makeDictionary */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
}
