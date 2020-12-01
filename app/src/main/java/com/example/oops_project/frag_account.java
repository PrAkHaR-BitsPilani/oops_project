package com.example.oops_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class frag_account extends Fragment {
    TextView name, email, phone, changePhoto, profession;
    Button verifyLogin;
    EditText mCC, mEnteredPhone;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView profileImage;
    String imgPath, uID;
    MaterialButton updateName, updateProfession;
    private frag_account_events listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        listener.writeData();

        uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());

        imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + uID
                + "/profileImg.jpg";

        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        phone = view.findViewById(R.id.profilePhone);
        mCC = view.findViewById(R.id.CC2);
        mEnteredPhone = view.findViewById(R.id.personPhone2);
        verifyLogin = view.findViewById(R.id.phoneVerify);
        profileImage = view.findViewById(R.id.profile_photo);
        changePhoto = view.findViewById(R.id.changeProfilePhoto);
        profession = view.findViewById(R.id.profileProfession2);
        updateName = view.findViewById(R.id.updateName);
        updateProfession = view.findViewById(R.id.updateProfession);

        /*if(!listener.isOnline())
        {
            updateName.setVisibility(View.GONE);
            updateProfession.setVisibility(View.GONE);
        }*/

        File file = new File(imgPath);

        if (file.exists()) {
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(imgPath)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(profileImage);
        } else {
            if (listener.isOnline()) {
                StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + uID
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
        }


        changePhoto.setOnClickListener(new View.OnClickListener() {
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
                        name.setText("Name: " + value.getString("name"));
                        email.setText("E-mail: " + value.getString("email"));

                        if (value.getString("profession") != null) {
                            profession.setText("Profession: " + value.getString("profession"));
                            profession.setVisibility(View.VISIBLE);
                        }

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

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetName = new EditText(v.getContext());
                resetName.setTextColor(Color.WHITE);
                DrawableCompat.setTint(resetName.getBackground(), ContextCompat.getColor(getActivity(), R.color.blue));
                resetName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme);
                passwordResetDialog.setTitle(Html.fromHtml("<font color='#FFFFFF'>Enter name: </font>"));
                passwordResetDialog.setView(resetName, 55, 0, 55, 0);
                passwordResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = resetName.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getActivity(), "Name cannot be empty!", Toast.LENGTH_LONG).show();
                            return;
                        } else  if (!name.matches("^[a-zA-Z\\s]+")) {
                            Toast.makeText(getActivity(), "Invalid name!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            FirebaseFirestore fStore = Objects.requireNonNull(FirebaseFirestore.getInstance());
                            DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            Map<String, Object> usersMap = new HashMap<>();
                            usersMap.put("name", name);
                            usersMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            TextView tv = getActivity().findViewById(R.id.profilePhone);
                            String p;
                            if (tv.getText().toString().trim().length() >= 23) {
                                p = tv.getText().toString().trim().substring(7, (tv.getText().toString().trim().length() - 13));
                            } else {
                                p = tv.getText().toString().trim().substring(7);
                            }
                            usersMap.put("phone", p);
                            String[] nm = profession.getText().toString().trim().split(" ", 2);
                            usersMap.put("profession",nm[1]);

                            documentReference.set(usersMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Name updated!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error! Something went wrong!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                });
                AlertDialog dialog = passwordResetDialog.create();

                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
                    }
                });
                dialog.show();

            }
        });

        updateProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetProfession = new EditText(v.getContext());
                resetProfession.setTextColor(Color.WHITE);
                DrawableCompat.setTint(resetProfession.getBackground(), ContextCompat.getColor(getActivity(), R.color.blue));
                resetProfession.setFilters(new InputFilter[] { new InputFilter.LengthFilter(17) });
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext(), R.style.MyDialogTheme);
                passwordResetDialog.setTitle("Edit profession");
                passwordResetDialog.setView(resetProfession, 55, 0, 55, 0);
                passwordResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String professionReset = resetProfession.getText().toString().trim();
                        professionReset = Character.toUpperCase(professionReset.charAt(0)) + professionReset.substring(1);
                        if (TextUtils.isEmpty(professionReset)) {
                            Toast.makeText(getActivity(), "Profession cannot be empty!", Toast.LENGTH_LONG).show();
                            return;
                        } else  if (!professionReset.matches("^[a-zA-Z\\s]+")) {
                            Toast.makeText(getActivity(), "Invalid profession!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            FirebaseFirestore fStore = Objects.requireNonNull(FirebaseFirestore.getInstance());
                            DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            Map<String, Object> usersMap = new HashMap<>();
                            usersMap.put("profession", professionReset);
                            usersMap.put("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            TextView tv = getActivity().findViewById(R.id.profilePhone);
                            String p;
                            if (tv.getText().toString().trim().length() >= 23) {
                                p = tv.getText().toString().trim().substring(7, (tv.getText().toString().trim().length() - 13));
                            } else {
                                p = tv.getText().toString().trim().substring(7);
                            }
                            usersMap.put("phone", p);
                            String[] nm = name.getText().toString().trim().split(" ", 2);
                            usersMap.put("name",nm[1]);

                            documentReference.set(usersMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Profession updated!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error! Something went wrong!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = passwordResetDialog.create();

                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
                    }
                });
                dialog.show();

            }
        });

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
        void verifyBtnClicked();

        void changePhotoClicked();

        void downloadPhoto(Uri uri);

        boolean isOnline();

        void writeData();
    }
}
