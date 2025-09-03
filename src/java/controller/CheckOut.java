package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryTypes;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
import hibernate.Stock;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int ORDER_PENDING = 1;
    private static final int WITHIN_COLOMBO = 1;
    private static final int OUT_OF_COLOMBO = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();

        try {
            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                responseObject.addProperty("message", "Please Log Again");
            } else {
                
                // Get checkout data from session (stored by PayhereProcess)
                String checkoutDataStr = (String) request.getSession().getAttribute("checkoutData");
                
                if (checkoutDataStr == null) {
                    responseObject.addProperty("message", "Invalid checkout session. Please try again.");
                } else {
                    
                    JsonObject checkoutData = gson.fromJson(checkoutDataStr, JsonObject.class);
                    JsonObject addressData = checkoutData.getAsJsonObject("addressData");
                    
                    boolean isCurrentAddress = addressData.get("isCurrentAddress").getAsBoolean();
                    String firstName = addressData.get("firstName").getAsString();
                    String lastName = addressData.get("lastName").getAsString();
                    String citySelect = addressData.get("citySelect").getAsString();
                    String addressLine = addressData.get("addressLine").getAsString();
                    String postalCode = addressData.get("postalCode").getAsString();
                    String mobile = addressData.get("mobile").getAsString();
                    String email = addressData.get("email").getAsString();

                    Address address = null;

                    if (isCurrentAddress) {
                        // Get existing address
                        Criteria c1 = s.createCriteria(Address.class);
                        c1.add(Restrictions.eq("user", user));
                        c1.addOrder(Order.desc("id"));

                        if (c1.list().isEmpty()) {
                            responseObject.addProperty("message", "Can't find the Address");
                        } else {
                            address = (Address) c1.list().get(0);
                            processCheckoutDatabase(s, tr, user, address, responseObject);
                        }

                    } else {
                        // Create and save new address
                        City city = (City) s.get(City.class, Integer.valueOf(citySelect));

                        if (city == null) {
                            responseObject.addProperty("message", "Invalid city name");
                        } else {
                            address = new Address();
                            address.setFirst_name(firstName);
                            address.setLast_name(lastName);
                            address.setEmail(email);
                            address.setMobile(mobile);
                            address.setLine(addressLine);
                            address.setPostal_code(postalCode);
                            address.setCity(city);
                            address.setUser(user);

                            s.save(address);
                            processCheckoutDatabase(s, tr, user, address, responseObject);
                        }
                    }
                    
                    // Clear checkout data from session
                    request.getSession().removeAttribute("checkoutData");
                }
            }

            if (responseObject.get("status").getAsBoolean()) {
                tr.commit();
            } else {
                tr.rollback();
            }

        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            responseObject.addProperty("message", "Something went wrong during checkout");
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    private void processCheckoutDatabase(Session s, Transaction tr, User user, Address address, JsonObject responseObject) {

        try {
            // Create order
            Orders orders = new Orders();
            orders.setAddress(address);
            orders.setOrder_date(new Date());
            orders.setUser(user);

            int orderId = (int) s.save(orders);

            // Get cart items
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "You have no products to buy");
                return;
            }

            // Get order status and delivery types
            OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, ORDER_PENDING);
            DeliveryTypes withInColombo = (DeliveryTypes) s.get(DeliveryTypes.class, WITHIN_COLOMBO);
            DeliveryTypes outOfColombo = (DeliveryTypes) s.get(DeliveryTypes.class, OUT_OF_COLOMBO);

            // Process each cart item
            for (Cart cart : cartList) {
                
                OrderItems orderItems = new OrderItems();

                // Set delivery type based on city
                if (address.getCity().getName().equalsIgnoreCase("Colombo")) {
                    orderItems.setDeliveryTypes(withInColombo);
                } else {
                    orderItems.setDeliveryTypes(outOfColombo);
                }

                Stock stock = cart.getStock();

                // Check if sufficient stock is available
                if (stock.getQty() < cart.getQty()) {
                    responseObject.addProperty("message", "Insufficient stock for " + stock.getProduct().getName());
                    return;
                }

                orderItems.setOrderStatus(orderStatus);
                orderItems.setOrders(orders);
                orderItems.setStock(stock);
                orderItems.setQty(cart.getQty());

                s.save(orderItems);

                // Update stock quantity
                stock.setQty(stock.getQty() - cart.getQty());
                s.update(stock);

                // Remove item from cart
                s.delete(cart);
            }

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Order created successfully");
            responseObject.addProperty("orderId", orderId);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Something went wrong during order processing");
        }
    }
}