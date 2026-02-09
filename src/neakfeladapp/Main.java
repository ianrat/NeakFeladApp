/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import javax.swing.*;
/**
 *
 * @author tarna
 */
public class Main {
    
    public static void main(String[] args) {
        // Swing alkalmazás indítása az Event Dispatch Thread-en
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            }
        });
    }    
}
