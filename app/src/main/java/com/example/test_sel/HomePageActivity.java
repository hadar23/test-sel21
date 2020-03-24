package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {
    GridLayout mainGrid;
    Context context = this;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //set EVENT
        setSingleEvent(mainGrid);

    }

    private void setSingleEvent(GridLayout mainGrid) {

        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            CardView c = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            Intent intent = new Intent(context, ScheduleActivity.class);
                            intent.putExtra(getString(R.string.intentsExtrasIsHost), true);
                            startActivity(intent);
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(), PostActivity.class));
                            break;
                        case 2:
                            //message
                            startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(), SearchMentoringActivity.class));
                            break;
                        case 4:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            break;
                        case 5:
                            startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                            break;
                    }
                }
            });
        }
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
//            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
//            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------



