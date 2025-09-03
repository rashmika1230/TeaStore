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

@WebServlet(name = "LoadStockData", urlPatterns = {"/LoadStockData"})
public class LoadStockData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        //search product
        Criteria c1 = s.createCriteria(Product.class);
        List<Product> productList = c1.list();

        //seacrh Sizes
        Criteria c2 = s.createCriteria(Size.class);
        List<Size> sizeList = c2.list();

        //load category
        Criteria c3 = s.createCriteria(Category.class);
        List<Category> categoryList = c3.list();

        //load color
        Criteria c4 = s.createCriteria(Color.class);
        List<Color> colorList = c4.list();

        Criteria c5 = s.createCriteria(Stock.class);
        c5.add(Restrictions.gt("qty", 0));
        c5.add(Restrictions.eq("size.id", 1));
        c5.addOrder(Order.desc("created_at"));
        c5.setMaxResults(9);
        List<Stock> stockList = c5.list();

        Gson gson = new Gson();
        responseObject.addProperty("allProductcount", c5.list().size());
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.add("sizeList", gson.toJsonTree(sizeList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("stockList", gson.toJsonTree(stockList));

        responseObject.addProperty("status", Boolean.TRUE);
        s.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("ok");

    }

}
