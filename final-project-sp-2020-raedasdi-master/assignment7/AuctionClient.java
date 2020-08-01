package assignment7;

import java.util.ArrayList;

public class AuctionClient {
	private String name;
	private String pass;
	public ArrayList<Item> itemsOwned = new ArrayList<Item>();
	
	public AuctionClient(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}
	
	public void replaceItem(Item replace, Item newItem) {
		for(int i = 0; i < itemsOwned.size(); i++) {
			if(itemsOwned.get(i).isEqual(replace.getName(), replace.getOwner())) {
				itemsOwned.set(i, newItem);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setClientID(String pass) {
		this.pass = pass;
	}

	public void addItem(Item newItem) {
		this.itemsOwned.add(newItem);
	}
	
	public Item removeItem(Item remItem) {
		Item returnItem = this.itemsOwned.get(this.itemsOwned.indexOf(remItem));
		this.itemsOwned.remove(remItem);
		return returnItem;
	}
	
	public void setItems(ArrayList<Item> items) {
		itemsOwned = items;
	}
	
	public ArrayList<Item> getItems(){
		return itemsOwned;
	}
}
