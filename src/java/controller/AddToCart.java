/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Stock;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stockId = request.getParameter("sid");
        String qty = request.getParameter("qty");

        System.out.println(stockId);
        System.out.println(qty);

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (!Util.isInteger(stockId)) {
            responseObject.addProperty("message", "Invalid Product");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "invalid Quantity");
        } else if (Integer.parseInt(qty) == 0) {
            responseObject.addProperty("message", "can't add , Quantity equal 0");
        } else {

            try {

                Session s = HibernateUtil.getSessionFactory().openSession();
                Transaction tr = s.beginTransaction();

                Stock stock = (Stock) s.get(Stock.class, Integer.valueOf(stockId));
                System.out.println(stock.getProduct().getName());
                if (stock == null) {
                    responseObject.addProperty("message", "can't found this product");
                } else {//found product in databse

                    User user = (User) request.getSession().getAttribute("user");
//                    System.out.println(user.getFirst_name());
                    if (user != null) { //availaible user

                        //add to database
                        Criteria c1 = s.createCriteria(Cart.class);
                        c1.add(Restrictions.eq("user", user));
                        c1.add(Restrictions.eq("stock", stock));

                        if (c1.list().isEmpty()) { //not same product -> insert

                            if (Integer.parseInt(qty) <= stock.getQty()) { //quantity availible

                                Cart cart = new Cart();
                                cart.setQty(Integer.parseInt(qty));
                                cart.setUser(user);
                                cart.setStock(stock);

                                s.save(cart);
                                tr.commit();

                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "product add to cart");

                            } else {
                                responseObject.addProperty("message", "Insufficient Product Quantity");
                            }

                        } else { // update

                            Cart cart = (Cart) c1.uniqueResult();

                            int newQty = cart.getQty() + Integer.parseInt(qty);

                            if (newQty <= stock.getQty()) {
                                cart.setQty(newQty);
                                s.update(cart);
                                tr.commit();
                                
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "product Updated cart");
                            } else {
                                responseObject.addProperty("message", "Insufficient Product Quantity");
                            }

                        }

                    } else { // not availible -> session cart
                        System.out.println("ses cart");
                        HttpSession ses = request.getSession();

                        if (ses.getAttribute("sessionCart") == null) { // session cart not available

                            if (Integer.parseInt(qty) <= stock.getQty()) {
                                ArrayList<Cart> sessCart = new ArrayList<>();
                                Cart cart = new Cart();

                                cart.setQty(Integer.parseInt(qty));
                                cart.setUser(null);
                                cart.setStock(stock);
                                sessCart.add(cart);

                                ses.setAttribute("sessionCart", sessCart);
                                
                                    responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "product added session cart");

                            } else {
                                responseObject.addProperty("message", "insufficient Quantity");
                            }

                        } else { // session cart available

                            ArrayList<Cart> sessionCartList = (ArrayList<Cart>) ses.getAttribute("sessionCart");
                            Cart foundedCart = null;

                            for (Cart cart : sessionCartList) {
                                if (cart.getStock().getId() == stock.getId()) {
                                    foundedCart = cart;
                                    break;
                                }
                            }

                            if (foundedCart != null) { //session cart found
                                int newQty = foundedCart.getQty() + Integer.parseInt(qty);

                                if (newQty <= stock.getQty()) {
                                    foundedCart.setUser(null);
                                    foundedCart.setQty(newQty);

                                    responseObject.addProperty("status", true);
                                    responseObject.addProperty("message", " session Product cart updated");
                                } else {
                                    responseObject.addProperty("message", "Insufficient Prodcut quantity!!!");
                                }

                            } else {

                                if (Integer.parseInt(qty) <= stock.getId()) {

                                    foundedCart = new Cart();
                                    foundedCart.setQty(Integer.parseInt(qty));
                                    foundedCart.setUser(null);
                                    foundedCart.setStock(stock);
                                    sessionCartList.add(foundedCart);

                                    responseObject.addProperty("status", true);
                                    responseObject.addProperty("message", "product added session cart");

                                } else {
                                    responseObject.addProperty("message", "Insufficient Prodcut quantity!!!");
                                }
                            }
                        }

                    }

                }
            } catch (Exception e) {
//                responseObject.addProperty("message", "something went wrong");
e.printStackTrace();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
