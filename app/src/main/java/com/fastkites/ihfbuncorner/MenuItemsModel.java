package com.fastkites.ihfbuncorner;

public class MenuItemsModel {

    String foodName, itemCode;
    int foodImage, foodPrice;

    public MenuItemsModel(String foodName, String itemCode, int foodImage, int foodPrice) {
        this.foodName = foodName;
        this.itemCode = itemCode;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(int foodImage) {
        this.foodImage = foodImage;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }
}
