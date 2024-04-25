package Vehicle_Parking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.Arrays;

class Registration extends JFrame {
    private JLabel userLabel,passLabel,confirmpassLabel,discountAmountLable,
            thresholdLable,timethresholdLable,carslotLable,bikeslotLable,registrationLable,controlLabel;
    private JTextField userField,discountField,thresholdField,timethresholdField,
            carslotField,bikeslotField;
    private JPasswordField passwordField,confirmpassField;
    private JButton addButton,deleteButton,clearButton,backLogin,editButton,saveButton;
    private JCheckBox showPassCheckBox;
    Registration(){
         setTitle("Control Panel");
         setSize(830,420);
         setLayout(null);
         setLocationRelativeTo(null);
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         initComponents();
         setVisible(true);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                char[] password = passwordField.getPassword();
                char[] confirmPassword = confirmpassField.getPassword();

                if (password.length == 0 || username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username and Password fields cannot be empty.");
                    return;
                }
                if(!(password.length >=8)){
                    JOptionPane.showMessageDialog(null,"Password must be greater than 8 character");
                    return;
                }

                // Check if the Password and Confirm Password fields match
                if (!Arrays.equals(password, confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Password and Confirm Password do not match.");
                    return;
                }

                // Connect to the database
                try (Connection connection = connectToDatabase()) {
                    if (connection != null) {
                        // Check if the username already exists in the database
                        if (isUsernameAlreadyExists(connection, username)) {
                            JOptionPane.showMessageDialog(null, "Username already exists.");
                            return;
                        }

                        String sql = "INSERT INTO user_info (username, password) VALUES (?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, username);
                        statement.setString(2, new String(password));
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            System.out.println("User added successfully:");
                            System.out.println("Username: " + username);
                            System.out.println("Password: " + new String(password));

                            JOptionPane.showMessageDialog(null, "User added successfully.");
                            // Clear the fields after successful insertion
                            userField.setText("");
                            passwordField.setText("");
                            confirmpassField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(null, "Error adding user.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                char[] password = passwordField.getPassword();
                char[] confirmPassword = confirmpassField.getPassword();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username field cannot be empty.");
                    return;
                }

                // Check if the Password and Confirm Password fields match
                if (!Arrays.equals(password, confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Password and Confirm Password do not match.");
                    return;
                }

                // Connect to the database
                try (Connection connection = connectToDatabase()) {
                    if (connection != null) {
                        String sql = "DELETE FROM user_info WHERE username = ? AND password = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, username);
                        statement.setString(2, new String(password));
                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            System.out.println("User deleted successfully:");
                            System.out.println("Username: " + username);

                            JOptionPane.showMessageDialog(null, "User deleted successfully.");
                            // Clear the username and password fields after successful deletion
                            userField.setText("");
                            passwordField.setText("");
                            confirmpassField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(null, "User not found or Password does not match.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error.");
                }
            }
        });




        showPassCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // Checkbox is checked, show the password
                    passwordField.setEchoChar((char) 0); // Set echo character to 0 (null)
                    confirmpassField.setEchoChar((char) 0);
                } else {
                    // Checkbox is unchecked, hide the password
                    passwordField.setEchoChar('*'); // Set echo character to '*'
                    confirmpassField.setEchoChar('*');
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userField.setText("");
                passwordField.setText("");
                confirmpassField.setText("");
            }
        });

        backLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                dispose(); // Close the Registration frame
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enable editing of text fields
                thresholdField.setEditable(true);
                timethresholdField.setEditable(true);
                discountField.setEditable(true);
                carslotField.setEditable(true);
                bikeslotField.setEditable(true);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Save the edited values back to the Main class
                    MainController.DISCOUNT_THRESHOLD = Integer.parseInt(thresholdField.getText());
                    MainController.MINIMUM_PARKING_HOURS = Integer.parseInt(timethresholdField.getText());
                    MainController.DISCOUNT_RATE= Double.parseDouble(discountField.getText());
                    MainController.carAvailableSlot = Integer.parseInt(carslotField.getText());
                    MainController.bikeAvailableSlot = Integer.parseInt(bikeslotField.getText());

                    // Disable editing of text fields
                    thresholdField.setEditable(false);
                    timethresholdField.setEditable(false);
                    discountField.setEditable(false);

