/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * diagAAutoCorredro.java
 *
 * Created on Mar 28, 2012, 9:28:48 PM
 */
package com.carreras.gui;

import com.carreras.common.config.Configuracion;
import com.carreras.common.util.Utilidades;
import com.carreras.dominio.modelo.Auto;
import com.carreras.dominio.modelo.Corredor;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.servicios.ServiceManager;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;

/**
 *
 * @author fanky
 */
public class diagAltaInscripto extends javax.swing.JDialog {
    private static final String TITLE = "Alta Corredor";
    public static boolean CANCELADO = true;
    private Inscripto inscripto;
    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    /** Creates new form diagAAutoCorredro */
    public diagAltaInscripto(JDialog parent, Inscripto inscripto) {
        super(parent, true);
        this.inscripto = inscripto;
        initComponents();
        init();
        setLocationRelativeTo(parent);
    }
    public diagAltaInscripto(JDialog parent) {
        this(parent,null);
    }
    private void init(){
        if(inscripto==null){
            setTitle("Alta Inscripto");
        }else{
            //TODO
            //load corresponding data.
            throw new IllegalArgumentException("you can not create me with a not null inscripto");
        }
    }
    
    private void autocompleta_patente(){
        String patente = txtPatente.getText();
        if(patente.length()==3){
            patente += "-";
        }
        txtPatente.setText(patente);
    }
    
    private void aceptar(){
        if(txtDNI.getText().isEmpty() || !Utilidades.es_numero(txtDNI)){
            lblEstado.setText("El DNI Debe ser un numero");
            txtDNI.grabFocus();
            return ;
        }
        if(txtNombre.getText().trim().isEmpty()){
            lblEstado.setText("Ingrese un Nombre valido");
            txtNombre.grabFocus();
            return ;
        }
        if(txtApellido.getText().trim().isEmpty()){
            lblEstado.setText("Ingrese un Apellido valido");
            txtApellido.grabFocus();
            return ;
        }
        if(!Utilidades.esPatente(txtPatente.getText())){
            lblEstado.setText("Verifique patente");
            txtPatente.grabFocus();
            return ;
        }
        
        Auto auto = new Auto();
        auto.setPatente(txtPatente.getText());
        auto.setId(serviceManager.saveAuto(auto));
        
        Corredor corredor = new Corredor();
        corredor.setApellido(txtApellido.getText());
        corredor.setDni(txtDNI.getText());
        corredor.setNombre(txtNombre.getText());
        corredor.setId(serviceManager.saveCorredor(corredor));
        
        inscripto = new Inscripto();
        inscripto.setAuto(auto);
        inscripto.setCorredor(corredor);
        inscripto.setId(serviceManager.saveInscripto(inscripto));
        
        CANCELADO = false;
        dispose();
    }
    /**
     * cancela toda operacion
     */
    private void cancelar(){
        CANCELADO = true;
        dispose();
    }

    public Inscripto getInscripto() {
        return inscripto;
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
        jLabel1 = new javax.swing.JLabel();
        txtDNI = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtPatente = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Conductor"));
        jPanel1.setLayout(new java.awt.GridLayout(0, 2));

        jLabel1.setText("DNI");
        jPanel1.add(jLabel1);
        jPanel1.add(txtDNI);

        jLabel2.setText("Nombre");
        jPanel1.add(jLabel2);
        jPanel1.add(txtNombre);

        jLabel3.setText("Apellido");
        jPanel1.add(jLabel3);
        jPanel1.add(txtApellido);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Auto"));
        jPanel5.setLayout(new java.awt.GridLayout());

        jLabel4.setText("Patente");
        jPanel5.add(jLabel4);

        txtPatente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPatenteKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPatenteKeyReleased(evt);
            }
        });
        jPanel5.add(txtPatente);

        jButton1.setText("Aceptar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        lblEstado.setText("Estado");
        lblEstado.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lblEstado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblEstadoKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
                .addContainerGap())
            .add(lblEstado, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(lblEstado))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        aceptar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cancelar();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void lblEstadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblEstadoKeyReleased
        
    }//GEN-LAST:event_lblEstadoKeyReleased

    private void txtPatenteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPatenteKeyPressed
        autocompleta_patente();
    }//GEN-LAST:event_txtPatenteKeyPressed

    private void txtPatenteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPatenteKeyReleased
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            aceptar();
        }
    }//GEN-LAST:event_txtPatenteKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPatente;
    // End of variables declaration//GEN-END:variables
}