package com.fastkites.ihfbuncorner;

import java.util.List;

public class MenuCategoriesModel {
    String title;
    List<MenuItemsModel> menuItemsList;

    public MenuCategoriesModel(String title, List<MenuItemsModel> menuItemsList) {
        this.title = title;
        this.menuItemsList = menuItemsList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuItemsModel> getMenuItemsList() {
        return menuItemsList;
    }

    public void setMenuItemsList(List<MenuItemsModel> menuItemsList) {
        this.menuItemsList = menuItemsList;
    }
}
