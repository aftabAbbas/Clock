package com.aftab.clock.Services;

import android.app.Activity;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.aftab.clock.R;
import com.aftab.clock.Utills.Functions;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class ScreenShotService extends IntentService {

    Activity activity;
    Context context;
    Dialog dialog;
    ConstraintLayout constraintLayout;
    WindowManager windowManager;
    int LAYOUT_FLAG;
    View view;
    WindowManager.LayoutParams params;

    public ScreenShotService() {
        super("ScreenShotService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        context = this;
        view = LayoutInflater.from(this).inflate(R.layout.empty_layout, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //Add the view to the window.
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(view, params);


       /* context = this;
        dialog = new Dialog(ScreenShotService.this);
        dialog.setContentView(R.layout.empty_layout);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        dialog.show();*/
        constraintLayout = view.findViewById(R.id.layout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                takeScreenshot(constraintLayout);

            }
        }, 2000);

    }


    private void takeScreenshot(ConstraintLayout constraintLayout) {

        try {

            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                    view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "" + Functions.getCurrentDate(), null);

            openScreenshot(new File(path));

            Log.d("FJJJFJF", "taken");
            // Toast.makeText(context, "taken", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            //Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("FJJJFJF", e.getMessage());
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

}