package com.example.xuanyonghao.oneshutroute.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xuanyonghao.oneshutroute.handler.MainActivityHandle;
import com.example.xuanyonghao.oneshutroute.R;
import com.example.xuanyonghao.oneshutroute.receiver.SMSReceiveResultReceiver;
import com.example.xuanyonghao.oneshutroute.receiver.SMSSendResultReceiver;

import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.CONFIG_FILE_NAME;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.DEFAULT_VALUE;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.HOST_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.USERNAME_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SENT_SMS_ACTION;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SEND_CONTENT;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SEND_PHONE_NUMBER;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //配置文件相关
    private SharedPreferences sharedPreferences = null;

    //控件
    private EditText hostEditText = null;
    private EditText usernameEditText = null;
    private Button linkButton = null;

    //短信相关
    private String host = null;
    private String username = null;
    private SMSSendResultReceiver smsSendResultReceiver;//短信发送广播接收器
    private SMSReceiveResultReceiver smsReceiveResultReceiver;//短信接收广播接收器

    public static MainActivityHandle mainActivityHandle =  null;

    private static String[] PERMISSION1= {Manifest.permission.INTERNET};
    private static String[] PERMISSION2= {Manifest.permission.SEND_SMS};
    private static String[] PERMISSION3= {Manifest.permission.RECEIVE_SMS};
    private static String[] PERMISSION4= {Manifest.permission.READ_SMS};

    private  boolean islacksOfPermission(String permission){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_DENIED;
        }
        return false;
    }

    private void checkPermission() {
        if(islacksOfPermission(PERMISSION1[0])){
            ActivityCompat.requestPermissions(this,PERMISSION1,0x12);
        }
        if(islacksOfPermission(PERMISSION2[0])){
            ActivityCompat.requestPermissions(this,PERMISSION2,0x13);
        }
        if(islacksOfPermission(PERMISSION3[0])){
            ActivityCompat.requestPermissions(this,PERMISSION3,0x14);
        }
        if(islacksOfPermission(PERMISSION4[0])){
            ActivityCompat.requestPermissions(this,PERMISSION4,0x15);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        loadData();
        initLayout();
        initReceiver();
        initHandle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryReceiver();
    }

    private void initLayout() {
        hostEditText = findViewById(R.id.host);
        hostEditText.setText(host);

        usernameEditText = findViewById(R.id.username);
        usernameEditText.setText(username);

        linkButton = findViewById(R.id.link);
        linkButton.setOnClickListener(this);
    }

    private void loadData() {
        sharedPreferences = this.getSharedPreferences(CONFIG_FILE_NAME,MODE_PRIVATE);
        host = sharedPreferences.getString(HOST_KEY,DEFAULT_VALUE);
        username = sharedPreferences.getString(USERNAME_KEY,DEFAULT_VALUE);

    }

    private void initReceiver() {
        smsSendResultReceiver = new SMSSendResultReceiver();
        IntentFilter intentFilter = new IntentFilter(SENT_SMS_ACTION);
        registerReceiver(smsSendResultReceiver,intentFilter);

        smsReceiveResultReceiver = new SMSReceiveResultReceiver();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter2.setPriority(100);
        registerReceiver(smsReceiveResultReceiver,intentFilter2);
    }

    private void initHandle() {
        mainActivityHandle = new MainActivityHandle();
        mainActivityHandle.init(this);
    }

    private void sendHostUsername(String host, String username) {
        Bundle data = new Bundle();
        data.putString(HOST_KEY,host);
        data.putString(USERNAME_KEY,username);
    }

    private void destoryReceiver() {
        unregisterReceiver(smsSendResultReceiver);

        unregisterReceiver(smsReceiveResultReceiver);
    }


    private void saveData (){
        host = hostEditText.getText().toString();
        username = usernameEditText.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HOST_KEY,host);
        editor.putString(USERNAME_KEY,username);
        editor.commit();
    }

    private void sendSms (){
        SmsManager smsManager = SmsManager.getDefault();
        Intent itSend = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, PendingIntent.FLAG_UPDATE_CURRENT);
        smsManager.sendTextMessage(SEND_PHONE_NUMBER,null,SEND_CONTENT,sentPI,null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.link:
                saveData();
                sendHostUsername(host,username);
                sendSms();
                break;
        }
    }
}
