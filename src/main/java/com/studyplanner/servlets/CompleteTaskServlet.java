package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/completeTask")
public class CompleteTaskServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            String taskType = request.getParameter("taskType");
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            
            DatabaseManager dbManager = new DatabaseManager();
            Connection conn = dbManager.getConnection();
            
            boolean updated = false;
            
            if ("assignment".equals(taskType)) {
                String query = "UPDATE assignments SET submission_status = 'COMPLETED', " +
                              "completed_date = NOW() WHERE id = ? AND course_id IN " +
                              "(SELECT id FROM courses WHERE user_id = ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, taskId);
                ps.setInt(2, userId);
                updated = ps.executeUpdate() > 0;
                ps.close();
            } else if ("exam".equals(taskType)) {
                String query = "UPDATE exams SET completed = TRUE WHERE id = ? AND " +
                              "course_id IN (SELECT id FROM courses WHERE user_id = ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, taskId);
                ps.setInt(2, userId);
                updated = ps.executeUpdate() > 0;
                ps.close();
            }
            
            conn.close();
            
            if (updated) {
                session.setAttribute("successMessage", 
                    taskType.substring(0, 1).toUpperCase() + taskType.substring(1) + 
                    " marked as completed! 🎉");
            } else {
                session.setAttribute("errorMessage", "Failed to update task status.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}