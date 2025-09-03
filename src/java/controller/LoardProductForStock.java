package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;

@WebServlet(name = "LoardProductForStock", urlPatterns = {"/LoardProductForStock"})
public class LoardProductForStock extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        String productId = request.getParameter("pid");
        System.out.println(productId);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        Product product = (Product) s.get(Product.class, Integer.valueOf(productId));
        Gson gson = new Gson();
        //Criteria c1 = s.createCriteria(Product.class);
        //List<Product> productList = c1.list();
        //need valofation
        try {

            if (!Util.isInteger(productId)) {
                responseObject.addProperty("message", "Prodcut can not Found");
            } else if (Integer.parseInt(productId) == 0) {
                responseObject.addProperty("message", "Prodcut can not Found");

            } else {
                if (product == null) {
                    responseObject.addProperty("message", "Prodcut can not Found");

                } else {

                    responseObject.add("product", gson.toJsonTree(product));
                    responseObject.addProperty("status", Boolean.TRUE);
                    //responseObject.add("productList", gson.toJsonTree(productList));

                }
            }

        } catch (Exception e) {
            responseObject.addProperty("message", "Prodcut can not Found");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
