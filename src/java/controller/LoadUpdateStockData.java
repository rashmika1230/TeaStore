package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Size;
import hibernate.Stock;
import java.io.BufferedReader;
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
import org.hibernate.Query;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadUpdateStockData", urlPatterns = {"/LoadUpdateStockData"})
public class LoadUpdateStockData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getParameter("sid");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            Stock stock = (Stock) s.get(Stock.class, Integer.valueOf(sid));

            Criteria c1 = s.createCriteria(Size.class);
            List<Size> sizeList = c1.list();

            if (stock == null) {

            } else {

                responseObject.addProperty("size", stock.getSize().getId());
                responseObject.addProperty("productName", stock.getProduct().getName());
                responseObject.addProperty("price", stock.getPrice());
                responseObject.addProperty("stockId", stock.getId());
                responseObject.addProperty("qty", stock.getQty());
                responseObject.add("sizeList", gson.toJsonTree(sizeList));

                responseObject.addProperty("status", Boolean.TRUE);

            }

        } catch (Exception e) {

        } finally {
            s.close();
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        Gson gson = new Gson();
        JsonObject payload = gson.fromJson(json, JsonObject.class);

        String stockIdStr = payload.get("stockId").getAsString();
        String sizeIdStr = payload.get("sizeId").getAsString();
        String stockPriceStr = payload.get("stockPrice").getAsString();
        String qtyStr = payload.get("qty").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Login First");
        } else if (!Util.isInteger(stockIdStr)) {
            responseObject.addProperty("message", "Invalid Stock ID");
        } else if (Integer.parseInt(stockIdStr) <= 0) {
            responseObject.addProperty("message", "Invalid Stock ID");
        } else if (!Util.isInteger(sizeIdStr)) {
            responseObject.addProperty("message", "Invalid Size");
        } else if (Integer.parseInt(sizeIdStr) <= 0) {
            responseObject.addProperty("message", "Please Select a Size");
        } else if (stockPriceStr.isEmpty()) {
            responseObject.addProperty("message", "Please Enter the Price");
        } else if (!Util.isDouble(stockPriceStr)) {
            responseObject.addProperty("message", "Invalid Price");
        } else if (Double.parseDouble(stockPriceStr) <= 0) {
            responseObject.addProperty("message", "Invalid Price");
        } else if (qtyStr.isEmpty()) {
            responseObject.addProperty("message", "Please Select Quantity");
        } else if (!Util.isInteger(qtyStr)) {
            responseObject.addProperty("message", "Invalid Quantity");
        } else if (Integer.parseInt(qtyStr) <= 0) {
            responseObject.addProperty("message", "Invalid Quantity");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();

            Stock stock = (Stock) s.get(Stock.class, Integer.valueOf(stockIdStr));

            if (stock == null) {
                responseObject.addProperty("message", "Stock not found");
            } else {
                Size size = (Size) s.get(Size.class, Integer.valueOf(sizeIdStr));

                if (size == null) {
                    responseObject.addProperty("message", "Please Select a Valid Size");
                } else {

                    double newPrice = Double.parseDouble(stockPriceStr);
                    int newQty = Integer.parseInt(qtyStr);

                    Criteria c1 = s.createCriteria(Stock.class);
                    c1.add(Restrictions.eq("size", size));
                    c1.add(Restrictions.eq("product", stock.getProduct()));
                    c1.add(Restrictions.eq("price", newPrice));
                    c1.add(Restrictions.ne("id", stock.getId()));

                    if (!c1.list().isEmpty()) {
                        responseObject.addProperty("message", "This product with size and price already exists");
                    } else {

                        Criteria c2 = s.createCriteria(OrderItems.class);
                        c2.add(Restrictions.eq("stock", stock));

                        if (c2.list().isEmpty()) {
                            stock.setPrice(newPrice);
                            stock.setQty(newQty);
                            stock.setSize(size);

                            s.update(stock);
                            s.beginTransaction().commit();
                            s.close();

                            responseObject.addProperty("status", Boolean.TRUE);
                            responseObject.addProperty("message", "Stock updated successfully");
                        } else {
                            responseObject.addProperty("message", "Can't Update Product");

                        }

                    }
                }
            }
            
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
