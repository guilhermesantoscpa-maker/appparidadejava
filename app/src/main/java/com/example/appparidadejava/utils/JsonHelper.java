package com.example.appparidadejava.utils;

import android.content.Context;

import java.io.InputStream;

public class JsonHelper {
    public static String loadJSON(Context context, int resourceId) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
