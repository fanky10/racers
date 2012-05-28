/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * diagEditCategoria.java
 *
 * Created on 18/10/2011, 16:58:48
 */
package com.carreras.gui;

import com.carreras.common.config.Configuracion;
import com.carreras.common.util.InscriptosCompetenciaFilter;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.servicios.ServiceManager;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author lisandro
 */
public class diagEditCategoria extends javax.swing.JDialog {

    private static boolean editando = false;
    private static final String TITLE = "Editar Categoria Inscripto";
    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    private InscriptoCompetencia inscriptoCompetencia;
    private List<InscriptoCompetencia> inscriptosCompetencia;
    private List<InscriptoCompetencia> inscriptosCompetenciaFiltrados;
    private List<Categoria> categorias;
    private Competencia competencia;

    /** Creates new form diagEditCategoria */
    public diagEditCategoria(java.awt.Frame parent, boolean modal, Competencia competencia) {
        super(parent, modal);
        initComponents();
        this.competencia = competencia;
        init();
        
        setTitle(TITLE);
        setLocationRelativeTo(null);
    }

    private void init() {
        initBusqueda();
        initTables();
        isEditando();
        rbDNI.setSelected(true);
        inscriptosCompetencia = serviceManager.getAllInscriptosCompetencia(competencia.getTorneo());
        for (InscriptoCompetencia ic : inscriptosCompetencia) {
            ic.setCategoria(serviceManager.getCategoria(competencia.getTorneo(), ic.getInscripto()));
        }
        recargaTabla(inscriptosCompetencia);
        
    }

    private void initBusqueda() {
        buttonGroup1.add(rbDNI);
        buttonGroup1.add(rbApellido);
        txtBusqueda.setText("");
    }

