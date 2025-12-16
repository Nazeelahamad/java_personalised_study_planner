<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Course - Study Planner</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .container { max-width: 600px; margin: 50px auto; padding: 30px; background: white; 
                    border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #667eea; margin-bottom: 30px; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; font-weight: 600; }
        input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; 
                                border-radius: 5px; font-size: 1em; }
        button { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; 
                padding: 12px 30px; border: none; border-radius: 5px; cursor: pointer; 
                font-size: 1em; }
        button:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102,126,234,0.4); }
        .alert { padding: 12px; border-radius: 5px; margin-bottom: 20px; }
        .alert-error { background: #fee; color: #c33; border: 1px solid #fcc; }
        .alert-success { background: #efe; color: #3c3; border: 1px solid #cfc; }
        a { color: #667eea; text-decoration: none; margin-top: 20px; display: inline-block; }
    </style>
</head>
<body>
    <div class="container">
        <h1>➕ Add New Course</h1>
        
        <% String error = (String) request.getAttribute("errorMessage");
           String success = (String) request.getAttribute("successMessage"); %>
        
        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>
        
        <% if (success != null) { %>
            <div class="alert alert-success"><%= success %></div>
        <% } %>

        <form method="POST" action="addCourse">
            <div class="form-group">
                <label for="courseCode">Course Code *</label>
                <input type="text" id="courseCode" name="courseCode" placeholder="e.g., CS101" required>
            </div>

            <div class="form-group">
                <label for="courseName">Course Name *</label>
                <input type="text" id="courseName" name="courseName" placeholder="e.g., Data Structures" required>
            </div>

            <div class="form-group">
                <label for="credits">Credits *</label>
                <input type="number" id="credits" name="credits" min="1" max="6" placeholder="e.g., 4" required>
            </div>

            <div class="form-group">
                <label for="semester">Semester</label>
                <input type="text" id="semester" name="semester" placeholder="e.g., Spring 2024">
            </div>

            <div class="form-group">
                <label for="instructor">Instructor</label>
                <input type="text" id="instructor" name="instructor" placeholder="Professor's name">
            </div>

            <div class="form-group">
                <label for="scheduleDays">Schedule Days</label>
                <input type="text" id="scheduleDays" name="scheduleDays" placeholder="e.g., MWF, TTh">
            </div>

            <button type="submit">Add Course</button>
            <a href="dashboard">← Back to Dashboard</a>
        </form>
    </div>
</body>
</html>