                    // Display a confirmation message
                    JOptionPane.showMessageDialog(Registration.this, "Settings saved successfully.",
                            "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    // Handle invalid input (non-numeric values)
                    JOptionPane.showMessageDialog(Registration.this, "Invalid input. Please enter numeric values.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    private boolean isUsernameAlreadyExists(Connection connection, String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_info WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
    }
    private void initComponents(){
         Font custom1=new Font("Arial",Font.BOLD,14);
         Font custom2= new Font("Arial",Font.PLAIN,14);
         Font custom3= new Font("Arial",Font.BOLD,13);

         registrationLable = new JLabel("<html><u>Admin Registration</u></html>");
         registrationLable.setFont(custom1);
         registrationLable.setBounds(20,10,150,30);
         add(registrationLable);

         controlLabel = new JLabel("<html><u>Control Settings</u></html>");
         controlLabel.setFont(custom1);
         controlLabel.setBounds(430,10,150,30);
         add(controlLabel);

         userLabel = new JLabel("Username");
         userLabel.setFont(custom1);
         userLabel.setBounds(20,55,100,30);
         add(userLabel);

         userField = new JTextField(10);
         userField.setFont(custom2);
         userField.setBounds(155,55,180,30);
         add(userField);

         passLabel = new JLabel("Password");
         passLabel.setFont(custom1);
         passLabel.setBounds(20,100,100,30);
         add(passLabel);

         passwordField = new JPasswordField(10);
         passwordField.setFont(custom2);
         passwordField.setBounds(155,100,180,30);
         add(passwordField);

         confirmpassLabel = new JLabel("Confirm Password");
         confirmpassLabel.setFont(custom1);
         confirmpassLabel.setBounds(20,145,130,30);
         add(confirmpassLabel);

         confirmpassField = new JPasswordField(10);
         confirmpassField.setFont(custom2);
         confirmpassField.setBounds(155,145,180,30);
         add(confirmpassField);

        showPassCheckBox = new JCheckBox("Show Password");
        showPassCheckBox.setBounds(180, 178, 150, 20);
        add(showPassCheckBox);

        addButton = new JButton("Add");
        addButton.setFont(custom3);
        addButton.setBounds(20,220,90,20);
        add(addButton);

        deleteButton = new JButton("Delete");
        deleteButton.setFont(custom3);
        deleteButton.setBounds(120,220,90,20);
        add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.setFont(custom3);
        clearButton.setBounds(220,220,90,20);
        add(clearButton);

        backLogin = new JButton("back to login");
        backLogin.setFont(custom3);
        backLogin.setBounds(20,255,120,20);
        add(backLogin);

        discountAmountLable = new JLabel("Discount Rate");
        discountAmountLable.setFont(custom1);
        discountAmountLable.setBounds(430, 55,130,30);
        add(discountAmountLable);

        discountField = new JTextField(String.valueOf(MainController.DISCOUNT_RATE),10);
        discountField.setEditable(false);
        discountField.setFont(custom2);
        discountField.setBounds(580,55,180,30);
        add(discountField);

        thresholdLable = new JLabel("Parking Threshold");
        thresholdLable.setFont(custom1);
        thresholdLable.setBounds(430,100,130,30);
        add(thresholdLable);

        thresholdField = new JTextField(String.valueOf(MainController.DISCOUNT_THRESHOLD),10);
        thresholdField.setEditable(false);
        thresholdField.setFont(custom2);
        thresholdField.setBounds(580,100,180,30);
        add(thresholdField);

        timethresholdLable = new JLabel("Time Threshold");
        timethresholdLable.setFont(custom1);
        timethresholdLable.setBounds(430,145,130,30);
        add(timethresholdLable);

        timethresholdField = new JTextField(String.valueOf(MainController.MINIMUM_PARKING_HOURS),10);
        timethresholdField.setEditable(false);
        timethresholdField.setFont(custom2);
        timethresholdField.setBounds(580,145,180,30);
        add(timethresholdField);

        carslotLable = new JLabel("Car slot ");
        carslotLable.setFont(custom1);
        carslotLable.setBounds(430,190,130,30);
        add(carslotLable);

        carslotField = new JTextField(String.valueOf(MainController.carAvailableSlot),10);
        carslotField.setEditable(false);
        carslotField.setFont(custom2);
        carslotField.setBounds(580,190,180,30);
        add(carslotField);

        bikeslotLable = new JLabel("Bike slot");
        bikeslotLable.setFont(custom1);
        bikeslotLable.setBounds(430,235,130,30);
        add(bikeslotLable);

        bikeslotField = new JTextField(String.valueOf(MainController.bikeAvailableSlot),10);
        bikeslotField.setEditable(false);
        bikeslotField.setFont(custom2);
        bikeslotField.setBounds(580,235,180,30);
        add(bikeslotField);


        editButton = new JButton("Edit");
        editButton.setFont(custom3);
        editButton.setBounds(430,310,90,20);
        add(editButton);

        saveButton = new JButton("Save");
        saveButton.setFont(custom3);
        saveButton.setBounds(540,310,90,20);
        add(saveButton);
    }
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

  /*  public static void main(String[] args) {
        new Registration();
    }

   */

}
