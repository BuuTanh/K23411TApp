package com.tranlebuutanh.k23411tapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tranlebuutanh.models.Category;
import com.tranlebuutanh.models.Customer;
import com.tranlebuutanh.models.DataWareHouse;
import com.tranlebuutanh.models.Employee;
import com.tranlebuutanh.models.Order;
import com.tranlebuutanh.models.OrderDetail;
import com.tranlebuutanh.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirebaseSyncHelper {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    public static void syncAllDataToFirebase() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        ArrayList<Category> categories = DataWareHouse.getCategories();
        for (Category c : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryName", c.getCategoryName());
            map.put("description", c.getDescription());
            db.child("categories").child(c.getCategoryId()).setValue(map);
        }

        ArrayList<Product> products = DataWareHouse.getProducts();
        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("productName", p.getProductName());
            map.put("price", p.getPrice());
            map.put("stock", p.getQuantity());
            map.put("categoryId", p.getCateId());
            map.put("isActive", true);
            db.child("products").child(p.getProductId()).setValue(map);
        }

        ArrayList<Customer> customers = DataWareHouse.getCustomers();
        for (Customer c : customers) {
            Map<String, Object> map = new HashMap<>();
            map.put("cusName", c.getCusName());
            map.put("phone", c.getPhone());
            map.put("email", c.getEmail());
            map.put("address", c.getAddress());
            if (c.getBirthday() != null) {
                map.put("birthday", sdf.format(c.getBirthday()));
            }
            db.child("customers").child(c.getCusId()).setValue(map);
        }

        ArrayList<Employee> employees = DataWareHouse.getEmployees();
        for (Employee e : employees) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", e.getName());
            map.put("phone", e.getPhone());
            if (e.getBirthPlace() != null) {
                map.put("birthPlace", e.getBirthPlace());
            }
            db.child("employees").child(e.getId()).setValue(map);
        }

        // Sync first 50 orders to avoid timeout
        ArrayList<Order> orders = DataWareHouse.getOrders();
        ArrayList<OrderDetail> allDetails = DataWareHouse.getOrderDetails();

        // Pre-compute order totals
        HashMap<String, Double> orderTotals = new HashMap<>();
        for (OrderDetail d : allDetails) {
            double amount = d.getQuantity() * d.getPrice() * (1 - d.getCoupon()) * (1 + d.getVAT());
            orderTotals.put(d.getOrderId(), orderTotals.getOrDefault(d.getOrderId(), 0.0) + amount);
        }

        int orderLimit = Math.min(orders.size(), 50);
        for (int i = 0; i < orderLimit; i++) {
            Order o = orders.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("customerId", o.getCustomerId());
            map.put("employeeId", o.getEmployeeId());
            map.put("orderDate", sdf.format(o.getOrderDate()));
            map.put("status", o.getOrderStatus() != null ? o.getOrderStatus().name() : "Completed");
            map.put("totalAmount", orderTotals.getOrDefault(o.getOrderId(), 0.0));
            db.child("orders").child(o.getOrderId()).setValue(map);
        }

        // Sync order details for those 50 orders
        for (OrderDetail d : allDetails) {
            // Check if this detail belongs to one of the first 50 orders
            for (int i = 0; i < orderLimit; i++) {
                if (d.getOrderId().equals(orders.get(i).getOrderId())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("orderId", d.getOrderId());
                    map.put("productId", d.getProductId());
                    map.put("quantity", d.getQuantity());
                    map.put("price", d.getPrice());
                    map.put("coupon", d.getCoupon());
                    map.put("VAT", d.getVAT());
                    db.child("orderDetails").child(d.getOrderDetailId()).setValue(map);
                    break;
                }
            }
        }
    }
}
