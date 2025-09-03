package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
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

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "AddNewCategory", urlPatterns = {"/AddNewCategory"})
public class AddNewCategory extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();
        JsonObject category = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            String newCategory = category.get("newCategory").getAsString();
//        System.out.println(newCategory);

            if (request.getSession().getAttribute("admin") == null) {
                responseObject.addProperty("message", "Please Loging First");
            } else if (newCategory.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The category");
            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c1 = s.createCriteria(Category.class);
                c1.add(Restrictions.eq("name", newCategory));

                if (!c1.list().isEmpty()) {
                    responseObject.addProperty("message", "Category is Already adedd");
                } else {
                    Category c = new Category();
                    c.setName(newCategory);

                    s.save(c);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Successfully added category!");

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
