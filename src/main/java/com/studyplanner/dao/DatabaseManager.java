package com.studyplanner.dao;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/study_planner_db";
	private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";  // CHANGE THIS TO YOUR MYSQL PASSWORD
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Get user by ID
    public static Map<String, Object> getUserById(int userId) throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "SELECT * FROM users WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        Map<String, Object> user = new HashMap<>();
        if (rs.next()) {
            user.put("id", rs.getInt("id"));
            user.put("username", rs.getString("username"));
            user.put("email", rs.getString("email"));
            user.put("fullName", rs.getString("full_name"));
            user.put("createdAt", rs.getTimestamp("created_at"));
        }
        
        ps.close();
        conn.close();
        return user;
    }

    // Get all courses for a user
    public static List<Map<String, Object>> getCoursesByUserId(int userId) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "SELECT * FROM courses WHERE user_id = ? ORDER BY semester DESC, created_at DESC";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        List<Map<String, Object>> courses = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> course = new HashMap<>();
            course.put("id", rs.getInt("id"));
            course.put("code", rs.getString("course_code"));
            course.put("name", rs.getString("course_name"));
            course.put("credits", rs.getInt("credits"));
            course.put("semester", rs.getString("semester"));
            course.put("instructor", rs.getString("instructor"));
            course.put("scheduleDays", rs.getString("schedule_days"));
            courses.add(course);
        }
        
        ps.close();
        conn.close();
        return courses;
    }

    // Get all pending assignments for a user
    public static List<Map<String, Object>> getPendingAssignments(int userId) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "SELECT a.*, c.course_name FROM assignments a " +
                      "JOIN courses c ON a.course_id = c.id " +
                      "WHERE c.user_id = ? AND a.submission_status = 'PENDING' " +
                      "ORDER BY a.due_date ASC";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        List<Map<String, Object>> assignments = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> assignment = new HashMap<>();
            assignment.put("id", rs.getInt("id"));
            assignment.put("title", rs.getString("assignment_title"));
            assignment.put("courseName", rs.getString("course_name"));
            assignment.put("dueDate", rs.getTimestamp("due_date"));
            assignment.put("weightage", rs.getInt("weightage"));
            assignment.put("estimatedHours", rs.getInt("estimated_hours"));
            assignments.add(assignment);
        }
        
        ps.close();
        conn.close();
        return assignments;
    }

    // Get all upcoming exams for a user
    public static List<Map<String, Object>> getUpcomingExams(int userId) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "SELECT e.*, c.course_name FROM exams e " +
                      "JOIN courses c ON e.course_id = c.id " +
                      "WHERE c.user_id = ? AND e.completed = FALSE " +
                      "ORDER BY e.exam_date ASC";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        List<Map<String, Object>> exams = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> exam = new HashMap<>();
            exam.put("id", rs.getInt("id"));
            exam.put("name", rs.getString("exam_name"));
            exam.put("courseName", rs.getString("course_name"));
            exam.put("examDate", rs.getTimestamp("exam_date"));
            exam.put("type", rs.getString("exam_type"));
            exam.put("weightage", rs.getInt("weightage"));
            exam.put("estimatedStudyHours", rs.getInt("estimated_study_hours"));
            exams.add(exam);
        }
        
        ps.close();
        conn.close();
        return exams;
    }

    // Add new course
    public static int addCourse(int userId, String code, String name, int credits, 
                               String semester, String instructor, String scheduleDays) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "INSERT INTO courses (user_id, course_code, course_name, credits, semester, instructor, schedule_days) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.setString(2, code);
        ps.setString(3, name);
        ps.setInt(4, credits);
        ps.setString(5, semester);
        ps.setString(6, instructor);
        ps.setString(7, scheduleDays);
        
        int result = ps.executeUpdate();
        int courseId = -1;
        
        if (result > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                courseId = generatedKeys.getInt(1);
            }
        }
        
        ps.close();
        conn.close();
        return courseId;
    }
}
