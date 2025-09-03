package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Size;
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
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.sql.JoinType;

@WebServlet(name = "AdvanceSearch", urlPatterns = {"/AdvanceSearch"})
public class AdvanceSearch extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            
            JsonObject requestData = gson.fromJson(request.getReader(), JsonObject.class);
            
            
            String categoryId = requestData.has("category") && !requestData.get("category").isJsonNull() ? 
                                requestData.get("category").getAsString() : null;
            String colorId = requestData.has("color") && !requestData.get("color").isJsonNull() ? 
                             requestData.get("color").getAsString() : null;
            String sizeId = requestData.get("size").getAsString();
            String sortValue = requestData.get("sortValue").getAsString();
            JsonObject priceRange = requestData.getAsJsonObject("priceRange");
            double minPrice = priceRange.get("min").getAsDouble();
            double maxPrice = priceRange.get("max").getAsDouble();

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(Stock.class, "stock");
            
            
            criteria.createAlias("stock.product", "product", JoinType.INNER_JOIN);
            criteria.createAlias("product.category", "category", JoinType.INNER_JOIN);
            criteria.createAlias("product.color", "color", JoinType.INNER_JOIN);
            criteria.createAlias("stock.size", "size", JoinType.INNER_JOIN);
            
         
            Conjunction andConditions = Restrictions.conjunction();
            
            
            if (categoryId != null) {
                andConditions.add(Restrictions.eq("category.id", Integer.parseInt(categoryId)));
            }
            
           
            if (colorId != null) {
                andConditions.add(Restrictions.eq("color.id", Integer.parseInt(colorId)));
            }
            
           
            if (!sizeId.equals("0")) {
                andConditions.add(Restrictions.eq("size.id", Integer.parseInt(sizeId)));
            }
            
           
            andConditions.add(Restrictions.between("stock.price", minPrice, maxPrice));
            
            criteria.add(andConditions);
            
            
            switch (sortValue) {
                case "Default":
                    criteria.addOrder(Order.desc("stock.created_at"));
                    break;               
                case "Name":
                    criteria.addOrder(Order.asc("product.name"));
                    break;
                case "price":
                    criteria.addOrder(Order.asc("stock.price"));
                    break;
            }
            
            responseObject.addProperty("allProductcount", criteria.list().size());
            
            if (requestData.has("firstResult")) {
                int firstResult = requestData.get("firstResult").getAsInt();
                criteria.setFirstResult(firstResult);
                criteria.setMaxResults(9);
            }
            
            
            
            // Execute query
            List<Stock> stockList = criteria.list();
            
            // Detach objects and clean up relationships
//            for (Stock stock : stockList) {
//                // Clear bidirectional relationships to prevent serialization issues
//                if (stock.getProduct() != null) {
//                    stock.setProduct(null); // Assuming bidirectional relationship
//                }
//            }
            
            // Prepare response
            responseObject.addProperty("status", true);
            responseObject.add("stockList", gson.toJsonTree(stockList));
            
            session.close();
        } catch (Exception e) {
            
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Error during search: " + e.getMessage());
        }
        
        out.print(responseObject.toString());
        out.flush();
    }
}