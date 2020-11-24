package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText mFullName, mEmail, mPassword, mPhone, mProfession, mCC;
    Button mRegisterBtn;
    TextView mLoginButton;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    LinearLayout greyScreen;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // INITIALIZING ALL COMPONENTS

        mFullName = findViewById(R.id.personName);
        mEmail = findViewById(R.id.personEmail);
        mPassword = findViewById(R.id.personPassword);
        mPhone = findViewById(R.id.personPhone);
        mRegisterBtn = findViewById(R.id.buttonRegister);
        mLoginButton = findViewById(R.id.isMember);
        mProfession = findViewById(R.id.personProfession);
        mCC = findViewById(R.id.CC);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        greyScreen = findViewById(R.id.greyScreenRegister);

        // FUNCTIONALITY OF SIGN UP BUTTON

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore = FirebaseFirestore.getInstance();

                String name = mFullName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String number = mPhone.getText().toString().trim();
                String code = mCC.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String profession = mProfession.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    mFullName.setError("Name field cannot be empty!");
                    return;
                }

                if (!name.matches("^[a-zA-Z\\s]+")) {
                    mFullName.setError("Invalid name!");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("E-mail field cannot be empty!");
                    return;
                }

                if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                    mEmail.setError("E-mail is badly formatted!");
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    mPhone.setError("Phone field cannot be empty!");
                    return;
                }

                if (!TextUtils.isDigitsOnly(number)) {
                    mPhone.setError("Phone field must contain only digits!");
                    return;
                }

                if (!TextUtils.isDigitsOnly(code)) {
                    mCC.setError("CC field must contain only digits!");
                    return;
                }

                if (number.length() != 10) {
                    mPhone.setError("Phone field must have 10 digits!");
                    return;
                }

                if (TextUtils.isEmpty(profession)) {
                    mProfession.setError("Profession field cannot be empty!");
                    return;
                }

                if(!profession.matches("^[a-zA-Z\\s]+"))
                {
                    mProfession.setError("Invalid profession!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password field cannot be empty!");
                    return;
                }

                if (password.length() < 8) {
                    mPassword.setError("Password must have 8 or more characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                greyScreen.setVisibility(View.VISIBLE);
                mRegisterBtn.setAlpha(0.3f);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "A verification e-mail has been sent to your e-mail address!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    greyScreen.setVisibility(View.GONE);
                                    mRegisterBtn.setAlpha(1f);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    greyScreen.setVisibility(View.GONE);
                                    mRegisterBtn.setAlpha(1f);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    startActivity(getIntent());
                                }
                            });

                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            Map<String, Object> usersMap = new HashMap<>();
                            usersMap.put("name", name);
                            usersMap.put("email", email);
                            usersMap.put("profession", profession);

                            if (TextUtils.isEmpty(code)) {
                                usersMap.put("phone", "+91" + number);
                            } else {
                                usersMap.put("phone", "+" + code + number);
                            }


                            documentReference.set(usersMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseAuth.getInstance().signOut();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    greyScreen.setVisibility(View.GONE);
                                    mRegisterBtn.setAlpha(1f);

                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(Register.this, "Error! Something went wrong!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    greyScreen.setVisibility(View.GONE);
                                    mRegisterBtn.setAlpha(1f);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            greyScreen.setVisibility(View.GONE);
                            mRegisterBtn.setAlpha(1f);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(Register.this, "Error! : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        if (!doubleBackToExitPressedOnce) {
            Toast.makeText(this, "Press BACK again to exit!", Toast.LENGTH_SHORT).show();
        }
        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}