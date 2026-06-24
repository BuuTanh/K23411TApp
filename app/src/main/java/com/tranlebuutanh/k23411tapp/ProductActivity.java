package com.tranlebuutanh.k23411tapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.adapters.ProductAdapter;
import com.tranlebuutanh.dals.ProductDAO;
import com.tranlebuutanh.models.Category;
import com.tranlebuutanh.models.Product;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    ListView lvProduct;
    TextView txtCategoryTitle;
    ArrayList<Product> products;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
    }

    private void addViews() {
        lvProduct        = findViewById(R.id.lvProduct);
        txtCategoryTitle = findViewById(R.id.txtCategoryTitle);

        // Nhận Category từ Intent
        Category category = (Category) getIntent().getSerializableExtra("CATEGORY");
        if (category != null) {
            txtCategoryTitle.setText("🗂 " + category.getCategoryName());
            products = ProductDAO.getProductsByCategory(this, category.getCategoryId());
        } else {
            products = new ArrayList<>();
        }

        productAdapter = new ProductAdapter(this, R.layout.item_custom_product);
        productAdapter.addAll(products);
        lvProduct.setAdapter(productAdapter);
    }
}
