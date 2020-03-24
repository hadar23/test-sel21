package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class HelpActivity extends AppCompatActivity {
    Toolbar toolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
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

