import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class loginForm extends JDialog {
    private JLabel label1;
    private JLabel label2;
    private JLabel labeImg;
    private JLabel emaiLabel;
    private JTextField textEmail;
    private JPasswordField textPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;
    private JLabel textPass;

    /* CONSTRUCTOR */
    public loginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // This method is executed when we click the OK button. Listener to OK button.
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = textEmail.getText();
                String password = String.valueOf(textPassword.getPassword());

                //Check if credentials are valid.
                user = getAutnenticated(email, password);

                if (user != null ) {
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(loginForm.this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //Listener to the cancel button. Terminate login form
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }
    public User user;
    private User getAutnenticated(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost:3306/mystore?useSSL=false";
        final String USERNAME = "myuser";
        final String PASSWORD = "mypassword";

        //Connect to Database
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Connected to database successful
            //SQL QUERY TO HELP FIND THE USER
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            //Execute query.
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.fName = resultSet.getString("fName");
                user.lName = resultSet.getString("lName");
                user.email = resultSet.getString("email");
                user.username = resultSet.getString("username");
                user.password = resultSet.getString("password");
                user.confirm = resultSet.getString("confirm");
            }
            //Close the connection
            stmt.close();
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();;
        }
        return (user);
    }


    /* Main Method */
    public static void main(String[] args) {
        /* Create an object of type LoginForm*/
        loginForm loginForm = new loginForm(null);
        User user = loginForm.user;

        //validate user
        if (user != null) {
            System.out.println("Successful Authentication of: "+user.username);
            System.out.println("        First: "+user.fName);
            System.out.println("        Last: "+user.lName);
            System.out.println("        Email: "+user.email);
        }
        else {
            System.out.println("Authentication Cancelled.");
        }
    }
}
