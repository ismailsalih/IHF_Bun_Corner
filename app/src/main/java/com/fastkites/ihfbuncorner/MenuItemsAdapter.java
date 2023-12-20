package com.fastkites.ihfbuncorner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.viewHolder> {

    List<MenuItemsModel> menuItemsList;
    Context context;
    OnItemClick onItemClick;

    public MenuItemsAdapter(List<MenuItemsModel> menuItemsList, Context context, OnItemClick onItemClick) {
        this.menuItemsList = menuItemsList;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.itemImage.setImageResource(R.drawable.reference_pic);
        holder.itemName.setText(menuItemsList.get(position).getFoodName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.itemClickListener(menuItemsList.get(holder.getAdapterPosition()).getItemCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    public interface OnItemClick {
        void itemClickListener(String itemCode);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        CardView menuItemLayout;
        ImageView itemImage;
        TextView itemName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.foodImage);
            itemName = itemView.findViewById(R.id.foodName);

        }
    }
}
