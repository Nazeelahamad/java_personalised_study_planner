<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Study Planner</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .container { max-width: 600px; margin: 100px auto; padding: 40px; background: white; 
                    border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
        h1 { color: #c33; font-size: 3em; margin-bottom: 20px; }
        p { color: #666; font-size: 1.1em; margin-bottom: 30px; }
        a { color: #667eea; text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>
    <div class="container">
        <h1>⚠️ Error</h1>
        <p><%= exception != null ? exception.getMessage() : "An unexpected error occurred." %></p>
        <a href="index.jsp">← Return to Home</a>
    </div>
</body>
</html>
