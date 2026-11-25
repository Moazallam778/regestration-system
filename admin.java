/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package regestrationsystem;

/**
 *
 * @author moaza
 */
public class admin implements login {

    private final String USER = "admin";
    private final String PASS = "1234";

    @Override
    public boolean login(String username, String password) {
        return username.equals(USER) && password.equals(PASS);
    }
}