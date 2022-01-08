package model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Deal {
	@Id
	int dealId;
	String productName;
	int maxItems;
	int price;
	int ItemsBought;
	LocalDateTime maxtime;
	
	
	public int getDealId() {
		return dealId;
	}
	public void setDealId(int dealId) {
		this.dealId = dealId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getMaxItems() {
		return maxItems;
	}
	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}
	public LocalDateTime getMaxtime() {
		return maxtime;
	}
	public void setMaxtime(LocalDateTime maxtime) {
		this.maxtime = maxtime;
	}
	public int getItemsBought() {
		return ItemsBought;
	}
	public void setItemsBought(int itemsBought) {
		ItemsBought = itemsBought;
	}

}
