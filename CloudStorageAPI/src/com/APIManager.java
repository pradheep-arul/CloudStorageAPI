package com;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class APIManager extends Thread {
    public static Drive driveService = null;

    public static void initialize() throws IOException, GeneralSecurityException {
        driveService = CredentialManager.buidService();
    }

    public static void destroyDrive() {
        driveService = null;
    }

    public static File getMetadata(String fileId) throws IOException {
        File file = driveService.files().get(fileId).execute();
        System.out.println(file.getMimeType());
        return file;
    }


    public static List<File> getFilesListInFolder(String parentID) throws IOException {
        FileList result = driveService.files().list()
                .setQ("'" + parentID + "' in parents")
                .setFields("nextPageToken, files(id, name, parents, mimeType)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                if (file.getMimeType().equals("application/vnd.google-apps.folder")){
                    System.out.println( "Fold : " + file.getId() + "       " + file.getName());
                }else {
                    System.out.println( "File : " + file.getId() + "       " + file.getName());
                }
            }
        }
        return files;
    }


    public static String uploadFileInFolderStream(InputStream inputStream ,String filename, String parentid) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(filename);
        fileMetadata.setParents(Collections.singletonList(parentid));
        File file = driveService.files().create(fileMetadata ,new InputStreamContent(null ,inputStream))
                .setFields("id")
                .execute();
        System.out.println("File ID: " + file.getId());
        return file.getId();
    }


    public static InputStream downloadFileInputStream(String fileId) throws IOException {
        InputStream in = driveService.files()
                .get(fileId)
                .executeMediaAsInputStream();
        return in;
    }

    public static String createFolder(String folderName, String parentID) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setParents(Collections.singletonList(parentID));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = driveService.files().create(fileMetadata)
                .setFields("id")
                .execute();
        System.out.println("Folder ID: " + file.getId());
        return file.getId();
    }

    public static void deleteFile(String fileid) throws IOException {
        driveService.files().delete(fileid)
                .execute();
    }
}