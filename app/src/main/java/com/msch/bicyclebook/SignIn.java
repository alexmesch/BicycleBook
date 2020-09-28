package com.msch.bicyclebook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends ProgressActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button register_button, login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.pswrd_field);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("firebase login: ", "onAuthStateChanged: signed_in");
                }
                else {
                    Log.d("firebase login: ", "onAuthStateChanged: signed_out");
                }
                //updateUI(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void register(View v) {
        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        register_button.setVisibility(View.INVISIBLE);
        login_button.setVisibility(View.VISIBLE);
    }

    public void login(View v) {
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    public void switchToRegister(View v) {
        register_button.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.INVISIBLE);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d("creatingAccountProcess:", "createAccount: " + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("CreateUser", "createUserWithEmail:onComplete" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),R.string.auth_failed,Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
        finish();
    }

    private void signIn(String email, String password) {
        Log.d("Login", "signIn: " + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login", "signInWithEmail: onComplete: " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Login", "signInWithEmail: failed ", task.getException());
                            Toast.makeText(getApplicationContext(),R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Вход выполнен успешно",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void returnToMaps(View v) {
        finish();
    }
}
