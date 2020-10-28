package com.example.oops_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView canResend, textView42;
    Boolean otpValid = true;

    FirebaseAuth firebaseAuth;

    PhoneAuthCredential phoneAuthCredential;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String verificationId;
    String phone;
    String userID;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateField(otpNumberOne);
                validateField(otpNumberTwo);
                validateField(otpNumberThree);
                validateField(otpNumberFour);
                validateField(otpNumberFive);
                validateField(otpNumberSix);

                if(otpValid){

                    String otp = otpNumberOne.getText().toString()+otpNumberTwo.getText().toString()+otpNumberThree.getText().toString()+otpNumberFour.getText().toString()+
                            otpNumberFive.getText().toString()+otpNumberSix.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

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
                loginAuthentication(phoneAuthCredential);
                resendOTP.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginWithPhone.this, "OTP verification failed! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Login.class ));
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
        mPhone = findViewById(R.id.buttonLoginPhone);

        if(TextUtils.isEmpty(mPhone.getText().toString())) {
            mPhone.setError("Phone field cannot be empty!");
            return;
        }

        if(!TextUtils.isDigitsOnly(mPhone.getText().toString())) {
            mPhone.setError("Phone field must contain only digits!");
            return;
        }

        if(!TextUtils.isDigitsOnly(mCC.getText().toString())) {
            mCC.setError("CC must contain only digits!");
            return;
        }

        if(mPhone.getText().toString().trim().length() < 10) {
            mPhone.setError("Phone field must have 10 digits!");
            return;
        }

        if(TextUtils.isEmpty(mCC.getText().toString())) {
            phone = "+91" + mPhone.getText().toString().trim();
        } else {
            phone = "+" + mCC.getText().toString().trim() + mPhone.getText().toString().trim();
        }
        sendOTP(phone);

    }

    public void sendOTP(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,30, TimeUnit.SECONDS, this, mCallbacks);
    }

    public void resendOTP(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,30, TimeUnit.SECONDS,this, mCallbacks, token);
    }

    public void validateField(EditText field){
        if(field.getText().toString().isEmpty()){
            field.setError("Required!");
            otpValid = false;
        }else {
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
                    if(TextUtils.isEmpty(email) || email == null) {
                        Toast.makeText(getApplicationContext(), "Error! : Phone number is not verified or associated with any user!", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Login through phone successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
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
