package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/addCourse")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class AddCourseServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "syllabi";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        request.getRequestDispatcher("addCourse.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            int credits = Integer.parseInt(request.getParameter("credits"));
            String semester = request.getParameter("semester");
            String instructor = request.getParameter("instructor");
            
            // Get multiple selected schedule days from checkboxes
            String[] selectedDays = request.getParameterValues("scheduleDays");
            String scheduleDays = null;
            if (selectedDays != null && selectedDays.length > 0) {
                scheduleDays = String.join(", ", selectedDays);
            }
            
            String syllabusPath = null;
            
            // Handle syllabus file upload (optional)
            Part filePart = request.getPart("syllabusFile");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                
                // Validate file type
                if (fileName.toLowerCase().endsWith(".pdf")) {
                    // Create upload directory if it doesn't exist
                    String appPath = request.getServletContext().getRealPath("");
                    String uploadPath = appPath + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
                    
                    // Create unique filename
                    String uniqueFileName = userId + "_" + System.currentTimeMillis() + "_" + fileName;
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    
                    // Save file
                    filePart.write(filePath);
                    
                    // Store relative path
                    syllabusPath = UPLOAD_DIR + "/" + uniqueFileName;
                } else {
                    session.setAttribute("errorMessage", "Only PDF files are allowed for syllabus!");
                    response.sendRedirect("addCourse");
                    return;
                }
            }
            
            int courseId = DatabaseManager.addCourse(userId, code, name, credits, 
                                                    semester, instructor, scheduleDays, syllabusPath);
            
            if (courseId > 0) {
                session.setAttribute("successMessage", "Course added successfully!" + 
                    (syllabusPath != null ? " Syllabus uploaded. 📄" : ""));
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("errorMessage", "Failed to add course");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error adding course: " + e.getMessage());
            doGet(request, response);
        }
    }

}
