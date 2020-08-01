/*
 * CRITTERS Main.java
 * EE422C Project 4 submission by
 * Alina Nguyen
 * amn2763
 * 16300
 * Raed Asdi
 * raa3426
 * 16300
 * Slip days used: <0>
 * Spring 2020
 */

package assignment4;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/*
 * Usage: java <pkg name>.Main <input file> test input file is
 * optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */

public class Main {

	/* Scanner connected to keyboard input, or input file */
	static Scanner kb;

	/* Input file, used instead of keyboard input if specified */
	private static String inputFile;

	/* If test specified, holds all console output */
	static ByteArrayOutputStream testOutputString;

	/* Use it or not, as you wish! */
	private static boolean DEBUG = false;

	/* if you want to restore output to console */
	static PrintStream old = System.out;

	/*
	 * Gets the package name. The usage assumes that Critter and its subclasses are
	 * all in the same package.
	 */
	private static String myPackage; // package of Critter file.

	/* Critter cannot be in default pkg. */
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}

	/** 
	 * Main method.
	 *
	 * @param args args can be empty. If not empty, provide two parameters -- the
	 *             first is a file name, and the second is test (for test output,
	 *             where all output to be directed to a String), or nothing.
	 * @throws InvalidCritterException 
	 */
	public static void main(String[] args){
		if (args.length != 0) {
			try {
				inputFile = args[0];
				kb = new Scanner(new File(inputFile));
			} catch (FileNotFoundException e) {
				System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
			}
			if (args.length >= 2) {
				/* If the word "test" is the second argument to java */
				if (args[1].equals("test")) {
					/* Create a stream to hold the output */
					testOutputString = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(testOutputString);
					/* Save the old System.out. */
					old = System.out;
					/*
					 * Tell Java to use the special stream; all console output will be redirected
					 * here from now
					 */
					System.setOut(ps);
				}
			}
		} else { // If no arguments to main
			kb = new Scanner(System.in); // Use keyboard and console
			
		}
		commandInterpreter(kb);

		System.out.flush();
	}

	/* Do not alter the code above for your submission. */
	/**
	 * commandInterpreter: will read in commands from keyboard and
	 * 					   manipulate game
	 * @param kb - the keyboard object
	 * @throws InvalidCritterException - if critter is invalid
	 */
	private static void commandInterpreter(Scanner kb) { //not sure if i should have this here but eclipse says i should
		while(true) { //loop that gets the commands
			System.out.print("critter>");
			
			String cmd = kb.nextLine();
			String command[] = cmd.split(" ");
			
			if(command[0].equals("quit") && command.length == 1) {//for quit command
				break;
			}
			
			else if (command[0].equals("show") && command.length == 1) {//for show command
				Critter.displayWorld();
			}
			
			else if (command[0].equals("step") && command.length == 1) {//for single step command
					Critter.worldTimeStep();
			}
			
			else if (command[0].equals("step") && isInteger(command[1]) && command.length == 2) {//for multiple step command
				int stepCount = Integer.parseInt(command[1]);
				
				for (int i = 1; i <= stepCount; i++) {
					Critter.worldTimeStep();
				}
			}
			
			else if (command[0].equals("seed") && isInteger(command[1]) && command.length == 2) {//for seed command
				int seedNumber = Integer.parseInt(command[1]);
				Critter.setSeed(seedNumber);
			}
			
			else if (command[0].equals("create") && command.length == 3 && isInteger(command[2])) {				
				try {
					for (int i = 0; i<Integer.parseInt(command[2]); i++) {
						Critter.createCritter(command[1]);
					}
				} catch(InvalidCritterException e) {
					System.out.println("error processing: " + cmd);
				}
				
				
			}
			
			else if(command[0].equals("stats") && command.length==2) {
				//for stats command, need to add parameter of checking if second word in command
				//is an actual critter in the game 
				List<Critter> stats;
				try {
					stats = Critter.getInstances(command[1]);
					if(command[1].equals("Critter")) Critter.runStats(stats);
					else try {
						Class<?> critterClass = Class.forName("assignment4." + command[1]);
						Object ob = critterClass.newInstance();
						Method statsMethod = null;
						try {
							statsMethod = ob.getClass().getMethod("runStats", List.class);
						} catch(NoSuchMethodException e) {
							System.out.println("error processing: " + cmd);
						} catch(SecurityException e){
							System.out.println("error processing: " + cmd);
						}
						try {
							statsMethod.invoke(ob, stats);
						} catch(IllegalArgumentException e) {
							System.out.println("error processing: " + cmd);
							return;
						} catch(InvocationTargetException e) {
							System.out.println("error processing: " + cmd);
							return;
						}
						
					} catch (ClassNotFoundException e) {
						System.out.println("error processing: " + cmd);
					} catch (InstantiationException e) {
						System.out.println("error processing: " + cmd);
					} catch (IllegalAccessException e) {
						System.out.println("error processing: " + cmd);
					}
				} catch (InvalidCritterException e1) {
					System.out.println("error processing: " + cmd);
				}
				
			}
			
			else if(command[0].equals("clear") && command.length == 1) {
				Critter.clearWorld();
			}
			
			else {
				System.out.println("error processing: " + cmd);
			}
		}
	} 
			
	static boolean isInteger(String s) { //checks to make sure a string in the command is an integer
	        for (int i = 0; i < s.length(); i++) 
	        if (Character.isDigit(s.charAt(i)) == false) {
	            return false; 
	        }
	        return true; 
	    } 
	
	
}
