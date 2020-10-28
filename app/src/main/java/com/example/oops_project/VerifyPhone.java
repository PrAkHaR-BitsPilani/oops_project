package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
    TextView textView3;

    FirebaseAuth firebaseAuth;

    PhoneAuthCredential phoneAuthCredential;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String verificationId;
    String phone;
    String userID;

    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Intent data = getIntent();
        phone = data.getStringExtra("phone");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore;

        otpNumberOne = findViewById(R.id.otpNumberOne);
        otpNumberTwo = findViewById(R.id.optNumberTwo);
        otpNumberThree = findViewById(R.id.otpNumberThree);
        otpNumberFour = findViewById(R.id.otpNumberFour);
        otpNumberFive = findViewById(R.id.otpNumberFive);
        otpNumberSix = findViewById(R.id.optNumberSix);

        verifyPhone = findViewById(R.id.verifyPhoneBTn);
        resendOTP = findViewById(R.id.resendOTP);

        textView3 =  findViewById(R.id.textView3);

        verifyPhone.setOnClickListener(new View.OnClickListener() {
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
                verifyAuthentication(phoneAuthCredential);
                resendOTP.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyPhone.this, "OTP verification failed! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Dashboard.class ));
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

    public void verifyAuthentication(PhoneAuthCredential credential){
        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(VerifyPhone.this, "Phone number linked successfully!", Toast.LENGTH_SHORT).show();

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = fStore.collection("users").document(userID);

                documentReference.update("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                startActivity(new Intent(getApplicationContext(), Dashboard.class ));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyPhone.this, "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), Dashboard.class ));
            }
        });
    }
}