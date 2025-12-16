// File: com/studyplanner/utils/SchedulingAlgorithm.java
package com.studyplanner.utils;

import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import com.studyplanner.dao.DatabaseManager;

/**
 * Smart Study Plan Generation Algorithm
 * Prioritizes tasks based on:
 * 1. Deadline proximity
 * 2. Task weightage/importance
 * 3. Estimated hours required
 * 4. Task type (exam > assignment)
 * 5. Daily study time constraints
 */
public class SchedulingAlgorithm {
    
    public static class StudyTask implements Comparable<StudyTask> {
        public int taskId;
        public String description;
        public LocalDate dueDate;
        public int hoursRequired;
        public int priority;
        public int weightage;
        public String taskType; // "exam" or "assignment"
        public int assignmentId;
        public int examId;

        @Override
        public int compareTo(StudyTask other) {
            // Priority 1: Days until due (urgency)
            long daysUntilThis = ChronoUnit.DAYS.between(LocalDate.now(), this.dueDate);
            long daysUntilOther = ChronoUnit.DAYS.between(LocalDate.now(), other.dueDate);
            
            if (daysUntilThis != daysUntilOther) {
                return Long.compare(daysUntilThis, daysUntilOther);
            }
            
            // Priority 2: Task type (exams are more critical)
            if (!this.taskType.equals(other.taskType)) {
                if ("exam".equals(this.taskType)) return -1;
                return 1;
            }
            
            // Priority 3: Weightage (importance)
            if (this.weightage != other.weightage) {
                return Integer.compare(other.weightage, this.weightage);
            }
            
            // Priority 4: Hours required (longer tasks scheduled first)
            return Integer.compare(other.hoursRequired, this.hoursRequired);
        }
    }

    /**
     * Generate personalized study plan for a user
     */
    public static int generateStudyPlan(int userId, String strategy) throws SQLException {
        DatabaseManager dbManager = new DatabaseManager();
        Connection conn = dbManager.getConnection();
        
        try {
            // Fetch all pending assignments and upcoming exams
            List<StudyTask> allTasks = getAllPendingTasks(conn, userId);
            
            // Sort tasks by priority
            Collections.sort(allTasks);
            
            // Distribute tasks across available days
            Map<LocalDate, List<String>> dailySchedule = distributeTasks(allTasks, strategy);
            
            // Create study plan record
            String planQuery = "INSERT INTO study_plans (user_id, plan_name, start_date, end_date, total_hours, priority_strategy) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement planPs = conn.prepareStatement(planQuery, Statement.RETURN_GENERATED_KEYS);
            planPs.setInt(1, userId);
            planPs.setString(2, "Study Plan - " + java.time.LocalDate.now());
            planPs.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            planPs.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(60)));
            planPs.setInt(5, calculateTotalHours(allTasks));
            planPs.setString(6, strategy);
            
            planPs.executeUpdate();
            ResultSet generatedKeys = planPs.getGeneratedKeys();
            int planId = -1;
            
            if (generatedKeys.next()) {
                planId = generatedKeys.getInt(1);
            }
            
            // Insert individual study tasks
            insertStudyTasks(conn, planId, allTasks, dailySchedule);
            
            planPs.close();
            conn.close();
            
            return planId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static List<StudyTask> getAllPendingTasks(Connection conn, int userId) throws SQLException {
        List<StudyTask> tasks = new ArrayList<>();
        
        // Get pending assignments
        String assignmentQuery = "SELECT a.id, a.assignment_title, a.due_date, a.weightage, a.estimated_hours, c.course_name " +
                                "FROM assignments a " +
                                "JOIN courses c ON a.course_id = c.id " +
                                "WHERE c.user_id = ? AND a.submission_status = 'PENDING' " +
                                "AND a.due_date >= NOW() " +
                                "ORDER BY a.due_date ASC";
        
        PreparedStatement assignmentPs = conn.prepareStatement(assignmentQuery);
        assignmentPs.setInt(1, userId);
        ResultSet assignmentRs = assignmentPs.executeQuery();
        
        while (assignmentRs.next()) {
            StudyTask task = new StudyTask();
            task.assignmentId = assignmentRs.getInt("id");
            task.description = assignmentRs.getString("assignment_title") + 
                             " (" + assignmentRs.getString("course_name") + ")";
            task.dueDate = assignmentRs.getDate("due_date").toLocalDate();
            task.weightage = assignmentRs.getInt("weightage");
            task.hoursRequired = assignmentRs.getInt("estimated_hours");
            task.taskType = "assignment";
            task.priority = calculatePriority("assignment", assignmentRs.getInt("weightage"));
            tasks.add(task);
        }
        assignmentPs.close();
        
        // Get upcoming exams
        String examQuery = "SELECT e.id, e.exam_name, e.exam_date, e.weightage, e.estimated_study_hours, c.course_name " +
                          "FROM exams e " +
                          "JOIN courses c ON e.course_id = c.id " +
                          "WHERE c.user_id = ? AND e.completed = FALSE " +
                          "AND e.exam_date >= NOW() " +
                          "ORDER BY e.exam_date ASC";
        
        PreparedStatement examPs = conn.prepareStatement(examQuery);
        examPs.setInt(1, userId);
        ResultSet examRs = examPs.executeQuery();
        
        while (examRs.next()) {
            StudyTask task = new StudyTask();
            task.examId = examRs.getInt("id");
            task.description = examRs.getString("exam_name") + 
                            " - " + examRs.getString("course_name");
            task.dueDate = examRs.getDate("exam_date").toLocalDate();
            task.weightage = examRs.getInt("weightage");
            task.hoursRequired = examRs.getInt("estimated_study_hours");
            task.taskType = "exam";
            task.priority = calculatePriority("exam", examRs.getInt("weightage"));
            tasks.add(task);
        }
        examPs.close();
        
        return tasks;
    }

