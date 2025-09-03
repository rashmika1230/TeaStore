package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Size;
import hibernate.Stock;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "UpdateStock", urlPatterns = {"/UpdateStock"})
public class UpdateStock extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        String productId = request.getParameter("productId");
        String sizeId = request.getParameter("sizeId");
        String stockPrice = request.getParameter("stockPrice");
        String qty = request.getParameter("qty");

//        System.out.println(qty);
//        System.out.println(stockPrice);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

         if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Loging First");
        } else if (!Util.isInteger(productId)) {
            responseObject.addProperty("message", "Invalid Product");
        } else if (Integer.parseInt(productId) <= 0) {
            responseObject.addProperty("message", "please Select a Product");
        } else if (!Util.isInteger(sizeId)) {
            responseObject.addProperty("message", "Invalid Size");
        } else if (Integer.parseInt(sizeId) <= 0) {
            responseObject.addProperty("message", "please Select a Size");
        } else if (stockPrice.isEmpty()) {
            responseObject.addProperty("message", "please Enter the Price");
        } else if (!Util.isDouble(stockPrice)) {
            responseObject.addProperty("message", "Invalid Price");
        } else if (Double.parseDouble(stockPrice) <= 0) {
            responseObject.addProperty("message", "Invalid Price");
        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Please Select Quantity");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Quantity");
        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Invalid Quantity");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();

            Product product = (Product) s.get(Product.class, Integer.valueOf(productId));

            if (product == null) {
                responseObject.addProperty("message", "Please Select a Valid Product");
            } else {
                Size size = (Size) s.get(Size.class, Integer.valueOf(sizeId));

                if (size == null) {
                    responseObject.addProperty("message", "Please Select a Valid Size");
                } else {

                    Criteria c1 = s.createCriteria(Stock.class);
                    c1.add(Restrictions.eq("size", size));
                    c1.add(Restrictions.eq("product", product));
                    c1.add(Restrictions.eq("price", Double.valueOf(stockPrice)));

                    if (c1.list().isEmpty()) {
                        Stock stock = new Stock();

                        stock.setPrice(Double.parseDouble(stockPrice));
                        stock.setQty(Integer.parseInt(qty));
                        stock.setProduct(product);
                        stock.setSize(size);
                        stock.setCreated_at(new Date());

                        s.save(stock);
                        s.beginTransaction().commit();
                        s.close();

                        responseObject.addProperty("status", Boolean.TRUE);
                    } else {
                        responseObject.addProperty("message", "this prodct already added ");
                    }
                }

            }

        }

        Gson gson = new Gson();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
