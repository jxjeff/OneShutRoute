package com.example.xuanyonghao.oneshutroute.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.xuanyonghao.oneshutroute.activity.MainActivity;

import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_MESSAGE_KEY;
import static com.example.xuanyonghao.oneshutroute.common.Config.MAIN_HANDLE.HANDLE_RECEIVED_SMS_CODE;
import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SEND_PHONE_NUMBER;


/**
 * Created by xuanyonghao on 2017/10/29.
 */

public class SMSReceiveResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "天翼密码获取成功！", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        StringBuffer messageContent = new StringBuffer();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (Object pdu : pdus) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = message.getOriginatingAddress();
                if (SEND_PHONE_NUMBER.equals(sender)) {
                    messageContent.append(message.getMessageBody());
                }
            }
        }
        Bundle data = new Bundle();
        data.putString(HANDLE_MESSAGE_KEY,messageContent.toString());
        Message message = new Message();
        message.what = HANDLE_RECEIVED_SMS_CODE;
        message.setData(data);
        MainActivity.mainActivityHandle.sendMessage(message);
    }
}
