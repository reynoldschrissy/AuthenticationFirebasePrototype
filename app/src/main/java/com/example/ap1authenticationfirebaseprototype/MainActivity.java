package com.example.ap1authenticationfirebaseprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //creating Variables
    private Button CreateAccountPageButton;
    private Button LogInPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connecting our variables to their resources
        CreateAccountPageButton = (Button) findViewById(R.id.CreateAccountPageButton);
        CreateAccountPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenAccountCreation();

            }
        });

        LogInPageButton = (Button) findViewById(R.id.LogInPageButton);
        LogInPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLogIn();

            }
        });


    }
//sets intent for button to open the Create Account Page
    private void OpenAccountCreation() {
        Intent intent = new Intent(this, AccountCreation.class);
        startActivity(intent);
    }
//sets intent for button to open to log in page
    private void OpenLogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);

    }

}