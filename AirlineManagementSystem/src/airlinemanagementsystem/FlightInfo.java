package airlinemanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class FlightInfo extends JFrame {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/airlinemanagementsytem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "yash";
    
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JLabel titleLabel;
    private JPanel mainPanel;
    
    public FlightInfo() {
        setTitle("Flight Information System");
        setupUI();
        loadFlightData();
    }
    
    private void setupUI() {
        // Main panel setup
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title setup
        titleLabel = new JLabel("Flight Information Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Table setup
        flightTable = new JTable();
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        flightTable.setModel(tableModel);
        
        // Table styling
        flightTable.setRowHeight(25);
        flightTable.setFont(new Font("Arial", Font.PLAIN, 14));
        flightTable.setGridColor(new Color(200, 200, 200));
        flightTable.setShowGrid(true);
        flightTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Table header styling
        JTableHeader header = flightTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(51, 51, 51));
        header.setForeground(Color.WHITE);
        
        // Scroll pane setup
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Frame setup
        setContentPane(mainPanel);
        setSize(1000, 600);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void loadFlightData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM flight")) {
            
            // Get metadata and set up columns
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Clear existing columns and data
            tableModel.setColumnCount(0);
            tableModel.setRowCount(0);
            
            // Add columns
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(formatColumnName(metaData.getColumnName(i)));
            }
            
            // Add rows
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }
            
        } catch (SQLException e) {
            showErrorDialog("Database Error", "Failed to load flight data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String formatColumnName(String columnName) {
        // Convert database column names to more readable format
        return columnName.substring(0, 1).toUpperCase() + 
               columnName.substring(1).toLowerCase().replace("_", " ");
    }
    
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            FlightInfo flightInfo = new FlightInfo();
            flightInfo.setVisible(true);
        });
    }
}
