package com.example.test_sel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_sel.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserSignUpActivity extends AppCompatActivity {
    //  https://medium.com/@hannaholukoye/adding-an-icon-on-a-spinner-on-android-e99c7bc6c180

    private EditText EDT_full_nameET, EDT_start_year, EDT_phone_numberET, EDT_description;
    private ImageView img_head;
    private Spinner SPN_engineering, SPN_academy;
    private Button BTNsign_up;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private ProgressBar ProgressBar;
    private String fullName, phone, academy, start_year, userId, imagePath = "", engineering = "", description = "";
    private DatabaseReference rootDataBase;

    private DatabaseReference refuser;

    private boolean isSignUp = true;
    Intent deleteIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        EDT_full_nameET = findViewById(R.id.EDT_full_nameET);
        EDT_phone_numberET = findViewById(R.id.EDT_phone_numberET);
        EDT_start_year = findViewById(R.id.EDT_start_year);
        BTNsign_up = findViewById(R.id.BTNsign_up);
        img_head = findViewById(R.id.IMG_signup_img);
        EDT_description = findViewById(R.id.EDT_description);

        String type = getIntent().getExtras().getString(getString(R.string.intentsExtrasType));
        isSignUp = type.equals(getString(R.string.intentsExtrasSignUp)) ? true : false;
        if (isSignUp) {
            EDT_phone_numberET.setText(getIntent().getExtras().getString(getString(R.string.intentsExtrasPhone)));
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ProgressBar = findViewById(R.id.pbarReg);
        userId = fAuth.getCurrentUser().getUid();

        //save data on firebase
        rootDataBase = FirebaseDatabase.getInstance().getReference(getString(R.string.globalKeysUsers));

        if (isSignUp) {
            userId = fAuth.getCurrentUser().getUid();
            phone = getIntent().getExtras().getString(getString(R.string.intentsExtrasPhone));
        } else {
            img_head.setImageResource(R.drawable.img_update);
            BTNsign_up.setText(getString(R.string.userSignUpActivityUpdate));
            setMyData();
            userId = getIntent().getExtras().getString(getString(R.string.intentsExtrasUserId));
        }


        //acadany spinner
        SPN_academy = findViewById(R.id.SPN_academy);

        ArrayAdapter<CharSequence> academicAdapter = ArrayAdapter.createFromResource(this, R.array.academy, android.R.layout.simple_spinner_item);
        academicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPN_academy.setAdapter(academicAdapter);
        SPN_academy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                academy = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    //select nothing
                    SPN_academy.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //engineering spinner
        SPN_engineering = findViewById(R.id.SPN_engineering);


        ArrayAdapter<CharSequence> engineeringAdapter = ArrayAdapter.createFromResource(this, R.array.engineering, android.R.layout.simple_spinner_item);
        engineeringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPN_engineering.setAdapter(engineeringAdapter);

        SPN_engineering.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                engineering = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    //select nothing
                    SPN_engineering.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //if user  save details
        BTNsign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = EDT_full_nameET.getText().toString();
                phone = EDT_phone_numberET.getText().toString().trim();
                start_year = EDT_start_year.getText().toString().trim();
                description = EDT_description.getText().toString().trim();


                //checks
                if (TextUtils.isEmpty(fullName)) {
                    EDT_full_nameET.setError(getText(R.string.userSignUpRequiredName));
                    return;
                }
                if (TextUtils.isEmpty(start_year)) {
                    EDT_start_year.setError(getText(R.string.userSignUpRequiredStartYear));
                    return;
                }
                if (Integer.parseInt(start_year) > 2020 || Integer.parseInt(start_year) < 2000) {
                    EDT_start_year.setError(getText(R.string.userSignUpActivityStartYearNotLegal));
                    return;
                }

                //ProgressBar after all the check
                ProgressBar.setVisibility(View.VISIBLE);

                User user = new User(userId, fullName, phone, academy, start_year, engineering, imagePath, description);

                // insert to the firebsae
                rootDataBase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isSignUp = false;
                            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                            finish();
                        } else {
                            Toast.makeText(UserSignUpActivity.this, R.string.userSignUpActivityDataNotInserted, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }


    public boolean isEDempty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.setError(getText(R.string.userSignUpActivityBoxEmpty));
            return true;
        }
        return false;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void setMyData() {

        //get data
        refuser = FirebaseDatabase.getInstance().getReference().child(getString(R.string.globalKeysUsers)).child(userId);
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EDT_full_nameET.setText(dataSnapshot.child(getString(R.string.globalKeysFullName)).getValue().toString());
                EDT_phone_numberET.setText(dataSnapshot.child(getString(R.string.globalKeysPhone)).getValue().toString());
                //  txt_fill_academy.setText(dataSnapshot.child(getString(R.string.globalKeysAcademy)).getValue().toString());
                EDT_start_year.setText(dataSnapshot.child(getString(R.string.globalKeysStartYear)).getValue().toString());
                EDT_description.setText(dataSnapshot.child(getString(R.string.globalKeysDescription)).getValue().toString());
                //txt_fill_engineering.setText(dataSnapshot.child(getString(R.string.globalKeysEngineering)).getValue().toString());
                imagePath = dataSnapshot.child(getString(R.string.globalKeysImagePath)).getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        if (isSignUp) {
            fAuth.signOut();
        }
        super.onStop();
    }
}
