package com.orion.prointeractofficer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OfficerAuthActivity extends AppCompatActivity {
    TextView emailInputField, passwordInputField;
    Button loginBtn, signupBtn;
    RelativeLayout officerAuthLayout;

    private FirebaseAuth mAuth;

    private String email, password;

    protected void signUp(View view) {
        mAuth.createUserWithEmailAndPassword(emailInputField.getText().toString(), passwordInputField.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(OfficerAuthActivity.this, "Signed up as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Snackbar.make(officerAuthLayout, "Something went wrong", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
    }
    protected void signIn(View view) {
        mAuth.signInWithEmailAndPassword(emailInputField.getText().toString(), passwordInputField.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(OfficerAuthActivity.this, "Signed in as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Snackbar.make(officerAuthLayout, "Login issue", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        officerAuthLayout = findViewById(R.id.officerAuthLayout);
        /*if(mAuth.getCurrentUser() == null) {
            //continue with login
        } else {
            //mAuth.createUserWithEmailAndPassword()
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_auth_layout);

        emailInputField = findViewById(R.id.emailInputField);
        passwordInputField = findViewById(R.id.passwordInputField);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        //Intent intent = getIntent();
    }
}
