package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    EditText otpNumberOne, otpNumberTwo, otpNumberThree, otpNumberFour, otpNumberFive, otpNumberSix;
    Button verifyPhone, resendOTP;
    Boolean otpValid = true;
    TextView textView3, textView4;
    LinearLayout greyScreen;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String verificationId;
    String phone;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Intent data = getIntent();
        phone = data.getStringExtra("phone");

        firebaseAuth = FirebaseAuth.getInstance();

        otpNumberOne = findViewById(R.id.otpNumberOne);
        otpNumberTwo = findViewById(R.id.optNumberTwo);
        otpNumberThree = findViewById(R.id.otpNumberThree);
        otpNumberFour = findViewById(R.id.otpNumberFour);
        otpNumberFive = findViewById(R.id.otpNumberFive);
        otpNumberSix = findViewById(R.id.optNumberSix);

        verifyPhone = findViewById(R.id.verifyPhoneBTn);
        resendOTP = findViewById(R.id.resendOTP);

        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        greyScreen = findViewById(R.id.greyScreenVerifyPhone);
        progressBar = findViewById(R.id.progressBarVerifyPhone);

        verifyPhone.setAlpha(0.3f);
        textView4.setAlpha(0.3f);
        textView3.setAlpha(0.3f);
        otpNumberOne.setAlpha(0.3f);
        otpNumberTwo.setAlpha(0.3f);
        otpNumberThree.setAlpha(0.3f);
        otpNumberFour.setAlpha(0.3f);
        otpNumberFive.setAlpha(0.3f);
        otpNumberSix.setAlpha(0.3f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        EditText[] otpArray = {otpNumberOne, otpNumberTwo, otpNumberThree, otpNumberFour, otpNumberFive, otpNumberSix};
        otpNumberOne.addTextChangedListener(new GenericTextWatcher(otpNumberOne, otpArray));
        otpNumberTwo.addTextChangedListener(new GenericTextWatcher(otpNumberTwo, otpArray));
        otpNumberThree.addTextChangedListener(new GenericTextWatcher(otpNumberThree, otpArray));
        otpNumberFour.addTextChangedListener(new GenericTextWatcher(otpNumberFour, otpArray));
        otpNumberFive.addTextChangedListener(new GenericTextWatcher(otpNumberFive, otpArray));
        otpNumberSix.addTextChangedListener(new GenericTextWatcher(otpNumberSix, otpArray));

        verifyPhone.setOnClickListener(new View.OnClickListener() {
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
                    verifyPhone.setAlpha(0.3f);

                    textView4.setAlpha(0.3f);
                    textView3.setAlpha(0.3f);
                    otpNumberOne.setAlpha(0.3f);
                    otpNumberTwo.setAlpha(0.3f);
                    otpNumberThree.setAlpha(0.3f);
                    otpNumberFour.setAlpha(0.3f);
                    otpNumberFive.setAlpha(0.3f);
                    otpNumberSix.setAlpha(0.3f);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    verifyAuthentication(credential);

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
                greyScreen.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                verifyPhone.setAlpha(1f);
                resendOTP.setAlpha(1f);
                textView4.setAlpha(1f);
                textView3.setAlpha(1f);
                otpNumberOne.setAlpha(1f);
                otpNumberTwo.setAlpha(1f);
                otpNumberThree.setAlpha(1f);
                otpNumberFour.setAlpha(1f);
                otpNumberFive.setAlpha(1f);
                otpNumberSix.setAlpha(1f);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getApplicationContext(), "OTP has been sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);

                resendOTP.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                greyScreen.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                verifyPhone.setAlpha(0.3f);
                resendOTP.setAlpha(0.3f);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                verifyAuthentication(phoneAuthCredential);
                resendOTP.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyPhone.this, "OTP verification failed! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        };

        sendOTP(phone);

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resendOTP(phone);
            }
        });

    }

    public void sendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 30, TimeUnit.SECONDS, this, mCallbacks);
    }

    public void resendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 30, TimeUnit.SECONDS, this, mCallbacks, token);

        greyScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        verifyPhone.setAlpha(0.3f);
        resendOTP.setAlpha(0.3f);
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

    public void verifyAuthentication(PhoneAuthCredential credential) {
        greyScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        verifyPhone.setAlpha(0.3f);
        resendOTP.setAlpha(0.3f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(VerifyPhone.this, "Phone number linked successfully!", Toast.LENGTH_SHORT).show();

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = fStore.collection("users").document(userID);

                documentReference.update("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyPhone.this, "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });
    }
}