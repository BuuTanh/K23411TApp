package com.tranlebuutanh.dals;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.tranlebuutanh.models.MyContact;

import java.util.ArrayList;

public class MyContactDAO {
    public static ArrayList<MyContact> getMyContacts(Context context) {
        ArrayList<MyContact> contacts = new ArrayList<>();
        Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor=context.getContentResolver().query(uri,
                null,null,null,null);
        while(cursor.moveToNext())
        {
            int nameindex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name=cursor.getString(nameindex);
            int phoneindex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phone=cursor.getString(phoneindex);
            MyContact contact=new MyContact(name,phone);
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }
}
