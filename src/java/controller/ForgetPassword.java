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
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ForgetPassword", urlPatterns = {"/ForgetPassword"})
public class ForgetPassword extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        final String email = request.getParameter("email");

//        System.out.println(email);
        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email is required");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");
            
        } else {
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            
            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            
            if (c.list().isEmpty()) {
                System.out.println("not valid");
            } else {

//                System.out.println("ok");
                User user = (User) c.list().get(0);
                
                final String verificationCode = Util.genarateCode();
                user.setVerification(verificationCode);
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "verification code", "<h1>" + verificationCode + "</h1>");
                    }
                }).start();
                
                s.update(user);
                s.beginTransaction().commit();
                s.close();
                
                responseObject.addProperty("status", Boolean.TRUE);
                responseObject.addProperty("message", "Chek Your Email");
                
            }
            
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Gson gson = new Gson();
        
        JsonObject userPassword = gson.fromJson(request.getReader(), JsonObject.class);
        
        String email = userPassword.get("email").getAsString();
        String vcode = userPassword.get("vcode").getAsString();
        String newPassword = userPassword.get("newPassword").getAsString();
        String confirmPassword = userPassword.get("confirmPassword").getAsString();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        
        if (vcode.isEmpty()) {
            responseObject.addProperty("message", "please Enter the Verifivation");
            
        } else if (!Util.isInteger(vcode)) {
            responseObject.addProperty("message", "Invalid Verification code");
            
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
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            
            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            
            if (!c.list().isEmpty()) {
                User u = (User) c.list().get(0);
                
                if (u.getVerification().equals(vcode)) {
                    u.setPassword(confirmPassword);
                    u.setVerification("verified");

//                    s.save(u);
                    s.update(u);
                    
                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Change password is Success");
                    s.close();
                } else {
                    responseObject.addProperty("message", "Check Your Verification Code");
                }
                
            } else {
                responseObject.addProperty("message", "Invalid User");
                
            }
            
        }
        
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        
    }
    
}
