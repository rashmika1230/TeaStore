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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadcartItems", urlPatterns = {"/LoadcartItems"})
public class LoadcartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);
        try {

            Session s = HibernateUtil.getSessionFactory().openSession();

            User user = (User) request.getSession().getAttribute("user");

            if (user != null) {
                //        System.out.println(user.getFirst_name());
                Criteria c1 = s.createCriteria(Cart.class);
                c1.add(Restrictions.eq("user", user));

                List<Cart> cartList = c1.list();

//        System.out.println(cartList);
                if (cartList.isEmpty()) {
                    responseObject.addProperty("message", "Cart is empty");
                } else {
                    for (Cart cart : cartList) {
                        cart.setUser(null);
                    }

                    responseObject.addProperty("message", "cart loaded successfull");
                    responseObject.addProperty("status", true);
                    responseObject.add("cartList", gson.toJsonTree(cartList));
                }
            } else { // session cart

                ArrayList<Cart> sessionCart = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");

                if (sessionCart != null) {

                    if (sessionCart.isEmpty()) {
                        responseObject.addProperty("message", "session cart is empty");
                    } else {
                        for (Cart cart : sessionCart) {
                            cart.setUser(null);
                        }

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "session cart load");
                        responseObject.add("cartList", gson.toJsonTree(sessionCart));
                    }

                } else {
                    responseObject.addProperty("message", "session cart is empty");
                }

            }
        } catch (Exception e) {
            responseObject.addProperty("message", "somthing went wrong");
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
