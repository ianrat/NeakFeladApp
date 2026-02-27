/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author tk
 */
public class ProcedureCallFrame extends JFrame {
    
    private JComboBox<ComboItem> intezmenyCombo;
    private JTextField idoszakField;
    private JTextField megjField;
    private JTextArea resultArea;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public ProcedureCallFrame() {
        setTitle("Havi adatok feladása");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Felső panel - választás
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Paraméterek"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Intézmény választó
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Intézmény:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        intezmenyCombo = new JComboBox<>();
        intezmenyCombo.setPreferredSize(new Dimension(300, 25));
        inputPanel.add(intezmenyCombo, gbc);
        
        // Időszak
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Időszak:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        idoszakField = new JTextField("2025");
        idoszakField.setPreferredSize(new Dimension(100, 25));
        inputPanel.add(idoszakField, gbc);
 
        // Megj
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Megjegyzés:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;      
        megjField = new JTextField("");
        megjField.setPreferredSize(new Dimension(100, 25));
        inputPanel.add(megjField, gbc);
        
        // Gombok panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton executeButton = new JButton("Indítás");
        //JButton refreshButton = new JButton("Lista frissítése");
        JButton closeButton = new JButton("Bezárás");
        
        buttonPanel.add(executeButton);
        //buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        inputPanel.add(buttonPanel, gbc);
        
        // Eredmény panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Eredmény"));
        
        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        JScrollPane scrollPanel = new JScrollPane(table);

        // Panelek hozzáadása
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event handlerek
        executeButton.addActionListener(e -> executeProcedure());
        //refreshButton.addActionListener(e -> loadAeekInt());
        closeButton.addActionListener(e -> dispose());
        
        // Adatok betöltése
        loadAeekInt();
        loadNaplo();
    }
    
    private void loadAeekInt() {
        intezmenyCombo.removeAllItems();
        
        String sql = "SELECT id, nev, nev_2, oepkod FROM astar_bsoft_kon.t_aeek_int WHERE id in (26,27,36,36,39,65,98,109,113) ORDER BY nev_2";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nev = rs.getString("nev");
                String nev_2 = rs.getString("nev_2");
                String oepkod = rs.getString("oepkod");
                
                
                String displayText = nev + " - " + nev_2 + " (" + oepkod + ")";
                
                intezmenyCombo.addItem(new ComboItem(id, displayText));
            }
            
            //resultArea.append("✓ Intézmnyek betöltve: " + intezmenyCombo.getItemCount() + " fő\n");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void executeProcedure() {
        // Kiválasztott alkalmazott
        ComboItem selectedItem = (ComboItem) intezmenyCombo.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                "Kérem válasszon egy intézményt!",
                "Figyelmeztetés",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = selectedItem.getId();
        
        // Emelés százalék
        String idoszak, megj;
   
        idoszak = idoszakField.getText().trim();
        megj = megjField.getText().trim();
   
        
        // Procedure hívása
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{call neak_felad.felad_java_hiv(?, ?, ?)}")) {
            
            // IN paraméterek
            cstmt.setInt(1, id);
            cstmt.setString(2, idoszak);
            cstmt.setString(3, megj);
            
           
            // Végrehajtás
            cstmt.execute();
            
            // Eredmény lekérése
            
            resultArea.append("\n=== Végrehajtás ===\n");
            resultArea.append("Intezmeny: " + id + "\n");
            resultArea.append("Időszak: " + idoszak + "%\n");
            resultArea.append("Megjegyzés: " + megj + "%\n");
            resultArea.append("Feladás lefutott\n");
            resultArea.append("==================\n");
            
            // Lista frissítése fölösleges
            //loadAeekInt();
            // Adatok betöltése
            loadNaplo();
            
            JOptionPane.showMessageDialog(this,
                idoszak,
                "Sikeres végrehajtás",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            resultArea.append("\n!!! HIBA !!!\n" + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                "Hiba a procedure hívása során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Belső osztály a ComboBox elemekhez
    private static class ComboItem {
        private final int id;
        private final String displayText;
        
        public ComboItem(int id, String displayText) {
            this.id = id;
            this.displayText = displayText;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
    private void loadNaplo() {
        // Táblázat ürítése
        tableModel.setRowCount(0);
        
        String sql = "SELECT naplo_id, korhaz_kod, ev, ho, felad_dt, megj FROM neak_felad.naplo ORDER BY naplo_id desc FETCH FIRST 5 ROWS ONLY";
        
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
