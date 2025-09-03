package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.Stock;
import hibernate.Orders;
import hibernate.OrderItems;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DashboardStats", urlPatterns = {"/DashboardStats"})
public class DashboardStats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            // Get current month and previous month dates
            Calendar cal = Calendar.getInstance();
            
            // Start of current month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfCurrentMonth = cal.getTime();
            
            // Start of previous month
            cal.add(Calendar.MONTH, -1);
            Date startOfPreviousMonth = cal.getTime();
            
            // End of previous month (start of current month)
            Date endOfPreviousMonth = startOfCurrentMonth;

            // 1) Total Users
            Criteria usersCriteria = s.createCriteria(User.class);
            usersCriteria.setProjection(Projections.rowCount());
            Long totalUsers = (Long) usersCriteria.uniqueResult();

            // Users this month
            Criteria usersThisMonthCriteria = s.createCriteria(User.class);
            usersThisMonthCriteria.add(Restrictions.ge("created_at", startOfCurrentMonth));
            usersThisMonthCriteria.setProjection(Projections.rowCount());
            Long usersThisMonth = (Long) usersThisMonthCriteria.uniqueResult();

            // Users previous month
            Criteria usersPreviousMonthCriteria = s.createCriteria(User.class);
            usersPreviousMonthCriteria.add(Restrictions.ge("created_at", startOfPreviousMonth));
            usersPreviousMonthCriteria.add(Restrictions.lt("created_at", endOfPreviousMonth));
            usersPreviousMonthCriteria.setProjection(Projections.rowCount());
            Long usersPreviousMonth = (Long) usersPreviousMonthCriteria.uniqueResult();

            // Calculate user growth percentage
            double userGrowthPercentage = 0.0;
            if (usersPreviousMonth > 0) {
                userGrowthPercentage = ((double) usersThisMonth / usersPreviousMonth - 1) * 100;
            }

            // 2) Total Tea Products (Stock entries)
            Criteria stockCriteria = s.createCriteria(Stock.class);
            stockCriteria.setProjection(Projections.rowCount());
            Long totalProducts = (Long) stockCriteria.uniqueResult();

            // New products this month (assuming created_at field exists in Stock)
            Criteria newProductsCriteria = s.createCriteria(Stock.class);
            newProductsCriteria.add(Restrictions.ge("created_at", startOfCurrentMonth));
            newProductsCriteria.setProjection(Projections.rowCount());
            Long newProducts = (Long) newProductsCriteria.uniqueResult();

            // 3) Monthly Orders
            Criteria ordersThisMonthCriteria = s.createCriteria(Orders.class);
            ordersThisMonthCriteria.add(Restrictions.ge("order_date", startOfCurrentMonth));
            ordersThisMonthCriteria.setProjection(Projections.rowCount());
            Long ordersThisMonth = (Long) ordersThisMonthCriteria.uniqueResult();

            // Orders previous month
            Criteria ordersPreviousMonthCriteria = s.createCriteria(Orders.class);
            ordersPreviousMonthCriteria.add(Restrictions.ge("order_date", startOfPreviousMonth));
            ordersPreviousMonthCriteria.add(Restrictions.lt("order_date", endOfPreviousMonth));
            ordersPreviousMonthCriteria.setProjection(Projections.rowCount());
            Long ordersPreviousMonth = (Long) ordersPreviousMonthCriteria.uniqueResult();

            // Calculate order growth percentage
            double orderGrowthPercentage = 0.0;
            if (ordersPreviousMonth > 0) {
                orderGrowthPercentage = ((double) ordersThisMonth / ordersPreviousMonth - 1) * 100;
            }

            // 4) Monthly Revenue from OrderItems
            String revenueThisMonthHQL = "SELECT SUM(oi.qty * oi.stock.price) FROM OrderItems oi WHERE oi.orders.order_date >= :startDate";
            Query revenueThisMonthQuery = s.createQuery(revenueThisMonthHQL);
            revenueThisMonthQuery.setParameter("startDate", startOfCurrentMonth);
            Double revenueThisMonthResult = (Double) revenueThisMonthQuery.uniqueResult();
            double revenueThisMonth = (revenueThisMonthResult != null) ? revenueThisMonthResult : 0.0;

            // Revenue previous month
            String revenuePreviousMonthHQL = "SELECT SUM(oi.qty * oi.stock.price) FROM OrderItems oi WHERE oi.orders.order_date >= :startDate AND oi.orders.order_date < :endDate";
            Query revenuePreviousMonthQuery = s.createQuery(revenuePreviousMonthHQL);
            revenuePreviousMonthQuery.setParameter("startDate", startOfPreviousMonth);
            revenuePreviousMonthQuery.setParameter("endDate", endOfPreviousMonth);
            Double revenuePreviousMonthResult = (Double) revenuePreviousMonthQuery.uniqueResult();
            double revenuePreviousMonth = (revenuePreviousMonthResult != null) ? revenuePreviousMonthResult : 0.0;

            // Calculate revenue growth percentage
            double revenueGrowthPercentage = 0.0;
            if (revenuePreviousMonth > 0) {
                revenueGrowthPercentage = ((revenueThisMonth / revenuePreviousMonth) - 1) * 100;
            }
            
            //admin details
            
            HttpSession adminSession = request.getSession(false);
            
            if (adminSession != null && adminSession.getAttribute("admin") != null) {
                
                Admin admin = (Admin) adminSession.getAttribute("admin");
                
                responseObject.addProperty("fname", admin.getFname());
                responseObject.addProperty("lname", admin.getLname());
            }

            // Build JSON response
            JsonObject statsObject = new JsonObject();
            
            JsonObject usersStats = new JsonObject();
            usersStats.addProperty("total", totalUsers != null ? totalUsers : 0);
            usersStats.addProperty("growthPercentage", Math.round(userGrowthPercentage * 10.0) / 10.0);
            usersStats.addProperty("newThisMonth", usersThisMonth != null ? usersThisMonth : 0);
            
            JsonObject productsStats = new JsonObject();
            productsStats.addProperty("total", totalProducts != null ? totalProducts : 0);
            productsStats.addProperty("newThisMonth", newProducts != null ? newProducts : 0);
            
            JsonObject ordersStats = new JsonObject();
            ordersStats.addProperty("total", ordersThisMonth != null ? ordersThisMonth : 0);
            ordersStats.addProperty("growthPercentage", Math.round(orderGrowthPercentage * 10.0) / 10.0);
            
            JsonObject revenueStats = new JsonObject();
            revenueStats.addProperty("total", Math.round(revenueThisMonth * 100.0) / 100.0);
            revenueStats.addProperty("growthPercentage", Math.round(revenueGrowthPercentage * 10.0) / 10.0);
            
            statsObject.add("users", usersStats);
            statsObject.add("products", productsStats);
            statsObject.add("orders", ordersStats);
            statsObject.add("revenue", revenueStats);
            
            responseObject.add("stats", statsObject);
            responseObject.addProperty("status", Boolean.TRUE);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
            
        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error loading dashboard stats: " + e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
        } finally {
            s.close();
        }
    }
}