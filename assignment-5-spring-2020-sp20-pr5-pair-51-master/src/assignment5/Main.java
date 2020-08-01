/* CRITTERS GUI <MyClass.java>
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

/*
   Describe here known bugs or issues in this file. If your issue spans multiple
   files, or you are not sure about details, add comments to the README.txt file.
 */
package assignment5;


import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {
	private static String myPackage;

	private TextArea statsText = new TextArea();
	public static GridPane world = new GridPane();
	private int animationSpeed = 0;
	private static int pixelSize;
	//error notifications
	private Label createError = new Label();
	private Label randomizerError = new Label();
	private Label timeStepError = new Label();
	private Label animateStatus = new Label();
	private Label animateError = new Label();
	private static ArrayList<String> validCritters = new ArrayList<String>();
	private int timeStep=0;
	private List<Node> nodes = new ArrayList<Node>();
	public static int screenHeight;
	public static int screenWidth;
	public static int worldHeight, worldWidth;
	public static int controlHeight, controlWidth;
	public static int statsHeight, statsWidth;


	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
		pixelSize = Math.max(Params.WORLD_HEIGHT, Params.WORLD_WIDTH);
		if(pixelSize > 70) pixelSize = 70;
		GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenWidth = screen.getDisplayMode().getWidth();
		screenHeight = screen.getDisplayMode().getHeight();
		screenWidth = (9*screenWidth)/10;
		screenHeight = (9*screenHeight)/10;
		worldHeight = screenHeight/3;
		worldWidth = screenWidth/3;
		int divide = Math.max(worldHeight, worldWidth);
		pixelSize = divide/pixelSize;
		for(int i = 0; i < Params.WORLD_WIDTH; i++) world.getColumnConstraints().add(new ColumnConstraints(pixelSize));
		for(int i = 0; i < Params.WORLD_HEIGHT; i++) world.getRowConstraints().add(new RowConstraints(pixelSize));
	}


	public static void main(String[] args) {
		launch(args);
	}

	
	public void start(Stage stage) throws Exception {
		stage.setTitle("Critters");
		Critter.displayWorld(world);
		Class[] critterClasses;
		try {
			critterClasses = generateClassList(myPackage);
			for(int i = 0; i < critterClasses.length; i++) {
				if((Critter.class.isAssignableFrom(critterClasses[i])) && (!Modifier.isAbstract(critterClasses[i].getModifiers()))) {
					validCritters.add(critterClasses[i].getName().substring(myPackage.length() + 1));
				}
			}
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		GridPane grid = new GridPane();
		GridPane.setConstraints(createError, 1, 8, 2, 1);
		GridPane.setConstraints(timeStepError, 1, 10, 3, 1);
		GridPane.setConstraints(randomizerError, 6, 8, 3, 1);

		grid.add(createError, 1, 8);
		grid.add(randomizerError, 6, 8);
		grid.add(timeStepError, 1, 10);
		grid.add(animateStatus, 1, 14);
		grid.add(animateError, 1, 12);

		Label title = new Label("Control Panel");
		GridPane.setConstraints(title, 0, 0, 4, 4);

		nodes.add(title);

		Label addCritter = new Label("Add a Critter: ");
		GridPane.setConstraints(addCritter, 0, 7);

		nodes.add(addCritter);

		ChoiceBox<String> critters = new ChoiceBox<>();
		critters.getItems().add(null);
		critters.getItems().addAll(validCritters);
		GridPane.setConstraints(critters, 2, 7);

		nodes.add(critters);

		TextField number = new TextField();
		number.setPromptText("Num of Critters");
		GridPane.setConstraints(number, 1, 7);

		nodes.add(number);

		Button makeCritter = new Button("Make Critters!");
		makeCritter.setOnAction(e -> critterCreate(critters, number));
		makeCritter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setConstraints(makeCritter, 3, 7);

		nodes.add(makeCritter);

		Label setSeed = new Label("Set Random Seed: ");
		GridPane.setConstraints(setSeed, 5, 7);

		nodes.add(setSeed);

		TextField randomSeed = new TextField();
		randomSeed.setPromptText("Random Seed");
		GridPane.setConstraints(randomSeed, 6, 7);

		nodes.add(randomSeed);

		Button performSeed = new Button("Randomize!");
		performSeed.setOnAction(e -> seedRequest(randomSeed));
		performSeed.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setConstraints(performSeed, 7, 7);

		nodes.add(performSeed);

		Label runStats = new Label("Show Stats: ");
		GridPane.setConstraints(runStats, 5, 9);

		nodes.add(runStats);

		CheckBox[] classes = new CheckBox[validCritters.size()];
		for (int i = 0; i < validCritters.size(); i++) {
			classes[i] = new CheckBox(validCritters.get(i));
			grid.add(classes[i], 6, i + 9);
		}

		Label timeSteps = new Label("Perform Time Steps:");
		GridPane.setConstraints(timeSteps, 0, 9);

		nodes.add(timeSteps);

		TextField number2 = new TextField();
		number2.setPromptText("Num of Time Steps");
		GridPane.setConstraints(number2, 1, 9);

		nodes.add(number2);

		Label numSteps = new Label("Time Step: " + timeStep);
		grid.add(numSteps, 0, 10);

		nodes.add(numSteps);

		Button performTime = new Button("Time Step");
		performTime.setOnAction(e -> timeStepRequest(number2, classes, numSteps));
		performTime.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setConstraints(performTime, 3, 9);

		nodes.add(performTime);

		Button reset = new Button("Reset World");
		reset.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				clearNotifications();
				Critter.clearWorld();
				Critter.displayWorld(world);
				timeStep = 0;
				numSteps.setText("Time Step: " + timeStep);

			}
		});
		
		reset.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setConstraints(reset, 3, 21);

		nodes.add(reset);


		Label speed = new Label("Animate:");
		GridPane.setConstraints(speed, 0, 11);

		nodes.add(speed);

		ChoiceBox<String> speedChoice = new ChoiceBox<>();
		speedChoice.getItems().addAll(null, "Slow", "Normal", "Fast", "SuperFast");
		GridPane.setConstraints(speedChoice, 1, 11);

		nodes.add(speedChoice);

		AnimationTimer timer = new AnimationTimer() {
			
			public void handle(long now) {

				makeCritter.setDisable(true);
				performTime.setDisable(true);
				performSeed.setDisable(true);
				reset.setDisable(true);
				speedChoice.setDisable(true);
				critters.setDisable(true);
				for(int i = 0; i < classes.length; i++) classes[i].setDisable(true);

				long startTime = System.nanoTime();

				Critter.worldTimeStep();
				Critter.displayWorld(world);
				timeStep++;
				numSteps.setText("Time Step: " + timeStep);
				runStatsRequest(classes);
				
				while (System.nanoTime() - startTime < animationSpeed) {}
			}
		};

		Button stop = new Button("        Stop       ");
		stop.setDisable(true);
		GridPane.setConstraints(stop, 3, 12, 2, 1);

		nodes.add(stop);

		stop.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				clearNotifications();
				timer.stop();
				stop.setDisable(true);
				makeCritter.setDisable(false);
				performTime.setDisable(false);
				performSeed.setDisable(false);
				reset.setDisable(false);
				speedChoice.setDisable(false);
				critters.setDisable(false);
				for(int i = 0; i < classes.length; i++) classes[i].setDisable(false);
				animateError.setText("");
			}
		});

		Button start = new Button("        Start       ");
		GridPane.setConstraints(start, 3, 11, 2, 1);

		nodes.add(start);

		start.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				clearNotifications();
				if (speedChoice.getValue()!=null) {
					animateStatus.setText("Animating");
					animateStatus.setTextFill(javafx.scene.paint.Color.BLUE);
					// disable other buttons on control panel
					stop.setDisable(false);
					makeCritter.setDisable(true);
					performTime.setDisable(true);
					performSeed.setDisable(true);
					reset.setDisable(true);

					String chosen = speedChoice.getValue();
					if (chosen.equals("Slow")) animationSpeed = 1000000000;
					else if (chosen.equals("Normal")) animationSpeed = 500000000;
					else if (chosen.equals("Fast")) animationSpeed = 100000000;
					else if (chosen.contentEquals("SuperFast")) animationSpeed = 300000000;
					timer.start();
				}
				else {
					animateError.setText("Speed Not Set");
					animateError.setTextFill(javafx.scene.paint.Color.RED);
				}
			}
		});

		// "Exit Critters" function
		Button exit = new Button("Exit Critters");

		nodes.add(exit);

		exit.setOnAction(new EventHandler <ActionEvent>() {
			
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		exit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setConstraints(exit, 3, 20, 2, 1);

		// add all nodes to the control panel
		grid.getChildren().addAll(title, addCritter, critters, number, makeCritter,
				timeSteps, number2, performTime, runStats, setSeed, randomSeed, performSeed,
				speed, speedChoice, start, stop, reset, exit);

		Label worldTitle = new Label();
		Label statsTitle = new Label();
		worldTitle.setText("World Grid");
		statsTitle.setText("Statistics");

		GridPane screenGrid = new GridPane();
		//world.setPrefHeight(screenGrid.getHeight()/2);
		//world.setPrefWidth(screenGrid.getWidth()/2);
		screenGrid.getChildren().addAll(grid, world, statsText, worldTitle, statsTitle);
		GridPane.setConstraints(worldTitle, 0, 0, 1, 1);
		GridPane.setConstraints(world, 0, 1, 4, 6);
		GridPane.setConstraints(statsTitle, 5, 7, 1, 1);
		GridPane.setConstraints(grid, 0, 8, 5, 3);
		GridPane.setConstraints(statsText, 5, 8, 5, 2);

		Scene scene = new Scene(screenGrid, screenWidth, screenHeight);
		stage.setScene(scene);
		stage.show();
	}

	/////////////////////////////////////////////////////////////////////////////////
	//                                                                             //
	//                                                                             //
	//                              our methods below                              //
	//                                                                             //
	//                                                                             //

	/////////////////////////////////////////////////////////////////////////////////	
	private void critterCreate(ChoiceBox<String> critters, TextField createInput) {

		clearNotifications();

		if(critters.getValue() == null) {
			createError.setText("Invalid Input: No Critter Selected");
			createError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}

		if(createInput.getText() == null || createInput.getText().isEmpty()) {
			createError.setText("Invalid Input: createInput cannot be empty");
			createError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}

		int num;

		try {
			num = Integer.parseInt(createInput.getText());
		} catch (Exception e) {
			createError.setText("Invalid Input: " + createInput.getText() + " is not a valid input");
			createError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}
		
		if(num > Params.WORLD_HEIGHT*Params.WORLD_WIDTH) {
			createError.setText("Invalid Input: Cannot create that many critters");
			createError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}

		if(num <= 0) {
			createError.setText("Invalid Input: please enter numbers greater than 0");
			createError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		}

		// create Critters of selected type and quantity
		while (num > 0) {
			try {
				Critter.createCritter(critters.getValue());
				createError.setText("Made " + createInput.getText() + " " + (critters.getValue()) + "(s)");
				createError.setTextFill(javafx.scene.paint.Color.GREEN);
			} catch (Exception e) {
				createError.setText("Error: problem occured in creating critters");
				createError.setTextFill(javafx.scene.paint.Color.RED);
				return;
			}
			num--;
		}

		Critter.displayWorld(world);

	}

	private void timeStepRequest(TextField timeStepInput, CheckBox[] classes, Label numTimeSteps) {

		clearNotifications();

		if (timeStepInput.getText() == null || timeStepInput.getText().isEmpty()) {
			timeStepError.setText("Ran 1 Time Step");
			timeStepError.setTextFill(javafx.scene.paint.Color.GREEN);
			Critter.worldTimeStep();
			timeStep++;
			numTimeSteps.setText("Time Step: " + timeStep);
			Critter.displayWorld(world);
		} else {

			int timeStepNum;

			try {
				timeStepNum = Integer.parseInt(timeStepInput.getText());
			}
			catch (Exception e) {
				timeStepError.setText("Invalid Input: " + timeStepInput.getText() + " is not valid");
				timeStepError.setTextFill(javafx.scene.paint.Color.RED);
				return;
			}

			if (timeStepNum <= 0) {
				timeStepError.setText("Invalid Input: please enter numbers greater than 0");
				timeStepError.setTextFill(javafx.scene.paint.Color.RED);
				return;
			}

			timeStepError.setText("Ran " + timeStepNum + " Time Steps");
			timeStepError.setTextFill(javafx.scene.paint.Color.GREEN);

			// perform time steps
			for(int i = 0; i < timeStepNum; i++) {
				Critter.worldTimeStep();
				timeStep++;
				numTimeSteps.setText("Time Step: " + timeStep);
			}

			Critter.displayWorld(world);
		}

		runStatsRequest(classes);
	}

	private void runStatsRequest(CheckBox[] classes) {
		boolean selected = false;
		clearNotifications();
		statsText.clear();
		statsText.appendText("===================================" + "\n");
		
		for (int i = 0; i < classes.length; i++) {

			String critterName = validCritters.get(i);

			if (classes[i].isSelected()) {
				selected = true;
				List<Critter> stats;

				try {
					stats = Critter.getInstances(critterName);
				} catch (InvalidCritterException e) {
					e.printStackTrace();
					return;
				}	
				
				try {
					Class<?> critterClass = Class.forName(myPackage + "." + critterName);
					Object ob = critterClass.newInstance();
					Method statsMethod = ob.getClass().getMethod("runStats", List.class);
					statsText.clear();
					statsText.appendText(statsMethod.invoke(null, stats) + "\n");
				} catch (NoSuchMethodException e) {
					//if it uses default critter runStats
					statsText.appendText(Critter.runStats(stats) + "\n");
				} catch (Exception e) {
				}
			}
		} if(!selected) {
			//if no critter is selected run all critter stats
			List<Critter> population;
			try {
				population = Critter.getInstances("Critter");
				statsText.appendText(Critter.runStats(population) + "\n");
			} catch (InvalidCritterException e) {
				e.printStackTrace();
				return;
			}
		}
		statsText.appendText("===================================" + "\n");
	}


	
	private void seedRequest(TextField seedInput) {
		
		if (seedInput.getText() == null || seedInput.getText().isEmpty()) {
			randomizerError.setText("Invalid Input: Please enter a seed");
		}

		if (seedInput.getText() == null || seedInput.getText().isEmpty()) {
			randomizerError.setText("Invalid Input:");
			randomizerError.setTextFill(javafx.scene.paint.Color.RED);
			return;
		} else {

			int seed;

			try{
				seed = Integer.parseInt(seedInput.getText());
			} catch (Exception e) {
				randomizerError.setText("Invalid Input: " + seedInput.getText() + " is not a valid input");
				randomizerError.setTextFill(javafx.scene.paint.Color.RED);
				return;
				}

			Critter.setSeed(seed);
			randomizerError.setText("Seed: " + seed);
			randomizerError.setTextFill(javafx.scene.paint.Color.GREEN);
			}
		}

	private void clearNotifications() {
		createError.setText("");
		randomizerError.setText("");
		timeStepError.setText("");
		animateStatus.setText("");
		animateError.setText("");
	}

	/**
	 *  generateClassList - this method was adapted from an online source on 
	 *  					stackExchange, it creates an array of all classes
	 *  					included in the program that extend Critter
	 * @param thisPackage - current project package
	 * @return	- array of all critter classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] generateClassList(String thisPackage) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader != null) {
			String path = thisPackage.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();

			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}

			ArrayList<Class> classes = new ArrayList<Class>();

			for (File directory : dirs) {
				classes.addAll(getClassList(directory, thisPackage));
			}

			Class[] returnArray = classes.toArray(new Class[classes.size()]);

			return returnArray;
		}

		return null;
    }
    /**
     * 	getClassList - this method is also adapted from a stackExchange method
     * 				   it brings together all of the classes in this package for
     * 				   generateClassList to work
     * @param directory	- current directory of the package
     * @param thisPackage - name of current package
     * @return
     * @throws ClassNotFoundException
     */
	private static List<Class> getClassList(File directory, String thisPackage) throws ClassNotFoundException {


		List<Class> classes = new ArrayList<Class>();

		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();

		for (File file : files) {

			if (file.isDirectory()) {
				if(!file.getName().contains(".")) classes.addAll(getClassList(file, thisPackage + "." + file.getName()));
			}
			else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(thisPackage + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}

		return classes;
	}


	/////////////////////////////////////////////////////////////////////////////////
	//                                                                             //
	//                                                                             //
	//                              our methods above                              //
	//                                                                             //
	//                                                                             //
	/////////////////////////////////////////////////////////////////////////////////

}