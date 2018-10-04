package com;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CredentialManager {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CALLBACK_FILE_PATH = System.getProperty("catalina.base") + File.separator + "callback.props";
    private static final String CREDENTIALS_FILE_PATH = System.getProperty("catalina.base") + File.separator + "client_secret.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("catalina.base") + File.separator +  "tokens";

    public static boolean storeCredentialFile(InputStream fileInputStream) throws IOException{
        return Helper.storeFile(fileInputStream , CREDENTIALS_FILE_PATH);
    }

    public static void deleteCredentialFile() throws IOException {
        File credential = new File(CREDENTIALS_FILE_PATH);
        Helper.delete(credential);
        File token = new File(TOKENS_DIRECTORY_PATH);
        Helper.delete(token);
    }


    public static Drive buidService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client driveService.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        File initialFile = new File(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(initialFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        File tempFile = new File(TOKENS_DIRECTORY_PATH);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tempFile))
                .setAccessType("offline")
                .build();
        VerificationCodeReceiver verificationCodeReceiver = new LocalServerReceiver.Builder().setHost("localhost").setPort(8090).build();
        return new AuthorizationCodeInstalledApp(flow, verificationCodeReceiver).authorize("user");
    }


    public static String buidULR() throws IOException, GeneralSecurityException {
        // Build a new authorized API client driveService.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return constructURl(HTTP_TRANSPORT);
    }

    private static String constructURl(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        File initialFile = new File(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(initialFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        File tempFile = new File(TOKENS_DIRECTORY_PATH);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tempFile))
                .setAccessType("offline")
                .build();
        return setReturnURI(flow);
    }


    public static String setReturnURI(GoogleAuthorizationCodeFlow flow){
        String url = flow.getAuthorizationServerEncodedUrl() +"?" ;
        Collection<String> scopes = flow.getScopes();
        String[] scopeString =scopes.toArray(new String[scopes.size()]);
        String redirectURL = Helper.getProperties(CALLBACK_FILE_PATH).getProperty("url");
        String parameters = "access_type="+ flow.getAccessType()+
                "&client_id=" + flow.getClientId()+
                "&scope="+ scopeString[0]+
                "&redirect_uri=" + redirectURL+
                "&response_type=code";
        System.out.println("redir : " + url + parameters);
        return url + parameters;
    }
}