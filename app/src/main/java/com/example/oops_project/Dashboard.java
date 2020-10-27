package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton add_button;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;
    private ImageView user_image;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // creating nav drawer

        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        toggler = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggler);
        toggler.setDrawerIndicatorEnabled(true);
        toggler.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggler.syncState();

        //creating add button

        add_button = findViewById(R.id.add);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Dashboard.this, "adding", Toast.LENGTH_SHORT).show();
            }
        });

        //load default fragment

        frag_manager = getSupportFragmentManager();
        frag_trans = frag_manager.beginTransaction();
        frag_trans.add(R.id.container_fragment , new frag_inventory());
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
                frag_trans.replace(R.id.container_fragment , new frag_inventory());
                frag_trans.commit();
                break;

            case R.id.events_list:
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_events());
                frag_trans.commit();
                break;

            case R.id.notes_list:
                add_button.show();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_notes());
                frag_trans.commit();
                break;

            case R.id.account:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_account());
                frag_trans.commit();
                /*String n = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                mCC = findViewById(R.id.CC2);
                mEnteredPhone = findViewById(R.id.personPhone2);
                verify = findViewById(R.id.phoneVerify);

               if(n != null && !TextUtils.isEmpty(n) && !TextUtils.isDigitsOnly(n)) {
                    mCC.setVisibility(View.GONE);
                    mEnteredPhone.setVisibility(View.GONE);
                    verify.setVisibility(View.GONE);
                }

                name = findViewById(R.id.profileName);
                email = findViewById(R.id.profileEmail);
                phone = findViewById(R.id.profilePhone);

                firebaseFirestore = FirebaseFirestore.getInstance();

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            name.setText(value.getString("name"));
                            email.setText(value.getString("email"));
                            phone.setText(value.getString("phone"));
                        }
                    }
                });*/
                break;

            case R.id.sharing:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_sharing());
                frag_trans.commit();
                break;

            case R.id.settings:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_setting());
                frag_trans.commit();
                break;

            case R.id.feedback:
                add_button.hide();
                frag_manager = getSupportFragmentManager();
                frag_trans = frag_manager.beginTransaction();
                frag_trans.replace(R.id.container_fragment , new frag_feedback());
                frag_trans.commit();
                break;

            case R.id.logout:
                //Toast.makeText(this, "Back to Aneesh", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}