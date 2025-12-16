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

@WebServlet("/addExam")
public class AddExamServlet extends HttpServlet {
    
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
            request.getRequestDispatcher("addExam.jsp").forward(request, response);
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
            String examName = request.getParameter("examName");
            String examDateStr = request.getParameter("examDate");
            String examType = request.getParameter("examType");
            int weightage = Integer.parseInt(request.getParameter("weightage"));
            int estimatedStudyHours = Integer.parseInt(request.getParameter("estimatedStudyHours"));
            
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Timestamp examDate = new Timestamp(sdf.parse(examDateStr).getTime());
            
            int examId = DatabaseManager.addExam(courseId, examName, examDate, 
                                                examType, weightage, estimatedStudyHours);
            
            if (examId > 0) {
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("errorMessage", "Failed to add exam");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error adding exam: " + e.getMessage());
            doGet(request, response);
        }
    }
}
