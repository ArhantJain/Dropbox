package dropbox;

import static spark.Spark.*;

import javax.servlet.MultipartConfigElement;

public class Main {
    public static void main(String[] args) {
        port(8081); 
        staticFiles.location("/public");

        FileService fileService = new FileService("uploads");

        // Enable CORS for all routes
        enableCORS("*", "*", "*");

        // Setup for handling multipart requests
        before((request, response) -> {
            if (request.raw().getMethod().equalsIgnoreCase("POST")) {
                if (request.raw().getContentType() != null && request.raw().getContentType().startsWith("multipart/form-data")) {
                    request.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
                }
            }
        });

        // Home page
        get("/", (req, res) -> {
            res.redirect("/files");
            return null;
        });

        // File upload form
        get("/files", (req, res) -> {
            // Render the file upload form
            return "<html><body><form ref='uploadForm' "
                 + "action='/files' method='post' "
                 + "encType='multipart/form-data'>"
                 + "<input type='file' name='file' multiple=''><br>"
                 + "<input type='submit' value='Upload'>"
                 + "</form></body></html>";
        });
        
     // Inside the post("/files") route
        post("/files", "multipart/form-data", (req, res) -> {
            System.out.println("Received file upload request.");
            return fileService.handleFileUpload(req);
        });


        // Download file
        get("/files/:filename", (req, res) -> {
            return fileService.downloadFile(req, res);
        });

        // Delete file
        delete("/files/:filename", (req, res) -> {
            return fileService.deleteFile(req);
        });
    }

    // Enable CORS for all routes
    private static void enableCORS(final String origin, final String methods, final String headers) {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}
