package com.tranlebuutanh.models;

import java.util.ArrayList;

public class ListUserAccount {
    public static ArrayList<UserAccount> getUserAccounts() {
        ArrayList<UserAccount> database= new ArrayList<>();
        database.add(new UserAccount("admin", "123", "Administrator",
                "Tánh", true));
        database.add(new UserAccount("u1", "123", "Employee",
                "Ny", true));
        database.add(new UserAccount("u2", "123", "Reporter",
                "Phúc", true));
        return database;
    }
    public static UserAccount login(String username, String password)
    {
            //step1: query database
            ArrayList<UserAccount> database= getUserAccounts();
            //step2: check username and password
            for (UserAccount acc : database)
            {
                if (acc.getUserName().equalsIgnoreCase(username) && acc.getPassword().equals(password))
                {
                    return acc;
                }
            }
            return null;
    }
}
