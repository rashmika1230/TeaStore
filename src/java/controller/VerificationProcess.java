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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "VerificationProcess", urlPatterns = {"/VerificationProcess"})
public class VerificationProcess extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        try {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("email") == null) {
                responseObject.addProperty("message", "1");
            } else {
                String email = ses.getAttribute("email").toString();

                JsonObject verification = gson.fromJson(request.getReader(), JsonObject.class);
                String verificationCode = verification.get("verificationCode").getAsString();
//            System.out.println(verificationCode);

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria c = s.createCriteria(User.class);

                Criterion cr1 = Restrictions.eq("email", email);
                Criterion cr2 = Restrictions.eq("verification", verificationCode);

                c.add(cr1);
                c.add(cr2);

                if (c.list().isEmpty()) {
                    responseObject.addProperty("message", "Invalid Verification Code");
                } else {
                    User u = (User) c.list().get(0);
                    u.setVerification("Verified");

                    s.update(u);
                    s.beginTransaction().commit();
                    s.close();

                    ses.setAttribute("user", u);

                    responseObject.addProperty("status", Boolean.TRUE);
                    responseObject.addProperty("message", "Verification Successfully..!");
                }
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Somthing went wrong");

        }
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
