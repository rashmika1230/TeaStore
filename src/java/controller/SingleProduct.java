package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Stock;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "SingleProduct", urlPatterns = {"/SingleProduct"})
public class SingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("ok");
        String stockId = request.getParameter("sid");
        System.out.println(stockId);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        try {
            if (Util.isInteger(stockId)) {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Stock stock = (Stock) s.get(Stock.class, Integer.valueOf(stockId));

                //related product
                Criteria c1 = s.createCriteria(Product.class);
                c1.add(Restrictions.eq("category", stock.getProduct().getCategory()));
                c1.add(Restrictions.eq("color", stock.getProduct().getColor()));

                List<Product> productList = c1.list();
//            for (Product product : productList) {
//                System.out.println(product.getName());
//
//            }

                Criteria c2 = s.createCriteria(Stock.class);
                c2.add(Restrictions.in("product", productList));
                c2.add(Restrictions.ne("id", stock.getId()));
//                c2.add(Restrictions.eq("size", stock.getSize()));
                c2.addOrder(Order.asc("price"));
                c2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                c2.setFirstResult(0);
                c2.setMaxResults(4);

                List<Stock> stockList = c2.list();
//            
//            for (Stock stock1 : stockList) {
//                System.out.println(stock1.getProduct().getName());
//            }

                Gson gson = new Gson();
                responseObject.add("stock", gson.toJsonTree(stock));
                responseObject.add("stockList", gson.toJsonTree(stockList));
                responseObject.addProperty("status", Boolean.TRUE);

                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(responseObject));

            } else {
                responseObject.addProperty("message", "invalid product");
            }

        } catch (Exception e) {
            responseObject.addProperty("message", "Product Not Found!");
        }

    }

}
