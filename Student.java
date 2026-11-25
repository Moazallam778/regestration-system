/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package regestrationsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moaza
 */
public class Student extends User {
    private int level;
    private String major;
    private List<Integer> enrolledCourses ;

    public Student() {
    }

    public Student(String name, int id,int level, String major, int age, String email) {
        super(name, id, age, email);
        this.level = level;
        this.major = major;
       this.enrolledCourses = new ArrayList<>();
    }

  

    // to add course to student
    public void enrollCourse(int courseId) {
      enrolledCourses.add(courseId);
        }
    
    public boolean isEnrolled(int courseId) {
         return enrolledCourses.contains(courseId);
        }

    
  
      public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<Integer> getEnrolledCourses() {
        return enrolledCourses;
    }
    
     

    // Overriding
   
    @Override
    public String getDetails() {
        return super.getDetails()
                + "\nLevel: " + level
                + "\nMajor: " + major
                + "\nEnrolled Courses: " + enrolledCourses;
        
    }

    @Override
    public String getRole() {
        return "Student";
    }

}
