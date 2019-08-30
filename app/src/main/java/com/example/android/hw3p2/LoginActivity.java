package com.example.android.hw3p2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private EditText editText1;
    private EditText editText2;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText1 = (EditText) findViewById(R.id.editTextEmail);
        editText2 = (EditText) findViewById(R.id.editTextPassword);
        button = (Button) findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
        }

        button.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if (view == button ){
            userLogin();
        }
    }

    private void userLogin() {

        String email= editText1.getText().toString();
        String password= editText2.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();

            return;
        }

        progressDialog.setMessage("Logging in.......");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Wrong email or Password" ,Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
