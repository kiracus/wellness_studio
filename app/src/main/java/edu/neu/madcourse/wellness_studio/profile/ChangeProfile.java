package edu.neu.madcourse.wellness_studio.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class ChangeProfile extends AppCompatActivity {
    // test
    protected static String TAG = "setting";

    protected final static String NAME_HINT = "Username should only contains alphabet and digits.";
    protected final static String NAME_TOAST = "Please enter a valid username within 25 chars.";
    protected final static String MISS_INFO_TOAST = "Please enter both email and password to create account.";
    protected final static String INVALID_INFO_TOAST = "Please enter valid email and password";
    protected final static String AUTH_INFO_SAVED = "Saved successfully.";
    protected final static String AUTH_INFO_NOT_SAVED = "Saved failed. Try again later";

    // VI
    ImageButton saveBtn, cancelBtn;
    EditText usernameInputET, emailInputET, passwordInputET;
    TextView usernameHintTV;
    BottomNavigationView bottomNavigationView;
    ImageView profileImg;

    // db
    protected AppDatabase db;
    protected User user;
    protected OutputStream outputStream;

    // Cloud
    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
    DatabaseReference UsersRef = dbRoot.child("users");
    String uid = "";

    // Firebase Auth
    private FirebaseAuth mAuth;

    // user input
    protected String nicknameInput;
    protected String emailInput;
    protected String passwordInput;
    protected Boolean hasChangedImg = false;

    // user info
    protected Boolean hasOnlineAccount = false;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // initialize auth instance
        mAuth = FirebaseAuth.getInstance();

        // get VI components
        profileImg = findViewById(R.id.imageButton_change_img);
        saveBtn = findViewById(R.id.imageButton_save);
        cancelBtn = findViewById(R.id.imageButton_cancel);

        usernameInputET = findViewById(R.id.username_input_ET);
        emailInputET = findViewById(R.id.email_input_ET);
        passwordInputET = findViewById(R.id.password_input_ET);
        usernameHintTV = findViewById(R.id.username_hint);

        // try set profile picture from local storage
        boolean loadRes = UserService.loadImageForProfile(profileImg);
        if (!loadRes) {
            loadImageFromAssets();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set bottom nav, should have no activated item
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false); // TODO bug here
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return true;
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    goToSleepGoal();
                    return true;
                case R.id.nav_leaderboard:
                    goToLeaderboard();
                    return true;
                default:
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });

        // show username
        usernameInputET.setText(UserService.getNickname(db));

        // check if user already has online account if yes show email and password as dots
        // and disable input area for email and password
        user = UserService.getCurrentUser(db);
        assert user != null;

        if (user.getHasOnlineAccount() != null && user.getHasOnlineAccount()) {
            hasOnlineAccount = true;
            emailInputET.setText(user.getEmail());
            emailInputET.setFocusable(false);
            emailInputET.setTextColor(Color.GRAY);
            passwordInputET.setText("******");
            passwordInputET.setFocusable(false);
            passwordInputET.setTextColor(Color.GRAY);
        }

        // TODO set profile pic, if none use some default img from assets


        // select image, process img and save in local, show img, flag img changed
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "you are clicking the change profile img button");
                if (checkStoragePermission()) {
                    Log.v(TAG, "has permission to access storage, ask user to select img");
                    // show prompt to select new img
                    Intent intent = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 3);  // call onActivityResult
                } else {
                    Log.v(TAG, "no permission to access storage, asking for it");
                    requestStoragePermission();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // check if img changed, if yes change db

                // in case user name has input, check if it's valid
                nicknameInput = usernameInputET.getText().toString();
                if (!nicknameInput.equals(user.nickname)) {  // name is changed
                    if (!nicknameInput.equals("") && Utils.checkValidName(nicknameInput)) {
                        // ignore no input(""), user just deleted the original text
                        user.nickname = nicknameInput;
                    } else if (!nicknameInput.equals("") && !Utils.checkValidName(nicknameInput)) {
                        // not empty and not valid (null or has special chars)
                        Utils.postToast(NAME_TOAST, ChangeProfile.this);
                        usernameHintTV.setText(NAME_HINT);
                    }
                }

                // check if email and password has input
                // in case either email or password has input, show toast and ignore input
                if (!hasOnlineAccount) {
                    emailInput = emailInputET.getText().toString();
                    passwordInput = passwordInputET.getText().toString();

                    Log.v(TAG, "[" + emailInput + "]");
                    Log.v(TAG, passwordInput);

                    if (emailInput.matches("") && passwordInput.matches("")) {
                        // no input, ignore
                        Log.v(TAG, "both empty");
                    } else if (emailInput.matches("") || passwordInput.matches("")) {
                        // only one has input
                        Log.v(TAG, "one empty");
                        Utils.postToast(MISS_INFO_TOAST, ChangeProfile.this);
                    } else {  // both have input, check if valid
                        if (!Utils.checkValidEmail(emailInput) || !Utils.checkValidPassword(passwordInput)) {
                            Utils.postToast(INVALID_INFO_TOAST, ChangeProfile.this);
                        } else {
                            // valid, update user info, create account
                            // online account
                            user.setEmail(emailInput);
                            user.setPassword(passwordInput); // TODO save a token?
                            user.setHasOnlineAccount(true);

                            // Create user with Firebase Auth
                            mAuth.createUserWithEmailAndPassword(user.email, user.password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful()) {
                                                Utils.postToast(AUTH_INFO_SAVED, ChangeProfile.this);
                                            }
                                            else {
                                                Utils.postToast(AUTH_INFO_NOT_SAVED, ChangeProfile.this);
                                            }
                                        }
                            });

                            // Create online id and pass to save
                            UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // can't change, only update once if new online account created
                                    DatabaseReference saveKey = UsersRef.push();
                                    uid = saveKey.getKey();
                                    DatabaseReference newUserRef = UsersRef.child(uid);
                                    newUserRef.child("name").setValue(user.nickname);
                                    newUserRef.child("email").setValue(user.email);
                                    newUserRef.child("img").setValue(user.profileImg);
                                    newUserRef.child("friends").setValue("");

                                    // Set online ID in local db
                                    user.setUserId(uid);
                                    UserService.updateUserInfo(db, user);

                                    // Log user in online upon account creation
                                    mAuth.signInWithEmailAndPassword(user.email, user.password);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                // if user is online, Write to cloud
                if (UserService.getOnlineStatus(db)) {
                    UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // can change, update everytime profile data saved
                            DatabaseReference saveUser = UsersRef.child(user.userId);
                            saveUser.child("name").setValue(user.nickname);
                            saveUser.child("img").setValue(user.profileImg);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                // update local use info
                UserService.updateUserInfo(db, user);
                goToProfile();
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if img changed flag on, if yes discard saved img
                if (hasChangedImg) {

                }
                // go back to profile activity, finish current activity, return
                goToProfile();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImg = data.getData();
            profileImg.setImageURI(selectedImg);  // show selected img
            Log.v(TAG, "have set new img");

            // get bitmap then save it to local storage
            try {
                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                saveImg(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // img changed flag on
            hasChangedImg = true;
        }
    }

    // check if has permission to access storage (called when change profile is clicked
    private boolean checkStoragePermission() {
        Log.v(TAG,"checking permission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            int read = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED &&
                    write == PackageManager.PERMISSION_GRANTED;
        } else {
            return Environment.isExternalStorageManager();
        }
    }

    // request permission to access storage
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                                  Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        } else {
            try {
                Log.v(TAG, "request storage permission: above R");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

                    } else {
                        if (Environment.isExternalStorageManager()) {
                            UserService.loadImageForProfile(profileImg);
                        } else {
                            Utils.postToast("access denied", ChangeProfile.this);
                        }
                    }
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0) {
                boolean resForWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean resForRead = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (resForRead && resForWrite) {
                    UserService.loadImageForProfile(profileImg);
                } else {
                    Utils.postToast("No permission to storage, can not load profile image.", ChangeProfile.this);
                }
            }
        }
    }

    private void saveImg(Bitmap bitmap) {
        OutputStream output;
        String recentImageInCache;
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/WellnessStudio/");
        boolean res = dir.mkdirs();
        Log.v(TAG, "mkdir result: " + res);

        // Create a name for the saved image
        File file = new File(dir, "user_avatar.jpg");
        try {
            output = new FileOutputStream(file);
            Log.v(TAG, "image saved to internal storage");
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.v(TAG, "error when saving image");
        }
    }

//    // load profile img from sdcard/WellnessStudio/user_avatar.jpg
//    private boolean loadImageForProfile(ImageView imageView) {
//        File filepath = Environment.getExternalStorageDirectory();
//        File dir = new File(filepath.getAbsolutePath()
//                + "/WellnessStudio/user_avatar.jpg");
//        if (dir.exists()) {
//            Bitmap bitmap =
//                    BitmapFactory.decodeFile(dir.getAbsolutePath());
//            imageView.setImageBitmap(bitmap);
//            return true;
//        } else {
//            return false;
//        }
//    }

    // load image from assets/ to profile image view
    private void loadImageFromAssets() {
        try {
            InputStream inputStream = getAssets().open("user_avatar.jpg");
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            profileImg.setImageDrawable(drawable);
            Log.v(TAG, "load from assets.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "can not load picture from assets");
            return;
        }
    }

    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(ChangeProfile.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(ChangeProfile.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(ChangeProfile.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(ChangeProfile.this, Leaderboard.class));
    }

    private void goToProfile() {
        startActivity(new Intent(ChangeProfile.this, Profile.class));
    }
}