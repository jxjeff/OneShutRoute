package com.example.xuanyonghao.oneshutroute.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.DEFAULT_VALUE;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.HOST_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.LINK_CONFIG.USERNAME_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_HOST_USERNAME_RECEIVE;
import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_MESSAGE_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_RECEIVED_SMS_CODE;


/**
 * Created by xuanyonghao on 2017/10/29.
 */

public class MainActivityHandle extends Handler {

    private Context mContext;
    private String host;
    private String username;
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_RECEIVED_SMS_CODE:
                String content = msg.getData().getString(HANDLE_MESSAGE_KEY);
                String code = parser(content);
                if (code != null) {
                    Log.d(HANDLE_MESSAGE_KEY, code);
                    link(code);
                } else {
                    Toast.makeText(mContext,"程序匹配密码出错，请联系程序猿！",Toast.LENGTH_LONG).show();
                }
                break;
            case HANDLE_HOST_USERNAME_RECEIVE:
                Bundle data = msg.getData();
                host = data.getString(HOST_KEY,DEFAULT_VALUE);
                username = data.getString(USERNAME_KEY,DEFAULT_VALUE);
                break;
        }
    }

    public void init(Context mContext) {
        this.mContext = mContext;
    }

    private String parser(String content) {
        String reg = "[0-9]+";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    private void link (final String code){
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();
                Toast.makeText(mContext,"正在登陆后台！-----",Toast.LENGTH_SHORT).show();
                String url = host+"/cgi-bin/luci";//登陆url
                RequestBody requestBody = new FormBody.Builder()
                        .add("username1","root")
                        .add("password1","admin")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);
                String cookie = null;
                try {
                    Response response = call.execute();
                    Toast.makeText(mContext,"设置天翼密码！-----",Toast.LENGTH_SHORT).show();
                    String tmp = response.header("Set-Cookie");
                    String stoke = tmp.substring(tmp.indexOf("path=")+5,tmp.length());
                    url = host+stoke+"/admin/network/network/wan";//设置天翼密码并保存url
                    cookie = tmp.substring(0,tmp.indexOf(";"));
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("cbi.submit","1")
                            .addFormDataPart("tab.network.wan","general")
                            .addFormDataPart("cbid.network.wan._fwzone","wan")
                            .addFormDataPart("cbid.network.wan._fwzone.newzone","")
                            .addFormDataPart("cbi.cbe.network.wan.ifname_single","1")
                            .addFormDataPart("cbid.network.wan.ifname_single","eth1")
                            .addFormDataPart("cbid.network.wan.proto","pppoe")
                            .addFormDataPart("cbid.network.wan.username",username)
                            .addFormDataPart("cbid.network.wan.password",code)
                            .addFormDataPart("cbid.network.wan.dialtype","2")
                            .addFormDataPart("cbi.cbe.network.wan.autoredial","1")
                            .addFormDataPart("cbi.cbe.network.wan.randommac","1")
                            .addFormDataPart("cbid.network.wan.ac","")
                            .addFormDataPart("cbid.network.wan.service","")
                            .addFormDataPart("cbi.cbe.network.wan.auto","1")
                            .addFormDataPart("cbid.network.wan.auto","1")
                            .addFormDataPart("cbi.cbe.network.wan.defaultroute","1")
                            .addFormDataPart("cbid.network.wan.defaultroute","1")
                            .addFormDataPart("cbid.network.wan.metric","1")
                            .addFormDataPart("cbi.cbe.network.wan.peerdns","1")
                            .addFormDataPart("cbid.network.wan.peerdns","1")
                            .addFormDataPart("cbid.network.wan._keepalive_failure","")
                            .addFormDataPart("cbid.network.wan._keepalive_interval","")
                            .addFormDataPart("cbid.network.wan.demand","")
                            .addFormDataPart("cbid.network.wan.mtu","")
                            .addFormDataPart("cbid.network.wan.macaddr","")
                            .addFormDataPart("cbi.apply",new String("保存&应用".getBytes("utf-8"),"utf-8"))
                            .build();
                    request = new Request.Builder()
                            .url(url)
                            .header("Cookie",cookie)
                            .header("Referer",url)
                            .post(requestBody)
                            .build();
                    call = okHttpClient.newCall(request);
                    response = call.execute();
                    Toast.makeText(mContext,"正在连接！-----",Toast.LENGTH_SHORT).show();
                    url = host+stoke+"/admin/network/iface_reconnect/wan?_="+new Random().nextDouble();//点击连接url
                    request = new Request.Builder()
                            .url(url)
                            .header("Cookie",cookie)
                            .get()
                            .build();
                    call = okHttpClient.newCall(request);
                    response = call.execute();
                    Toast.makeText(mContext,"成功！-----",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
