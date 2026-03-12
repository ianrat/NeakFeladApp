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
import java.util.List;
import java.util.ArrayList;

public class NaploListFrame extends JFrame {
    
    private JTable table;
    private JComboBox<ComboItem> intezmenyCombo;    
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JSpinner feladDtSpinner;
    private JCheckBox feladDtCheck; // opcionálissá tételhez
    
    public NaploListFrame() {
        setTitle("Feladási napló");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Főpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Felső panel - 2 soros elrendezés
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // 1. sor: Intézmény + Feladás dátuma
        JPanel row1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JLabel intezmenyLabel = new JLabel("Intézmény:");
        intezmenyCombo = new JComboBox<>();

        feladDtSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(feladDtSpinner, "yyyy.MM.dd.");
        feladDtSpinner.setEditor(editor);
        feladDtSpinner.setEnabled(false);

        feladDtCheck = new JCheckBox("Feladás dátuma:");
        feladDtCheck.addActionListener(e -> feladDtSpinner.setEnabled(feladDtCheck.isSelected()));

        row1Panel.add(intezmenyLabel);
        row1Panel.add(intezmenyCombo);
        row1Panel.add(Box.createHorizontalStrut(15));
        row1Panel.add(feladDtCheck);
        row1Panel.add(feladDtSpinner);

        // 2. sor: Keresés + Gombok
        JPanel row2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JLabel searchLabel = new JLabel("Keresés:");
        searchField = new JTextField(20);
        JButton refreshButton = new JButton("Frissítés");
        JButton closeButton = new JButton("Bezárás");

        row2Panel.add(searchLabel);
        row2Panel.add(searchField);
        row2Panel.add(refreshButton);
        row2Panel.add(closeButton);

        topPanel.add(row1Panel);
        topPanel.add(row2Panel);
        
        // Táblázat létrehozása
        String[] columnNames = {"Kórház kód", "Név", "Év", "Hó", "Felad Dt", "Napló ID", "Esetek", "Eset tétel", "Eset műtét", "Khely fej", "Khely tétel"};
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
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // Kórház kód
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Név
        table.getColumnModel().getColumn(2).setPreferredWidth(40);  // Év
        table.getColumnModel().getColumn(3).setPreferredWidth(20);  // Hó
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Dátum
        table.getColumnModel().getColumn(5).setPreferredWidth(50);  // NaplóID
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Esetek
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Eset tétel
        table.getColumnModel().getColumn(8).setPreferredWidth(80);  // Eset műtét
        table.getColumnModel().getColumn(9).setPreferredWidth(80);  // Khely fej
        table.getColumnModel().getColumn(10).setPreferredWidth(80);  // Khely tétel

        // Fejléc igazítás:
        AlignedHeaderRenderer leftHeader = new AlignedHeaderRenderer(SwingConstants.LEFT);
        AlignedHeaderRenderer rightHeader = new AlignedHeaderRenderer(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(1).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(2).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(3).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(4).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(5).setHeaderRenderer(leftHeader);   
        table.getColumnModel().getColumn(6).setHeaderRenderer(rightHeader);  
        table.getColumnModel().getColumn(7).setHeaderRenderer(rightHeader);  
        table.getColumnModel().getColumn(8).setHeaderRenderer(rightHeader);  
        table.getColumnModel().getColumn(9).setHeaderRenderer(rightHeader);  
        table.getColumnModel().getColumn(10).setHeaderRenderer(rightHeader);  
        
        
        NumberRenderer numberRenderer = new NumberRenderer(0);

        table.getColumnModel().getColumn(6).setCellRenderer(numberRenderer); // Esetek
        table.getColumnModel().getColumn(7).setCellRenderer(numberRenderer); // Eset tétel
        table.getColumnModel().getColumn(8).setCellRenderer(numberRenderer); // Eset műtét
        table.getColumnModel().getColumn(9).setCellRenderer(numberRenderer); // Khely fej
        table.getColumnModel().getColumn(10).setCellRenderer(numberRenderer); // Khely tétel
            
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
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            refreshButton.setEnabled(false);
            loadNaplo();
            statusLabel.setText("Sorok száma: " + table.getRowCount());
            setCursor(Cursor.getDefaultCursor());
            refreshButton.setEnabled(true);  // Gomb engedélyezése               
        });
        
