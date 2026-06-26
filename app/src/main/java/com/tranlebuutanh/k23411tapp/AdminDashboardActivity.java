package com.tranlebuutanh.k23411tapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView txtSyncStatus, txtTotalRevenue, txtTotalOrders, txtTotalProducts, txtTotalCustomers;
    private EditText edtFromDate, edtToDate;
    private Button btnSyncAll, btnFilter, btnOpenClient, btnOpenContacts;
    private ListView lvTopCustomers, lvTopProducts;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private Date filterFromDate, filterToDate;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
                btnSyncAll.setEnabled(false);
                btnSyncAll.setText("Syncing...");
                executor.execute(() -> {
                    FirebaseSyncHelper.syncAllDataToFirebase();
                    mainHandler.post(() -> {
                        btnSyncAll.setEnabled(true);
                        btnSyncAll.setText("Sync All Data to Firebase");
                        Toast.makeText(this, "Sync completed!", Toast.LENGTH_LONG).show();
                    });
                });
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
        txtTotalRevenue.setText("Loading...");
        txtTotalOrders.setText("...");

        executor.execute(() -> {
            ArrayList<Order> orders;
            if (fromDate != null && toDate != null) {
                orders = DataWareHouse.filterOrdersByDate(fromDate, toDate);
            } else {
                orders = DataWareHouse.getOrders();
            }

            ArrayList<OrderDetail> allDetails = DataWareHouse.getOrderDetails();
            ArrayList<Product> products = DataWareHouse.getProducts();
            int customerCount = DataWareHouse.getCustomers().size();

            // Pre-compute order totals using detail map
            HashMap<String, Double> orderTotals = new HashMap<>();
            for (OrderDetail d : allDetails) {
                double amount = d.getQuantity() * d.getPrice() * (1 - d.getCoupon()) * (1 + d.getVAT());
                orderTotals.put(d.getOrderId(), orderTotals.getOrDefault(d.getOrderId(), 0.0) + amount);
            }

            double totalRevenue = 0;
            HashMap<String, Double> customerSpend = new HashMap<>();
            for (Order o : orders) {
                double amt = orderTotals.getOrDefault(o.getOrderId(), 0.0);
                totalRevenue += amt;
                customerSpend.put(o.getCustomerId(), customerSpend.getOrDefault(o.getCustomerId(), 0.0) + amt);
            }

            // Top products by qty sold (only for filtered orders)
            HashMap<String, Integer> productSold = new HashMap<>();
            for (Order o : orders) {
                for (OrderDetail d : allDetails) {
                    if (d.getOrderId().equals(o.getOrderId())) {
                        productSold.put(d.getProductId(), productSold.getOrDefault(d.getProductId(), 0) + d.getQuantity());
                    }
                }
            }

            // Sort
            ArrayList<Map.Entry<String, Double>> sortedCustomers = new ArrayList<>(customerSpend.entrySet());
            Collections.sort(sortedCustomers, (a, b) -> Double.compare(b.getValue(), a.getValue()));

            ArrayList<Map.Entry<String, Integer>> sortedProducts = new ArrayList<>(productSold.entrySet());
            Collections.sort(sortedProducts, (a, b) -> Integer.compare(b.getValue(), a.getValue()));

            HashMap<String, String> productNameMap = new HashMap<>();
            for (Product p : products) {
                productNameMap.put(p.getProductId(), p.getProductName());
            }

            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            double finalTotalRevenue = totalRevenue;
            int orderSize = orders.size();

            ArrayList<String> topCustStrings = new ArrayList<>();
            int count = 0;
            for (Map.Entry<String, Double> entry : sortedCustomers) {
                if (count >= 5) break;
                topCustStrings.add((count + 1) + ". " + entry.getKey() + "  -  " + nf.format(entry.getValue()) + " VND");
                count++;
            }

            ArrayList<String> topProdStrings = new ArrayList<>();
            count = 0;
            for (Map.Entry<String, Integer> entry : sortedProducts) {
                if (count >= 5) break;
                String name = productNameMap.getOrDefault(entry.getKey(), entry.getKey());
                topProdStrings.add((count + 1) + ". " + name + "  -  " + entry.getValue() + " sold");
                count++;
            }

            mainHandler.post(() -> {
                txtTotalRevenue.setText(nf.format(finalTotalRevenue) + " VND");
                txtTotalOrders.setText(String.valueOf(orderSize));
                txtTotalProducts.setText(String.valueOf(products.size()));
                txtTotalCustomers.setText(String.valueOf(customerCount));
                lvTopCustomers.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topCustStrings));
                lvTopProducts.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topProdStrings));
            });
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
