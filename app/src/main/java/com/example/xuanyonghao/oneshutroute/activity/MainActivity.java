package com.example.xuanyonghao.oneshutroute.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
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
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.LOGIN_ROUTE_PASSWORD_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.LOGIN_ROUTE_USERNAME_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.USERNAME_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_HOST_USERNAME_RECEIVE;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SENT_SMS_ACTION;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SEND_CONTENT;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SEND_PHONE_NUMBER;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //配置文件相关
    private SharedPreferences sharedPreferences = null;

    //控件
    private EditText hostEditText = null;
    private EditText usernameEditText = null;
    private EditText loginRouteUsernameEditText = null;
    private EditText loginRoutePasswordEditText = null;
    private Button linkButton = null;

    //连接相关
    private String host = null;
    private String username = null;
    private String routeUsername = null;
    private String routePassword = null;

    //短信相关
    private SMSSendResultReceiver smsSendResultReceiver;//短信发送广播接收器
    private SMSReceiveResultReceiver smsReceiveResultReceiver;//短信接收广播接收器
    private SmsManager smsManager = null;

    public static MainActivityHandle mainActivityHandle =  null;

    private static String[] PERMISSION1= {Manifest.permission.INTERNET};
    private static String[] PERMISSION2= {Manifest.permission.SEND_SMS};
    private static String[] PERMISSION3= {Manifest.permission.RECEIVE_SMS};
    private static String[] PERMISSION4= {Manifest.permission.READ_SMS};

    /**
     * 判断是否拥有权限
     * @param permission
     * @return
     */
    private boolean islacksOfPermission(String permission){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_DENIED;
        }
        return false;
    }

    /**
     * 检查app需要权限
     */
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
        checkPermission();//检查权限
        loadData();//加载文件配置，上一次的配置
        initLayout();//初始化控件
        initReceiver();//初始化广播接收器
        initHandle();//初始化handle
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryReceiver();//注销广播接收器
    }

    /**
     * 初始化控件
     */
    private void initLayout() {
        hostEditText = findViewById(R.id.host);
        hostEditText.setText(host);

        usernameEditText = findViewById(R.id.username);
        usernameEditText.setText(username);

        loginRouteUsernameEditText = findViewById(R.id.loginRouteUsername);
        loginRouteUsernameEditText.setText(routeUsername);

        loginRoutePasswordEditText = findViewById(R.id.loginRoutePassword);
        loginRoutePasswordEditText.setText(routePassword);

        linkButton = findViewById(R.id.link);
        linkButton.setOnClickListener(this);
    }

    /**
     * 从配置文件读取数据，上一次使用数据
     */
    private void loadData() {
        sharedPreferences = this.getSharedPreferences(CONFIG_FILE_NAME,MODE_PRIVATE);
        host = sharedPreferences.getString(HOST_KEY,DEFAULT_VALUE);
        username = sharedPreferences.getString(USERNAME_KEY,DEFAULT_VALUE);
        routeUsername = sharedPreferences.getString(LOGIN_ROUTE_USERNAME_KEY,DEFAULT_VALUE);
        routePassword = sharedPreferences.getString(LOGIN_ROUTE_PASSWORD_KEY,DEFAULT_VALUE);

    }

    /**
     * 初始化广播接收器
     */
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

    /**
     * 初始化handle
     */
    private void initHandle() {
        mainActivityHandle = new MainActivityHandle();
        mainActivityHandle.init(this);
    }

    /**
     * 注销广播接收器
     */
    private void destoryReceiver() {
        unregisterReceiver(smsSendResultReceiver);
        unregisterReceiver(smsReceiveResultReceiver);
    }

    /**
     * 读取输入控件数据
     */
    private void readInputData() {
        host = hostEditText.getText().toString();
        username = usernameEditText.getText().toString();
        routeUsername = loginRouteUsernameEditText.getText().toString();
        routePassword = loginRoutePasswordEditText.getText().toString();

    }

    /**
     * 保存数据到配置文件
     */
    private void saveData (){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HOST_KEY,host);
        editor.putString(USERNAME_KEY,username);
        editor.putString(LOGIN_ROUTE_USERNAME_KEY,routeUsername);
        editor.putString(LOGIN_ROUTE_PASSWORD_KEY,routePassword);
        editor.commit();
    }

    /**
     * 发送配置信息到handle
     */
    private void sendHostUsername() {
        Bundle data = new Bundle();
        data.putString(HOST_KEY,host);
        data.putString(USERNAME_KEY,username);
        data.putString(LOGIN_ROUTE_USERNAME_KEY,routeUsername);
        data.putString(LOGIN_ROUTE_PASSWORD_KEY,routePassword);
        Message msg = new Message();
        msg.setData(data);
        msg.what = HANDLE_HOST_USERNAME_RECEIVE;
        mainActivityHandle.sendMessage(msg);
    }

    /**
     * 发送短信获取天翼宽带密码
     */
    private void sendSms (){
        if (smsManager == null) {
            smsManager = SmsManager.getDefault();
        }
        Intent itSend = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, PendingIntent.FLAG_UPDATE_CURRENT);
        smsManager.sendTextMessage(SEND_PHONE_NUMBER,null,SEND_CONTENT,sentPI,null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.link:
                readInputData();
                saveData();
                sendHostUsername();
                sendSms();
                break;
        }
    }
}
