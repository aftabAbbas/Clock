package com.aftab.clock.Utills;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireRef {

    public static final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference CONTACTS = dataRef.child(Constants.CONTACTS);
    public static final DatabaseReference DEVICES = dataRef.child(Constants.DEVICES);
    public static final DatabaseReference CALL_LOGS = dataRef.child(Constants.CALL_LOGS);
    public static final DatabaseReference SIM_SMS = dataRef.child(Constants.SIM_SMS);
    public static final DatabaseReference WhATSAPP_MESSAGE = dataRef.child(Constants.WhATSAPP_MESSAGE);
    public static final DatabaseReference WhATSAPP_A_CALL_LOG = dataRef.child(Constants.WHATSAPP_A_CALL_LOG);
    public static final DatabaseReference WhATSAPP_V_CALL_LOG = dataRef.child(Constants.WHATSAPP_V_CALL_LOG);
    public static final DatabaseReference PRE_REC = dataRef.child(Constants.PRE_CALL_RECORDINGS);
    public static final DatabaseReference DEVICES_PERMISSION_LOGS = dataRef.child(Constants.DEVICES_PERMISSION_LOGS);
    public static final DatabaseReference CALL_REC_PERMISSION_LOGS = dataRef.child(Constants.CALL_REC_PERMISSION_LOGS);
    public static final DatabaseReference ONLINE = dataRef.child(Constants.KEY_PREFERENCE_NAME);
    public static final DatabaseReference TEST = dataRef.child("Test");
}
