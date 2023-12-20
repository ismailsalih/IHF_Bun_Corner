package com.fastkites.ihfbuncorner;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static List<MenuItemsModel> category1ItemsList = new ArrayList<>();
    public static List<MenuItemsModel> category2ItemsList = new ArrayList<>();
    public static List<MenuCategoriesModel> menuCategoriesList = new ArrayList<>();
    public static List<TicketItemsModel> ticketItemsList = new ArrayList<>();

    public static void updateLists() {
        category1ItemsList.add(new MenuItemsModel("Shredded Chicken Sub", "001", R.drawable.reference_pic, 150));
        category1ItemsList.add(new MenuItemsModel("Shredded Chilli Chicken Sub", "002", R.drawable.reference_pic, 200));
        category1ItemsList.add(new MenuItemsModel("Shredded Butter Chicken Sub", "003", R.drawable.reference_pic, 200));
        category1ItemsList.add(new MenuItemsModel("Shredded Beef Sub", "004", R.drawable.reference_pic, 200));
        category1ItemsList.add(new MenuItemsModel("Cheese & Veg Sub", "005", R.drawable.reference_pic, 100));
        menuCategoriesList.add(new MenuCategoriesModel("Submarine", category1ItemsList));

        category2ItemsList.add(new MenuItemsModel("Donut", "006", R.drawable.reference_pic, 60));
        menuCategoriesList.add(new MenuCategoriesModel("Dessert", category2ItemsList));
    }
}
