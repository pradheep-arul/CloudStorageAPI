package com;

import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class Helper {
    public static String formatList(List<File> files){
        if (files == null || files.isEmpty()) {
            return "No files found.";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append( "Files: " + "\n");
            for (File file : files) {
                if (file.getMimeType().equals("application/vnd.google-apps.folder")){
                    stringBuilder.append( "Fold : " + file.getId() + "       " + file.getName() + "\n");
                }else {
                    stringBuilder.append( "File : " + file.getId() + "       " + file.getName() + "\n");
                }
            }
            return stringBuilder.toString();
        }
    }

    public static boolean storeFile(InputStream fileInputStream , String filepath) throws IOException {
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            java.io.File tempFile = new java.io.File(filepath);
            System.out.println(tempFile.getAbsolutePath());
            OutputStream out = new FileOutputStream( tempFile);
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }


    public static void delete(java.io.File file) throws IOException {
        if (file.isDirectory()) {
            for (java.io.File childFile : file.listFiles()) {
                delete(childFile);
            }
            if (!file.delete()) {
                throw new IOException();
            }
        } else {
            if (!file.delete()) {
                throw new IOException();
            }
        }
    }


    public static Properties getProperties(String confFileName) {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if ((new java.io.File(confFileName).exists())) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        } catch (Exception ex) {
            System.out.println("Caught exception: " + ex);
            ex.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ex) {
                System.out.println("Caught exception: " + ex);
                ex.printStackTrace();
            }
        }
        return props;
    }
}