package neakfeladapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserEditDialog extends JDialog {
    
    private JTextField vezetekNevField;
    private JTextField keresztNevField;
    private JTextField loginNevField;
    private JTextField emailField;
    private JPasswordField jelszoField;
    private JPasswordField jelszoConfirmField;
    private JCheckBox statuszCheckbox;
    private JCheckBox irasiJogCheckbox;
    private JSpinner hozzaferesiSzintSpinner;
    private JComboBox<String> nyelvCombo;
    private JTextArea megjArea;
    private JComboBox<ComboItem> aeekIntCombo;
    
    private Integer userId;  // null = új, érték = szerkesztés
    private boolean saved = false;
    
    public UserEditDialog(Frame parent, Integer userId) {
        super(parent, userId == null ? "Új felhasználó" : "Felhasználó szerkesztése", true);
        this.userId = userId;
        
        setSize(500, 650);
        setLocationRelativeTo(parent);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Vezetéknév
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Vezetéknév:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        vezetekNevField = new JTextField(20);
        formPanel.add(vezetekNevField, gbc);
        row++;
        
        // Keresztnév
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Keresztnév:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        keresztNevField = new JTextField(20);
        formPanel.add(keresztNevField, gbc);
        row++;
        
        // Login név
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Login név: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        loginNevField = new JTextField(20);
        formPanel.add(loginNevField, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        row++;
        
        // Jelszó
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        String jelszoLabel = (userId == null) ? "Jelszó: *" : "Új jelszó: (üresen hagyva nem változik)";
        formPanel.add(new JLabel(jelszoLabel), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        jelszoField = new JPasswordField(20);
        formPanel.add(jelszoField, gbc);
        row++;
        
        // Jelszó megerősítés
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Jelszó megerősítés:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        jelszoConfirmField = new JPasswordField(20);
        formPanel.add(jelszoConfirmField, gbc);
        row++;
        
        // Státusz checkbox
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Aktív:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        statuszCheckbox = new JCheckBox();
        statuszCheckbox.setSelected(true);
        formPanel.add(statuszCheckbox, gbc);
        row++;
        
        // Írási jog checkbox
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Írási jog:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        irasiJogCheckbox = new JCheckBox();
        formPanel.add(irasiJogCheckbox, gbc);
        row++;
        
        // Hozzáférési szint
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Hozzáférési szint:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        hozzaferesiSzintSpinner = new JSpinner(new SpinnerNumberModel(40, 0, 100, 1));
        formPanel.add(hozzaferesiSzintSpinner, gbc);
        row++;
        
        // Nyelv
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Nyelv:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nyelvCombo = new JComboBox<>(new String[]{"en-gb", "hu", "ro"});
        formPanel.add(nyelvCombo, gbc);
        row++;
        
        // AEEK intézmény
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("AEEK intézmény:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        aeekIntCombo = new JComboBox<>();
        formPanel.add(aeekIntCombo, gbc);
        row++;
        
        // Megjegyzés
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Megjegyzés:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        megjArea = new JTextArea(3, 20);
        megjArea.setLineWrap(true);
        megjArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(megjArea);
        formPanel.add(scrollPane, gbc);
        
        // Gombok panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Mentés");
        JButton cancelButton = new JButton("Mégse");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Panelek hozzáadása
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Event handlerek
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());
        
        // Adatok betöltése
        loadAeekInt();
        
        if (userId != null) {
            loadUserData();
        }
    }
    
    private void loadAeekInt() {
        aeekIntCombo.addItem(new ComboItem(0, "-- Nincs --"));
        
        String sql = "SELECT id, oepkod FROM astar_bsoft_kon.t_aeek_int ORDER BY oepkod";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String kod = rs.getString("oepkod");
                aeekIntCombo.addItem(new ComboItem(id, kod));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUserData() {
        String sql = "SELECT * FROM KOZP_DB.T_FELHASZNALOK WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                vezetekNevField.setText(rs.getString("VEZETEK_NEV"));
                keresztNevField.setText(rs.getString("KERESZT_NEV"));
                loginNevField.setText(rs.getString("LOGIN_NEV"));
                loginNevField.setEnabled(false);  // Login név ne legyen módosítható
                emailField.setText(rs.getString("EMAIL_CIM"));
                statuszCheckbox.setSelected(rs.getInt("STATUSZ") == 1);
                irasiJogCheckbox.setSelected(rs.getInt("IRASI_JOG") == 1);
                hozzaferesiSzintSpinner.setValue(rs.getInt("HOZZAFERESI_SZINT"));
                nyelvCombo.setSelectedItem(rs.getString("NYELV"));
                megjArea.setText(rs.getString("MEGJ"));
                
                int aeekIntId = rs.getInt("AEEK_INT_ID");
                if (!rs.wasNull()) {
                    for (int i = 0; i < aeekIntCombo.getItemCount(); i++) {
                        if (((ComboItem) aeekIntCombo.getItemAt(i)).getId() == aeekIntId) {
                            aeekIntCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void saveUser() {
        // Validáció
        String loginNev = loginNevField.getText().trim();
        if (loginNev.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "A login név megadása kötelező!",
                "Hiányzó adat",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String jelszo = new String(jelszoField.getPassword());
        String jelszoConfirm = new String(jelszoConfirmField.getPassword());
        
        if (userId == null && jelszo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Új felhasználónál a jelszó megadása kötelező!",
                "Hiányzó adat",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!jelszo.isEmpty() && !jelszo.equals(jelszoConfirm)) {
            JOptionPane.showMessageDialog(this,
                "A két jelszó nem egyezik!",
                "Hiba",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            if (userId == null) {
                // Új felhasználó
                insertUser(conn, loginNev, jelszo);
            } else {
                // Meglévő felhasználó szerkesztése
                updateUser(conn, loginNev, jelszo);
            }
            
            saved = true;
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba a mentés során:\n" + e.getMessage(),
                "Hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void insertUser(Connection conn, String loginNev, String jelszo) throws SQLException {
        String sql = "INSERT INTO KOZP_DB.T_FELHASZNALOK " +
                     "(ID, VEZETEK_NEV, KERESZT_NEV, LOGIN_NEV, EMAIL_CIM, JELSZO, " +
                     "STATUSZ, IRASI_JOG, HOZZAFERESI_SZINT, NYELV, MEGJ, AEEK_INT_ID, TOROLT) " +
                     "VALUES (KOZP_DB.SEQ_FELHASZNALOK.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vezetekNevField.getText().trim());
            pstmt.setString(2, keresztNevField.getText().trim());
            pstmt.setString(3, loginNevField.getText().trim());
            pstmt.setString(4, emailField.getText().trim());
            pstmt.setString(5, hashPassword(loginNev, jelszo));  // Hash-eld a jelszót!
            pstmt.setInt(6, statuszCheckbox.isSelected() ? 1 : 0);
            pstmt.setInt(7, irasiJogCheckbox.isSelected() ? 1 : 0);
            pstmt.setInt(8, (Integer) hozzaferesiSzintSpinner.getValue());
            pstmt.setString(9, (String) nyelvCombo.getSelectedItem());
            pstmt.setString(10, megjArea.getText().trim());
            
            ComboItem selectedAeek = (ComboItem) aeekIntCombo.getSelectedItem();
            if (selectedAeek != null && selectedAeek.getId() != 0) {
                pstmt.setInt(11, selectedAeek.getId());
            } else {
                pstmt.setNull(11, Types.NUMERIC);
            }
            
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Felhasználó sikeresen létrehozva!",
                "Siker",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateUser(Connection conn, String loginNev, String jelszo) throws SQLException {
        String sql = "UPDATE KOZP_DB.T_FELHASZNALOK SET " +
                     "VEZETEK_NEV = ?, KERESZT_NEV = ?, EMAIL_CIM = ?, " +
                     "STATUSZ = ?, IRASI_JOG = ?, HOZZAFERESI_SZINT = ?, " +
                     "NYELV = ?, MEGJ = ?, AEEK_INT_ID = ? ";
        
        if (!jelszo.isEmpty()) {
            sql += ", JELSZO = ? ";
        }
        
        sql += "WHERE ID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            
            pstmt.setString(paramIndex++, vezetekNevField.getText().trim());
            pstmt.setString(paramIndex++, keresztNevField.getText().trim());
            pstmt.setString(paramIndex++, emailField.getText().trim());
            pstmt.setInt(paramIndex++, statuszCheckbox.isSelected() ? 1 : 0);
            pstmt.setInt(paramIndex++, irasiJogCheckbox.isSelected() ? 1 : 0);
            pstmt.setInt(paramIndex++, (Integer) hozzaferesiSzintSpinner.getValue());
            pstmt.setString(paramIndex++, (String) nyelvCombo.getSelectedItem());
            pstmt.setString(paramIndex++, megjArea.getText().trim());
            
            ComboItem selectedAeek = (ComboItem) aeekIntCombo.getSelectedItem();
            if (selectedAeek != null && selectedAeek.getId() != 0) {
                pstmt.setInt(paramIndex++, selectedAeek.getId());
            } else {
                pstmt.setNull(paramIndex++, Types.NUMERIC);
            }
            
            if (!jelszo.isEmpty()) {
                pstmt.setString(paramIndex++, hashPassword(loginNev, jelszo));
            }
            
            pstmt.setInt(paramIndex, userId);
            
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Felhasználó sikeresen módosítva!",
                "Siker",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Egyszerű jelszó hash (élesben használj BCrypt vagy hasonlót!)
    private String hashPassword(String loginName, String password) {
        // FIGYELEM: Ez csak példa! Éles környezetben használj BCrypt-et!
        String encodedPw = null;
        String sql = "SELECT kvikmod.FELHASZNALO_PKG.jelszo_encode(?,?) encodedpw FROM dual";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loginName);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                encodedPw = rs.getString("encodedpw");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return encodedPw;
    }   
    
    public boolean isSaved() {
        return saved;
    }
    
    // ComboItem osztály
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
}