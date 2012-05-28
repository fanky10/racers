/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmMain.java
 *
 * Created on Oct 10, 2011, 5:30:27 PM
 */
package com.carreras.gui;

import com.carreras.common.config.Configuracion;
import javax.swing.JFrame;



/**
 *
 * @author fanky
 */
public class frmMain extends javax.swing.JFrame {
    private static final String TITLE = "Carreras v"+Configuracion.getCurrentSysVersion();
    /** Creates new form frmMain */
    public frmMain() {
        //components initialization
        initComponents();
        //internal initialization
        init();
        //frame initialization
        setLocationRelativeTo(null);
        setTitle(TITLE);
    }
    /**
     * inicializa lo siguiente:
     * si no hay temp. con carreras.. onda todo finalizo correctamente
     * el boton de continuar carreras queda inhabilitado (:
     */
    private void init(){
        btnContinuarCarrera.setEnabled(false);
    }
    private void nueva_carrera(){
        JFrame fc = new frmCarrera();
        fc.setVisible(true);
        this.dispose();
    }
    private void cargar_carrera(){
        throw new UnsupportedOperationException("not supported yet");
        
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
        btnNuevaCarrera = new javax.swing.JButton();
        btnContinuarCarrera = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        btnNuevaCarrera.setText("Nuevo Torneo");
        btnNuevaCarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaCarreraActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevaCarrera);

        btnContinuarCarrera.setText("Continuar Torneo");
        btnContinuarCarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarCarreraActionPerformed(evt);
            }
        });
        jPanel1.add(btnContinuarCarrera);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton3.setText("Opc Menu 1");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .add(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevaCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaCarreraActionPerformed
        nueva_carrera();
    }//GEN-LAST:event_btnNuevaCarreraActionPerformed

    private void btnContinuarCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarCarreraActionPerformed
        cargar_carrera();
    }//GEN-LAST:event_btnContinuarCarreraActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new frmMain().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContinuarCarrera;
    private javax.swing.JButton btnNuevaCarrera;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
