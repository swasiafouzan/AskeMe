package com.example.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextInputLayout t1, t2;
    ProgressBar bar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        t1 = (TextInputLayout) findViewById(R.id.email);
        t2 = (TextInputLayout) findViewById(R.id.password);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        //Added from mAuth till finish();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            Intent intent = new Intent(MainActivity.this, DashBoard.class);
            startActivity(intent);
            finish();
        }
    }
    public void gotosignin(View view) {
        startActivity(new Intent(MainActivity.this, login.class));
    }
    public void signuphere(View view) {
        bar.setVisibility(View.VISIBLE);
        String email = t1.getEditText().getText().toString();
        String password = t2.getEditText().getText().toString();

        //Initialize FireBase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            t1.getEditText().setText("");
                            t2.getEditText().setText("");
                            Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(getApplicationContext(),"Process Error",Toast.LENGTH_LONG).show();
                            }
                    }
                });

    }

}