        // Bezárás gomb
        closeButton.addActionListener(e -> dispose());
        
        // Adatok betöltése
        loadAeekInt();
    }

    private void loadAeekInt() {
        intezmenyCombo.removeAllItems();
        //intezmenyCombo.addActionListener(e -> loadNaplo());
        
        // ===== ÜRES TÉTEL AZ ELEJÉN =====
        intezmenyCombo.addItem(new ComboItem(null, "-- Válassz intézményt --"));
        String sql = "SELECT id, nev, nev_2, oepkod FROM astar_bsoft_kon.t_aeek_int WHERE id in (26,27,36,36,39,65,98,109,113) ORDER BY nev_2";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String nev = rs.getString("nev");
                String nev_2 = rs.getString("nev_2");
                String oepkod = rs.getString("oepkod");
                
                
                String displayText = nev + " - " + nev_2 + " (" + oepkod + ")";
                
                intezmenyCombo.addItem(new ComboItem(oepkod, displayText));
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

        ComboItem selectedAeekInt = (ComboItem) intezmenyCombo.getSelectedItem();

        java.util.Date selectedFeladDt = (java.util.Date) feladDtSpinner.getValue();
        tableModel.setRowCount(0);

        boolean filterByKorhaz = selectedAeekInt != null
                && selectedAeekInt.getId() != null
                && !selectedAeekInt.getId().isEmpty();
        boolean filterByDatum = feladDtCheck.isSelected();

        String sql = "select n.korhaz_kod, i.nev_2 nev, n.ev, n.ho, to_char(n.felad_dt,'YYYY.MM.DD. HH24:MI') felad_dt, n.naplo_id, f.cnt eset_fej, t.cnt eset_tetel, m.cnt eset_mutet, khf.cnt khely_fej, kht.cnt khely_tetel\n" +
                     "from neak_felad.naplo n\n" +
                     "left join astar_bsoft_kon.t_aeek_int i on i.oepkod = n.korhaz_kod\n" +
                     "left join (select naplo_id, count(*) cnt from neak_felad.eset_fej group by naplo_id) f on f.naplo_id = n.naplo_id\n" +
                     "left join (select naplo_id, count(*) cnt from neak_felad.eset_tetel group by naplo_id) t on t.naplo_id = n.naplo_id\n" +
                     "left join (select naplo_id, count(*) cnt from neak_felad.eset_mutet group by naplo_id) m on m.naplo_id = n.naplo_id\n" +
                     "left join (select naplo_id, count(*) cnt from neak_felad.khely_fej group by naplo_id) khf on khf.naplo_id = n.naplo_id\n" +
                     "left join (select naplo_id, count(*) cnt from neak_felad.khely_tetel group by naplo_id) kht on kht.naplo_id = n.naplo_id\n";

        List<String> conditions = new ArrayList<>();
        if (filterByKorhaz) conditions.add("n.korhaz_kod = ?");
        if (filterByDatum)  conditions.add("trunc(n.felad_dt) = trunc(?)");

        if (!conditions.isEmpty()) {
            sql += "where " + String.join(" and ", conditions) + "\n";
        }
        sql += "order by n.felad_dt desc, n.naplo_id desc";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (filterByKorhaz) {
                pstmt.setString(paramIndex++, selectedAeekInt.getId());
            }
            if (filterByDatum) {
                pstmt.setDate(paramIndex++, new java.sql.Date(selectedFeladDt.getTime()));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("korhaz_kod"),
                        rs.getString("nev"),
                        rs.getString("ev"),
                        rs.getString("ho"),
                        rs.getString("felad_dt"),
                        rs.getInt("naplo_id"),
                        rs.getInt("eset_fej"),
                        rs.getInt("eset_tetel"),
                        rs.getInt("eset_mutet"),
                        rs.getInt("khely_fej"),
                        rs.getInt("khely_tetel")
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Hiba az adatok betöltése során:\n" + e.getMessage(),
                "Adatbázis hiba",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    // Belső osztály a ComboBox elemekhez - STRING ID-val
    private static class ComboItem {
        private final String id;  // int helyett String!
        private final String displayText;

        public ComboItem(String id, String displayText) {  // String paraméter
            this.id = id;
            this.displayText = displayText;
        }

        public String getId() {  // String visszatérési érték
            return id;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }
}
