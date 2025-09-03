/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "ChangePassword", urlPatterns = {"/ChangePassword"})
public class ChangePassword extends HttpServlet {
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        JsonObject userPassword = gson.fromJson(request.getReader(), JsonObject.class);
        
        String oldPassword = userPassword.get("oldPassword").getAsString();
        String newPassword = userPassword.get("newPassword").getAsString();
        String confirmPassword = userPassword.get("confirmPassword").getAsString();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        if (oldPassword.isEmpty()) {
            responseObject.addProperty("message", "please Enter the Current password");
            
        } else if (newPassword.isEmpty()) {
            responseObject.addProperty("message", "please Enter the New password");

//        } else if (!Util.isPasswordValid(newPassword)) {
//            responseObject.addProperty("message", "ivalid password");
//
//        
        } else if (confirmPassword.isEmpty()) {
            responseObject.addProperty("message", "please enter the confirm password");
            
        } else if (!newPassword.equals(confirmPassword)) {
            responseObject.addProperty("message", "Passwords are doesnt match");
        } else {
            
            HttpSession ses = request.getSession();
            
            if (ses.getAttribute("user") != null) {
                
                User user = (User) ses.getAttribute("user");
                
                Session s = HibernateUtil.getSessionFactory().openSession();
                
                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", user.getEmail()));
                
                if (!c.list().isEmpty()) {
                    User u = (User) c.list().get(0);
//                    System.out.println(u.getPassword());
                    
                    if (u.getPassword().equals(oldPassword)) {
                        u.setPassword(confirmPassword);
                        
                        s.save(u);
                        
                        s.beginTransaction().commit();
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Change password is Success");
                        s.close();
                    }else{
                        responseObject.addProperty("message", "Incorrect Current Password Try again!");
                        
                    }
                    
                }
                
            }
            
        }
        
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        
    }
    
}
