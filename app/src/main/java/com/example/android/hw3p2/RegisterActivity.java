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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextFirstName = (EditText) findViewById(R.id.editTextfirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextlastName);

        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == buttonRegister){
            registerUser();
            saveUserInfo();
        }
        else if (view == textViewSignIn){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void saveUserInfo() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();

        if (TextUtils.isEmpty(firstName)){

            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(lastName)){

            Toast.makeText(this, "Please enter Last Name ", Toast.LENGTH_SHORT).show();

            return;
        }

        progressDialog.setMessage("Registering User.......");
        progressDialog.show();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                finish();
                                startActivity(new Intent(getApplicationContext(),MapsActivity.class));

                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Could not Update Profile, Please Try again!" ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void registerUser() {

        String email= editTextEmail.getText().toString();
        String password= editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();

            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registered Successfully" ,Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Could not Register! Please Try again" ,Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
