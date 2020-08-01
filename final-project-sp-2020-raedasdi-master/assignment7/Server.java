package assignment7;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 	A server for an Auction House that holds items where clients can bid or
 * 	buy items outright
 * @author Raed
 *
 */
public class Server {
	private static ArrayList<Item> items;
	private static Map<String, ClientObserver> userObservers;
	private static Map<String, ArrayList<Item>> itemList;
	private static final String UnitSeparator = Character.toString((char) 31);
	private static final String groupSeparator = Character.toString((char) 29);
	private static String fileName;
	private static String userFileName;
	private static String itemListFile;
	private static String saleItemFile;
	private boolean addedNewItems = false;
	private ServerSocket serverSock;
	private static final String USER_HEADER = "^^^";
	private static final String PASS_SEPARATOR = "***";

	/**
	 * 	Server - creates files for information storage
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Server() throws FileNotFoundException, IOException {
		userObservers = new HashMap<String, ClientObserver>();
		itemList = new HashMap<String, ArrayList<Item>>();
		items = new ArrayList<Item>();
		
		fileName = new File(".").getCanonicalPath() + "\\AuctionHouse";
		itemListFile = fileName + "\\itemList.txt";
		saleItemFile = fileName + "\\itemSaleList.txt";
		userFileName = fileName + "\\users.txt";
		
		// Create one directory
		new File(fileName).mkdir();

		File yourFile = new File(userFileName);

		//create username/password file if it doesn't exist
		try {
			yourFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File itemFile = new File(itemListFile);
		
		//create user/item file if it doesn't exist
		try {
			itemFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File saleFile = new File(saleItemFile);
		
		//create items for sale file if it doesn't exist
		try {
			saleFile.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		//populate items for sale
		Scanner inFile = new Scanner(new FileReader(saleItemFile));
		while(inFile.hasNext()) {
			String line = inFile.nextLine();
			items.add(Item.parseString(line));
		}
		inFile.close();
		
		Scanner inFile2 = new Scanner(new FileReader(itemListFile));
		while(inFile2.hasNext()) {
			String[] line = inFile2.nextLine().split(UnitSeparator);
			ArrayList<Item> userItems = new ArrayList<Item>();
			String[] items = line[1].split(groupSeparator);
			for(int i = 0; i < items.length; i++) {
				userItems.add(Item.parseString(items[i]));
			}
			itemList.put(line[0], userItems);
		}
		inFile2.close();
		
		
	}

	/**
	 * 	setUpNetWorking - opens socket 4342 as a seceratary socket for all client needs
	 * @throws Exception
	 */
	public void setUpNetworking() throws Exception {
		this.serverSock = new ServerSocket(4242); 
		while (true) { 
			Socket clientSocket = serverSock.accept();
			System.out.println("Received connection " + clientSocket);
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket, writer)); 
			t.start(); 
		}
	}

	/**
	 * 	ClientHandler - class that allows for server to receive messages from clients and respond
	 * 					accordingly
	 * @author Raed
	 *
	 */
	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private ClientObserver writer;
		private Socket sock;

		public ClientHandler(Socket clientSocket, ClientObserver writer) {
			this.sock = clientSocket;

			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream())); 
				this.writer = writer;
			} 
			catch (IOException e) { 
				e.printStackTrace();
			}
		}
		
		/**
		 * 	updateSaleFile - updates Sale item file when last user logs off server
		 */
		public void updateSaleFile() {
			try {
				PrintWriter outgoing = new PrintWriter(new FileWriter(saleItemFile, false));
				for(Item i: items) {
					outgoing.println(i.toString());
				}
				outgoing.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		/**
		 * 	updateInventoryFile - updates User inventory database
		 * @param user			- username
		 */
		public void updateInventoryFile(String user) {
			String out = user + UnitSeparator;
			if(itemList.get(user) != null && !itemList.get(user).isEmpty() && itemList.containsKey(user)) {
					for(int i = 0; i < itemList.get(user).size(); i++) {
						if (i == 0) out += itemList.get(user).get(i).toString();
						else out += groupSeparator + itemList.get(user).get(i).toString();
					}
				}
			
			try {
				boolean userFound = false;
				PrintWriter outgoing = new PrintWriter(new FileWriter(itemListFile, false));
				Scanner inFile = new Scanner(new FileReader(itemListFile));
				while(inFile.hasNext()) {
					String line = inFile.nextLine();
					if(line.contains(user)) {
						userFound = true;
						out = user + UnitSeparator;
						if(!itemList.get(user).isEmpty()) {
							for(int i = 0; i < itemList.get(user).size(); i++) {
								if (i == 0) out += itemList.get(user).get(i).toString();
								else out += groupSeparator + itemList.get(user).get(i).toString();
							}
							outgoing.println(out);
						}
					}else {
						String[] existingUser = line.split(UnitSeparator);
						String[] existingItems = existingUser[1].split(groupSeparator);
						ArrayList<Item> existingItemList = new ArrayList<Item>();
						for(int i = 0; i < existingItems.length; i++) {
							existingItemList.add(Item.parseString(existingItems[i]));
						}
						
						if(itemList.containsKey(existingUser[0]) && !itemList.get(existingUser[0]).equals(existingItemList)) {
							existingItemList = itemList.get(existingUser[0]);
							line = existingUser[0] + UnitSeparator;
							for(int i = 0; i < existingItemList.size(); i++) {
								if(i == 0) line += existingItemList.get(i);
								else line += groupSeparator + existingItemList.get(i);
							}
							outgoing.println(line);
						} else {
							outgoing.println(line);
						}
						
					}
				}
				if(!userFound) outgoing.println(out);
				inFile.close();
				outgoing.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * 	run - Handler for incoming messages from client
		 */
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					String[] array = message.split(UnitSeparator);

						//////////////////////////////////////////////////////////////
						//						NEWUSER								//
						//////////////////////////////////////////////////////////////
						if(array[0].equals("NEWUSER")) {
							String user = array[1];
							String pwd = array[2];
							
							//check username file and password to see if user already exists in server database
							boolean userExists = false;
							Scanner inFile = new Scanner(new FileReader(userFileName));
								while(inFile.hasNext()) {
									String line = inFile.nextLine();
									if (line.toUpperCase().contains(((USER_HEADER) + user).toUpperCase()) && line.contains(PASS_SEPARATOR + pwd)) {
										userExists =  true;
									}
								}
								inFile.close();

							// if user is online or exists in database
							if(userObservers.containsKey(user) || userExists) {
								userObservers.put("ERRORNAME", this.writer);
								String error = "USEREXISTS" + UnitSeparator + "ERRORNAME" + UnitSeparator +  "ERRORNAME";
								String[] tempArray = error.split(UnitSeparator);
								AuctionHouse temp = new AuctionHouse();
								temp.addObservers(tempArray);
								temp.sendClientMessage(error);
								userObservers.remove("ERRORNAME");
							}
							else {
								userObservers.put(user, this.writer);
								itemList.put(user, new ArrayList<Item>());
								PrintWriter out = new PrintWriter(new FileWriter(userFileName, true));
								out.println(USER_HEADER + user + PASS_SEPARATOR + pwd);
								out.close();
							}


						}

						//////////////////////////////////////////////////////////////
						//						LOGIN								//
						//////////////////////////////////////////////////////////////
						
						else if(array[0].equals("LOGIN")) {


							String user = array[1];
							String pwd = array[2];
							
							//check if user is online
							if(userObservers.containsKey(user)) {
								userObservers.put("ERRORNAME", this.writer);
								String error = "ALREADYLOGGEDIN" + UnitSeparator + "ERRORNAME" + UnitSeparator +  "ERRORNAME";
								String[] tempArray = error.split(UnitSeparator);
								AuctionHouse temp = new AuctionHouse();
								temp.addObservers(tempArray);
								temp.sendClientMessage(error);
								userObservers.remove("ERRORNAME");

							}
							else {
								boolean passCorrect = false;
								Scanner inFile = new Scanner(new FileReader(userFileName));
									while(inFile.hasNext()) {
										String line = inFile.nextLine();
										if (line.toUpperCase().contains(((USER_HEADER) + user).toUpperCase()) && line.contains(PASS_SEPARATOR + pwd)) {
											passCorrect =  true;
										}
									}
									inFile.close();
							
								//check is password is correct
								if(passCorrect) {
									userObservers.put(user, this.writer);
								} else {
									userObservers.put("ERRORNAME", this.writer);
									String error = "WRONGPASS" + UnitSeparator + "ERRORNAME" + UnitSeparator +  "ERRORNAME";
									String[] tempArray = error.split(UnitSeparator);
									AuctionHouse temp = new AuctionHouse();
									temp.addObservers(tempArray);
									temp.sendClientMessage(error);
									userObservers.remove("ERRORNAME");
								}
							}
						}

						//////////////////////////////////////////////////////////////
						//						REFRESH								//
						//////////////////////////////////////////////////////////////
						else if(array[0].equals("REFRESH")) {
							if(addedNewItems) {
								addedNewItems = false;
								String itemMessage = "";

								for(Item item: items) {
									itemMessage += item.toString() + groupSeparator;
								}

								itemMessage = ("REFRESH" + UnitSeparator + array[1] + UnitSeparator + itemMessage);

								AuctionHouse temp = new AuctionHouse();
								
								String names = "";

								// return back REFRESH string with user names separated by groupSeparator
								names = ("REFRESH" + UnitSeparator + array[1]);

								// use chat room observer pattern to send message
								String[] tempUserArray = names.split(UnitSeparator);
								
								temp.addObservers(tempUserArray);
								temp.sendClientMessage(itemMessage);
							} else {
								String itemMessage = "";

								// create String with all online user names
								for(Item item: items) {
									itemMessage += item.toString() + groupSeparator;
								}

								// return back REFRESH string with user names separated by groupSeparator
								itemMessage = ("REFRESH" + UnitSeparator + array[1] + UnitSeparator + itemMessage);

								// use chat room observer pattern to send message
								//String[] tempArray = itemMessage.split(UnitSeparator);
								AuctionHouse temp = new AuctionHouse();
							
								String names = "";

								// return back REFRESH string with user names separated by groupSeparator
								names = ("REFRESH" + UnitSeparator + array[1]);

								// use chat room observer pattern to send message
								String[] tempUserArray = names.split(UnitSeparator);
							
								temp.addObservers(tempUserArray);
								temp.sendClientMessage(itemMessage);
							}
						}
						
						//////////////////////////////////////////////////////////////
						//							SELL							//
						//////////////////////////////////////////////////////////////
						
						else if(array[0].equals("SELL")) {
							String[] itemsToSell = array[1].split(groupSeparator);
							
							for(int i = 0; i < itemsToSell.length; i++) {
								items.add(Item.parseString(itemsToSell[i]));
							}
							
							addedNewItems = true;
							
						}
						
						//////////////////////////////////////////////////////////////
						//							BID								//
						//////////////////////////////////////////////////////////////
					
						else if(array[0].equals("BID")) {
							String bidClient = array[1];
							int bidAmt = Integer.parseInt(array[2]);
							String itemInfo = array[3];
							Item itemBid = Item.parseString(itemInfo);
							int index = 0;
							boolean itemFound = false;
							boolean sameOwner = false;
							String clientMessage;
							String[] notifyUser = new String[2];
							AuctionHouse temp = new AuctionHouse();
							notifyUser[1] = bidClient;
							temp.addObservers(notifyUser);
							
							for(Item i: items) {
								if(i.isEqual(itemBid.getName(), itemBid.getOwner())) {
									index = items.indexOf(i);
									itemBid = i;
									if (bidClient.equals(i.getOwner())) sameOwner = true;
									else itemFound = true;
								}
							}
							
							if(itemFound) {
								if(items.get(index).bidSuccessful(bidAmt)) {
									//bid is successful and item is sold
									if(items.get(index).targetMet()) {
										items.remove(index);
										if(itemList.containsKey(itemBid.getOwner())) {	
											String ownerMessage = "ITEMSOLD" + UnitSeparator + itemBid.toString() + UnitSeparator + bidClient;
											if(userObservers.containsKey(itemBid.getOwner())) {	
												AuctionHouse owner = new AuctionHouse();
												String[] tempUser = new String[2];
												tempUser[1] = itemBid.getOwner();
												owner.addObservers(tempUser);
												owner.sendClientMessage(ownerMessage);
											}
											for(int i = 0; i < itemList.get(itemBid.getOwner()).size(); i++){
												if(itemList.get(itemBid.getOwner()).get(i).getName().equals(itemBid.getName())) {
													itemList.get(itemBid.getOwner()).remove(i);
												}
											}
										}
										itemBid.setOwner(bidClient);
										itemList.get(bidClient).add(itemBid);
										clientMessage = "BIDWON" + UnitSeparator + itemBid.toString();
										temp.sendClientMessage(clientMessage);
										
									} 
									//bid is successful, target not met
									else {
										String ownerMessage = "ITEMBIDSUCCESS" + UnitSeparator + itemBid.toString();
										if(userObservers.containsKey(itemBid.getOwner())) {	
											AuctionHouse owner = new AuctionHouse();
											String[] tempUser = new String[2];
											tempUser[1] = itemBid.getOwner();
											owner.addObservers(tempUser);
											owner.sendClientMessage(ownerMessage);
										}
										clientMessage = "BIDSUCCESS" + UnitSeparator + bidAmt + UnitSeparator + itemBid.getName();
										temp.sendClientMessage(clientMessage);
										if(itemList.containsKey(itemBid.getOwner())) {
											itemList.get(itemBid.getOwner()).remove(Item.parseString(itemInfo));
											itemList.get(itemBid.getOwner()).add(itemBid);
										}
									}
								} 
								//bid is unsuccessful because bid was not high enough
								else {
									clientMessage = "BIDUNSUCCESS" + UnitSeparator + itemBid.getCurrentBid() + UnitSeparator + itemBid.getName();
									temp.sendClientMessage(clientMessage);
								}
							} 
							//bidder owns item
							else if(sameOwner) {
								clientMessage = "SAMEOWNER" + UnitSeparator + itemBid.getName();
								temp.sendClientMessage(clientMessage);
							} 
							//item does not exist
							else {
								clientMessage = "ITEMDOESNTEXIST" + UnitSeparator + itemBid.getName();
								temp.sendClientMessage(clientMessage);
							}
						}
						
						//////////////////////////////////////////////////////////////
						//						BUY NOW								//
						//////////////////////////////////////////////////////////////
						
						else if(array[0].equals("BUYNOW")) {
							String bidClient = array[1];
							int bidAmt = Integer.parseInt(array[2]);
							String itemInfo = array[3];
							Item itemBid = Item.parseString(itemInfo);
							int index = 0;
							boolean itemFound = false;
							boolean sameOwner = false;
							String clientMessage;
							String[] notifyUser = new String[2];
							AuctionHouse temp = new AuctionHouse();
							notifyUser[1] = bidClient;
							temp.addObservers(notifyUser);
							
							for(Item i: items) {
								if(i.isEqual(itemBid.getName(), itemBid.getOwner()) ) {
									index = items.indexOf(i);
									itemBid = i;
									if (bidClient.equals(i.getOwner())) sameOwner = true;
									else itemFound = true;
								}
							}
							
							if(itemFound) {
								if(items.get(index).buyNowMet(bidAmt)) {
									items.remove(index);
									if(itemList.containsKey(itemBid.getOwner())) {	
										String ownerMessage = "ITEMSOLD" + UnitSeparator + itemBid.toString() + UnitSeparator + bidClient;
										if(userObservers.containsKey(itemBid.getOwner())) {	
											AuctionHouse owner = new AuctionHouse();
											String[] tempUser = new String[2];
											tempUser[1] = itemBid.getOwner();
											owner.addObservers(tempUser);
											owner.sendClientMessage(ownerMessage);
										}
										for(int i = 0; i < itemList.get(itemBid.getOwner()).size(); i++){
											if(itemList.get(itemBid.getOwner()).get(i).getName().equals(itemBid.getName())) {
												itemList.get(itemBid.getOwner()).remove(i);
											}
										}
									}
									
									itemBid.setOwner(bidClient);
									ArrayList<Item> ownerItems = new ArrayList<Item>();
									if(itemList.containsKey(itemBid.getOwner())) ownerItems = itemList.get(itemBid.getOwner());
									ownerItems.add(itemBid);
									itemList.put(itemBid.getOwner(), ownerItems);
									clientMessage = "BIDWON" + UnitSeparator + itemBid.toString();
									temp.sendClientMessage(clientMessage);
									
								}
							} else if(sameOwner) {
								clientMessage = "SAMEOWNER" + UnitSeparator + itemBid.getName();
								temp.sendClientMessage(clientMessage);
							} else {
								clientMessage = "ITEMDOESNTEXIST" + UnitSeparator + itemBid.getName();
								temp.sendClientMessage(clientMessage);
							}
						}
						
						//////////////////////////////////////////////////////////////
						//						USERITEMUPDATE						//
						//////////////////////////////////////////////////////////////
						
						else if(array[0].equals("USERITEMUPDATE")) {
							String[] items = array[1].split(groupSeparator);
							String user = array[2];
							ArrayList<Item> userItems = new ArrayList<Item>();
							
							if(!items[0].equals("")) {	
								for(int i = 0; i < items.length; i++) {
									userItems.add(Item.parseString(items[i]));
								}
							}
							itemList.put(user, userItems);
							
							PrintWriter out = new PrintWriter(new FileWriter(itemListFile, true));
							String output = user + UnitSeparator;
							for(Item i: userItems) {
								output += i.toString() + groupSeparator;
							}
							out.println(output);
							out.close();
						}
						
						//////////////////////////////////////////////////////////////
						//						USERREQUESTITEM						//
						//////////////////////////////////////////////////////////////
						
						else if(array[0].equals("USERREQUESTITEM")) {
							String user = array[1];
							String[] itemString = new String[1];
							ArrayList<Item> inFileItems = new ArrayList<Item>();
							Scanner inFile = new Scanner(new FileReader(itemListFile));
							
							//check to see if user has items in their name
							boolean userFound = false;
							while(inFile.hasNext()) {
								String line = inFile.nextLine();
								if (line.contains(user)) {
									String[] temp = line.split(UnitSeparator);
									if(temp.length>=1) {
									String[] itemStringtemp = temp[1].split(groupSeparator);
									itemString = new String[itemStringtemp.length];
									itemString = itemStringtemp;
									userFound = true;
									}
								}
							}
							
							if(userFound) {
								for(int i = 0; i < itemString.length; i++) {
								inFileItems.add(Item.parseString(itemString[i]));
								}
								itemList.put(user, inFileItems);
							}
							
							String itemMessage = "USERITEMS" + UnitSeparator;
							if(itemList.get(user) == null || itemList.get(user).isEmpty()) {
								itemMessage = "NOITEMS";
							} else {
								for(int i = 0; i < itemList.get(user).size(); i++) {
									itemMessage += itemList.get(user).get(i).toString();
									if(i != itemList.get(user).size()-1) itemMessage += groupSeparator;
								}
							}
							AuctionHouse temp = new AuctionHouse();
							String[] userArray = new String[2];
							userArray[1] = user;
							temp.addObservers(userArray);
							temp.sendClientMessage(itemMessage);
							inFile.close();
						}

						//////////////////////////////////////////////////////////////
						//						LOGOUT								//
						//////////////////////////////////////////////////////////////
						else if(array[0].equals("LOGOUT")) {
							updateInventoryFile(array[1]);
							userObservers.remove(array[1]);
							//if no more users on server, update the sale file
							if(userObservers.isEmpty()) {
								updateSaleFile();
							}
						}
					}
				} catch (IOException e) {

					e.printStackTrace();

				}
			}
			
		}
	
		/**
		 * 	AuctionHouse - The observable class that allows us to communicate
		 * 				   from server to client using the Observer Design Patter
		 * @author Raed
		 *
		 */
		class AuctionHouse extends Observable {
			public List<String> users = new ArrayList<String>();
			public List<Item> itemList = new ArrayList<Item>();
			
			/**
			 * 	addObservers - adds input of users to observer list for AuctionHouse message
			 * @param array - input of usernames, array[1] has user and array[2] contains
			 * 				  any other users who need to be notified
			 */
			public void addObservers(String[] array) {
				if(userObservers.containsKey(array[1])) {
					addObserver(userObservers.get(array[1]));
					users.add(array[1]);
				}
				if(array.length > 2){
					String[] otherUsers = array[2].split(groupSeparator);
					for(int i = 0; i < otherUsers.length; i++) {
						if(userObservers.containsKey(otherUsers[i])) {
							addObserver(userObservers.get(otherUsers[i]));
							users.add(otherUsers[i]);
						}
					}
				}
			}

			/**
			 * 	sendClientMessage - send a message to the Incoming Readers of all observers
			 * @param message - a message, formatted to be accepted by users to be sent
			 */
			public void sendClientMessage(String message) {
				setChanged();
				notifyObservers(message);
				
			}
		}

		public static void main(String[] args) {
			try {
				new Server().setUpNetworking();
			} catch (Exception e) { e.printStackTrace(); }


		}
	}