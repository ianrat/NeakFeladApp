package neakfeladapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class UserManagementFrame extends JFrame {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JCheckBox showDeletedCheckbox;
    
    public UserManagementFrame() {
        setTitle("Felhasználó karbantartás");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Felső panel - eszköztár
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton addButton = new JButton("Új felhasználó");
        JButton editButton = new JButton("Szerkesztés");
        JButton deleteButton = new JButton("Törlés");
        JButton activateButton = new JButton("Aktiválás");
        
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(activateButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        JLabel searchLabel = new JLabel("Keresés:");
        searchField = new JTextField(20);
        showDeletedCheckbox = new JCheckBox("Törölt felhasználók mutatása", false);
        JButton refreshButton = new JButton("Frissítés");
        JButton closeButton = new JButton("Bezárás");
        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(showDeletedCheckbox);
        topPanel.add(refreshButton);
        topPanel.add(closeButton);
        
        // Táblázat
        String[] columnNames = {"ID", "Vezetéknév", "Keresztnév", "Login név", 
                                "Email", "Státusz", "Írási jog", "Hozzáférési szint", "Törölt"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5 || columnIndex == 6 || columnIndex == 8) {
                    return Boolean.class; // Checkbox megjelenítés
                }
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Oszlopszélességek
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Vezetéknév
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Keresztnév
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Login név
        table.getColumnModel().getColumn(4).setPreferredWidth(200);  // Email
        table.getColumnModel().getColumn(5).setPreferredWidth(80);   // Státusz
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Írási jog
        table.getColumnModel().getColumn(7).setPreferredWidth(120);  // Hozzáférési szint
        table.getColumnModel().getColumn(8).setPreferredWidth(80);   // Törölt
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Alsó panel
        JLabel statusLabel = new JLabel("Felhasználók száma: 0");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(statusLabel);
        
        // Panelek hozzáadása
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Keresés
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
                statusLabel.setText("Felhasználók száma: " + table.getRowCount());
            }
        });
        
        // Event handlerek
        
        // Új felhasználó
        addButton.addActionListener(e -> {
            UserEditDialog dialog = new UserEditDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadUsers();
                statusLabel.setText("Felhasználók száma: " + table.getRowCount());
            }
        });
        
        // Szerkesztés
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Kérem válasszon ki egy felhasználót!",
                    "Figyelmeztetés",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            
            UserEditDialog dialog = new UserEditDialog(this, userId);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadUsers();
                statusLabel.setText("Felhasználók száma: " + table.getRowCount());
            }
        });
        
        // Törlés (logikai)
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Kérem válasszon ki egy felhasználót!",
                    "Figyelmeztetés",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            String loginName = tableModel.getValueAt(modelRow, 3).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Biztosan törli a(z) '" + loginName + "' felhasználót?",
                "Törlés megerősítése",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                deleteUser(userId);
                loadUsers();
                statusLabel.setText("Felhasználók száma: " + table.getRowCount());
            }
        });
        
        // Aktiválás (törölt flag visszavonása)
        activateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Kérem válasszon ki egy felhasználót!",
                    "Figyelmeztetés",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int userId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            
            activateUser(userId);
            loadUsers();
            statusLabel.setText("Felhasználók száma: " + table.getRowCount());
        });
        
        // Törölt mutatása checkbox
        showDeletedCheckbox.addActionListener(e -> {
            loadUsers();
            statusLabel.setText("Felhasználók száma: " + table.getRowCount());
        });
        
        // Frissítés
        refreshButton.addActionListener(e -> {
            loadUsers();
            statusLabel.setText("Felhasználók száma: " + table.getRowCount());
        });
        
        // Bezárás
        closeButton.addActionListener(e -> dispose());
        
        // Dupla klikk szerkesztéshez
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editButton.doClick();
                }
            }
        });
        
        // Adatok betöltése
        loadUsers();
        statusLabel.setText("Felhasználók száma: " + table.getRowCount());
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        
        String sql = "SELECT ID, VEZETEK_NEV, KERESZT_NEV, LOGIN_NEV, EMAIL_CIM, " +
                     "STATUSZ, IRASI_JOG, HOZZAFERESI_SZINT, TOROLT " +
                     "FROM KOZP_DB.T_FELHASZNALOK ";
        
        if (!showDeletedCheckbox.isSelected()) {
            sql += "WHERE TOROLT = 0 ";
        }
        
        sql += "ORDER BY VEZETEK_NEV, KERESZT_NEV";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ID"),
                    rs.getString("VEZETEK_NEV"),
                    rs.getString("KERESZT_NEV"),
                    rs.getString("LOGIN_NEV"),
                    rs.getString("EMAIL_CIM"),
                    rs.getInt("STATUSZ") == 1,  // Boolean checkbox
                    rs.getInt("IRASI_JOG") == 1,
                    rs.getInt("HOZZAFERESI_SZINT"),
                    rs.getInt("TOROLT") == 1
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
    
    private void deleteUser(int userId) {
        String sql = "UPDATE KOZP_DB.T_FELHASZNALOK SET TOROLT = 1 WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Felhasználó sikeresen törölve!",
                "Siker",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba a törlés során:\n" + e.getMessage(),
                "Hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void activateUser(int userId) {
        String sql = "UPDATE KOZP_DB.T_FELHASZNALOK SET TOROLT = 0, STATUSZ = 1 WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Felhasználó sikeresen aktiválva!",
                "Siker",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az aktiválás során:\n" + e.getMessage(),
                "Hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}