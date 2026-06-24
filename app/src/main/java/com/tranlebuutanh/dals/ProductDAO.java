package com.tranlebuutanh.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tranlebuutanh.models.Product;

import java.util.ArrayList;

public class ProductDAO {
    public static final String DATABASE_NAME = "K23411TSales.sqlite";
    public static final String TABLE_NAME = "Product";

    public static SQLiteDatabase database = null;

    public static ArrayList<Product> getProductsByCategory(Context context, String categoryId) {
        ArrayList<Product> products = new ArrayList<>();
        String dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE CategoryId = ?",
                new String[]{categoryId});

        while (cursor.moveToNext()) {
            String productId   = cursor.getString(0);
            String productName = cursor.getString(1);
            int quantity       = cursor.getInt(2);
            double price       = cursor.getDouble(3);
            double coupon      = cursor.getDouble(4);
            double vat         = cursor.getDouble(5);
            String cateId      = cursor.getString(6);
            products.add(new Product(productId, productName, quantity, price, coupon, vat, cateId));
        }
        cursor.close();
        return products;
    }
}
