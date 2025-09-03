/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "AdminLogin", urlPatterns = {"/AdminLogin"})
public class AdminLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("OK");
        Gson gson = new Gson();
        JsonObject admin = gson.fromJson(request.getReader(), JsonObject.class);

        String email = admin.get("email").getAsString();
        String password = admin.get("password").getAsString();

//        System.out.println(email);
//        System.out.println(password);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        try {
            if (email.isEmpty()) {
                responseObject.addProperty("message", "Email is requierd");
            } else if (!Util.isEmailValid(email)) {
                responseObject.addProperty("message", "Invalid Email");
            } else if (password.isEmpty()) {
                responseObject.addProperty("message", "Password is requierd");

            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c1 = s.createCriteria(Admin.class);

                Criterion ctr1 = Restrictions.eq("email", email);
                Criterion ctr2 = Restrictions.eq("password", password);

                c1.add(ctr1);
                c1.add(ctr2);

                System.out.println(c1.list());

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message", "cant found");

                } else {
                    Admin a = (Admin) c1.list().get(0);

                    HttpSession ses = request.getSession();
                    responseObject.addProperty("status", Boolean.TRUE);

//            ses.setAttribute("adminEmail", email);
                    ses.setAttribute("admin", a);

                    responseObject.addProperty("message", "success");
                }

                s.close();
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Somthing went wrong");
        }
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
