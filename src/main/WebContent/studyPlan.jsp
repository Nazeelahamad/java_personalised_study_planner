<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Integer planId = (Integer) request.getAttribute("planId");
    List<Map<String, Object>> tasks = (List<Map<String, Object>>) request.getAttribute("tasks");
    Integer totalHours = (Integer) request.getAttribute("totalHours");
    String strategy = (String) request.getAttribute("strategy");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    Date today = new Date();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Study Plan - Study Planner</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; margin: 0; }
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
        .timeline-item .task { font-weight: 600; color: #333; margin-bottom: 5px; }
        .timeline-item .details { font-size: 0.9em; color: #666; }
        .timeline-item .priority { display: inline-block; padding: 4px 10px; border-radius: 12px; font-size: 0.8em; margin-top: 5px; }
        .priority-high { background: #fab1a0; color: #c0392b; }
        .priority-medium { background: #ffeaa7; color: #d97706; }
        .priority-low { background: #dfe6e9; color: #636e72; }
        .badge-exam { background: #a29bfe; color: #6c5ce7; padding: 3px 8px; border-radius: 10px; font-size: 0.85em; }
        .badge-assignment { background: #74b9ff; color: #0984e3; padding: 3px 8px; border-radius: 10px; font-size: 0.85em; }
        a { color: #667eea; text-decoration: none; margin-top: 20px; display: inline-block; }
        .empty-state { padding: 60px 20px; text-align: center; color: #999; }
        .empty-state h3 { margin-bottom: 10px; }
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

        <% if (planId != null && planId > 0 && tasks != null && !tasks.isEmpty()) { 
            // Calculate study hours per day based on strategy
            int hoursPerDay = 3; // balanced
            if ("intensive".equals(strategy)) hoursPerDay = 6;
            else if ("light".equals(strategy)) hoursPerDay = 2;
            
            int daysToComplete = (int) Math.ceil((double) totalHours / hoursPerDay);
        %>
            <p style="margin: 20px 0; color: #666;">
                Your personalized study plan has been generated! The tasks are organized by priority 
                (weightage and deadline) and spread across <%= daysToComplete %> days based on your 
                <strong><%= strategy %></strong> study strategy (<%= hoursPerDay %> hrs/day).
            </p>

            <div class="plan-stats">
                <div class="stat-box">
                    <h3>Total Study Hours</h3>
                    <div class="value"><%= totalHours %></div>
                </div>
                <div class="stat-box">
                    <h3>Total Tasks</h3>
                    <div class="value"><%= tasks.size() %></div>
                </div>
                <div class="stat-box">
                    <h3>Days to Complete</h3>
                    <div class="value"><%= daysToComplete %></div>
                </div>
            </div>

            <div class="timeline">
                <h3>Your Schedule (Priority Ordered)</h3>
                <% 
                    for (int i = 0; i < tasks.size(); i++) {
                        Map<String, Object> task = tasks.get(i);
                        String taskType = (String) task.get("type");
                        String title = (String) task.get("title");
                        String courseCode = (String) task.get("courseCode");
                        String courseName = (String) task.get("courseName");
                        Date dueDate = (Date) task.get("dueDate");
                        int hours = (int) task.get("hours");
                        int weightage = (int) task.get("weightage");
                        
                        // Calculate days until due
                        long diffInMillies = dueDate.getTime() - today.getTime();
                        long daysUntilDue = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        
                        // Determine priority level
                        String priorityClass = "priority-low";
                        String priorityLabel = "Low Priority";
                        if (weightage >= 30 || daysUntilDue <= 3) {
                            priorityClass = "priority-high";
                            priorityLabel = "High Priority";
                        } else if (weightage >= 20 || daysUntilDue <= 7) {
                            priorityClass = "priority-medium";
                            priorityLabel = "Medium Priority";
                        }
                        
                        String badgeClass = taskType.equals("Exam") ? "badge-exam" : "badge-assignment";
                %>
                    <div class="timeline-item">
                        <div class="date">
                            📍 Due: <%= dateFormat.format(dueDate) %> 
                            (<%= daysUntilDue > 0 ? daysUntilDue + " days left" : "OVERDUE!" %>)
                        </div>
                        <div class="task">
                            <span class="<%= badgeClass %>"><%= taskType %></span>
                            <%= title %>
                        </div>
                        <div class="details">
                            <%= courseCode %> - <%= courseName %> | 
                            <%= hours %> hours | 
                            Weightage: <%= weightage %>%
                        </div>
                        <div class="priority <%= priorityClass %>"><%= priorityLabel %></div>
                    </div>
                <% } %>
            </div>

            <div style="margin-top: 30px; padding: 20px; background: #f0f7ff; border-radius: 8px; border-left: 4px solid #667eea;">
                <h4 style="color: #667eea;">💡 Smart Scheduling Tips:</h4>
                <ul style="margin-left: 20px; color: #666; line-height: 1.8;">
                    <li>Exams and high-weightage tasks are automatically prioritized</li>
                    <li>Tasks closer to deadline appear at the top</li>
                    <li>Spread your <%= hoursPerDay %> hours/day across multiple tasks</li>
                    <li>Start with high-priority items first each day</li>
                    <li>Take breaks between study sessions for better retention</li>
                </ul>
            </div>
        <% } else { %>
            <div class="empty-state">
                <h3>📋 No Study Plan Generated Yet</h3>
                <p>Add some assignments and exams, then generate your personalized study plan from the dashboard.</p>
            </div>
        <% } %>

        <a href="dashboard">← Back to Dashboard</a>
    </div>
</body>
</html>
