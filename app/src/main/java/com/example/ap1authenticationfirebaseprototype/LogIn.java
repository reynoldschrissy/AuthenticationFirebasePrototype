package com.example.ap1authenticationfirebaseprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {
//creating variables
    EditText Email, Password;
    Button LogInTo;
    TextView Create, ForgottenPassword;
    ProgressBar ProgressBar;
    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Connecting Variables to our resources
        Email = findViewById(R.id.EmailAddressTxt);
        Password = findViewById(R.id.PasswordTxt);
        ProgressBar = findViewById(R.id.progressBar);
        Auth = FirebaseAuth.getInstance();
        LogInTo = findViewById(R.id.LogInBtn);
        Create = findViewById(R.id.CreateBtn);
        ForgottenPassword = findViewById(R.id.forgotPasswordTxt);

        LogInTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gets email and password from text fields and coverts the to strings
                String email= Email.getText().toString().trim();
                String password = Password.getText().toString();

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

                //Authenticate the Log In details

                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        }
                        else {
                            Toast.makeText(LogIn.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });
        //this listener allows the user to click on the text field to return to the Account Creation page
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AccountCreation.class));
            }
        });

        //this allows the user to click the button and a dialog box will appear to let the user enter their details
        ForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your E-Mail for Password Reset Link");
                passwordResetDialog.setView(resetMail);

                //sets dialog button for yes
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This gets the email address and sends reset link to the user

                        //Extracting the email from the edit text field
                        String mail = resetMail.getText().toString();

                        //firebase password reset email listener
                        Auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LogIn.this, "Reset link has been sent to your E-Mail", Toast.LENGTH_SHORT).show();
                            }


                        }).
                               //displays toast message if there is issues
                                addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogIn.this, "Resent link could not be sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                //sets dialog button for No
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog box
                    }


                });

                //creates and shows the dialog box
                passwordResetDialog.create().show();
            }
        });


    }
}