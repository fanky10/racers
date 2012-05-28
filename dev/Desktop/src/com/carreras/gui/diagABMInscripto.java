/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * diagAAutoCorredor.java
 *
 * Created on Oct 11, 2011, 10:39:28 PM
 */
package com.carreras.gui;

import com.carreras.common.config.Configuracion;
import com.carreras.common.util.InscriptosFilter;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fanky
 */
public class diagABMInscripto extends javax.swing.JDialog {

    private static final String TITLE = "Auto-Corredor";
    public static boolean CANCELADO = true;
    private List<InscriptoCompetencia> inscriptosUsados;
    private List<Inscripto> inscriptosDisponibles;
    private List<Inscripto> inscriptosDisponiblesFiltrados;
    private Inscripto inscripto;
    private Integer nroRondas = 1;

    public diagABMInscripto(java.awt.Frame parent, List<InscriptoCompetencia> inscriptosUsados) {
        this(parent, true, inscriptosUsados);
    }

    public diagABMInscripto(java.awt.Frame parent) {
        this(parent, true, new ArrayList<InscriptoCompetencia>());
    }

    private diagABMInscripto(java.awt.Frame parent, boolean modal) {
        this(parent, modal, new ArrayList<InscriptoCompetencia>());
    }

    /** Creates new form diagAAutoCorredor */
    private diagABMInscripto(java.awt.Frame parent, boolean modal, List<InscriptoCompetencia> inscriptosUsados) {
        super(parent, modal);
        this.inscriptosUsados = inscriptosUsados;
        this.inscriptosDisponibles = new ArrayList<Inscripto>();
        //autoinit
        initComponents();
        //customini
        init();
        //frame config
        setTitle(TITLE);
        setLocationRelativeTo(null);
    }

