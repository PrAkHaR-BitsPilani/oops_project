package com.example.oops_project;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Dashboard extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        frag_account.frag_account_events {

    private final ArrayList<category> categories = new ArrayList<>();
    String imgPath;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/com.example.oops_project/files/users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    EditText mCC, mEnteredPhone;
    String passPhone;
    long downloadID, downloadID2, downloadID3;
    String uID;
    Toolbar toolbar;
    private FloatingActionButton add_button;
    private DrawerLayout drawer;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView userNameNav, professionNav;
    private ProgressBar imgUploadProgressBar;
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
                finish();
                startActivity(getIntent());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

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
                    String nm[] = name.split(" ", 2);
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
                    String i_name = reader.nextLine();
                    String i_price = reader.nextLine();
                    String i_quantity = reader.nextLine();
                    String i_imgURI = reader.nextLine();
                    c_items.add(new item(i_ID, i_name, i_price, i_quantity, i_imgURI));
                    item_count--;
                }
                reader.nextLine();
                categories.add(new category(c_ID, c_Name, c_shortDes, c_ImgURI, c_items));
                count--;

            }
            //Toast.makeText(this, "Data Read", Toast.LENGTH_SHORT).show();
            reader.close();
        } catch (FileNotFoundException e) {
            File f = new File(Environment.getExternalStorageDirectory(),
                    "/Android/data/com.example.oops_project/files/users/" + uID + "/");
            f.mkdirs();

        }

        frag_manager = getSupportFragmentManager();
        frag_trans = frag_manager.beginTransaction();

        frag_trans.add(R.id.container_fragment, new frag_inventory(categories, add_button, toolbar));

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
                frag_trans.replace(R.id.container_fragment, new frag_inventory(categories, add_button, toolbar));
                frag_trans.commit();
                break;

            case R.id.events_list:
                toolbar.setTitle("Events");
                ArrayList<event> events = new ArrayList<>();
                events.add(new event("0", "Meeting with Panda", "Presentation of your app"));
                events.add(new event("1", "Meeting with Manjanna", "Presentation of your assignment"));
                events.add(new event("2", "Meeting with Venkata", "Presentation of your assignment"));
                events.add(new event("3", "Meeting with Rishi", "Presentation of your solution"));

                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_events(events, add_button));
                frag_trans.commit();
                break;

            case R.id.todo_list:
                toolbar.setTitle("Checklist");
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new ToDoFrag(add_button));
                frag_trans.commit();
                break;

            case R.id.account:
                toolbar.setTitle("Account");
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_account());
                frag_trans.commitNow();
                break;

            case R.id.upload:
                try {
                    FileWriter fw = new FileWriter(new File(path, "myfile.txt"));
                    fw.write(categories.size() + "\n");
                    for (category i : categories) {
                        fw.write(i.toString());
                    }
                    fw.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                if (isOnline()) {
                    File file = new File(path + "/myfile.txt");
                    if (file.exists()) {
                        uploadDataToFirebase(Uri.fromFile(new File(path + "/myfile.txt")));
                    }
                    file = new File(path + "/toDoListDatabase");
                    if (file.exists()) {
                        uploadChecklistToFirebase(Uri.fromFile(file));
                    }
                } else {
                    Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.download:
                if (isOnline()) {
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
                } else {
                    Toast.makeText(this, "Internet connection is not present!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(Dashboard.this, "Logout successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
                finish();
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
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Toast.makeText(Dashboard.this, "Uploading your photo to the cloud...", Toast.LENGTH_LONG).show();

        imgUploadProgressBar = findViewById(R.id.imgProgressBar);
        imgUploadProgressBar.setVisibility(View.VISIBLE);

        ImageView photo = findViewById(R.id.profile_photo);
        photo.setAlpha(0.3f);

        LinearLayout greyScreen = findViewById(R.id.greyScreen);
        greyScreen.setVisibility(View.VISIBLE);

        Button verify = findViewById(R.id.phoneVerify);
        verify.setAlpha(0.3f);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + uID + "/profileImg.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadPhoto(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Dashboard.this, "Error! Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                });
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
                Toast.makeText(Dashboard.this, "Inventory data uploaded!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Dashboard.this, "Checklist data uploaded!", Toast.LENGTH_SHORT).show();
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
            imgUploadProgressBar.setVisibility(View.VISIBLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            ImageView photo = findViewById(R.id.profile_photo);
            photo.setAlpha(0.3f);

            Button verify = findViewById(R.id.phoneVerify);
            verify.setAlpha(0.3f);

            LinearLayout greyScreen = findViewById(R.id.greyScreen);
            greyScreen.setVisibility(View.VISIBLE);

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

        File f = new File(path + "/myfile.txt");
        if (f.exists()) f.delete();

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

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + uID;

        try {
            FileWriter fw = new FileWriter(new File(path, "myfile.txt"));
            fw.write(categories.size() + "\n");
            for (category i : categories) {
                fw.write(i.toString());
            }
            fw.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
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

    public void writeData()
    {
        try {
            FileWriter fw = new FileWriter(new File(path, "myfile.txt"));
            fw.write(categories.size() + "\n");
            for (category i : categories) {
                fw.write(i.toString());
            }
            fw.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}