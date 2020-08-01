/*
 * CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Alina Nguyen
 * amn2763
 * 16300
 * Raed Asdi
 * raa3426
 * 16300
 * Slip days used: <0>
 * Spring 2020
 */


//
package assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* 
 * See the PDF for descriptions of the methods and fields in this
 * class. 
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

	private int energy = 0;

	private int x_coord;
	private int y_coord;

	private static List<Critter> population = new ArrayList<Critter>(); 
	private static List<Critter> babies = new ArrayList<Critter>();
	private static String [][] gameboard = new String [Params.WORLD_HEIGHT+2][Params.WORLD_WIDTH+2];
	/*
	 * Gets the package name. This assumes that Critter and its subclasses are all
	 * in the same package.
	 */
	private static String myPackage;

	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}

	private static Random rand = new Random();

	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}

	public static void setSeed(long new_seed) {
		rand = new Random(new_seed);
	}
	
	/**
	 * setPosition: sets position of the critter
	 * @param x	- int for the x coordinate
	 * @param y - int for the y coordinate
	 */
	public void setPosition(int x, int y) {
		this.x_coord = x;
		this.y_coord = y;
	}
	
	public int getXPos() {
		return this.x_coord;
	}
	
	public int getYPos() {
		return this.y_coord;
	}
	
	public static boolean comparePos(Critter critter1, Critter critter2) {//true if in same position -> encounter
		return ((critter1.getXPos() == critter2.getXPos()) && (critter1.getYPos() == critter2.getYPos()));
	}
	
	/**
	 * setEnergy: sets energy level of critter
	 * @param energy - int for level of energy to be set
	 */
	public void setCritterEnergy(int energy) {
		this.energy = energy;
	}

	/**
	 * create and initialize a Critter subclass. critter_class_name must be the
	 * qualified name of a concrete subclass of Critter, if not, an
	 * InvalidCritterException must be thrown.
	 *
	 * @param critter_class_name
	 *  @throws InvalidCritterException
	 */
	public static void createCritter(String critter_class_name) throws InvalidCritterException {
		try {
		Class<?> critterClass = Class.forName("assignment4." + critter_class_name);
		Critter newCritter = (Critter) critterClass.newInstance();
		newCritter.setPosition(1+(getRandomInt(Params.WORLD_WIDTH)), 1+(getRandomInt(Params.WORLD_HEIGHT)));
		newCritter.setCritterEnergy(Params.START_ENERGY);
		population.add(newCritter);
		} catch (NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		} catch (InstantiationException e) {
			throw new InvalidCritterException(critter_class_name);
		} catch (IllegalAccessException e) {
			throw new InvalidCritterException(critter_class_name);
		} catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}
		
	}

	/**
	 * Gets a list of critters of a specific type.
	 *
	 * @param critter_class_name What kind of Critter is to be listed. Unqualified
	 *                           class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		if(critter_class_name.equals("Critter")) {
			return population;
		}
		
		Object tempCritter = null;
		List<Critter> instances = new ArrayList<Critter>();
		
		try {
			Class<?> critterClass = Class.forName("assignment4." + critter_class_name);
			try {
				tempCritter = critterClass.newInstance();
			}
			catch(InstantiationException e) {
				System.out.println("Invalid Command: states " + critter_class_name);
				return null;
			}
			catch(IllegalAccessException e) {
				System.out.println("Invalid Command: states " + critter_class_name);
				return null;
			}
			
		}
		catch(ClassNotFoundException e){
			System.out.println("Invalid Command: states " + critter_class_name);
			return null;
		}
		
		for(int i = 0; i < population.size(); i++) {
			if((population.get(i)).getClass().isInstance(tempCritter)) {
				instances.add(population.get(i));
			}
		}
		return instances;
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population.clear();
		babies.clear();
		
		for (int s = 1; s<=Params.WORLD_HEIGHT; s++) {//fills blanks for board
			for (int t = 1; t<= Params.WORLD_WIDTH; t++)
				gameboard[s][t] = " ";
		}
	}

	public static void worldTimeStep() {		
		
		for (int i = 0; i < population.size(); i++) {
			population.get(i).doTimeStep();
		}
		
		for(Critter critter1: population) {
			for(Critter critter2: population) {
				if(critter1 != critter2 && comparePos(critter1, critter2) && critter1.getEnergy()>0 && critter2.getEnergy()>0) {
					handleEncounter(critter1, critter2);
				}
			}
		}
		
		for(Critter critter: population) critter.setCritterEnergy(critter.getEnergy() - Params.REST_ENERGY_COST);
		
		for(int i = 0; i < population.size(); i++) {
			if(population.get(i).getEnergy() <= 0) population.remove(i);
		}
		
		for(int i = 0; i < babies.size(); i++) {
			population.add(babies.get(i));
			//babies.remove(i);
		}
		babies.clear();
		
		
	}
	
	public static void handleEncounter(Critter a, Critter b) {
		if(a.getEnergy() <= 0 || b.getEnergy() <= 0) return;
		int aRoll = -1;
		int bRoll = -1;
		
		if(a.fight(b.toString())) {
			if(a.getEnergy() <= 0) return;
			aRoll = Critter.getRandomInt(a.getEnergy());
		}
		if(b.fight(a.toString())) {
			if(b.getEnergy() <= 0) return;
			bRoll = Critter.getRandomInt(b.getEnergy());
		}
		
		if(comparePos(a,b)) {
			if(aRoll >= bRoll) {
				a.setCritterEnergy(a.getEnergy() + b.getEnergy()/2);
				b.setCritterEnergy(0);
			} else {
				b.setCritterEnergy(b.getEnergy() + a.getEnergy()/2);
				a.setCritterEnergy(0);
			}
		}
		
		return;
	}

	public static void displayWorld() {
		gameboard[0][0] = "+";
		gameboard[0][Params.WORLD_WIDTH+1] = "+";
		gameboard[Params.WORLD_HEIGHT+1][0] = "+";
		gameboard[Params.WORLD_HEIGHT+1][Params.WORLD_WIDTH+1] = "+";//4 corners
		
		for (int i = 1; i<=Params.WORLD_WIDTH; i++) {//top and bottom edge borders
			gameboard[0][i] = "-";
			gameboard[Params.WORLD_HEIGHT+1][i] = "-";
		}
		
		for (int j = 1; j<=Params.WORLD_HEIGHT; j++) {//left and right edge borders
			gameboard[j][0] = "|";
			gameboard[j][Params.WORLD_WIDTH+1] = "|";
		}
		
		for (int s = 1; s<=Params.WORLD_HEIGHT; s++) {//fills blanks for board
			for (int t = 1; t< Params.WORLD_WIDTH; t++)
				gameboard[s][t] = " ";  
		}
		
		
		for (int a = 0; a<population.size(); a++) {//insert critter characters here
			int xCoord = population.get(a).getXPos();
			int yCoord = population.get(a).getYPos();
			
			//System.out.print(xCoord + " " + yCoord + " ");
			gameboard[yCoord][xCoord] = population.get(a).toString();
		}
		
		
		for (int k = 0; k<=Params.WORLD_HEIGHT+1; k++) {//prints out gameboard
			for (int r = 0; r<=Params.WORLD_WIDTH+1; r++) {
				System.out.print(gameboard[k][r]);
			}
			System.out.println();
		}
		// TODO: Complete this method
	}

	/**
	 * Prints out how many Critters of each type there are on the board.
	 *
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		Map<String, Integer> critter_count = new HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			critter_count.put(crit_string, critter_count.getOrDefault(crit_string, 0) + 1);
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();
		
	}

	public abstract void doTimeStep();

	public abstract boolean fight(String opponent);

	/*
	 * a one-character long string that visually depicts your critter in the ASCII
	 * interface
	 */
	public String toString() {
		return myPackage;
	}

	protected int getEnergy() {
		return energy;
	}

	protected final void walk(int direction) {
		this.energy -= Params.WALK_ENERGY_COST;
		if(this.energy <= 0) return;
		int nextX = -1, nextY = -1;
		
		switch(direction) {
			case 0://right
				nextX = this.x_coord + 1;
				nextY = this.y_coord;
				break;
			case 1://upper right
				nextX = this.x_coord + 1;	
				nextY = this.y_coord + 1;
				break;
			case 2://up
				nextX = this.x_coord;
				nextY = this.y_coord + 1;
				break;
			case 3://upper left
				nextX = this.x_coord - 1;	
				nextY = this.y_coord + 1;
				break;
			case 4://left
				nextX = this.x_coord - 1;	
				nextY = this.y_coord;
				break;
			case 5://down left
				nextX = this.x_coord - 1;
				nextY = this.y_coord - 1;
				break;
			case 6://down
				nextX = this.x_coord;
				nextY = this.y_coord - 1;	
				break;
			case 7://down right
				nextX = this.x_coord + 1;	
				nextY = this.y_coord - 1;
				break;
			default:
				System.out.println("invalid direction");
				break;	
		}
		
		this.x_coord = nextX;
		this.y_coord = nextY;
		//wrapping if out of bounds
		if (this.x_coord >= Params.WORLD_WIDTH-1){
			this.x_coord = 1;
		}
		
		if (this.x_coord == 0){
			this.x_coord = Params.WORLD_WIDTH-2;
		}
		
		if (this.y_coord >= Params.WORLD_HEIGHT-1){
			this.y_coord = 1;
		}
	
		if (this.y_coord == 0){
			this.y_coord = Params.WORLD_HEIGHT-2;
		}
	
	}

	protected final void run(int direction) {
		this.energy -= Params.RUN_ENERGY_COST;
		if(this.energy <= 0) return;
		int nextX = -1, nextY = -1;
		
		switch(direction) {
			case 0://right
				nextX = this.x_coord + 2;
				nextY = this.y_coord;
				break;
			case 1://upper right
				nextX = this.x_coord + 2;	
				nextY = this.y_coord + 2;
				break;
			case 2://up
				nextX = this.x_coord;
				nextY = this.y_coord + 2;
				break;
			case 3://upper left
				nextX = this.x_coord - 2;	
				nextY = this.y_coord + 2;
				break;
			case 4://left
				nextX = this.x_coord - 2;	
				nextY = this.y_coord;
				break;
			case 5://down left
				nextX = this.x_coord - 2;
				nextY = this.y_coord - 2;
				break;
			case 6://down
				nextX = this.x_coord;
				nextY = this.y_coord - 2;	
				break;
			case 7://down right
				nextX = this.x_coord + 2;	
				nextY = this.y_coord - 2;
				break;
			default:
				System.out.println("invalid direction");
				break;	
		}
		
		this.x_coord = nextX;
		this.y_coord = nextY;
		//wrapping if out of bounds
		if (this.x_coord >= Params.WORLD_WIDTH){
			this.x_coord = (this.x_coord - Params.WORLD_WIDTH) + 1;
		}
		if (this.x_coord == 0){
			this.x_coord = Params.WORLD_WIDTH-2;
		}
		if (this.x_coord < 0){
			this.x_coord = Params.WORLD_WIDTH-3;
		}
		if (this.y_coord >= Params.WORLD_HEIGHT){
			this.y_coord = (this.y_coord - Params.WORLD_HEIGHT) + 1;
		}
		if (this.y_coord == 0){
			this.y_coord = Params.WORLD_HEIGHT-2;
		}
		if (this.y_coord < 0){
			this.y_coord = Params.WORLD_HEIGHT-3;
		}

	}

	protected final void reproduce(Critter offspring, int direction) {
		if (this.getEnergy() < Params.MIN_REPRODUCE_ENERGY) {
			return;
		}
		
		int parentEnergy = this.getEnergy();
		offspring.setCritterEnergy(parentEnergy/2);
		this.setCritterEnergy(parentEnergy - offspring.getEnergy());
		offspring.setPosition(this.getXPos(), this.getYPos());
		offspring.walk(direction);
		babies.add(offspring);
	}

	/**
	 * The TestCritter class allows some critters to "cheat". If you want to create
	 * tests of your Critter model, you can create subclasses of this class and then
	 * use the setter functions contained here.
	 * <p>
	 * NOTE: you must make sure that the setter functions work with your
	 * implementation of Critter. That means, if you're recording the positions of
	 * your critters using some sort of external grid or some other data structure
	 * in addition to the x_coord and y_coord functions, then you MUST update these
	 * setter functions so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {

		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}

		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}

		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}

		protected int getX_coord() {
			return super.x_coord;
		}

		protected int getY_coord() {
			return super.y_coord;
		}

		/**
		 * This method getPopulation has to be modified by you if you are not using the
		 * population ArrayList that has been provided in the starter code. In any case,
		 * it has to be implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}

		/**
		 * This method getBabies has to be modified by you if you are not using the
		 * babies ArrayList that has been provided in the starter code. In any case, it
		 * has to be implemented for grading tests to work. Babies should be added to
		 * the general population at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}
}