    /**
     * inicializa la tabla con los datos de los competidores cargados
     * el radioButton seleccionado por default
     */
    private void init() {
        rbDNI.setSelected(true);
        tblInscriptos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInscriptos.setAutoCreateRowSorter(false);
        tblInscriptos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                btnSeleccionar.setEnabled(!(tblInscriptos.getSelectedRow() < 0));
                txtNroRondas.grabFocus();
            }
        ;
        });
        loadTable();
    }

    private void loadTable() {
        inscriptosDisponibles = Configuracion.getInstance().getServiceManager().getAllInscriptosBut(inscriptosUsados);
        actualizaTabla(inscriptosDisponibles);
    }

    private void filtra_corredor_auto() {
        String txt = txtBusqueda.getText();
        if (rbDNI.isSelected()) {
            actualizaTabla(InscriptosFilter.filtrarDNI(inscriptosDisponibles, txt));
        } else if (rbApellido.isSelected()) {
            actualizaTabla(InscriptosFilter.filtrarApellido(inscriptosDisponibles, txt));
        } else {
            throw new IllegalArgumentException("Opcion Desconocida");
        }

    }

    private void actualizaTabla(final List<Inscripto> inscriptos) {
        inscriptosDisponiblesFiltrados = inscriptos;
        final DefaultTableModel tmodel = new NotEditableTableModel();
        if (inscriptos == null || inscriptosDisponiblesFiltrados.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Sin Datos"});
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"DNI", "Nombre", "Apellido", "Patente"});
            for (Inscripto ca : inscriptosDisponiblesFiltrados) {
                tmodel.addRow(new Object[]{ca.getCorredor().getDni(), ca.getCorredor().getNombre(), ca.getCorredor().getApellido(), ca.getAuto().getPatente()});
            }
        }
        tblInscriptos.setModel(tmodel);

    }

    /**
     * en vez de guardar uno nuevo, este deja listo el corredor_auto para que 
     * el que llamo a esta clase lo use.
     */
    private void seleccionar() {
        try {
            int idx = tblInscriptos.getSelectedRow();
            if (idx < 0) {
                return;
            }
            inscripto = inscriptosDisponiblesFiltrados.get(idx);
            nroRondas = Integer.parseInt(txtNroRondas.getText());
            CANCELADO = false;
            dispose();
        } catch (NumberFormatException ex) {
            //ignored
            lblEstado.setText("El nro de rondas debe ser un numero");
        }
    }

    /**
     * guarda el nuevo corredor y lo deja listo para que el que llamo a este diag. lo use
     */
    private void agregar() {
        diagAltaInscripto diag = new diagAltaInscripto(this);
        diag.setVisible(true);
        diag.dispose();
        if (!diagAltaInscripto.CANCELADO) {
            loadTable();
        }
    }

    /**
     * cancela toda operacion
     */
    private void cancelar() {
        CANCELADO = true;
        dispose();
    }

    public Inscripto getInscripto() {
        return inscripto;
    }

    public Integer getNroRondas() {
        return nroRondas;
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
        txtBusqueda = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        rbDNI = new javax.swing.JRadioButton();
        rbApellido = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInscriptos = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnSeleccionar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNroRondas = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtBusqueda.setText("Buscar");
        txtBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBusquedaFocusGained(evt);
            }
        });
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaKeyReleased(evt);
            }
        });

        buttonGroup1.add(rbDNI);
        rbDNI.setText("DNI");
        rbDNI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDNIActionPerformed(evt);
            }
        });
        jPanel4.add(rbDNI);

        buttonGroup1.add(rbApellido);
        rbApellido.setText("Apellido");
        rbApellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbApellidoActionPerformed(evt);
            }
        });
        jPanel4.add(rbApellido);

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
        tblInscriptos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblInscriptosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblInscriptos);

        btnSeleccionar.setText("Seleccionar");
        btnSeleccionar.setEnabled(false);
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
            }
        });
        jPanel3.add(btnSeleccionar);

        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        jPanel3.add(btnAgregar);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        jPanel3.add(btnCancelar);

        lblEstado.setText("Estado");
        lblEstado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jPanel1.setLayout(new java.awt.GridLayout());

        jLabel1.setText("Numero de rondas a correr");
        jPanel1.add(jLabel1);

        txtNroRondas.setText("1");
        txtNroRondas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNroRondasFocusGained(evt);
            }
        });
        txtNroRondas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNroRondasKeyReleased(evt);
            }
        });
        jPanel1.add(txtNroRondas);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lblEstado, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtBusqueda, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(txtBusqueda, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblEstado))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        seleccionar();
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tblInscriptosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInscriptosMouseClicked
        if (evt.getClickCount() == 2) {
            seleccionar();
        }
    }//GEN-LAST:event_tblInscriptosMouseClicked

    private void txtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaKeyReleased
        filtra_corredor_auto();
    }//GEN-LAST:event_txtBusquedaKeyReleased

    private void txtBusquedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBusquedaFocusGained
        txtBusqueda.selectAll();
    }//GEN-LAST:event_txtBusquedaFocusGained

    private void rbApellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbApellidoActionPerformed
        txtBusqueda.grabFocus();
    }//GEN-LAST:event_rbApellidoActionPerformed

    private void rbDNIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDNIActionPerformed
        txtBusqueda.grabFocus();
    }//GEN-LAST:event_rbDNIActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregar();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void txtNroRondasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNroRondasFocusGained
        txtNroRondas.selectAll();
    }//GEN-LAST:event_txtNroRondasFocusGained

    private void txtNroRondasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNroRondasKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            seleccionar();
        }
    }//GEN-LAST:event_txtNroRondasKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Configuracion.init_conf(args);
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
            java.util.logging.Logger.getLogger(diagABMInscripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(diagABMInscripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(diagABMInscripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(diagABMInscripto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                diagABMInscripto dialog = new diagABMInscripto(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                dialog.dispose();
                System.exit(0);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JRadioButton rbApellido;
    private javax.swing.JRadioButton rbDNI;
    private javax.swing.JTable tblInscriptos;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JTextField txtNroRondas;
    // End of variables declaration//GEN-END:variables
}
