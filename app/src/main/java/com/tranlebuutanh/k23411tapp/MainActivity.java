package com.tranlebuutanh.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.models.Order;
import com.tranlebuutanh.models.UserAccount;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtWelcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtWelcome=findViewById(R.id.txtWelcome);
        //Step 1: get intent from Login:
        Intent intent=getIntent();
        //Step 2: get data from intent
        UserAccount acc= (UserAccount) intent.getSerializableExtra("USER_ACCOUNT");
        if(acc!=null)
        {
            txtWelcome.setText("Xin chào, " + acc.getDisplayName() + "!");
        }
    }

    public void say_hello(View view) {
        Toast.makeText( this,
         "Hello K23411T",
        Toast.LENGTH_LONG).show();
    }

    public void exit_app(View view){
        finish();
    }

    public void show_my_major(View view) {
        //String my_major="Data Science!!!";
        String my_major=getString(R.string.str_my_major);
        Toast.makeText(this,my_major,Toast.LENGTH_LONG).show();
    }

    public void openCalculatorApp(View view) {
        Intent intent=new Intent(MainActivity.this,CalculatorActivity.class);
        startActivity(intent);
    }

    public void openOrderManagement(View view) {
        Intent intent=new Intent(MainActivity.this,OrderManagementActivity.class);
        startActivity(intent);
    }

    public void openCategoryManagement(View view) {
        Intent intent=new Intent(MainActivity.this,CategoryActivity.class);
        startActivity(intent);
    }

    public void openMyContactManagement(View view) {
        Intent intent=new Intent(MainActivity.this,MyContactActivity.class);
        startActivity(intent);
    }

    public void openSMSSpywareActivity(View view) {
        Intent intent=new Intent(MainActivity.this,SMSSpywareActivity.class);
        startActivity(intent);
    }

    public void openMultiThreadActivity(View view) {
        Intent intent=new Intent(MainActivity.this,MultiThreadActivity.class);
        startActivity(intent);
    }

    public void openMultiThreadObjectActivity(View view) {
        Intent intent=new Intent(MainActivity.this,MultiThreadObjectActivity.class);
        startActivity(intent);
    }

    public void openMyUELSearch(View view) {
        Intent intent = new Intent(MainActivity.this, MyUELSearchActivity.class);
        startActivity(intent);
    }

    public void openWeatherActivity(View view) {
        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
        startActivity(intent);
    }

    public void openFirebaseContactActivity(View view) {
        Intent intent = new Intent(MainActivity.this, FirebaseContactActivity.class);
        startActivity(intent);
    }

    public void openFontAndMusicActivity(View view) {
        Intent intent = new Intent(MainActivity.this, FontAndMusicActivity.class);
        startActivity(intent);
    }
}