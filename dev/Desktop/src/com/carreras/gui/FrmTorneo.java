/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmTorneo.java
 *
 * Created on Jul 11, 2012, 1:14:56 AM
 */
package com.carreras.gui;

import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import com.carreras.common.util.ArduinoManager;
import com.carreras.common.util.Utilidades;
import com.carreras.controllers.CompetenciaController;
import com.carreras.controllers.impl.CompetenciaControllerImplNew;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoCompetencia;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author fanky
 */
public class FrmTorneo extends javax.swing.JFrame {

    private static final String TITLE = "Carreras v" + Configuracion.getCurrentSysVersion();
    private static boolean AUTO_INICIA_CARRERA = Configuracion.isAutoiniciaCarrera();
    private static boolean MUESTRA_MENSAJES = Configuracion.isMuestraMensajes();
    private static final int COLUMNA_CORREDOR_ESTADO = 0;
    private static final int COLUMNA_CORREDOR_CATEGORIA = 2;
    //es una unica instancia para toda la competencia
    private ArduinoManager ardmgr;
    private CompetenciaController competenciaController = new CompetenciaControllerImplNew();

    /** Creates new form FrmTorneo */
    public FrmTorneo() {
        initComponents();
        //initial config
        init();
        //frame config
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                //cierro el arduino
                finalizaComunicacion();
                System.exit(0);
            }
        });
        setTitle(TITLE);
        setLocationRelativeTo(null);
    }

    private void init() {
        chkProxCarrAuto.setSelected(AUTO_INICIA_CARRERA);
        ardmgr = new ArduinoManager(createEventListener());
        initComunication();
        competenciaController.iniciaTorneo();
        Competencia competenciaActual = competenciaController.getCompetenciaActual();
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
        recargaCategorias();
        recargaTblCorredores();
    }

    private void recargaCategorias() {
        List<Categoria> categorias = competenciaController.getCategorias();
        DefaultComboBoxModel comboboxModel = new DefaultComboBoxModel();
        if (categorias.isEmpty()) {
            comboboxModel.addElement("Agregue un Corredor");
        }
        for (Categoria c : categorias) {
            comboboxModel.addElement(c);
        }
        cmbCategoria.setModel(comboboxModel);
    }

    private ArduinoEventListener createEventListener() {
        return new ArduinoEventListener() {

            @Override
            public void EstadoArduino(ArduinoEvent arduino_event) {

                if (arduino_event.getEstado() == ArduinoEvent.ARDUINO_ONLINE) {
                    muestraEstado("Semaforo ONLINE - Esperando datos...");
                } else {
                    muestraEstado(arduino_event.getMensaje());
                }
            }

            @Override
            public void Estado_Datos(RespuestaEvent respuesta_event) {
                eventoDatos(respuesta_event);
            }
        };
    }

    private void eventoDatos(final RespuestaEvent rtaEvt) {
        switch (rtaEvt.getNro_evento()) {
            case RespuestaEvent.CARRIL:
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        muestraEstado("Recibiendo datos...");
                        competenciaController.eventoCarril(rtaEvt);
                        recargaTblTiempos();
                    }
                });
                break;
            case RespuestaEvent.ERROR:
                muestraEstado(rtaEvt.getError().getMensaje());
                //reinicia arduino :D
                reiniciaComunicacion();
                break;
        }
    }

    private void proximaCarrera() {
        //reinicio att.
        Map modelMap = competenciaController.proximaCarrera();
        Boolean finCarreras = (Boolean) modelMap.get("finCarreras");
        if (finCarreras) {
            if (!MUESTRA_MENSAJES) {
                nuevaRonda();
                return;
            }
            int status = javax.swing.JOptionPane.showConfirmDialog(this, "Se ha finalizado con todas las carreras disponibles \nGenere una nueva ronda o competencia", "Seleccione Accion", javax.swing.JOptionPane.OK_CANCEL_OPTION);
            if (status == javax.swing.JOptionPane.OK_OPTION) {
                nuevaRonda();
            }
            return;
        }
        recargaTblCorredores();
        recargaCarriles();
        recargaTblTiempos();
    }

    private void nuevaRonda() {
        Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
        Map modelMap = competenciaController.nuevaRonda(categoriaSeleccionada);
        Inscripto ganador = (Inscripto) modelMap.get("ganadorCompetencia");
        Competencia competenciaActual = competenciaController.getCompetenciaActual();
        List<InscriptoCompetencia> inscriptosCorriendo = competenciaController.getInscriptosCorriendo();
        if (ganador != null) {
            javax.swing.JOptionPane.showMessageDialog(rootPane, "El ganador de la categoria: "+categoriaSeleccionada.getDescripcion()+" es: " + ganador.getCorredor().getNombre());
            javax.swing.JOptionPane.showMessageDialog(rootPane, "Seleccione una nueva categoria e inicie una nueva ronda");
            recargaTblCorredores();
            recargaTblTiempos();
            recargaCategorias();
            if(competenciaController.getCategorias().isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(rootPane, "Se han finalizado con todas las categorias - The end");
                System.exit(0);
            }
            return ;
        } else if (competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL && competenciaActual.getNumeroRonda() == 1) {
            javax.swing.JOptionPane.showMessageDialog(rootPane, "Estamos en la final!!");
        } else  if (inscriptosCorriendo.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(rootPane, "No quedan jugadores con Rondas Restantes");
            if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
                finalizaPruebaLibre();
            }
            return;
        }
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
        recargaTblCorredores();
        recargaTblTiempos();
        recargaCategorias();
    }

    private void agregarCorredor() {
        diagABMInscripto diag = new diagABMInscripto(this, competenciaController.getInscriptosSeleccionados());
        diag.setVisible(true);
        diag.dispose();
        if (!diagABMInscripto.CANCELADO) {
            competenciaController.agregaNuevoInscripto(diag.getInscripto(), diag.getNroRondas());
            recargaCarriles();
            recargaTblCorredores();
            recargaCategorias();
        }
    }

    private void finalizaPruebaLibre() {
        diagEditCategoria diag = new diagEditCategoria(this, true, competenciaController.getCompetenciaActual());
        diag.setVisible(true);
        diag.dispose();
        competenciaController.finalizaInscripcion(diag.getInscriptosCompetencia());
        Competencia competenciaActual = competenciaController.getCompetenciaActual();
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
        recargaTblCorredores();
        recargaTblTiempos();
        recargaCategorias();
        btnFinInscripcion.setEnabled(false);
        btnAgregarCorredor.setEnabled(false);
        cmbCategoria.setSelectedIndex(0);
    }

    private void recargaTblCorredores() {
        recargaTblCorredores(competenciaController.getInscriptosCorriendo());
    }

    private void recargaTblCorredores(List<InscriptoCompetencia> inscriptos) {
        final DefaultTableModel tmodel = new NotEditableTableModel();
        int linea_corriendo = 0;
        if (inscriptos.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Sin Datos"});
            tblEstadoCorredores.setModel(tmodel);
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"Estado", "Numero", "Categoria"});//just for debug:, "RR"});
            for (int idx = 0; idx < inscriptos.size(); idx++) {
                InscriptoCompetencia ins = inscriptos.get(idx);
                if (ins.getEstado() != EstadoInscriptoCompetenciaCarrera.NO_CORRE) {
                    tmodel.addRow(new Object[]{ins.getEstado(), ins.getNumeroGenerado(), ins.getCategoria().getDescripcion(), ins.getRondasRestantes()});
                    //pisa hasta que agarre el ultimo corriendo (:
                    if (ins.getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                        linea_corriendo = idx;
                    }
                }
            }
            if (!inscriptos.isEmpty()) {
                tblEstadoCorredores.setModel(tmodel);
                tblEstadoCorredores.getColumnModel().getColumn(COLUMNA_CORREDOR_ESTADO).setCellRenderer(new CorredoresAutosEstadoRenderer(inscriptos));
                tblEstadoCorredores.getColumnModel().getColumn(COLUMNA_CORREDOR_CATEGORIA).setCellRenderer(new CorredoresAutosCategoriaRenderer(inscriptos));
                Utilidades.scrollToVisible(tblEstadoCorredores, linea_corriendo, 0);
            }
        }
    }

    private void recargaTblTiempos() {
        //TODO: cambiar esto por algun algoritmo que chequee que no hay tiempos.
        final DefaultTableModel tmodel = new NotEditableTableModel();
        List<Carril> carriles = competenciaController.getCarriles();
        Map modelMap = competenciaController.recargaTiempos();
        Map<Carril, List<com.carreras.dominio.modelo.Tiempo>> carrilTiempos = (Map<Carril, List<com.carreras.dominio.modelo.Tiempo>>) modelMap.get("carrilTiempos");
        InscriptoCompetencia inscriptoGanador = (InscriptoCompetencia) modelMap.get("inscriptoGanador");
        if (carriles.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Sin Datos - Agrege un inscripto"});
            tblTiempos.setModel(tmodel);
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"Carril", "Nro", "Patente", "TipoTiempo", "Tiempo"});
            for (Carril carrilModelo : carriles) {
                if (carrilModelo.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.ADELANTADO) {
                    tmodel.addRow(new Object[]{EstadoInscriptoCompetenciaCarrera.ADELANTADO, "", "", "", ""});
                } else if (carrilModelo.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.ROTO) {
                    tmodel.addRow(new Object[]{EstadoInscriptoCompetenciaCarrera.ROTO, "", "", "", ""});
                } else {
                    //tiempos!
                    List<com.carreras.dominio.modelo.Tiempo> tiemposModel = carrilTiempos.get(carrilModelo);
                    int i = 0; //para ver si es el inicial
                    for (com.carreras.dominio.modelo.Tiempo tt : tiemposModel) {
                        if (i == 0) {
                            tmodel.addRow(new Object[]{carrilModelo.getNumero(),
                                        carrilModelo.getInscriptoCompetencia().getNumeroGenerado(),
                                        carrilModelo.getInscriptoCompetencia().getInscripto().getAuto().getPatente(),
                                        tt.getTipoTiempo().getDescripcion(), tt.getTiempo()});
                        } else {
                            tmodel.addRow(new Object[]{"", "", "", tt.getTipoTiempo().getDescripcion(), tt.getTiempo()});

                        }
                        i++;
                    }
                }
            }
            tblTiempos.setModel(tmodel);

        }
        if (inscriptoGanador != null) {
            if (MUESTRA_MENSAJES) {
                javax.swing.JOptionPane.showMessageDialog(this, "Carrera finalizada! "
                        + "\nGanador: " + inscriptoGanador.getNumeroGenerado()
                        + "\nNombre: " + inscriptoGanador.getInscripto().getCorredor().getNombre());
            }
            btnNextBattle.setEnabled(true);
            recargaTblCorredores();
            if (chkProxCarrAuto.isSelected()) {
                proximaCarrera();
            }

        }

    }

    private void muestraEstado(String mensaje) {
        CarrerasLogger.info(FrmTorneo.class, mensaje);
        lblEstado.setText("Estado: " + mensaje);
    }

    /**
     * actualizo los datos de los corredores en los carriles corresp.
     */
    private void recargaCarriles() {
        List<Carril> carriles = competenciaController.getCarriles();
        limpiaCarriles();
        for (com.carreras.dominio.modelo.Carril cc : carriles) {
            if (cc.getNumero() == 1) {
                lblC1Apellido.setText(cc.getInscriptoCompetencia().getInscripto().getCorredor().getApellido());
                lblC1Auto.setText(cc.getInscriptoCompetencia().getInscripto().getAuto().getPatente());
                lblC1Cat.setText(cc.getInscriptoCompetencia().getCategoria().getDescripcion());
                lblC1Nombre.setText(cc.getInscriptoCompetencia().getInscripto().getCorredor().getNombre());
                lblC1Numero.setText(String.valueOf(cc.getInscriptoCompetencia().getNumeroGenerado()));
            } else if (cc.getNumero() == 2) {
                lblC2Apellido.setText(cc.getInscriptoCompetencia().getInscripto().getCorredor().getApellido());
                lblC2Auto.setText(cc.getInscriptoCompetencia().getInscripto().getAuto().getPatente());
                lblC2Cat.setText(cc.getInscriptoCompetencia().getCategoria().getDescripcion());
                lblC2Nombre.setText(cc.getInscriptoCompetencia().getInscripto().getCorredor().getNombre());
                lblC2Numero.setText(String.valueOf(cc.getInscriptoCompetencia().getNumeroGenerado()));
            }
        }

    }

    private void limpiaCarriles() {
        lblC1Apellido.setText("");
        lblC1Auto.setText("");
        lblC1Cat.setText("");
        lblC1Nombre.setText("");
        lblC1Numero.setText("");

        lblC2Apellido.setText("");
        lblC2Auto.setText("");
        lblC2Cat.setText("");
        lblC2Nombre.setText("");
        lblC2Numero.setText("");
    }

    private void filtraCorredores() {
        try {

            Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
            Map modelMap = competenciaController.filtraCorredores(categoriaSeleccionada);

            Boolean finCarreras = (Boolean) modelMap.get("finCarreras");
            if (finCarreras) {
                int status = javax.swing.JOptionPane.showConfirmDialog(this, "Se ha finalizado con todas las carreras disponibles \nCambie Categoria", "Seleccione Accion", javax.swing.JOptionPane.OK_CANCEL_OPTION);
                return;
            }
            recargaTblCorredores();
            recargaCarriles();
            recargaTblTiempos();
        } catch (ClassCastException ex) {
            //ignored
            CarrerasLogger.debug(FrmTorneo.class, "classCastException: " + ex.getMessage());
        }
    }

    private void initComunication() {
        try {
            btnIniciarComunicacion.setEnabled(false);
            ardmgr.inicializa_arduino();
        } catch (IOException ex) {
            System.out.println("ha ocurrido una exception " + ex.getMessage());

            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(rootPane, "Verifique librerias de conexion", "Error Conexion", javax.swing.JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(rootPane, "Verifique que el puerto no este en uso", "Error Conexion", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * finaliza la conexion con el arduino y limpia la tabla 
     * para que puedan seleccionarse nuevos competidores
     */
    private void finalizaComunicacion() {

        try {
            btnIniciarComunicacion.setEnabled(true);
            ardmgr.finaliza_arduino();
        } catch (NullPointerException ex) {
            System.out.println("nunca inicie el arduino");
        } catch (Throwable t) {
            CarrerasLogger.warn(FrmTorneo.class, "throwable: " + t.getMessage());
        }
    }

    /**
     * intenta reinicar comunicacion con el arduino
     */
    private void reiniciaComunicacion() {
        finalizaComunicacion();
        initComunication();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnAgregarCorredor = new javax.swing.JButton();
        btnFinInscripcion = new javax.swing.JButton();
        btnNuevaRonda = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEstadoCorredores = new javax.swing.JTable();
        cmbCategoria = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTiempos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnIniciarComunicacion = new javax.swing.JButton();
        btnNextBattle = new javax.swing.JButton();
        chkProxCarrAuto = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblC1Numero = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblC1Nombre = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblC1Apellido = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblC1Cat = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblC1Auto = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblC2Numero = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblC2Nombre = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblC2Apellido = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblC2Cat = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblC2Auto = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblEstadoGlobal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Corredores"));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAgregarCorredor.setText("Agregar Corredor");
        btnAgregarCorredor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCorredorActionPerformed(evt);
            }
        });
        jPanel1.add(btnAgregarCorredor);

        btnFinInscripcion.setText("Fin Prueba Libre");
        btnFinInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinInscripcionActionPerformed(evt);
            }
        });
        jPanel1.add(btnFinInscripcion);

        btnNuevaRonda.setText("Nueva Ronda");
        btnNuevaRonda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaRondaActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevaRonda);

        tblEstadoCorredores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Carrera Finalizada", "", null},
                {"Corriendo", null, null},
                {"Corriendo", null, null},
                {"Esperando", null, null},
                {null, null, null}
            },
            new String [] {
                "Estado", "Numero", "Categoria"
            }
        ));
        jScrollPane1.setViewportView(tblEstadoCorredores);

        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
            .add(cmbCategoria, 0, 446, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbCategoria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tiempos"));

        tblTiempos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null}
            },
            new String [] {
                "Esperando Tiempos"
            }
        ));
        jScrollPane2.setViewportView(tblTiempos);

        btnIniciarComunicacion.setText("Inicia Comunicacion");
        btnIniciarComunicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarComunicacionActionPerformed(evt);
            }
        });
        jPanel4.add(btnIniciarComunicacion);

        btnNextBattle.setText("Proxima Carrera");
        btnNextBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextBattleActionPerformed(evt);
            }
        });
        jPanel4.add(btnNextBattle);

        chkProxCarrAuto.setText("ProxCarr.Auto");
        jPanel4.add(chkProxCarrAuto);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .add(14, 14, 14)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Carrera Actual"));

        jPanel7.setLayout(new java.awt.GridLayout(1, 0));

        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel8.setLayout(new java.awt.GridLayout(0, 2));

        jLabel2.setText("Carril");
        jPanel8.add(jLabel2);

        jLabel9.setText("1");
        jPanel8.add(jLabel9);

        jLabel4.setText("Numero");
        jPanel8.add(jLabel4);
        jPanel8.add(lblC1Numero);

        jLabel1.setText("Nombre");
        jPanel8.add(jLabel1);
        jPanel8.add(lblC1Nombre);

        jLabel3.setText("Apellido");
        jPanel8.add(jLabel3);
        jPanel8.add(lblC1Apellido);

        jLabel5.setText("Categoria");
        jPanel8.add(jLabel5);
        jPanel8.add(lblC1Cat);

        jLabel7.setText("Auto");
        jPanel8.add(jLabel7);
        jPanel8.add(lblC1Auto);

        jPanel7.add(jPanel8);

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel9.setLayout(new java.awt.GridLayout(0, 2));

        jLabel10.setText("Carril");
        jPanel9.add(jLabel10);

        jLabel11.setText("2");
        jPanel9.add(jLabel11);

        jLabel6.setText("Numero");
        jPanel9.add(jLabel6);
        jPanel9.add(lblC2Numero);

        jLabel12.setText("Nombre");
        jPanel9.add(jLabel12);
        jPanel9.add(lblC2Nombre);

        jLabel14.setText("Apellido");
        jPanel9.add(jLabel14);
        jPanel9.add(lblC2Apellido);

        jLabel16.setText("Categoria");
        jPanel9.add(jLabel16);
        jPanel9.add(lblC2Cat);

        jLabel18.setText("Auto");
        jPanel9.add(jLabel18);
        jPanel9.add(lblC2Auto);

        jPanel7.add(jPanel9);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 516, Short.MAX_VALUE)
            .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 303, Short.MAX_VALUE)
            .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
        );

        lblEstado.setText("Estado Comunicacion");
        lblEstado.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblEstadoGlobal.setText("Estado Actual: Prueba Libre Nro: N / Competicion Ronda: N");
        jPanel6.add(lblEstadoGlobal);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(lblEstado, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(layout.createSequentialGroup()
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblEstado))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarComunicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarComunicacionActionPerformed
        initComunication();
    }//GEN-LAST:event_btnIniciarComunicacionActionPerformed

    private void btnAgregarCorredorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCorredorActionPerformed
        agregarCorredor();
    }//GEN-LAST:event_btnAgregarCorredorActionPerformed

    private void btnNextBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextBattleActionPerformed
        proximaCarrera();
    }//GEN-LAST:event_btnNextBattleActionPerformed

    private void btnFinInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinInscripcionActionPerformed
        finalizaPruebaLibre();
    }//GEN-LAST:event_btnFinInscripcionActionPerformed

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaActionPerformed
        filtraCorredores();
    }//GEN-LAST:event_cmbCategoriaActionPerformed

    private void btnNuevaRondaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaRondaActionPerformed
        nuevaRonda();
    }//GEN-LAST:event_btnNuevaRondaActionPerformed

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
            java.util.logging.Logger.getLogger(FrmTorneo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmTorneo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmTorneo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmTorneo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrmTorneo().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCorredor;
    private javax.swing.JButton btnFinInscripcion;
    private javax.swing.JButton btnIniciarComunicacion;
    private javax.swing.JButton btnNextBattle;
    private javax.swing.JButton btnNuevaRonda;
    private javax.swing.JCheckBox chkProxCarrAuto;
    private javax.swing.JComboBox cmbCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblC1Apellido;
    private javax.swing.JLabel lblC1Auto;
    private javax.swing.JLabel lblC1Cat;
    private javax.swing.JLabel lblC1Nombre;
    private javax.swing.JLabel lblC1Numero;
    private javax.swing.JLabel lblC2Apellido;
    private javax.swing.JLabel lblC2Auto;
    private javax.swing.JLabel lblC2Cat;
    private javax.swing.JLabel lblC2Nombre;
    private javax.swing.JLabel lblC2Numero;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstadoGlobal;
    private javax.swing.JTable tblEstadoCorredores;
    private javax.swing.JTable tblTiempos;
    // End of variables declaration//GEN-END:variables

    private class CorredoresAutosEstadoRenderer implements TableCellRenderer {

        private DefaultTableCellRenderer default_cell_renderer;
        //inscriptos que estan actualmente en uso
        private List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();

        public CorredoresAutosEstadoRenderer(List<InscriptoCompetencia> inscriptosCorriendo) {
            this.inscriptosCorriendo = inscriptosCorriendo;
            default_cell_renderer = new DefaultTableCellRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            JLabel renderer = new JLabel(value.toString());
            renderer.setOpaque(true);
            if (inscriptosCorriendo.isEmpty()) {
                return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            //for some kind of weird reason tries to paint unknown row
            try {
                InscriptoCompetencia ic = inscriptosCorriendo.get(row);
                if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ESPERANDO) {
                    renderer.setBackground(Color.CYAN);
                    return renderer;
                } else if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ROTO || ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ADELANTADO) {
                    renderer.setBackground(Color.GRAY);
                    return renderer;
                } else if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.GANADOR) {
                    renderer.setBackground(Color.GREEN);
                    return renderer;
                } else if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.GANADOR_CATEGORIA) {
                    renderer.setBackground(Color.DARK_GRAY);
                    return renderer;
                } else if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.PERDEDOR) {
                    renderer.setBackground(Color.RED);
                    return renderer;
                } else if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                    renderer.setBackground(Color.BLUE);
                    return renderer;
                }
            } catch (java.lang.IndexOutOfBoundsException ex) {
                //ignored!
            }
            return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class CorredoresAutosCategoriaRenderer implements TableCellRenderer {

        private DefaultTableCellRenderer default_cell_renderer;
        private List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();

        public CorredoresAutosCategoriaRenderer(List<InscriptoCompetencia> inscriptosCorriendo) {
            this.inscriptosCorriendo = inscriptosCorriendo;
            default_cell_renderer = new DefaultTableCellRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            JLabel renderer = new JLabel(value.toString());
            renderer.setOpaque(true);
            if (inscriptosCorriendo.isEmpty()) {
                return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            InscriptoCompetencia ic = inscriptosCorriendo.get(row);
            if (ic.getEstadoCompetencia() == EstadoCompetencia.COMPETENCIA_GANADORES) {
                renderer.setBackground(new Color(23, 246, 200));
                return renderer;
            } else if (ic.getEstadoCompetencia() == EstadoCompetencia.COMPETENCIA_PERDEDORES) {
                renderer.setBackground(new Color(243, 189, 99));
                return renderer;
            }
            return default_cell_renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}