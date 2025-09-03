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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        Gson gson = new Gson();
        JsonObject rqUser = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        String email = rqUser.get("email").getAsString();
        String password = rqUser.get("password").getAsString();

//        System.out.println(email);
//        System.out.println(password);
        if (email.isEmpty()) {
            responseObject.addProperty("message", "Please Enter the Email");

        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");

        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Please Enter the Password");

        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);

            Criterion cr1 = Restrictions.eq("email", email);
            Criterion cr2 = Restrictions.eq("password", password);

            c.add(cr1);
            c.add(cr2);

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid credentials");

            } else {

                User user = (User) c.list().get(0);

                responseObject.addProperty("status", Boolean.TRUE);
                HttpSession ses = request.getSession();

                if (user.getVerification().equals("verified")) {
                    ses.setAttribute("user", user);
                    responseObject.addProperty("message", "2");
                } else {
                    ses.setAttribute("email", email);
                    responseObject.addProperty("message", "1");
                }

            }

            s.close();
        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
