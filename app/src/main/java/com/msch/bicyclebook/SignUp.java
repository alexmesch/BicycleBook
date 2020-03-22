package com.msch.bicyclebook;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class SignUp extends SignIn {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText login_field;
    private EditText pswrd_field;
    private EditText repeat_pswrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        login_field = findViewById(R.id.login_field);
        pswrd_field = findViewById(R.id.pswrd_field);
        repeat_pswrd = findViewById(R.id.repeat_pswrd_field);

        firebaseInit();
    }

    public void firebaseInit() {
        FirebaseApp.initializeApp(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    public void registerNewUser(View v){
        User user = new User(UUID.randomUUID().toString(),
                login_field.getText().toString(),
                pswrd_field.getText().toString());
        mDatabaseReference.child("users").child(user.getUid()).setValue(user);
        clearEditText();
        returnToMaps(v);
    }

    void clearEditText() {
        login_field.setText("");
        pswrd_field.setText("");
        repeat_pswrd.setText("");
        Toast.makeText(this,"Успешно!", Toast.LENGTH_SHORT).show();
    }
}
