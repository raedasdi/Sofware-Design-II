package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ListIterator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {

	private AuctionClient client;
	private Socket clientSocket;
	private BufferedReader reader;
	private PrintWriter writer;
	private static final String UnitSeparator = Character.toString((char) 31);
	private static final String groupSeparator = Character.toString((char) 29);
	private static final String FORMATTED_SPACER = "                 ";
	private Stage clientStage = null;
	private VBox clientVBoxTop;
	private VBox clientVBoxBottom;
	private Scene realScene;
	private ToggleGroup itemsForSale;
	private ArrayList<RadioButton> itemButtons;
	private CheckBox[] clientInventory;
	private ScrollPane scroll;
	private String font = "Courier";
	private String buttonColor = "#6183bc";
	private static String localIPAddress;
	private static String BOLD = "-fx-font-weight: bold";

	/**
	 * 	Begins intial client GUI setup
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		InetAddress localHost = InetAddress.getLocalHost();
		localIPAddress = localHost.getHostAddress();

		clientStage = primaryStage;
		clientVBoxTop = new VBox();
		clientVBoxTop.setPadding(new Insets(10, 40, 40, 40));
		clientVBoxTop.setSpacing(10);
		primaryStage.setTitle("Client");

		Label title = new Label("RBay");
		title.setTextFill(Color.DARKCYAN);
		title.setStyle(BOLD);
		title.setFont(Font.font(font, 30));

		Label ipInstruction = new Label("Enter IP Address:");
		TextField ip = new TextField();
		ip.setPromptText("IP Address");

		Label error = new Label();
		
		//IP of local server
		Button localIP = new Button("Local Server");
		localIP.setStyle("-fx-base: " + buttonColor + ";");
		localIP.setAccessibleHelp("Use this button for local server hosting");
		localIP.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
					try {
						setUpNetworking(localIPAddress);
						userLogin();
					} catch (UnknownHostException e) {
						error.setText(localIPAddress + " is an invalid IP, reenter.");
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		});
		
		// User-Input IP tp connect to
		Button enterIP = new Button("Connect");
		enterIP.setStyle("-fx-base: " + buttonColor + ";");
		enterIP.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if(!ip.getText().equals("")) {
					try {
						setUpNetworking(ip.getText());
						userLogin();
					} catch (UnknownHostException e) {
						error.setText("Invalid IP, reenter.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		//allows option for enterKey to be pressed
		ip.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER){ 
					if(!ip.getText().equals("")) {
						try {
							setUpNetworking(ip.getText());
							userLogin();
						} catch (UnknownHostException e) {
							error.setText("Invalid IP, reenter.");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		clientVBoxTop.getChildren().addAll(title, ipInstruction, ip, localIP, enterIP, error);
		Scene scene = new Scene(clientVBoxTop, 300, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 	userLogin - GUI for the login screen
	 */
	public void userLogin() {
 
		clientVBoxTop.getChildren().clear();

		clientVBoxTop = new VBox();
		clientVBoxTop.setPadding(new Insets(10, 40, 40, 40));
		clientVBoxTop.setSpacing(10);

		Label title = new Label("Welcome Client");
		title.setTextFill(Color.DARKCYAN);
		title.setStyle(BOLD);
		title.setFont(Font.font(font, 30));

		Label userNameInput = new Label("Please enter a username:");
		TextField username = new TextField();
		username.setPromptText("Username");

		Label passwordInput = new Label("Please enter a password:");
		TextField password = new TextField();
		password.setPromptText("Password");

		Button createAcc = new Button("Create Account");
		createAcc.setStyle("-fx-base: " + buttonColor + ";");
		createAcc.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String user = username.getText();
				String pwd = password.getText();

				if (!user.equals("") && !pwd.equals("")) {
					client = new AuctionClient(user, pwd);

					// Server New User Request
					writer.println("NEWUSER" + UnitSeparator + user + UnitSeparator + pwd);
					writer.flush();
					
					// Server items request (if user has any stored on server)
					writer.println("USERREQUESTITEM" + UnitSeparator + client.getName());
					writer.flush();
					
					// Server item refresh request
					writer.println("REFRESH" + UnitSeparator + client.getName());
					writer.flush();
				}	
			}

		});

		Button login = new Button("Login");
		login.setStyle("-fx-base: " + buttonColor + ";");
		login.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String user = username.getText();
				String pwd = password.getText();

				if (!user.equals("") && !pwd.equals("")) {
					client = new AuctionClient(user, pwd);

					// Server Login request
					writer.println("LOGIN" + UnitSeparator + user + UnitSeparator + pwd);
					writer.flush();
					
					// Server items request (if user has any stored on server)
					writer.println("USERREQUESTITEM" + UnitSeparator + client.getName());
					writer.flush();
					
					// Server item refresh request
					writer.println("REFRESH" + UnitSeparator + client.getName());
					writer.flush();

				}	
			}

		});

		clientVBoxTop.getChildren().addAll(title, userNameInput, username, passwordInput, password, createAcc, login);
		Scene scene = new Scene(clientVBoxTop, 350, 300);
		clientStage.setScene(scene);
		clientStage.show();
	}

	/**
	 * 	setUpNetWorking - this function connects the client 
	 * 					  to the IP address provided
	 * @param IP is a String that holds the IP address entered by the user
	 * @throws Exception
	 */
	private void setUpNetworking(String IP) throws Exception {

		this.clientSocket = new Socket(IP, 4242);
		InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
		reader = new BufferedReader(streamReader);
		
		try
		{
			writer = new PrintWriter(clientSocket.getOutputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
	}

	/**
	 * 	userExists - sends request to Server and checks if current username is taken 
	 * @param serverMessage is not used in this function
	 */
	public void doesUserExist(String[] serverMessage) { 


		for(ListIterator<Node> iterator = clientVBoxTop.getChildren().listIterator(); iterator.hasNext();) {
			Node currentNode = iterator.next();
			if (currentNode instanceof Label && ((Label)currentNode).getText().contains("ERROR")) {
				iterator.remove();
			}
		}

		String error = "ERROR: Username already exists. Enter another username.";
		Label notif = new Label();
		notif.setText(error);
		notif.setWrapText(true);
		clientVBoxTop.getChildren().addAll(notif);


	}

	/**
	 * 	isUserOnline - Checks with server to see if user is online 
	 * @param serverMessage  - is not used
	 */
	public void isUserOnline(String[] serverMessage) {

		for(ListIterator<Node> iterator = clientVBoxTop.getChildren().listIterator(); iterator.hasNext();) {
			Node currentNode = iterator.next();
			if (currentNode instanceof Label && ((Label)currentNode).getText().contains("ERROR")) {
				iterator.remove();
			}
		}

		String error = "ERROR: User already logged in. Enter another username.";
		Label notif = new Label();
		notif.setText(error);
		notif.setWrapText(true);
		clientVBoxTop.getChildren().addAll(notif);

	}

	/**
	 * 	isPassCorrect - Authenticates username and password with Server Data
	 * @param serverMessage - is not used
	 */
	public void isPassCorrect(String[] serverMessage) { 

		for(ListIterator<Node> iterator = clientVBoxTop.getChildren().listIterator(); iterator.hasNext();) {
			Node currentNode = iterator.next();
			if (currentNode instanceof Label && ((Label)currentNode).getText().contains("ERROR")) {
				iterator.remove();
			}
		}

		String error = "ERROR: Invalid Password. Please Retry.";
		Label notif = new Label();
		notif.setText(error);
		notif.setWrapText(true);
		clientVBoxTop.getChildren().addAll(notif);


	}

	/**
	 * 	launchClientGUI - main function that is responsible for launching the client GUI
	 * @param available - list of items available on auction
	 */
	public void launchClientGUI(String[] available) {
		// close login screen
		double x = clientStage.getX();
		double y = clientStage.getY();
		clientStage.close();

		clientStage = new Stage();
		clientStage.setTitle("RBay");
		clientStage.setX(x);
		clientStage.setY(y);

		Label spacer1 = new Label("");
		Label spacer2 = new Label("");
		Label spacer3 = new Label("");

		clientVBoxTop = new VBox();
		clientVBoxTop.setPadding(new Insets(10, 40, 0, 40));
		clientVBoxTop.setSpacing(5);
		clientVBoxBottom = new VBox();
		clientVBoxBottom.setPadding(new Insets(0, 10, 40, 10));
		clientVBoxBottom.setSpacing(2);

		Label welcome = new Label("Welcome, " + client.getName() + "!");
		welcome.setTextFill(Color.DARKCYAN);
		welcome.setStyle(BOLD);
		welcome.setFont(Font.font(font, 40));

		Label itemsLabel = new Label("Items For Sale");
		Label legend = new Label("     Item Name     Current Bid     Buy Now    Starting Bid    Target Bid      Owner");

		clientVBoxTop.getChildren().addAll(welcome, spacer1, itemsLabel, legend);
		
		itemButtons = new ArrayList<RadioButton>();
		
		if(available[0] != "NOITEMS") {
			itemsForSale = new ToggleGroup();
			if(available.length >= 1) {
				for(int i = 0; i < available.length; i++) {
					Item tempItem = Item.parseString(available[i]);
					String formattedString;
					if(tempItem.getBuyNowPrice() != -1) formattedString = tempItem.getName() + FORMATTED_SPACER + tempItem.getCurrentBid() + FORMATTED_SPACER + tempItem.getBuyNowPrice() + FORMATTED_SPACER + tempItem.getStartingBid() + FORMATTED_SPACER + tempItem.getTargetBid() + FORMATTED_SPACER + tempItem.getOwner();
					else formattedString = tempItem.getName() + FORMATTED_SPACER + tempItem.getCurrentBid() + FORMATTED_SPACER + "N/A" + FORMATTED_SPACER + tempItem.getStartingBid() + FORMATTED_SPACER + tempItem.getTargetBid() + FORMATTED_SPACER + tempItem.getOwner();
					RadioButton temp = new RadioButton(formattedString);
					temp.setToggleGroup(itemsForSale);
					itemButtons.add(temp);
					clientVBoxTop.getChildren().add(temp);
				}
			}
		}
		//////////////////////////////////////////////////////////////
		//						BIDDING								//
		//////////////////////////////////////////////////////////////
		
		Label bidAmount = new Label("Bid Amount");
		TextField bidAmt = new TextField("");
		bidAmt.setPromptText("Enter Bid Amount");
		
		Button bid = new Button("Bid");
		bid.setStyle("-fx-base: " + buttonColor + ";");
		bid.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				if(bidAmt.getText().equals("")) {
					String error = "ERROR: Please enter a bid amount.";
					Label notif = new Label();
					notif.setText(error);
					notif.setWrapText(true);
					clientVBoxBottom.getChildren().addAll(notif);
					return;
				}
				
				String names = "";
				for(RadioButton item: itemButtons) {
					if (item.isSelected()) {
						Item tempItem = Item.parseFormattedString(item.getText());
						names = tempItem.toString();
					}
						
				}

				if (names.equals("")) {
					return;
				}

				// Request Server to check if Bid is successful
				String serverMessage = "BID" + UnitSeparator + client.getName() + UnitSeparator + bidAmt.getText() + UnitSeparator + names;
				writer.println(serverMessage);
				writer.flush();

			}});
		
		//////////////////////////////////////////////////////////////
		//						BUY NOW								//
		//////////////////////////////////////////////////////////////
		
		Label buyNowLabel = new Label("Error in buying item");
		buyNowLabel.setVisible(false);
		
		Button buyNow = new Button("Buy Now");
		buyNow.setStyle("-fx-base: " + buttonColor + ";");
		buyNow.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				buyNowLabel.setVisible(false);
				
				int itemAmt = 0;
				
				String name = "";
				
				for(RadioButton item: itemButtons) {
					if (item.isSelected()) {
						Item tempItem = Item.parseFormattedString(item.getText());
						name = tempItem.toString();
						itemAmt = tempItem.getBuyNowPrice();
					}
				}

				if (name.equals("")) {
					buyNowLabel.setVisible(true);
					return;
				}
				
				//Request Server to see if Buy Now was successful
				String serverMessage = "BUYNOW" + UnitSeparator + client.getName() + UnitSeparator + itemAmt + UnitSeparator + name;
				writer.println(serverMessage);
				writer.flush();
			}});
		
		//////////////////////////////////////////////////////////////
		//						CREATE ITEM							//
		//////////////////////////////////////////////////////////////
		
		Button newItem = new Button("Add an Item");
		newItem.setStyle("-fx-base: " + buttonColor + ";");
		newItem.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unused")
			@Override
			public void handle(ActionEvent event) {
				NewItemWindow newWindow = new NewItemWindow(available);
			}
		});
		
		//////////////////////////////////////////////////////////////
		//						EDIT ITEM							//
		//////////////////////////////////////////////////////////////
		
		Label editError = new Label();
		editError.setVisible(false);
		Button editItem = new Button("Edit an Item");
		editItem.setStyle("-fx-base: " + buttonColor + ";");
		editItem.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unused")
			@Override
			public void handle(ActionEvent event) {
				Item edit = new Item();
				boolean itemSelected = false;
				boolean error = false;
				editError.setVisible(false);
				
				for(int i = 0; i < clientInventory.length; i++) {
						if(clientInventory[i].isSelected()) {
							if(itemSelected || clientInventory[i].getText().equals("Please Add an Item")) {
								error = true;
							} else if(!error) {
								edit = Item.parseFormattedString(clientInventory[i].getText());
								itemSelected = true;
							}
						}
				}
				if(error) {
					editError.setText("ERROR: You can only edit one item!");
					editError.setVisible(true);
				} else if(!itemSelected) {
					editError.setText("ERROR: Please select an item to edit");
					editError.setVisible(true);
				} else {
					EditItemWindow editWindow = new EditItemWindow(available, edit);
				}
			}
		});

		//////////////////////////////////////////////////////////////
		//						LOG OUT								//
		//////////////////////////////////////////////////////////////

		Button logOut = new Button("Log Out");
		logOut.setStyle("-fx-base: " + buttonColor + ";");
		logOut.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				writer.println("LOGOUT" + UnitSeparator + client.getName());
				writer.flush();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				writer.println("REFRESH" + UnitSeparator + client.getName());
				writer.flush();

				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clientStage.close();
				exit();
			}
		});
		
		if(!client.getItems().isEmpty()) {
			clientInventory = new CheckBox[client.getItems().size()];
			for(int i = 0; i < client.getItems().size(); i++) {
				Item temp = client.getItems().get(i);
				if(temp.getBuyNowPrice() == -1) clientInventory[i] = new CheckBox(temp.getName() + FORMATTED_SPACER + temp.getCurrentBid() + FORMATTED_SPACER + "N/A" + FORMATTED_SPACER + temp.getStartingBid() + FORMATTED_SPACER + temp.getTargetBid() + FORMATTED_SPACER + temp.getOwner() + FORMATTED_SPACER + temp.getHighestBidder()); 
				else clientInventory[i] = new CheckBox(temp.getName() + FORMATTED_SPACER + temp.getCurrentBid() + FORMATTED_SPACER + temp.getBuyNowPrice() + FORMATTED_SPACER + temp.getStartingBid() + FORMATTED_SPACER + temp.getTargetBid() + FORMATTED_SPACER + temp.getOwner() + FORMATTED_SPACER + temp.getHighestBidder());
			}
		}
		else {
			clientInventory = new CheckBox[1];
			clientInventory[0] = new CheckBox("Please Add an Item");
		}
		
		Label sellNotif = new Label("");	
		sellNotif.setVisible(false);
		
		//////////////////////////////////////////////////////////////
		//						SELL								//
		//////////////////////////////////////////////////////////////
		
		Button sell = new Button("Sell");
		sell.setStyle("-fx-base: " + buttonColor + ";");
		sell.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String itemSell = "";
				sellNotif.setVisible(false);
				boolean itemExists = false;
				if(!clientInventory[0].getText().equals("Please Add an Item")) {
					for(int i = 0; i < clientInventory.length; i++) {
						if(clientInventory[i].isSelected()) {
							itemExists = false;
							for(RadioButton j: itemButtons) {
								Item forSale = Item.parseFormattedString(clientInventory[i].getText());
								if(j.getText().contains(forSale.getName()) && j.getText().contains(forSale.getOwner())) itemExists = true;
							}
							Item tempItem = Item.parseFormattedString(clientInventory[i].getText());
							if(itemSell.equals("") && !itemExists) itemSell += tempItem.toString();
							else if(!itemExists) itemSell += groupSeparator + tempItem.toString();
						}
					}
				}
				
				if(!itemSell.equals("")) {
					writer.println("SELL" + UnitSeparator + itemSell);
					writer.flush();
					
					writer.println("REFRESH" + UnitSeparator + client.getName());
					writer.flush();
				} else if(itemExists) {
					sellNotif.setText("You cannot sell items that are already for sale");
					sellNotif.setVisible(true);;
					
				}else {
					sellNotif.setText("Please select a valid item to sell");
					sellNotif.setVisible(true);
				}
			}
		});	
		
		GridPane pane = new GridPane();
		pane.getRowConstraints().add(new RowConstraints(380));
		pane.getRowConstraints().add(new RowConstraints(10));
		pane.getColumnConstraints().add(new ColumnConstraints(580));
		pane.getColumnConstraints().add(new ColumnConstraints(30));
		
		Label invLabel = new Label("Inventory");
		Label invLegend = new Label("       Item Name | Current Bid | Buy Now | Starting Bid | Target | Owner");		
		
		scroll = new ScrollPane();
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		pane.add(scroll, 0, 0);
		
		int row = 0;
		GridPane inventory = new GridPane();
		for(CheckBox i: clientInventory) {
			inventory.add(i, 0, row);
			row++;
		}
		
		Button refresh = new Button("Refresh");
		refresh.setStyle("-fx-base: " + buttonColor + ";");
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				writer.println("REFRESH" + UnitSeparator + client.getName());
				writer.flush();
			}
		});
		
		scroll.setContent(inventory);
		
		VBox tempVBox = new VBox();
		tempVBox.setPadding(new Insets(0, 20, 10, 20));
		tempVBox.setSpacing(2);
		
		tempVBox.getChildren().addAll(invLabel, invLegend, pane);
		clientVBoxBottom.getChildren().addAll(bidAmount, bidAmt, bid, buyNow, buyNowLabel, spacer2, spacer3,  sellNotif, sell, editError, editItem, newItem, refresh, logOut);
		
		GridPane secondaryPane = new GridPane();
		
		secondaryPane.add(clientVBoxBottom, 0, 0, 2, 1);
		secondaryPane.add(tempVBox, 3, 0, 2, 1);
		
		GridPane mainPane = new GridPane();
		mainPane.setPadding(new Insets(10, 30, 10, 30));
		mainPane.getRowConstraints().add(new RowConstraints(450));
		mainPane.getRowConstraints().add(new RowConstraints(10));
		mainPane.getColumnConstraints().add(new ColumnConstraints(780));
		mainPane.getColumnConstraints().add(new ColumnConstraints(30));
		
		mainPane.add(clientVBoxTop, 0, 0, 3, 1);
		mainPane.add(secondaryPane, 0, 1, 3, 1);
		
		ScrollPane mainScroll = new ScrollPane();
		
		GridPane scenePane = new GridPane();
		scenePane.getRowConstraints().add(new RowConstraints(780));
		scenePane.getRowConstraints().add(new RowConstraints(0));
		scenePane.getColumnConstraints().add(new ColumnConstraints(800));
		scenePane.getColumnConstraints().add(new ColumnConstraints(0));
		
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scenePane.add(mainScroll, 0, 0);
		
		mainScroll.setContent(mainPane);
		
		realScene = new Scene(scenePane, 800, 800);
		
		// if user closes client window, it automatically logs them out
		clientStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {

				writer.println("LOGOUT" + UnitSeparator + client.getName());
				writer.flush();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				
				writer.println("REFRESH" + UnitSeparator + client.getName());
				writer.flush();

				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clientStage.close();
				exit();
			}


		});
		
		clientStage.setScene(realScene);
		clientStage.show();
	}

	// closes the socket
	public void exit() {
		System.exit(0);
	}
	
	class EditItemWindow extends Application{
		private Stage editItemStage;
		private VBox editItem;
		private String itemName;
		private Integer bidPrice;
		private Integer buyNowPrice;
		private Integer targetBid;
		private boolean errorMade = false;
		private Label error;
		
		public EditItemWindow(String[] serverMessage, Item editItemObj) {
			editItemStage = new Stage();
			editItemStage.setTitle("Edit Item");
			
			itemName = editItemObj.getName();
			bidPrice = editItemObj.getStartingBid();
			targetBid = editItemObj.getTargetBid();
			buyNowPrice = editItemObj.getBuyNowPrice();
				
			editItem = new VBox();
			
			error = new Label();
			error.setVisible(false);
				
			Label name = new Label("Item Name");
			TextField enterName = new TextField("");
			enterName.setText(itemName);
			
			Label bid = new Label("Starting Bid");
			TextField enterBid = new TextField("");
			enterBid.setText(bidPrice.toString());
			
			Label buyNow = new Label("Buy Now Price");
			TextField enterBuyNow = new TextField("");
			enterBuyNow.setText(buyNowPrice.toString());
			
			Label targetPrice = new Label("Target Bid");
			TextField enterTargetPrice = new TextField("");
			enterTargetPrice.setText(targetBid.toString());
			
			Button create = new Button("Done");
			create.setStyle("-fx-base: " + buttonColor + ";");
			create.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					error.setVisible(false);
					errorMade = false;
					String enteredName = enterName.getText();
					String enteredBid = enterBid.getText();
					String enteredBuyNow = enterBuyNow.getText();
					String enteredTarget = enterTargetPrice.getText();
					
					try {
						bidPrice = Integer.parseInt(enteredBid);
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for Current bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					try {
						targetBid = Integer.parseInt(enteredTarget);
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for Target bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					try {
						 if(!enteredBuyNow.equals("")) buyNowPrice = Integer.parseInt(enteredBuyNow);
						 else buyNowPrice = -1;
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for buy now price!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(Integer.parseInt(enteredTarget) <= Integer.parseInt(enteredBid)) {
						error.setText("ERROR: Cannot have current bid be more than target bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(enteredName.equals("")) {
						error.setText("ERROR: Please enter a name!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(enteredName.contains("/")) {
						error.setText("ERROR: Name cannot contain / !");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(!errorMade) {
						itemName = enteredName;
						if(enteredBid.equals("")) bidPrice = 0;
						else bidPrice = Integer.parseInt(enteredBid);
						if(enteredBuyNow.equals("")) buyNowPrice = -1;
						else buyNowPrice = Integer.parseInt(enteredBuyNow);
						targetBid = Integer.parseInt(enteredTarget);
						Item editItem = Item.parseString(itemName + "/" + bidPrice + "/" + buyNowPrice + "/" + bidPrice + "/" + targetBid + "/" + client.getName());
						client.replaceItem(editItemObj, editItem);
						launchClientGUI(serverMessage);
						String serverMessage = "USERITEMUPDATE" + UnitSeparator;
						for(int i = 0; i < client.getItems().size(); i++) {
							serverMessage += client.getItems().get(i).toString();
							if(i != client.getItems().size() - 1) serverMessage += groupSeparator;
						}
						serverMessage += UnitSeparator + client.getName();
						writer.println(serverMessage);
						writer.flush();
						editItemStage.close();
					}
				}
			});
			
			editItem.getChildren().addAll(name, enterName, bid, enterBid, buyNow, enterBuyNow, targetPrice, enterTargetPrice, create, error);
			Scene scene = new Scene(editItem, 300, 500);
			editItemStage.setScene(scene);
			editItemStage.show();
				
		}
		
		//not needed since contructor starts it anyways
		@Override
		public void start(Stage primaryStage) throws Exception {
			
		}
	}
	
	/**
	 * 	NewItemWindow - GUI for creating new items to add to client inventory
	 * @author Raed
	 *
	 */
	class NewItemWindow extends Application{
		private Stage newItemStage;
		private VBox newItem;
		private String itemName;
		private int bidPrice;
		private int buyNowPrice;
		private int targetBid;
		private boolean created = false;
		private boolean errorMade = false;
		private Label error;
		
		public NewItemWindow(String[] serverMessage) {
			newItemStage = new Stage();
			newItemStage.setTitle("Create New Item");
				
			newItem = new VBox();
			
			error = new Label();
			error.setVisible(false);
				
			Label name = new Label("Item Name");
			TextField enterName = new TextField("");
			enterName.setPromptText("Enter Item Name");
			
			Label bid = new Label("Starting Bid");
			TextField enterBid = new TextField("");
			enterBid.setPromptText("Enter Starting Bid Amount");
			
			Label buyNow = new Label("Buy Now Price");
			TextField enterBuyNow = new TextField("");
			enterBuyNow.setPromptText("Enter Buy Now Price");
			
			Label targetPrice = new Label("Target Bid");
			TextField enterTargetPrice = new TextField("");
			enterTargetPrice.setPromptText("Enter Target Bid");
			
			Button create = new Button("Create Item");
			create.setStyle("-fx-base: " + buttonColor + ";");
			create.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					error.setVisible(false);
					errorMade = false;
					String enteredName = enterName.getText();
					String enteredBid = enterBid.getText();
					String enteredBuyNow = enterBuyNow.getText();
					String enteredTarget = enterTargetPrice.getText();
					
					targetBid = -1;
					bidPrice = 0;
					
					try {
						bidPrice = Integer.parseInt(enteredBid);
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for Current bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					try {
						targetBid = Integer.parseInt(enteredTarget);
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for Target bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					try {
						 if(!enteredBuyNow.equals("")) buyNowPrice = Integer.parseInt(enteredBuyNow);
						 else buyNowPrice = -1;
					} catch (Exception e) {
						error.setText("ERROR: Please enter a valid input for buy now price!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(targetBid <= 0 || bidPrice <= 0 || buyNowPrice == 0 || buyNowPrice < -1) {
						error.setText("ERROR: Cannot have 0 or negative Prices!");
						error.setVisible(true);
						errorMade = true;
					} else if(targetBid <= bidPrice) {
						error.setText("ERROR: Cannot have current bid be more than target bid!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(enteredName.equals("")) {
						error.setText("ERROR: Please enter a name!");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(enteredName.contains("/")) {
						error.setText("ERROR: Name cannot contain / !");
						error.setVisible(true);
						errorMade = true;
					}
					
					if(!errorMade) {
						itemName = enteredName;
						if(enteredBid.equals("")) bidPrice = 0;
						else bidPrice = Integer.parseInt(enteredBid);
						if(enteredBuyNow.equals("")) buyNowPrice = -1;
						else buyNowPrice = Integer.parseInt(enteredBuyNow);
						targetBid = Integer.parseInt(enteredTarget);
						created = true;
						Item newItem = Item.parseString(itemName + "/" + bidPrice + "/" + buyNowPrice + "/" + bidPrice + "/" + targetBid + "/" + client.getName());
						client.addItem(newItem);
						launchClientGUI(serverMessage);
						String serverMessage = "USERITEMUPDATE" + UnitSeparator;
						for(int i = 0; i < client.getItems().size(); i++) {
							serverMessage += client.getItems().get(i).toString();
							if(i != client.getItems().size() - 1) serverMessage += groupSeparator;
						}
						serverMessage += UnitSeparator + client.getName();
						writer.println(serverMessage);
						writer.flush();
						newItemStage.close();
					}
				}
			});
			
			newItem.getChildren().addAll(name, enterName, bid, enterBid, buyNow, enterBuyNow, targetPrice, enterTargetPrice, create, error);
			Scene scene = new Scene(newItem, 300, 500);
			newItemStage.setScene(scene);
			newItemStage.show();
				
		}
		
		//not needed since contructor starts it anyways
		@Override
		public void start(Stage primaryStage) throws Exception {
			
		}
		//checks to see if window has created item or not.
		public boolean isRunning() {
			return !created;
		}
	}
	
	/**
	 * 	BidNotif - Popup for when client gets a Bid notification
	 * @author Raed
	 *
	 */
	class BidNotif extends Application{
		private Stage notifStage = new Stage();;
		private VBox box;
		private String notif = "";
		
		public BidNotif(String[] input) {
			notifStage.setTitle("Bidding Notification for " + client.getName());;
			String code = input[0];
			if(code.equals("BIDSUCCESS")) {
				notif = "Successfully bid $" + input[1] + " on " + input[2]; 
			} 
			else if(code.equals("BIDUNSUCCESS")) {
				notif = "Bid unsuccessful, $" + input[1] + " or more required on " + input[2];
			} 
			else if(code.equals("ITEMDOESNTEXIST")){
				notif = input[1] + " does not exist on server";
			} 
			else if(code.equals("BIDWON")){
				Item temp = Item.parseString(input[1]);
				notif = "Congratulations! You have received " + temp.getName();
				client.addItem(temp);
				String serverMessage = "USERITEMUPDATE" + UnitSeparator;
				for(int i = 0; i < client.getItems().size(); i++) {
					serverMessage += client.getItems().get(i).toString();
					if(i != client.getItems().size() - 1) serverMessage += groupSeparator;
				}
				serverMessage += UnitSeparator + client.getName();
				writer.println(serverMessage);
				writer.flush();
			} else if(code.equals("SAMEOWNER")){
				notif = "You cannot bid on an item you own!";
			} else if (code.contentEquals("ITEMSOLD")){
				Item temp = Item.parseString(input[1]);
				int removeIndex = -1;
				for(Item i: client.getItems()) {
					if(i.getName().equals(temp.getName())) removeIndex = client.getItems().indexOf(i);
				}
				
				if(removeIndex != -1) client.getItems().remove(removeIndex);
				String winner = input[2];
				notif = "Congratulations! Your " + temp.getName() + " was sold to " + winner + ".";
				
				String serverMessage = "USERITEMUPDATE" + UnitSeparator;
				for(int i = 0; i < client.getItems().size(); i++) {
					serverMessage += client.getItems().get(i).toString();
					if(i != client.getItems().size() - 1) serverMessage += groupSeparator;
				}
				serverMessage += UnitSeparator + client.getName();
				writer.println(serverMessage);
				writer.flush();
			} else {
				notifStage.close();
			}
			
			Label textNotif = new Label(notif);
			textNotif.setStyle(BOLD);
			textNotif.setWrapText(true);
			
			Button ok = new Button("OK");
			ok.setStyle("-fx-base: " + buttonColor + ";");
			ok.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					writer.println("REFRESH" + UnitSeparator + client.getName());
					writer.flush();
					notifStage.close();
				}
			});
			
			box = new VBox();
			box.getChildren().addAll(textNotif, ok);
			GridPane grid = new GridPane();
			grid.getRowConstraints().add(new RowConstraints(250));
			grid.getRowConstraints().add(new RowConstraints(50));
			grid.getColumnConstraints().add(new ColumnConstraints(480));
			grid.getColumnConstraints().add(new ColumnConstraints(20));
			
			grid.add(textNotif, 0, 0);
			grid.add(ok, 0, 1);
			GridPane.setHalignment(textNotif, HPos.CENTER);
			GridPane.setHalignment(ok, HPos.CENTER);
			
			Scene scene = new Scene(grid, 500, 300);
			notifStage.setScene(scene);
			notifStage.show();
			
		}
		
		//not needed since constructor starts it anyways
		@Override
		public void start(Stage primaryStage) throws Exception {
			
		}
		
	}

	/**
	 * 	IncomingReader - Reader for server messages and requests from server
	 * 					 allows client to interact with server
	 * @author Raed
	 *
	 */
	class IncomingReader implements Runnable {

		@SuppressWarnings("unused")
		@Override
		public void run() {
			
			String incoming;
			
			try {
				while ((incoming = reader.readLine()) != null) {
					String[] serverMessage = incoming.split(UnitSeparator);

					//////////////////////////////////////////////////////////////
					//						REFRESH								//
					//////////////////////////////////////////////////////////////
					
					if (serverMessage[0].equals("REFRESH")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									launchClientGUI(serverMessage[2].split(groupSeparator));
								} catch(Exception e) {
									String[] itemsList = new String [1];
									itemsList[0] = "NOITEMS";
									launchClientGUI(itemsList);
								}
							}
						});
					}
					
					//////////////////////////////////////////////////////////////
					//						USEREXISTS							//
					//////////////////////////////////////////////////////////////
					else if (serverMessage[0].equals("USEREXISTS")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								doesUserExist(serverMessage);
							}
						});
					}
					
					//////////////////////////////////////////////////////////////
					//						USERITEMS							//
					//////////////////////////////////////////////////////////////
					else if(serverMessage[0].equals("USERITEMS")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								String[] items = serverMessage[1].split(groupSeparator);
								
								if(items[0] != "NOITEMS") {
									ArrayList<Item> itemList = new ArrayList<Item>();
									for(int i = 0; i < items.length; i++) {
										itemList.add(Item.parseString(items[i]));
									}
									client.setItems(itemList);
								}
							}
						});
					}
					
					//////////////////////////////////////////////////////////////
					//						BIDDING 							//
					//////////////////////////////////////////////////////////////
					else if(serverMessage[0].equals("BIDSUCCESS") || serverMessage[0].equals("BIDUNSUCCESS") 
							|| serverMessage[0].equals("ITEMDOESNTEXIST") || serverMessage[0].equals("BIDWON") 
							|| serverMessage[0].equals("SAMEOWNER") || serverMessage[0].equals("ITEMSOLD")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								BidNotif bidNotif = new BidNotif(serverMessage);
							}
						});
					}
					
					else if(serverMessage[0].equals("ITEMBIDSUCCESS")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Item bidItem = Item.parseString(serverMessage[1]);
								
								for(int i = 0; i < client.getItems().size(); i++) {
									if(client.getItems().get(i).getName().equals(bidItem.getName())) {
										client.getItems().get(i).setCurrentBid(bidItem.getCurrentBid());
									}
								}
							}
						});
					}
					
					//////////////////////////////////////////////////////////////
					//					  ALREADYLOGGEDIN						//
					//////////////////////////////////////////////////////////////
					else if(serverMessage[0].equals("ALREADYLOGGEDIN")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								isUserOnline(serverMessage);
							}
						});
					}

					//////////////////////////////////////////////////////////////
					//						WRONGPASS							//
					//////////////////////////////////////////////////////////////
					else if (serverMessage[0].equals("WRONGPASS")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								isPassCorrect(serverMessage);
							}
						});
					}
				}
			} catch (IOException ex) { 
				if(ex instanceof SocketException) {}
				else ex.printStackTrace(); 	}		
		}
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) { e.printStackTrace(); }

	}



}