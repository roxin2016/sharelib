package com.zsyj.sharesdk.download;

import android.os.Environment;

import java.io.File;


/**
 * Created by long on 2015/11/17.
 * 下载配置
 */
public class DownloadConfig {

    // 下载目录
    private String mDownloadDir;
    // 去水印下载目录
    private String mRemoveLogDownloadDir;
    private int mRetryTimes;
    // 下载目录文件保存
    private static final String STORAGE_KEY = "DownloadDir";


    private DownloadConfig(String path) {
        mDownloadDir = getSdcardPathOnSys() + path;
        File dir = new File(mDownloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdirs();
        }
        mRetryTimes = 10;
        mRemoveLogDownloadDir = getSdcardPathOnSys() + "/RemoveLog/Video/";
        File dir1 = new File(mRemoveLogDownloadDir);
        if (!dir1.exists()) {
            dir1.mkdirs();
        } else if (!dir1.isDirectory()) {
            dir1.delete();
            dir1.mkdirs();
        }
    }

    public String getDownloadDir() {
        return mDownloadDir;
    }

    public String getRemoveLogDownloadDir() {
        return mRemoveLogDownloadDir;
    }

    public int getRetryTimes() {
        return mRetryTimes;
    }


    /**
     * 构建器
     */
    public static class Builder {

        private DownloadConfig config;

        public Builder(String path) {
            config = new DownloadConfig(path);
        }

        public DownloadConfig build() {
            return config;
        }

        public Builder setDownloadDir(String downloadDir) {
            config.mDownloadDir = downloadDir;
//            PreferencesUtils.putString(FileDownloader.getContext(), STORAGE_KEY, downloadDir);
            return this;
        }

        public Builder setRetryTimes(int retryTimes) {
            config.mRetryTimes = retryTimes;
            return this;
        }
    }

    public String getSdcardPathOnSys() {
        try {
            // ��������ķ�������ȡ������ֱ����������·��
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()
                        .getPath();
                ////mLog("SDCARDĿ¼��" + path);
                return path;
            }

            // ��SDCARD�ĸ�Ŀ¼��ʼɨ��
            File path = Environment.getExternalStorageDirectory()
                    .getParentFile();

            // ������ļ��еĻ�
            if (path.isDirectory()) {
                // �����ļ������е�����
                File[] files = path.listFiles();

                // ��ѯ��ǰĿ¼�µ��ļ���
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        // �ж��Ƿ�ΪSDCARDĿ¼
                        if (containsAny(files[i].getPath(), "sdcard")) {
                            ////mLog("Ŀ¼����" + files[i].getPath());

                            if (files[i].list().length > 0) {
                                ////mLog("Ŀ¼" + files[i].getPath() + "��Ϊ��");
                                return files[i].getPath();
                            } else {
                                ////mLog("Ŀ¼" + files[i].getPath() + "Ϊ��");
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "/mnt/sdcard"; // Ĭ��SD��λ��
    }

    public boolean containsAny(String str, String searchChars) {
        return str.contains(searchChars);
    }
}
