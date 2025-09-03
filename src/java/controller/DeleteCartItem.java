package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "DeleteCartItem", urlPatterns = {"/DeleteCartItem"})
public class DeleteCartItem extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        System.out.println("ok");
        Gson gson = new Gson();
        JsonObject reqJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        String cartId = reqJsonObject.get("cartId").getAsString();
        String stockId = reqJsonObject.get("stockId").getAsString();

//        System.out.println(cartId);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            if (!Util.isInteger(cartId)) {
                responseObject.addProperty("message", "cant delete product");
            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                User user = (User) req.getSession().getAttribute("user");

                if (user != null) { // delete from DB

                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("id", Integer.valueOf(cartId)));
                    c1.add(Restrictions.eq("user", user));

                    Cart cart = (Cart) c1.uniqueResult();

                    if (cart == null) {
                        responseObject.addProperty("message", "can't delete");
                    } else {
                        s.delete(cart);
                        s.beginTransaction().commit();
                        s.close();

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Delete Success");
                    }

//            System.out.println(cart.getUser());
//            System.out.println(cart.getStock().getProduct().getName());

                } else { //Remove from session
                    ArrayList<Cart> sessionCarts = (ArrayList<Cart>) req.getSession().getAttribute("sessionCart");

                    if (sessionCarts != null && !sessionCarts.isEmpty()) {

                        boolean removed = sessionCarts.removeIf(cart
                                -> cart.getId() == Integer.parseInt(cartId) && cart.getStock().getId() == Integer.parseInt(stockId)
                        );

                        if (removed) {
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Item removed from cart.");
                        } else {
                            responseObject.addProperty("message", "Item not found in cart.");
                        }

                    } else {
                        responseObject.addProperty("message", "Your cart is empty.");
                    }

                }

            }

        } catch (Exception e) {
            responseObject.addProperty("message", "somthing went wrong");
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session s = HibernateUtil.getSessionFactory().openSession();

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {//db

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));

            List<Cart> cartList = c1.list();

            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "Can't Delete Cart Have't any products");

            } else {
                for (Cart cart : cartList) {

                    if (cart == null) {

                        responseObject.addProperty("message", "Can't Delete");

                    } else {
                        s.delete(cart);

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Delete Success");

                    }
                }

                s.beginTransaction().commit();
                s.close();
            }

        } else { //session

            request.getSession().setAttribute("sessionCart", null);
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Delete Success");

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
