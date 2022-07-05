package com.aftab.clock.Receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aftab.clock.Activities.MainActivity;

public class DialerReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {

            try {

               /* Intent intent1 = new Intent(context, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.addCategory("android.intent.category.LAUNCHER");
                context.startActivity(intent1);*/

                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_MAIN);
                intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName(context, MainActivity.class);
                intent1.setComponent(cn);
                context.startActivity(intent1);

            } catch (Exception e) {

                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


    }

}
