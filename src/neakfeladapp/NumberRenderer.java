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
    
    private int decimalPlaces;  // Tizedesjegyek száma
    
    // Konstruktor tizedesjegy nélkül (alapértelmezett 2)
    public NumberRenderer() {
        this(2);  // Alapértelmezetten 2 tizedesjegy
    }
    
    // Konstruktor tizedesjegyek számával
    public NumberRenderer(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
    @Override
    protected void setValue(Object value) {
        if (value == null) {
            setText("");
        } else if (value instanceof Number) {
            if (decimalPlaces == 0) {
                // Egész szám formázás
                setText(String.format("%,d", ((Number) value).intValue()));
            } else {
                // Tizedes formázás
                String pattern = "%,." + decimalPlaces + "f";
                setText(String.format(pattern, ((Number) value).doubleValue()));
            }
        } else {
            setText(value.toString());
        }
    }
}

