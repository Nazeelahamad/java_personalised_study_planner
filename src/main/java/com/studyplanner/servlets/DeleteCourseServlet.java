package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/deleteCourse")
public class DeleteCourseServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            
            boolean deleted = DatabaseManager.deleteCourse(courseId, userId);
            
            if (deleted) {
                session.setAttribute("successMessage", "Course deleted successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to delete course. Please try again.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error deleting course: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}
