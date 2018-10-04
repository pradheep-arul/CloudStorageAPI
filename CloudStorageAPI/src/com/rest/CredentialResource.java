package com.rest;


import com.APIManager;
import com.CredentialManager;
import com.Helper;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Path("/credential")
public class CredentialResource {
    private static final String CALLBACK_FILE_PATH = System.getProperty("catalina.base") + File.separator + "callback.props";

    @POST
    @Path("/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadCredentials( @FormDataParam("credentialFile") InputStream fileInputStream) {
        try{
            CredentialManager.storeCredentialFile(fileInputStream);
        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading Credential file. Please try again !!");
        }
        return Response.ok("Credential File uploaded successfully !!").build();
    }


    @GET
    public Response getRegisterURL(){
        String regidterURL = "";
        try {
            regidterURL = CredentialManager.buidULR();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        System.out.println("Register URL : " + regidterURL);
        String callbackURL = Helper.getProperties(CALLBACK_FILE_PATH).getProperty("url");
        return Response.accepted("Add Redirect URL : " + callbackURL  + " in Authorized redirect URIs in Credential page. Call this URL after Credentials Registering call !! URL : " + regidterURL).build();
    }

    @POST
    public Response registerCredentials(){
        try {
            APIManager.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        System.out.println("Credentials Registered !!");
        return Response.accepted("Credentials Registered !!").build();
    }


    @DELETE
    public Response deleteCredentials(){
        try {
            APIManager.destroyDrive();
            CredentialManager.deleteCredentialFile();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        System.out.println("Credentials Deleted !!");
        return Response.accepted("Credentials Deleted !!").build();
    }


}
