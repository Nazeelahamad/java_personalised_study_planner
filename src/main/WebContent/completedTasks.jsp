<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String fullName = (String) session.getAttribute("fullName");
    List<Map<String, Object>> completedAssignments = 
        (List<Map<String, Object>>) request.getAttribute("completedAssignments");
    List<Map<String, Object>> completedExams = 
        (List<Map<String, Object>>) request.getAttribute("completedExams");
    
    if (completedAssignments == null) completedAssignments = new ArrayList<>();
    if (completedExams == null) completedExams = new ArrayList<>();
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Completed Tasks - Study Planner</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; margin: 0; }
        header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white; padding: 20px 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .section { background: white; padding: 30px; border-radius: 10px; 
                   margin-bottom: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        h3 { color: #333; margin-bottom: 20px; border-bottom: 2px solid #f0f0f0; padding-bottom: 15px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        table th { background: #f5f5f5; padding: 12px; text-align: left; font-weight: 600; 
                   color: #666; border-bottom: 2px solid #e0e0e0; }
        table td { padding: 12px; border-bottom: 1px solid #f0f0f0; }
        table tr:hover { background: #f9f9f9; }
        .badge-completed { background: #d4edda; color: #155724; padding: 5px 12px; 
                          border-radius: 20px; font-size: 0.85em; font-weight: 600; }
        .empty-message { text-align: center; padding: 30px; color: #999; font-style: italic; }
        a { color: #667eea; text-decoration: none; display: inline-block; margin-top: 20px; }
        .stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 20px; border-radius: 10px; 
                    box-shadow: 0 2px 10px rgba(0,0,0,0.05); text-align: center; 
                    border-left: 4px solid #28a745; }
        .stat-card .number { font-size: 2.5em; font-weight: bold; color: #28a745; }
        .stat-card h4 { color: #999; font-size: 0.9em; margin-top: 10px; }
    </style>
</head>
<body>
    <header>
        <h1>✅ Completed Tasks</h1>
    </header>
    
    <div class="container">
        <div class="stats-grid">
            <div class="stat-card">
                <div class="number"><%= completedAssignments.size() %></div>
                <h4>Completed Assignments</h4>
            </div>
            <div class="stat-card">
                <div class="number"><%= completedExams.size() %></div>
                <h4>Completed Exams</h4>
            </div>
            <div class="stat-card">
                <div class="number"><%= completedAssignments.size() + completedExams.size() %></div>
                <h4>Total Completed</h4>
            </div>
        </div>

        <div class="section">
            <h3>Completed Assignments</h3>
            <% if (completedAssignments.isEmpty()) { %>
                <div class="empty-message">No completed assignments yet.</div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Assignment</th>
                            <th>Course</th>
                            <th>Completed On</th>
                            <th>Weightage</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> assignment : completedAssignments) { %>
                            <tr>
                                <td><strong><%= assignment.get("title") %></strong></td>
                                <td><%= assignment.get("courseName") %></td>
                                <td><%= dateFormat.format(assignment.get("completedDate")) %></td>
                                <td><span class="badge-completed"><%= assignment.get("weightage") %>%</span></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <div class="section">
            <h3>Completed Exams</h3>
            <% if (completedExams.isEmpty()) { %>
                <div class="empty-message">No completed exams yet.</div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Exam Name</th>
                            <th>Course</th>
                            <th>Exam Date</th>
                            <th>Type</th>
                            <th>Weightage</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> exam : completedExams) { %>
                            <tr>
                                <td><strong><%= exam.get("name") %></strong></td>
                                <td><%= exam.get("courseName") %></td>
                                <td><%= dateFormat.format(exam.get("examDate")) %></td>
                                <td><span class="badge-completed"><%= exam.get("type") %></span></td>
                                <td><%= exam.get("weightage") %>%</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <a href="dashboard">← Back to Dashboard</a>
    </div>
</body>
</html>