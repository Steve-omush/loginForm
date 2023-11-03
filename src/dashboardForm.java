import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class dashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JLabel adminLabel;
    private JButton btnRegister;

    public dashboardForm() {
        //Initialize the Frame
        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(1200, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //This Frame should not be visible for un authenticated users.
        //Check if we have users in the database, if we have users display login form otherwise register form.
        //method returns true if we have users in db, otherwise false
        boolean hasRegisteredUsers = connectToDatabase();
        if (hasRegisteredUsers) {
            //show LoginForm
            loginForm loginForm = new loginForm(this);
            User user = loginForm.user;
            //If User authenticated correctly make this frame visible
            if (user != null) {
                adminLabel.setText("User: "+user.username);
                setLocationRelativeTo(null);
                setVisible(true);
            }
            else {
                dispose();
            }
        }
        //no users in database, show registration form.
        else {
            RegistrationForm registrationForm = new RegistrationForm(this);
            User user = registrationForm.user;

            if (user != null) {
                adminLabel.setText("User: "+user.username);
                setLocationRelativeTo(null);
                setVisible(true);
            }
            else {
                dispose();
            }
        }
        //method executed when click on theb register button
        //display registration form
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if registration is one correctly, return a valid user.
                //if user is valid, display user
                RegistrationForm registrationForm = new RegistrationForm(dashboardForm.this);
                User user = registrationForm.user;

                if (user != null) {
                    JOptionPane.showMessageDialog(dashboardForm.this,
                            "New user: "+user.username,
                            "Successful Registration",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        final String MYSQL_SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false";
        final String DB_URL = "jdbc:mysql://localhost:3306/mystore?useSSL=false";
        final String USERNAME = "myuser";
        final String PASSWORD = "mypassword";

        try {
            //FIRST, CONNECT TO MYSQL SERVER AND CREATE THE DATABASE IF NOT CREATED.
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS mystore");
            statement.close();
            conn.close();

            //SECOND, CONNECT TO DATABASE AND CREATE A TABLE IF TABLE "users" not created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT(10) AUTO_INCREMENT PRIMARY KEY NOT NULL,"
                    + "fname VARCHAR(255) NOT NULL,"
                    + "lname VARCHAR(255) NOT NULL,"
                    + "email VARCHAR(255) NOT NULL,"
                    + "userName VARCHAR(255) NOT NULL,"
                    + "password INT NOT NULL,"
                    + "confirm INT NOT NULL"
                    + ")";
            statement.executeUpdate(sql);

            //Check if we have registered users in the database or not.
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0){
                    hasRegisteredUsers = true;
                }
            }

            //close the connection of database
            statement.close();
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return (hasRegisteredUsers);
    }

    //RUN THE APPLICATION
    public static void main(String[] args) {
        dashboardForm myForm = new dashboardForm();
    }
}
