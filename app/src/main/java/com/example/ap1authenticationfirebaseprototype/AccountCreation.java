package com.example.ap1authenticationfirebaseprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountCreation extends AppCompatActivity {
//Creating Variables
    EditText FirstName, SecondName, Email, Password;
    Button RegisterButton;
    TextView LogInButton;
    FirebaseAuth Auth;          //Firebase supplied for ability for authentication methods using firebase
    ProgressBar ProgressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation3);

        
        //Connecting Variables to our resources
        FirstName = findViewById(R.id.FirstNameTxt);
        SecondName = findViewById(R.id.SecondNameTxt);
        Email = findViewById(R.id.EmailAddressTxt);
        Password = findViewById(R.id.PasswordTxt);
        RegisterButton = findViewById(R.id.RegisterBtn);
        LogInButton = findViewById(R.id.RelogInBtn);

        Auth = FirebaseAuth.getInstance();
        ProgressBar = findViewById(R.id.progressBar);
        fStore = FirebaseFirestore.getInstance();


        //If user is already logged in they will not be taken to the log in or account creation page
        if(Auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // strings are used to get the text from the entered data
                String email= Email.getText().toString().trim();
                String password = Password.getText().toString();
                String firstname = FirstName.getText().toString();
                String secondname = SecondName.getText().toString();

                //if statement used to notify user if email field is empty as it is required
                if (TextUtils.isEmpty(email)) {
                    Email.setError("E-Mail Address is Required");
                    return;
                }
                    if(TextUtils.isEmpty(password)) {
                        Password.setError("Password is Required");
                    }

                    //if statement to display error if password is less than 16 characters
                    if(password.length() <6 ) {
                        Password.setError("Password must be 6 characters or more");
                        return;
                    }

                    ProgressBar.setVisibility(View.VISIBLE);

                    //Registers the user in firebase

                Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if statement to confirm to user if the task was completed correctly it will either inform the user and move them to the next page or give them error message if not
                        if(task.isSuccessful()) {
                            //Send Verification E-Mail
                            //retrieve current user
                            FirebaseUser cUser = Auth.getCurrentUser();
                            cUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AccountCreation.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: Email not sent" + e.getMessage());
                                }
                            });

                            //gets current user id and puts the requested data into the database
                            Toast.makeText(AccountCreation.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = Auth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("FirstName", firstname);
                            user.put("SecondName", secondname);
                            user.put("Email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Tag", "onSuccess: user profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure" + e.toString());
                                }
                            });
                                    startActivity(new Intent(getApplicationContext(), Dashboard.class));

                        } else {
                            Toast.makeText(AccountCreation.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressBar.setVisibility(View.GONE);
                        }
                    }
                })
            ;}
        });
// //this listener allows the user to click on the text field to return to the Log In page
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });




    }
}