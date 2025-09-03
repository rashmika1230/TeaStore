
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadUsers", urlPatterns = {"/LoadUsers"})
public class LoadUsers extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("user ok");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria c1 = s.createCriteria(User.class);
        c1.add(Restrictions.ne("verification", "admin"));

        List<User> user = c1.list();

        if (user.isEmpty()) {
            responseObject.addProperty("message", "can't found users");
        } else {

            for (User user1 : user) {
                user1.setPassword(null);
            }

            c1.setProjection(Projections.rowCount());

            Long totalCount = (Long) c1.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalCount / 10);

            responseObject.add("userList", gson.toJsonTree(user));
            responseObject.addProperty("totalPages", totalPages);
            responseObject.addProperty("status", Boolean.TRUE);

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
