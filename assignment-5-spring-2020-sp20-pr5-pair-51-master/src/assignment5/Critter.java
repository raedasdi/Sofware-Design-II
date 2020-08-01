/* CRITTERS Critter.java
 * EE422C Project 5 submission by
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

package assignment5;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
//import javafx.scene.paint.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.lang.Double;

/*
 * See the PDF for descriptions of the methods and fields in this
 * class.
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    /* START --- NEW FOR PROJECT 5 */
    public enum CritterShape {
        CIRCLE,
        SQUARE,
        TRIANGLE,
        DIAMOND,
        STAR
    }

    /* the default color is white, which I hope makes critters invisible by default
     * If you change the background color of your View component, then update the default
     * color to be the same as you background
     *
     * critters must override at least one of the following three methods, it is not
     * proper for critters to remain invisible in the view
     *
     * If a critter only overrides the outline color, then it will look like a non-filled
     * shape, at least, that's the intent. You can edit these default methods however you
     * need to, but please preserve that intent as you implement them.
     */
    public javafx.scene.paint.Color viewColor() {
        return javafx.scene.paint.Color.WHITE;
    }

    public javafx.scene.paint.Color viewOutlineColor() {
        return viewColor();
    }

    public javafx.scene.paint.Color viewFillColor() {
        return viewColor();
    }

    public abstract CritterShape viewShape();
    
	/////////////////////////////////////////////////////////////////////////////////
	//                                                                             //
	//                                                                             //
	//                           static methods below                              //
	//                                                                             //
	//                                                                             //
	/////////////////////////////////////////////////////////////////////////////////
    
    public void setPosition(int x, int y) {
		this.x_coord = x;
		this.y_coord = y;
	}
    
    public boolean hasMoved() {
    	return this.hasMoved;
    }
    
    public void setMoved(boolean moved) {
    	this.hasMoved = moved;
    }
    
    public boolean inFight() {
    	return this.fighting;
    }
    
    public void setFight(boolean fighting) {
    	this.fighting = fighting;
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
	
	public static void handleEncounter(Critter a, Critter b) {
		if(a.getEnergy() <= 0 || b.getEnergy() <= 0) {
			a.setFight(false);
			b.setFight(false);
			return;
		}
		int aRoll = -1;
		int bRoll = -1;
		
		if(a.fight(b.toString())) {
			if(a.getEnergy() <= 0) {
				a.setFight(false);
				b.setFight(false);
				return;
			}
			aRoll = Critter.getRandomInt(a.getEnergy());
		}
		if(b.fight(a.toString())) {
			if(b.getEnergy() <= 0) {
				a.setFight(false);
				b.setFight(false);
				return;
			}
			bRoll = Critter.getRandomInt(b.getEnergy());
		}
		
		if(comparePos(a,b)) {
			if(aRoll >= bRoll) {
				a.setCritterEnergy(a.getEnergy() + b.getEnergy()/2);
				b.setCritterEnergy(0);
				a.setFight(false);
				b.setFight(false);
			} else {
				b.setCritterEnergy(b.getEnergy() + a.getEnergy()/2);
				a.setCritterEnergy(0);
				a.setFight(false);
				b.setFight(false);
			}
		}
		
		return;
	} 
	/**
	 * 	critterLook - public method that calls look method
	 * @param direction - a number 0-7 that designates the direction
	 * @param speed	- how far to look, aka running or walking
	 * @return
	 */
	public String critterLook(int direction, boolean speed) {
    	return look(direction, speed);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//                                                                             //
	//                                                                             //
	//                           static methods above                              //
	//                                                                             //
	//                                                                             //
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 	look - this returns a string of what critter is in the direction
	 * 		   from this critter
	 * @param direction - a number 0-7 that designates the direction
	 * @param steps	- how far to look, aka running or walking
	 * @return
	 */
    protected final String look(int direction, boolean steps) {
    	this.energy = this.getEnergy() - Params.LOOK_ENERGY_COST;
    	int xpos = this.x_coord;
    	int ypos = this.y_coord;
    	
    	int lookx = xpos;
    	int looky = ypos;
    	int stepCount = 1;
    	if (steps) {
    		stepCount = 2;
    	}
    	
    	switch(direction) {
		case 0://right
			lookx = lookx + stepCount;
			break;
		case 1://upper right
			lookx = lookx + stepCount;
			looky = looky + stepCount;
			break;
		case 2://up
			looky = looky + stepCount;
			break;
		case 3://upper left
			lookx = lookx - stepCount;
			looky = looky + stepCount;
			break;
		case 4://left
			lookx = lookx - stepCount;
			break;
		case 5://down left
			lookx = lookx - stepCount;
			looky = looky - stepCount;
			break;
		case 6://down
			looky = looky - stepCount;	
			break;
		case 7://down right
			lookx = lookx + stepCount;
			looky = looky - stepCount;
			break;
		default:
			System.out.println("invalid direction");
			break;	
	}
    	
    	Critter temp = new Clover();
    	temp.setPosition(lookx, looky);
    	
    	for(Critter c: population) {
    		if(comparePos(c, temp)) return c.toString();
    	}
    	
        return null;
    }
    
    /**
     * 	runStats - default runStats method for all critters, will 
     * 			   print out how many of that critter there are
     * @param critters - a list of critters to run stats on
     * @return
     */
    public static String runStats(List<Critter> critters) {
    	String output = "" + critters.size() + " critters as follows -- " + '\n';
		Map<String, Integer> critter_count = new HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			critter_count.put(crit_string, critter_count.getOrDefault(crit_string, 0) + 1);
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			output += prefix + s + ":" + critter_count.get(s);
			prefix = ", ";
		}
		
		return output;
    }

    /**
     * 	displayWorld - taked in our display object and converts our
     * 				   data into a gui
     * @param pane	- the object the world will be displayed on
     */
    public static void displayWorld(Object pane) {
		try{
			if(pane instanceof GridPane){
				GridPane grid = (GridPane)pane;
				grid.getChildren().clear();
				grid.setStyle("-fx-background-color: silver;");

				for(int row = 0; row < Params.WORLD_HEIGHT; row++){
					for(int col = 0; col < Params.WORLD_WIDTH; col++){
						Pane temp = new Pane();
						temp.setStyle("-fx-border-color: white");
						GridPane.setConstraints(temp, col, row, 1,1);
						grid.add(temp, col, row);
						}
				}


				for (int i = 0; i<population.size(); i++){
					Critter critter = population.get(i);
					grid.add(getIcon(grid, critter.viewShape(), critter.viewFillColor(), critter.viewOutlineColor()), critter.getXPos(), critter.getYPos());

					Shape icon = getIcon(grid, critter.viewShape(), critter.viewFillColor(), critter.viewOutlineColor());
					grid.add(icon, critter.getXPos(), critter.getYPos());
					GridPane.setConstraints(icon, critter.getXPos(), critter.getYPos(), 1,1);
					//grid.setAlignment(Pos.CENTER);

				}

			} else{
				System.out.println("Wrong pane object");
			}
		}catch(Exception e){
			e.printStackTrace();
		}

    }

	/* END --- NEW FOR PROJECT 5
			rest is unchanged from Project 4 */

    private int energy = 0;

    private int x_coord;
    private int y_coord;
    private boolean hasMoved;
    private boolean fighting;

    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();

    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
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
     * create and initialize a Critter subclass.
     * critter_class_name must be the qualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name) throws InvalidCritterException {
    	try {
    		Class<?> critterClass = Class.forName("assignment5." + critter_class_name);
    		Critter newCritter = (Critter) critterClass.newInstance();
    		newCritter.setPosition(getRandomInt(Params.WORLD_WIDTH), getRandomInt(Params.WORLD_HEIGHT));
    		newCritter.setCritterEnergy(Params.START_ENERGY);
    		newCritter.setMoved(false);
    		newCritter.setFight(false);
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
     * @param critter_class_name What kind of Critter is to be listed.
     *                           Unqualified class name.
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
			Class<?> critterClass = Class.forName(myPackage + "." + critter_class_name);
			try {
				tempCritter = critterClass.newInstance();
			}
			catch(InstantiationException e) {
				System.out.println("Invalid Command: instantiation exception " + critter_class_name);
				return null;
			}
			catch(IllegalAccessException e) {
				System.out.println("Invalid Command: illegal access exception" + critter_class_name);
				return null;
			}
			
		} catch(ClassNotFoundException e){
			System.out.println("Invalid Command: class not found " + critter_class_name);
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

    }

    public static void worldTimeStep() {
    	for (int i = 0; i < population.size(); i++) {
			population.get(i).doTimeStep();
    	}
		
		for(Critter critter1: population) {
			for(Critter critter2: population) {
				if(critter1 != critter2 && comparePos(critter1, critter2) && critter1.getEnergy()>0 && critter2.getEnergy()>0) {
					critter1.setFight(true);
					critter2.setFight(true);
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
		}
		babies.clear();
		for(Critter critter: population) critter.setMoved(false);
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String opponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    protected final void walk(int direction) {
    	if (!(this.hasMoved)) {
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
    		if (this.x_coord >= Params.WORLD_WIDTH){
    			this.x_coord = 0;
    		}
    		
    		if (this.x_coord < 0){
    			this.x_coord = Params.WORLD_WIDTH-1;
    		}
    		
    		if (this.y_coord >= Params.WORLD_HEIGHT){
    			this.y_coord = 0;
    		}
    	
    		if (this.y_coord < 0){
    			this.y_coord = Params.WORLD_HEIGHT-1;
    		}
    		
    		this.setMoved(true);
    	}
    }

    protected final void run(int direction) {
    	if (!(this.hasMoved)) {
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
    		if (this.x_coord > Params.WORLD_WIDTH){//goes 2 steps off right
    			this.x_coord = 1;
    		}
			if (this.x_coord == Params.WORLD_WIDTH){//goes 1 step off right
				this.x_coord = 0;
			}
    		if (this.x_coord == -2){//goes 2 steps off left
    			this.x_coord = Params.WORLD_WIDTH-2;
    		}
    		if (this.x_coord == -1){//goes 1 step off left
    			this.x_coord = Params.WORLD_WIDTH-1;
    		}
			if (this.y_coord > Params.WORLD_HEIGHT){//goes 2 steps off bottom
				this.y_coord = 1;
			}
			if (this.y_coord == Params.WORLD_HEIGHT){//goes 1 step off bottom
				this.y_coord = 0;
			}
			if (this.y_coord == -2){//goes 2 steps off top
				this.y_coord = Params.WORLD_HEIGHT-2;
			}
			if (this.y_coord == -1){//goes 1 step off top
				this.y_coord = Params.WORLD_HEIGHT-1;
			}
    		
    		this.setMoved(true);
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

    public static Shape getIcon(GridPane grid, CritterShape inputShape, javafx.scene.paint.Color fill, javafx.scene.paint.Color Outline){
		Shape shape = null;
		double cell_width = grid.getWidth()/Params.WORLD_WIDTH-2;
		double cell_height = grid.getHeight()/Params.WORLD_HEIGHT-2;
		double centerX = grid.getWidth()/Params.WORLD_WIDTH/2;
		double centerY = grid.getHeight()/Params.WORLD_HEIGHT/2;
		//double cell_min = 5;
		switch (inputShape) {
			case CIRCLE:
				shape = new Circle();
				((Circle)shape).setRadius(cell_width/2);
				((Circle)shape).setCenterX(centerX);
				((Circle)shape).setCenterY(centerY);
				shape.setFill(fill);
				shape.setStroke(Outline);
				break;
			case SQUARE:
				shape = new Rectangle();
				((Rectangle)shape).setWidth(cell_width);
				((Rectangle)shape).setHeight(cell_height);
				((Rectangle)shape).setX(2.0);
				((Rectangle)shape).setY(2.0);

				shape.setFill(fill);
				shape.setStroke(Outline);
				break;
			case TRIANGLE:
				shape = new Polygon();
				((Polygon)shape).getPoints().addAll(new Double[]{
						2.0, cell_height,//bottomleft corner
						cell_width, cell_height,//botton right corner
						centerX, 2.0//top vertex

				});
				((Polygon)shape).scaleXProperty().bind(grid.widthProperty().divide(Params.WORLD_WIDTH).divide(grid.getWidth()/Params.WORLD_WIDTH));
				((Polygon)shape).scaleYProperty().bind(grid.heightProperty().divide(Params.WORLD_HEIGHT).divide(grid.getHeight()/Params.WORLD_HEIGHT));
				shape.setFill(fill);
				shape.setStroke(Outline);
				break;
			case DIAMOND:
				shape = new Polygon();
				((Polygon)shape).getPoints().addAll(new Double[]{
						centerX, 2.0,//top vertex
						cell_width, centerY,//right vertex
						centerX, cell_height,//bottom vertex
						2.0, centerY});

				shape.setFill(fill);
				shape.setStroke(Outline);
				break;
			case STAR:
				shape = new Polygon();
				((Polygon)shape).getPoints().addAll(new Double[]{
						centerX, 2.0,//top point
						grid.getWidth()/Params.WORLD_WIDTH*2/3, grid.getHeight()/Params.WORLD_HEIGHT/3,//inside vertex
						cell_width, grid.getHeight()/Params.WORLD_HEIGHT/3,//right point
						grid.getWidth()/Params.WORLD_WIDTH*3/4, grid.getHeight()/Params.WORLD_HEIGHT*3/5,//inside vertex
						cell_width, cell_height,//bottom right point
						centerX, grid.getHeight()/Params.WORLD_HEIGHT*3/4,//inside vertex
						2.0, cell_height,//bottom left point
						grid.getWidth()/Params.WORLD_WIDTH/4,grid.getHeight()/Params.WORLD_HEIGHT*3/5, //inside vertex
						2.0, grid.getHeight()/Params.WORLD_HEIGHT/3,//left point
						grid.getWidth()/Params.WORLD_WIDTH/3, grid.getHeight()/Params.WORLD_HEIGHT/3//inside vertex
				});
				((Polygon)shape).scaleXProperty().bind(grid.widthProperty().divide(Params.WORLD_WIDTH).divide(grid.getWidth()/Params.WORLD_WIDTH));
				((Polygon)shape).scaleYProperty().bind(grid.heightProperty().divide(Params.WORLD_HEIGHT).divide(grid.getHeight()/Params.WORLD_HEIGHT));
				shape.setFill(fill);
				shape.setStroke(Outline);
				break;
		}
		return shape;
	}


    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
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
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
