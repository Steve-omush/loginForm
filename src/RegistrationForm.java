import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog {
    private JLabel labelRegister;
    private JLabel labeIcon;
    private JLabel nameLabel;
    private JTextField textFname;
    private JTextField textLname;
    private JTextField textEmail;
    private JTextField textUser;
    private JPasswordField textPass;
    private JPasswordField textConpass;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        //read from the fields which the user has entered data
        String fName = textFname.getText();
        String lName = textLname.getText();
        String email = textEmail.getText();
        String userName = textUser.getText();
        String password = String.valueOf(textPass.getPassword());
        String confirm = String.valueOf(textConpass.getPassword());
        /* necessary to close the application when you click on close button*/
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (fName.isBlank() || lName.isEmpty() || email.isEmpty() || userName.isEmpty() || password.isEmpty() ||confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all the fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(fName, lName, email, userName, password, confirm);
        /* if valid, create user else an error*/
        if (user != null)
        {
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    User user;
    private User addUserToDatabase(String fName, String lName, String email, String userName, String password, String confirm) {
        User user = null;
        /* Variables to connect to database*/
        final String DB_URL = "jdbc:mysql://localhost:3306/mystore?useSSL=false";
        final String USERNAME = "myuser";
        final String PASSWORD = "mypassword";
        /* Import some classes*/
        /* Connection */
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Connected to database successfully.
            /* SQL statement to add new user*/
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (fName, lName, email, userName, password, confirm)" +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, fName);
            preparedStatement.setString(2, lName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, userName);
            preparedStatement.setString(5, password);
            preparedStatement.setString(6, confirm);

            //Execute the SQL
            //INSERT ROWS INTO THE TABLE
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0){
                user = new User();
                user.fName = fName;
                user.lName = lName;
                user.email = email;
                user.username = userName;
                user.password = password;
                user.confirm = confirm;
            }
            /* Close the connection*/
            stmt.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return (user);
    }

    public static void main(String[] args){
        RegistrationForm myForm = new RegistrationForm(null);
        /* read the user object to see if its null.*/
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: "+user.username);
        }
        else {
            System.out.println("Registration Cancelled");
        }
    }
}
