package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/deleteAssignment")
public class DeleteAssignmentServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            
            boolean deleted = DatabaseManager.deleteAssignment(assignmentId, userId);
            
            if (deleted) {
                session.setAttribute("successMessage", "Assignment deleted successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to delete assignment. Please try again.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error deleting assignment: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}
