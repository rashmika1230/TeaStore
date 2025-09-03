package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.User;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name="OrderHistory", urlPatterns={"/OrderHistory"})
public class OrderHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("status", false);

       
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            json.addProperty("message", "Not authenticated");
            resp.getWriter().write(gson.toJson(json));
            return;
        }

       
        int page = 1, size = 10;
        try {
            String p = req.getParameter("page");
            String s = req.getParameter("size");
            if (p != null) page = Integer.parseInt(p);
            if (s != null) size = Integer.parseInt(s);
        } catch (NumberFormatException e) {  }

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            
            Criteria crit = session.createCriteria(OrderItems.class, "oi")
                .createAlias("oi.orders", "o")
                .add(Restrictions.eq("o.user.id", user.getId()))     // <-- restrict to current user
                .addOrder(org.hibernate.criterion.Order.desc("o.id"))
                .setFirstResult((page-1)*size)
                .setMaxResults(size);

            @SuppressWarnings("unchecked")
            List<OrderItems> list = crit.list();

           
            Criteria countCrit = session.createCriteria(OrderItems.class, "oi")
                .createAlias("oi.orders", "o")
                .add(Restrictions.eq("o.user.id", user.getId()))
                .setProjection(Projections.rowCount());
            Long totalCount = (Long) countCrit.uniqueResult();
            int totalPages = (int)Math.ceil((double)totalCount / size);

           
            json.add("orderItemList", gson.toJsonTree(list));
            json.addProperty("totalPages", totalPages);
            json.addProperty("status", true);

        } finally {
            session.close();
        }

        resp.getWriter().write(gson.toJson(json));
    }
}

