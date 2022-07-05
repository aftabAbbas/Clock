package com.aftab.clock.Receivers;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class SmsObserver extends ContentObserver {

    Context context;

    public SmsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Uri smsuri = Uri.parse("content://sms/sent");
        Cursor cursor = context.getContentResolver().query(smsuri, null, null, null, null);
        String outgoingSMS = null;

        if (cursor != null && cursor.moveToFirst()) {

            try {

                cursor.moveToFirst();
                String type = cursor.getString(cursor.getColumnIndex("type"));

                if (type.equals("2")) {

                    outgoingSMS = cursor.getString(cursor.getColumnIndex("address"));
                    String subString = outgoingSMS.substring(0, 3);
                }

            } catch (CursorIndexOutOfBoundsException e) {
               // Toast.makeText(context, "1" + e, Toast.LENGTH_SHORT).show();

            } finally {
                cursor.close();
            }

            //Filter out duplicate ids here.

           // Toast.makeText(context, "" + outgoingSMS, Toast.LENGTH_SHORT).show();
        }
    }


}