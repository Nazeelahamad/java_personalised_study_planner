// File: com/studyplanner/servlets/LoginServlet.java
package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import com.studyplanner.dao.DatabaseManager;
import com.studyplanner.utils.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String action = request.getParameter("action");
        
        response.setContentType("text/html;charset=UTF-8");
        
        // Input validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        if ("register".equals(action)) {
            handleRegistration(request, response, username, password);
        } else {
            handleLogin(request, response, username, password);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, 
                            String username, String password) throws ServletException, IOException {
        DatabaseManager dbManager = new DatabaseManager();
        
        try {
            Connection conn = dbManager.getConnection();
            String query = "SELECT id, full_name, password_hash FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("username", username);
                    session.setAttribute("fullName", rs.getString("full_name"));
                    session.setMaxInactiveInterval(3600); // 1 hour
                    
                    // Log the session
                    logSession(conn, rs.getInt("id"), request.getRemoteAddr());
                    
                    response.sendRedirect("dashboard");
                } else {
                    request.setAttribute("errorMessage", "Invalid password");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Username not found");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response, 
                                   String username, String password) throws ServletException, IOException {
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        
        if (email == null || email.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required for registration");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        DatabaseManager dbManager = new DatabaseManager();
        
        try {
            Connection conn = dbManager.getConnection();
            
            // Check if user already exists
            String checkQuery = "SELECT id FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setString(1, username);
            checkPs.setString(2, email);
            ResultSet checkRs = checkPs.executeQuery();
            
            if (checkRs.next()) {
                request.setAttribute("errorMessage", "Username or email already exists");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }
            
            // Register new user
            String hashedPassword = PasswordUtil.hashPassword(password);
            String insertQuery = "INSERT INTO users (username, email, password_hash, full_name) VALUES (?, ?, ?, ?)";
            PreparedStatement insertPs = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertPs.setString(1, username);
            insertPs.setString(2, email);
            insertPs.setString(3, hashedPassword);
            insertPs.setString(4, fullName);
            
            int rowsAffected = insertPs.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = insertPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    
                    // Auto-login after registration
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", userId);
                    session.setAttribute("username", username);
                    session.setAttribute("fullName", fullName);
                    session.setMaxInactiveInterval(3600);
                    
                    logSession(conn, userId, request.getRemoteAddr());
                    
                    response.sendRedirect("dashboard");
                }
            }
            
            insertPs.close();
            checkPs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void logSession(Connection conn, int userId, String ipAddress) {
        try {
            String query = "INSERT INTO session_logs (user_id, ip_address) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, ipAddress);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
