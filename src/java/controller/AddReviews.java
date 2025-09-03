package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Reviews;
import hibernate.Stock;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "AddReviews", urlPatterns = {"/AddReviews"})
public class AddReviews extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        Gson gson = new Gson();

        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);

//        String userName = requestJson.get("userName").getAsString();
//        String userEmail = requestJson.get("userEmail").getAsString();
        String userMessage = requestJson.get("userMessage").getAsString();
        String stockId = requestJson.get("stockId").getAsString();
        String rateValue = requestJson.get("rateValue").getAsString();

//        System.out.println(userName);
//        System.out.println(userEmail);
//        System.out.println(userMessage);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        try {

            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                responseObject.addProperty("message", "1");
            } else {

//            if (userName.isEmpty()) {
//                responseObject.addProperty("message", "Name can not empty");
//            } else if (userEmail.isEmpty()) {
//                responseObject.addProperty("message", "Email can not empty");
//            } else {}
                if (Integer.parseInt(rateValue) == 0) {
                    responseObject.addProperty("message", "Select Your rating");
                } else if (!Util.isInteger(rateValue)) {
                    responseObject.addProperty("message", "invalid rate");
                } else if (userMessage.isEmpty()) {
                    responseObject.addProperty("message", "Enter the message");
                } else if (!Util.isInteger(stockId)) {
                    responseObject.addProperty("message", "Invalid Product");

                } else {

                    Session s = HibernateUtil.getSessionFactory().openSession();

                    Stock stock = (Stock) s.get(Stock.class, Integer.valueOf(stockId));

                    if (stock == null) {
                        responseObject.addProperty("message", "Invalid Product");
                    } else {
                        Reviews reviews = new Reviews();

                        reviews.setMessage(userMessage);
                        reviews.setUser(user);
                        reviews.setStock(stock);
                        reviews.setReview_date(new Date());
                        reviews.setRatings(Integer.parseInt(rateValue));

                        s.save(reviews);
                        s.beginTransaction().commit();
                        s.close();

                        responseObject.addProperty("status", Boolean.TRUE);
                        responseObject.addProperty("message", "Success");
                    }

                }

            }

        } catch (Exception e) {
            responseObject.addProperty("message", "Somthing went wrong");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();

        String stockId = request.getParameter("sid");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria c1 = s.createCriteria(Reviews.class);
        c1.add(Restrictions.eq("stock.id", Integer.valueOf(stockId)));
        c1.addOrder(Order.desc("review_date"));
        c1.setMaxResults(10);

        List<Reviews> reviewList = c1.list();

        if (reviewList.isEmpty()) {
            responseObject.addProperty("message", "1");
        } else {
            for (Reviews reviews : reviewList) {
                reviews.getUser().setPassword(null);
                reviews.getUser().setMobile(null);
                reviews.getUser().setVerification(null);
            }

            responseObject.add("reviewList", gson.toJsonTree(reviewList));
            responseObject.addProperty("status", Boolean.TRUE);

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
