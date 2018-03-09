
package com.alonsocage.componentes.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alonso on 20.02.18.
 */
public class ImagesDir {

    private static final String TEMP_IMAGES_PATH = "images/temp";
    private static File imagesTempDir;

    public static File getTempImagesDir(Context context) {
        if (imagesTempDir == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                imagesTempDir = context.getExternalFilesDir(TEMP_IMAGES_PATH);
            } else {
                imagesTempDir = context.getCacheDir();
            }
        }

        if (imagesTempDir != null && !imagesTempDir.exists()) {
            imagesTempDir.mkdirs();
        }

        return imagesTempDir;
    }

    public static boolean isTempImagesDir(String path, Context context) {
        try {
            return path.startsWith(getTempImagesDir(context).getCanonicalPath());
        } catch (IOException e) {
            LogUtil.logError("isTempImagesDir", "Failed to get temp images folder", e);
        }

        return false;
    }

    public static void cleanDirs(File file) {

        if (!file.exists())
            return;


        if (file.isDirectory()) {
            for (File f : file.listFiles()) {

                cleanDirs(f);
            }
        }

        file.delete();

    }

}
