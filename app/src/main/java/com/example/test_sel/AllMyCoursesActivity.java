package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.test_sel.Callbacks.CallBack_ArrayReady;
import com.example.test_sel.Callbacks.CallBack_StringValueReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.CoursePerUser;
import com.example.test_sel.Classes.MyFireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AllMyCoursesActivity extends AppCompatActivity {
    private RecyclerView courseRecyclerView;
    private PostAdapter postAdapter;
    private ImageView image_AddMentor;
    private FirebaseAuth fAuth;
    DatabaseReference myRef;
    Boolean isvisit;
    private Toolbar toolBar;
    Intent visit;

    private String currentUserId = "", username, visitorUserId = "", userId = "";

    ArrayList<CoursePerUser> myCoursePerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_courses);

        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();

        image_AddMentor = findViewById(R.id.image_AddMentor);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        if (getIntent().getStringExtra(getString(R.string.intentsExtrasVisiitUserId)) != null) {
            userId = getIntent().getExtras().getString(getString(R.string.intentsExtrasVisiitUserId));
            isvisit = true;
        } else {
            userId = fAuth.getCurrentUser().getUid();
        }
        //get user name
        MyFireBase.getStringValue(FirebaseDatabase.getInstance().getReference().child(getString(R.string.globalKeysUsers))
                .child(userId), getString(R.string.globalKeysFullName), new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                username = value;
            }

            @Override
            public void error() {

            }
        });

        MyFireBase.getStringValue(FirebaseDatabase.getInstance().getReference().child(getString(R.string.globalKeysUsers))
                .child(userId), getString(R.string.globalKeysUserId), new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                visitorUserId = value;
            }

            @Override
            public void error() {

            }
        });
        //check if the user is a visitor

        if (!((currentUserId.trim()).equals(userId.trim()))) {
            visit = new Intent();
            visit.putExtra(getString(R.string.intentsExtrasYes), getString(R.string.intentsExtrasYes));
            image_AddMentor.setVisibility(View.GONE);
        }

        //get CoursesPerUser list to show in recycle
        myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.globalKeysUsers)).child(userId).child(getString(R.string.globalKeysCoursesInUser));
        MyFireBase.getCoursesPerUser(myRef, new CallBack_ArrayReady<CoursePerUser>() {
            @Override
            public void arrayReady(ArrayList<CoursePerUser> coursePerUsers) {
                AllMyCoursesActivity.this.myCoursePerUsers = coursePerUsers;
                buildCourseList(AllMyCoursesActivity.this.myCoursePerUsers);
            }

            @Override
            public void error() {

            }
        });

        image_AddMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PostActivity.class);
                i.putExtra(getString(R.string.intentsExtrasKind), getString(R.string.intentsExtrasAddCourse));
                i.putExtra(getString(R.string.intentsExtrasUserName), username);
                i.putExtra(getString(R.string.intentsExtrasUserId), userId);
                startActivity(i);
                finish();

            }
        });
        //search part
        EditText editText = findViewById(R.id.edt_search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
    }

    private void filter(String text) {
        ArrayList<CoursePerUser> courseArrRec = new ArrayList<>();
        for (CoursePerUser u : myCoursePerUsers) {
            if (u.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
            if (u.getCourseCode().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
        }

        buildCourseList(courseArrRec);

    }

    private void buildCourseList(ArrayList<CoursePerUser> courses) {
        ArrayList<CardInfo> cards = new ArrayList<>(courses.size());
        for (CardInfo cardInfo : courses) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        courseRecyclerView = findViewById(R.id.RCL_courseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (visit != null) {
            postAdapter = new PostAdapter(this, cards, visit);
        } else {
            postAdapter = new PostAdapter(this, cards);
        }
        postAdapter.setClickListener(myItemClickListener);
        courseRecyclerView.setAdapter(postAdapter);
    }

    private PostAdapter.ItemClickListener myItemClickListener = new PostAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();


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
            startActivity(new Intent(getApplicationContext(), SearchMentoringActivity.class));
            finish();
        }

        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------

