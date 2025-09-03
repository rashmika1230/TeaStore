package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddNewColor", urlPatterns = {"/AddNewColor"})
public class AddNewColor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();
        JsonObject colorName = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            String newColor = colorName.get("newColor").getAsString();

            if (request.getSession().getAttribute("admin") == null) {
                responseObject.addProperty("message", "Please Loging First");
            } else if (newColor.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The Color");
            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c1 = s.createCriteria(Color.class);
                c1.add(Restrictions.eq("name", newColor));

                if (!c1.list().isEmpty()) {
                    responseObject.addProperty("message", "Color is Already adedd");
                } else {
                    Color c = new Color();
                    c.setName(newColor);

                    s.save(c);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Successfully added Color!");

                    s.close();
                }

            }

        } catch (Exception e) {
            responseObject.addProperty("message", "Somthing went wrong");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
