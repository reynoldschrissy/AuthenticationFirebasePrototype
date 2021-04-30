package com.example.ap1authenticationfirebaseprototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Dashboard extends AppCompatActivity {

    //Creating Variables
    Button VerifyBtn;
    TextView VerifyTxt;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    TextView firstname, secondname, email;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Connecting Variables to our resources
        VerifyBtn = findViewById(R.id.VerifyBtn);
        VerifyTxt = findViewById(R.id.VerifyTxt);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firstname = findViewById(R.id.dashFirstNameTxt);
        secondname = findViewById(R.id.dashSecondNameTxt);
        email = findViewById(R.id.dashEmailTxt);
        userID = Auth.getCurrentUser().getUid();

        FirebaseUser user = Auth.getCurrentUser();
        //references the firestore collection named users and user and use this to get string using doucment snapshot from firebase fields
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                firstname.setText(documentSnapshot.getString("FirstName"));
                secondname.setText(documentSnapshot.getString("SecondName"));
                email.setText(documentSnapshot.getString("Email"));
            }
        });


        //if users emails isnt verified the below fields will become visible
        if(!user.isEmailVerified()){
            VerifyTxt.setVisibility(View.VISIBLE);
            VerifyBtn.setVisibility(View.VISIBLE);

            VerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Send Verification E-Mail
                    //retrieve current user
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: Email not sent" + e.getMessage());

                        }
                    });
                }
            });

        }

    }



//created logout method linked with firebase to logout of the system and return to log in page
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LogIn.class));
        finish();
    }
}