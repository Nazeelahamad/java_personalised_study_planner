package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import com.studyplanner.dao.DatabaseManager;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/addAssignment")
public class AddAssignmentServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            List<Map<String, Object>> courses = DatabaseManager.getCoursesByUserId(userId);
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("addAssignment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading courses: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            String title = request.getParameter("title");
            String dueDateStr = request.getParameter("dueDate");
            int weightage = Integer.parseInt(request.getParameter("weightage"));
            int estimatedHours = Integer.parseInt(request.getParameter("estimatedHours"));
            String description = request.getParameter("description");
            
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Timestamp dueDate = new Timestamp(sdf.parse(dueDateStr).getTime());
            
            int assignmentId = DatabaseManager.addAssignment(courseId, title, dueDate, 
                                                            weightage, estimatedHours, description);
            
            if (assignmentId > 0) {
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("errorMessage", "Failed to add assignment");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error adding assignment: " + e.getMessage());
            doGet(request, response);
        }
    }
}
