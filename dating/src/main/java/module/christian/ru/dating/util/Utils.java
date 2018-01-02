package module.christian.ru.dating.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by dakishin@gmail.com
 * 17.08.2014.
 */
public class Utils {
    private static final String DAY_FORMAT = "dd.MM.yyyy EE";
    private static final String EXTRA_PUSH = "EXTRA_PUSH";
    private static final String PERMISSION = "com.club.pif.permission.RECEIVER";


    public static boolean isNotEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    public static void writeObjectToFile(Context context, String fileName, Serializable objectToWrite) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(objectToWrite);
        oos.close();
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(baos.toByteArray());
        fos.close();

    }

    public static Serializable readObjectFromFile(Context context, String fileName) throws IOException, ClassNotFoundException {

        FileInputStream fos = context.openFileInput(fileName);
        ObjectInputStream oos = new ObjectInputStream(fos);
        Serializable serializable = (Serializable) oos.readObject();
        fos.close();
        oos.close();
        return serializable;


    }


    static private boolean isEqualDates(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }


    public static void showKeyboard(Context context, View view) {
        if (context == null || view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyBoard(Context context, View editText) {
        if (context == null || editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static Display getDisplay(Activity context) {
        return context.getWindowManager().getDefaultDisplay();
    }


    public static String extractEmail(String email) {
        if (email == null) {
            return null;
        }
        String[] values = email.split("_");
        if (values.length > 2) {
            return values[2];
        }
        return null;
    }

    public static String createFullAccountName(String name, String type) {
        return type + "_" + name;

    }


    public static String encodeBase64(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
    }

    public static String decodeBase64(String text) {
        if (text == null) {
            return null;
        }
        try {
            return new String(Base64.decode(text.getBytes(), Base64.DEFAULT), "utf8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }



    public static boolean isPermissionGranted(String permission, Activity activity) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }


    public static void sendBroadCast(Context context, String action, Serializable param) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_PUSH, param);
        context.sendOrderedBroadcast(intent, PERMISSION);
    }




    public static boolean isNotBlank(@Nullable String string) {
        return string != null && !string.trim().equals("");
    }


    @NotNull
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
