package dropbox;

import spark.Request;
import spark.Response;
import spark.utils.IOUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class FileService {
    private String uploadDirectory;

    public FileService(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
        initialize();
    }

    private void initialize() {
        File folder = new File(uploadDirectory);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public Object listFiles() {
        File folder = new File(uploadDirectory);
        File[] files = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    public Object handleFileUpload(Request req) {
        try (InputStream is = req.raw().getPart("file").getInputStream()) {
            String fileName = req.raw().getPart("file").getSubmittedFileName();
            String filePath = uploadDirectory + File.separator + fileName;
            
            // Log information about the file being uploaded
            System.out.println("Uploading file: " + fileName);
            System.out.println("File path: " + filePath);

            try (OutputStream os = new FileOutputStream(filePath)) {
                IOUtils.copy(is, os);
            }

            // Log success message
            System.out.println("File uploaded successfully: " + fileName);

            return new Gson().toJson(Map.of("message", "File uploaded successfully", "fileName", fileName));
        } catch (Exception e) {
            // Log error message
            System.err.println("Error uploading file:");
            e.printStackTrace();
            
            return new Gson().toJson(Map.of("error", "Error uploading file"));
        }
    }



    public Object downloadFile(Request req, Response res) throws FileNotFoundException {
        String fileName = req.params(":filename");
        File file = new File(uploadDirectory + File.separator + fileName);
        if (file.exists()) {
            res.header("Content-Disposition", "attachment; filename=" + fileName);
            return new FileInputStream(file);
        } else {
            res.status(404);
            return "File not found.";
        }
    }

    public Object deleteFile(Request req) {
        String fileName = req.params(":filename");
        File file = new File(uploadDirectory + File.separator + fileName);
        if (file.exists()) {
            file.delete();
            return new Gson().toJson(Map.of("message", "File deleted successfully", "fileName", fileName));
        } else {
            return new Gson().toJson(Map.of("error", "File not found"));
        }
    }

}

