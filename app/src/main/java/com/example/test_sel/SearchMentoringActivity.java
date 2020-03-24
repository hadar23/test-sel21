package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchMentoringActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Toolbar topToolBar;
    private ImageView image_mentoring;
    private Intent intent;
    DatabaseReference myRef;
    Fragment fragment = null;
    private Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_activity);

        topToolBar = findViewById(R.id.toolbar);
        image_mentoring = findViewById(R.id.image_mentoring);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setTitle("");

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        //if user click on serach by course ' and then click on the course card
        //list of user that to mentor to the spese course

        if (getIntent().getStringExtra(getString(R.string.intentsExtrasKey)) != null) {
            if (getIntent().getStringExtra(getString(R.string.intentsExtrasType)).equals(getString(R.string.intentsExtrasComeFromCourseFreg))) {
                myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.globalKeysCourses))
                        .child(getIntent().getExtras().getString(getString(R.string.intentsExtrasKey)));
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(getString(R.string.globalKeysUsersList))) {
                            bottomNavigationView.getMenu().getItem(1).setChecked(true);
                            image_mentoring.setImageResource(R.drawable.img_mentoring);
                            fragment = new SearchMentoringFragmentCoursesPerUser();
                            Bundle args = new Bundle();
                            args.putString(getString(R.string.intentsExtrasComeFromCourseFreg), getIntent().getExtras().getString(getString(R.string.intentsExtrasKey)));
                            fragment.setArguments(args);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
                        } else {
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            ///
            if (getIntent().getStringExtra(getString(R.string.intentsExtrasType)).equals(getString(R.string.intentsExtrasComeFromUserFreg))) {
                myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.globalKeysUsers)).child(getIntent().getExtras().getString(getString(R.string.intentsExtrasKey))).child(getString(R.string.globalKeysCoursesInUser));
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            image_mentoring.setImageResource(R.drawable.img_mentoring);
                            fragment = new SearchMentoringFragmentCoursesPerUser();
                            Bundle args = new Bundle();
                            args.putString(getString(R.string.intentsExtrasComeFromUserFreg), getIntent().getExtras().getString(getString(R.string.intentsExtrasKey)));
                            fragment.setArguments(args);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
                        } else {
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }


        bottomNavigationView = findViewById(R.id.bottomBar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, new SearchMentoringFragmentUsers()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.page_courses:

                        image_mentoring.setImageResource(R.drawable.img_search_by_course);
                        fragment = new SearchMentoringFragmentCourses();
//                        Bundle bundle = new Bundle();
//                        bundle.putString(getString(R.string.intentsExtrasType), "userList");
//                        fragment.setArguments(bundle);
                        break;
                    case R.id.page_users:

                        fragment = new SearchMentoringFragmentUsers();
                        image_mentoring.setImageResource(R.drawable.img_search_by_user);
                        break;


                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
                return true;
            }
        });
    }



//----------------START top toolbar------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inFlater = getMenuInflater();
        inFlater.inflate(R.menu.side_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            finish();
            finishAffinity();
        }
        if (item.getItemId() == R.id.search) {
//            startActivity(new Intent(getApplicationContext(), SearchMentoringActivity.class));
//            finish();
        }
        if (item.getItemId() == R.id.home) {
//            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------