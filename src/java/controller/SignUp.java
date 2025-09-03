package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        String mobile = user.get("mobile").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        //validate
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        try {

            if (firstName.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The First Name");

            } else if (lastName.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The Last Name");
            } else if (mobile.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The Mobile Number");

            } else if (email.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The Email");

            } else if (!Util.isEmailValid(email)) {
                responseObject.addProperty("message", "Please Enter The Valid Email");

            } else if (password.isEmpty()) {
                responseObject.addProperty("message", "Please Enter The Password");

            } else if (Util.isPasswordValid(password)) {
                responseObject.addProperty("message", "Please Enter The Valid Password");

            } else {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", email));

                if (!c.list().isEmpty()) {
                    responseObject.addProperty("message", "Email is already Taken");
                } else {

                    User u = new User();

                    u.setFirst_name(firstName);
                    u.setLast_name(lastName);
                    u.setMobile(mobile);
                    u.setEmail(email);
                    u.setPassword(password);

                    final String verificationCode = Util.genarateCode();
                    u.setVerification(verificationCode);

                    u.setCreated_at(new Date());

                    s.save(u);
                    s.beginTransaction().commit();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Mail.sendMail(email, "verification code", "<h1>" + verificationCode + "</h1>");
                        }
                    }).start();

                    HttpSession ses = request.getSession();
                    ses.setAttribute("email", email);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Success");
                }

                s.close();
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Somthing went wrong");

        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
