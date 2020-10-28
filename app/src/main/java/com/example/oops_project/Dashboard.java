package com.example.oops_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class Dashboard extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        frag_account.frag_account_events
{

    private FloatingActionButton add_button;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager frag_manager;
    private FragmentTransaction frag_trans;
    private boolean doubleBackToExitPressedOnce = false;
    EditText mCC, mEnteredPhone;
    String passPhone;
    private TextView userNameNav;


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
        View headerView = navigationView.getHeaderView(0);
        userNameNav = headerView.findViewById(R.id.user_name);

            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            documentReference.addSnapshotListener(this , new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null) {
                        userNameNav.setText(value.getString("name"));
                    }
                }
            });

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
                frag_trans.replace(R.id.container_fragment , new frag_account(this));
                frag_trans.commitNow();
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

    @Override
    public void verifyBtnClicked() {

        mCC = findViewById(R.id.CC2);
        mEnteredPhone = findViewById(R.id.personPhone2);

        if(TextUtils.isEmpty(mEnteredPhone.getText().toString().trim())) {
            mEnteredPhone.setError("Phone field cannot be empty!");
            return;
        }

        if(!TextUtils.isDigitsOnly(mEnteredPhone.getText().toString().trim())) {
            mEnteredPhone.setError("Phone field must contain only digits!");
            return;
        }

        if(!TextUtils.isDigitsOnly(mCC.getText().toString())) {
            mCC.setError("CC field must contain only digits!");
            return;
        }

        if(mEnteredPhone.getText().toString().trim().length() != 10) {
            mEnteredPhone.setError("Phone field must have 10 digits!");
            return;
        }

        if(TextUtils.isEmpty(mCC.getText().toString())) {
            passPhone = "+91" + mEnteredPhone.getText().toString().trim();
        } else {
            passPhone = "+" + mCC.getText().toString().trim() + mEnteredPhone.getText().toString().trim();
        }

        Intent phone = new Intent(this, VerifyPhone.class);
        phone.putExtra("phone", passPhone);
        startActivity(phone);

    }
}