package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import com.studyplanner.utils.SchedulingAlgorithm;

@WebServlet("/generatePlan")
public class GenerateStudyPlanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        String strategy = request.getParameter("strategy");
        
        if (strategy == null || strategy.trim().isEmpty()) {
            strategy = "balanced";
        }
        
        try {
            int planId = SchedulingAlgorithm.generateStudyPlan(userId, strategy);
            
            if (planId > 0) {
                request.setAttribute("successMessage", "Study plan generated successfully!");
                request.setAttribute("planId", planId);
            } else {
                request.setAttribute("errorMessage", "Failed to generate study plan");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }
        
        request.getRequestDispatcher("studyPlan.jsp").forward(request, response);
    }
}



	