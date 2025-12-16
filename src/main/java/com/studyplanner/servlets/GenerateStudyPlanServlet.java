package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.studyplanner.dao.DatabaseManager;

@WebServlet("/generatePlan")
public class GenerateStudyPlanServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            String strategy = request.getParameter("strategy");
            
            // Get all tasks (assignments + exams)
            List<Map<String, Object>> tasks = DatabaseManager.getTasksForPlan(userId);
            
            if (tasks.isEmpty()) {
                request.setAttribute("errorMessage", "No pending assignments or exams found. Add some tasks first!");
                request.getRequestDispatcher("studyPlan.jsp").forward(request, response);
                return;
            }
            
            // Calculate total hours
            int totalHours = 0;
            for (Map<String, Object> task : tasks) {
                totalHours += (int) task.get("hours");
            }
            
            // Save study plan
            int planId = DatabaseManager.saveStudyPlan(userId, strategy, totalHours);
            
            // Sort tasks by priority (weightage and due date)
            tasks.sort((t1, t2) -> {
                int weight1 = (int) t1.get("weightage");
                int weight2 = (int) t2.get("weightage");
                if (weight1 != weight2) return Integer.compare(weight2, weight1);
                
                Date date1 = (Date) t1.get("dueDate");
                Date date2 = (Date) t2.get("dueDate");
                return date1.compareTo(date2);
            });
            
            // Pass data to JSP
            request.setAttribute("planId", planId);
            request.setAttribute("tasks", tasks);
            request.setAttribute("totalHours", totalHours);
            request.setAttribute("strategy", strategy);
            request.setAttribute("successMessage", "Study plan generated successfully!");
            
            request.getRequestDispatcher("studyPlan.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error generating study plan: " + e.getMessage());
            request.getRequestDispatcher("studyPlan.jsp").forward(request, response);
        }
    }
}
