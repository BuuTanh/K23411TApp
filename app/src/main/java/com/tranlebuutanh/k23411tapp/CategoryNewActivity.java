package com.tranlebuutanh.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.dals.CategoryDAO;
import com.tranlebuutanh.models.Category;

public class CategoryNewActivity extends AppCompatActivity {

    EditText edtCategoryId;
    EditText edtCategoryName;
    EditText edtCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_new);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtCategoryName = findViewById(R.id.edtCategoryName);
        edtCategoryDescription = findViewById(R.id.edtCategoryDescription);
    }

    public void processSaveCategory(View view) {
        String cateId=edtCategoryId.getText().toString();
        String cateName=edtCategoryName.getText().toString();
        String description=edtCategoryDescription.getText().toString();
        Category category=new Category(cateId,cateName,description);
        long result= CategoryDAO.saveNewCategory(this,category);
        if(result>0)
        {
            Intent intent=getIntent();
            setResult(2,intent);
            finish();
        }
        else
            finish();
    }

    public void processCancelCategory(View view) {
        finish();
    }
}