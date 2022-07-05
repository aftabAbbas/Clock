package com.aftab.clock.Utills;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.aftab.clock.R;


public class LoadingDialog extends Dialog {
    public LoadingDialog(@NonNull Context context, String text) {
        super(context);

        setContentView(R.layout.loading_dialog);

        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        TextView tv = findViewById(R.id.textView);
        tv.setText(text);

    }
}
