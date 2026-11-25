/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package regestrationsystem;

/**
 *
 * @author moaza
 */
public abstract class User  {
    private String name;
    private int id;
    private int age;
    private String email;
    
      

    public User() {
    }

    public User(String name, int id, int age, String email) {
        this.name = name;
        this.id = id;
        this.age = age;
        this.email = email;
    }

   
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }
   
  

    public String getDetails() {
        return "Name: " + name
                + "\nID: " + id
                + "\nAge: " + age
                + "\nEmail: " + email
                + "\nRole: " + getRole();
    }

    public abstract String getRole();
}
 
