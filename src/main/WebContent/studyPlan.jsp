<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Integer planId = (Integer) request.getAttribute("planId");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Study Plan - Study Planner</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .container { max-width: 900px; margin: 30px auto; padding: 30px; background: white; 
                    border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #667eea; margin-bottom: 10px; }
        .alert { padding: 15px; border-radius: 5px; margin-bottom: 20px; font-weight: 600; }
        .alert-success { background: #efe; color: #3c3; border: 1px solid #cfc; }
        .alert-error { background: #fee; color: #c33; border: 1px solid #fcc; }
        .plan-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin: 30px 0; }
        .stat-box { background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #667eea; }
        .stat-box h3 { color: #999; font-size: 0.9em; margin-bottom: 10px; }
        .stat-box .value { font-size: 2em; font-weight: bold; color: #667eea; }
        .timeline { margin-top: 30px; }
        .timeline-item { padding: 20px; border-left: 3px solid #667eea; margin-bottom: 15px; background: #f9f9f9; border-radius: 5px; }
        .timeline-item .date { color: #999; font-size: 0.9em; margin-bottom: 8px; }
        .timeline-item .task { font-weight: 600; color: #333; }
        .timeline-item .priority { display: inline-block; padding: 4px 10px; border-radius: 12px; font-size: 0.8em; margin-top: 5px; }
        .priority-high { background: #fab1a0; color: #c0392b; }
        .priority-medium { background: #ffeaa7; color: #d97706; }
        a { color: #667eea; text-decoration: none; margin-top: 20px; display: inline-block; }
    </style>
</head>
<body>
    <div class="container">
        <h1>📅 Your Study Plan</h1>
        
        <% if (successMessage != null) { %>
            <div class="alert alert-success"><%= successMessage %></div>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <% } %>

        <% if (planId != null && planId > 0) { %>
            <p style="margin: 20px 0; color: #666;">
                Your personalized study plan has been generated! The tasks are organized by priority 
                and spread across days based on your selected study strategy.
            </p>

            <div class="plan-stats">
                <div class="stat-box">
                    <h3>Total Study Hours</h3>
                    <div class="value">45</div>
                </div>
                <div class="stat-box">
                    <h3>Tasks</h3>
                    <div class="value">12</div>
                </div>
                <div class="stat-box">
                    <h3>Days to Complete</h3>
                    <div class="value">30</div>
                </div>
            </div>

            <div class="timeline">
                <h3>Your Daily Schedule</h3>
                <div class="timeline-item">
                    <div class="date">📍 Today - Jan 20, 2024</div>
                    <div class="task">Binary Search Tree Implementation (3h) - CS101</div>
                    <div class="priority priority-high">High Priority</div>
                </div>
                <div class="timeline-item">
                    <div class="date">📍 Tomorrow - Jan 21, 2024</div>
                    <div class="task">Algorithm Analysis Assignment (2h) - CS102</div>
                    <div class="priority priority-high">High Priority</div>
                </div>
                <div class="timeline-item">
                    <div class="date">📍 Jan 22, 2024</div>
                    <div class="task">Discrete Mathematics Review (2h) - MATH101</div>
                    <div class="priority priority-medium">Medium Priority</div>
                </div>
            </div>

            <div style="margin-top: 30px; padding: 20px; background: #f0f7ff; border-radius: 8px; border-left: 4px solid #667eea;">
                <h4 style="color: #667eea;">💡 Smart Scheduling Tips:</h4>
                <ul style="margin-left: 20px; color: #666;">
                    <li>Exams are prioritized higher than assignments</li>
                    <li>Tasks are distributed based on days until deadline</li>
                    <li>Larger tasks are scheduled earlier to allow buffer time</li>
                    <li>Study hours per day match your selected strategy</li>
                </ul>
            </div>
        <% } else { %>
            <div style="padding: 40px; text-align: center; color: #999;">
                <p>No study plan generated yet.</p>
                <p>Go to dashboard and click "Generate Study Plan" to create your personalized schedule.</p>
            </div>
        <% } %>

        <a href="dashboard">← Back to Dashboard</a>
    </div>
</body>
</html>
