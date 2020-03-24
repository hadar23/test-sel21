package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    /////camera/////

    //permissions
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    //image pick constatn
    public static final int IMAGE_PICK_CAMERA_CODE = 300;
    public static final int IMAGE_PICK_GALLERY_CODE = 400;

    public TextView txt_fill_allCourses, txt_head_allCourses;

    //image picked will be in thie uri
    Uri image_uri = null;

    String[] cameraPermission;
    String[] StoragePermission;
/////camera/////

    private String[] engineeringArray, acadenyArray;
    private TextView txt_fill_academy, txt_fill_start_year, txt_fill_engineering, txt_Description, txt_fill_name, txt_fill_phone;
    private ImageView img_edit, img_camera, img_profile, ic_whatsapp, img_add, img_schedule;
    private Button btn_course, BTN_schedule, btn_add;
    private String currentPhotoPath, userId;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DatabaseReference userDBR;
    private StorageReference fstorage;
    private Toolbar toolBar;

    //  private DocumentReference documentRef;
    private ProgressDialog pd;

    private Boolean isPicked;

    private DatabaseReference refuser;
    private DatabaseReference refImage;
    //pgoto
    private StorageReference storageReferanc;
    //path where the image of the user will stored
    final static String storagePath = "user_profile_image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nice);

        //init
        findById();

//TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Profile");

/////camera premissions/////
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        StoragePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//arra from R
        engineeringArray = getResources().getStringArray(R.array.engineering);
        acadenyArray = getResources().getStringArray(R.array.academy);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance().getReference();

        if (getIntent().getStringExtra(getString(R.string.intentsExtrasVisiitUserId)) != null) {
            userId = getIntent().getExtras().getString(getString(R.string.intentsExtrasVisiitUserId));
            img_edit.setVisibility(View.INVISIBLE);
            img_camera.setVisibility(View.INVISIBLE);
            img_schedule.setVisibility(View.INVISIBLE);
        } else {
            userId = fAuth.getCurrentUser().getUid();
        }

        //init progress dialog
        pd = new ProgressDialog(ProfileActivity.this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(img_profile);
            }
        }

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage(getText(R.string.profileActivityUpdatingProfilePicture));
                showPicDialog();
            }
        });

        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserSignUpActivity.class);
                i.putExtra(getString(R.string.intentsExtrasUpdate), getString(R.string.intentsExtrasUpdate));
                i.putExtra(getString(R.string.intentsExtrasUserId), userId);
                i.putExtra(getString(R.string.intentsExtrasType), getString(R.string.intentsExtrasUpdate));
                startActivity(i);
                finish();
                // showEditDialog();
            }
        });

        ic_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AllMyCoursesActivity.class);
                i.putExtra(getString(R.string.intentsExtrasVisiitUserId), userId);
                startActivity(i);


            }
        });

        img_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ScheduleActivity.class);
                i.putExtra(getString(R.string.intentsExtrasIsHost), true);
                startActivity(i);
            }
        });

        //get data
        refuser = FirebaseDatabase.getInstance().getReference().child(getString(R.string.globalKeysUsers)).child(userId);
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_fill_name.setText(dataSnapshot.child(getString(R.string.globalKeysFullName)).getValue().toString());
                txt_fill_phone.setText(dataSnapshot.child(getString(R.string.globalKeysPhone)).getValue().toString());
                txt_fill_academy.setText(dataSnapshot.child(getString(R.string.globalKeysAcademy)).getValue().toString());
                txt_fill_start_year.setText(dataSnapshot.child(getString(R.string.globalKeysStartYear)).getValue().toString());
                txt_fill_engineering.setText(dataSnapshot.child(getString(R.string.globalKeysEngineering)).getValue().toString());
