package com.fastkites.ihfbuncorner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketItemsAdapter extends RecyclerView.Adapter<TicketItemsAdapter.viewHolder> {

    List<TicketItemsModel> ticketItemsList;
    String root;
    TextView ticketTotal;
    double ticketTotalAmount;

    public TicketItemsAdapter(List<TicketItemsModel> ticketItemsList, String root, TextView ticketTotal) {
        this.ticketItemsList = ticketItemsList;
        this.root = root;
        this.ticketTotal = ticketTotal;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.foodName.setText(ticketItemsList.get(position).getName());
        holder.foodQty.setText("x " + ticketItemsList.get(position).getQty());
        holder.value.setText("" + ticketItemsList.get(position).getQty());
        holder.foodPrice.setText("LKR " + (Integer.parseInt(holder.value.getText().toString()) * ticketItemsList.get(position).getPrice()));
        if (ticketTotal != null) {
            ticketTotalAmount = Double.parseDouble(ticketTotal.getText().toString().substring(4));
        }
        holder.minBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = Integer.parseInt(holder.value.getText().toString());
                y--;
                if (y == 0) {
                    Toast.makeText(view.getContext(), "Press Close Button to delete the item", Toast.LENGTH_SHORT).show();
                } else {
                    holder.value.setText(String.valueOf(y));
                    Constants.ticketItemsList.get(holder.getAdapterPosition()).setQty(y);
                    ticketTotalAmount -= ticketItemsList.get(holder.getAdapterPosition()).getPrice();
                    ticketTotal.setText("LKR " + ticketTotalAmount);
                    holder.foodPrice.setText("LKR " + (y * ticketItemsList.get(holder.getAdapterPosition()).getPrice()));
                    notifyItemChanged(holder.getAdapterPosition(), ticketItemsList.get(holder.getAdapterPosition()).getQty());
                }
            }
        });
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = Integer.parseInt(holder.value.getText().toString());
                y++;
                holder.value.setText(String.valueOf(y));
                Constants.ticketItemsList.get(holder.getAdapterPosition()).setQty(y);
                ticketTotalAmount += ticketItemsList.get(holder.getAdapterPosition()).getPrice();
                ticketTotal.setText("LKR " + ticketTotalAmount);
                holder.foodPrice.setText("LKR " + (y * ticketItemsList.get(holder.getAdapterPosition()).getPrice()));
                notifyItemChanged(holder.getAdapterPosition(), ticketItemsList.get(holder.getAdapterPosition()).getQty());
            }
        });
        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ticketTotalAmount -= ticketItemsList.get(holder.getAdapterPosition()).getPrice() * Integer.parseInt(holder.value.getText().toString());
                ticketTotal.setText("LKR " + ticketTotalAmount);
                Constants.ticketItemsList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketItemsList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView foodName, foodQty, foodPrice, minBtn, plusBtn, value;
        CardView inc;
        ImageButton closeBtn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.foodName);
            foodQty = itemView.findViewById(R.id.foodQty);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            minBtn = itemView.findViewById(R.id.minBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            value = itemView.findViewById(R.id.value);
            inc = itemView.findViewById(R.id.inc);
            closeBtn = itemView.findViewById(R.id.closeBtn);

            if (root.equals("receipt")) {
                closeBtn.setVisibility(View.INVISIBLE);
                inc.setVisibility(View.GONE);
            }
        }
    }
}
