/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "CheckSessionCart", urlPatterns = {"/CheckSessionCart"})
public class CheckSessionCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            ArrayList<Cart> sessionCartList = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");

            if (sessionCartList != null && !sessionCartList.isEmpty()) {
                Session session = null;
                Transaction transaction = null;

                try {
                    session = HibernateUtil.getSessionFactory().openSession();
                    transaction = session.beginTransaction(); // Start transaction ONCE

                    for (Cart sessionCart : sessionCartList) {
                        // Create NEW criteria for each iteration (this was your main bug)
                        Criteria criteria = session.createCriteria(Cart.class);
                        criteria.add(Restrictions.eq("user", user));
                        criteria.add(Restrictions.eq("stock", sessionCart.getStock()));

                        // Validate stock exists
                        Stock stock = (Stock) session.get(Stock.class, sessionCart.getStock().getId());
                        if (stock == null) {
                            System.err.println("Stock not found for ID: " + sessionCart.getStock().getId());
                            continue; // Skip this item
                        }

                        Cart existingCart = (Cart) criteria.uniqueResult();

                        if (existingCart == null) {
                            // No existing cart item - add new one
                            if (sessionCart.getQty() <= stock.getQty()) {
                                sessionCart.setUser(user);
                                session.save(sessionCart);
                                System.out.println("Added new cart item: " + sessionCart.getQty() + " units");
                            } else {
                                System.err.println("Insufficient stock. Required: "
                                        + sessionCart.getQty() + ", Available: " + stock.getQty());
                            }
                        } else {
                            // Existing cart item found - merge quantities
                            int newQty = sessionCart.getQty() + existingCart.getQty();
                            System.out.println("Merging quantities: " + sessionCart.getQty()
                                    + " + " + existingCart.getQty() + " = " + newQty);

                            if (newQty <= stock.getQty()) {
                                existingCart.setQty(newQty);
                                session.update(existingCart);
                                System.out.println("Updated cart item quantity to: " + newQty);
                            } else {
                                System.err.println("Cannot merge - insufficient stock. Required: "
                                        + newQty + ", Available: " + stock.getQty());
                                // Option: Set to maximum available
                                // existingCart.setQty(stock.getQty());
                                // session.update(existingCart);
                            }
                        }
                    }

                    // Commit ALL changes at once (outside the loop)
                    transaction.commit();
                    System.out.println("All cart operations committed successfully");

                    // Clear the session cart after successful database update
                    request.getSession().removeAttribute("sessionCart");

                } catch (Exception e) {
                    // Rollback the transaction on any error
                    if (transaction != null && transaction.isActive()) {
                        try {
                            transaction.rollback();
                            System.err.println("Transaction rolled back due to error");
                        } catch (Exception rollbackEx) {
                            System.err.println("Error during rollback: " + rollbackEx.getMessage());
                        }
                    }

                    System.err.println("Error processing session cart: " + e.getMessage());
                    e.printStackTrace();

                    // You might want to set an error attribute for the frontend
                    request.setAttribute("cartError", "Failed to update cart. Please try again.");

                } finally {
                    // Always close the session
                    if (session != null && session.isOpen()) {
                        session.close();
                    }
                }
            } else {
                System.out.println("No session cart found or cart is empty");
            }
        } else {
            System.out.println("User not logged in - cannot process session cart");
        }

        // Since this is a doGet method, you might want to redirect or forward
        // response.sendRedirect("cart.jsp");
        // or
        // request.getRequestDispatcher("cart.jsp").forward(request, response);
    }
}
