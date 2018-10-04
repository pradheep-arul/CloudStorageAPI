package com.rest;

import com.APIManager;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.model.File;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/files")
public class FileResource {

    @GET
    @Path("{fileid}/metadata")
    public Response getFileMetadata(@PathParam( "fileid") String fileid) throws IOException {
        File filemetadata = APIManager.getMetadata(fileid);
        return Response.ok(filemetadata.toString()).build();
    }


    @GET
    @Path("/{fileid}")
    public Response downloadFile1(@PathParam("fileid") String fileid) throws IOException {
        File filemetadata = APIManager.getMetadata(fileid);
        String mimeType = filemetadata.getMimeType();
        if(mimeType== null || mimeType.equals("") || mimeType.equals("application/vnd.google-apps.folder")){
            return Response.serverError().build();
        }
        StreamingOutput fileStream =  new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream output) throws WebApplicationException {
                try {
                    InputStream input  = APIManager.downloadFileInputStream(fileid);
                    IOUtils.copy(input, output);
                    output.close();
                }
                catch (Exception e) {
                    throw new WebApplicationException("File Not Found !!");
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = " + filemetadata.getName())
                .build();
    }


    @POST
    @Path("/{parentid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile( @FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                @PathParam("parentid") String parentid) throws IOException {
        String fileid = APIManager.uploadFileInFolderStream(fileInputStream, fileMetaData.getFileName(), parentid);
        return Response.ok("File " + fileMetaData.getFileName() + " created with ID : " + fileid ).build();
    }


    @DELETE
    @Path("/{fileid}")
    public Response deleteFolder(@PathParam( "fileid") String fileid) {
        try {
            APIManager.deleteFile(fileid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error while deleting file : " + fileid + " Please try again !!");
        }
        return Response.accepted("File " + fileid + " deleted").build();
    }

}