package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/completedTasks")
public class CompletedTasksServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            
            DatabaseManager dbManager = new DatabaseManager();
            Connection conn = dbManager.getConnection();
            
            // Get completed assignments
            String assignmentQuery = "SELECT a.*, c.course_name FROM assignments a " +
                                    "JOIN courses c ON a.course_id = c.id " +
                                    "WHERE c.user_id = ? AND a.submission_status = 'COMPLETED' " +
                                    "ORDER BY a.completed_date DESC";
            PreparedStatement ps1 = conn.prepareStatement(assignmentQuery);
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            
            List<Map<String, Object>> completedAssignments = new ArrayList<>();
            while (rs1.next()) {
                Map<String, Object> assignment = new HashMap<>();
                assignment.put("id", rs1.getInt("id"));
                assignment.put("title", rs1.getString("assignment_title"));
                assignment.put("courseName", rs1.getString("course_name"));
                assignment.put("completedDate", rs1.getTimestamp("completed_date"));
                assignment.put("weightage", rs1.getInt("weightage"));
                completedAssignments.add(assignment);
            }
            
            // Get completed exams
            String examQuery = "SELECT e.*, c.course_name FROM exams e " +
                              "JOIN courses c ON e.course_id = c.id " +
                              "WHERE c.user_id = ? AND e.completed = TRUE " +
                              "ORDER BY e.exam_date DESC";
            PreparedStatement ps2 = conn.prepareStatement(examQuery);
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            
            List<Map<String, Object>> completedExams = new ArrayList<>();
            while (rs2.next()) {
                Map<String, Object> exam = new HashMap<>();
                exam.put("id", rs2.getInt("id"));
                exam.put("name", rs2.getString("exam_name"));
                exam.put("courseName", rs2.getString("course_name"));
                exam.put("examDate", rs2.getTimestamp("exam_date"));
                exam.put("type", rs2.getString("exam_type"));
                exam.put("weightage", rs2.getInt("weightage"));
                completedExams.add(exam);
            }
            
            request.setAttribute("completedAssignments", completedAssignments);
            request.setAttribute("completedExams", completedExams);
            
            ps1.close();
            ps2.close();
            conn.close();
            
            request.getRequestDispatcher("completedTasks.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading completed tasks: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}