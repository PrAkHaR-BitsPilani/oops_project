package com.example.oops_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView mRegisterBtn, mForgotPassword;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INITIALIZING ALL COMPONENTS

        loginEmail = findViewById(R.id.personEmail);
        loginPassword = findViewById(R.id.personPassword);
        progressBar = findViewById(R.id.progressBar2);
        loginBtn = findViewById(R.id.buttonLogin);
        mRegisterBtn = findViewById(R.id.notMember);
        mForgotPassword = findViewById(R.id.forgotPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        //GO TO DASHBOARD IF USER HASN'T LOGGED OUT

        if(firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getEmail() != null) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        }

        // FUNCTIONALITY OF LOGIN BUTTON

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    loginEmail.setError("E-mail field cannot be empty!");
                    return;
                }

                if(!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                    loginEmail.setError("E-mail is badly formatted!");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password field cannot be empty!");
                    return;
                }

                if(password.length() < 8) {
                    loginPassword.setError("Password must have 8 or more characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if(user.isEmailVerified()) {
                                Toast.makeText(Login.this, "Account login successful!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                                startActivity(new Intent(getApplicationContext(), Dashboard.class));

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder resendEmailDialog = new AlertDialog.Builder(Login.this);
                                resendEmailDialog.setTitle("E-mail account is not verified!");
                                resendEmailDialog.setMessage(("Do you want to re-send verification e-mail?"));
                                resendEmailDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Login.this, "A new verification e-mail has been sent to your e-mail address!", Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Login.this, "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        });

                                    }
                                });

                                resendEmailDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                                resendEmailDialog.create().show();
                                if(!resendEmailDialog.create().isShowing()) {
                                    FirebaseAuth.getInstance().signOut();
                                }
                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error! : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //FUNCTIONAlITY OF REGISTER BUTTON

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        //FUNCTIONALITY OF FORGOT PASSWORD

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage(("Enter you registered E-mail to receive a reset link"));
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = resetMail.getText().toString().trim();

                        if(TextUtils.isEmpty(email)) {
                            Toast.makeText(Login.this,  "E-mail field cannot be empty!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link has been sent to your E-mail!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error! :" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }

        });
    }

    // FUNCTIONALITY OF LOGIN USING PHONE BUTTON

    public void loginViaPhone(View view) {
        startActivity(new Intent(getApplicationContext(), LoginWithPhone.class));
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}