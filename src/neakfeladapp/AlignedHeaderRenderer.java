/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author tarna
 */
// Újrafelhasználható Header Renderer osztály
class AlignedHeaderRenderer extends DefaultTableCellRenderer {
    
    private int alignment;
    
    public AlignedHeaderRenderer(int alignment) {
        this.alignment = alignment;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
        
        label.setHorizontalAlignment(alignment);
        label.setBackground(table.getTableHeader().getBackground());
        label.setForeground(table.getTableHeader().getForeground());
        label.setFont(table.getTableHeader().getFont());
        label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        
        return label;
    }
}
