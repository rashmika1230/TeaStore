package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryTypes;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "PayhereProcess", urlPatterns = {"/PayhereProcess"})
public class PayhereProcess extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int WITHIN_COLOMBO = 1;
    private static final int OUT_OF_COLOMBO = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isCurrentAddress = requJsonObject.get("isCurrentAddress").getAsBoolean();
        String firstName = requJsonObject.get("firstName").getAsString();
        String lastName = requJsonObject.get("lastName").getAsString();
        String citySelect = requJsonObject.get("citySelect").getAsString();
        String addressLine = requJsonObject.get("addressLine").getAsString();
        String postalCode = requJsonObject.get("postalCode").getAsString();
        String mobile = requJsonObject.get("mobile").getAsString();
        String email = requJsonObject.get("email").getAsString();

        Session s = HibernateUtil.getSessionFactory().openSession();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            responseObject.addProperty("message", "Please Log Again");
        } else {

            Address address = null;

            if (isCurrentAddress) {
                // Get existing address
                Criteria c1 = s.createCriteria(Address.class);
                c1.add(Restrictions.eq("user", user));
                c1.addOrder(Order.desc("id"));

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message", "Can't find the Address");
                } else {
                    // Validate required fields for current address
                    if (firstName.isEmpty()) {
                        responseObject.addProperty("message", "First Name is required");
                    } else if (lastName.isEmpty()) {
                        responseObject.addProperty("message", "Last Name is required");
                    } else if (email.isEmpty()) {
                        responseObject.addProperty("message", "Email is required");
                    } else if (!Util.isEmailValid(email)) {
                        responseObject.addProperty("message", "Invalid Email");
                    } else if (mobile.isEmpty()) {
                        responseObject.addProperty("message", "Mobile is required");
                    } else if (!Util.isMobileValid(mobile)) {
                        responseObject.addProperty("message", "Invalid Mobile");
                    } else if (addressLine.isEmpty()) {
                        responseObject.addProperty("message", "Address Line is required");
                    } else if (postalCode.isEmpty()) {
                        responseObject.addProperty("message", "Postal is required");
                    } else if (!Util.isCodeValid(postalCode)) {
                        responseObject.addProperty("message", "Invalid Postal Code");
                    } else if (!Util.isInteger(citySelect)) {
                        responseObject.addProperty("message", "Invalid City");
                    } else if (Integer.parseInt(citySelect) == SELECTOR_DEFAULT_VALUE) {
                        responseObject.addProperty("message", "Invalid City");
                    } else {
                        address = (Address) c1.list().get(0);
                        processPayHere(s, user, address, responseObject, requJsonObject, request);
                    }
                }

            } else {
                // Create new address - validate all fields
                if (firstName.isEmpty()) {
                    responseObject.addProperty("message", "First Name is required");
                } else if (lastName.isEmpty()) {
                    responseObject.addProperty("message", "Last Name is required");
                } else if (email.isEmpty()) {
                    responseObject.addProperty("message", "Email is required");
                } else if (!Util.isEmailValid(email)) {
                    responseObject.addProperty("message", "Invalid Email");
                } else if (mobile.isEmpty()) {
                    responseObject.addProperty("message", "Mobile is required");
                } else if (!Util.isMobileValid(mobile)) {
                    responseObject.addProperty("message", "Invalid Mobile");
                } else if (addressLine.isEmpty()) {
                    responseObject.addProperty("message", "Address Line is required");
                } else if (postalCode.isEmpty()) {
                    responseObject.addProperty("message", "Postal is required");
                } else if (!Util.isCodeValid(postalCode)) {
                    responseObject.addProperty("message", "Invalid Postal Code");
                } else if (!Util.isInteger(citySelect)) {
                    responseObject.addProperty("message", "Invalid City");
                } else if (Integer.parseInt(citySelect) == SELECTOR_DEFAULT_VALUE) {
                    responseObject.addProperty("message", "Invalid City");
                } else {

                    City city = (City) s.get(City.class, Integer.valueOf(citySelect));

                    if (city == null) {
                        responseObject.addProperty("message", "Invalid city name");
                    } else {
                        // Create temporary address object for PayHere (don't save to DB yet)
                        address = new Address();
                        address.setFirst_name(firstName);
                        address.setLast_name(lastName);
                        address.setEmail(email);
                        address.setMobile(mobile);
                        address.setLine(addressLine);
                        address.setPostal_code(postalCode);
                        address.setCity(city);
                        address.setUser(user);

                        processPayHere(s, user, address, responseObject, requJsonObject, request);
                    }
                }
            }
        }

        s.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    private void processPayHere(Session s, User user, Address address, JsonObject responseObject, JsonObject requestData, HttpServletRequest request) {
        try {
            // Get cart items to calculate amount
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "You have no products to buy");
                return;
            }

            DeliveryTypes withInColombo = (DeliveryTypes) s.get(DeliveryTypes.class, WITHIN_COLOMBO);
            DeliveryTypes outOfColombo = (DeliveryTypes) s.get(DeliveryTypes.class, OUT_OF_COLOMBO);

            double amount = 0;
            String items = "";
            double deliveryCharge = 0;

            // Calculate total amount and delivery charge
            for (Cart cart : cartList) {
                amount += cart.getQty() * cart.getStock().getPrice();
                items += cart.getStock().getProduct().getName() + " x " + cart.getQty() + ",";
            }

            // Add delivery charge based on city
            if (address.getCity().getName().equalsIgnoreCase("Colombo")) {
                deliveryCharge = withInColombo.getPrice();
            } else {
                deliveryCharge = outOfColombo.getPrice();
            }

            amount += deliveryCharge;

            // Generate temporary order ID for PayHere (we'll create actual order after payment)
            String tempOrderId = "TEMP_" + System.currentTimeMillis();

            // PayHere process
            String merchantID = "1224009";
            String merchantSecret = "MjgyMDQ5NjUyOTQwNDA4MzQwODI0MjA0ODgzMzIyNjgwOTk5MTk0";
            String orderID = "#000" + tempOrderId;
            String currency = "LKR";
            String formattedAmount = new DecimalFormat("0.00").format(amount);
            String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);

            String hash = PayHere.generateMD5(merchantID + orderID + formattedAmount + currency + merchantSecretMD5);

            JsonObject payHereJson = new JsonObject();
            payHereJson.addProperty("sandbox", true);
            payHereJson.addProperty("merchant_id", merchantID);

            payHereJson.addProperty("return_url", "");
            payHereJson.addProperty("cancel_url", "");
            payHereJson.addProperty("notify_url", "https://f0455ed14863.ngrok-free.app/TeaStore/VerifyPayments");

            payHereJson.addProperty("order_id", orderID);
            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFirst_name());
            payHereJson.addProperty("last_name", user.getLast_name());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", address.getMobile());
            payHereJson.addProperty("address", address.getLine());
            payHereJson.addProperty("city", address.getCity().getName());
            payHereJson.addProperty("country", "Sri Lanka");

            // Store checkout data in session for later use
            JsonObject checkoutData = new JsonObject();
            checkoutData.add("addressData", requestData);
            checkoutData.addProperty("tempOrderId", tempOrderId);
            
            // Store in session attribute
            request.getSession().setAttribute("checkoutData", checkoutData.toString());

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Payment setup completed");
            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Something went wrong during payment setup");
        }
    }
}