package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            
            // Check for success/error messages from session
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }
            
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
            
            // Rest of your existing code...
            List<Map<String, Object>> courses = DatabaseManager.getCoursesByUserId(userId);
            List<Map<String, Object>> assignments = DatabaseManager.getPendingAssignments(userId);
            List<Map<String, Object>> exams = DatabaseManager.getUpcomingExams(userId);
            
            request.setAttribute("courses", courses);
            request.setAttribute("assignments", assignments);
            request.setAttribute("exams", exams);
            
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

}
