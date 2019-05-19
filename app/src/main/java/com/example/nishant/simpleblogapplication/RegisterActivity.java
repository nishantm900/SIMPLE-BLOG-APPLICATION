package com.example.nishant.simpleblogapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_first_name_field, reg_last_name_field, reg_email_field,reg_password_field, reg_confirm_password_field;
    private  Button reg_button, reg_login_button;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth =  FirebaseAuth.getInstance();

        reg_first_name_field =(EditText)findViewById(R.id.reg_firstName);
        reg_last_name_field =(EditText)findViewById(R.id.reg_lastName);
        reg_email_field =(EditText)findViewById(R.id.reg_email);
        reg_password_field =(EditText)findViewById(R.id.reg_password);
        reg_confirm_password_field =(EditText)findViewById(R.id.reg_confirm_password);

        reg_button =(Button)findViewById(R.id.reg_btn);
        reg_login_button =(Button)findViewById(R.id.reg_login_btn);

        reg_progress =(ProgressBar)findViewById(R.id.reg_progress);

        reg_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = reg_first_name_field.getText().toString().trim();
                String lastName = reg_last_name_field.getText().toString().trim();
                String email = reg_email_field.getText().toString().trim();
                String password = reg_password_field.getText().toString().trim();
                String confirm_password= reg_confirm_password_field.getText().toString().trim();

                if (!TextUtils.isEmpty(firstName)&& !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)&!TextUtils.isEmpty(confirm_password)){

                    if (password.equals(confirm_password)){

                        reg_progress.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                    } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "ERROR :"  + errorMessage, Toast.LENGTH_LONG).show();

                                }
                                reg_progress.setVisibility(View.INVISIBLE);

                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "CONFIRM PASSWORD AND PASSWORD FIELD DOESN'T MATCH",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();


    }
}
