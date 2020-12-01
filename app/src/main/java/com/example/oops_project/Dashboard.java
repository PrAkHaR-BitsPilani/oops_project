package com.example.oops_project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

public class Dashboard extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        frag_account.frag_account_events {

    private static final int CALENDAR_PERMISSION_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private static final String permissions[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR};
    private static final int requestCodes[] = new int[] {STORAGE_PERMISSION_CODE, CALENDAR_PERMISSION_CODE};

    int addEvents = 0;
    String imgPath;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/com.example.oops_project/files/users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    EditText mCC, mEnteredPhone;
    String passPhone;
    long downloadID, downloadID2, downloadID3;
    public static String uID;
    Toolbar toolbar;

    MaterialAlertDialogBuilder dialog;
    androidx.appcompat.app.AlertDialog d;

    private final ArrayList<category> categories = new ArrayList<>();
    private final ArrayList<event> events = new ArrayList();
    private FloatingActionButton add_button;
    private DrawerLayout drawer;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView userNameNav, professionNav;
    private ProgressBar imgUploadProgressBar;
    private categoryRecViewAdapter categoryRecViewAdapter;
    private itemRecViewAdapter itemRecViewAdapter;
    private int Position;
    private frag_inventory frag_inventory;
    private frag_events frag_events;
    private ToDoFrag toDoFrag;
    private frag_account frag_account;
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                imgUploadProgressBar = findViewById(R.id.imgProgressBar);
                imgUploadProgressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "Profile picture updated successfully!", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            } else if (downloadID3 == id) {
                Toast.makeText(getApplicationContext(), "Data downloaded successfully!", Toast.LENGTH_LONG).show();
                Intent eventIntent = new Intent(getApplicationContext(), Dashboard.class);
                eventIntent.putExtra("login", "1");
                finish();
                startActivity(eventIntent);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        checkPermission(permissions, requestCodes);

        uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());

        imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + Objects.requireNonNull(uID)
                + "/profileImg.jpg";

        // creating nav drawer

        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggler = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggler);
        toggler.setDrawerIndicatorEnabled(true);
        toggler.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggler.syncState();
        View headerView = navigationView.getHeaderView(0);
        userNameNav = headerView.findViewById(R.id.user_name);
        professionNav = headerView.findViewById(R.id.profession_nav);
        ImageView profileImageNav = headerView.findViewById(R.id.user_image);

        Menu menu = navigationView.getMenu();

        MenuItem tools= menu.findItem(R.id.generalHeader);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        tools = menu.findItem(R.id.userHeader);
        s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        tools = menu.findItem(R.id.syncHeader);
        s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        File file = new File(imgPath);
        if (file.exists()) {
            Glide.with(getApplicationContext())
                    .load(imgPath)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(profileImageNav);
        }

        //image download progress checker
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    String name = value.getString("name");
                    String[] nm = name.split(" ", 2);
                    name = nm[0];
                    userNameNav.setText(name);
                    if (!TextUtils.isEmpty(value.getString("profession"))) {
                        professionNav.setText(value.getString("profession"));
                    }
                }
            }
        });

        //creating add button

        add_button = findViewById(R.id.add);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_category();
            }
        });

        readData();

        Intent data = getIntent();
        if(data.getStringExtra("login") != null) {
            addEvents = Integer.parseInt(data.getStringExtra("login"));
        }

        if(addEvents == 1) {
            insertEvents();
        }

        frag_inventory = new frag_inventory(categories, add_button, toolbar);
        frag_events = new frag_events(events, add_button);
        toDoFrag = new ToDoFrag(add_button);
        frag_account = new frag_account();

        frag_inventory.setTransferCall(new frag_inventory.transferCall() {
            @Override
            public void imageUploadCategory(categoryRecViewAdapter adapter, int pos) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Position = pos;
                categoryRecViewAdapter = adapter;
                startActivityForResult(openGalleryIntent, 1001);
            }

            @Override
            public void imageUploadItem(itemRecViewAdapter adapter, int pos) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Position = pos;
                itemRecViewAdapter = adapter;
                startActivityForResult(openGalleryIntent, 1002);
            }
        });



        frag_manager = getSupportFragmentManager();
        frag_trans = frag_manager.beginTransaction();

        frag_trans.add(R.id.container_fragment, frag_inventory);

        frag_trans.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.inventory_list:
                toolbar.setTitle("Inventory");
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, frag_inventory);
                frag_trans.commit();
                break;

            case R.id.events_list:
                toolbar.setTitle("Events");
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, frag_events);
                frag_trans.commit();
                break;

            case R.id.todo_list:
                toolbar.setTitle("Checklist");
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, toDoFrag);
                frag_trans.commit();
                break;

            case R.id.account:
                toolbar.setTitle("Account");
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, frag_account);
                frag_trans.commitNow();
                break;

            case R.id.upload:
                writeData();

                if (isOnline()) {
                    dialog = new MaterialAlertDialogBuilder(Dashboard.this, R.style.MyDialogTheme);
                    dialog.setTitle("Backup data");
                    dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to backup your current data?</font>"));
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(path + "/myfile.txt");
                            if (file.exists()) {
                                uploadDataToFirebase(Uri.fromFile(new File(path + "/myfile.txt")));
                            }
                            file = new File(path + "/toDoListDatabase");
                            if (file.exists()) {
                                uploadChecklistToFirebase(Uri.fromFile(file));
                            }
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d = dialog.create();
                    d.show();
                    d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));
                } else {
                    Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.download:
                if (isOnline()) {
                    dialog = new MaterialAlertDialogBuilder(Dashboard.this, R.style.MyDialogTheme);
                    dialog.setTitle("Download data");
                    dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to download your backed up data?</font>"));
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + uID
                                    + "/myfile.txt");

                            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(Dashboard.this, "Getting your data from the cloud...", Toast.LENGTH_SHORT).show();
                                    downloadFile(uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Dashboard.this, "Error! : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            StorageReference profileRef2 = FirebaseStorage.getInstance().getReference().child("users/" + uID
                                    + "/toDoListDatabase");

                            profileRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Toast.makeText(Dashboard.this, "Getting your data from the cloud...", Toast.LENGTH_SHORT).show();
                                    downloadFile2(uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Dashboard.this, "Error! : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d = dialog.create();
                    d.show();
                    d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));
                } else {
                    Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.logout:

                dialog = new MaterialAlertDialogBuilder(Dashboard.this, R.style.MyDialogTheme);
                dialog.setTitle("Logout");
                dialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to logout?</font>"));
                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteEvents();
                                FirebaseAuth.getInstance().signOut();
                                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                    Toast.makeText(Dashboard.this, "Logout successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                }
                                finish();
                            }
                        });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                d = dialog.create();
                d.show();
                d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));
                d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Dashboard.this, R.color.blue));

                break;
            default:
                break;
        }

        return true;
    }

    public void onBackPressed() {

        // note: you can also use 'getSupportFragmentManager()'
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // No backstack to pop, so calling super
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
        } else {
            getSupportFragmentManager().popBackStack();
            toolbar.setTitle("Inventory");
        }
    }

    @Override
    public void verifyBtnClicked() {

        mCC = findViewById(R.id.CC2);
        mEnteredPhone = findViewById(R.id.personPhone2);

        if (TextUtils.isEmpty(mEnteredPhone.getText().toString().trim())) {
            mEnteredPhone.setError("Phone field cannot be empty!");
            return;
        }

        if (!TextUtils.isDigitsOnly(mEnteredPhone.getText().toString().trim())) {
            mEnteredPhone.setError("Phone field must contain only digits!");
            return;
        }

        if (!TextUtils.isDigitsOnly(mCC.getText().toString())) {
            mCC.setError("CC field must contain only digits!");
            return;
        }

        if (mEnteredPhone.getText().toString().trim().length() != 10) {
            mEnteredPhone.setError("Phone field must have 10 digits!");
            return;
        }

        if (TextUtils.isEmpty(mCC.getText().toString())) {
            passPhone = "+91" + mEnteredPhone.getText().toString().trim();
        } else {
            passPhone = "+" + mCC.getText().toString().trim() + mEnteredPhone.getText().toString().trim();
        }

        Intent phone = new Intent(this, VerifyPhone.class);
        phone.putExtra("phone", passPhone);
        startActivity(phone);

    }

    @Override
    public void changePhotoClicked() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imageUri = data.getData();
                if (isOnline()) {
                    uploadImageToFirebase(imageUri);
                } else {
                    Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_LONG).show();
                }
            }
        }

        if(requestCode == 1001) {
            if(resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imgUri = data.getData();

                String realUri  = getRealPathFromURI(imgUri);
                String dest = path + "/" + categoryRecViewAdapter.getCategories().get(Position).getId() + ".jpg";
                categoryRecViewAdapter.getCategories().get(Position).setImageURL(dest);

                File source = new File(realUri);
                File destination = new File(dest);
                try
                {
                    FileUtils.copyFile(source, destination);
                }
                catch (IOException e)
                {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                categoryRecViewAdapter.notifyItemChanged(Position);
            }
        }

        if(requestCode == 1002) {
            if(resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imgUri = data.getData();

                String realUri = getRealPathFromURI(imgUri);
                int categoryId = itemRecViewAdapter.getItems().get(Position).getCategoryId();
                String itemId = itemRecViewAdapter.getItems().get(Position).getId();

                String dest = path + "/" + categoryId + itemId + ".jpg";

                itemRecViewAdapter.getItems().get(Position).setImgURI(dest);
                itemRecViewAdapter.getItems().get(Position).setShareURI(dest);

                File source = new File(realUri);
                File destination = new File(dest);
                try
                {
                    FileUtils.copyFile(source, destination);
                }
                catch (IOException e)
                {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                itemRecViewAdapter.notifyItemChanged(Position);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Toast.makeText(Dashboard.this, "Uploading your photo to the cloud...", Toast.LENGTH_LONG).show();

        imgUploadProgressBar = findViewById(R.id.imgProgressBar);
        ImageView photo = findViewById(R.id.profile_photo);
        Button verify = findViewById(R.id.phoneVerify);
        LinearLayout greyScreen = findViewById(R.id.greyScreen);
        EditText cc = findViewById(R.id.CC2);
        EditText phone = findViewById(R.id.personPhone2);
        MaterialButton updateName = findViewById(R.id.updateName);
        MaterialButton updateProfession = findViewById(R.id.updateProfession);
        TextView name, email, phone2, profession, changePhoto;
        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        phone2 = findViewById(R.id.profilePhone);
        profession = findViewById(R.id.profileProfession2);
        changePhoto = findViewById(R.id.changeProfilePhoto);

        greyScreen.setVisibility(View.VISIBLE);
        imgUploadProgressBar.setVisibility(View.VISIBLE);

        photo.setAlpha(0.3f);
        changePhoto.setAlpha(0.3f);
        name.setAlpha(0.3f);
        email.setAlpha(0.3f);
        phone2.setAlpha(0.3f);
        profession.setAlpha(0.3f);
        updateName.setAlpha(0.3f);
        updateProfession.setAlpha(0.3f);
        verify.setAlpha(0.3f);
        phone.setAlpha(0.3f);
        cc.setAlpha(0.3f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + uID + "/profileImg.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                File source = new File(getRealPathFromURI(imageUri));
                File destination = new File(imgPath);
                try
                {
                    FileUtils.copyFile(source, destination);
                }
                catch (IOException e)
                {
                    Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                Toast.makeText(Dashboard.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imgUploadProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadDataToFirebase(Uri fileUri) {
        Toast.makeText(Dashboard.this, "Uploading your data to the cloud...", Toast.LENGTH_LONG).show();

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + uID + "/myfile.txt");
        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Dashboard.this, "Data uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadChecklistToFirebase(Uri fileUri) {
        //Toast.makeText(Dashboard.this, "Uploading your checklist to the cloud...", Toast.LENGTH_LONG).show();

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + uID + "/toDoListDatabase");
        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(Dashboard.this, "Checklist data uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void downloadPhoto(Uri uri) {
        if (isOnline()) {

            imgUploadProgressBar = findViewById(R.id.imgProgressBar);
            ImageView photo = findViewById(R.id.profile_photo);
            Button verify = findViewById(R.id.phoneVerify);
            LinearLayout greyScreen = findViewById(R.id.greyScreen);
            EditText cc = findViewById(R.id.CC2);
            EditText phone = findViewById(R.id.personPhone2);
            MaterialButton updateName = findViewById(R.id.updateName);
            MaterialButton updateProfession = findViewById(R.id.updateProfession);
            TextView name, email, phone2, profession, changePhoto;
            name = findViewById(R.id.profileName);
            email = findViewById(R.id.profileEmail);
            phone2 = findViewById(R.id.profilePhone);
            profession = findViewById(R.id.profileProfession2);
            changePhoto = findViewById(R.id.changeProfilePhoto);

            greyScreen.setVisibility(View.VISIBLE);
            imgUploadProgressBar.setVisibility(View.VISIBLE);

            photo.setAlpha(0.3f);
            changePhoto.setAlpha(0.3f);
            name.setAlpha(0.3f);
            email.setAlpha(0.3f);
            phone2.setAlpha(0.3f);
            profession.setAlpha(0.3f);
            updateName.setAlpha(0.3f);
            updateProfession.setAlpha(0.3f);
            verify.setAlpha(0.3f);
            phone.setAlpha(0.3f);
            cc.setAlpha(0.3f);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            DownloadManager downloadManager = (DownloadManager) Dashboard.this.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);

            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }

            request.setDestinationInExternalFilesDir(Dashboard.this, "users/"
                    + uID, "profileImg.jpg");

            downloadID = downloadManager.enqueue(request);
        } else {
            Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_LONG).show();
        }
    }

    public void downloadFile(Uri uri) {

        deleteEvents();

        File f = new File(path + "/myfile.txt");
        if (f.exists())
            f.delete();

        DownloadManager downloadManager = (DownloadManager) Dashboard.this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(Dashboard.this, "users/"
                + uID, "myfile.txt");

        downloadID2 = downloadManager.enqueue(request);
    }

    public void downloadFile2(Uri uri) {

        File f = new File(path + "/toDoListDatabase");
        if (f.exists()) f.delete();

        DownloadManager downloadManager = (DownloadManager) Dashboard.this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(Dashboard.this, "users/"
                + uID, "toDoListDatabase");

        downloadID3 = downloadManager.enqueue(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    public void add_category() {
        Toast.makeText(Dashboard.this, "adding", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeData();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertEvents() {
        ContentResolver contentResolver;
        contentResolver = getContentResolver();

        for(event e : events) {
            Calendar cal = e.getCalendar();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.TITLE, e.getName());
            contentValues.put(CalendarContract.Events.DESCRIPTION, e.getDescription());
            contentValues.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
            contentValues.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
            contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
            if(uri == null) {
                Toast.makeText(this, "Here while inserting: " + e.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }

            long eventID = Long.parseLong(uri.getLastPathSegment());
            e.setEventId(eventID);


            contentValues = new ContentValues();
            contentValues.put(CalendarContract.Reminders.MINUTES, 1);
            contentValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
            contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

            uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

            contentValues.put(CalendarContract.Reminders.MINUTES, 10);
            uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

            contentValues.put(CalendarContract.Reminders.MINUTES, 30);
            uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);

            contentValues.put(CalendarContract.Reminders.MINUTES, 60);
            uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);


            if (uri == null) {
                Toast.makeText(this, "Reminder here while inserting: " + e.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public boolean deleteEvents() {
        ContentResolver contentResolver;
        contentResolver = getContentResolver();
        for(event e : events) {

            String where = "_id =" + e.getEventId() + " and " + CalendarContract.Events.CALENDAR_ID + "=" + 1;
            int uri = contentResolver.delete(CalendarContract.Events.CONTENT_URI, where, null);

            if(uri == 0) {
                Toast.makeText(this, "Error in deleteEvents: " + e.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public void readData() {
        try {
            Scanner reader = new Scanner(new File(path, "myfile.txt"));
            int count = reader.nextInt();
            reader.nextLine();
            while (count != 0) {
                reader.nextLine();
                int c_ID = reader.nextInt();
                reader.nextLine();
                String c_Name = reader.nextLine();
                String c_shortDes = reader.nextLine();
                String c_ImgURI = reader.nextLine();
                int item_count = reader.nextInt();
                reader.nextLine();
                ArrayList<item> c_items = new ArrayList<>();
                while (item_count != 0) {
                    String i_ID = reader.nextLine();
                    int c_id = reader.nextInt();
                    reader.nextLine();
                    String i_name = reader.nextLine();
                    String i_price = reader.nextLine();
                    String i_quantity = reader.nextLine();
                    String i_imgURI = reader.nextLine();
                    String i_shareURI = reader.nextLine();
                    c_items.add(new item(i_ID, c_id, i_name, i_price, i_quantity, i_imgURI, i_shareURI));
                    item_count--;
                }
                reader.nextLine();
                categories.add(new category(c_ID, c_Name, c_shortDes, c_ImgURI, c_items));
                count--;

            }

            int countEvent = reader.nextInt();
            reader.nextLine();
            while(countEvent != 0)
            {
                reader.nextLine();
                String e_ID = reader.nextLine();
                long e_eventID = Long.parseLong(reader.nextLine());
                String e_Name = reader.nextLine();
                String e_shortDes = reader.nextLine();
                String e_Date = reader.nextLine();
                String e_Time = reader.nextLine();
                reader.nextLine();
                events.add(new event(e_ID,e_Name,e_shortDes,e_Date,e_Time, e_eventID));
                countEvent--;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            File f = new File(Environment.getExternalStorageDirectory(),
                    "/Android/data/com.example.oops_project/files/users/" + uID + "/");
            f.mkdirs();

        }
    }

    public void writeData() {
        try {
            FileWriter fw = new FileWriter(new File(path, "myfile.txt"));
            fw.write(categories.size() + "\n");
            for (category i : categories) {
                fw.write(i.toString());
            }

            fw.write(events.size() + "\n");
            for (event i : events) {
                fw.write(i.toString());
            }

            fw.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void checkPermission(String[] permissions, int[] requestCode)
    {
        ActivityCompat.requestPermissions(Dashboard.this, permissions, 100);
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