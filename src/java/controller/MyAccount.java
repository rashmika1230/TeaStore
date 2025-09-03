/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
@WebServlet(name = "MyAccount", urlPatterns = {"/MyAccount"})
public class MyAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("my acc ok");
        HttpSession ses = request.getSession();

        if (ses != null && ses.getAttribute("user") != null) {

            User user = (User) ses.getAttribute("user");

            JsonObject responseObject = new JsonObject();

            responseObject.addProperty("firstName", user.getFirst_name());
            responseObject.addProperty("lastName", user.getLast_name());
            responseObject.addProperty("email", user.getEmail());
            responseObject.addProperty("mobile", user.getMobile());

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c = s.createCriteria(Address.class);

            c.add(Restrictions.eq("user", user));

            Gson gson = new Gson();

            if (!c.list().isEmpty()) {
                List<Address> addressList = c.list();
                responseObject.add("addressList", gson.toJsonTree(addressList));

            }

            String toJson = gson.toJson(responseObject);

            response.setContentType("application/json");
            response.getWriter().write(toJson);

        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("put ok");

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String mobile = userData.get("mobile").getAsString();
        String line = userData.get("line").getAsString();
        String postalCode = userData.get("postalCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();

        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(mobile);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {

            responseObject.addProperty("message", "First Name can not be empty ! ");

        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name can not be empty ! ");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile can not be empty ! ");

        } else if (!Util.isMobileValid(mobile)) {
            responseObject.addProperty("message", "Invalid Mobile");

        } else if (line.isEmpty()) {
            responseObject.addProperty("message", "Line One can not be empty!");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Postal Code can not be empty!");
        } else if (!Util.isCodeValid(postalCode)) { //check is number
            responseObject.addProperty("message", "Postal Code must be a (4-5) long digit");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Select a city");
        } else {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("user") != null) {

                User user = (User) ses.getAttribute("user");

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", user.getEmail()));

                if (!c.list().isEmpty()) {

                    User u = (User) c.list().get(0);

                    u.setFirst_name(firstName);
                    u.setLast_name(lastName);
                    u.setMobile(mobile);

                    City city = (City) s.load(City.class, cityId);

                    Criteria addressCriteria = s.createCriteria(Address.class);

                    addressCriteria.add(Restrictions.eq("line", line));
                    addressCriteria.add(Restrictions.eq("postal_code", postalCode));
                    addressCriteria.add(Restrictions.eq("city", city));
                    addressCriteria.add(Restrictions.eq("user", u));

                    ses.setAttribute("user", u);

                    s.merge(u);

                    if (addressCriteria.list().isEmpty()) {

                        Address address = new Address();
                        address.setLine(line);
                        address.setPostal_code(postalCode);
                        address.setCity(city);
                        address.setUser(u);

                        s.save(address);
                    }

                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "User profile details update successfully");
                    s.close();
                }
            }

        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
