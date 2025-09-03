package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ContactMail;
import model.Mail;
import model.Util;

@WebServlet(name = "ContactUs", urlPatterns = {"/ContactUs"})
public class ContactUs extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject reqData = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        String fullname = reqData.get("fullname").getAsString();
        String email = reqData.get("email").getAsString();
        String subject = reqData.get("subject").getAsString();
        String message = reqData.get("message").getAsString();

        if (fullname.isEmpty()) {
            responseObject.addProperty("message", "Full name is requierd");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email is requierd");

        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");

        } else if (subject.isEmpty()) {
            responseObject.addProperty("message", "Subject is requierd");

        } else if (message.isEmpty()) {
            responseObject.addProperty("message", "Message is requierd");

        } else {

            Mail.sendMail(email, subject, fullname + " - " + message);
            responseObject.addProperty("status", Boolean.TRUE);

        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
