<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Course - Study Planner</title>
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
            max-width: 700px;
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
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group-full {
            grid-column: 1 / -1;
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
        
        input[type="file"] {
            border: 2px dashed #ddd;
            cursor: pointer;
            padding: 10px;
        }
        
        input[type="file"]:hover {
            border-color: #667eea;
        }
        
        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .note {
            font-size: 0.85em;
            color: #999;
            margin-top: 5px;
        }
        
        .file-section {
            background: #f9f9f9;
            padding: 15px;
            border-radius: 5px;
            border-left: 4px solid #667eea;
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
            text-decoration: none;
            text-align: center;
            line-height: 1.5;
        }
        
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
        }
		        .checkbox-group {
		    display: grid;
		    grid-template-columns: repeat(2, 1fr);
		    gap: 10px;
		    padding: 15px;
		    background: #f9f9f9;
		    border-radius: 5px;
		    border: 1px solid #e0e0e0;
		}
		
		.checkbox-label {
		    display: flex;
		    align-items: center;
		    padding: 10px 15px;
		    background: white;
		    border: 2px solid #ddd;
		    border-radius: 5px;
		    cursor: pointer;
		    transition: all 0.3s;
		    font-weight: normal;
		    user-select: none;
		}
		
		.checkbox-label:hover {
		    border-color: #667eea;
		    background: #f0f7ff;
		    transform: translateY(-2px);
		    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
		}
		
		.checkbox-label input[type="checkbox"] {
		    width: 18px;
		    height: 18px;
		    margin-right: 10px;
		    cursor: pointer;
		    accent-color: #667eea;
		}
		
		.checkbox-label input[type="checkbox"]:checked ~ span {
		    font-weight: 600;
		    color: #667eea;
		}
		
		.checkbox-label:has(input:checked) {
		    border-color: #667eea;
		    background: #f0f7ff;
		    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
		}
		
		@media (max-width: 768px) {
		    .checkbox-group {
		        grid-template-columns: 1fr;
		    }
		}
		        
    </style>
</head>
<body>
    <header>
        <h1>📚 Study Planner - Add Course</h1>
    </header>
    
    <div class="container">
        <h2>Add New Course</h2>
        
        <% if (errorMessage != null) { %>
            <div class="alert-error"><%= errorMessage %></div>
        <% } %>
        
        <form method="POST" action="addCourse" enctype="multipart/form-data">
            <div class="form-row">
                <div class="form-group">
                    <label for="code">Course Code *</label>
                    <input type="text" id="code" name="code" 
                           placeholder="e.g., CS101" maxlength="20" required>
                </div>
                
                <div class="form-group">
                    <label for="credits">Credits *</label>
                    <input type="number" id="credits" name="credits" 
                           min="1" max="10" placeholder="e.g., 3" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="name">Course Name *</label>
                <input type="text" id="name" name="name" 
                       placeholder="e.g., Data Structures and Algorithms" maxlength="200" required>
            </div>
            
            <div class="form-row">
                <div class="form-group">
				    <label for="semester">Semester *</label>
				    <input type="number" id="semester" name="semester" 
				           min="1" max="12" placeholder="e.g., 3" required>
				    <div class="note">Enter semester number (1-12)</div>
				</div>

                
                <div class="form-group">
                    <label for="instructor">Instructor</label>
                    <input type="text" id="instructor" name="instructor" 
                           placeholder="e.g., Dr. Smith">
                </div>
            </div>
            
            <div class="form-group">
				    <label>Schedule Days</label>
				    <div class="checkbox-group">
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Monday">
				            <span>Monday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Tuesday">
				            <span>Tuesday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Wednesday">
				            <span>Wednesday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Thursday">
				            <span>Thursday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Friday">
				            <span>Friday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Saturday">
				            <span>Saturday</span>
				        </label>
				        <label class="checkbox-label">
				            <input type="checkbox" name="scheduleDays" value="Sunday">
				            <span>Sunday</span>
				        </label>
				    </div>
				    <div class="note">Select the days when this course meets</div>
				</div>

            
            <div class="form-group file-section">
                <label for="syllabusFile">📄 Course Syllabus (Optional)</label>
                <input type="file" id="syllabusFile" name="syllabusFile" accept=".pdf">
                <div class="note">
                    Upload course syllabus PDF (Max 10MB). You can also add it later from the dashboard.
                </div>
            </div>
            
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">✓ Add Course</button>
                <a href="dashboard" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>
