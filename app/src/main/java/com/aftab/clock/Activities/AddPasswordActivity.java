package com.aftab.clock.Activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aftab.clock.Model.DeviceInfo;
import com.aftab.clock.R;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.LoadingDialog;

import java.util.HashMap;

public class AddPasswordActivity extends AppCompatActivity {

    Context context;
    EditText etFirstName, etLastName, etPassword, etCPassword;
    Button btnSave;
    String fName, lName, password, cPassword, deviceId;
    boolean valid = false;
    LoadingDialog loadingDialog;
    DeviceInfo deviceInfoIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        initUI();
        clickListeners();
        getIntentInfo();
    }


    private void initUI() {
        context = AddPasswordActivity.this;
        etFirstName = findViewById(R.id.et_fName);
        etLastName = findViewById(R.id.et_lName);
        etPassword = findViewById(R.id.et_password);
        etCPassword = findViewById(R.id.et_cPassword);
        btnSave = findViewById(R.id.btn_save);

        deviceId = Functions.getDeviceId(context);

    }

    private void clickListeners() {

        btnSave.setOnClickListener(view -> {

            fName = etFirstName.getText().toString().trim();
            lName = etLastName.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            cPassword = etCPassword.getText().toString().trim();

            valid = fieldsValidation();


            boolean bothPasswordMatch = password.equals(cPassword);


            if (valid && bothPasswordMatch) {

                saveToDB();

            } else {

                Toast.makeText(context, "Both Passwords are not same", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private Boolean fieldsValidation() {


        if (fName.equals("")) {

            etFirstName.setError(Constants.EMPTY_ERROR);

        } else if (lName.equals("")) {

            etLastName.setError(Constants.EMPTY_ERROR);

        } else if (password.equals("")) {

            etPassword.setError(Constants.EMPTY_ERROR);

        } else if (cPassword.equals("")) {

            etCPassword.setError(Constants.EMPTY_ERROR);

        } else {

            valid = true;
        }


        return valid;

    }

    private void saveToDB() {

        loadingDialog = new LoadingDialog(context, "Loading");
        loadingDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constants.FIRST_NAME, fName);
        hashMap.put(Constants.LAST_NAME, lName);
        hashMap.put(Constants.PASSWORD, cPassword);

        FireRef.DEVICES.child(deviceId)
                .updateChildren(hashMap)
                .addOnCompleteListener(task -> {

                    loadingDialog.dismiss();
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> {

            loadingDialog.dismiss();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        });


    }

    private void getIntentInfo() {

        deviceInfoIntent = (DeviceInfo) getIntent().getSerializableExtra(Constants.DEVICES);

        if (!deviceInfoIntent.getFirstName().equals("") && !deviceInfoIntent.getLastName().equals("") && !deviceInfoIntent.getPassword().equals("")) {

            showInfo(deviceInfoIntent);

        }
    }

    private void showInfo(DeviceInfo deviceInfo) {

        fName = deviceInfo.getFirstName();
        lName = deviceInfo.getLastName();
        password = deviceInfo.getPassword();
        cPassword = deviceInfo.getPassword();

        etFirstName.setText(fName);
        etLastName.setText(lName);
        etCPassword.setText(cPassword);
        etPassword.setText(password);


    }
}