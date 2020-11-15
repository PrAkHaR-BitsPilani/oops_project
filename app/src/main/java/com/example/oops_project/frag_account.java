package com.example.oops_project;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

import static android.content.Context.DOWNLOAD_SERVICE;

public class frag_account extends Fragment {

    ImageButton imageButton;
    TextView name, email, phone;
    Button verifyLogin;
    EditText mCC, mEnteredPhone;
    private frag_account_events listener;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView profileImage;
    String imgPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        imgPath =  Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/profileImg.jpg";

        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        phone = view.findViewById(R.id.profilePhone);
        mCC = view.findViewById(R.id.CC2);
        mEnteredPhone = view.findViewById(R.id.personPhone2);
        verifyLogin = view.findViewById(R.id.phoneVerify);
        imageButton = view.findViewById(R.id.imageButton);
        profileImage = view.findViewById(R.id.profile_photo);

        File file = new File(imgPath);

        if (file.exists()) {
            profileImage.setImageURI(Uri.parse(imgPath));
        } else {
            StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                    + "/profileImg.jpg");

            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Toast.makeText(getActivity(), "Getting your profile photo from the cloud...", Toast.LENGTH_SHORT).show();
                    listener.downloadPhoto(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changePhotoClicked();
            }
        });

        verifyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.verifyBtnClicked();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String n = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();

        if (firebaseAuth != null && firebaseFirestore != null) {
            final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());

            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null) {
                        name = view.findViewById(R.id.profileName);
                        email = view.findViewById(R.id.profileEmail);
                        phone = view.findViewById(R.id.profilePhone);

                        name.setText("Name: " + value.getString("name"));
                        email.setText("E-mail: " + value.getString("email"));

                        if (n != null && !TextUtils.isEmpty(n)) {
                            phone.setText("Phone: " + value.getString("phone"));
                        } else {
                            phone.setText("Phone: " + value.getString("phone") + " (unverified)");
                        }
                    }
                }
            });
        }

        // checks if number is verified
        if (n != null && !TextUtils.isEmpty(n)) {

            // makes them go out of view
            mCC.setVisibility(View.GONE);
            mEnteredPhone.setVisibility(View.GONE);
            verifyLogin.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof frag_account_events)
            listener = (frag_account_events) context;
        else
            throw new ClassCastException(context.toString() + " must implement frag_account_events");
    }

    public interface frag_account_events {
        public void verifyBtnClicked();

        public void changePhotoClicked();

        public void downloadPhoto(Uri uri);
    }
}
