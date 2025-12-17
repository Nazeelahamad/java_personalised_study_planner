<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    Map<String, Object> course = (Map<String, Object>) request.getAttribute("course");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Syllabus - Study Planner</title>
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
        
        h2 { color: #667eea; margin-bottom: 10px; }
        
        .course-info {
            background: #f0f7ff;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
            border-left: 4px solid #667eea;
        }
        
        .course-info strong {
            color: #667eea;
            font-size: 1.1em;
        }
        
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
        
        input[type="file"] {
            width: 100%;
            padding: 12px;
            border: 2px dashed #ddd;
            border-radius: 5px;
            font-size: 14px;
            cursor: pointer;
            background: #fafafa;
            transition: all 0.3s;
        }
        
        input[type="file"]:hover {
            border-color: #667eea;
            background: #f0f7ff;
        }
        
        .note {
            font-size: 0.9em;
            color: #999;
            margin-top: 5px;
        }
        
        .tips {
            background: #fffbf0;
            border-left: 4px solid #f39c12;
            padding: 15px;
            border-radius: 5px;
            margin-top: 15px;
        }
        
        .tips strong {
            color: #f39c12;
        }
        
        .tips ul {
            margin-left: 20px;
            margin-top: 8px;
        }
        
        .tips li {
            margin: 5px 0;
            color: #666;
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
            text-decoration: none;
            text-align: center;
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
    </style>
</head>
<body>
    <header>
        <h1>📚 Study Planner - Upload Syllabus</h1>
    </header>
    
    <div class="container">
        <h2>Upload Course Syllabus</h2>
        
        <div class="course-info">
            <strong><%= course.get("code") %></strong> - <%= course.get("name") %><br>
            Semester: <%= course.get("semester") %> | Credits: <%= course.get("credits") %>
        </div>
        
        <% if (errorMessage != null) { %>
            <div class="alert-error"><%= errorMessage %></div>
        <% } %>
        
        <form method="POST" action="uploadSyllabus" enctype="multipart/form-data">
            <input type="hidden" name="courseId" value="<%= course.get("id") %>">
            
            <div class="form-group">
                <label for="syllabusFile">📄 Select Syllabus PDF *</label>
                <input type="file" id="syllabusFile" name="syllabusFile" 
                       accept=".pdf" required>
                <div class="note">
                    Only PDF files are accepted (Max size: 10MB)
                </div>
            </div>
            
            <div class="tips">
                <strong>💡 Tips:</strong>
                <ul>
                    <li>Upload the official course syllabus PDF</li>
                    <li>Ensure the file is clear and readable</li>
                    <li>Previous syllabus will be replaced if it exists</li>
                    <li>You can update the syllabus anytime</li>
                </ul>
            </div>
            
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">📤 Upload Syllabus</button>
                <a href="dashboard" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>
