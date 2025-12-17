<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.studyplanner.dao.DatabaseManager" %>
<%
    // Check if user is logged in
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String fullName = (String) session.getAttribute("fullName");
    List<Map<String, Object>> courses = (List<Map<String, Object>>) request.getAttribute("courses");
    List<Map<String, Object>> assignments = (List<Map<String, Object>>) request.getAttribute("assignments");
    List<Map<String, Object>> exams = (List<Map<String, Object>>) request.getAttribute("exams");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    if (courses == null) courses = new ArrayList<>();
    if (assignments == null) assignments = new ArrayList<>();
    if (exams == null) exams = new ArrayList<>();
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Personalized Study Planner</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f7fa;
            color: #333;
        }

        header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        header h1 {
            font-size: 1.8em;
        }

        header .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        header .btn-logout {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            padding: 10px 20px;
            border: 1px solid white;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
        }

        header .btn-logout:hover {
            background: white;
            color: #667eea;
        }

        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: 500;
            animation: slideDown 0.3s ease;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .welcome-section {
            background: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .welcome-section h2 {
            color: #667eea;
            margin-bottom: 10px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            border-left: 4px solid #667eea;
            text-align: center;
        }

        .stat-card h3 {
            font-size: 0.9em;
            color: #999;
            margin-bottom: 10px;
            text-transform: uppercase;
        }

        .stat-card .number {
            font-size: 2.5em;
            font-weight: bold;
            color: #667eea;
        }

        .section {
            background: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .section h3 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
            font-size: 1.3em;
        }

        .action-buttons {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-size: 0.95em;
            transition: all 0.3s;
            display: inline-block;
        }

        .btn:hover {
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

        .btn-delete {
            background: #dc3545;
            color: white;
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.85em;
            transition: all 0.3s;
        }

        .btn-delete:hover {
            background: #c82333;
            transform: scale(1.05);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        table th {
            background-color: #f5f5f5;
            padding: 12px;
            text-align: left;
            font-weight: 600;
            color: #666;
            border-bottom: 2px solid #e0e0e0;
        }

        table td {
            padding: 12px;
            border-bottom: 1px solid #f0f0f0;
        }

        table tr:hover {
            background-color: #f9f9f9;
        }

        .empty-message {
            text-align: center;
            padding: 30px;
            color: #999;
            font-style: italic;
        }

        .badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }

        .badge-pending {
            background-color: #ffeaa7;
            color: #d97706;
        }

        .badge-urgent {
            background-color: #fab1a0;
            color: #c0392b;
        }

        .badge-upcoming {
            background-color: #a29bfe;
            color: #6c5ce7;
        }

        @media (max-width: 768px) {
            header {
                flex-direction: column;
                gap: 15px;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                text-align: center;
            }

            table {
                font-size: 0.9em;
            }
            .btn-view-syllabus {
		    color: #667eea;
		    text-decoration: none;
		    font-weight: 600;
		    padding: 6px 12px;
		    border-radius: 4px;
		    background: #e8f0fe;
		    display: inline-block;
		    transition: all 0.3s;
		}
		
		.btn-view-syllabus:hover {
		    background: #667eea;
		    color: white;
		    transform: scale(1.05);
		}
		
		.btn-upload-syllabus {
		    color: #f39c12;
		    text-decoration: none;
		    font-weight: 600;
		    padding: 6px 12px;
		    border-radius: 4px;
		    background: #fef5e7;
		    display: inline-block;
		    transition: all 0.3s;
		}
		
		.btn-upload-syllabus:hover {
		    background: #f39c12;
		    color: white;
		    transform: scale(1.05);
		}
            
        }
    </style>
    <script>
        function confirmDelete(type, id, name) {
            if (confirm('Are you sure you want to delete "' + name + '"?\n\nThis action cannot be undone.')) {
                document.getElementById('deleteForm' + type + id).submit();
            }
        }
    </script>
</head>
<body>
    <header>
        <h1>📚 Study Planner Dashboard</h1>
        <div class="user-info">
        <a href="completedTasks" style="color: white; margin-right: 20px;">View Completed Tasks</a>
       
            <span>Welcome, <strong><%= fullName != null ? fullName : "Student" %></strong>!</span>
            <a href="logout" class="btn-logout">Logout</a>
        </div>
    </header>

    <div class="container">
        <!-- Success/Error Messages -->
        <% if (successMessage != null) { %>
            <div class="alert alert-success">✓ <%= successMessage %></div>
        <% } %>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">✗ <%= errorMessage %></div>
        <% } %>

        <!-- Welcome Section -->
        <div class="welcome-section">
        <!-- <form method="POST" action="addDemoData" style="margin-top: 15px;">
		    <button type="submit" class="btn btn-secondary">
		        🎯 Add Demo Data (for testing)
		    </button>
		</form>  -->
            <h2>Your Academic Overview</h2>
            <p>Manage your courses, assignments, exams, and generate personalized study plans.</p>
        </div>

        <!-- Statistics Grid -->
        <%
	     // Get userId from session
	        int userId = (int) session.getAttribute("userId");
	        
	        // Calculate completion stats
	        int totalAssignments = assignments.size();
		    int totalExams = exams.size();
		    
		    // You'll need to get these from database
		    DatabaseManager dbManager = new DatabaseManager();
		    Connection statsConn = dbManager.getConnection();
		    
		    String completedQuery = "SELECT " +
		        "(SELECT COUNT(*) FROM assignments a JOIN courses c ON a.course_id = c.id " +
		        "WHERE c.user_id = ? AND a.submission_status = 'COMPLETED') as completed_assignments, " +
		        "(SELECT COUNT(*) FROM exams e JOIN courses c ON e.course_id = c.id " +
		        "WHERE c.user_id = ? AND e.completed = TRUE) as completed_exams";
		    
		    PreparedStatement statsPs = statsConn.prepareStatement(completedQuery);
		    statsPs.setInt(1, userId);
		    statsPs.setInt(2, userId);
		    ResultSet statsRs = statsPs.executeQuery();
		    
		    int completedAssignments = 0;
		    int completedExams = 0;
		    if (statsRs.next()) {
		        completedAssignments = statsRs.getInt("completed_assignments");
		        completedExams = statsRs.getInt("completed_exams");
		    }
		    statsPs.close();
		    statsConn.close();
		    
		    int totalTasks = totalAssignments + totalExams + completedAssignments + completedExams;
		    int completedTasks = completedAssignments + completedExams;
		    int completionPercentage = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
		%>

<div class="stats-grid">
    <div class="stat-card">
        <h3>Courses</h3>
        <div class="number"><%= courses.size() %></div>
    </div>
    <div class="stat-card">
        <h3>Pending Tasks</h3>
        <div class="number"><%= assignments.size() + exams.size() %></div>
    </div>
    <div class="stat-card">
        <h3>Completed Tasks</h3>
        <div class="number"><%= completedTasks %></div>
    </div>
    <div class="stat-card" style="border-left-color: #28a745;">
        <h3>Completion Rate</h3>
        <div class="number" style="color: #28a745;"><%= completionPercentage %>%</div>
    </div>
</div>

        <!-- Courses Section -->
		<div class="section">
		    <h3>Your Courses</h3>
		    <div class="action-buttons">
		        <a href="addCourse" class="btn">+ Add New Course</a>
		    </div>
		
		    <% if (courses.isEmpty()) { %>
		        <div class="empty-message">No courses added yet. Start by adding your courses!</div>
		    <% } else { %>
		        <table>
		            <thead>
					    <tr>
					        <th>Course Code</th>
					        <th>Course Name</th>
					        <th>Credits</th>
					        <th>Semester</th>
					        <th>Instructor</th>
					        <th>Schedule</th>
					        <th>Syllabus</th>
					        <th>Action</th>
					    </tr>
					</thead>

		            <tbody>
		                <% for (Map<String, Object> course : courses) { 
		                    String syllabusPath = (String) course.get("syllabusPath");
		                    boolean hasSyllabus = syllabusPath != null && !syllabusPath.isEmpty();
		                %>
		                    <tr>
							    <td><strong><%= course.get("code") %></strong></td>
							    <td><%= course.get("name") %></td>
							    <td><%= course.get("credits") %></td>
							    <td><%= course.get("semester") %></td>
							    <td><%= course.get("instructor") != null ? course.get("instructor") : "N/A" %></td>
							    <td>
							        <% 
							            String scheduleDays = (String) course.get("scheduleDays");
							            if (scheduleDays != null && !scheduleDays.isEmpty()) {
							        %>
							            <span style="font-size: 0.85em; color: #667eea; font-weight: 500;">
							                📅 <%= scheduleDays %>
							            </span>
							        <% } else { %>
							            <span style="color: #999; font-size: 0.85em;">Not set</span>
							        <% } %>
							    </td>
							    <td>
							        <% if (hasSyllabus) { %>
							            <a href="viewSyllabus?path=<%= syllabusPath %>" 
							               target="_blank" 
							               class="btn-view-syllabus">
							                📄 View
							            </a>
							        <% } else { %>
							            <a href="uploadSyllabus?courseId=<%= course.get("id") %>" 
							               class="btn-upload-syllabus">
							                📤 Upload
							            </a>
							        <% } %>
							    </td>
							    <td>
							        <form id="deleteFormCourse<%= course.get("id") %>" method="POST" action="deleteCourse" style="display: inline;">
							            <input type="hidden" name="courseId" value="<%= course.get("id") %>">
							            <button type="button" class="btn-delete" 
							                    onclick="confirmDelete('Course', <%= course.get("id") %>, '<%= course.get("code") %>')">
							                🗑️ Delete
							            </button>
							        </form>
							    </td>
							</tr>

		                <% } %>
		            </tbody>
		        </table>
		    <% } %>
		</div>


        <!-- Assignments Section -->
        <div class="section">
            <h3>Pending Assignments</h3>
            <div class="action-buttons">
                <a href="addAssignment" class="btn">+ Add Assignment</a>
            </div>

            <% if (assignments.isEmpty()) { %>
                <div class="empty-message">No pending assignments. Great work!</div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Assignment</th>
                            <th>Course</th>
                            <th>Due Date</th>
                            <th>Weightage</th>
                            <th>Est. Hours</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> assignment : assignments) { %>
                            <tr>
                                <td><strong><%= assignment.get("title") %></strong></td>
                                <td><%= assignment.get("courseName") %></td>
                                <td><%= dateTimeFormat.format(assignment.get("dueDate")) %></td>
                                <td><span class="badge badge-pending"><%= assignment.get("weightage") %>%</span></td>
                                <td><%= assignment.get("estimatedHours") %> hrs</td>
                                <!-- In the Assignments table, replace the Delete button cell with: -->
								<td>
								    <form method="POST" action="completeTask" style="display: inline;">
								        <input type="hidden" name="taskType" value="assignment">
								        <input type="hidden" name="taskId" value="<%= assignment.get("id") %>">
								        <button type="submit" class="btn-complete" 
								                style="background: #28a745; color: white; padding: 6px 12px; 
								                       border: none; border-radius: 4px; cursor: pointer; margin-right: 5px;">
								            ✓ Complete
								        </button>
								    </form>
								    <form id="deleteFormAssignment<%= assignment.get("id") %>" 
								          method="POST" action="deleteAssignment" style="display: inline;">
								        <input type="hidden" name="assignmentId" value="<%= assignment.get("id") %>">
								        <button type="button" class="btn-delete" 
								                onclick="confirmDelete('Assignment', <%= assignment.get("id") %>, 
								                        '<%= assignment.get("title") %>')">
								            🗑️ Delete
								        </button>
								    </form>
								</td>
								
								<!-- Do the same for Exams table -->
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <!-- Exams Section -->
        <div class="section">
            <h3>Upcoming Exams</h3>
            <div class="action-buttons">
                <a href="addExam" class="btn">+ Add Exam</a>
            </div>

            <% if (exams.isEmpty()) { %>
                <div class="empty-message">No upcoming exams scheduled.</div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Exam Name</th>
                            <th>Course</th>
                            <th>Exam Date</th>
                            <th>Type</th>
                            <th>Weightage</th>
                            <th>Study Hours</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> exam : exams) { %>
                            <tr>
                                <td><strong><%= exam.get("name") %></strong></td>
                                <td><%= exam.get("courseName") %></td>
                                <td><%= dateTimeFormat.format(exam.get("examDate")) %></td>
                                <td><span class="badge badge-urgent"><%= exam.get("type") %></span></td>
                                <td><%= exam.get("weightage") %>%</td>
                                <td><%= exam.get("estimatedStudyHours") %> hrs</td>
                                <td>
						    <form method="POST" action="completeTask" style="display: inline;">
						        <input type="hidden" name="taskType" value="exam">
						        <input type="hidden" name="taskId" value="<%= exam.get("id") %>">
						        <button type="submit" class="btn-complete" 
						                style="background: #28a745; color: white; padding: 6px 12px; 
						                       border: none; border-radius: 4px; cursor: pointer; margin-right: 5px;">
						            ✓ Complete
						        </button>
						    </form>
						    <form id="deleteFormExam<%= exam.get("id") %>" method="POST" action="deleteExam" style="display: inline;">
						        <input type="hidden" name="examId" value="<%= exam.get("id") %>">
						        <button type="button" class="btn-delete" 
						                onclick="confirmDelete('Exam', <%= exam.get("id") %>, '<%= exam.get("name") %>')">
						            🗑️ Delete
						        </button>
						    </form>
						</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <!-- Study Plan Generation -->
        <div class="section">
            <h3>Generate Study Plan</h3>
            <p style="margin-bottom: 20px; color: #666;">
                Create a personalized study schedule based on your courses, assignments, and exams.
            </p>
            <form method="POST" action="generatePlan" style="display: flex; gap: 10px; flex-wrap: wrap; align-items: center;">
                <label for="strategy">Study Strategy:</label>
                <select id="strategy" name="strategy" style="padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                    <option value="balanced">Balanced (3 hrs/day)</option>
                    <option value="intensive">Intensive (6 hrs/day)</option>
                    <option value="light">Light (1-2 hrs/day)</option>
                </select>
                <button type="submit" class="btn">Generate Plan</button>
            </form>
            <p style="margin-top: 15px; font-size: 0.9em; color: #999;">
                💡 The algorithm will prioritize tasks based on due dates, weightage, and task type.
            </p>
        </div>
    </div>
</body>
</html>
