package com.example.dawidr.androidtestproject;

import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class GoogleDriveHelper {

    public static File CreateFolder(Drive service, String title) throws IOException {
        return CreateFolder(service, title, null);
    }

    public static File CreateFolder(Drive service, String title, String parent_id) throws IOException {
        File folder = new File();
        folder.setTitle(title);
        folder.setMimeType("application/vnd.google-apps.folder");

        if (parent_id != null)
            folder.setParents(Arrays.asList(new ParentReference().setId(parent_id)));

        return service.files().insert(folder).execute();
    }

    public static File CreateFile(String title, String parent_id) {
        File file = new File();
        file.setTitle(title);
        file.setMimeType("text/plain");
        file.setParents(Arrays.asList(new ParentReference().setId(parent_id)));
        return file;
    }

    public static File GetFolder(Drive service, String title) throws IOException {
        return GetFolder(service, title, null);
    }

    public static File GetFolder(Drive service, String title, String parent_id) throws IOException {
        String query = "";

        if (parent_id == null)
            query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='" + title + "'";
        else
            query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='" + title + "' AND '" + parent_id + "' in parents";

        FileList files = service.files()
                .list()
                .setQ(query)
                .execute();

        return files.getItems().get(0);
    }

    public static File GetFile(Drive service, String title, String parent_id) throws IOException {
        FileList files = service.files()
                .list()
                .setQ("trashed=false AND title='" + title + "' AND '" + parent_id + "' in parents")
                .execute();

        return files.getItems().get(0);
    }

    public static FileContent GetTempFileContent(String content) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("tempfile", "txt");

        String tempPath = tempFile.getAbsolutePath();

        BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath));
        bw.write(content);
        bw.close();

        FileContent mediaContent = new FileContent("text/plain", tempFile);

        return mediaContent;
    }

    public static void UploadPhotoToGoogleDrive(Drive service, WorkPhoto workPhoto, String folder_id, MediaHttpUploaderProgressListener mediaHttpUploaderProgressListener) throws IOException {
        java.io.File mediaFile = new java.io.File(workPhoto.path);
        InputStreamContent mediaContent =
                new InputStreamContent("image/jpeg",
                        new BufferedInputStream(new FileInputStream(mediaFile)));
        mediaContent.setLength(mediaFile.length());

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setTitle(workPhoto.name);
        fileMetadata.setParents(Arrays.asList(new ParentReference().setId(folder_id)));

        Drive.Files.Insert request = service.files().insert(fileMetadata, mediaContent);
        request.getMediaHttpUploader().setProgressListener(mediaHttpUploaderProgressListener);
        request.execute();
    }
}
