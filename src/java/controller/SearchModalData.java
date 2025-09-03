
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

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "SearchModalData", urlPatterns = {"/SearchModalData"})
public class SearchModalData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject reqData = gson.fromJson(request.getReader(), JsonObject.class);

        String productName = reqData.get("productName").getAsString();
        String productDesc = reqData.get("productDesc").getAsString();
        String productColor = reqData.get("productColor").getAsString();
        String productSize = reqData.get("productSize").getAsString();

        System.out.println(productSize);
        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria c2 = s.createCriteria(Color.class);
        c2.add(Restrictions.eq("name", productColor));
        Color color = (Color) c2.uniqueResult();

//        Criteria c3 = s.createCriteria(Category.class);
//        c3.add(Restrictions.eq("name", productCategory));
//        Category category = (Category) c3.uniqueResult();
//        Criteria c5 = s.createCriteria(Size.class);
//        c5.add(Restrictions.eq("name", productSize));
//        Size size = (Size) c5.uniqueResult();
//        System.out.println(size.getName());
        Criteria c1 = s.createCriteria(Product.class);
        c1.add(Restrictions.eq("name", productName));
        c1.add(Restrictions.eq("description", productDesc));
        c1.add(Restrictions.eq("color", color));

        Product product = (Product) c1.uniqueResult();

//        System.out.println(product.getName());
        Criteria c4 = s.createCriteria(Stock.class);
        c4.add(Restrictions.eq("product", product));
        c4.add(Restrictions.eq("size.id", Integer.parseInt(productSize)));
        c4.addOrder(Order.desc("created_at"));
        
        c4.setMaxResults(1);
        
        List<Stock> stockList = c4.list();
//        System.out.println(stockList);

        if (stockList.isEmpty()) {
            responseObject.addProperty("message", "cant not found product"); // can't found product
        } else {
            responseObject.add("stockList", gson.toJsonTree(stockList));
            responseObject.addProperty("status", true);
            System.out.println();

        }

        s.close();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
