package com.msch.bicyclebook;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

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
    private AppCompatCheckBox hidePswrdCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.pswrd_field);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
        hidePswrdCheckbox =findViewById(R.id.hidePasswordBtn);

        hidePswrdCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    hidePswrdCheckbox.setBackgroundResource(R.drawable.show_password);
                    showPassword();
                } else {
                    hidePswrdCheckbox.setBackgroundResource(R.drawable.hide_password);
                    hidePassword();
                }
            }
        });


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

    public void showPassword() {
        mPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public void hidePassword() {
        mPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void register(View v) {
        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    public void login(View v) {
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Обязательное поле");
            valid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Некорретный email");
            valid = false;
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Обязательное поле");
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
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                            Log.d("CreateUser", "createUserWithEmail:onComplete" + task.isSuccessful());
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Не удалось создать пользователя",Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
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
}
