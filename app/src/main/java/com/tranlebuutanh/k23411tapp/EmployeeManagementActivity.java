package com.tranlebuutanh.k23411tapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class EmployeeManagementActivity extends AppCompatActivity {

    EditText edtID, edtName, edtPhone;
    Button btnSave, btnClear, btnExit;
    ListView lvEmployee;
    ArrayList<String> listOfEmployee;
    ArrayAdapter<String> adapterEmployee;
    int lastSelectedPosition = -1; // Lưu vị trí item được chọn
    String name_share_ref = "employee_pref"; // Tên file SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
        loadData();
        if (listOfEmployee.isEmpty()) {
            sampleData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lưu vị trí được chọn gần nhất vào SharedPreferences
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_selected_pos", lastSelectedPosition);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đọc lại vị trí đã lưu
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        lastSelectedPosition = preferences.getInt("last_selected_pos", -1);

        // Nếu có vị trí hợp lệ, hiển thị lại thông tin lên UI
        if (lastSelectedPosition != -1 && lastSelectedPosition < listOfEmployee.size()) {
            String emp = listOfEmployee.get(lastSelectedPosition);
            String[] arrInfo = emp.split("-");
            if (arrInfo.length >= 3) {
                edtID.setText(arrInfo[0]);
                edtName.setText(arrInfo[1]);
                edtPhone.setText(arrInfo[2]);
            }
            // Yêu cầu ListView vẽ lại để đổi màu nền cho dòng này
            adapterEmployee.notifyDataSetChanged();
        }
    }

    private void addEvents() {
        lvEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                // Cập nhật vị trí được chọn
                lastSelectedPosition = i;
                // Thông báo cho adapter vẽ lại ListView để cập nhật màu nền
                adapterEmployee.notifyDataSetChanged();

                // Hiển thị thông tin lên EditText
                String emp = listOfEmployee.get(i);
                String[] arrInfo = emp.split("-");
                if (arrInfo.length >= 3) {
                    edtID.setText(arrInfo[0]);
                    edtName.setText(arrInfo[1]);
                    edtPhone.setText(arrInfo[2]);
                }
            }
        });
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder sb = new StringBuilder();
        for (String s : listOfEmployee) {
            sb.append(s).append(";"); // Dùng dấu ; để phân tách các nhân viên
        }
        editor.putString("employee_list_str", sb.toString());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        String listStr = preferences.getString("employee_list_str", "");
        if (!listStr.isEmpty()) {
            String[] items = listStr.split(";");
            listOfEmployee.clear();
            for (String item : items) {
                if (!item.trim().isEmpty()) {
                    listOfEmployee.add(item);
                }
            }
        }
    }

    private void sampleData() {
        listOfEmployee.add("e1-Tánh-0955792911");
        listOfEmployee.add("e2-Ny-0955795611");
        listOfEmployee.add("e3-Huyền-0955793411");
        
        Random random = new Random();
        for (int i = 4; i < 10; i++) {
            String id = "e" + i;
            String name = "Name" + i;
            String phone = "090";
            int provider = random.nextInt(3);
            if (provider == 1) phone = "098";
            else if (provider == 2) phone = "094";
            for (int p = 1; p <= 7; p++) phone += random.nextInt(10);
            listOfEmployee.add(id + "-" + name + "-" + phone);
        }
        adapterEmployee.notifyDataSetChanged();
    }

    private void addViews() {
        edtID = findViewById(R.id.edtID);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnDelete);
        btnExit = findViewById(R.id.btnExit);
        lvEmployee = findViewById(R.id.lvEmployee);
        
        listOfEmployee = new ArrayList<>();
        // Khởi tạo adapter và ghi đè getView để đổi màu nền
        adapterEmployee = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listOfEmployee) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                
                // Kiểm tra nếu dòng hiện tại là dòng được chọn
                if (position == lastSelectedPosition) {
                    v.setBackgroundColor(Color.YELLOW); // Đổi sang màu vàng
                } else {
                    v.setBackgroundColor(Color.TRANSPARENT); // Các dòng khác để trong suốt (mặc định)
                }

                return v;
            }
        };
        lvEmployee.setAdapter(adapterEmployee);
    }

    public void closeActivity(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        ImageView imgYes = dialog.findViewById(R.id.imgYes);
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        
        imgYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void saveEmployee(View view) {
        String id = edtID.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (id.isEmpty()) return;

        String emp = id + "-" + name + "-" + phone;

        boolean isExist = false;
        for (int i = 0; i < listOfEmployee.size(); i++) {
            String item = listOfEmployee.get(i);
            if (item.startsWith(id + "-")) {
                // Nếu ID đã tồn tại, thực hiện cập nhật
                listOfEmployee.set(i, emp);
                lastSelectedPosition = i;
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            // Nếu ID chưa tồn tại, thêm mới vào danh sách
            listOfEmployee.add(emp);
            lastSelectedPosition = listOfEmployee.size() - 1;
        }

        adapterEmployee.notifyDataSetChanged();
        saveData();
    }

    public void deleteEmployee(View view) {
        if (lastSelectedPosition == -1) {
            Toast.makeText(this, R.string.str_msg_select_employee, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.str_title_confirm_delete);
        builder.setMessage(R.string.str_msg_confirm_delete);
        builder.setIcon(android.R.drawable.ic_delete);

        builder.setPositiveButton(R.string.str_btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xóa khỏi danh sách
                listOfEmployee.remove(lastSelectedPosition);

                // Reset vị trí chọn và xóa trắng các ô nhập liệu
                lastSelectedPosition = -1;
                edtID.setText("");
                edtName.setText("");
                edtPhone.setText("");

                // Cập nhật lại giao diện
                adapterEmployee.notifyDataSetChanged();
                saveData();
                Toast.makeText(EmployeeManagementActivity.this, R.string.str_msg_delete_success, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.str_btn_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}