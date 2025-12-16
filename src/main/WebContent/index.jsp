<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personalized Study Planner - Login & Register</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            max-width: 900px;
            width: 100%;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            overflow: hidden;
        }

        .content-wrapper {
            display: flex;
            height: 100%;
        }

        .left-section {
            flex: 1;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 60px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .left-section h1 {
            font-size: 2.5em;
            margin-bottom: 20px;
            font-weight: 700;
        }

        .left-section p {
            font-size: 1.1em;
            line-height: 1.8;
            margin-bottom: 30px;
            opacity: 0.95;
        }

        .features {
            list-style: none;
        }

        .features li {
            padding: 10px 0;
            font-size: 1em;
            display: flex;
            align-items: center;
        }

        .features li:before {
            content: "✓";
            margin-right: 15px;
            font-weight: bold;
            font-size: 1.2em;
        }

        .right-section {
            flex: 1;
            padding: 60px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .form-container {
            display: none;
        }

        .form-container.active {
            display: block;
        }

        .form-container h2 {
            font-size: 1.8em;
            margin-bottom: 30px;
            color: #333;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
            font-size: 0.95em;
        }

        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 1em;
            transition: all 0.3s ease;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .btn {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 10px;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }

        .toggle-form {
            margin-top: 20px;
            text-align: center;
            color: #666;
            font-size: 0.95em;
        }

        .toggle-form a {
            color: #667eea;
            cursor: pointer;
            font-weight: 600;
            text-decoration: none;
        }

        .toggle-form a:hover {
            text-decoration: underline;
        }

        .alert {
            padding: 12px 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 0.95em;
        }

        .alert-error {
            background-color: #fee;
            color: #c33;
            border: 1px solid #fcc;
        }

        .alert-success {
            background-color: #efe;
            color: #3c3;
            border: 1px solid #cfc;
        }

        @media (max-width: 768px) {
            .content-wrapper {
                flex-direction: column;
            }

            .left-section,
            .right-section {
                padding: 40px 20px;
            }

            .left-section h1 {
                font-size: 1.8em;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="content-wrapper">
            <!-- Left Section: Features & Branding -->
            <div class="left-section">
                <h1>📚 Study Planner</h1>
                <p>Master your academic goals with intelligent study planning.</p>
                <ul class="features">
                    <li>Smart Study Schedule Generation</li>
                    <li>Course & Assignment Tracking</li>
                    <li>Exam Preparation Management</li>
                    <li>Priority-Based Task Scheduling</li>
                    <li>Real-time Progress Tracking</li>
                    <li>Personalized Study Insights</li>
                </ul>
            </div>

            <!-- Right Section: Forms -->
            <div class="right-section">
                <%
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    String successMessage = (String) request.getAttribute("successMessage");
                %>
                
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="alert alert-error"><%= errorMessage %></div>
                <% } %>
                
                <% if (successMessage != null && !successMessage.isEmpty()) { %>
                    <div class="alert alert-success"><%= successMessage %></div>
                <% } %>

                <!-- Login Form -->
                <div id="loginForm" class="form-container active">
                    <h2>Welcome Back!</h2>
                    <form method="POST" action="login">
                        <div class="form-group">
                            <label for="loginUsername">Username</label>
                            <input type="text" id="loginUsername" name="username" required 
                                   placeholder="Enter your username">
                        </div>
                        <div class="form-group">
                            <label for="loginPassword">Password</label>
                            <input type="password" id="loginPassword" name="password" required 
                                   placeholder="Enter your password">
                        </div>
                        <input type="hidden" name="action" value="login">
                        <button type="submit" class="btn">Sign In</button>
                    </form>
                    <div class="toggle-form">
                        Don't have an account? <a onclick="toggleForms()">Sign up</a>
                    </div>
                </div>

                <!-- Registration Form -->
                <div id="registerForm" class="form-container">
                    <h2>Create Account</h2>
                    <form method="POST" action="login">
                        <div class="form-group">
                            <label for="registerFullName">Full Name</label>
                            <input type="text" id="registerFullName" name="fullName" required 
                                   placeholder="Your full name">
                        </div>
                        <div class="form-group">
                            <label for="registerUsername">Username</label>
                            <input type="text" id="registerUsername" name="username" required 
                                   placeholder="Choose a username">
                        </div>
                        <div class="form-group">
                            <label for="registerEmail">Email</label>
                            <input type="email" id="registerEmail" name="email" required 
                                   placeholder="your.email@university.edu">
                        </div>
                        <div class="form-group">
                            <label for="registerPassword">Password</label>
                            <input type="password" id="registerPassword" name="password" required 
                                   placeholder="Create a strong password">
                        </div>
                        <input type="hidden" name="action" value="register">
                        <button type="submit" class="btn">Sign Up</button>
                    </form>
                    <div class="toggle-form">
                        Already have an account? <a onclick="toggleForms()">Sign in</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        function toggleForms() {
            const loginForm = document.getElementById('loginForm');
            const registerForm = document.getElementById('registerForm');
            
            loginForm.classList.toggle('active');
            registerForm.classList.toggle('active');
        }
    </script>
</body>
</html>
