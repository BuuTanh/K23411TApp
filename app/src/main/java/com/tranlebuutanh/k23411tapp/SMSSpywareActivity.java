package com.tranlebuutanh.k23411tapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

public class SMSSpywareActivity extends AppCompatActivity {

    TextView txtPhoneIncoming;
    TextView txtDateDelivery;
    TextView txtMessageBody;

    BroadcastReceiver smsReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if(bundle!=null)
            {
                Object[] pdus=(Object[]) bundle.get("pdus");
                if(pdus.length>0)
                {
                    SmsMessage[] messages=new SmsMessage[pdus.length];
                    for(int i=0;i<pdus.length;i++)
                    {
                        messages[i]=SmsMessage.createFromPdu((byte[]) pdus[i]);
                        txtPhoneIncoming.setText(messages[i].getOriginatingAddress());
                        Date date=new Date(messages[i].getTimestampMillis());
                        txtDateDelivery.setText(date.toString());
                        txtMessageBody.setText(messages[i].getMessageBody());
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_smsspyware);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtPhoneIncoming=findViewById(R.id.txtPhoneIncoming);
        txtDateDelivery=findViewById(R.id.txtDateDelivery);
        txtMessageBody=findViewById(R.id.txtMessageBody);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter smsFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver,smsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}