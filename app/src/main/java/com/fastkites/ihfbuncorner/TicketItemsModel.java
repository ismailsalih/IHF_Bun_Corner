package com.fastkites.ihfbuncorner;

public class TicketItemsModel {

    String name, itemCode;
    int qty;
    double price;

    public TicketItemsModel(String name, String itemCode, int qty, double price) {
        this.name = name;
        this.itemCode = itemCode;
        this.qty = qty;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
