package com.tranlebuutanh.k23411tapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.adapters.OrderAdapter;
import com.tranlebuutanh.models.DataWareHouse;
import com.tranlebuutanh.models.Order;
import com.tranlebuutanh.models.OrderStatus;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderManagementActivity extends AppCompatActivity {

    TextView txtFromDate, txtToDate;
    ImageView imgFromDate, imgToDate, imgClearFilter, imgFilter;
    LinearLayout btnClearFilter, btnFilter;
    ListView lvOrder;
    ArrayList<Order> orders;
    OrderAdapter orderAdapter;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    DecimalFormat df = new DecimalFormat("#,##0.##");
    Calendar calFromDate = Calendar.getInstance();
    Calendar calToDate = Calendar.getInstance();
    OrderStatus currentStatus = OrderStatus.ALL; // lưu status đang chọn

    DatePickerDialog.OnDateSetListener fromDateListener;
    DatePickerDialog.OnDateSetListener toDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_management);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtFromDate    = findViewById(R.id.txtFromDate);
        txtToDate      = findViewById(R.id.txtToDate);
        imgFromDate    = findViewById(R.id.imgFromDate);
        imgToDate      = findViewById(R.id.imgToDate);
        imgClearFilter = findViewById(R.id.imgClearFilter);
        imgFilter      = findViewById(R.id.imgFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        btnFilter      = findViewById(R.id.btnFilter);
        lvOrder        = findViewById(R.id.lvOrder);

        calFromDate.set(2026, 0, 1);
        calToDate.set(2026, 0, 31);
        txtFromDate.setText(sdf.format(calFromDate.getTime()));
        txtToDate.setText(sdf.format(calToDate.getTime()));

        orders = DataWareHouse.getOrders();
        orderAdapter = new OrderAdapter(this, orders);
        lvOrder.setAdapter(orderAdapter);
    }

    private void addEvents() {
        fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calFromDate.set(year, month, day);
                txtFromDate.setText(sdf.format(calFromDate.getTime()));
            }
        };

        toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calToDate.set(year, month, day);
                txtToDate.setText(sdf.format(calToDate.getTime()));
            }
        };

        imgFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrderManagementActivity.this, fromDateListener,
                        calFromDate.get(Calendar.YEAR),
                        calFromDate.get(Calendar.MONTH),
                        calFromDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        imgToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrderManagementActivity.this, toDateListener,
                        calToDate.get(Calendar.YEAR),
                        calToDate.get(Calendar.MONTH),
                        calToDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Clear: reset về tất cả, giữ nguyên status hiện tại
        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = OrderStatus.ALL;
                calFromDate.set(2026, 0, 1);
                calToDate.set(2026, 0, 31);
                txtFromDate.setText(sdf.format(calFromDate.getTime()));
                txtToDate.setText(sdf.format(calToDate.getTime()));
                reloadList(DataWareHouse.getOrders());
            }
        });

        // Filter: lọc theo date + status đang chọn
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Order> filtered = DataWareHouse.filterOrdersByDateAndStatus(
                        calFromDate.getTime(), calToDate.getTime(), currentStatus);
                reloadList(filtered);
            }
        });

        // Click vào item → hiện dialog thông tin order
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = orderAdapter.getItem(position);
                if (order == null) return;
                showOrderDialog(order);
            }
        });
    }

    private void reloadList(ArrayList<Order> data) {
        orders = data;
        orderAdapter.clear();
        orderAdapter.addAll(orders);
        orderAdapter.notifyDataSetChanged();
    }

    private void showOrderDialog(Order order) {
        String status = order.getOrderStatus() != null ? order.getOrderStatus().name() : "N/A";
        double total = DataWareHouse.sumOfMoneyForOrder(order);

        String message =
                "🆔  Mã đơn hàng:  " + order.getOrderId() + "\n\n" +
                "📅  Ngày đặt:        " + sdf.format(order.getOrderDate()) + "\n\n" +
                "💰  Tổng tiền:        " + df.format(total) + "đ" + "\n\n" +
                "📦  Trạng thái:      " + formatStatus(order.getOrderStatus());

        new AlertDialog.Builder(this)
                .setTitle("Thông tin đơn hàng")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private String formatStatus(OrderStatus status) {
        if (status == null) return "N/A";
        switch (status) {
            case COMPLETED:    return "✓ Completed";
            case NOT_PAYMENT:  return "✗ Not Payment";
            case ON_LOGISTIC:  return "⟳ On Logistic";
            case COMPLAIN:     return "! Complain";
            default:           return "All";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnu_order_status_all) {
            currentStatus = OrderStatus.ALL;
        } else if (item.getItemId() == R.id.mnu_str_order_status_completed) {
            currentStatus = OrderStatus.COMPLETED;
        } else if (item.getItemId() == R.id.mnu_order_status_not_payment) {
            currentStatus = OrderStatus.NOT_PAYMENT;
        } else if (item.getItemId() == R.id.mnu_order_status_on_logistic) {
            currentStatus = OrderStatus.ON_LOGISTIC;
        } else if (item.getItemId() == R.id.mnu_order_status_complain) {
            currentStatus = OrderStatus.COMPLAIN;
        } else {
            return super.onOptionsItemSelected(item);
        }

        // Lọc theo status + khoảng ngày hiện tại
        ArrayList<Order> filtered = DataWareHouse.filterOrdersByDateAndStatus(
                calFromDate.getTime(), calToDate.getTime(), currentStatus);
        reloadList(filtered);
        return true;
    }
}
