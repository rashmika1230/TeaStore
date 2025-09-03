package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet(name = "ChangeOrderStatus", urlPatterns = {"/ChangeOrderStatus"})
public class ChangeOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String oid = request.getParameter("oid");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();

        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Login First");
        } else {
            try {

                OrderItems orderItems = (OrderItems) s.get(OrderItems.class, Integer.valueOf(oid));
                OrderStatus deliverSt = (OrderStatus) s.get(OrderStatus.class, 2);
                OrderStatus completeSt = (OrderStatus) s.get(OrderStatus.class, 3);

                String currentStatus = orderItems.getOrderStatus().getValue();

                if ("Pending".equals(currentStatus)) {
                    orderItems.setOrderStatus(deliverSt);
                } else if ("Delivered".equals(currentStatus)) {
                    orderItems.setOrderStatus(completeSt);
                } else if ("Complete".equals(currentStatus)) {
                    responseObject.addProperty("message", "This order is already completed");
                }

                s.update(orderItems);

                tx.commit();
                responseObject.addProperty("status", true);
            } catch (Exception ex) {
                tx.rollback();
                responseObject.addProperty("message", "Error updating status: " + ex.getMessage());
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
