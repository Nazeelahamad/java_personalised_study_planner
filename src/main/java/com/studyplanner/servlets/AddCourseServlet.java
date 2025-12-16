// ============================================================
// FILE: com/studyplanner/servlets/AddCourseServlet.java
// PACKAGE: com.studyplanner.servlets
// ============================================================

package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/addCourse")
public class AddCourseServlet extends HttpServlet {
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
        String code = request.getParameter("courseCode");
        String name = request.getParameter("courseName");
        String creditsStr = request.getParameter("credits");
        String semester = request.getParameter("semester");
        String instructor = request.getParameter("instructor");
        String scheduleDays = request.getParameter("scheduleDays");
        
        // Validate inputs
        if (name == null || name.trim().isEmpty() || creditsStr == null || creditsStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Course name and credits are required");
            request.getRequestDispatcher("addCourse.jsp").forward(request, response);
            return;
        }
        
        try {
            int credits = Integer.parseInt(creditsStr);
            int courseId = DatabaseManager.addCourse(userId, code, name, credits, semester, instructor, scheduleDays);
            
            if (courseId > 0) {
                request.setAttribute("successMessage", "Course added successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to add course");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid credits value");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        request.getRequestDispatcher("addCourse.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        request.getRequestDispatcher("addCourse.jsp").forward(request, response);
    }
}
