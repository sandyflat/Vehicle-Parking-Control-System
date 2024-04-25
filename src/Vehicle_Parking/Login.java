package Vehicle_Parking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.sql.*;

public class Login extends JFrame {


    JButton loginButton,clearButton,managerButton;
    JTextField userField;
    JPasswordField passField;
    JCheckBox showPassCheckBox;
    JLabel managerLabel,companyName;

    String managerPassword = "abcde";
    Login(){
        setTitle("Login");
        setSize(800,450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Color backgroundColor = Color.decode("#4a7fd4");
        getContentPane().setBackground(backgroundColor);
        initComponents();
        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                char[] password = passField.getPassword();

                if (username.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(null, "Enter both Username and password");
                    return;
                }

                // Connect to the database
                try (Connection connection = connectToDatabase()) {
                    if (connection != null) {
                        String sql = "SELECT * FROM user_info WHERE username = ? AND password = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, username);
                        statement.setString(2, new String(password));
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            // Successful login
                            JOptionPane.showMessageDialog(null, "Login successful");
                         new MainController(username);
                        } else {
                            // Invalid login
                            JOptionPane.showMessageDialog(null, "Invalid username or password");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userField.setText("");
                passField.setText("");
            }
        });

        showPassCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passField.setEchoChar((char) 0);
                }else{
                    passField.setEchoChar('*');
                }
            }
        });

        managerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                while (true) {
                    // Create a panel to hold the components
                    JPanel panel = new JPanel(new GridBagLayout());

                    // Create a GridBagConstraints object to customize component placement
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(5, 5, 5, 5); // Padding

                    // Add the label and password field to the panel
                    JLabel managerPassLabel = new JLabel("Password: ");
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    panel.add(managerPassLabel, gbc);

                    JPasswordField passwordField = new JPasswordField(10);
                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    panel.add(passwordField, gbc);

                    // Show an input dialog with the panel
                    int option = JOptionPane.showConfirmDialog(
                            null,
                            panel,
                            "Enter Manager Password",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (option == JOptionPane.OK_OPTION) {
                        char[] inputPassword = passwordField.getPassword();
                        String enteredPassword = new String(inputPassword);

                        if (enteredPassword.equals(managerPassword)) {
                            new Registration(); // Open the Registration frame
                            break; // Exit the loop when the password is correct
                        } else {
                            // Clear the password field and allow the user to re-enter the password
                            passwordField.setText("");
                            JOptionPane.showMessageDialog(null, "Incorrect Manager Password");
                        }
                    } else {
                        // User canceled the dialog, exit the loop
                        break;
                    }
                }
            }
        });

    }
    // Database connection method
    private Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/vehicle_info";
            String user = "root";
            String password = "";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initComponents(){
        Font customFont = new Font("Arial", Font.BOLD, 14);
        JPanel panel = new JPanel();
        panel.setBounds(400,20,365,370);
        panel.setLayout(null);
        panel.setBackground(Color.white);
        add(panel);

        JLabel topicLabel = new JLabel("Login");
        topicLabel.setFont(new Font("Arial",Font.BOLD,20));
        topicLabel.setBounds(160,10,100,30);
        panel.add(topicLabel);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(20,40,100,30);
        userLabel.setFont(customFont);
        panel.add(userLabel);

        userField = new JTextField(10);
        userField.setBounds(20,70,320,30);
        userField.setFont(new Font("Arial",Font.PLAIN,14));
        panel.add(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(customFont);
        passLabel.setBounds(20,115,100,30);
        panel.add(passLabel);

        passField = new JPasswordField(10);
        passField.setFont(new Font("Arial",Font.PLAIN,14));
        passField.setBounds(20,150,320,30);
        panel.add(passField);

        showPassCheckBox = new JCheckBox("Show Password");
        showPassCheckBox.setBackground(Color.white);
        showPassCheckBox.setBounds(220, 190, 130, 20);
        panel.add(showPassCheckBox);

        loginButton = new JButton("Login");
        loginButton.setBounds(20,230,90,30);
        panel.add(loginButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(120,230,90,30);
        panel.add(clearButton);

        managerLabel = new JLabel("<html><u>Manager options</u></html>");
        managerLabel.setBounds(20,330,100,30);
        panel.add(managerLabel);

        companyName = new JLabel("Vehicle Parking System");
        companyName.setFont(new Font("Arial",Font.BOLD,25));
        companyName.setForeground(Color.white);
        companyName.setBounds(40,200,300,40);
        add(companyName);

        ImageIcon image2 = new ImageIcon("C:\\java\\Vehicle_Parking\\src\\Vehicle_Parking\\spaces.png");
        JLabel imageLabel2 = new JLabel(image2);
        imageLabel2.setBounds(145,140,image2.getIconWidth(),image2.getIconHeight());
        add(imageLabel2);
    }

    public static void main(String[] args) {
        new Login();
    }
}


/*
  to create the table user_info
  CREATE TABLE user_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

* */
