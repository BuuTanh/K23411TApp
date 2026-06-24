package com.tranlebuutanh.k23411tapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorActivity extends AppCompatActivity {

    EditText edtFormular;
    Button btnDel, btnCalculate;
    TextView txtMC, txtMR, txtMPlus, txtMMinus, txtMS, txtM;
    View.OnClickListener m_click_listener;
    double memoryValue = 0;
    String name_share_ref = "CalculatorData"; // Tên file lưu trữ dữ liệu máy tính

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formular = edtFormular.getText().toString();
                String new_formular = "";
                if (formular.length() > 1) {
                    new_formular = formular.substring(0, formular.length() - 1);
                }
                edtFormular.setText(new_formular);
            }
        });
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Step 1: Lấy biểu thức và chuyển đổi x -> * , : -> /
                String formular = edtFormular.getText().toString().replace("x", "*").replace(":", "/");
                //Step 2
                String result = "";
                try {
                    // Nạp biểu thức vào trình xây dựng của thư viện
                    Expression e = new ExpressionBuilder(formular).build();
                    // Yêu cầu thư viện tính toán kết quả
                    double resValue = e.evaluate();

                    // Chuyển kết quả số về String
                    result = String.valueOf(resValue);

                    // Nếu kết quả là số nguyên thì bỏ phần thập phân
                    if (result.endsWith(".0")) {
                        result = result.substring(0, result.length() - 2);
                    }
                } catch (Exception e) {
                    // Nếu người dùng nhập sai (ví dụ 5++5) thì báo lỗi
                    result = "Error";
                }
                //result=call lib (formular)
                //Step 3
                edtFormular.setText(result);
            }
        });
        m_click_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy giá trị hiện tại trên màn hình để tính toán cho M+, M-
                double currentValue = 0;
                try {
                    String tempFormular = edtFormular.getText().toString().replace("x", "*").replace(":", "/");
                    if (!tempFormular.isEmpty()) {
                        currentValue = new ExpressionBuilder(tempFormular).build().evaluate();
                    }
                } catch (Exception e) { currentValue = 0; }

                if (view.equals(txtMC)) { // Memory Clear: Xóa bộ nhớ
                    memoryValue = 0;
                    Toast.makeText(CalculatorActivity.this, "Memory Cleared", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txtMR)) { // Memory Recall: Gọi số từ bộ nhớ ra
                    edtFormular.setText(edtFormular.getText().toString() + memoryValue);
                }
                else if (view.equals(txtMPlus)) { // Memory Plus: Cộng thêm vào bộ nhớ
                    memoryValue += currentValue;
                }
                else if (view.equals(txtMMinus)) { // Memory Minus: Trừ bớt ở bộ nhớ
                    memoryValue -= currentValue;
                }
                else if (view.equals(txtMS)) { // Memory Store: Lưu số đang có vào bộ nhớ
                    memoryValue = currentValue;
                    Toast.makeText(CalculatorActivity.this, "Stored", Toast.LENGTH_SHORT).show();
                }
                else if (view.equals(txtM)) { // Xem giá trị bộ nhớ
                    edtFormular.setText(String.valueOf(memoryValue));
                }
            }
        };
        txtMC.setOnClickListener(m_click_listener);
        txtMR.setOnClickListener(m_click_listener);
        txtMPlus.setOnClickListener(m_click_listener);
        txtMMinus.setOnClickListener(m_click_listener);
        txtMS.setOnClickListener(m_click_listener);
        txtM.setOnClickListener(m_click_listener);
    }

    private void addViews() {
        edtFormular = findViewById(R.id.edtFormular);
        btnDel = findViewById(R.id.btnDel);
        btnCalculate=findViewById(R.id.btnCalculate);
        txtMC=findViewById(R.id.txtMC);
        txtMR=findViewById(R.id.txtMR);
        txtMPlus=findViewById(R.id.txtMPlus);
        txtMMinus=findViewById(R.id.txtMMinus);
        txtMS=findViewById(R.id.txtMS);
        txtM=findViewById(R.id.txtM);
    }

    public void processInputData(View view) {
        Button btn = (Button) view;
        String new_value = btn.getText().toString();
        String current_value = edtFormular.getText().toString();

        // 1. Xử lý nút xóa hết (C hoặc CE)
        if (new_value.equals("C") || new_value.equals("CE")) {
            edtFormular.setText("");
            return;
        }

        // 2. Chuyển đổi các nút ký hiệu đặc biệt sang định dạng thư viện hiểu
        if (new_value.equals("sqrt(x)") || new_value.equals("x^2") || new_value.equals("1/x")) {
            if (current_value.isEmpty()) return;
            try {
                String expressionStr = "";
                String tempFormular = current_value.replace("x", "*").replace(":", "/");

                if (new_value.equals("sqrt(x)")) expressionStr = "sqrt(" + tempFormular + ")";
                else if (new_value.equals("x^2")) expressionStr = "(" + tempFormular + ")^2";
                else if (new_value.equals("1/x")) expressionStr = "1/(" + tempFormular + ")";

                double res = new ExpressionBuilder(expressionStr).build().evaluate();
                String result = String.valueOf(res);
                if (result.endsWith(".0")) result = result.substring(0, result.length() - 2);

                edtFormular.setText(result);
                return; // Thoát để không thực hiện bước 3 bên dưới
            } catch (Exception e) {
                edtFormular.setText("Error");
                return;
            }
        } else if (new_value.equals(",")) {
            new_value = ".";
        } else if (new_value.equals("%")) {
            new_value = "/100"; // Thay đổi này giúp tính toán 50% thành 50/100
        } else if (new_value.equals("+/-")) {
            if (!current_value.startsWith("-")) {
                edtFormular.setText("-" + current_value);
            } else {
                edtFormular.setText(current_value.substring(1));
            }
            return; // Thoát hàm vì đã set text rồi
        }
        // 3. Cập nhật lên màn hình
        edtFormular.setText(current_value + new_value);
    }
    @Override
    protected void onPause() {    super.onPause();
        // Khởi tạo SharedPreferences
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Lấy chuỗi phép tính hiện tại và lưu vào key "current_formular"
        String formular = edtFormular.getText().toString();
        editor.putString("current_formular", formular);

        // Xác nhận lưu
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Đọc dữ liệu từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);

        // Lấy chuỗi ra, nếu không có dữ liệu thì để trống ("")
        String savedFormular = preferences.getString("current_formular", "");

        // Hiển thị lại lên EditText
        edtFormular.setText(savedFormular);

        // Đưa con trỏ về cuối văn bản để người dùng nhập tiếp
        edtFormular.setSelection(edtFormular.getText().length());
    }
}