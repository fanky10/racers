/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DiagOrdenaInscriptos.java
 *
 * Created on Jul 29, 2012, 1:32:33 PM
 */
package com.carreras.gui;

import com.carreras.common.util.InscriptoCompetenciaHelper;
import com.carreras.common.util.Utilidades;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.servicios.impl.ServiceManagerImpl;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fanky
 */
public class DiagOrdenaInscriptos extends javax.swing.JDialog {

    private List<InscriptoCompetencia> inscriptos = new ArrayList<InscriptoCompetencia>();
    private Map<EstadoCompetencia, List<InscriptoCompetencia>> estadoInscriptos = new EnumMap<EstadoCompetencia, List<InscriptoCompetencia>>(EstadoCompetencia.class);
    private List<InscriptoCompetencia> inscriptosSeleccionados = new ArrayList<InscriptoCompetencia>();

    /** Creates new form DiagOrdenaInscriptos */
    public DiagOrdenaInscriptos(java.awt.Frame parent, boolean modal, List<InscriptoCompetencia> inscriptos) {
        super(parent, modal);
        this.inscriptos = inscriptos;
        initComponents();
        init();
    }

    private void init() {
        // fill table, etc.
        tblInscriptos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInscriptos.setAutoCreateRowSorter(false);
        tblInscriptos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                // may be do something here

            }
        ;
        });
        divideInscriptos();
        cargaComboBox();
        refreshTable();

    }

    private void divideInscriptos() {
        //TODO: mover a un controlador
        if(inscriptos.size()>2){
            for (InscriptoCompetencia ic : inscriptos) {
                List<InscriptoCompetencia> inscriptos = null;
                if (estadoInscriptos.containsKey(ic.getEstadoCompetencia())) {
                    inscriptos = estadoInscriptos.get(ic.getEstadoCompetencia());
                    inscriptos.add(ic);
                } else {
                    inscriptos = new ArrayList<InscriptoCompetencia>();
                    inscriptos.add(ic);
                    estadoInscriptos.put(ic.getEstadoCompetencia(), inscriptos);
                }
            }
        }else{//estamos en la final (:
            estadoInscriptos.put(EstadoCompetencia.COMPETENCIA_EN_FINAL, inscriptos);
            
        }
    }

    private void cargaComboBox() {
        //el comboBox va a tener los estados
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if(estadoInscriptos.keySet().size()>1){//si no hay uno solo, avisar
            model.addElement("<Seleccione un estado competencia>");
        }
        for (EstadoCompetencia ec : estadoInscriptos.keySet()) {
            model.addElement(ec);
        }
        cmbEstadoCompetencia.setModel(model);
    }

    private void refreshTable() {
        //trata de ver que ha seleccionado el usuario de antemano
        //luego con eso, refrescar la tabla
        inscriptosSeleccionados = new ArrayList<InscriptoCompetencia>();
        try {
            EstadoCompetencia estadoCompetencia = (EstadoCompetencia) cmbEstadoCompetencia.getSelectedItem();
            if (estadoInscriptos.containsKey(estadoCompetencia)) {
                inscriptosSeleccionados = estadoInscriptos.get(estadoCompetencia);
                InscriptoCompetenciaHelper.suffleInscriptos(inscriptosSeleccionados);
            }
        } catch (ClassCastException ex) {
            //ignored
        }
        refreshTable(inscriptosSeleccionados);
    }

    private void refreshTable(List<InscriptoCompetencia> inscriptos) {
        DefaultTableModel tmodel = new NotEditableTableModel();
        if (inscriptos == null || inscriptos.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Seleccione estado en competencia"});
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"Numero", "Apellido", "Nombre", "Categoria"});
            for (int idx = 0; idx < inscriptos.size(); idx++) {
                InscriptoCompetencia ins = inscriptos.get(idx);
                tmodel.addRow(new Object[]{ins.getNumeroGenerado(), ins.getInscripto().getCorredor().getNombre(), ins.getInscripto().getCorredor().getNombre(), ins.getCategoria().getDescripcion()});
            }
        }
        tblInscriptos.setModel(tmodel);
    }
    public void subirCorredor() {
        mueveRow(-1);
    }

    public void bajarCorredor() {
        mueveRow(1);
    }

    private void mueveRow(int i) {
        // se podria hacer con algun helper jejeje

        // podria ser implementado dentro del tableModel
        // para un nuevo refactor
        int idx = tblInscriptos.getSelectedRow();
        Integer selectedRow = 0;

        Boolean valido = false;
        //el quiero moverme hacia adelante es mayor a cero, no debo estar en el ultimo
        if (i > 0 && idx + 1 != inscriptosSeleccionados.size()) {
            valido = true;

        }//si me quiero mover hacia atras no debo estar en el primero
        else if (i < 0 && idx > 0) {
            valido = true;
        }
        if (valido) {
            if (i > 0) {
                InscriptoCompetenciaHelper.mueveInscriptoFordward(inscriptosSeleccionados, idx);
            } else {
                InscriptoCompetenciaHelper.mueveInscriptoBackward(inscriptosSeleccionados, idx);
            }
            refreshTable(inscriptosSeleccionados);
            selectedRow = idx + i;
            tblInscriptos.setRowSelectionInterval(selectedRow, selectedRow);
            Utilidades.scrollToVisible(tblInscriptos, selectedRow, 0);
        }
    }

    

    public List<InscriptoCompetencia> getInscriptos() {
        List<InscriptoCompetencia> inscriptos = new ArrayList<InscriptoCompetencia>();
        for(EstadoCompetencia ec: estadoInscriptos.keySet()){
            inscriptos.addAll(estadoInscriptos.get(ec));
        }
        return inscriptos;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cmbEstadoCompetencia = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInscriptos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnAceptar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnSubir = new javax.swing.JButton();
        btnBajar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbEstadoCompetencia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbEstadoCompetencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoCompetenciaActionPerformed(evt);
            }
        });
        jPanel1.add(cmbEstadoCompetencia);

        tblInscriptos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblInscriptos);

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });
        jPanel2.add(btnAceptar);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridLayout(0, 1));

        btnSubir.setText("Subir");
        btnSubir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubirActionPerformed(evt);
            }
        });
        jPanel3.add(btnSubir);

        btnBajar.setText("Bajar");
        btnBajar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBajarActionPerformed(evt);
            }
        });
        jPanel3.add(btnBajar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 375, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .addContainerGap())
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubirActionPerformed
        subirCorredor();
    }//GEN-LAST:event_btnSubirActionPerformed

    private void btnBajarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajarActionPerformed
        bajarCorredor();
    }//GEN-LAST:event_btnBajarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void cmbEstadoCompetenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoCompetenciaActionPerformed
        refreshTable();
    }//GEN-LAST:event_cmbEstadoCompetenciaActionPerformed
    //for testing purposes

    public static void main(String args[]) {
        //mocked competencia and categoria
        Competencia comp = new Competencia();
        comp.setId(730);
        Categoria cat = new Categoria();
        cat.setId(6);
        List<InscriptoCompetencia> inscriptos = new ServiceManagerImpl().getInscriptosCompetencia(comp, cat);
        DiagOrdenaInscriptos diag = new DiagOrdenaInscriptos(null, true, inscriptos);
        diag.setVisible(true);
        diag.dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBajar;
    private javax.swing.JButton btnSubir;
    private javax.swing.JComboBox cmbEstadoCompetencia;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblInscriptos;
    // End of variables declaration//GEN-END:variables

    
}
