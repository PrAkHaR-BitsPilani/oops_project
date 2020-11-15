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
import android.widget.EditText;
import android.widget.ImageView;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Scanner;

public class Dashboard extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        frag_account.frag_account_events {

    String imgPath;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
    EditText mCC, mEnteredPhone;
    String passPhone;
    long downloadID, downloadID2;
    private FloatingActionButton add_button;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView userNameNav;
    private ImageView profileImageNav;
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
                profileImageNav.setImageResource(0);
                profileImageNav.setImageURI(Uri.parse(imgPath));
                Toast.makeText(getApplicationContext(), "Profile picture updated successfully!", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
            else if(downloadID2 == id)
            {
                Toast.makeText(getApplicationContext(), "Data downloaded successfully!", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        }
    };
    private final ArrayList<category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/profileImg.jpg";

        // creating nav drawer

        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard_page);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        toggler = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggler);
        toggler.setDrawerIndicatorEnabled(true);
        toggler.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggler.syncState();
        View headerView = navigationView.getHeaderView(0);
        userNameNav = headerView.findViewById(R.id.user_name);
        profileImageNav = headerView.findViewById(R.id.user_image);

        File file = new File(imgPath);
        if (file.exists()) {
            profileImageNav.setImageURI(Uri.parse(imgPath));
        }

        //image download progress checker
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {
                    userNameNav.setText(value.getString("name"));

                }
            }
        });
        /*Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/profileImg.jpg";*/

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
                    int i_price = reader.nextInt();
                    reader.nextLine();
                    int i_quantity = reader.nextInt();
                    reader.nextLine();
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

            StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
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
                    // default data
                    ArrayList<item> items_pantry = new ArrayList<>();
                    ArrayList<item> items_stationary = new ArrayList<>();
                    items_pantry.add(new item("0" , "Milk", 24, 5, "https://i0.wp.com/post.healthline.com/wp-content/uploads/2019/11/milk-soy-hemp-almond-non-dairy-1296x728-header-1296x728.jpg?w=1155&h=1528.jpg"));
                    items_pantry.add(new item("1" , "Potato", 30, 5, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTObd35g74rosg3jPC8qxp4vLF_q3f1AFYWvQ&usqp=CAU.jpg"));
                    items_pantry.add(new item("2", "Ketchup", 120,3 ,"https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQgg8YHhZAhDxvA0lpwqpGkIhN_uxVYr57noA&usqp=CAU.jpg"));
                    items_pantry.add(new item("3", "Wheat Flour", 350,5 ,"https://media.gettyimages.com/photos/hessian-sack-of-grain-and-wheat-picture-id157580609?k=6&m=157580609&s=612x612&w=0&h=TDEilPMbcnSrV1odVEmfLLo53jSQpPsYFS-jzBBKskk="));
                    categories.add(new category(0,"Pantry" , "Contains vegetables and other milk products" , "https://static01.nyt.com/images/2020/03/14/dining/23pantry1/23pantry1-superJumbo.jpg" , items_pantry));

                    items_stationary.add(new item("0" , "Pens", 10, 7, "https://media.gettyimages.com/photos/set-of-eight-different-pens-picture-id183057646?k=6&m=183057646&s=612x612&w=0&h=rKO5xoVUmSbMJzpZCyHraDXtZyefVH-N2Na8Ows4rfQ="));
                    items_stationary.add(new item("1" , "Notebook", 30, 4, "https://media.gettyimages.com/photos/back-to-school-education-textbooks-on-desk-chalkboard-picture-id524537058?k=6&m=524537058&s=612x612&w=0&h=o8b740939h1nmH-PYcvBxUUuIghBxd8Yc5TcU3LrwFk="));
                    items_stationary.add(new item("2" , "Battery", 25, 10, "https://media.gettyimages.com/photos/batteries-picture-id1131330918?k=6&m=1131330918&s=612x612&w=0&h=DQuGlGxX1rgEPBOuo8JOmPbpHJt7qwhcOWqulXM-zfg="));
                    categories.add(new category(1,"Stationary" , "Books, pens and other documents" ,  "https://media.gettyimages.com/photos/large-assortment-of-office-supplies-on-white-backdrop-picture-id182060333?k=6&m=182060333&s=612x612&w=0&h=oDNXDX5StoZ_UO4-5UvhKPr1A1OGXADsLus9AbMlG4M=" , items_stationary));
                    try {
                        File f = new File(Environment.getExternalStorageDirectory(),
                                "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/");
                        f.mkdirs();
                        FileWriter fw = new FileWriter(new File(path , "myfile.txt"));
                        fw.write(categories.size() + "\n");
                        for(category i : categories)
                        {
                            fw.write(i.toString());
                        }
                        fw.close();
                        Toast.makeText(Dashboard.this, "Data Written", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } catch (IOException e2) {
                        //Toast.makeText(this, "Data Not Written", Toast.LENGTH_SHORT).show();
                        e2.printStackTrace();
                    }
                }
            });

        }

        frag_manager = getSupportFragmentManager();
        frag_trans = frag_manager.beginTransaction();

        frag_trans.add(R.id.container_fragment, new frag_inventory(categories));

        frag_trans.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.inventory_list:
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_inventory(categories));
                frag_trans.commit();
                break;

            case R.id.events_list:

                ArrayList<event> events = new ArrayList<>();
                events.add(new event("0", "Meeting with Panda", "Presentation of your app"));
                events.add(new event("1", "Meeting with Manjanna", "Presentation of your assignment"));
                events.add(new event("2", "Meeting with Venkata", "Presentation of your assignment"));
                events.add(new event("3", "Meeting with Rishi", "Presentation of your solution"));

                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_events(events));
                frag_trans.commit();
                break;

            case R.id.notes_list:
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_notes());
                frag_trans.commit();
                break;

            case R.id.account:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_account());
                frag_trans.commitNow();
                break;

            case R.id.upload:
                try {
                    FileWriter fw = new FileWriter(new File(path , "myfile.txt"));
                    fw.write(categories.size() + "\n");
                    for(category i : categories)
                    {
                        fw.write(i.toString());
                    }
                    fw.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                uploadDataToFirebase(Uri.fromFile(new File(path+"/myfile.txt")));
                break;

            case R.id.settings:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment, new frag_setting());
                frag_trans.commit();
                break;

            case R.id.download:
                StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
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
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Toast.makeText(Dashboard.this, "Uploading your photo to the cloud...", Toast.LENGTH_LONG).show();
        imgUploadProgressBar = findViewById(R.id.imgProgressBar);
        imgUploadProgressBar.setVisibility(View.VISIBLE);

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profileImg.jpg");
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

        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/myfile.txt");
        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Dashboard.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void downloadPhoto(Uri uri) {
        imgUploadProgressBar = findViewById(R.id.imgProgressBar);
        imgUploadProgressBar.setVisibility(View.VISIBLE);
        DownloadManager downloadManager = (DownloadManager) Dashboard.this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        File file = new File(imgPath);
        if (file.exists()) {
            file.delete();
        }

        request.setDestinationInExternalFilesDir(Dashboard.this, "users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid(), "profileImg.jpg");

        downloadID = downloadManager.enqueue(request);
    }

    public void downloadFile(Uri uri)
    {
        //imgUploadProgressBar = findViewById(R.id.imgProgressBar);
        //imgUploadProgressBar.setVisibility(View.VISIBLE);

        File f = new File(path+"/myfile.txt");
        if(f.exists())f.delete();

        DownloadManager downloadManager = (DownloadManager) Dashboard.this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(Dashboard.this, "users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid(), "myfile.txt");

        downloadID2 = downloadManager.enqueue(request);
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
    protected void onStop()
    {
        super.onStop();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.oops_project/files/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            FileWriter fw = new FileWriter(new File(path , "myfile.txt"));
            fw.write(categories.size() + "\n");
            for(category i : categories)
            {
                fw.write(i.toString());
            }
            fw.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}