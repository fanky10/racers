/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmPpal.java
 *
 * Created on 13/07/2011, 23:11:05
 */
package arduino;

import arduino.entidades.Adelantamiento;
import arduino.entidades.Carril;
import arduino.entidades.Datos;
import arduino.entidades.Rotura;
import arduino.entidades.Tiempo;
import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;

/**
 *
 * @author Fabian Nonino <<fabian.nonino@gmail.com>>
 */
public class frmPpal extends javax.swing.JFrame {
    private Arduino arduino;
    /** Creates new form frmPpal */
    public frmPpal() {
        initComponents();
        this.setTitle("Arduino driver " + Config.VERSION);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnInicia = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnBorrarLog = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnInicia.setText("Iniciar Arduino");
        btnInicia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciaActionPerformed(evt);
            }
        });

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        btnBorrarLog.setText("borrar Log");
        btnBorrarLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarLogActionPerformed(evt);
            }
        });

        jButton1.setText("cierra");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnInicia)
                        .addGap(29, 29, 29)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                        .addComponent(btnBorrarLog)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInicia)
                    .addComponent(btnBorrarLog)
                    .addComponent(jButton1))
                .addGap(36, 36, 36)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciaActionPerformed
        ArduinoEventListener arduinoEvent = new ArduinoEventListener() {

            @Override
            public void EstadoArduino(ArduinoEvent arduino_event) {
                txtLog.append(arduino_event.getMensaje() + "\n");
            }

            @Override
            public void Estado_Datos(RespuestaEvent respuesta_event) {
                switch (respuesta_event.getNro_evento()) {
                    case RespuestaEvent.CARRIL:
                        if(respuesta_event.getDatos().getTipo()==Datos.CARRIL){
                            Carril reto = (Carril) respuesta_event.getDatos();
                            txtLog.append("**** INICIO******\n");
                            txtLog.setSelectionStart(txtLog.getText().length());
                            txtLog.append("nro: carril: " + reto.getNro_carril() + "\n");
                            txtLog.setSelectionStart(txtLog.getText().length());
                            for (Tiempo t : reto.getTiempoV()) {
                                txtLog.append("***\n");
                                txtLog.setSelectionStart(txtLog.getText().length());
                                txtLog.append("tipo tiempo: " + t.getTipo_tiempo() + "\n");
                                txtLog.setSelectionStart(txtLog.getText().length());
                                txtLog.append("tiempo: " + t.getTiempo() + "\n");
                                txtLog.setSelectionStart(txtLog.getText().length());
                            }
                            txtLog.append("**** FIN******\n");
                            txtLog.setSelectionStart(txtLog.getText().length());
                        }else if(respuesta_event.getDatos().getTipo()==Datos.ADELANTAMIENTO){
                            Adelantamiento adelantamiento = (Adelantamiento) respuesta_event.getDatos();
                            txtLog.append("**** ADELANTAMIENTO******\n");
                            txtLog.append("NRO CARRIL: "+adelantamiento.getNro_carril());
                            txtLog.append("**** FIN******\n");
                        }else if(respuesta_event.getDatos().getTipo()==Datos.ROTURA){
                            Rotura rotura = (Rotura) respuesta_event.getDatos();
                            txtLog.append("**** ROTURA******\n");
                            txtLog.append("NRO CARRIL: "+rotura.getNro_carril());
                            txtLog.append("**** FIN******\n");
                        }
                        break;
                        
                    case RespuestaEvent.ERROR:
                        txtLog.append(respuesta_event.getError().getMensaje() + "\n");
                        txtLog.setSelectionStart(txtLog.getText().length());
                }
            }
        };
        arduino = new Arduino(arduinoEvent);
        arduino.ini();
        

    }//GEN-LAST:event_btnIniciaActionPerformed

    private void btnBorrarLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarLogActionPerformed
        txtLog.setText("");
    }//GEN-LAST:event_btnBorrarLogActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        arduino.close();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                
                System.out.println("path! "+System.getProperty("java.library.path"));
                new frmPpal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBorrarLog;
    private javax.swing.JButton btnInicia;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}