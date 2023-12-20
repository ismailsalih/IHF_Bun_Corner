package com.fastkites.ihfbuncorner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    ImageButton menuBtn, ticketBtn;
    TextView ticketCount;
    RecyclerView menuRecView;
    CardView ticketBtnLayout;
    List<MenuCategoriesModel> menuCategoriesList;
    MenuCategoriesAdapter menuCategoriesAdapter;
    ConstraintLayout ticketLayout;
    TicketItemsAdapter ticketItemsAdapter;
    double ticketTotalAmount;
    long userID;
    long lastTicket;
    String ticketNoString;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.menuCategoriesList.clear();
        Constants.updateLists();

        //drawerLayout
        menuBtn = findViewById(R.id.menuBtn);
        drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START, true);
            }
        });
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                // Handle navigation item clicks here
                switch (item.getItemId()) {
                    case R.id.nav_inventory:
                        startActivity(new Intent(getApplicationContext(), MenuConfig.class));
                        finish();
                        break;
                    // Add more cases for other items
                }

                // Close the navigation drawer
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //drawerLayout

        ticketCount = findViewById(R.id.ticketCount);
        ticketBtn = findViewById(R.id.ticketBtn);
        ticketBtnLayout = findViewById(R.id.ticketBtnCard);
        menuRecView = findViewById(R.id.menuRecView);
        ticketLayout = findViewById(R.id.ticketLayout);
        menuCategoriesList = Constants.menuCategoriesList;
        menuCategoriesAdapter = new MenuCategoriesAdapter(menuCategoriesList, ticketCount, getApplicationContext());
        userID = 1;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");

        ticketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ticketCount.getText().toString().equals("0")) {
                    progressDialog.show();
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int date = calendar.get(Calendar.DATE);
                    firestore.collection("USERS").document(String.valueOf(userID)).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    lastTicket = documentSnapshot.getLong("LAST_TICKET") + 1;
                                    ticketNoString = new StringBuilder().append(String.valueOf(year).substring(2)).append(month)
                                            .append(date).append("00").append(userID).append(lastTicket).toString();
                                    ticketLayoutConfig(ticketLayout);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NotNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        menuRecView.setLayoutManager(layoutManager);
        menuRecView.setAdapter(menuCategoriesAdapter);
        menuRecView.setHasFixedSize(true);
        menuCategoriesAdapter.notifyDataSetChanged();

    }

    private void ticketLayoutConfig(ConstraintLayout ticketLayout) {
        ticketLayout.setVisibility(View.VISIBLE);
        ticketBtnLayout.setVisibility(View.INVISIBLE);

        TextView ticketNo = ticketLayout.findViewById(R.id.ticketNo);
        TextView total = ticketLayout.findViewById(R.id.ticketTotal);
        TextView chargeBtn = ticketLayout.findViewById(R.id.chargeBtn);
        ImageButton ticketCloseBtn = ticketLayout.findViewById(R.id.closeBtn);
        EditText adjustment = ticketLayout.findViewById(R.id.adjustment);
        RecyclerView ticketItemsRecView = ticketLayout.findViewById(R.id.ticketItemsRecView);
        SwipeButton swipeButton = ticketLayout.findViewById(R.id.swipeBtn);

        ticketNo.setText("Ticket No: " + ticketNoString);
        ticketTotalAmount = 0.0;
        Map<String, TicketItemsModel> resultMap = new HashMap<>();

        for (TicketItemsModel ticketItems : Constants.ticketItemsList) {
            String itemCode = ticketItems.getItemCode();
            if (resultMap.containsKey(itemCode)) {
                // ItemCode already exists in the map, update values
                TicketItemsModel existingModel = resultMap.get(itemCode);
                existingModel.setQty(existingModel.getQty() + ticketItems.getQty());
            } else {
                // ItemCode doesn't exist in the map, add a new entry
                resultMap.put(itemCode, new TicketItemsModel(ticketItems.getName(), itemCode, ticketItems.getQty(), ticketItems.getPrice()));
            }

        }
        Constants.ticketItemsList = new ArrayList<>(resultMap.values());
        ticketItemsAdapter = new TicketItemsAdapter(Constants.ticketItemsList, "ticket", total);

        ticketItemsRecView.setLayoutManager(new LinearLayoutManager(this));
        ticketItemsRecView.setAdapter(ticketItemsAdapter);
        ticketItemsAdapter.notifyDataSetChanged();

        for (TicketItemsModel items : Constants.ticketItemsList) {
            ticketTotalAmount += items.getPrice() * items.getQty();
        }
        total.setText("LKR " + ticketTotalAmount);
        if (!adjustment.getText().toString().equals("")) {
            chargeBtn.setText("Charge\nLKR " + (ticketTotalAmount - Double.parseDouble(adjustment.getText().toString())));
        }

        total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    ticketTotalAmount = Double.parseDouble(total.getText().toString().substring(4));
                    if (!adjustment.getText().toString().equals("")) {
                        chargeBtn.setText("Charge\nLKR " + (ticketTotalAmount - Double.parseDouble(adjustment.getText().toString())));
                    }
                }
            }
        });

        ticketCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ticketLayout.setVisibility(View.GONE);
                ticketBtnLayout.setVisibility(View.VISIBLE);
                int totalTicketCount = 0;
                for (TicketItemsModel item : Constants.ticketItemsList) {
                    totalTicketCount += item.getQty();
                }
                ticketCount.setText(String.valueOf(totalTicketCount));
            }
        });

        swipeButton.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                Constants.ticketItemsList.clear();
                ticketItemsAdapter.notifyDataSetChanged();
                ticketLayout.setVisibility(View.GONE);
                ticketBtnLayout.setVisibility(View.VISIBLE);
                ticketCount.setText("0");
            }
        });

        adjustment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    chargeBtn.setText("Charge\nLKR " + (ticketTotalAmount - Integer.parseInt(s.toString())));
                }
            }
        });

        chargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPayDialog(adjustment, ticketNo, total);

            }
        });

        progressDialog.dismiss();
    }

    private void createPayDialog(TextView adjustment, TextView ticketNo, TextView total) {
        Dialog payDialog = new Dialog(MainActivity.this);
        payDialog.setCancelable(true);
        payDialog.setContentView(R.layout.balance_cal_dlg);
        payDialog.getWindow().setGravity(Gravity.CENTER);
        payDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        payDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        payDialog.show();
        ImageButton payDlgCloseBtn = payDialog.findViewById(R.id.closeBtn);
        TextView netTotal = payDialog.findViewById(R.id.netTotal);
        TextView payBtn = payDialog.findViewById(R.id.payBtn);
        EditText cash = payDialog.findViewById(R.id.cash);

        payDlgCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.dismiss();
            }
        });

        if (!adjustment.getText().toString().equals("")) {
            netTotal.setText("" + (ticketTotalAmount - Integer.parseInt(adjustment.getText().toString())));
        }
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.dismiss();
                createReceiptDialog(ticketNo, total, adjustment, cash);
            }
        });
    }

    private void createReceiptDialog(TextView ticketNo, TextView total, TextView adjustment, EditText cash) {
        Dialog receiptDialog = new Dialog(MainActivity.this);
        receiptDialog.setCancelable(true);
        receiptDialog.setContentView(R.layout.receipt_dlg);
        receiptDialog.getWindow().setGravity(Gravity.CENTER);
        receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        receiptDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        receiptDialog.show();

        TextView receiptTicketNo, receiptSubtotal, receiptAdjustment, receiptNetTotal, receiptCash, receiptBalance, confirmBtn;
        ImageButton receiptCloseBtn;
        RecyclerView receiptItemsRecView;

        receiptTicketNo = receiptDialog.findViewById(R.id.ticketNo);
        receiptSubtotal = receiptDialog.findViewById(R.id.subTotal);
        receiptAdjustment = receiptDialog.findViewById(R.id.adjustment);
        receiptNetTotal = receiptDialog.findViewById(R.id.netTotal);
        receiptCash = receiptDialog.findViewById(R.id.cash);
        receiptBalance = receiptDialog.findViewById(R.id.balance);
        confirmBtn = receiptDialog.findViewById(R.id.confirmBtn);
        receiptCloseBtn = receiptDialog.findViewById(R.id.closeBtn);
        receiptItemsRecView = receiptDialog.findViewById(R.id.receiptRecView);

        receiptItemsRecView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        receiptItemsRecView.setAdapter(new TicketItemsAdapter(Constants.ticketItemsList, "receipt", null));

        receiptCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiptDialog.dismiss();
            }
        });

        receiptTicketNo.setText(ticketNo.getText().toString());
        receiptSubtotal.setText(total.getText().toString().replace("LKR ", ""));
        receiptAdjustment.setText(adjustment.getText().toString());
        if (!adjustment.getText().toString().equals("")) {
            receiptNetTotal.setText("" + (ticketTotalAmount - Double.parseDouble(adjustment.getText().toString())));
        }
        receiptCash.setText(String.valueOf(Double.parseDouble(cash.getText().toString())));
        receiptBalance.setText("" + (Double.parseDouble(cash.getText().toString()) - Double.parseDouble(receiptNetTotal.getText().toString())));

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                saveInDatabase(receiptSubtotal, receiptAdjustment, receiptCash, receiptDialog);
            }
        });
        receiptDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                int totalTicketCount = 0;
                for (TicketItemsModel item : Constants.ticketItemsList) {
                    totalTicketCount += item.getQty();
                }
                ticketCount.setText(String.valueOf(totalTicketCount));
            }
        });
    }

    private void saveInDatabase(TextView subtotal, TextView adjustment, TextView cash, Dialog receiptDialog) {
        Map<String, Object> sale = new HashMap<>();
        sale.put("ticketNo", ticketNoString);
        sale.put("time", System.currentTimeMillis());
        sale.put("subTotal", Double.parseDouble(subtotal.getText().toString().replace(",", "")));
        sale.put("adjustment", Double.parseDouble(adjustment.getText().toString().replace(",", "")));
        sale.put("cash", Double.parseDouble(cash.getText().toString().replace(",", "")));
        sale.put("items", new Gson().toJson(Constants.ticketItemsList));

        firestore.collection("SALES").document(ticketNoString).set(sale)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firestore.collection("USERS").document(String.valueOf(userID))
                                .update("LAST_TICKET", lastTicket).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Congrats! You've completed a sale"
                                        , Toast.LENGTH_SHORT).show();
                                receiptDialog.dismiss();
                                ticketLayout.setVisibility(View.GONE);
                                ticketBtnLayout.setVisibility(View.VISIBLE);
                                Constants.ticketItemsList.clear();
                                ticketItemsAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                Toast.makeText(MainActivity.this, "Sorry! Your sale is not updated in server due to network issue"
                        , Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}