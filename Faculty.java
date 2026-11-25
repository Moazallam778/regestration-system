package regestrationsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Faculty {
    private String facultyName;
    //compostion
    public List<Student> students;
    public List<lecturer> teachers;
    public List<Course> courses;
  

    public Faculty() {
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.courses = new ArrayList<>();
      
    }

    public Faculty(String facultyName) {
        this();
        this.facultyName = facultyName;
        System.out.println("School Management System for " + facultyName + " Initialized.");
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<lecturer> getTeachers() {
        return teachers;
    }

    public void loadDataFromDatabase() {
        students.clear();
        teachers.clear();
        courses.clear();
        loadStudentsFromDatabase();
        loadTeachersFromDatabase();
        loadCoursesFromDatabase();
        loadStudentCoursesFromDatabase();
        loadTeacherCoursesFromDatabase();
    }

    private boolean isIdUnique(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return false;
            }
        }
        for (lecturer t : teachers) {
            if (t.getId() == id) {
                return false;
            }
        }
        for (Course c : courses) {
            if (c.getCourseId() == id) {
                return false;
            }
        }
        return true;
    }

    public Student addStudent(String name, int id, int level, String major, int age, String email) {
        if (!isIdUnique(id)) {
            throw new IllegalArgumentException("ID already exists.");
        }
        if (name == null || name.trim().isEmpty() || major == null || major.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Student data is incomplete.");
        }
        Student student = new Student(name, id, level, major, age, email);
        students.add(student);
        save(student);
        return student;
    }

    public lecturer addTeacher(String name, int id, int age, int salary, String email) {
        if (!isIdUnique(id)) {
            throw new IllegalArgumentException("ID already exists.");
        }
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher data is incomplete.");
        }
        lecturer teacher = new lecturer(name, id, age, salary, email);
        teachers.add(teacher);
        save(teacher);
        return teacher;
    }

    public Course addCourse(String name, int id, int capacity) {
        if (!isIdUnique(id)) {
            throw new IllegalArgumentException("ID already exists.");
        }
        if (name == null || name.trim().isEmpty() || capacity <= 0) {
            throw new IllegalArgumentException("Course data is invalid.");
        }
        Course course = new Course(id, name, capacity);
        courses.add(course);
        save(course);
        return course;
    }


    public String getAvailableCourses() {
        StringBuilder builder = new StringBuilder();
        boolean found = false;

        for (Course course : courses) {
            if (course.checkCapacity()) {
                builder.append("ID: ").append(course.getCourseId())
                        .append(" | Name: ").append(course.getName())
                        .append(" | Capacity: ").append(course.getCapacity())
                        .append(" | Enrolled: ").append(course.getEnrolledStudents())
                        .append(System.lineSeparator());
                found = true;
            }
        }

        if (!found) {
            return "No courses currently have available space.";
        }

        return builder.toString();
    }

    public String registerStudentForCourse(int studentId, int courseId) {
        Student student = searchStudentById(studentId);
        Course course = searchCourseById(courseId);

        if (student == null || course == null) {
            throw new IllegalArgumentException("Student or course not found.");
        }
        if (student.isEnrolled(courseId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }
        if (!course.checkCapacity()) {
            throw new IllegalArgumentException("Course is full.");
        }

        student.enrollCourse(courseId);
        course.enroll();
        saveStudentCourse(studentId, courseId);
        updateCourseEnrollment(course);
        return student.getName() + " registered for " + course.getName();
    }


    public String assignCourseToTeacher(int teacherId, int courseId) {
        lecturer teacher = searchTeacherById(teacherId);
        Course course = searchCourseById(courseId);
        int assignedTeacherId = getAssignedTeacherId(courseId);

        if (teacher == null || course == null) {
            return "Lecturer or course not found.";
        } else if (assignedTeacherId == teacherId || teacher.isTaught(courseId)) {
            course.setTeacherId(teacherId);
            if (!teacher.isTaught(courseId)) {
                teacher.assignCourse(courseId);
            }
            updateCourseTeacher(course);
            return "Course is already assigned to this lecturer.";
        } else if (assignedTeacherId != 0) {
            return "Course is already assigned to another lecturer.";
        } else if (teacher.isTaught(courseId)) {
            return "Lecturer already teaches this course.";
        } else {
            if (!saveTeacherCourse(teacherId, courseId)) {
                return "Could not save lecturer course in database.";
            }
            teacher.assignCourse(courseId);
            course.setTeacherId(teacherId);
            updateCourseTeacher(course);
            return teacher.getName() + " assigned to " + course.getName();
        }
    }


    public Student searchStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    public lecturer searchTeacherById(int id) {
        for (lecturer teacher : teachers) {
            if (teacher.getId() == id) {
                return teacher;
            }
        }
        return null;
    }

    public Course searchCourseById(int id) {
        for (Course course : courses) {
            if (course.getCourseId() == id) {
                return course;
            }
        }
        return null;
    }

    public String searchStudentSummary(int id) {
        Student student = searchStudentById(id);
        if (student == null) {
            return "Student not found.";
        }
        return student.getDetails()
                + "\nCourses Names: " + getStudentCourseNames(student);
    }

    public String searchTeacherSummary(int id) {
        lecturer teacher = searchTeacherById(id);
        if (teacher == null) {
            return "Teacher not found.";
        }
        return teacher.getDetails();
    }

    public void StudentsSort() {
        System.out.println("\n--- Sorting Students by ID ---");
        List<Student> sortedStudents = new ArrayList<>(students);
        int n = sortedStudents.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedStudents.get(j).getId() > sortedStudents.get(j + 1).getId()) {
                    Student temp = sortedStudents.get(j);
                    sortedStudents.set(j, sortedStudents.get(j + 1));
                    sortedStudents.set(j + 1, temp);
                }
            }
        }
     
    }

    public String getStudentsSummary() {
        List<Student> sortedStudents = new ArrayList<>(students);
        int n = sortedStudents.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedStudents.get(j).getId() > sortedStudents.get(j + 1).getId()) {
                    Student temp = sortedStudents.get(j);
                    sortedStudents.set(j, sortedStudents.get(j + 1));
                    sortedStudents.set(j + 1, temp);
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Student s : sortedStudents) {
            builder.append("ID: ").append(s.getId())
                    .append(" | Name: ").append(s.getName())
                    .append(" | Level: ").append(s.getLevel())
                    .append(" | Major: ").append(s.getMajor())
                    .append(System.lineSeparator());
        }
        return builder.length() == 0 ? "No students found." : builder.toString();
    }

    public String getTeachersSummary() {
        List<lecturer> sortedLecturers = new ArrayList<>(teachers);
        int n = sortedLecturers.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedLecturers.get(j).getId() > sortedLecturers.get(j + 1).getId()) {
                    lecturer temp = sortedLecturers.get(j);
                    sortedLecturers.set(j, sortedLecturers.get(j + 1));
                    sortedLecturers.set(j + 1, temp);
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (lecturer t : sortedLecturers) {
            builder.append("ID: ").append(t.getId())
                    .append(" | Name: ").append(t.getName())
                    .append(" | Salary: ").append(t.getSalary())
                    .append(System.lineSeparator());
        }
        return builder.length() == 0 ? "No teachers found." : builder.toString();
    }

    public String getCoursesSummary() {
        List<Course> sortedCourses = new ArrayList<>(courses);
        int n = sortedCourses.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedCourses.get(j).getCourseId() > sortedCourses.get(j + 1).getCourseId()) {
                    Course temp = sortedCourses.get(j);
                    sortedCourses.set(j, sortedCourses.get(j + 1));
                    sortedCourses.set(j + 1, temp);
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Course c : sortedCourses) {
            int assignedTeacherId = getAssignedTeacherId(c.getCourseId());
            builder.append("ID: ").append(c.getCourseId())
                    .append(" | Name: ").append(c.getName())
                    .append(" | Capacity: ").append(c.getCapacity())
                    .append(" | Enrolled: ").append(c.getEnrolledStudents())
                    .append(" | Teacher ID: ").append(assignedTeacherId)
                    .append(System.lineSeparator());
        }
        return builder.length() == 0 ? "No courses found." : builder.toString();
    }

    private void save(Student student) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "INSERT INTO students (id, name, age, level_no, major, email) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, student.getId());
            pst.setString(2, student.getName());
            pst.setInt(3, student.getAge());
            pst.setInt(4, student.getLevel());
            pst.setString(5, student.getMajor());
            pst.setString(6, student.getEmail());
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is inserted");
            }
        } catch (SQLException ex) {
            System.out.println("Student save warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void save(lecturer teacher) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "INSERT INTO teachers (id, name, age, salary, email) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, teacher.getId());
            pst.setString(2, teacher.getName());
            pst.setInt(3, teacher.getAge());
            pst.setDouble(4, teacher.getSalary());
            pst.setString(5, teacher.getEmail());
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is inserted");
            }
        } catch (SQLException ex) {
            System.out.println("Teacher save warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void save(Course course) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "INSERT INTO courses (id, name, capacity, enrolled_students, teacher_id) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, course.getCourseId());
            pst.setString(2, course.getName());
            pst.setInt(3, course.getCapacity());
            pst.setInt(4, course.getEnrolledStudents());
            pst.setInt(5, course.getTeacherId());
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is inserted");
            }
        } catch (SQLException ex) {
            System.out.println("Course save warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void updateCourseEnrollment(Course course) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "UPDATE courses SET enrolled_students = ? WHERE id = ?";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, course.getEnrolledStudents());
            pst.setInt(2, course.getCourseId());
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is updated");
            }
        } catch (SQLException ex) {
            System.out.println("Course update warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private boolean updateCourseTeacher(Course course) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "UPDATE courses SET teacher_id = ? WHERE id = ?";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, course.getTeacherId());
            pst.setInt(2, course.getCourseId());
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is updated");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Course update warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }

    private void saveStudentCourse(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, studentId);
            pst.setInt(2, courseId);
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is inserted");
            }
        } catch (SQLException ex) {
            System.out.println("Student course save warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private boolean saveTeacherCourse(int teacherId, int courseId) {
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "INSERT INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";
        try {
            conn = dbConn.DBConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, teacherId);
            pst.setInt(2, courseId);
            int i = pst.executeUpdate();
            if (i == 1) {
                System.out.println("Data is inserted");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Teacher course save warning: " + ex.getMessage());
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }

    private void loadStudentsFromDatabase() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "SELECT id, name, age, level_no, major, email FROM students";
        try {
            conn = dbConn.DBConnection();
            if (conn == null) {
                System.out.println("Load students warning: database connection unavailable.");
                return;
            }
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getInt("level_no"),
                        rs.getString("major"),
                        rs.getInt("age"),
                        rs.getString("email")
                );
                students.add(student);
            }
        } catch (SQLException ex) {
            System.out.println("Load students warning: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void loadTeachersFromDatabase() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "SELECT id, name, age, salary, email FROM teachers";
        try {
            conn = dbConn.DBConnection();
            if (conn == null) {
                System.out.println("Load teachers warning: database connection unavailable.");
                return;
            }
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                lecturer teacher = new lecturer(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getInt("age"),
                        rs.getDouble("salary"),
                        rs.getString("email")
                );
                teachers.add(teacher);
            }
        } catch (SQLException ex) {
            System.out.println("Load teachers warning: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void loadCoursesFromDatabase() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "SELECT id, name, capacity, enrolled_students, teacher_id FROM courses";
        try {
            conn = dbConn.DBConnection();
            if (conn == null) {
                System.out.println("Load courses warning: database connection unavailable.");
                return;
            }
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("capacity")
                );
                course.setEnrolledStudents(rs.getInt("enrolled_students"));
                course.setTeacherId(rs.getInt("teacher_id"));
                courses.add(course);
            }
        } catch (SQLException ex) {
            System.out.println("Load courses warning: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void loadStudentCoursesFromDatabase() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "SELECT student_id, course_id FROM student_courses";
        try {
            conn = dbConn.DBConnection();
            if (conn == null) {
                System.out.println("Load student courses warning: database connection unavailable.");
                return;
            }
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Student student = searchStudentById(rs.getInt("student_id"));
                if (student != null) {
                    student.enrollCourse(rs.getInt("course_id"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Load student courses warning: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void loadTeacherCoursesFromDatabase() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "SELECT teacher_id, course_id FROM teacher_courses";
        try {
            conn = dbConn.DBConnection();
            if (conn == null) {
                System.out.println("Load teacher courses warning: database connection unavailable.");
                return;
            }
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                lecturer teacher = searchTeacherById(rs.getInt("teacher_id"));
                if (teacher != null) {
                    int courseId = rs.getInt("course_id");
                    if (!teacher.isTaught(courseId)) {
                        teacher.assignCourse(courseId);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Load teacher courses warning: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        for (Course course : courses) {
            if (course.getTeacherId() != 0) {
                lecturer teacher = searchTeacherById(course.getTeacherId());
                if (teacher != null && !teacher.isTaught(course.getCourseId())) {
                    teacher.assignCourse(course.getCourseId());
                }
            }
        }

        for (lecturer teacher : teachers) {
            for (int courseId : teacher.getTaughtCourses()) {
                Course course = searchCourseById(courseId);
                if (course != null && course.getTeacherId() == 0) {
                    course.setTeacherId(teacher.getId());
                }
            }
        }
    }

    private String getStudentCourseNames(Student student) {
        if (student.getEnrolledCourses().isEmpty()) {
            return "No courses enrolled";
        }

        StringBuilder builder = new StringBuilder();
        for (int courseId : student.getEnrolledCourses()) {
            Course course = searchCourseById(courseId);
            if (builder.length() > 0) {
                builder.append(", ");
            }
            if (course != null) {
                builder.append(course.getName());
            } else {
                builder.append("Course ID ").append(courseId);
            }
        }
        return builder.toString();
    }

    private int getAssignedTeacherId(int courseId) {
        for (lecturer teacher : teachers) {
            if (teacher.isTaught(courseId)) {
                return teacher.getId();
            }
        }
        Course course = searchCourseById(courseId);
        if (course != null) {
            return course.getTeacherId();
        }
        return 0;
    }

}
