package com.example.wgwong.budgettracker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Utilities {
    public static boolean saveFile(String filename, HashMap contents, Context ctx) {
        FileOutputStream outputStream;
        ObjectOutputStream out;
        try {
            outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(outputStream);
            out.writeObject(contents);
            out.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Object loadFile(String filename, Context ctx) {
        FileInputStream inputStream;
        ObjectInputStream in;
        Object contents = new Object();
        try {
            inputStream = new FileInputStream(ctx.getFilesDir() + "/" + filename);
            in = new ObjectInputStream(inputStream);
            contents = in.readObject();
            in.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt( 10000000, 99999999 + 1);
    }

    public static int dpToPixels(int dp, Context ctx) {
        Resources r = ctx.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) Math.floor(px);
    }
}
