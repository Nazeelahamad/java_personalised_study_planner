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
        
        int userId = (Integer) session.getAttribute("userId");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // Get user data
            Map<String, Object> user = DatabaseManager.getUserById(userId);
            
            // Get courses
            List<Map<String, Object>> courses = DatabaseManager.getCoursesByUserId(userId);
            
            // Get pending assignments
            List<Map<String, Object>> assignments = DatabaseManager.getPendingAssignments(userId);
            
            // Get upcoming exams
            List<Map<String, Object>> exams = DatabaseManager.getUpcomingExams(userId);
            
            // Set attributes for JSP
            request.setAttribute("user", user);
            request.setAttribute("courses", courses);
            request.setAttribute("assignments", assignments);
            request.setAttribute("exams", exams);
            request.setAttribute("totalCourses", courses.size());
            request.setAttribute("pendingAssignments", assignments.size());
            request.setAttribute("upcomingExams", exams.size());
            
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        }
    }
}
