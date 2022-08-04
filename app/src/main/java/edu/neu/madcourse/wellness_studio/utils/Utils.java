package edu.neu.madcourse.wellness_studio.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    static String TAG = "util";

    // send a short toast from given context
    public static void postToast(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // send a long toast from given context
    public static void postToastLong(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // return current date in a form like "2022-07-26"
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    // check if a string only contains alphabet and digit and len < 26
    public static boolean checkValidName(String s) {
        if (s == null || s.equals("")) {
            return false;
        } else {
            int len = s.length();
            if (len > 25) return false;

            for (int i = 0; i < len; i++) {
                if ((!Character.isLetterOrDigit(s.charAt(i)))) {
                    return false;
                }
            }
            return true;
        }
    }

    // check if a string is valid as an email address
    // code from https://stackoverflow.com/questions/8204680/java-regex-email
    public static boolean checkValidEmail(String s) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(s);
        Boolean res =  matcher.find();
        Log.v(TAG, "email result: " + s + " | " + res);
        return res;
    }

    // check valid password with:
    // at least one numeric character
    // at least one lowercase character
    // at least one uppercase character
    // at least one special symbol among @_#$%
    // length between 6 and 20
    // ** regex doesnot work so use a temp checker only check length
    public static boolean checkValidPassword(String s) {
//        Pattern VALID_EMAIL_ADDRESS_REGEX =
//                Pattern.compile("^(?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@_#$%]).{6,20}$",
//                        Pattern.CASE_INSENSITIVE);
//
//        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(s);
//        Log.v(TAG, "password result: " + matcher.find()+"");
//        return matcher.find();
        return (s.length() >= 6 && s.length() <= 15);
    }


    public static void saveImage(Bitmap bitmap, String username) {
        OutputStream output;
        String recentImageInCache;
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/wellnessStudio/profile");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, username+".jpg");
        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == i && resultCode == RESULT_OK && data != null) {
//            Uri selectedImage = data.getData();
//
//            saveImage(yourbitmap);
//            coverpic.setImageURI(selectedImage);
//            pathToImage = selectedImage.getPath();
//            //stuff to do on click button upload cover??
//        }
//    }

}