    private static int calculatePriority(String taskType, int weightage) {
        if ("exam".equals(taskType)) {
            return 10 + (weightage / 10);
        }
        return 5 + (weightage / 10);
    }

    private static Map<LocalDate, List<String>> distributeTasks(List<StudyTask> tasks, String strategy) {
        Map<LocalDate, List<String>> schedule = new LinkedHashMap<>();
        LocalDate currentDate = LocalDate.now();
        int maxHoursPerDay = "intensive".equals(strategy) ? 6 : 3;
        
        for (StudyTask task : tasks) {
            long daysUntilDue = ChronoUnit.DAYS.between(currentDate, task.dueDate);
            int hoursPerDay = (int) Math.ceil((double) task.hoursRequired / Math.max(daysUntilDue, 1));
            hoursPerDay = Math.min(hoursPerDay, maxHoursPerDay);
            
            LocalDate scheduleDate = currentDate;
            int remainingHours = task.hoursRequired;
            
            while (remainingHours > 0 && scheduleDate.isBefore(task.dueDate)) {
                if (!schedule.containsKey(scheduleDate)) {
                    schedule.put(scheduleDate, new ArrayList<>());
                }
                
                List<String> dayTasks = schedule.get(scheduleDate);
                int dailyHours = Math.min(hoursPerDay, remainingHours);
                dayTasks.add(task.description + " (" + dailyHours + "h)");
                remainingHours -= dailyHours;
                scheduleDate = scheduleDate.plusDays(1);
            }
        }
        
        return schedule;
    }

    private static int calculateTotalHours(List<StudyTask> tasks) {
        return tasks.stream().mapToInt(t -> t.hoursRequired).sum();
    }

    private static void insertStudyTasks(Connection conn, int planId, List<StudyTask> allTasks,
                                        Map<LocalDate, List<String>> dailySchedule) throws SQLException {
        String taskQuery = "INSERT INTO study_tasks (plan_id, assignment_id, exam_id, task_description, " +
                          "task_date, hours_required, priority, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement taskPs = conn.prepareStatement(taskQuery);
        int taskOrder = 1;
        
        for (Map.Entry<LocalDate, List<String>> entry : dailySchedule.entrySet()) {
            LocalDate date = entry.getKey();
            List<String> descriptions = entry.getValue();
            
            for (String description : descriptions) {
                // Find matching task
                StudyTask matchingTask = allTasks.stream()
                    .filter(t -> t.description.contains(description.split("\\(")[0].trim()))
                    .findFirst()
                    .orElse(null);
                
                taskPs.setInt(1, planId);
                taskPs.setObject(2, matchingTask != null ? matchingTask.assignmentId : null);
                taskPs.setObject(3, matchingTask != null ? matchingTask.examId : null);
                taskPs.setString(4, description);
                taskPs.setDate(5, java.sql.Date.valueOf(date));
                taskPs.setInt(6, extractHours(description));
                taskPs.setInt(7, matchingTask != null ? matchingTask.priority : 5);
                taskPs.setString(8, "PENDING");
                
                taskPs.addBatch();
            }
        }
        
        taskPs.executeBatch();
        taskPs.close();
    }

    private static int extractHours(String description) {
        try {
            String[] parts = description.split("\\(");
            String hours = parts[1].replace("h)", "");
            return Integer.parseInt(hours);
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Get study plan with all tasks for a user
     */
    public static Map<String, Object> getStudyPlan(Connection conn, int userId) throws SQLException {
        String query = "SELECT * FROM study_plans WHERE user_id = ? ORDER BY created_date DESC LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        
        Map<String, Object> plan = null;
        if (rs.next()) {
            plan = new HashMap<>();
            int planId = rs.getInt("id");
            plan.put("id", planId);
            plan.put("name", rs.getString("plan_name"));
            plan.put("totalHours", rs.getInt("total_hours"));
            plan.put("strategy", rs.getString("priority_strategy"));
            
            // Get all study tasks for this plan
            String taskQuery = "SELECT * FROM study_tasks WHERE plan_id = ? ORDER BY task_date ASC";
            PreparedStatement taskPs = conn.prepareStatement(taskQuery);
            taskPs.setInt(1, planId);
            ResultSet taskRs = taskPs.executeQuery();
            
            List<Map<String, Object>> tasks = new ArrayList<>();
            while (taskRs.next()) {
                Map<String, Object> task = new HashMap<>();
                task.put("id", taskRs.getInt("id"));
                task.put("description", taskRs.getString("task_description"));
                task.put("date", taskRs.getDate("task_date"));
                task.put("hours", taskRs.getInt("hours_required"));
                task.put("status", taskRs.getString("status"));
                task.put("priority", taskRs.getInt("priority"));
                tasks.add(task);
            }
            plan.put("tasks", tasks);
            taskPs.close();
        }
        
        ps.close();
        return plan;
    }
}
