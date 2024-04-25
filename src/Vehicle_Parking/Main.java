package Vehicle_Parking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class MainController extends JFrame {
    JTextField plateField;
    JButton addButton,removeButton,searchButton,clearButton,payButton,logoutButton;
    JRadioButton carRadio,bikeRadio;
    JLabel plateLabel,typeLabel,carLabel,bikeLabel,carTlabel,carRlabel,carAlabel,bikeTlabel,
            bikeRlabel,bikeAlabel, carTslot,carRslot,carAslot,bikeTslot,bikeRslot,bikeAslot,operatorLabel
            ,operatorName;
    private DefaultTableModel tableModel;
    private JTable vehicleTable;
    JTextArea infoTextArea;


    static int carAvailableSlot = 10;
    static int bikeAvailableSlot = 10;
    Car[] carSlot = new Car[carAvailableSlot];
    Bike[] bikeSlot = new Bike[bikeAvailableSlot];

    final double HOURLY_RATE = 5.0; // Adjust this rate as needed

    static int DISCOUNT_THRESHOLD = 5;
    static int MINIMUM_PARKING_HOURS = 5;
    static double DISCOUNT_RATE = 0.10; // 10% discount rate

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/vehicle_info";
        String username = "root"; // Replace with your MySQL username
        String password = ""; // Replace with your MySQL password
        return DriverManager.getConnection(url, username, password);
    }


    MainController(String userName){
        setTitle("Vehicle Parking System");
        setSize(800,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        initComponents();
        operatorLabel = new JLabel(userName);
        operatorLabel.setFont(new Font("Arial",Font.BOLD,13));
        operatorLabel.setBounds(625,5,100,30);
        add(operatorLabel);
        setVisible(true);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plateNo = plateField.getText();

                // Check if the plate number is already present in any slot (both car and bike)
                if (isPlateNumberPresent(plateNo)) {
                    // Show an error dialog if the vehicle is already parked
                    JOptionPane.showMessageDialog(MainController.this, "The vehicle with plate number " + plateNo +
                            " is already parked in a slot.", "Vehicle Already Parked", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method
                }

                String type = null;
                if (carRadio.isSelected()) {
                    type = "car";
                } else if (bikeRadio.isSelected()) {
                    type = "bike";
                }

                // Check if the plate number is provided
                if (plateNo.isEmpty()) {
                    // Show an error dialog for the plate number
                    JOptionPane.showMessageDialog(MainController.this, "Please enter a plate number.",
                            "Plate Number Missing", JOptionPane.ERROR_MESSAGE);
                } else if (type == null) {
                    // Show an error dialog for the vehicle type
                    JOptionPane.showMessageDialog(MainController.this, "Please select a vehicle type.",
                            "Vehicle Type Missing", JOptionPane.ERROR_MESSAGE);
                } else if ((type.equals("car") && isCarSlotFull()) || (type.equals("bike") && isBikeSlotFull())) {
                    // Show an error dialog if the corresponding slot is full
                    JOptionPane.showMessageDialog(MainController.this, "The " + type + " slot is full.",
                            type + " Slot Full", JOptionPane.ERROR_MESSAGE);
                } else {
                    int slotNumber = -1; // Initialize to -1 (not added)

                    if (type.equals("car")) {
                        for (int i = 0; i < carAvailableSlot; i++) {
                            if (carSlot[i] == null) {
                                carSlot[i] = new Car(plateNo);
                                slotNumber = i + 1; // Slot number (1-based index)
                                break;
                            }
                        }
                    } else if (type.equals("bike")) {
                        for (int i = 0; i < bikeAvailableSlot; i++) {
                            if (bikeSlot[i] == null) {
                                bikeSlot[i] = new Bike(plateNo);
                                slotNumber = i + 1; // Slot number (1-based index)
                                break;
                            }
                        }
                    }

                    if (slotNumber != -1) {
                        // Vehicle successfully added to the slot
                        clearFields();
                        // Update the JTable with the correct slot number
                        updateTable(type, plateNo, LocalDateTime.now(), slotNumber);
                        JOptionPane.showMessageDialog(MainController.this, type + " with plate number " + plateNo +
                                " has been added to slot " + slotNumber + ".", "Vehicle Added", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("Vehicle with plate number " + plateNo + " and type " + type +
                                " has been added to slot " + slotNumber + ".");
                    } else {
                        // An error occurred while adding the vehicle
                        JOptionPane.showMessageDialog(MainController.this, "An error occurred while adding the " + type + ".",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                updateSlotLabels();
            }
        });


        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plateNo = plateField.getText();
                String type = null;
                if (carRadio.isSelected()) {
                    type = "car";
                } else if (bikeRadio.isSelected()) {
                    type = "bike";
                }

                // Check if the plate number and type are provided
                if (plateNo.isEmpty()) {
                    // Show an error dialog for the plate number
                    JOptionPane.showMessageDialog(MainController.this, "Please enter a plate number.",
                            "Plate Number Missing", JOptionPane.ERROR_MESSAGE);
                } else if (type == null) {
                    // Show an error dialog for the vehicle type
                    JOptionPane.showMessageDialog(MainController.this, "Please select a vehicle type.",
                            "Vehicle Type Missing", JOptionPane.ERROR_MESSAGE);
                } else {
                    boolean removed = false;
                    int slotNumber = -1; // Initialize to -1 (not found)

                    LocalDateTime entryTime = null; // Initialize entry time

                    if (type.equals("car")) {
                        for (int i = 0; i < carAvailableSlot; i++) {
                            if (carSlot[i] != null && carSlot[i].getPlateNumber().equals(plateNo)) {
                                entryTime = carSlot[i].getEntryTime(); // Get the entry time
                                carSlot[i] = null;
                                removed = true;
                                slotNumber = i + 1; // Slot number (1-based index)
                                break;
                            }
                        }
                    } else if (type.equals("bike")) {
                        for (int i = 0; i < bikeAvailableSlot; i++) {
                            if (bikeSlot[i] != null && bikeSlot[i].getPlateNumber().equals(plateNo)) {
                                entryTime = bikeSlot[i].getEntryTime(); // Get the entry time
                                bikeSlot[i] = null;
                                removed = true;
                                slotNumber = i + 1; // Slot number (1-based index)
                                break;
                            }
                        }
                    }

                    if (removed) {
                        // Remove the row from the table based on plate number
                        removeRowFromTable(plateNo);
                        clearFields();

                        LocalDateTime exitTime = LocalDateTime.now(); // Get the exit time

                        // Calculate the time difference for exit time
                        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
                        long minutes = ChronoUnit.MINUTES.between(entryTime, exitTime) % 60;

                        // Calculate parking charges based on hours and minutes
                        double parkingCharges = (hours * HOURLY_RATE) + ((double) minutes / 60 * HOURLY_RATE);

                        double discountAmount = 0.0; // Initialize discountAmount to 0.0
                        // Apply the discount if applicable
                        boolean discountGiven = false; // Flag to check if a discount is given
                        if (hours >= MINIMUM_PARKING_HOURS && hours >= DISCOUNT_THRESHOLD) {
                            discountAmount = parkingCharges * DISCOUNT_RATE;
                            parkingCharges -= discountAmount;
                            discountGiven = true; // Discount is given
                        }

                        // Display the exit time along with other information in the infoTextArea
                        String discountInfo = discountGiven ? "Discount Given: Yes" : "Discount Given: No";
                        String infoText = "\t======================\n"+"\t      PARKING RECEIPT\n"+"\t======================\n"+" "+
                                type + " Plate number: " + plateNo + "\n" +
                                " Slot No " + slotNumber + ".\n" +
                                " Entry Time: " + entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                                " Exit Time: " + exitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                                " Duration: " + hours + " hours " + minutes + " minutes\n" +" "+
                                discountInfo+"\n"+
                                " Discount Rate "+DISCOUNT_RATE+"\n"+
                                " Discount Amount: Rs " + String.format("%.2f", discountAmount) + "\n" +
                                " Total Parking Charges: Rs " + String.format("%.2f", parkingCharges) + "\n";


                        infoTextArea.setText(infoText);

                        // Insert data into the database
                        try (Connection connection = getConnection()) {
                            String insertQuery = "INSERT INTO parking_records (plate_no, vehicle_type, entry_time, exit_time, parking_charge) VALUES (?, ?, ?, ?, ?)";
                            // Create a PreparedStatement to insert data
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                            preparedStatement.setString(1, plateNo);
                            preparedStatement.setString(2, type);
                            preparedStatement.setTimestamp(3, Timestamp.valueOf(entryTime));
                            preparedStatement.setTimestamp(4, Timestamp.valueOf(exitTime));
                            preparedStatement.setDouble(5, parkingCharges);

                            preparedStatement.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            // Handle database errors
                        }

                        JOptionPane.showMessageDialog(MainController.this, type + " with plate number " + plateNo +
                                " has been removed from slot " + slotNumber + ".\n" +
                                "Parking Charges: Rs " + String.format("%.2f", parkingCharges), "Vehicle Removed", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Vehicle not found in the slot
                        JOptionPane.showMessageDialog(MainController.this, "No " + type + " with plate number " + plateNo +
                                " found in the slot.", "Vehicle Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
                updateSlotLabels();
            }
        });





        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plateNo = plateField.getText();
                String type = null;
                if (carRadio.isSelected()) {
                    type = "car";
                } else if (bikeRadio.isSelected()) {
                    type = "bike";
                }

                if (plateNo.isEmpty() || type == null) {
                    // Display an error message if plate number or vehicle type is missing
                    JOptionPane.showMessageDialog(MainController.this, "Please enter both plate number and vehicle type.",
                            "Plate Number or Vehicle Type Missing", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int slotNumber = -1; // Initialize to an invalid value

                if (type.equals("car")) {
                    for (int i = 0; i < carAvailableSlot; i++) {
                        if (carSlot[i] != null && carSlot[i].getPlateNumber().equals(plateNo)) {
                            slotNumber = i + 1; // Slot numbers are 1-based
                            break;
                        }
                    }
                    if (slotNumber == -1) {
                        JOptionPane.showMessageDialog(MainController.this, "Vehicle with plate number " + plateNo +
                                " was not found in any car slot.", "Vehicle Not Found", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainController.this,
                                "Vehicle with plate number " + plateNo + " is present in slot " + slotNumber + " (car).",
                                "Vehicle Found", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (type.equals("bike")) {
                    for (int i = 0; i < bikeAvailableSlot; i++) {
                        if (bikeSlot[i] != null && bikeSlot[i].getPlateNumber().equals(plateNo)) {
                            slotNumber = i + 1; // Slot numbers are 1-based
                            break;
                        }
                    }
                    if (slotNumber == -1) {
                        JOptionPane.showMessageDialog(MainController.this, "Vehicle with plate number " + plateNo +
                                " was not found in any bike slot.", "Vehicle Not Found", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainController.this,
                                "Vehicle with plate number " + plateNo + " is present in slot " + slotNumber + " (bike).",
                                "Vehicle Found", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Display an error message for an invalid vehicle type
                    JOptionPane.showMessageDialog(MainController.this, "Invalid vehicle type: " + type,
                            "Invalid Vehicle Type", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hide the current MainController frame
                dispose();
                // Open a new Login frame with the previous MainController frame reference
                new Login();
            }
        });

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the plateField text when the Pay button is clicked
                plateField.setText("");
                infoTextArea.setText(""); // Optionally, clear the infoTextArea as well
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields(); // This method clears the input fields
            }
        });


    }
    private void removeRowFromTable(String plateNo) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(plateNo)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }

    private void initComponents(){
        Font customLabel = new Font("Arial", Font.BOLD, 14);

       plateLabel = new JLabel("Plate No");                       // plate label
       plateLabel.setFont(customLabel);
       plateLabel.setBounds(20,13,100,30);
       add(plateLabel);

       plateField = new JTextField(10);                         // plate field
       plateField.setFont(new Font("Arial",Font.PLAIN,13));
       plateField.setBounds(100,13,240,30);
       add(plateField);

       typeLabel = new JLabel("Type");                             // type label
       typeLabel.setFont(customLabel);
       typeLabel.setBounds(20,50,100,30);
       add(typeLabel);

       carRadio = new JRadioButton("Car");                         // car radio button
       carRadio.setFont(customLabel);
       carRadio.setBounds(100,50,50,30);
       add(carRadio);

       bikeRadio = new JRadioButton("Bike");                     // bike radio button
       bikeRadio.setFont(customLabel);
       bikeRadio.setBounds(160,50,100,30);
       add(bikeRadio);

       ButtonGroup buttonGroup = new ButtonGroup();
       buttonGroup.add(carRadio);
       buttonGroup.add(bikeRadio);

       addButton = new JButton("Add");                       // add button
       addButton.setFont(new Font("Arial",Font.BOLD,13));
       addButton.setBounds(20,100,90,20);
       add(addButton);

       removeButton = new JButton("Remove");                         // remove button
       removeButton.setFont(new Font("Arial",Font.BOLD,13));
       removeButton.setBounds(120,100,90,20);
       add(removeButton);

       searchButton = new JButton("Search");                        // search button
       searchButton.setFont(new Font("Arial",Font.BOLD,13));
       searchButton.setBounds(220,100,90,20);
       add(searchButton);

       clearButton = new JButton("Clear");                          // clear button
       clearButton.setFont(new Font("Arial",Font.BOLD,13));
       clearButton.setBounds(20,130,90,20);
       add(clearButton);

       carLabel = new JLabel("Car");
       carLabel.setFont(customLabel);
       carLabel.setBounds(20,170,100,30);
       add(carLabel);

       bikeLabel = new JLabel("Bike");
       bikeLabel.setFont(customLabel);
       bikeLabel.setBounds(180,170,100,30);
       add(bikeLabel);

       carTlabel = new JLabel("Total Slot");                 // car total label
       carTlabel.setFont(new Font("Arial",Font.BOLD,13));
       carTlabel.setBounds(20,200,100,30);
       add(carTlabel);

       carTslot = new JLabel(String.valueOf(carAvailableSlot));         // car total slot
       carTslot.setFont(new Font("Arial",Font.BOLD,13));
       carTslot.setBounds(110,200,60,30);
       add(carTslot);

       bikeTlabel = new JLabel("Total Slot");                      // bike total label
       bikeTlabel.setFont(new Font("Arial",Font.BOLD,13));
       bikeTlabel.setBounds(180,200,100,30);
       add(bikeTlabel);

       bikeTslot = new JLabel(String.valueOf(bikeAvailableSlot));              // bike total slot
       bikeTslot.setFont(new Font("Arial",Font.BOLD,13));
       bikeTslot.setBounds(280,200,60,30);
       add(bikeTslot);

       carRlabel = new JLabel("Reserved");                       // car reserved label
       carRlabel.setFont(new Font("Arial",Font.BOLD,13));
       carRlabel.setBounds(20,230,100,30);
       add(carRlabel);

       carRslot = new JLabel("0");                             // car reserved slot
       carRslot.setFont(new Font("Arial",Font.BOLD,13));
       carRslot.setBounds(110,230,60,30);
       add(carRslot);

       bikeRlabel = new JLabel("Reserved");                  // bike reserved label
       bikeRlabel.setFont(new Font("Arial",Font.BOLD,13));
       bikeRlabel.setBounds(180,230,100,30);
       add(bikeRlabel);

       bikeRslot = new JLabel("0");                          // bike reserved slot
       bikeRslot.setFont(new Font("Arial",Font.BOLD,13));
       bikeRslot.setBounds(280,230,60,30);
       add(bikeRslot);

       carAlabel = new JLabel("Available");                  // car available label
       carAlabel.setFont(new Font("Arial",Font.BOLD,13));
       carAlabel.setBounds(20,260,100,30);
       add(carAlabel);

       carAslot = new JLabel(String.valueOf(carAvailableSlot));            // car available slot
       carAslot.setFont(new Font("Arial",Font.BOLD,13));
       carAslot.setBounds(110,260,60,30);
       add(carAslot);

       bikeAlabel = new JLabel("Available");                         // bike available label
       bikeAlabel.setFont(new Font("Arial",Font.BOLD,13));
       bikeAlabel.setBounds(180,260,100,30);
       add(bikeAlabel);

       bikeAslot = new JLabel(String.valueOf(bikeAvailableSlot));         // bike available slot
       bikeAslot.setFont(new Font("Arial",Font.BOLD,13));
       bikeAslot.setBounds(280,260,60,30);
       add(bikeAslot);

        // Create a DefaultTableModel with headers
        String[] tableHeaders = {"Type", "Plate Number", "Date","Entry Time","Slot"};
        tableModel = new DefaultTableModel(null, tableHeaders); // Initialize with no data

        // Create the JTable using the DefaultTableModel
        vehicleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBounds(20, 320, 740, 150); // Adjust the position and size as needed
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Add vertical scrolling
        add(scrollPane);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial",Font.BOLD,13));
        logoutButton.setBounds(680,10,80,20);
        add(logoutButton);

        infoTextArea = new JTextArea();
        infoTextArea.setBounds(420, 50, 340, 227); // Adjust position and size as needed
        infoTextArea.setFont(new Font("Arial",Font.PLAIN,13));
        infoTextArea.setEditable(true); // Make it non-editable
        add(infoTextArea);

        payButton = new JButton("Pay"); // Create a "Pay" button
        payButton.setFont(new Font("Arial",Font.BOLD,13));
        payButton.setBounds(420, 280, 60, 20); // Position the button below the infoTextArea
        add(payButton);

        ImageIcon image = new ImageIcon("C:\\java\\Vehicle_Parking\\src\\Vehicle_Parking\\admin2.png");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setBounds(600,10,image.getIconWidth(),image.getIconHeight());
        add(imageLabel);
    }
    private void clearFields(){
        plateField.setText("");
        carRadio.setSelected(false);
        bikeRadio.setSelected(false);
    }
    private boolean isPlateNumberPresent(String plateNo) {
        // Check if the plate number is already present in any car slot
        for (int i = 0; i < carAvailableSlot; i++) {
            if (carSlot[i] != null && carSlot[i].getPlateNumber().equals(plateNo)) {
                return true; // Plate number is already parked in a car slot
            }
        }

        // Check if the plate number is already present in any bike slot
        for (int i = 0; i < bikeAvailableSlot; i++) {
            if (bikeSlot[i] != null && bikeSlot[i].getPlateNumber().equals(plateNo)) {
                return true; // Plate number is already parked in a bike slot
            }
        }

        return false; // Plate number is not present in any slot
    }


    private void updateTable(String type, String plateNo, LocalDateTime entryTime, int slotNumber) {
       // Extract date and time from LocalDateTime
       String date = entryTime.toLocalDate().toString();
       String time = entryTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")); // Format as HH:mm

       // Add a new row to the table with separate date and time columns
       String[] rowData = {type, plateNo, date, time, String.valueOf(slotNumber)};
       tableModel.addRow(rowData);
   }
    private boolean isCarParked(String plateNo) {
        for (int i = 0; i < carAvailableSlot; i++) {
            if (carSlot[i] != null && carSlot[i].getPlateNumber().equals(plateNo)) {
                return true; // Car with the same plate number is already parked
            }
        }
        return false; // Car with the same plate number is not parked
    }

    private boolean isBikeParked(String plateNo) {
        for (int i = 0; i < bikeAvailableSlot; i++) {
            if (bikeSlot[i] != null && bikeSlot[i].getPlateNumber().equals(plateNo)) {
                return true; // Bike with the same plate number is already parked
            }
        }
        return false; // Bike with the same plate number is not parked
    }


    private boolean isCarSlotFull() {
        for (int i = 0; i < carAvailableSlot; i++) {
            if (carSlot[i] == null) {
                return false; // There is at least one empty slot
            }
        }
        return true; // All slots are occupied
    }

    private boolean isBikeSlotFull() {
        for (int i = 0; i < bikeAvailableSlot; i++) {
            if (bikeSlot[i] == null) {
                return false; // There is at least one empty slot
            }
        }
        return true; // All slots are occupied
    }
    private void updateSlotLabels() {
        // Calculate total, reserved, and available slots for cars and bikes
        int totalCarSlots = carAvailableSlot;
        int reservedCarSlots = 0;
        int totalBikeSlots = bikeAvailableSlot;
        int reservedBikeSlots = 0;

        for (int i = 0; i < carAvailableSlot; i++) {
            if (carSlot[i] != null) {
                reservedCarSlots++;
            }
        }

        for (int i = 0; i < bikeAvailableSlot; i++) {
            if (bikeSlot[i] != null) {
                reservedBikeSlots++;
            }
        }

        int availableCarSlots = totalCarSlots - reservedCarSlots;
        int availableBikeSlots = totalBikeSlots - reservedBikeSlots;

        // Update labels with the calculated values
        carTslot.setText(Integer.toString(totalCarSlots));
        carRslot.setText(Integer.toString(reservedCarSlots));
        carAslot.setText(Integer.toString(availableCarSlots));

        bikeTslot.setText(Integer.toString(totalBikeSlots));
        bikeRslot.setText(Integer.toString(reservedBikeSlots));
        bikeAslot.setText(Integer.toString(availableBikeSlots));
    }


}
/*public class Main{
    public static void main(String[] args) {
        try {
            // Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the exception if the driver class is not found
        }
        new MainController("testing");
    }




}

/*
*  to create the table Vehicle_info
* CREATE TABLE parking_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plate_no VARCHAR(10) NOT NULL,
    vehicle_type VARCHAR(10) NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME NOT NULL,
    parking_charge DECIMAL(10, 2) NOT NULL
);
* */
