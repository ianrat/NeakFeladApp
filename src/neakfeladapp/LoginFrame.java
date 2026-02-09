/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author tarna
 */
public class LoginFrame extends JFrame {
private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginFrame() {
        setTitle("Bejelentkezés");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel létrehozása
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Felhasználónév
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Felhasználónév:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);
        
        // Jelszó
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Jelszó:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);
        
        // Belépés gomb
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Belépés");
        panel.add(loginButton, gbc);
        
        add(panel);
        
        // Gomb eseménykezelő
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Enter billentyű kezelése
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Integer auth = 0;
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Kérem töltse ki az összes mezőt!", 
                "Hiányzó adatok", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Adatbázis ellenőrzés
        try (Connection conn = DatabaseConnection.getConnection();
                
            CallableStatement cstmt = conn.prepareCall("{? = call kvikmod.felhasznalo_auth(?,?)}")) {
            
            // 1. paraméter: VISSZATÉRÉSI ÉRTÉK regisztrálása
            cstmt.registerOutParameter(1, Types.INTEGER);
            
            // 2. paraméter: IN paraméter beállítása
            cstmt.setString(2, username);
            cstmt.setString(3, password);
            
            // Function végrehajtása
            cstmt.execute();
            
            // Visszatérési érték lekérése
            auth = cstmt.getInt(1);               
            
            if (auth == 1) {
                try (Connection conn2 = DatabaseConnection.getConnection();
                
                    CallableStatement cstmt2 = conn2.prepareCall("{? = call kvikmod.felhasznalo_nev(?)}")) {
            
                    // 1. paraméter: VISSZATÉRÉSI ÉRTÉK regisztrálása
                    cstmt2.registerOutParameter(1, Types.VARCHAR);
            
                    // 2. paraméter: IN paraméter beállítása
                    cstmt2.setString(2, username);
            
                    // Function végrehajtása
                    cstmt2.execute();
            
                    // Visszatérési érték lekérése
                    String fullName = cstmt2.getString(1);      ;            
                
                    JOptionPane.showMessageDialog(this, 
                        "Sikeres bejelentkezés!\nÜdvözöljük: " + fullName, 
                        "Siker", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                    // Menürendszer megnyitása
                    this.dispose();
                    new MenuFrame(fullName).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Adatbázis hiba: " + ex.getMessage(), 
                        "Hiba", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }   

            } else {
                JOptionPane.showMessageDialog(this, 
                    "Hibás felhasználónév vagy jelszó!", 
                    "Hiba", 
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Adatbázis hiba: " + ex.getMessage(), 
                "Hiba", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }    
}