//                txt_fill_allCourses.setText(dataSnapshot.child("userCourses").getValue().toString());
                txt_Description.setText(dataSnapshot.child(getString(R.string.globalKeysDescription)).getValue().toString());
                //image
                String imagePath = dataSnapshot.child(getString(R.string.globalKeysImagePath)).getValue().toString();
                try {
                    //if image is recieves then set
                    Picasso.get().load(imagePath).into(img_profile);
                } catch (Exception e) {
                    //if ther is ane error while getting image
                    Picasso.get().load(R.drawable.ic_person).into(img_profile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void findById() {
        //init
        txt_fill_name = findViewById(R.id.txt_fill_name);
        txt_fill_phone = findViewById(R.id.txt_fill_phone);
        txt_fill_academy = findViewById(R.id.txt_fill_academy);
        txt_fill_start_year = findViewById(R.id.txt_fill_start_year);
        txt_fill_engineering = findViewById(R.id.txt_fill_engineering);
        txt_Description = findViewById(R.id.txt_Description);
        //txt_fill_allCourses = findViewById(R.id.txt_fill_allCourses);
        txt_head_allCourses = findViewById(R.id.txt_head_allCourses);
        img_edit = findViewById(R.id.img_edit);
        img_profile = findViewById(R.id.img_profile);
        img_camera = findViewById(R.id.img_camera);
        ic_whatsapp = findViewById(R.id.ic_whatsapp);
//        btn_course=findViewById(R.id.BTN_AllCourse);
//        BTN_schedule=findViewById(R.id.BTN_schedule);
//        btn_add=findViewById(R.id.BTN_add);
        img_add = findViewById(R.id.img_add);
        img_schedule = findViewById(R.id.img_schedule);


    }

    private void sendMessage() {
        Uri uri = Uri.parse("smsto:" + txt_fill_phone.getText().toString());
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));


    }

    //----------------START CAMERA METHODS------------------------------------------
/////camera /////
    private boolean checkStoragePermissions() {
        //check if storage permission enale or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, StoragePermission, GALLERY_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        //check if camera permission enale or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        //intent to pick image from gallery
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, getString(R.string.profileActivityTempPick));
        cv.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.profileActivityTempDescription));
        //put image uri
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(getString(R.string.intentsExtrasImagePlus));
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this method is called when user press Aloow or Deny from permission request dialog
        //here we will handle with permission cases(allow or denny)
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission enable
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(this, R.string.profileActivityCameraStoragePermissionRequired, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case GALLERY_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, R.string.profileActivityStoragePermissionRequired, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                img_profile.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                img_profile.setImageURI(image_uri);
            }
        }
        uploadImage(image_uri);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadImage(Uri uri) {
        if (uri == null){
            Toast.makeText(this, "no image", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("trytry", "heyhey");
        pd.setMessage(getText(R.string.profileActivityUploadImage));
        pd.show();
        String filePathName = storagePath + "_" + userId;

        StorageReference storageReference2 = fstorage.child(filePathName);
        storageReference2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while ((!uriTask.isSuccessful())) ;
                Uri downloadUri = uriTask.getResult();
                //check if image is upload or not and uri is recieve
                if (uriTask.isSuccessful()) {
                    //image is upload
                    //add / update uri in user database
                    updateDataToDoc(downloadUri.toString(), getString(R.string.globalKeysImagePath));
                } else {
                    //error
                    pd.dismiss();
                    Toast.makeText(ProfileActivity.this, R.string.profileActivitySomeError, Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                Toast.makeText(ProfileActivity.this, R.string.profileActivityCanceled, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void showPicDialog() {
        //option to show in dialog
        String option[] = {getString(R.string.profileActivityCamera), getString(R.string.profileActivityGallery)};
        //allert dialog
        // his line :AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        //set title
        addMytitle(builder, getString(R.string.profileActivityPickImageFrom));

        // set item to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user choose camera
                if (which == 0) {
                    Toast.makeText(ProfileActivity.this, R.string.profileActivityChooseFromCamera, Toast.LENGTH_SHORT).show();
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions();
                    } else {
                        pickFromCamera();

                    }
//                    handLeImageClicike(img_camera);
                }
                //user choose gallery
                else if (which == 1) {
                    Toast.makeText(ProfileActivity.this, R.string.profileActivityChooseFromGallery, Toast.LENGTH_SHORT).show();
                    if (!checkStoragePermissions()) {
                        requestStoragePermissions();
                    } else {
                        pickFromGallery();


                    }
                    //                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                }
            }
        });
        builder.create().show();

    }

//----------------END CAMERA METHODS------------------------------------------


//----------------start details METHODS------------------------------------------

    private void addMytitle(AlertDialog.Builder builder, String s) {
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText(s);
        //TODO
        title.setBackgroundColor(((ContextCompat.getColor(this, R.color.alertTitleColor))));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);

        builder.setCustomTitle(title);
    }

    public void updateDataToDoc(final String newValue, String key) {
        if (image_uri == null) {
            Toast.makeText(this, "no image", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();
        refuser.child(key).setValue(newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updat
                        pd.dismiss();
                        Toast.makeText(ProfileActivity.this, R.string.profileActivityUpdatingProfilePicture, Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ProfileActivity.this, R.string.profileActivityErrorUploadingFirebase, Toast.LENGTH_SHORT).show();

            }
        });
    }

//----------------end details METHODS------------------------------------------

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
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------
