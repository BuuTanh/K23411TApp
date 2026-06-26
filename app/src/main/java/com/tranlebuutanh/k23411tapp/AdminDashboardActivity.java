package com.tranlebuutanh.k23411tapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.tranlebuutanh.models.DataWareHouse;
import com.tranlebuutanh.models.Order;
import com.tranlebuutanh.models.OrderDetail;
import com.tranlebuutanh.models.Product;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView txtSyncStatus, txtTotalRevenue, txtTotalOrders, txtTotalProducts, txtTotalCustomers;
    private EditText edtFromDate, edtToDate;
    private Button btnSyncAll, btnFilter, btnOpenClient, btnOpenContacts;
    private ListView lvTopCustomers, lvTopProducts;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private Date filterFromDate, filterToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupEvents();
        loadDashboard(null, null);
        checkFirebaseStatus();
    }

    private void initViews() {
        txtSyncStatus = findViewById(R.id.txtSyncStatus);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtTotalCustomers = findViewById(R.id.txtTotalCustomers);
        edtFromDate = findViewById(R.id.edtFromDate);
        edtToDate = findViewById(R.id.edtToDate);
        btnSyncAll = findViewById(R.id.btnSyncAll);
        btnFilter = findViewById(R.id.btnFilter);
        btnOpenClient = findViewById(R.id.btnOpenClient);
        btnOpenContacts = findViewById(R.id.btnOpenContacts);
        lvTopCustomers = findViewById(R.id.lvTopCustomers);
        lvTopProducts = findViewById(R.id.lvTopProducts);
    }

    private void setupEvents() {
        edtFromDate.setOnClickListener(v -> showDatePicker(true));
        edtToDate.setOnClickListener(v -> showDatePicker(false));

        btnFilter.setOnClickListener(v -> {
            if (filterFromDate != null && filterToDate != null) {
                loadDashboard(filterFromDate, filterToDate);
            } else {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
            }
        });

        btnSyncAll.setOnClickListener(v -> {
            if (isOnline()) {
                Toast.makeText(this, "Syncing all data to Firebase...", Toast.LENGTH_SHORT).show();
                FirebaseSyncHelper.syncAllDataToFirebase();
                Toast.makeText(this, "Sync completed!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        btnOpenClient.setOnClickListener(v -> startActivity(new Intent(this, ClientViewActivity.class)));
        btnOpenContacts.setOnClickListener(v -> startActivity(new Intent(this, FirebaseContactActivity.class)));
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            selected.set(Calendar.MILLISECOND, 0);
            if (isFrom) {
                filterFromDate = selected.getTime();
                edtFromDate.setText(sdf.format(filterFromDate));
            } else {
                filterToDate = selected.getTime();
                edtToDate.setText(sdf.format(filterToDate));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadDashboard(Date fromDate, Date toDate) {
        ArrayList<Order> orders;
        if (fromDate != null && toDate != null) {
            orders = DataWareHouse.filterOrdersByDate(fromDate, toDate);
        } else {
            orders = DataWareHouse.getOrders();
        }

        ArrayList<OrderDetail> allDetails = DataWareHouse.getOrderDetails();
        ArrayList<Product> products = DataWareHouse.getProducts();

        // Total Revenue
        double totalRevenue = 0;
        for (Order o : orders) {
            totalRevenue += DataWareHouse.sumOfMoneyForOrder(o);
        }
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        txtTotalRevenue.setText(nf.format(totalRevenue) + " VND");
        txtTotalOrders.setText(String.valueOf(orders.size()));
        txtTotalProducts.setText(String.valueOf(products.size()));
        txtTotalCustomers.setText(String.valueOf(DataWareHouse.getCustomers().size()));

        // Top Customers by spending
        HashMap<String, Double> customerSpend = new HashMap<>();
        for (Order o : orders) {
            double amount = DataWareHouse.sumOfMoneyForOrder(o);
            customerSpend.put(o.getCustomerId(), customerSpend.getOrDefault(o.getCustomerId(), 0.0) + amount);
        }

        ArrayList<Map.Entry<String, Double>> sortedCustomers = new ArrayList<>(customerSpend.entrySet());
        Collections.sort(sortedCustomers, (a, b) -> Double.compare(b.getValue(), a.getValue()));

        ArrayList<String> topCustomerStrings = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : sortedCustomers) {
            if (count >= 5) break;
            topCustomerStrings.add((count + 1) + ". " + entry.getKey() + "  -  " + nf.format(entry.getValue()) + " VND");
            count++;
        }
        lvTopCustomers.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topCustomerStrings));

        // Top Products by quantity sold
        HashMap<String, Integer> productSold = new HashMap<>();
        for (Order o : orders) {
            for (OrderDetail d : allDetails) {
                if (d.getOrderId().equals(o.getOrderId())) {
                    productSold.put(d.getProductId(), productSold.getOrDefault(d.getProductId(), 0) + d.getQuantity());
                }
            }
        }

        ArrayList<Map.Entry<String, Integer>> sortedProducts = new ArrayList<>(productSold.entrySet());
        Collections.sort(sortedProducts, (a, b) -> Integer.compare(b.getValue(), a.getValue()));

        HashMap<String, String> productNameMap = new HashMap<>();
        for (Product p : products) {
            productNameMap.put(p.getProductId(), p.getProductName());
        }

        ArrayList<String> topProductStrings = new ArrayList<>();
        count = 0;
        for (Map.Entry<String, Integer> entry : sortedProducts) {
            if (count >= 5) break;
            String name = productNameMap.getOrDefault(entry.getKey(), entry.getKey());
            topProductStrings.add((count + 1) + ". " + name + "  -  " + entry.getValue() + " sold");
            count++;
        }
        lvTopProducts.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topProductStrings));
    }

    private void checkFirebaseStatus() {
        if (isOnline()) {
            txtSyncStatus.setText("Firebase: Online");
            DatabaseReference connRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    txtSyncStatus.setText(Boolean.TRUE.equals(connected) ? "Firebase: Connected" : "Firebase: Disconnected");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            txtSyncStatus.setText("Firebase: Offline");
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
