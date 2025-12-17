package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/addDemoData")
public class DemoDataServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            
            // Add sample courses
            int courseId1 = DatabaseManager.addCourse(userId, "CS101", "Data Structures", 
                4, "Fall 2024", "Dr. Smith", "MWF");
            int courseId2 = DatabaseManager.addCourse(userId, "CS102", "Algorithms", 
                4, "Fall 2024", "Prof. Johnson", "TTh");
            int courseId3 = DatabaseManager.addCourse(userId, "MATH201", "Calculus II", 
                3, "Fall 2024", "Dr. Williams", "MWF");
            
            // Add sample assignments
            Timestamp dueDate1 = new Timestamp(System.currentTimeMillis() + 86400000L * 7); // 7 days
            DatabaseManager.addAssignment(courseId1, "Binary Search Tree Implementation", 
                dueDate1, 25, 8, "Implement BST with insert, delete, search operations");
            
            Timestamp dueDate2 = new Timestamp(System.currentTimeMillis() + 86400000L * 3); // 3 days
            DatabaseManager.addAssignment(courseId2, "Sorting Algorithms Analysis", 
                dueDate2, 20, 6, "Compare time complexity of various sorting algorithms");
            
            // Add sample exams
            Timestamp examDate1 = new Timestamp(System.currentTimeMillis() + 86400000L * 14); // 14 days
            DatabaseManager.addExam(courseId1, "Midterm Examination", 
                examDate1, "Midterm", 30, 20);
            
            Timestamp examDate2 = new Timestamp(System.currentTimeMillis() + 86400000L * 21); // 21 days
            DatabaseManager.addExam(courseId3, "Calculus II Final", 
                examDate2, "Final", 40, 30);
            
            session.setAttribute("successMessage", "Demo data added successfully! You now have 3 courses, 2 assignments, and 2 exams.");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error adding demo data: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}