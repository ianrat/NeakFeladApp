/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author tarna
 */
public class MenuFrame extends JFrame {
    
    private String userName;
    
    public MenuFrame(String userName) {
        this.userName = userName;
        
        setTitle("Főmenü - " + userName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menüsor létrehozása
        JMenuBar menuBar = new JMenuBar();
        
        // Fájl menü
        JMenu fileMenu = new JMenu("Fájl");
        JMenuItem exitItem = new JMenuItem("Kilépés");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Adatok menü
        JMenu dataMenu = new JMenu("Adatok");
        JMenuItem listItem = new JMenuItem("Listázás");
        JMenuItem callProcItem = new JMenuItem("Időszaki adatok feladása");
            callProcItem.addActionListener(e -> {
                ProcedureCallFrame procFrame = new ProcedureCallFrame();
                procFrame.setVisible(true);
            });
        
        listItem.addActionListener(e -> showMessage("Listázás funkció"));
        callProcItem.addActionListener(e -> showMessage("Feladás funkció"));
        
        dataMenu.add(listItem);
        dataMenu.add(callProcItem);
        
        // Súgó menü
        JMenu helpMenu = new JMenu("Súgó");
        JMenuItem aboutItem = new JMenuItem("Névjegy");
        aboutItem.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "Oracle Adatbázis Alkalmazás\nVerziószám: 1.0\nFelhasználó: " + userName,
                "Névjegy",
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        
        // Főpanel gomokkal
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btn1 = new JButton("Adatok listázása");
                            btn1.addActionListener(e -> {
                            NaploListFrame listFrame = new NaploListFrame();
                            listFrame.setVisible(true);
                    });
        JButton btn2 = new JButton("Időszaki adatok feladása");
        btn2.addActionListener(e -> {
            ProcedureCallFrame procFrame = new ProcedureCallFrame();
            procFrame.setVisible(true);
        });                            
        JButton btn3 = new JButton("Keresés");
        JButton btn4 = new JButton("Kijelentkezés");
        

        btn3.addActionListener(e -> showMessage("Keresés az adatbázisban"));
        btn4.addActionListener(e -> logout());
        
        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(btn4);
        
        add(panel);
        
        // Üdvözlő üzenet
        JLabel welcomeLabel = new JLabel("Üdvözöljük, " + userName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            message + "\n(Ez a funkció még nincs implementálva)", 
            "Információ", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Biztosan ki szeretne jelentkezni?", 
            "Kijelentkezés", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
