package com.example.lab14mob.cache;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class CacheStore {

    private CacheStore() {}

    public static void write(Context context, String fileName, String content) throws Exception {
        File file = new File(context.getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String read(Context context, String fileName) throws Exception {
        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists()) return null;
        try (FileInputStream fis = new FileInputStream(file)) {
            Scanner s = new Scanner(fis).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    public static int purge(Context context) {
        File[] files = context.getCacheDir().listFiles();
        if (files == null) return 0;
        int deleted = 0;
        for (File f : files) {
            if (f.delete()) deleted++;
        }
        return deleted;
    }
}