package regestrationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegestrationSystem extends Application {

    private final Faculty facultySystem = new Faculty("Faculty of Science");
    private final admin adminLogin = new admin();
    private final TextArea outputArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        facultySystem.loadDataFromDatabase();

        Label loginLabel = new Label("Login");
        Label userName = new Label("Username");
        Label password = new Label("Password");
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");

        GridPane loginPane = new GridPane();
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(20));
        loginPane.setAlignment(Pos.CENTER);
        loginPane.add(loginLabel, 1, 0);
        loginPane.add(userName, 0, 1);
        loginPane.add(password, 0, 2);
        loginPane.add(userField, 1, 1);
        loginPane.add(passField, 1, 2);
        loginPane.add(loginButton, 1, 3);

        Scene loginScene = new Scene(loginPane, 420, 300);
        loginScene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());

        loginButton.setOnAction(event -> {
            if (adminLogin.login(userField.getText(), passField.getText())) {
                primaryStage.setScene(buildDashboardScene());
            } else {
                showError("Invalid username or password.");
            }
        });

        primaryStage.setTitle("Registration System");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Scene buildDashboardScene() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(createStudentsTab());
        tabPane.getTabs().add(createTeachersTab());
        tabPane.getTabs().add(createCoursesTab());
        tabPane.getTabs().add(createRegistrationTab());
        tabPane.getTabs().add(createSearchTab());

        outputArea.setEditable(false);
        outputArea.setPrefHeight(220);
        outputArea.setText("Welcome to the registration system.\n");

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(outputArea);
        BorderPane.setMargin(outputArea, new Insets(10));

        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        return scene;
    }

    private Tab createStudentsTab() {
        TextField nameField = new TextField();
        TextField idField = new TextField();
        TextField ageField = new TextField();
        TextField levelField = new TextField();
        TextField majorField = new TextField();
        TextField emailField = new TextField();
        Button addButton = new Button("Add Student");
        Button showButton = new Button("Show Students");

        GridPane form = createForm();
        form.addRow(0, new Label("Name"), nameField);
        form.addRow(1, new Label("ID"), idField);
        form.addRow(2, new Label("Age"), ageField);
        form.addRow(3, new Label("Level"), levelField);
        form.addRow(4, new Label("Major"), majorField);
        form.addRow(5, new Label("Email"), emailField);
        form.add(actions(addButton, showButton), 1, 6);

        addButton.setOnAction(event -> {
            try {
                facultySystem.addStudent(
                        nameField.getText(),
                        parseInt(idField.getText(), "Student ID"),
                        parseInt(levelField.getText(), "Level"),
                        majorField.getText(),
                        parseInt(ageField.getText(), "Age"),
                        emailField.getText()
                );
                outputArea.setText("Student added successfully.");
                clearFields(nameField, idField, ageField, levelField, majorField, emailField);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        showButton.setOnAction(event -> outputArea.setText(facultySystem.getStudentsSummary()));

        Tab tab = new Tab("Students", form);
        tab.setClosable(false);
        return tab;
    }

    private Tab createTeachersTab() {
        TextField nameField = new TextField();
        TextField idField = new TextField();
        TextField ageField = new TextField();
        TextField salaryField = new TextField();
        TextField emailField = new TextField();
        Button addButton = new Button("Add Lecturer");
        Button showButton = new Button("Show Lecturers");

        GridPane form = createForm();
        form.addRow(0, new Label("Name"), nameField);
        form.addRow(1, new Label("ID"), idField);
        form.addRow(2, new Label("Age"), ageField);
        form.addRow(3, new Label("Salary"), salaryField);
        form.addRow(4, new Label("Email"), emailField);
        form.add(actions(addButton, showButton), 1, 5);

        addButton.setOnAction(event -> {
            try {
                facultySystem.addTeacher(
                        nameField.getText(),
                        parseInt(idField.getText(), "Teacher ID"),
                        parseInt(ageField.getText(), "Age"),
                        parseInt(salaryField.getText(), "Salary"),
                        emailField.getText()
                );
                outputArea.setText("Lecturer added successfully.");
                clearFields(nameField, idField, ageField, salaryField, emailField);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        showButton.setOnAction(event -> outputArea.setText(facultySystem.getTeachersSummary()));

        Tab tab = new Tab("Lecturers", form);
        tab.setClosable(false);
        return tab;
    }

    private Tab createCoursesTab() {
        TextField nameField = new TextField();
        TextField idField = new TextField();
        TextField capacityField = new TextField();
        Button addButton = new Button("Add Course");
        Button showButton = new Button("Show Courses");

        GridPane form = createForm();
        form.addRow(0, new Label("Course Name"), nameField);
        form.addRow(1, new Label("Course ID"), idField);
        form.addRow(2, new Label("Capacity"), capacityField);
        form.add(actions(addButton, showButton), 1, 3);

        addButton.setOnAction(event -> {
            try {
                facultySystem.addCourse(
                        nameField.getText(),
                        parseInt(idField.getText(), "Course ID"),
                        parseInt(capacityField.getText(), "Capacity")
                );
                outputArea.setText("Course added successfully.");
                clearFields(nameField, idField, capacityField);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        showButton.setOnAction(event -> outputArea.setText(facultySystem.getCoursesSummary()));

        Tab tab = new Tab("Courses", form);
        tab.setClosable(false);
        return tab;
    }

    private Tab createRegistrationTab() {
        TextField studentIdField = new TextField();
        TextField courseIdField = new TextField();
        TextField lecturerIdField = new TextField();
        TextField assignCourseIdField = new TextField();
        Button registerButton = new Button("Register Student");
        Button assignButton = new Button("Assign Lecturer");
        Button availableButton = new Button("Available Courses");

        GridPane form = createForm();
        form.addRow(0, new Label("Student ID"), studentIdField);
        form.addRow(1,new Label("Course ID"),courseIdField);
        form.addRow(2,registerButton);
        form.addRow(3,availableButton);
        form.addRow(4,new Label("Lecturer ID"), lecturerIdField);
        form.addRow(5,new Label("Course ID"),assignCourseIdField);
        form.addRow(6,assignButton);

        registerButton.setOnAction(event -> {
            try {
                String message = facultySystem.registerStudentForCourse(
                        parseInt(studentIdField.getText(), "Student ID"),
                        parseInt(courseIdField.getText(), "Course ID")
                );
                outputArea.setText(message);
                clearFields(studentIdField, courseIdField);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        availableButton.setOnAction(event -> outputArea.setText(facultySystem.getAvailableCourses()));

        assignButton.setOnAction(event -> {
            try {
                String message = facultySystem.assignCourseToTeacher(
                        parseInt(lecturerIdField.getText(), "Lecturer ID"),
                        parseInt(assignCourseIdField.getText(), "Course ID")
                );
                outputArea.setText(message);
                clearFields(lecturerIdField, assignCourseIdField);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        Tab tab = new Tab("Registration", form);
        tab.setClosable(false);
        return tab;
    }

    private Tab createSearchTab() {
        TextField studentSearchField = new TextField();
        TextField lecturerSearchField = new TextField();
        Button studentSearchButton = new Button("Search Student");
        Button lecturerSearchButton = new Button("Search Lecturer");

         GridPane form = createForm();
        form.addRow(0, new Label("StudentID"),studentSearchField,studentSearchButton);
        form.addRow(1, new Label("lecturer ID"), lecturerSearchField,lecturerSearchButton);
        
       
        studentSearchButton.setOnAction(event -> {
            try {
                outputArea.setText(facultySystem.searchStudentSummary(parseInt(studentSearchField.getText(), "Student ID")));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
        lecturerSearchButton.setOnAction(event -> {
            try {
                outputArea.setText(facultySystem.searchTeacherSummary(parseInt(lecturerSearchField.getText(), "Lecturer ID")));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        Tab tab = new Tab("Search", form);
        tab.setClosable(false);
        return tab;
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private HBox actions(Button... buttons) {
        HBox box = new HBox(10, buttons);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
        java.awt.EventQueue.invokeLater(() -> {
            AdminChat adminChat = new AdminChat();
            StudentChat studentChat = new StudentChat();

            adminChat.setStudentChat(studentChat);
            studentChat.setAdminChat(adminChat);

            adminChat.setVisible(true);
            studentChat.setVisible(true);
        });
    }}
    

    