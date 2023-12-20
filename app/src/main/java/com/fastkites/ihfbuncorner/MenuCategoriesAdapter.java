package com.fastkites.ihfbuncorner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuCategoriesAdapter extends RecyclerView.Adapter<MenuCategoriesAdapter.viewHolder> implements MenuItemsAdapter.OnItemClick {

    List<MenuCategoriesModel> menuCategoriesList;
    TextView ticketCount;
    Context context;

    public MenuCategoriesAdapter(List<MenuCategoriesModel> menuCategoriesList, TextView ticketCount, Context context) {
        this.menuCategoriesList = menuCategoriesList;
        this.ticketCount = ticketCount;
        this.context = context;
    }

    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_categories_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull viewHolder holder, int position) {
        holder.title.setText(menuCategoriesList.get(position).getTitle());
        GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);
        holder.menuItemsGridView.setLayoutManager(layoutManager);
        holder.menuItemsGridView.setAdapter(new MenuItemsAdapter(menuCategoriesList.get(position).getMenuItemsList(),
                holder.itemView.getContext(),
                this));
    }

    @Override
    public int getItemCount() {
        return menuCategoriesList.size();
    }

    @Override
    public void itemClickListener(String itemCode) {
        if (ticketCount != null) {
            ticketCount.setText(String.valueOf(Long.parseLong(ticketCount.getText().toString()) + 1));
            for (MenuCategoriesModel categories : Constants.menuCategoriesList) {
                for (MenuItemsModel items : categories.getMenuItemsList()) {
                    if (items.getItemCode().equals(itemCode)) {
                        Constants.ticketItemsList.add(new TicketItemsModel(items.getFoodName(), itemCode, 1, items.getFoodPrice()));
                    }
                }
            }
        } else {
            Intent intent = new Intent(context, MenuDetails.class);
            intent.putExtra("itemCode", itemCode);
            context.startActivity(intent);
        }
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView menuItemsGridView;

        public viewHolder(@NotNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            menuItemsGridView = itemView.findViewById(R.id.menuItemsGridView);
        }
    }
}
