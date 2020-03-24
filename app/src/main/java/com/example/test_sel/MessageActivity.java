package com.example.test_sel;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_sel.Callbacks.CallBack_ArrayReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.Message;
import com.example.test_sel.Classes.MyFireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView courseRecyclerView;
    private PostAdapter postSearchAdapter;
    private ImageView image_mentoring;
    private ArrayList<Message> messages;
    Toolbar toolBar;

    private FirebaseAuth fAuth;
    private String userId;
    private DatabaseReference refUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        image_mentoring = findViewById(R.id.image_mentoring);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        refUser = FirebaseDatabase.getInstance().getReference().child(getString(R.string.globalKeysUsers)).child(userId);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        image_mentoring.setImageResource(R.drawable.img_massages);

        if (getIntent().getStringExtra(getString(R.string.intentsExtrasAddCourse))!=null) {
            image_mentoring.setVisibility(View.VISIBLE);
        }

        MyFireBase.getMessages(refUser.child(getString(R.string.messageActivityMessages)), new CallBack_ArrayReady<Message>() {
            @Override
            public void arrayReady(ArrayList<Message> array) {
                messages = array;
                buildMessageList(messages);
            }

            @Override
            public void error() {
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
        ArrayList<Message> messageArrRec = new ArrayList<>();
        for (Message message : messages) {
            if (message.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                messageArrRec.add(message);
            }
            if (message.getCourseCode().contains(text.toLowerCase())) {
                messageArrRec.add(message);
            }
            if (message.getHostName().contains(text.toLowerCase())) {
                messageArrRec.add(message);
            }

        }

        buildMessageList(messageArrRec);

    }

    private void buildMessageList(ArrayList<Message> messagesList) {
        ArrayList<CardInfo> cards = new ArrayList<>(messagesList.size());
        for (CardInfo cardInfo : messagesList) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        courseRecyclerView = findViewById(R.id.RCL_courseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras() != null) {
            postSearchAdapter = new PostAdapter(this, cards,getIntent());
        } else {
            postSearchAdapter = new PostAdapter(this, cards);
        }
        postSearchAdapter.setClickListener(myItemClickListener);
        courseRecyclerView.setAdapter(postSearchAdapter);
    }

    private PostAdapter.ItemClickListener myItemClickListener = new PostAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

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
            finishAffinity();
//            finish();
        }
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(getApplicationContext(), SearchMentoringActivity.class));
            finish();
        }

        if (item.getItemId() == R.id.home) {
//            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------


