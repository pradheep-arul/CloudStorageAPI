package com.rest;

import com.APIManager;
import com.Helper;
import com.google.api.services.drive.model.File;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/folders")
public class FolderResource {

    @GET
    @Path("/{folderid}")
    public Response getListinFolder(@PathParam( "folderid") String folderid) throws IOException {
        File filemetadata = APIManager.getMetadata(folderid);
        String mimeType = filemetadata.getMimeType();
        if(mimeType!= null && !mimeType.equals("")){
            if(mimeType.equals("application/vnd.google-apps.folder")) {
                List<File> files = APIManager.getFilesListInFolder(folderid);
                return Response.ok(Helper.formatList(files)).build();
            }
        }
        return Response.serverError().build();
    }

    @GET
    @Path("/{folderid}/metadata")
    public Response getFolderMetadata(@PathParam( "folderid") String folderid) throws IOException {
        File filemetadata = APIManager.getMetadata(folderid);
        return Response.ok(filemetadata.toString()).build();
    }

    @POST
    @Path("/{parentid}")
    public Response createFolderInFolder(@PathParam("parentid") String parentid , @QueryParam("foldername") String foldername){
        String folderID;
        try {
            folderID = APIManager.createFolder(foldername, parentid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error while creating folder " + foldername + " Please try again !!");
        }
        return Response.accepted("Folder " + foldername + " created with ID : " + folderID).build();
    }


    @DELETE
    @Path("/{folderid}")
    public Response deleteFolder(@PathParam( "folderid") String folderid) {
        try {
            APIManager.deleteFile(folderid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error while deleting folder : " + folderid + " Please try again !!");
        }
        return Response.accepted("Folder " + folderid + " deleted").build();
    }
}
