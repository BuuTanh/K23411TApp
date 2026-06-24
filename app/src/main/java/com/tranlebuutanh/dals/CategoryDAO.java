package com.tranlebuutanh.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tranlebuutanh.models.Category;

import java.util.ArrayList;

public class CategoryDAO {
    public static final String DATABASE_NAME = "K23411TSales.sqlite";
    public static final String TABLE_NAME = "Category";

    public static SQLiteDatabase database = null;

    public static ArrayList<Category> getCategories(Context context) {
        ArrayList<Category> categories = new ArrayList<>();
        String dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME,
               null);
        while(cursor.moveToNext()){
            String cateId = cursor.getString(0);
            String cateName = cursor.getString(1);
            String description = cursor.getString(2);
            Category c = new Category(cateId, cateName, description);
            categories.add(c);
            //To do something ….
        }
        cursor.close();

        return categories;
    }
    public static long saveNewCategory(Context context,Category category)
    {
        long result=-1;
        database=context.openOrCreateDatabase(DATABASE_NAME,
                Context.MODE_PRIVATE,null);

        ContentValues values=new ContentValues();
        values.put("CategoryId",category.getCategoryId());
        values.put("CategoryName",category.getCategoryName());
        values.put("Description",category.getDescription());
        result=database.insert(TABLE_NAME,null,values);

        return result;
    }
    public static long removeCategory(Context context,Category category)
    {
        long result=-1;
        database=context.openOrCreateDatabase(DATABASE_NAME,
                Context.MODE_PRIVATE,null);

        result=database.delete(TABLE_NAME,
                "CategoryId=?",
                new String[]{category.getCategoryId()});

        return result;
    }
}
