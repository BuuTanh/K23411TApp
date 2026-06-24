package com.tranlebuutanh.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.adapters.EmployeeAdapter;
import com.tranlebuutanh.models.Department;
import com.tranlebuutanh.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvancedMainActivity extends AppCompatActivity {

    ListView lvEmployee;
    ArrayList<Employee> listOfEmployee;
    EmployeeAdapter adapterEmployee;

    Spinner spDepartment;
    ArrayList<Department> listOfDepartment;
    ArrayAdapter<Department> adapterDepartment;

    ImageView imgAddEmployee, imgEditEmployee, imgDeleteEmployee;

    ActivityResultLauncher<Intent> addEmployeeLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                String id = data.getStringExtra("id");
                String name = data.getStringExtra("name");
                String phone = data.getStringExtra("phone");
                Employee newEmp = new Employee(id, name, phone);

                listOfEmployee.add(newEmp);
                listOfDepartment.get(0).addEmployee(newEmp);

                int selectedPos = spDepartment.getSelectedItemPosition();
                adapterEmployee.clear();
                adapterEmployee.addAll(listOfDepartment.get(selectedPos).getListOfEmployee());
                adapterEmployee.notifyDataSetChanged();
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_advanced_main);
        addViews();
        sampleData();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Department selectedDepartment = listOfDepartment.get(i);
                ;
                adapterEmployee.clear();
                adapterEmployee.addAll(selectedDepartment.getListOfEmployee());
                adapterEmployee.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imgAddEmployee.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeeAdvancedMainActivity.this,
                    AddEmployeeActivity.class);
            //addEmployeeLauncher.launch(intent);
            startActivityForResult(intent, 999);
        }
        );
    }


    private void sampleData() {
        Employee e1 = new Employee("e1", "Tánh", "0955792911");
        Employee e2 = new Employee("e2", "Ny", "0955795611");
        Employee e3 = new Employee("e3", "Huyền", "0955793411");
        listOfEmployee.add(e1);
        listOfEmployee.add(e2);
        listOfEmployee.add(e3);
        adapterEmployee.addAll(listOfEmployee);
        adapterEmployee.notifyDataSetChanged();

        Department dAll = new Department("all", "Tất cả");
        dAll.addListEmployee(listOfEmployee);

        Department d1 = new Department("d1", "Phòng 1");
        Department d2 = new Department("d2", "Phòng 2");
        Department d3 = new Department("d3", "Phòng Nhân sự");
        d1.addEmployee(e1);
        d2.addEmployee(e2);
        d3.addEmployee(e3);

        listOfDepartment.add(dAll);
        listOfDepartment.add(d1);
        listOfDepartment.add(d2);
        listOfDepartment.add(d3);
        adapterDepartment.addAll(listOfDepartment);
        adapterDepartment.notifyDataSetChanged();
    }

    private void addViews() {
        lvEmployee = findViewById(R.id.lvEmployee);
        listOfEmployee = new ArrayList<>();
        adapterEmployee = new EmployeeAdapter(this, R.layout.item_custom_employee);
        lvEmployee.setAdapter(adapterEmployee);

        spDepartment = findViewById(R.id.spDepartment);
        listOfDepartment = new ArrayList<>();
        adapterDepartment = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                listOfDepartment);
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(adapterDepartment);

        imgAddEmployee = findViewById(R.id.imgAddEmployee);
        imgEditEmployee = findViewById(R.id.imgEditEmployee);
        imgDeleteEmployee = findViewById(R.id.imgDeleteEmployee);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == 888 && data != null) {
            Employee emp = (Employee) data.getSerializableExtra("K23411T_EMPLOYEE");
            if (emp == null) return;

            int selectedPos = spDepartment.getSelectedItemPosition();
            Department selectedDept = listOfDepartment.get(selectedPos);
            Department dAll = listOfDepartment.get(0);
            Department dNS = listOfDepartment.get(3); // d3 là Phòng nhân sự

            // 1. Luôn thêm vào danh sách "Tất cả"
            dAll.addEmployee(emp);

            // 2. Kiểm tra logic thêm vào phòng ban
            if (selectedPos == 0) {
                // Nếu đang chọn "Tất cả", thì mặc định thêm vào "Phòng nhân sự"
                dNS.addEmployee(emp);
            } else {
                // Nếu đang chọn một phòng cụ thể, thêm vào phòng đó
                selectedDept.addEmployee(emp);
            }

            // 3. Cập nhật giao diện ListView theo phòng ban đang chọn
            adapterEmployee.clear();
            adapterEmployee.addAll(selectedDept.getListOfEmployee());
            adapterEmployee.notifyDataSetChanged();
        }
    }
}