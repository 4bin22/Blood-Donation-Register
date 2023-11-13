import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class MainMenu {
    MainMenu() {
        JFrame mainFrame = new JFrame("Blood Donation");
        JButton registerButton = new JButton("Register");
        JButton findDonorButton = new JButton("Find Donor");

        registerButton.setBounds(200, 100, 100, 50);
        findDonorButton.setBounds(200, 200, 100, 50);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                new Blood();
            }
        });

        findDonorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
                new FindDonor();
            }
        });

        mainFrame.add(registerButton);
        mainFrame.add(findDonorButton);
        mainFrame.setSize(600, 600);
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static void main(String args[]) {
        new MainMenu();
    }
}

class Blood {
    JLabel j1, j2, j3, j4, j5, j6;
    JTextField tf1, tf2, tf3, tf4;
    JButton b1;
    JRadioButton r1, r2;
    JComboBox<String> comboBox;

    Blood() {
        JFrame f = new JFrame("Blood Donation Campaigning");
        j1 = new JLabel("Blood Donation Campaign");
        j1.setBounds(300, 30, 1000, 100);
        j1.setForeground(Color.RED);
        j1.setFont(new Font("Serif", Font.PLAIN, 50));
        j1.setHorizontalAlignment(JTextField.CENTER);

        j2 = new JLabel("Name:");
        j2.setBounds(50, 130, 100, 20);

        j3 = new JLabel("Age:");
        j3.setBounds(50, 180, 100, 20);

        j4 = new JLabel("Sex:");
        j4.setBounds(50, 230, 100, 20);

        j5 = new JLabel("Blood group:");
        j5.setBounds(50, 280, 100, 20);

        j6 = new JLabel("Contact no:");
        j6.setBounds(50, 330, 100, 20);

        tf1 = new JTextField("");
        tf1.setBounds(125, 130, 150, 20);

        tf2 = new JTextField("");
        tf2.setBounds(125, 180, 150, 20);

        tf4 = new JTextField("");
        tf4.setBounds(125, 330, 150, 20);

        b1 = new JButton("Register");
        b1.setBounds(500, 410, 100, 50);
        b1.setEnabled(false);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerDonor();
            }
        });

        r1 = new JRadioButton("Male");
        r1.setBounds(125, 230, 150, 20);
        r2 = new JRadioButton("Female");
        r2.setBounds(280, 230, 150, 20);
        ButtonGroup group = new ButtonGroup();
        group.add(r1);
        group.add(r2);

        String[] bloodGroups = {"~", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        comboBox = new JComboBox<>(bloodGroups);
        comboBox.setBounds(125, 280, 150, 20);

        tf1.getDocument().addDocumentListener(new TextFieldDocumentListener());
        tf2.getDocument().addDocumentListener(new TextFieldDocumentListener());
        tf4.getDocument().addDocumentListener(new TextFieldDocumentListener());

        f.add(j1);
        f.add(j2);
        f.add(j3);
        f.add(j4);
        f.add(j5);
        f.add(j6);
        f.add(tf1);
        f.add(tf2);
        f.add(tf4);
        f.add(b1);
        f.add(r1);
        f.add(r2);
        f.add(comboBox);
        f.setSize(1200, 600);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    class TextFieldDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            updateButtonEnabledState();
        }

        public void removeUpdate(DocumentEvent e) {
            updateButtonEnabledState();
        }

        public void changedUpdate(DocumentEvent e) {
            updateButtonEnabledState();
        }
    }

    private void updateButtonEnabledState() {
        if (tf1.getText().isEmpty() || tf2.getText().isEmpty() || tf4.getText().isEmpty()) {
            b1.setEnabled(false);
        } else {
            b1.setEnabled(true);
        }
    }

    private void registerDonor() {
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
            String name = tf1.getText();
            int age = Integer.parseInt(tf2.getText());
            String sex = r1.isSelected() ? "Male" : "Female";
            String bloodGroup = comboBox.getSelectedItem().toString();
            String contactNo = tf4.getText();

            String sql = "INSERT INTO Donors_list (Name, Age, Sex, Blood_group, Phn_no) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, sex);
            statement.setString(4, bloodGroup);
            statement.setString(5, contactNo);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registered Successfully...\nThank you for being a part of Saving lives",
                    "Thanks Note", JOptionPane.INFORMATION_MESSAGE);

            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Blood();
    }
}

class FindDonor {
    JComboBox<String> bloodGroupComboBox;
    JTextArea donorDetailsTextArea;
    JButton searchButton;

    FindDonor() {
        JFrame frame = new JFrame("Find Donor");
        frame.setLayout(null);

        JLabel label = new JLabel("Select Blood Group:");
        label.setBounds(50, 50, 150, 20);

        String[] bloodGroups = {"~","A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodGroupComboBox = new JComboBox<>(bloodGroups);
        bloodGroupComboBox.setBounds(200, 50, 150, 20);

        searchButton = new JButton("Search");
        searchButton.setBounds(400, 50, 100, 20);

        donorDetailsTextArea = new JTextArea();
        donorDetailsTextArea.setBounds(50, 100, 500, 300);
        donorDetailsTextArea.setEditable(false);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bloodGroup = bloodGroupComboBox.getSelectedItem().toString();
                String details = getDonorDetails(bloodGroup);
                donorDetailsTextArea.setText(details);
            }
        });

        frame.add(label);
        frame.add(bloodGroupComboBox);
        frame.add(searchButton);
        frame.add(donorDetailsTextArea);
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private String getDonorDetails(String bloodGroup) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        StringBuilder details = new StringBuilder();

        try {
            connection = DatabaseConnector.getConnection();
            String sql = "SELECT * FROM Donors_list WHERE Blood_group = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, bloodGroup);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                int age = resultSet.getInt("Age");
                String sex = resultSet.getString("Sex");
                String contactNo = resultSet.getString("Phn_no");

                details.append("Name: ").append(name).append("\n");
                details.append("Age: ").append(age).append("\n");
                details.append("Sex: ").append(sex).append("\n");
                details.append("Contact No: ").append(contactNo).append("\n");
                details.append("-------------------------------------\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return details.toString();
    }
}

class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/Blood_Donation";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
