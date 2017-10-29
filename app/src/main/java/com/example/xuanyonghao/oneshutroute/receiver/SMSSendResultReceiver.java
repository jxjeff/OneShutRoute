package com.example.xuanyonghao.oneshutroute.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import static com.example.xuanyonghao.oneshutroute.common.Config.SMS_CONFIG.SENT_SMS_ACTION;

/**
 * Created by xuanyonghao on 2017/10/29.
 */

public class SMSSendResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SENT_SMS_ACTION.equals(intent.getAction())) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    // 发送成功
                    Toast.makeText(context, "短信发送成功！", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                default:
                    // 发送失败
                    Toast.makeText(context, "短信发送失败！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
