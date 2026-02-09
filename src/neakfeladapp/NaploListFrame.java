package neakfeladapp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author tarna
 */
    
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class NaploListFrame extends JFrame {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    
    public NaploListFrame() {
        setTitle("Feladási napló");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Felső panel - keresés és gombok
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel searchLabel = new JLabel("Keresés:");
        searchField = new JTextField(20);
        JButton refreshButton = new JButton("Frissítés");
        JButton closeButton = new JButton("Bezárás");
        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(refreshButton);
        topPanel.add(closeButton);
        
        // Táblázat létrehozása
        String[] columnNames = {"ID", "Kórházkód", "Év", "Hó", "Dátum", "Megj"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Táblázat nem szerkeszthető
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); // Oszlopokra kattintva rendezés
        
        // Oszlopszélességek beállítása
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100);  // Kórházkód
        table.getColumnModel().getColumn(2).setPreferredWidth(40);  // Év
        table.getColumnModel().getColumn(3).setPreferredWidth(40);  // Hó
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Dátum
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // megj
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Alsó panel - statisztika
        JLabel statusLabel = new JLabel("Sorok száma: 0");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(statusLabel);
        
        // Panelek hozzáadása
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Keresés funkció
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
                statusLabel.setText("Sorok száma: " + table.getRowCount());
            }
        });
        
        // Frissítés gomb
        refreshButton.addActionListener(e -> {
            loadEmployees();
            statusLabel.setText("Sorok száma: " + table.getRowCount());
        });
        
        // Bezárás gomb
        closeButton.addActionListener(e -> dispose());
        
        // Adatok betöltése
        loadEmployees();
        statusLabel.setText("Sorok száma: " + table.getRowCount());
    }
    
    private void loadEmployees() {
        // Táblázat ürítése
        tableModel.setRowCount(0);
        
        String sql = "SELECT naplo_id, korhaz_kod, ev, ho, felad_dt, megj FROM neak_felad.naplo ORDER BY naplo_id desc";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("naplo_id"),
                    rs.getString("korhaz_kod"),
                    rs.getString("ev"),
                    rs.getString("ho"),
                    rs.getDate("felad_dt"),
                    rs.getString("megj")
                    
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
