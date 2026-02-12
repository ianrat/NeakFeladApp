/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package neakfeladapp;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import java.text.DecimalFormat;
/**
 *
 * @author tk
 */
class NumberRenderer extends DefaultTableCellRenderer {
    private DecimalFormat formatter;
    
    public NumberRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        formatter = new DecimalFormat("#,##0.00"); // vagy "#,##0" ha nincs tizedes
    }
    
    @Override
    protected void setValue(Object value) {
        if (value instanceof Number) {
            value = formatter.format(value);
        }
        super.setValue(value);
    }
}
