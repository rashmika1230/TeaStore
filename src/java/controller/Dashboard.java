
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.Stock;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

@WebServlet(name = "Dashboard", urlPatterns = {"/Dashboard"})
public class Dashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        //1) Read page & size from query params
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
        int size = (sizeParam != null) ? Integer.parseInt(sizeParam) : 10;

        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            //2) Fetch this page of OrderItems
            Criteria c1 = s.createCriteria(OrderItems.class);
            c1.addOrder(org.hibernate.criterion.Order.desc("orders.id"));
            c1.setFirstResult((page - 1) * size);
            c1.setMaxResults(size);

            @SuppressWarnings("unchecked")
            List<OrderItems> orderItemsesList = c1.list();

            // 3) Total count & pages
            Criteria countCriteria = s.createCriteria(OrderItems.class);
            countCriteria.setProjection(Projections.rowCount());
            Long totalCount = (Long) countCriteria.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalCount / size);

            // 4) Build JSON response
            if (orderItemsesList.isEmpty()) {
                responseObject.addProperty("message", "cant found Orders");
            } else {
                responseObject.add("orderItemList", gson.toJsonTree(orderItemsesList));
                responseObject.addProperty("totalPages", totalPages);
                responseObject.addProperty("status", Boolean.TRUE);
            }

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
        } finally {
            s.close();
        }
    }

}
