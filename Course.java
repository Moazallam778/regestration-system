/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package regestrationsystem;

/**
 *
 * @author moaza
 */
public class Course {
    private int courseId;
    private String name;
    private int capacity;
    private int enrolledStudents;
    private int teacherId; // Association with Teacher

    // Constructor for Course
    public Course(int courseId, String name, int capacity) {
        this.courseId = courseId;
        this.name = name;
        this.capacity = capacity;
        this.enrolledStudents = 0;
        this.teacherId = 0; // 0 means unassigned
    }

    // Getters
    public int getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    
    
    
    public int getCapacity() {
        return capacity;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    // check if the course ina  maxcapacity or not 
    public boolean checkCapacity() {
        return enrolledStudents < capacity;
    }
     // increase the number of enrolledStudents
    public void enroll() {
        if (checkCapacity()) {
            enrolledStudents++;
        }
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
   

}
    

