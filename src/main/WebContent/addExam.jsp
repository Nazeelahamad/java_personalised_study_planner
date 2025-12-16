<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    List<Map<String, Object>> courses = (List<Map<String, Object>>) request.getAttribute("courses");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Exam - Study Planner</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        
        header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 40px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .container {
            max-width: 600px;
            margin: 40px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h2 { color: #667eea; margin-bottom: 20px; }
        
        .alert-error {
            background: #fee;
            color: #c33;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #fcc;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
        }
        
        input, select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            font-family: inherit;
        }
        
        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        
        .btn {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #f0f0f0;
            color: #667eea;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        
        .note {
            font-size: 0.9em;
            color: #999;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <header>
        <h1>📚 Study Planner - Add Exam</h1>
    </header>
    
    <div class="container">
        <h2>Add New Exam</h2>
        
        <% if (errorMessage != null) { %>
            <div class="alert-error"><%= errorMessage %></div>
        <% } %>
        
        <form method="POST" action="addExam">
            <div class="form-group">
                <label for="courseId">Select Course *</label>
                <select id="courseId" name="courseId" required>
                    <option value="">-- Choose a course --</option>
                    <% if (courses != null) {
                        for (Map<String, Object> course : courses) { %>
                            <option value="<%= course.get("id") %>">
                                <%= course.get("code") %> - <%= course.get("name") %>
                            </option>
                        <% }
                    } %>
                </select>
            </div>
            
            <div class="form-group">
                <label for="examName">Exam Name *</label>
                <input type="text" id="examName" name="examName" 
                       placeholder="e.g., Midterm Examination" required>
            </div>
            
            <div class="form-group">
                <label for="examDate">Exam Date & Time *</label>
                <input type="datetime-local" id="examDate" name="examDate" required>
            </div>
            
            <div class="form-group">
                <label for="examType">Exam Type *</label>
                <select id="examType" name="examType" required>
                    <option value="">-- Select type --</option>
                    <option value="Midterm">Midterm</option>
                    <option value="Final">Final</option>
                    <option value="Quiz">Quiz</option>
                    <option value="Practical">Practical</option>
                    <option value="Viva">Viva</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="weightage">Weightage (%) *</label>
                <input type="number" id="weightage" name="weightage" 
                       min="1" max="100" placeholder="e.g., 30" required>
                <div class="note">Percentage contribution to final grade</div>
            </div>
            
            <div class="form-group">
                <label for="estimatedStudyHours">Estimated Study Hours *</label>
                <input type="number" id="estimatedStudyHours" name="estimatedStudyHours" 
                       min="1" max="200" placeholder="e.g., 20" required>
                <div class="note">How many hours you need to prepare for this exam</div>
            </div>
            
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Add Exam</button>
                <a href="dashboard" class="btn btn-secondary" 
                   style="text-align: center; text-decoration: none; line-height: 1.5;">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>
