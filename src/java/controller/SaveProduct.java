package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Session;

@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String categoryId = request.getParameter("categoryId");
        String colorId = request.getParameter("colorId");
        String productName = request.getParameter("productName");
        String productDescription = request.getParameter("productDescription");

        System.out.println(categoryId);

        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");
        Part part4 = request.getPart("image4");
        Part part5 = request.getPart("image5");
        Part part6 = request.getPart("image6");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Session s = HibernateUtil.getSessionFactory().openSession();

         if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("message", "Please Loging First");
        } else if (!Util.isInteger(categoryId)) {
            responseObject.addProperty("message", "Invalid Category!");
        } else if (Integer.parseInt(categoryId) == 0) {
            responseObject.addProperty("message", "Please Select a Category");
        } else if (!Util.isInteger(colorId)) {
            responseObject.addProperty("message", "Invalid Color!");
        } else if (Integer.parseInt(colorId) == 0) {
            responseObject.addProperty("message", "Please Select a Color!");
        } else if (productName.isEmpty()) {
            responseObject.addProperty("message", "Please Enter the product Name");
        } else if (productName.length() >= 100) {
            responseObject.addProperty("message", "Product Name is too long");
        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image is required");
        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Additional Product image1 is required");
        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Additional Product image2 is required");
        } else if (part4.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Additional Product image3 is required");
        } else if (part5.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Additional Product image4 is required");
        } else if (part6.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Additional Product image5 is required");
        } else if (productDescription.isEmpty()) {
            responseObject.addProperty("message", "Please Enter the product Description");
        } else {
            Category category = (Category) s.get(Category.class, Integer.valueOf(categoryId));

            if (category == null) {
                responseObject.addProperty("message", "Please select a valid Category!");
            } else {
                Color color = (Color) s.load(Color.class, Integer.parseInt(colorId));
                if (color == null) {
                    responseObject.addProperty("message", "Please select a valid Color!");
                } else {
                    Product product = new Product();
                    product.setName(productName);
                    product.setDescription(productDescription);
                    product.setCategory(category);
                    product.setColor(color);
                    product.setCreated_at(new Date());

                    int pid = (int) s.save(product);
                    s.beginTransaction().commit();
                    s.close();

                    String appPath = getServletContext().getRealPath("");
//                    System.out.println(appPath);
                    //C:\Users\VICTUS\Documents\NetBeansProjects\TeaStore\build\web

                    String newPath = appPath.replace("build\\web", "web\\product-images");

                    File productImageFolder = new File(newPath, String.valueOf(pid));
                    productImageFolder.mkdir();

                    File file1 = new File(productImageFolder, "image1.png");
                    Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file2 = new File(productImageFolder, "image2.png");
                    Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file3 = new File(productImageFolder, "image3.png");
                    Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file4 = new File(productImageFolder, "image4.png");
                    Files.copy(part4.getInputStream(), file4.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file5 = new File(productImageFolder, "image5.png");
                    Files.copy(part5.getInputStream(), file5.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file6 = new File(productImageFolder, "image6.png");
                    Files.copy(part6.getInputStream(), file6.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    responseObject.addProperty("status", true);
                }
            }

        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
