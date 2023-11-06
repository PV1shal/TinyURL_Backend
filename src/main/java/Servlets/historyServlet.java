package Servlets;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static Connections.Connections.collection;
import static com.mongodb.client.model.Filters.eq;

@WebServlet(name = "Servlets.historyServlet", value = "/history")
public class historyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String username = request.getParameter("username");
        Bson projection = Projections.exclude("_id", "username");
        MongoCursor cursor = collection.find(eq("username", username)).projection(projection).iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                response.getWriter().write(doc.toJson());
            }
        } finally {
            cursor.close();
        }
    }
}