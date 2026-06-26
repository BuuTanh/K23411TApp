package com.tranlebuutanh.k23411tapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tranlebuutanh.models.ListUserAccount;
import com.tranlebuutanh.models.UserAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity {

    EditText edtUserName;
    EditText edtPassword;
    TextView txtMessage;
    CheckBox chkSaveInfor;
    String name_share_ref="LoginInfor";
    RadioButton radAdministrator,radEmployee,radClient;
    Button btnLogin;
    BroadcastReceiver internetStateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            if(connectivityManager!=null)
            {
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isConnected())
                {
                    btnLogin.setVisibility(View.VISIBLE);;
                }
                else
                {
                    btnLogin.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                }
            }
            else{
                btnLogin.setVisibility(View.INVISIBLE);
            }
        }
    };

    public static final String DATABASE_NAME = "K23411TSales.sqlite";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    private void copyDataBase(){
        try{
            if(CopyDBFromAsset()){
                Log.d("DB", "Copy database successful!");
            }else{
                Log.e("DB", "Copy database fail!");
            }
        }catch (Exception e){
            Log.e("Error: ", e.toString());
        }
    }

    private boolean CopyDBFromAsset() {
        String dbPath = getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if(!f.exists()){
                f.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(dbPath);
            byte[] buffer = new byte[1024]; int length;
            while((length=inputStream.read(buffer))>0){
                outputStream.write(buffer,0, length);
            }
            outputStream.flush();  outputStream.close(); inputStream.close();
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        copyDataBase(); // Phải copy xong trước khi user login
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtUserName=findViewById(R.id.edtUserName);
        edtPassword=findViewById(R.id.edtPassword);
        txtMessage=findViewById(R.id.txtMessage);
        chkSaveInfor=findViewById(R.id.chkSaveInfor);
        radAdministrator=findViewById(R.id.radAdministrator);
        radEmployee=findViewById(R.id.radEmployee);
        radClient=findViewById(R.id.radClient);
        btnLogin=findViewById(R.id.btnLogin);
    }

    public void loginSystem(View view) {
        String username=edtUserName.getText().toString();
        String pwd=edtPassword.getText().toString();
        UserAccount acc= ListUserAccount.login(username,pwd);
        if(acc!=null)
        {
            SharedPreferences preferences=getSharedPreferences(name_share_ref,MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("UserName",username);
            editor.putString("Password",pwd);
            boolean saved=chkSaveInfor.isChecked();
            editor.putBoolean("SAVED",saved);
            editor.commit();
            //dĩ nhiên còn bước cjeck quyền thực sự từ server gửi về khi đăng nhập thành công
            if(radAdministrator.isChecked())
            {
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USER_ACCOUNT", acc);
                startActivity(intent);
            }
            else if(radClient.isChecked())
            {
                Intent intent=new Intent(LoginActivity.this, ClientViewActivity.class);
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(LoginActivity.this, EmployeeAdvancedMainActivity.class);
                startActivity(intent);
            }
            txtMessage.setText(R.string.str_login_successful);
        }
        else
        {
            txtMessage.setText(getString(R.string.str_login_failed));
        }
    }


    public void exitSystem(View view) {
        //finish();
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có muốn thoát không?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences=getSharedPreferences(name_share_ref,MODE_PRIVATE);
        String username=preferences.getString("UserName","");
        String pwd=preferences.getString("Password","");
        boolean saved=preferences.getBoolean("SAVED",false);
        if(saved)
        {
            edtUserName.setText(username);
            edtPassword.setText(pwd);
        }
        chkSaveInfor.setChecked(saved);

        IntentFilter internetFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver,internetFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetStateReceiver);
    }
}