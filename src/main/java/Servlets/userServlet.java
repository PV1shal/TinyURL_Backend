package Servlets;

import Models.Response;
import Models.UserInfo;
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

import static Connections.Connections.usersCollection;

@WebServlet(name = "Servlets.userServlet", value = "/user")
public class userServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload userCreds = new ServletFileUpload(factory);
        try {
            List<FileItem> items = userCreds.parseRequest(request);
            String username = null;
            String password = null;
            int tier = 0;
            for (FileItem item : items) {
                switch (item.getFieldName()) {
                    case "username": {
                        username = item.getString();
                        break;
                    }
                    case "password": {
                        password = item.getString();
                        break;
                    }
                    case "Tier": {
                        tier = Integer.parseInt(item.getString());
                        break;
                    }
                }
            }
            UserInfo userInfo = new UserInfo(username, password, tier);
            usersCollection.insertOne(new Document("username", userInfo.getUsername())
                    .append("password", userInfo.getPassword())
                    .append("tier", userInfo.getTier())
                    .append("requestsLeft", userInfo.getRequestsLeft()));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(new Gson().toJson(new Response("OK", "User Created")));
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }
}