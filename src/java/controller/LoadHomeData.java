package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
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

/**
 *
 * @author VICTUS
 */
@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("ok");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria c1 = s.createCriteria(Stock.class);
        c1.add(Restrictions.gt("qty", 0));
        c1.add(Restrictions.eq("size.id", 1));
        c1.addOrder(Order.desc("created_at"));
        c1.setFirstResult(0);
        c1.setMaxResults(8);

        List<Stock> productList = c1.list();
        
        Gson gson = new Gson();
        
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", Boolean.TRUE);
        
        s.close();
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
