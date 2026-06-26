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

        // Categories
        ArrayList<Category> categories = DataWareHouse.getCategories();
        for (Category c : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryName", c.getCategoryName());
            map.put("description", c.getDescription());
            db.child("categories").child(c.getCategoryId()).setValue(map);
        }

        // Products
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

        // Customers
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

        // Employees
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

        // Orders
        ArrayList<Order> orders = DataWareHouse.getOrders();
        for (Order o : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("customerId", o.getCustomerId());
            map.put("employeeId", o.getEmployeeId());
            map.put("orderDate", sdf.format(o.getOrderDate()));
            map.put("status", o.getOrderStatus() != null ? o.getOrderStatus().name() : "Completed");
            map.put("totalAmount", DataWareHouse.sumOfMoneyForOrder(o));
            db.child("orders").child(o.getOrderId()).setValue(map);
        }

        // OrderDetails
        ArrayList<OrderDetail> details = DataWareHouse.getOrderDetails();
        for (OrderDetail d : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", d.getOrderId());
            map.put("productId", d.getProductId());
            map.put("quantity", d.getQuantity());
            map.put("price", d.getPrice());
            map.put("coupon", d.getCoupon());
            map.put("VAT", d.getVAT());
            db.child("orderDetails").child(d.getOrderDetailId()).setValue(map);
        }
    }
}
