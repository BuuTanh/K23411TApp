package com.tranlebuutanh.k23411tapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranlebuutanh.models.Category;
import com.tranlebuutanh.models.DataWareHouse;
import com.tranlebuutanh.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ClientViewActivity extends AppCompatActivity {

    private TextView txtClientStatus;
    private EditText edtSearch;
    private Button btnSearch;
    private LinearLayout layoutCategories;
    private ListView lvProducts;

    private ArrayList<Category> categories;
    private ArrayList<Product> allProducts;
    private ArrayList<Product> displayProducts;
    private ArrayAdapter<String> productAdapter;
    private ArrayList<String> productStrings;
    private String selectedCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_view);

        initViews();
        loadData();
        setupEvents();
    }

    private void initViews() {
        txtClientStatus = findViewById(R.id.txtClientStatus);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        layoutCategories = findViewById(R.id.layoutCategories);
        lvProducts = findViewById(R.id.lvProducts);

        productStrings = new ArrayList<>();
        productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productStrings);
        lvProducts.setAdapter(productAdapter);

        txtClientStatus.setText(isOnline() ? "Online" : "Offline");
    }

    private void loadData() {
        if (isOnline()) {
            loadFromFirebase();
        } else {
            loadFromLocal();
        }
    }

    private void loadFromFirebase() {
        txtClientStatus.setText("Loading from Firebase...");

        DatabaseReference dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getKey();
                    String name = child.child("categoryName").getValue(String.class);
                    String desc = child.child("description").getValue(String.class);
                    categories.add(new Category(id, name != null ? name : "", desc != null ? desc : ""));
                }
                buildCategoryButtons();
                loadProductsFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadFromLocal();
            }
        });
    }

    private void loadProductsFromFirebase() {
        DatabaseReference dbProducts = FirebaseDatabase.getInstance().getReference("products");
        dbProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getKey();
                    String name = child.child("productName").getValue(String.class);
                    Double price = child.child("price").getValue(Double.class);
                    Long stock = child.child("stock").getValue(Long.class);
                    String catId = child.child("categoryId").getValue(String.class);
                    allProducts.add(new Product(id,
                            name != null ? name : "",
                            stock != null ? stock.intValue() : 0,
                            price != null ? price : 0,
                            0, 0,
                            catId != null ? catId : ""));
                }
                txtClientStatus.setText("Online - Firebase");
                filterAndDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadFromLocal();
            }
        });
    }

    private void loadFromLocal() {
        txtClientStatus.setText("Offline - Local Data");
        categories = DataWareHouse.getCategories();
        allProducts = DataWareHouse.getProducts();
        buildCategoryButtons();
        filterAndDisplay();
    }

    private void buildCategoryButtons() {
        layoutCategories.removeAllViews();

        // All button
        Button btnAll = createCategoryButton("All", null);
        layoutCategories.addView(btnAll);

        for (Category c : categories) {
            Button btn = createCategoryButton(c.getCategoryName(), c.getCategoryId());
            layoutCategories.addView(btn);
        }
    }

    private Button createCategoryButton(String text, String categoryId) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(12);
        btn.setAllCaps(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 8);
        btn.setLayoutParams(params);

        boolean isSelected = (categoryId == null && selectedCategoryId == null) ||
                (categoryId != null && categoryId.equals(selectedCategoryId));

        if (isSelected) {
            btn.setBackgroundColor(Color.parseColor("#00897B"));
            btn.setTextColor(Color.WHITE);
            btn.setTypeface(null, Typeface.BOLD);
        } else {
            btn.setBackgroundColor(Color.WHITE);
            btn.setTextColor(Color.parseColor("#333333"));
        }

        btn.setOnClickListener(v -> {
            selectedCategoryId = categoryId;
            buildCategoryButtons();
            filterAndDisplay();
        });

        return btn;
    }

    private void setupEvents() {
        btnSearch.setOnClickListener(v -> filterAndDisplay());

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            filterAndDisplay();
            return true;
        });
    }

    private void filterAndDisplay() {
        String keyword = edtSearch.getText().toString().trim().toLowerCase();
        displayProducts = new ArrayList<>();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (Product p : allProducts) {
            boolean matchCategory = selectedCategoryId == null || p.getCateId().equals(selectedCategoryId);
            boolean matchSearch = keyword.isEmpty() || p.getProductName().toLowerCase().contains(keyword);

            if (matchCategory && matchSearch) {
                displayProducts.add(p);
            }
        }

        productStrings.clear();
        for (Product p : displayProducts) {
            productStrings.add(p.getProductName() + "\nPrice: " + nf.format(p.getPrice()) + " VND  |  Stock: " + p.getQuantity());
        }
        productAdapter.notifyDataSetChanged();

        if (displayProducts.isEmpty()) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
