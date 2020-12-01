package com.example.oops_project;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private static final int CALENDAR_PERMISSION_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private static final String permissions[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR};
    private static final int requestCodes[] = new int[] {STORAGE_PERMISSION_CODE, CALENDAR_PERMISSION_CODE};

    FirebaseAuth firebaseAuth;
    boolean doubleBackToExitPressedOnce = false;
    private TextView or, loginToAccount, appName;
    private EditText loginEmail, loginPassword;
    private Button loginBtn, loginPhoneBtn;
    private ProgressBar progressBar;
    private LinearLayout greyScreen, whiteScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.personEmail);
        loginPassword = findViewById(R.id.personPassword);
        progressBar = findViewById(R.id.progressBar2);
        loginBtn = findViewById(R.id.buttonLogin);
        TextView mRegisterBtn = findViewById(R.id.notMember);
        TextView mForgotPassword = findViewById(R.id.forgotPassword);
        greyScreen = findViewById(R.id.greyScreenLogin);
        whiteScreen = findViewById(R.id.whiteScreenLogin);
        loginPhoneBtn = findViewById(R.id.LoginPhone);
        or = findViewById(R.id.textView2);
        loginToAccount = findViewById(R.id.loginHeading2);
        appName = findViewById(R.id.loginHeading);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getEmail() != null) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                intent.putExtra("login", "0");
                startActivity(intent);
            }
        }
        whiteScreen.setVisibility(View.GONE);

        checkPermission(permissions, requestCodes);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("E-mail field cannot be empty!");
                    return;
                }

                if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                    loginEmail.setError("E-mail is badly formatted!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password field cannot be empty!");
                    return;
                }

                if (password.length() < 8) {
                    loginPassword.setError("Password must have 8 or more characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                greyScreen.setVisibility(View.VISIBLE);
                loginBtn.setAlpha(0.3f);
                loginPhoneBtn.setAlpha(0.3f);
                loginEmail.setAlpha(0.3f);
                loginPassword.setAlpha(0.3f);
                mRegisterBtn.setAlpha(0.3f);
                mForgotPassword.setAlpha(0.3f);
                or.setAlpha(0.3f);
                loginToAccount.setAlpha(0.3f);
                appName.setAlpha(0.3f);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user.isEmailVerified()) {

                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                intent.putExtra("login", "1");

                                Toast.makeText(Login.this, "Account login successful!", Toast.LENGTH_SHORT).show();

                                startActivity(intent);

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                greyScreen.setVisibility(View.GONE);
                                loginBtn.setAlpha(1f);
                                loginPhoneBtn.setAlpha(1f);
                                loginBtn.setAlpha(1f);
                                loginPhoneBtn.setAlpha(1f);
                                loginEmail.setAlpha(1f);
                                loginPassword.setAlpha(1f);
                                mRegisterBtn.setAlpha(1f);
                                mForgotPassword.setAlpha(1f);
                                or.setAlpha(1f);
                                loginToAccount.setAlpha(1f);
                                appName.setAlpha(1f);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                AlertDialog.Builder resendEmailDialog = new AlertDialog.Builder(Login.this, R.style.MyDialogTheme);
                                resendEmailDialog.setTitle("E-mail account is not verified!");
                                resendEmailDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you want to resend verification e-mail?</font>"));
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

                                AlertDialog dialog = resendEmailDialog.create();

                                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Login.this, R.color.blue));
                                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Login.this, R.color.blue));
                                    }
                                });
                                dialog.show();
                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            greyScreen.setVisibility(View.GONE);
                            loginBtn.setAlpha(1f);
                            loginPhoneBtn.setAlpha(1f);
                            loginBtn.setAlpha(1f);
                            loginPhoneBtn.setAlpha(1f);
                            loginEmail.setAlpha(1f);
                            loginPassword.setAlpha(1f);
                            mRegisterBtn.setAlpha(1f);
                            mForgotPassword.setAlpha(1f);
                            or.setAlpha(1f);
                            loginToAccount.setAlpha(1f);
                            appName.setAlpha(1f);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(Login.this, "Error! : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                resetMail.setTextColor(Color.WHITE);
                DrawableCompat.setTint(resetMail.getBackground(), ContextCompat.getColor(getApplicationContext(), R.color.blue));

                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme);
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Enter your e-mail to receive a reset link: </font>"));
                passwordResetDialog.setView(resetMail, 55, 0, 55, 0);
                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = resetMail.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(Login.this, "E-mail field cannot be empty!", Toast.LENGTH_LONG).show();
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

                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = passwordResetDialog.create();

                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                    }
                });
                dialog.show();
            }

        });
    }

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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void checkPermission(String[] permissions, int[] requestCode)
    {
        ActivityCompat.requestPermissions(Login.this, permissions, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                finishAndRemoveTask();
            }

        }
    }


}