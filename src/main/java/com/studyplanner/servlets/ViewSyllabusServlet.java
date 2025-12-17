package com.studyplanner.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/viewSyllabus")
public class ViewSyllabusServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            String syllabusPath = request.getParameter("path");
            
            if (syllabusPath == null || syllabusPath.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Syllabus path not provided");
                return;
            }
            
            // Get the absolute path
            String appPath = request.getServletContext().getRealPath("");
            String fullPath = appPath + File.separator + syllabusPath;
            File file = new File(fullPath);
            
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Syllabus file not found");
                return;
            }
            
            // Set content type and headers
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            response.setContentLengthLong(file.length());
            
            // Stream the file
            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error viewing syllabus");
        }
    }
}
