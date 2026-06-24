package com.tranlebuutanh.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.models.Employee;

public class AddEmployeeActivity extends AppCompatActivity {

    EditText edtId, edtName, edtPhone;
    AutoCompleteTextView actBirthPlace;
    String[] listOfBirthPlace;
    ArrayAdapter<String> adapterBirthPlace;
    ImageView imgSave, imgCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_employee);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        actBirthPlace = findViewById(R.id.actBirthPlace);
        imgSave = findViewById(R.id.imgSave);
        imgCancel = findViewById(R.id.imgCancel);

        listOfBirthPlace = getResources().getStringArray(R.array.arr_province);
        adapterBirthPlace = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                listOfBirthPlace);
        actBirthPlace.setAdapter(adapterBirthPlace);
    }

    private void addEvents() {
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSaveEmployee();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Hiển thị danh sách gợi ý khi người dùng nhấn vào ô quê quán
        actBirthPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    actBirthPlace.showDropDown();
                }
            }
        });

        actBirthPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actBirthPlace.showDropDown();
            }
        });
    }

    private void processSaveEmployee() {
        Employee emp=new Employee();
        emp.setId(edtId.getText().toString());
        emp.setName(edtName.getText().toString());
        emp.setPhone(edtPhone.getText().toString());
        emp.setBirthPlace(actBirthPlace.getText().toString());

        Intent intent=getIntent();
        intent.putExtra("K23411T_EMPLOYEE",emp);
        setResult(888,intent);
        //Call finsih --> Advanced Employee --> foreground liftime
        finish();
    }
}