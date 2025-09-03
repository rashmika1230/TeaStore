package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Stock;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

@WebServlet(name = "LoadStockDetails", urlPatterns = {"/LoadStockDetails"})
public class LoadStockDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Session session = null;
        
        try {
            // Safely parse parameters with default values
            int page = parseIntParameter(request, "page", 1);
            int size = parseIntParameter(request, "size", 10);
            
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Create criteria for paginated stock data
            Criteria criteria = session.createCriteria(Stock.class);
            
            // Set pagination
            criteria.setFirstResult((page - 1) * size);
            criteria.setMaxResults(size);
            
            // Order by date descending
            criteria.addOrder(Order.desc("created_at"));
            
            // Fetch stock data
            List<Stock> stockList = criteria.list();
            
            // Get total count
            Criteria countCriteria = session.createCriteria(Stock.class);
            countCriteria.setProjection(Projections.rowCount());
            Long totalCount = (Long) countCriteria.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalCount / size);
            
            // Build JSON response
            JsonArray stockArray = new JsonArray();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (Stock stock : stockList) {
                JsonObject stockObj = new JsonObject();
                stockObj.addProperty("date", dateFormat.format(stock.getCreated_at()));
                stockObj.addProperty("productName", stock.getProduct().getName());
                stockObj.addProperty("sizeName", stock.getSize().getName());
                stockObj.addProperty("price", stock.getPrice());
                stockObj.addProperty("quantity", stock.getQty());
                stockObj.addProperty("id", stock.getId());
                stockArray.add(stockObj);
            }
            
            responseObject.addProperty("status", true);
            responseObject.add("stockList", stockArray);
            responseObject.addProperty("totalPages", totalPages);
            
        } catch (Exception e) {
            responseObject.addProperty("message", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().write(responseObject.toString());
    }
    
    // Helper method to safely parse integer parameters
    private int parseIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        try {
            return Integer.parseInt(request.getParameter(paramName));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}