package assignment7;

public class Item {
	private String name;
	private String owner;
	private int startingBid;
	private int buyNowPrice;
	private int currentBid;
	private int targetBid;
	private String highestBidder;
	private static final String FORMATTED_SPACER = "                 ";
	
	
	public Item() {
		this.name = "NULL";
		this.owner = "NULL";
		this.startingBid = 0;
		this.currentBid = 0;
		this.targetBid = 0;
		this.highestBidder = "NULL";
	}
	
	
	public Item(String name, int currentBid, int startingBid, int targetBid, String owner) {
		this.name = name;
		this.owner = owner;
		this.startingBid = startingBid;
		this.currentBid = currentBid;
		this.targetBid = targetBid;
		this.buyNowPrice = -1;
		this.highestBidder = "";
	}
	
	public Item(String name, int currentBid, int startingBid, int targetBid, String owner, String bidder) {
		this.name = name;
		this.owner = owner;
		this.startingBid = startingBid;
		this.currentBid = currentBid;
		this.targetBid = targetBid;
		this.buyNowPrice = -1;
		this.highestBidder = bidder;
	}
	
	public Item(String name, int currentBid, int buyNowPrice, int startingBid, int targetBid, String owner) {
		this.name = name;
		this.owner = owner;
		this.startingBid = startingBid;
		this.currentBid = currentBid;
		this.buyNowPrice = buyNowPrice;
		this.targetBid = targetBid;
		this.highestBidder = "";
	}
	
	public Item(String name, int currentBid, int buyNowPrice, int startingBid, int targetBid, String owner, String bidder) {
		this.name = name;
		this.owner = owner;
		this.startingBid = startingBid;
		this.currentBid = currentBid;
		this.buyNowPrice = buyNowPrice;
		this.targetBid = targetBid;
		this.highestBidder = bidder;
	}
	
	public boolean isEqual(String name, String owner) {
		return (this.name.equals(name) && this.owner.equals(owner));
	}
	
	public boolean bidSuccessful(int bid) {
		if(bid > this.currentBid) {
			currentBid = bid;
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean targetMet() {
		return currentBid >= targetBid;
	}
	
	public boolean buyNowMet(int buyNowAmt) {
		return buyNowAmt == buyNowPrice;
	}
	
	public int getTargetBid() {
		return targetBid;
	}
	
	public String getHighestBidder() {
		return highestBidder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getStartingBid() {
		return startingBid;
	}

	public void setStartingBid(int startingBid) {
		this.startingBid = startingBid;
	}

	public int getBuyNowPrice() {
		return buyNowPrice;
	}

	public void setBuyNowPrice(int buyNowPrice) {
		this.buyNowPrice = buyNowPrice;
	}

	public int getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(int currentBid) {
		this.currentBid = currentBid;
	}
	
	public String toString() {
		return this.name + "/" + this.currentBid + "/" + this.buyNowPrice + "/" + this.startingBid + "/" + this.targetBid + "/" + this.owner + "/" + this.highestBidder;
	}
	
	public static Item parseString(String itemString) {
		String[] input = itemString.split("/");
		String name = input[0];
		int currentBid = Integer.parseInt(input[1]);
		int buyNowPrice = Integer.parseInt(input[2]);
		int startingBid = Integer.parseInt(input[3]);
		int targetBid = Integer.parseInt(input[4]);
		String owner = input[5];
		String highestBidder = "";
		try {
			highestBidder = input[6];
		} catch (Exception e) {
			
		}
		
		if(currentBid < startingBid) currentBid = startingBid;
		if(input.length == 8) highestBidder = input[7];
		if(buyNowPrice > 0)	return new Item(name, currentBid, buyNowPrice, startingBid, targetBid, owner, highestBidder);
		else return new Item(name, currentBid, startingBid, targetBid, owner, highestBidder);
	}
	
	public static Item parseFormattedString(String formattedString) {
		String[] input = formattedString.split(FORMATTED_SPACER);
		String name = input[0];
		int currentBid = Integer.parseInt(input[1]);
		int buyNowPrice = -1;
		int startingBid = Integer.parseInt(input[3]);
		int targetBid = Integer.parseInt(input[4]);
		String owner = input[5];
		String highestBidder = "";
		
		try {
			highestBidder = input[6];
		} catch (Exception e) {
			
		}
		
		if(!input[2].equals("N/A")) buyNowPrice = Integer.parseInt(input[2]);
		
		if(buyNowPrice > 0)	return new Item(name, currentBid, buyNowPrice, startingBid, targetBid, owner, highestBidder);
		else return new Item(name, currentBid, startingBid, targetBid, owner, highestBidder);
		
	}
	
}
