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
    // Add new assignment
    public static int addAssignment(int courseId, String title, Timestamp dueDate, 
                                   int weightage, int estimatedHours, String description) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "INSERT INTO assignments (course_id, assignment_title, due_date, " +
                      "weightage, estimated_hours, description, submission_status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, courseId);
        ps.setString(2, title);
        ps.setTimestamp(3, dueDate);
        ps.setInt(4, weightage);
        ps.setInt(5, estimatedHours);
        ps.setString(6, description);
        
        int result = ps.executeUpdate();
        int assignmentId = -1;
        
        if (result > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                assignmentId = generatedKeys.getInt(1);
            }
        }
        
        ps.close();
        conn.close();
        return assignmentId;
    }

    // Add new exam
    public static int addExam(int courseId, String examName, Timestamp examDate, 
                             String examType, int weightage, int estimatedStudyHours) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "INSERT INTO exams (course_id, exam_name, exam_date, exam_type, " +
                      "weightage, estimated_study_hours, completed) " +
                      "VALUES (?, ?, ?, ?, ?, ?, FALSE)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, courseId);
        ps.setString(2, examName);
        ps.setTimestamp(3, examDate);
        ps.setString(4, examType);
        ps.setInt(5, weightage);
        ps.setInt(6, estimatedStudyHours);
        
        int result = ps.executeUpdate();
        int examId = -1;
        
        if (result > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                examId = generatedKeys.getInt(1);
            }
        }
        
        ps.close();
        conn.close();
        return examId;
    }

    // Get tasks for study plan generation
    public static List<Map<String, Object>> getTasksForPlan(int userId) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        // Get assignments
        String assignmentQuery = "SELECT a.*, c.course_code, c.course_name " +
                                "FROM assignments a JOIN courses c ON a.course_id = c.id " +
                                "WHERE c.user_id = ? AND a.submission_status = 'PENDING' " +
                                "ORDER BY a.due_date ASC";
        PreparedStatement ps1 = conn.prepareStatement(assignmentQuery);
        ps1.setInt(1, userId);
        ResultSet rs1 = ps1.executeQuery();
        
        while (rs1.next()) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "Assignment");
            task.put("title", rs1.getString("assignment_title"));
            task.put("courseCode", rs1.getString("course_code"));
            task.put("courseName", rs1.getString("course_name"));
            task.put("dueDate", rs1.getTimestamp("due_date"));
            task.put("hours", rs1.getInt("estimated_hours"));
            task.put("weightage", rs1.getInt("weightage"));
            tasks.add(task);
        }
        
        // Get exams
        String examQuery = "SELECT e.*, c.course_code, c.course_name " +
                          "FROM exams e JOIN courses c ON e.course_id = c.id " +
                          "WHERE c.user_id = ? AND e.completed = FALSE " +
                          "ORDER BY e.exam_date ASC";
        PreparedStatement ps2 = conn.prepareStatement(examQuery);
        ps2.setInt(1, userId);
        ResultSet rs2 = ps2.executeQuery();
        
        while (rs2.next()) {
            Map<String, Object> task = new HashMap<>();
            task.put("type", "Exam");
            task.put("title", rs2.getString("exam_name") + " - " + rs2.getString("exam_type"));
            task.put("courseCode", rs2.getString("course_code"));
            task.put("courseName", rs2.getString("course_name"));
            task.put("dueDate", rs2.getTimestamp("exam_date"));
            task.put("hours", rs2.getInt("estimated_study_hours"));
            task.put("weightage", rs2.getInt("weightage"));
            tasks.add(task);
        }
        
        ps1.close();
        ps2.close();
        conn.close();
        
        return tasks;
    }

    // Save study plan
    // Save study plan
    public static int saveStudyPlan(int userId, String strategy, int totalHours) 
            throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        String query = "INSERT INTO study_plans (user_id, strategy, total_hours) " +
                      "VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.setString(2, strategy);
        ps.setInt(3, totalHours);
        
        int result = ps.executeUpdate();
        int planId = -1;
        
        if (result > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                planId = generatedKeys.getInt(1);
            }
        }
        
        ps.close();
        conn.close();
        return planId;
    }

    // Delete course by ID
    public static boolean deleteCourse(int courseId, int userId) throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        // Verify the course belongs to the user
        String verifyQuery = "SELECT user_id FROM courses WHERE id = ?";
        PreparedStatement verifyPs = conn.prepareStatement(verifyQuery);
        verifyPs.setInt(1, courseId);
        ResultSet rs = verifyPs.executeQuery();
        
        if (!rs.next() || rs.getInt("user_id") != userId) {
            verifyPs.close();
            conn.close();
            return false;
        }
        
        String query = "DELETE FROM courses WHERE id = ? AND user_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, courseId);
        ps.setInt(2, userId);
        
        int result = ps.executeUpdate();
        
        ps.close();
        verifyPs.close();
        conn.close();
        return result > 0;
    }

    // Delete assignment by ID
    public static boolean deleteAssignment(int assignmentId, int userId) throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        // Verify the assignment belongs to the user's course
        String verifyQuery = "SELECT c.user_id FROM assignments a " +
                            "JOIN courses c ON a.course_id = c.id " +
                            "WHERE a.id = ?";
        PreparedStatement verifyPs = conn.prepareStatement(verifyQuery);
        verifyPs.setInt(1, assignmentId);
        ResultSet rs = verifyPs.executeQuery();
        
        if (!rs.next() || rs.getInt("user_id") != userId) {
            verifyPs.close();
            conn.close();
            return false;
        }
        
        String query = "DELETE FROM assignments WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assignmentId);
        
        int result = ps.executeUpdate();
        
        ps.close();
        verifyPs.close();
        conn.close();
        return result > 0;
    }

    // Delete exam by ID
    public static boolean deleteExam(int examId, int userId) throws SQLException {
        DatabaseManager manager = new DatabaseManager();
        Connection conn = manager.getConnection();
        
        // Verify the exam belongs to the user's course
        String verifyQuery = "SELECT c.user_id FROM exams e " +
                            "JOIN courses c ON e.course_id = c.id " +
                            "WHERE e.id = ?";
        PreparedStatement verifyPs = conn.prepareStatement(verifyQuery);
        verifyPs.setInt(1, examId);
        ResultSet rs = verifyPs.executeQuery();
        
        if (!rs.next() || rs.getInt("user_id") != userId) {
            verifyPs.close();
            conn.close();
            return false;
        }
        
        String query = "DELETE FROM exams WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, examId);
        
        int result = ps.executeUpdate();
        
        ps.close();
        verifyPs.close();
        conn.close();
        return result > 0;
    }

}
