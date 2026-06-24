package com.tranlebuutanh.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataWareHouse {
    public static ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        Category c1 = new Category("C01", "Mì các loại", "Mì chống đói");
        Category c2 = new Category("C02", "Nước uống có ga", "Nước coca");
        Category c3 = new Category("C03", "Nước uống không ga", "Nước pepsi");
        Category c4 = new Category("C04", "Rau củ quả", "Xoài");
        Category c5 = new Category("C05", "Trái cây", "Cam");
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);
        categories.add(c4);
        categories.add(c5);
        return categories;
    }

    public static ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Category> categories = getCategories();

        // C01: Mì các loại
        products.add(new Product("P01", "Mì Omachi chay", 100, 20, 0, 0.05, categories.get(0).getCategoryId()));
        products.add(new Product("P02", "Mì Hảo Hảo chua cay", 200, 15, 2, 0.05, categories.get(0).getCategoryId()));
        products.add(new Product("P03", "Mì Kokomi 90g", 150, 10, 0, 0.05, categories.get(0).getCategoryId()));
        products.add(new Product("P04", "Mì Indomie", 80, 25, 1, 0.05, categories.get(0).getCategoryId()));

        // C02: Nước uống có ga
        products.add(new Product("P05", "Coca Cola 330ml", 300, 12, 0, 0.1, categories.get(1).getCategoryId()));
        products.add(new Product("P06", "Pepsi 330ml", 250, 11, 0.5, 0.1, categories.get(1).getCategoryId()));
        products.add(new Product("P07", "7Up 330ml", 180, 12, 0, 0.1, categories.get(1).getCategoryId()));
        products.add(new Product("P08", "Mirinda Cam", 120, 12, 0, 0.1, categories.get(1).getCategoryId()));

        // C03: Nước uống không ga
        products.add(new Product("P09", "Nước khoáng Lavie 500ml", 400, 6, 0, 0, categories.get(2).getCategoryId()));
        products.add(new Product("P10", "Nước tinh khiết Aquafina 500ml", 350, 6, 0, 0, categories.get(2).getCategoryId()));
        products.add(new Product("P11", "Trà xanh Không độ", 100, 10, 0.5, 0.08, categories.get(2).getCategoryId()));
        products.add(new Product("P12", "Sữa tươi Vinamilk", 90, 30, 0, 0.05, categories.get(2).getCategoryId()));

        // C04: Rau củ quả
        products.add(new Product("P13", "Cà rốt Đà Lạt", 50, 15, 0, 0, categories.get(3).getCategoryId()));
        products.add(new Product("P14", "Khoai tây", 60, 20, 0, 0, categories.get(3).getCategoryId()));
        products.add(new Product("P15", "Cải thìa", 40, 12, 0, 0, categories.get(3).getCategoryId()));
        products.add(new Product("P16", "Cà chua", 70, 18, 0, 0, categories.get(3).getCategoryId()));

        // C05: Trái cây
        products.add(new Product("P17", "Cam sành", 100, 45, 5, 0, categories.get(4).getCategoryId()));
        products.add(new Product("P18", "Xoài cát Hòa Lộc", 30, 80, 0, 0, categories.get(4).getCategoryId()));
        products.add(new Product("P19", "Táo Envy", 45, 120, 10, 0, categories.get(4).getCategoryId()));
        products.add(new Product("P20", "Chuối già Nam Mỹ", 80, 25, 0, 0, categories.get(4).getCategoryId()));

        return products;
    }

    public static Product downloadProduct(int i)
    {
        ArrayList<Product> products = getProducts();
        if (i < 0 || i >= products.size())
            return null;
        return products.get(i);
    }
    public static ArrayList<Employee> getEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new Employee("E01", "Trần Lê Bữu Tánh", "0955792911", "Huế"));
        employees.add(new Employee("E02", "Nguyễn Văn An", "0901234567", "Hà Nội"));
        employees.add(new Employee("E03", "Trần Thị Bình", "0912345678", "Đà Nẵng"));
        employees.add(new Employee("E04", "Lê Văn Cường", "0923456789", "TP. Hồ Chí Minh"));
        employees.add(new Employee("E05", "Phạm Thị Diễm", "0934567890", "Cần Thơ"));
        employees.add(new Employee("E06", "Hoàng Văn Em", "0945678901", "Hải Phòng"));
        employees.add(new Employee("E07", "Đặng Thị Hoa", "0967890123", "Nha Trang"));
        employees.add(new Employee("E08", "Bùi Văn Giang", "0978901234", "Đà Lạt"));
        employees.add(new Employee("E09", "Võ Thị Hương", "0989012345", "Vũng Tàu"));
        employees.add(new Employee("E10", "Lý Văn Hùng", "0990123456", "Quảng Ninh"));

        return employees;
    }

    public static ArrayList<Customer> getCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String[] ho = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương"};
        String[] lot = {"Văn", "Thị", "Minh", "Anh", "Đức", "Thành", "Quốc", "Hồng", "Bửu", "Gia", "Hữu", "Kim", "Ngọc", "Tuyết", "Xuân"};
        String[] ten = {"An", "Bình", "Chi", "Dũng", "Em", "Hạnh", "Hương", "Khánh", "Linh", "Minh", "Nam", "Oanh", "Phúc", "Quang", "Sơn", "Tâm", "Thảo", "Uyên", "Việt", "Yến"};
        String[] tinh = {"Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Huế", "Cần Thơ", "Hải Phòng", "Nha Trang", "Đà Lạt", "Vũng Tàu", "Quảng Ninh", "Bình Dương", "Đồng Nai", "Long An", "Tiền Giang", "Bến Tre"};

        for (int i = 0; i < 100; i++) {
            // Dữ liệu cố định dựa trên chỉ số i để không thay đổi mỗi lần chạy
            String cusId = "Cust" + (i + 1);
            String hoTen = ho[i % ho.length] + " " + lot[i % lot.length] + " " + ten[i % ten.length];
            
            // Số điện thoại cố định: 0900000001, 0900000002...
            String phone = String.format(Locale.US, "09%08d", i + 1);
            String email = "customer" + (i + 1) + "@gmail.com";
            
            Calendar cal = Calendar.getInstance();
            // Năm sinh xoay vòng từ 1960 đến 2006
            int year = 1960 + (i % (2006 - 1960 + 1));
            int month = i % 12;
            int day = (i % 28) + 1;
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            String address = tinh[i % tinh.length];
            
            customers.add(new Customer(cusId, hoTen, phone, email, cal.getTime(), address));
        }
        return customers;
    }
    public static ArrayList<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Customer> customers = getCustomers();
        ArrayList<Employee> employees = getEmployees();

        if (customers.isEmpty() || employees.isEmpty()) return orders;

        for (int i = 0; i < 1000; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, 0, 1, 8, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            // Spread 1000 orders from 2024 to Q1 2026 (approx 821 days)
            // 821 days * 24 * 60 / 1000 approx 1182 minutes
            cal.add(Calendar.MINUTE, i * 1182);

            String orderId = "Ord" + String.format(Locale.US, "%04d", i + 1);
            String customerId = customers.get(i % customers.size()).getCusId();
            String employeeId = employees.get(i % employees.size()).getId();

            // Phân bổ trạng thái xoay vòng (bỏ qua trạng thái ALL ở index 0)
            OrderStatus status = OrderStatus.values()[(i % 4) + 1];

            orders.add(new Order(orderId, customerId, employeeId, cal.getTime(), status));
        }

        return orders;
    }
    public static ArrayList<OrderDetail> getOrderDetails()
    {
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        ArrayList<Order> orders = getOrders();
        ArrayList<Product> products = getProducts();

        int detailCounter = 1;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            // Tạo từ 1 đến 10 chi tiết cho mỗi hóa đơn (cố định theo chỉ số i)
            int numberOfDetails = (i % 10) + 1;

            for (int j = 0; j < numberOfDetails; j++) {
                // Chọn sản phẩm xoay vòng
                Product p = products.get((i + j) % products.size());

                String detailId = "Det" + String.format(Locale.US, "%05d", detailCounter++);
                int quantity = (j % 5) + 1;

                // Chia Coupon và VAT cho 100
                double coupon = p.getCoupon() / 100.0;
                double vat = p.getVAT() / 100.0;

                orderDetails.add(new OrderDetail(
                        detailId,
                        order.getOrderId(),
                        p.getProductId(),
                        quantity,
                        p.getPrice(),
                        coupon,
                        vat
                ));
            }
        }
        return orderDetails;
    }
    public static double sumOfMoneyForOrder(Order od)
    {
        double sum = 0;
        ArrayList<OrderDetail> details = getOrderDetails();
        for (OrderDetail detail : details) {
            if (detail.getOrderId().equals(od.getOrderId())) {
                // Công thức: (Số lượng * Đơn giá) * (1 - Chiết khấu) * (1 + Thuế)
                double amount = detail.getQuantity() * detail.getPrice();
                double afterCoupon = amount * (1 - detail.getCoupon());
                double finalAmount = afterCoupon * (1 + detail.getVAT());
                sum += finalAmount;
            }
        }
        return sum;
    }
    public static ArrayList<Order> filterOrdersByDate(Date fromDate, Date toDate) {
        ArrayList<Order> orders = getOrders();
        ArrayList<Order> results = new ArrayList<>();

        // Chuẩn hóa fromDate (về 0h 0m 0s 0ms)
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(fromDate);
        calFrom.set(Calendar.HOUR_OF_DAY, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.SECOND, 0);
        calFrom.set(Calendar.MILLISECOND, 0);

        // Chuẩn hóa toDate (về 0h 0m 0s 0ms)
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(toDate);
        calTo.set(Calendar.HOUR_OF_DAY, 0);
        calTo.set(Calendar.MINUTE, 0);
        calTo.set(Calendar.SECOND, 0);
        calTo.set(Calendar.MILLISECOND, 0);

        for (Order order : orders) {
            // Chuẩn hóa ngày của Order
            Calendar calOrder = Calendar.getInstance();
            calOrder.setTime(order.getOrderDate());
            calOrder.set(Calendar.HOUR_OF_DAY, 0);
            calOrder.set(Calendar.MINUTE, 0);
            calOrder.set(Calendar.SECOND, 0);
            calOrder.set(Calendar.MILLISECOND, 0);

            // So sánh chỉ ngày, tháng, năm
            if (!calOrder.before(calFrom) && !calOrder.after(calTo)) {
                results.add(order);
            }
        }
        return results;
    }
    public static ArrayList<Order> filterOrdersByStatus(OrderStatus status) {
        ArrayList<Order> orders = getOrders();
        if (status == OrderStatus.ALL) {
            return orders;
        }
        ArrayList<Order> results = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderStatus() == status) {
                results.add(order);
            }
        }
        return results;
    }

    public static ArrayList<Order> filterOrdersByDateAndStatus(Date fromDate, Date toDate, OrderStatus status) {
        ArrayList<Order> byDate = filterOrdersByDate(fromDate, toDate);
        if (status == OrderStatus.ALL) {
            return byDate;
        }
        ArrayList<Order> results = new ArrayList<>();
        for (Order order : byDate) {
            if (order.getOrderStatus() == status) {
                results.add(order);
            }
        }
        return results;
    }
}
