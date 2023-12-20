package com.fastkites.ihfbuncorner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuConfig extends AppCompatActivity {

    DrawerLayout drawer;
    ImageButton menuBtn;
    RecyclerView menuRecView;
    List<MenuCategoriesModel> menuCategoriesList;
    MenuCategoriesAdapter menuCategoriesAdapter;
    FloatingActionButton addBtn;
    Dialog dialog;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_config);

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
        navigationView.setCheckedItem(R.id.nav_inventory);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                // Handle navigation item clicks here
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

        menuRecView = findViewById(R.id.menuRecView);
        addBtn = findViewById(R.id.addBtn);
        menuCategoriesList = Constants.menuCategoriesList;
        menuCategoriesAdapter = new MenuCategoriesAdapter(menuCategoriesList, null, getApplicationContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        menuRecView.setLayoutManager(layoutManager);
        menuRecView.setAdapter(menuCategoriesAdapter);
        menuRecView.setHasFixedSize(true);
        menuCategoriesAdapter.notifyDataSetChanged();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MenuConfig.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.new_item_dialog_layout);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView itemImage = findViewById(R.id.itemImage);
                EditText itemName = findViewById(R.id.itemName);
                EditText itemPrice = findViewById(R.id.itemPrice);
                Spinner caSpinner = findViewById(R.id.catSpinner);
                TextView discardBtn = findViewById(R.id.discardBtn);
                TextView saveBtn = findViewById(R.id.saveBtn);

                discardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveItemInDatabase(itemImage, itemName, itemPrice, caSpinner);
                    }
                });
            }
        });

    }

    private void saveItemInDatabase(ImageView itemImage, EditText itemName, EditText itemPrice, Spinner catSpinner) {

        String itemCategory = "";
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemName", itemName.getText().toString());
        itemMap.put("itemPrice", Double.parseDouble(itemPrice.getText().toString()));
        itemMap.put("itemCategory", itemCategory);
        firestore.collection("Menu").document("").set(itemMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MenuConfig.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                Toast.makeText(MenuConfig.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (dialog.isShowing()) {
            dialog.dismiss();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
//            super.onBackPressed();
        }
    }
}