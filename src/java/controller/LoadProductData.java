/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Size;
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

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        //load category
        Criteria c1 = s.createCriteria(Category.class);
        List<Category> categoryList = c1.list();
        
        

        //load color
        Criteria c2 = s.createCriteria(Color.class);
        List<Color> colorList = c2.list();
        
        //load Sizes
        Criteria c3  = s.createCriteria(Size.class);
        List<Size> sizeList = c3.list();
        
        Gson gson = new Gson();
        
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("sizeList", gson.toJsonTree(sizeList));
        
        responseObject.addProperty("status", Boolean.TRUE);
        
        s.close();
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
