/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.gui;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fanky10
 */
public class NotEditableTableModel extends DefaultTableModel{
    public NotEditableTableModel(Object object[],int row){
        super(object, row);
    }
    public NotEditableTableModel(){
        super();
    }
    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }
}
