package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/deleteExam")
public class DeleteExamServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            int examId = Integer.parseInt(request.getParameter("examId"));
            
            boolean deleted = DatabaseManager.deleteExam(examId, userId);
            
            if (deleted) {
                session.setAttribute("successMessage", "Exam deleted successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to delete exam. Please try again.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error deleting exam: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}
