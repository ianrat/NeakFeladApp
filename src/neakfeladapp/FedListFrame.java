/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

/**
 *
 * @author tarna
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class FedListFrame extends JFrame {
    
    private JTable table;
    private JComboBox<ComboItem> intezmenyCombo;
    private JComboBox<ComboItem> naploCombo;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    
    public FedListFrame() {
        setTitle("Feladási napló");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
     
        // Felső panel - keresés és gombok
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Intézmény választó

        JLabel intezmenyLabel = new JLabel("Intézmény:");
        intezmenyCombo = new JComboBox<>();
        intezmenyCombo.setPreferredSize(new Dimension(300, 25));
   
        // Napló választó

        JLabel naploLabel = new JLabel("Napló:");
        naploCombo = new JComboBox<>();
        naploCombo.setPreferredSize(new Dimension(300, 25));
        
        JLabel searchLabel = new JLabel("Keresés:");
        searchField = new JTextField(20);
        JButton refreshButton = new JButton("Frissítés");
        JButton closeButton = new JButton("Bezárás");
        
        topPanel.add(intezmenyLabel);
        topPanel.add(intezmenyCombo);
        topPanel.add(naploLabel);
        topPanel.add(naploCombo);        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(refreshButton);
        topPanel.add(closeButton);
        
        // Táblázat létrehozása
        String[] columnNames = {"Bev/Kiad", "Kód", "Név", "Fed.kód", "Fed.név", "Mennyiség", "Megys", "Egységár", "Érték"};
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
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // Bevét/kiadás
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Kód
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // NÉv
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // fakód
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // fanév
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // menny
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // megys
        table.getColumnModel().getColumn(7).setPreferredWidth(100);  // egységár
        table.getColumnModel().getColumn(8).setPreferredWidth(100);  // ertek
     
                        
        NumberRenderer numberRenderer = new NumberRenderer();

        table.getColumnModel().getColumn(5).setCellRenderer(numberRenderer); // Mennyiség
        table.getColumnModel().getColumn(7).setCellRenderer(numberRenderer); // Egységár
        table.getColumnModel().getColumn(8).setCellRenderer(numberRenderer); // Érték
        
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
            loadReport();
            statusLabel.setText("Sorok száma: " + table.getRowCount());
        });
        
        // Bezárás gomb
        closeButton.addActionListener(e -> dispose());
        
        // Adatok betöltése
        loadAeekInt();
        loadNaplo();
        loadReport();
        statusLabel.setText("Sorok száma: " + table.getRowCount());
        
    }
    private void loadAeekInt() {
        intezmenyCombo.removeAllItems();
        intezmenyCombo.addActionListener(e -> loadNaplo());
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
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void loadNaplo() {
        naploCombo.removeAllItems();
        
        ComboItem selectedAeekInt = (ComboItem) intezmenyCombo.getSelectedItem();
        if (selectedAeekInt == null || selectedAeekInt.getId() == 0) {
            naploCombo.setEnabled(false);
            return;
        }
        
        int AeekIntId = selectedAeekInt.getId();
        String AeekIntKod = null;
        String sql = "SELECT oepkod FROM astar_bsoft_kon.t_aeek_int WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, AeekIntId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                AeekIntKod = rs.getString("oepkod");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }        
        
        sql = "SELECT naplo_id, korhaz_kod, ev, ho, felad_dt, megj FROM neak_felad.naplo WHERE korhaz_kod = ? ORDER BY naplo_id desc";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {;
             
             pstmt.setString(1, AeekIntKod);   
             ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("naplo_id");
                String ev = rs.getString("ev");
                String ho = rs.getString("ho");
                Date felad_dt = rs.getDate("felad_dt");
                String megj = rs.getString("megj");
                
                
                String displayText = id + " - " + ev +  ho + " (" +  felad_dt + ") " + megj ;
                
                naploCombo.addItem(new ComboItem(id, displayText));
            }
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }    
    private void loadReport() {
        // Táblázat ürítése
        tableModel.setRowCount(0);
        
        ComboItem selectedNaplo = (ComboItem) naploCombo.getSelectedItem();
        int NaploId = selectedNaplo.getId();
        
        String sql = "select t.bevet_kiadas, fed.kod, fed.nev, t.fedezet_torzs_kod fa_kod, fed.fedezet_nev fa_nev, \n" +
"            sum(case when t.bevet_kiadas = 2 then null else nvl(t.mennyiseg,0) end) menny, \n" +
"            case when t.bevet_kiadas = 2 then null else fed.fedezet_megys end megys, \n" +
"            case when t.bevet_kiadas = 2 or sum(nvl(t.mennyiseg,0)) = 0 then null else sum(nvl(t.netto_ertek,0) + nvl(t.nemgy_netto,0)) / sum(nvl(t.mennyiseg,0)) end egysegar,\n" +
"            sum(nvl(t.netto_ertek,0) + nvl(t.nemgy_netto,0)) * case when t.bevet_kiadas = 1 then 1 else -1 end ertek\n" +
"            from neak_felad.eset_fej f\n" +
"                left join neak_felad.eset_tetel t on t.eset_fej_id = f.eset_fej_id\n" +
"                inner join neak_felad.naplo n on n.naplo_id = f.naplo_id\n" +
"                inner join neak_felad.t_fedezet fed on fed.naplo_id = f.naplo_id and fed.fedezet_kod = t.fedezet_torzs_kod\n" +
"\n" +
"            where f.naplo_id = ?\n" +
"            group by t.bevet_kiadas, fed.kod, fed.nev, t.fedezet_torzs_kod, fed.fedezet_nev, fed.fedezet_megys\n" +
"            order by 1,2,3";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, NaploId);
             ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("bevet_kiadas"),
                    rs.getString("kod"),
                    rs.getString("nev"),
                    rs.getString("fa_kod"),
                    rs.getString("fa_nev"),
                    rs.getDouble("menny"),
                    rs.getString("megys"),
                    rs.getDouble("egysegar"),
                    rs.getDouble("ertek")
                    
                };
                tableModel.addRow(row);
            }
            // Összesen
            double total = 0.0;

            // Érték oszlop összegzése (7. oszlop)
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Object value = tableModel.getValueAt(i, 8);
                if (value instanceof Number) {
                    total += ((Number) value).doubleValue();
                }
            }

            // Összesen sor hozzáadása
            Object[] totalRow = {"", "", "", "", "", "", "", "ÖSSZESEN:", total};
            tableModel.addRow(totalRow);              
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
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
