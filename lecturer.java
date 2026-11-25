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

    public class lecturer extends  User{
    private double salary;
    private List<Integer> taughtCourses;

    public lecturer() {
    }


    

    public lecturer( String name, int id, int age, double salary, String email) {
        super(name, id, age, email);
        this.taughtCourses = new ArrayList<>();
        this.salary=salary;
    }

   public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

   
    
    // add course to taughtCourses
     public void assignCourse(int courseId) {
         taughtCourses.add(courseId);
    }
     public boolean isTaught(int courseId) {
    return taughtCourses.contains(courseId);
    }

    
   

    @Override
    public String getDetails() {
        return super.getDetails()
                + "\nSalary: " + salary
                + "\nTaught Courses: " + taughtCourses;
    }

    public List<Integer> getTaughtCourses() {
        return taughtCourses;
    }

    @Override
    public String getRole() {
        return "Lecturer";
    }
  
    


}
    

