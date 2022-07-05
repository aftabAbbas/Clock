package com.aftab.clock.Utills;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApp extends Application {
    int u = 0;
    SharedPref sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = new SharedPref(this);
/*
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                ///Toast.makeText(MyApp.this, "HHH", Toast.LENGTH_SHORT).show();

             //   task();


            }
        });*/
    }

    private void task() {

        FireRef.TEST.setValue(System.currentTimeMillis() + "").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                task();

            }
        });
    }
}