    private void initTables() {
        tblAutoCorredor.setModel(new DefaultTableModel(new Object[]{"Sin Datos"},0));
        tblAutoCorredor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAutoCorredor.setAutoCreateRowSorter(false);
        tblAutoCorredor.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                filaSeleccionada();
            }
        });
    }
    
    private void recargaTabla(final List<InscriptoCompetencia> inscriptosFiltrados) {
        inscriptosCompetenciaFiltrados = inscriptosFiltrados;
        final DefaultTableModel tmodel = new NotEditableTableModel();
        if (inscriptosCompetenciaFiltrados.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Sin Datos"});
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"Numero", "DNI", "Nombre", "Apellido", "Categoria"});
            for (InscriptoCompetencia ic : inscriptosCompetenciaFiltrados) {
                Inscripto ca = ic.getInscripto();
                tmodel.addRow(new Object[]{ic.getNumeroGenerado(),ca.getCorredor().getDni(),ca.getCorredor().getNombre(),ca.getCorredor().getApellido(),ic.getCategoria()});
                
            }
        }
        tblAutoCorredor.setModel(tmodel);

    }
    private void filtraInscriptos(){
        String txt = txtBusqueda.getText();
        if(rbDNI.isSelected()){
            recargaTabla(InscriptosCompetenciaFilter.filtrarDNI(inscriptosCompetencia, txt));
        }else if(rbApellido.isSelected()){
            recargaTabla(InscriptosCompetenciaFilter.filtrarApellido(inscriptosCompetencia, txt));
        }else{
            throw new IllegalArgumentException("Opcion Desconocida");
        }
        
    }
    
    private void filaSeleccionada() {
        loadCategorias();
        int idx = tblAutoCorredor.getSelectedRow();
        if (idx < 0) {
            return;
        }
        inscriptoCompetencia = inscriptosCompetenciaFiltrados.get(idx);
        txtNumero.setText(String.valueOf(inscriptoCompetencia.getNumeroGenerado()));
        cmbCategorias.setSelectedItem(inscriptoCompetencia.getCategoria());
        selectCategoria();
        editando = true;
        isEditando();

    }

    private void selectCategoria() {
        for (Categoria a : categorias) {
            if (a.getId() == inscriptoCompetencia.getCategoria().getId()) {
                categorias.remove(a);
                DefaultComboBoxModel model = new DefaultComboBoxModel();
                model.addElement(a);
                for (Categoria c : categorias) {
                    model.addElement(c);
                }

                cmbCategorias.setModel(model);
                break;
            }

        }
    }

    private void loadCategorias() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        categorias = serviceManager.getAllCategorias();
        model.addElement("<Seleccione una categoria>");
        for (Categoria c : categorias) {
            model.addElement(c);
        }
        cmbCategorias.setModel(model);
    }
    
    private void guardar(){
        try{
            Categoria categoria_new = (Categoria) cmbCategorias.getSelectedItem();
            inscriptoCompetencia.setCategoria(categoria_new);
            //si hacemos esto aca, va a ser modificada la categoria a una competencia vieja
            //serviceManager.updateInscriptoCompetencia(inscriptoCompetencia);
            recargaTabla(inscriptosCompetencia);
            editando = false;
            isEditando();
        }catch(ClassCastException ex){
            System.out.println("categoria no seleccionada");
            javax.swing.JOptionPane.showMessageDialog(null, "Debe seleccionar una nueva categoria");
            cmbCategorias.grabFocus();
        }
        loadCategorias();
    }

    private void isEditando() {
        if (editando) {
            btnGuardar.setEnabled(editando);
            cmbCategorias.setEnabled(editando);
        } else {
            btnGuardar.setEnabled(editando);
            txtNumero.setText("");
            cmbCategorias.setEnabled(editando);
        }
    }

    private void cerrar() {
        this.dispose();
    }
    
    //retorna los inscriptos actualizados! (:
    public List<InscriptoCompetencia> getInscriptosCompetencia() {
        return inscriptosCompetencia;
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAutoCorredor = new javax.swing.JTable();
        txtBusqueda = new javax.swing.JTextField();
        rbDNI = new javax.swing.JRadioButton();
        rbApellido = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbCategorias = new javax.swing.JComboBox();
        btnNuevaCategoria = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Corredores"));

        tblAutoCorredor.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAutoCorredor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAutoCorredorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAutoCorredor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaKeyReleased(evt);
            }
        });

        rbDNI.setText("DNI");
        rbDNI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDNIActionPerformed(evt);
            }
        });

        rbApellido.setText("Apellido");
        rbApellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbApellidoActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Editar Categoria"));

        btnGuardar.setText("Actualizar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new java.awt.GridLayout(0, 2));

        jLabel1.setText("Numero: ");
        jPanel3.add(jLabel1);

        txtNumero.setEditable(false);
        txtNumero.setEnabled(false);
        jPanel3.add(txtNumero);

        jLabel2.setText("Categoria: ");
        jPanel3.add(jLabel2);

        cmbCategorias.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cmbCategorias);

        btnNuevaCategoria.setText("Nueva Categoria");
        btnNuevaCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNuevaCategoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNuevaCategoria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addContainerGap())
        );

        jButton2.setText("Cerrar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addComponent(rbDNI)
                        .addGap(49, 49, 49)
                        .addComponent(rbApellido))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtBusqueda, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbApellido)
                    .addComponent(rbDNI))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void rbDNIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDNIActionPerformed
    txtBusqueda.grabFocus();
}//GEN-LAST:event_rbDNIActionPerformed

private void rbApellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbApellidoActionPerformed
    txtBusqueda.grabFocus();
}//GEN-LAST:event_rbApellidoActionPerformed

private void txtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaKeyReleased
    filtraInscriptos();
}//GEN-LAST:event_txtBusquedaKeyReleased

private void tblAutoCorredorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAutoCorredorMouseClicked
    if(evt.getClickCount() == 2) {
        filaSeleccionada();
    } else {
        isEditando();
    }
}//GEN-LAST:event_tblAutoCorredorMouseClicked

private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
    guardar();
}//GEN-LAST:event_btnGuardarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cerrar();//o cerrar xD
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnNuevaCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaCategoriaActionPerformed
        nuevaCategoria();
    }//GEN-LAST:event_btnNuevaCategoriaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevaCategoria;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbCategorias;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbApellido;
    private javax.swing.JRadioButton rbDNI;
    private javax.swing.JTable tblAutoCorredor;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

    private void nuevaCategoria() {
        diagAltaCategoria diaAltaCategoria = new diagAltaCategoria(null, true);
        diaAltaCategoria.setVisible(true);
        diaAltaCategoria.dispose();
        if(!diaAltaCategoria.CANCELADO){
            loadCategorias();
        }
    }


}
