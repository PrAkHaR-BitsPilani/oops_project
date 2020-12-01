package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginWithPhone extends AppCompatActivity {

    EditText otpNumberOne, otpNumberTwo, otpNumberThree, otpNumberFour, otpNumberFive, otpNumberSix;
    EditText mCC, mPhone;
    Button requestOTP, resendOTP, loginButton;
    TextView canResend, textView42, textView32;
    Boolean otpValid = true;
    LinearLayout greyScreen;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String verificationId;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        otpNumberOne = findViewById(R.id.otpNumberOne2);
        otpNumberTwo = findViewById(R.id.optNumberTwo2);
        otpNumberThree = findViewById(R.id.otpNumberThree2);
        otpNumberFour = findViewById(R.id.otpNumberFour2);
        otpNumberFive = findViewById(R.id.otpNumberFive2);
        otpNumberSix = findViewById(R.id.optNumberSix2);

        requestOTP = findViewById(R.id.requestOtpBtn);
        resendOTP = findViewById(R.id.resendOTP);
        loginButton = findViewById(R.id.button3);
        canResend = findViewById(R.id.textView32);
        textView42 = findViewById(R.id.textView42);
        textView32 = findViewById(R.id.textView32);
        greyScreen = findViewById(R.id.greyScreenLoginWithPhone);
        progressBar = findViewById(R.id.progressBarLoginWithPhone);

        EditText[] otpArray = {otpNumberOne, otpNumberTwo, otpNumberThree, otpNumberFour, otpNumberFive, otpNumberSix};
        otpNumberOne.addTextChangedListener(new GenericTextWatcher(otpNumberOne, otpArray));
        otpNumberTwo.addTextChangedListener(new GenericTextWatcher(otpNumberTwo, otpArray));
        otpNumberThree.addTextChangedListener(new GenericTextWatcher(otpNumberThree, otpArray));
        otpNumberFour.addTextChangedListener(new GenericTextWatcher(otpNumberFour, otpArray));
        otpNumberFive.addTextChangedListener(new GenericTextWatcher(otpNumberFive, otpArray));
        otpNumberSix.addTextChangedListener(new GenericTextWatcher(otpNumberSix, otpArray));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateField(otpNumberOne);
                validateField(otpNumberTwo);
                validateField(otpNumberThree);
                validateField(otpNumberFour);
                validateField(otpNumberFive);
                validateField(otpNumberSix);

                if (otpValid) {

                    String otp = otpNumberOne.getText().toString() + otpNumberTwo.getText().toString() + otpNumberThree.getText().toString() + otpNumberFour.getText().toString() +
                            otpNumberFive.getText().toString() + otpNumberSix.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                    greyScreen.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    resendOTP.setAlpha(0.3f);
                    loginButton.setAlpha(0.3f);
                    textView42.setAlpha(0.3f);
                    textView32.setAlpha(0.3f);
                    otpNumberOne.setAlpha(0.3f);
                    otpNumberTwo.setAlpha(0.3f);
                    otpNumberThree.setAlpha(0.3f);
                    otpNumberFour.setAlpha(0.3f);
                    otpNumberFive.setAlpha(0.3f);
                    otpNumberSix.setAlpha(0.3f);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    loginAuthentication(credential);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;
                token = forceResendingToken;
                resendOTP.setVisibility(View.GONE);
                requestOTP.setVisibility(View.GONE);
                otpNumberOne.setVisibility(View.VISIBLE);
                otpNumberTwo.setVisibility(View.VISIBLE);
                otpNumberThree.setVisibility(View.VISIBLE);
                otpNumberFour.setVisibility(View.VISIBLE);
                otpNumberFive.setVisibility(View.VISIBLE);
                otpNumberSix.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                canResend.setVisibility(View.VISIBLE);
                mCC.setVisibility(View.GONE);
                mPhone.setVisibility(View.GONE);
                textView42.setText("Enter the OTP");

                greyScreen.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                resendOTP.setAlpha(1f);
                loginButton.setAlpha(1f);
                textView42.setAlpha(1f);
                textView32.setAlpha(1f);
                otpNumberOne.setAlpha(1f);
                otpNumberTwo.setAlpha(1f);
                otpNumberThree.setAlpha(1f);
                otpNumberFour.setAlpha(1f);
                otpNumberFive.setAlpha(1f);
                otpNumberSix.setAlpha(1f);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), "OTP has been sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                resendOTP.setVisibility(View.VISIBLE);
                canResend.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                greyScreen.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setAlpha(0.3f);
                resendOTP.setAlpha(0.3f);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                loginAuthentication(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginWithPhone.this, "OTP verification failed! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        };

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP(phone);
            }
        });

    }

    public void requestOTP(View view) {
        mCC = findViewById(R.id.loginCC);
        mPhone = findViewById(R.id.LoginPhone);


        if (TextUtils.isEmpty(mPhone.getText().toString())) {
            mPhone.setError("Phone field cannot be empty!");
            return;
        }

        if (!TextUtils.isDigitsOnly(mPhone.getText().toString())) {
            mPhone.setError("Phone field must contain only digits!");
            return;
        }

        if (!TextUtils.isDigitsOnly(mCC.getText().toString())) {
            mCC.setError("CC must contain only digits!");
            return;
        }

        if (mPhone.getText().toString().trim().length() < 10) {
            mPhone.setError("Phone field must have 10 digits!");
            return;
        }

        if (TextUtils.isEmpty(mCC.getText().toString())) {
            phone = "+91" + mPhone.getText().toString().trim();
        } else {
            phone = "+" + mCC.getText().toString().trim() + mPhone.getText().toString().trim();
        }
        greyScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        requestOTP.setAlpha(0.3f);
        textView42.setAlpha(0.3f);
        mPhone.setAlpha(0.3f);
        mCC.setAlpha(0.3f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        sendOTP(phone);

    }

    public void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 30, TimeUnit.SECONDS, this, mCallbacks);
    }

    public void resendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 30, TimeUnit.SECONDS, this, mCallbacks, token);

        greyScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        resendOTP.setAlpha(0.3f);
        loginButton.setAlpha(0.3f);
        textView42.setAlpha(0.3f);
        textView32.setAlpha(0.3f);
        otpNumberOne.setAlpha(0.3f);
        otpNumberTwo.setAlpha(0.3f);
        otpNumberThree.setAlpha(0.3f);
        otpNumberFour.setAlpha(0.3f);
        otpNumberFive.setAlpha(0.3f);
        otpNumberSix.setAlpha(0.3f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void validateField(EditText field) {
        if (field.getText().toString().isEmpty()) {
            field.setError("Required!");
            otpValid = false;
        } else {
            otpValid = true;
        }
    }

    public void loginAuthentication(PhoneAuthCredential credential) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    if (TextUtils.isEmpty(email) || email == null) {
                        Toast.makeText(getApplicationContext(), "Error! : Phone number is not verified or associated with any user!", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Login through phone successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.putExtra("login", "1");
                        startActivity(intent);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

}
