package com.example.oops_project;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class frag_account extends Fragment{

    ImageView profileImage;
    TextView name , email , phone;
    Button verifyLogin;
    EditText mCC, mEnteredPhone;
    private frag_account_events listener;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Context context;

    public frag_account(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account , container , false);

        profileImage = view.findViewById(R.id.profile_photo);
        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        phone = view.findViewById(R.id.profilePhone);
        mCC = view.findViewById(R.id.CC2);
        mEnteredPhone = view.findViewById(R.id.personPhone2);
        verifyLogin = view.findViewById(R.id.phoneVerify);

        verifyLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.verifyBtnClicked();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // gets user number
        String n = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        if (firebaseAuth != null && firebaseFirestore != null) {

            final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        name = view.findViewById(R.id.profileName);
                        email = view.findViewById(R.id.profileEmail);
                        phone = view.findViewById(R.id.profilePhone);

                        name.setText(value.getString("name"));
                        email.setText(value.getString("email"));
                        if (n != null && !TextUtils.isEmpty(n) && !TextUtils.isDigitsOnly(n)) {
                            phone.setText(value.getString("phone"));
                        } else {
                            phone.setText(value.getString("phone") + "(unverified)");
                        }
                    }
                }
            });
        }

        // checks if it is valid
        if(n != null && !TextUtils.isEmpty(n) && !TextUtils.isDigitsOnly(n)) {

            // sets them to invisible
            mCC.setVisibility(View.GONE);
            mEnteredPhone.setVisibility(View.GONE);
            verifyLogin.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof frag_account_events)
            listener = (frag_account_events) context;
        else
            throw new ClassCastException(context.toString() + " must implement frag_account_events");
    }

    public interface frag_account_events {
        public void verifyBtnClicked();
    }

}
