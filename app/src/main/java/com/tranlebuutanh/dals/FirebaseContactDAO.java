package com.tranlebuutanh.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tranlebuutanh.models.FirebaseContact;

import java.util.ArrayList;

public class FirebaseContactDAO {
    public static final String DATABASE_NAME = "K23411TSales.sqlite";
    public static final String TABLE_NAME = "FirebaseContact";
    private static SQLiteDatabase database = null;

    private static void openDatabase(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "syncStatus TEXT DEFAULT 'SYNCED'" +
                ")");
    }

    public static ArrayList<FirebaseContact> getContacts(Context context) {
        ArrayList<FirebaseContact> contacts = new ArrayList<>();
        try {
            openDatabase(context);
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String email = cursor.getString(3);
                String syncStatus = cursor.getString(4);
                contacts.add(new FirebaseContact(id, name, phone, email, syncStatus));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
        return contacts;
    }

    public static ArrayList<FirebaseContact> getPendingContacts(Context context) {
        ArrayList<FirebaseContact> contacts = new ArrayList<>();
        try {
            openDatabase(context);
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE syncStatus = ?",
                    new String[]{FirebaseContact.PENDING});
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                String email = cursor.getString(3);
                String syncStatus = cursor.getString(4);
                contacts.add(new FirebaseContact(id, name, phone, email, syncStatus));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
        return contacts;
    }

    public static void saveContact(Context context, FirebaseContact contact) {
        try {
            openDatabase(context);
            ContentValues values = new ContentValues();
            values.put("id", contact.getId());
            values.put("name", contact.getName());
            values.put("phone", contact.getPhone());
            values.put("email", contact.getEmail());
            values.put("syncStatus", contact.getSyncStatus());
            database.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public static void markSynced(Context context, String id) {
        try {
            openDatabase(context);
            ContentValues values = new ContentValues();
            values.put("syncStatus", FirebaseContact.SYNCED);
            database.update(TABLE_NAME, values, "id = ?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public static void clearAll(Context context) {
        try {
            openDatabase(context);
            database.delete(TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }
}
