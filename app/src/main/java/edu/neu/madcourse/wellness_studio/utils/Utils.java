package edu.neu.madcourse.wellness_studio.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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
        return matcher.find();
    }

    // check valid password with:
    // at least one numeric character
    // at least one lowercase character
    // at least one uppercase character
    // at least one special symbol among @_#$%
    // length between 6 and 20
    public static boolean checkValidPassword(String s) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^(?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@_#$%]).{6,20}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(s);
        return matcher.find();
    }

}
