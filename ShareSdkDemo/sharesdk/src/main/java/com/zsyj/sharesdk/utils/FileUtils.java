package com.zsyj.sharesdk.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {


    public static final String SDCARD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator;

    public static void mLog(String msg) {

    }

    public static String getSdcardPathOnSys() {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()
                        .getPath();
                return path;
            }

            File path = Environment.getExternalStorageDirectory()
                    .getParentFile();

            if (path.isDirectory()) {
                File[] files = path.listFiles();

                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        if (containsAny(files[i].getPath(), "sdcard")) {

                            if (files[i].list().length > 0) {
                                return files[i].getPath();
                            } else {
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
        }

        return "/mnt/sdcard";
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean containsAny(String str, String searchChars) {
        return str.contains(searchChars);
    }


    public static void installApk(Context context, String apkPath) {
        mLog(apkPath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean sdCardIsOk() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String copyAssetsFile(Context mContext, String fileName) {

        String pkName = mContext.getPackageName();

        String pwdDictionaryPath = "/data/data/" + pkName + "/mAssets/";
        String pwdDictionaryAllPath = pwdDictionaryPath + fileName;

        try {
            AssetManager asset = null;
            InputStream fis = null;

            File dir = new File(pwdDictionaryPath);
            if (!dir.exists())
                dir.mkdirs();

            File pwdFile = new File(pwdDictionaryAllPath);

            if (pwdFile.exists()) {
                pwdFile.delete();
            }

            if (StringUtils.isNullStr(fileName)) {
            } else {
                asset = mContext.getAssets();
                fis = asset.open(fileName);
            }

            pwdFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(pwdFile);
            byte[] buffer = new byte[4096];
            int count = 0;

            while ((count = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }

            fos.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pwdDictionaryAllPath;
    }

    public static String copyAssetsFile(Context mContext, String srcFileName,
                                        String desFileName) {

        String pkName = mContext.getPackageName();

        String pwdDictionaryPath = "/data/data/" + pkName + "/mAssets/";
        String pwdDictionaryAllPath = pwdDictionaryPath + desFileName;

        try {
            AssetManager asset = null;
            InputStream fis = null;

            File dir = new File(pwdDictionaryPath);
            if (!dir.exists())
                dir.mkdirs();

            File pwdFile = new File(pwdDictionaryAllPath);

            if (pwdFile.exists()) {
                pwdFile.delete();
            }

            if (StringUtils.isNullStr(srcFileName)) {
            } else {
                asset = mContext.getAssets();
                fis = asset.open(srcFileName);
            }

            pwdFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(pwdFile);
            byte[] buffer = new byte[4096];
            int count = 0;

            while ((count = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }

            fos.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pwdDictionaryAllPath;
    }

    public static String assetsToSdcard(Context mContext, String filePath,
                                        String srcFileName) {

        String pwdDictionaryPath = filePath;
        String pwdDictionaryAllPath = filePath + srcFileName;

        try {
            AssetManager asset = null;
            InputStream fis = null;

            File dir = new File(pwdDictionaryPath);
            if (!dir.exists())
                dir.mkdirs();

            File pwdFile = new File(pwdDictionaryAllPath);

            if (pwdFile.exists()) {
                pwdFile.delete();
            }


            if (StringUtils.isNullStr(srcFileName)) {
                return "";
            } else {
                asset = mContext.getAssets();
                fis = asset.open(srcFileName);
            }

            pwdFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(pwdFile);
            byte[] buffer = new byte[4096];
            int count = 0;

            while ((count = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }

            fos.close();
            fis.close();

        } catch (Exception e) {
        }

        return pwdDictionaryAllPath;
    }

    public static byte[] openAssets(Context context, String fileName) {

        byte[] tempByte = null;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            tempByte = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempByte;
    }

    /**
     * FileInputStreamתInputStream
     */
    public static InputStream getInputStream(FileInputStream fileInput) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = -1;
        InputStream inputStream = null;
        try {
            while ((n = fileInput.read(buffer)) != -1) {
                baos.write(buffer, 0, n);

            }
            byte[] byteArray = baos.toByteArray();
            inputStream = new ByteArrayInputStream(byteArray);
            return inputStream;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void delFile(String filePath) {
        try {
            File temp = new File(filePath);
            temp.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * @return true if the apk can install,or false that the apk download
     * incomplete
     */
    public static boolean isApkCanInstall(Context mContext, String apkPath) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 保存缓存数据到文件
     *
     * @param context
     * @param fileName
     * @param object
     * @return
     */
    public static boolean saveCachDataToFile(Context context, String fileName,
                                             Object object) {
        if (object == null) {
            return false;
        }
        try {
            // 需要一个文件输出流和对象输出流；文件输出流用于将字节输出到文件，对象输出流用于将对象输出为字节
            FileOutputStream fout = context.openFileOutput(fileName + ".ser",
                    Activity.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(object);
            out.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 从文件里面获取缓存数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Object getCacheDataFromFile(Context context, String fileName) {
        Object object = null;
        try {
            FileInputStream fin = context.openFileInput(fileName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fin);
            object = in.readObject();
            fin.close();
            in.close();
        } catch (Exception e) {
            return object;
        }
        return object;
    }

    /**
     * 清除指定文件名的缓存
     *
     * @param context
     * @param fileName
     */
    public static void clearCacheDataToFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * 获取指定路径下的.mp4文件
     *
     * @param fileAbsolutePath
     * @return
     */
    public static List<String> getVideoFileName(String fileAbsolutePath) {
        List<String> vecFile = new ArrayList<>();
        try {
            File file = new File(fileAbsolutePath);
            if (file != null) {
                File[] subFile = file.listFiles();
                if (subFile != null) {
                    for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                        // 判断是否为文件夹
                        if (!subFile[iFileLength].isDirectory()) {
                            String filename = subFile[iFileLength].getName();
                            // 判断是否为MP4结尾
                            if (filename.trim().toLowerCase().endsWith(".mp4") || filename.endsWith("_xmsp")) {
                                vecFile.add(filename);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return vecFile;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


}
