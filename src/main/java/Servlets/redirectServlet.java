package Servlets;

import Models.Response;
import com.google.gson.Gson;
import org.bson.Document;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static Connections.Connections.collection;
import static Connections.Connections.usersCollection;
import static com.mongodb.client.model.Filters.eq;

@WebServlet(name = "Servlets.redirectServlet", value = "/*")
public class redirectServlet extends HttpServlet {
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] urlPaths = request.getRequestURI().split("/");
        String tinyURL = urlPaths[2];
        String username = request.getParameter("username");
        Document result = (Document) collection.find(eq("customURL", tinyURL)).first();
        Document user = (Document) usersCollection.find(eq("username", username)).first();

        if (result == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new Response("Bad Request", "Tiny URL does not exist")));
        } else {
            if (user.getInteger("requestsLeft") < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new Response("Bad Request", "User has no requests left")));
                return;
            }
            response.sendRedirect("https://" + result.getString("url"));
            usersCollection.updateOne(eq("username", username),
                    new Document("$set", new Document("requestsLeft", user.getInteger("requestsLeft") - 1)));
        }
    }
}