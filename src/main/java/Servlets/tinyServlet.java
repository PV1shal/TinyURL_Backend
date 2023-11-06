package Servlets;

import Models.Response;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static Connections.Connections.collection;
import static com.mongodb.client.model.Filters.eq;

@WebServlet(name = "Servlets.tinyServlet", value = "/maketiny")
public class tinyServlet extends HttpServlet {
    Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getRequestURI();

        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new Gson().toJson(new Response("Bad Request", "Invalid Request")));
            return;
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        String customURL = null;
        String url = null;
        String username = null;
        String isCustom = null;

        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                switch (item.getFieldName()) {
                    case "url": {
                        url = item.getString();
                        break;
                    }
                    case "customtiny": {
                        customURL = item.getString();
                        break;
                    }
                    case "username": {
                        username = item.getString();
                        break;
                    }
                    case "custom": {
                        isCustom = item.getString();
                    }
                }
            }
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }

        int added = convertToTinyURL(url, customURL, username, isCustom);
        if(added == 1) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(new Response("OK", "Tiny URL created")));
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new Response("Bad Request", "Tiny URL already exists, try another one")));
        }
    }

    private int convertToTinyURL(String url, String customURL, String username, String isCustom) {
        if(isCustom.equals("false")) {
            customURL = generateRandomCustomURL() ;
            collection.insertOne(new Document("customURL", customURL).append("url", url).append("username", username));
            return 1;
        }
        if (collection.find(eq("customURL", customURL)).first() != null) {
            return 0;
        }
        else {
            collection.insertOne(new Document("customURL", customURL).append("url", url).append("username", username));
            return 1;
        }
    }
    private String generateRandomCustomURL() {
        String randomUUID = UUID.randomUUID().toString();
        randomUUID = randomUUID.replace("-", "").substring(0, 6);
        return "tiny-" + randomUUID + collection.countDocuments();
    }
}