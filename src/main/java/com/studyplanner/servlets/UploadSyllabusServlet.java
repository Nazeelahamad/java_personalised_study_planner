package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.studyplanner.dao.DatabaseManager;

@WebServlet("/uploadSyllabus")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class UploadSyllabusServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "syllabi";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int userId = (int) session.getAttribute("userId");
            
            Map<String, Object> course = DatabaseManager.getCourseById(courseId, userId);
            
            if (course.isEmpty()) {
                session.setAttribute("errorMessage", "Course not found.");
                response.sendRedirect("dashboard");
                return;
            }
            
            request.setAttribute("course", course);
            request.getRequestDispatcher("uploadSyllabus.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error loading upload page: " + e.getMessage());
            response.sendRedirect("dashboard");
        }
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
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            
            // Get the file part
            Part filePart = request.getPart("syllabusFile");
            
            if (filePart == null || filePart.getSize() == 0) {
                session.setAttribute("errorMessage", "Please select a file to upload.");
                response.sendRedirect("uploadSyllabus?courseId=" + courseId);
                return;
            }
            
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            
            // Validate file type
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                session.setAttribute("errorMessage", "Only PDF files are allowed!");
                response.sendRedirect("uploadSyllabus?courseId=" + courseId);
                return;
            }
            
            // Create upload directory if it doesn't exist
            String appPath = request.getServletContext().getRealPath("");
            String uploadPath = appPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            
            // Create unique filename to avoid conflicts
            String uniqueFileName = userId + "_" + courseId + "_" + System.currentTimeMillis() + "_" + fileName;
            String filePath = uploadPath + File.separator + uniqueFileName;
            
            // Save file
            filePart.write(filePath);
            
            // Save path to database (relative path for portability)
            String relativePath = UPLOAD_DIR + "/" + uniqueFileName;
            boolean updated = DatabaseManager.updateCourseSyllabus(courseId, userId, relativePath);
            
            if (updated) {
                session.setAttribute("successMessage", "Syllabus uploaded successfully! 📄");
            } else {
                session.setAttribute("errorMessage", "Failed to update syllabus in database.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error uploading syllabus: " + e.getMessage());
        }
        
        response.sendRedirect("dashboard");
    }
}
