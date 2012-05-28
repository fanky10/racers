/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.gui;

import arduino.entidades.Adelantamiento;
import arduino.entidades.Datos;
import arduino.entidades.Rotura;
import arduino.entidades.Tiempo;
import arduino.entidades.Tiempos;
import arduino.eventos.ArduinoEvent;
import arduino.eventos.ArduinoEventListener;
import arduino.eventos.RespuestaEvent;
import com.carreras.common.config.Configuracion;
import com.carreras.common.logger.CarrerasLogger;
import com.carreras.common.util.ArduinoManager;
import com.carreras.common.util.Utilidades;
import com.carreras.controllers.InscriptoCompetenciaController;
import com.carreras.dominio.modelo.Carrera;
import com.carreras.dominio.modelo.Carril;
import com.carreras.dominio.modelo.Categoria;
import com.carreras.dominio.modelo.Competencia;
import com.carreras.dominio.modelo.EstadoCompetencia;
import com.carreras.dominio.modelo.EstadoInscriptoCompetenciaCarrera;
import com.carreras.dominio.modelo.Inscripto;
import com.carreras.dominio.modelo.InscriptoCompetencia;
import com.carreras.dominio.modelo.TipoCompetencia;
import com.carreras.dominio.modelo.TipoTiempo;
import com.carreras.dominio.modelo.Torneo;
import com.carreras.servicios.ServiceManager;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
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
public class frmCarrera extends javax.swing.JFrame {

    private static final String TITLE = "Carreras v" + Configuracion.getCurrentSysVersion();
    private ArduinoManager ardmgr;
    private static final String obLocker = "aLock";
    private Carrera carreraActual;
    private boolean finalizo_competencia_actual = false;
    public static boolean AUTO_INICIA_CARRERA = true;
    private static final int COLUMNA_CORREDOR_ESTADO = 0;
    private static final int COLUMNA_CORREDOR_CATEGORIA = 2;
    private ServiceManager serviceManager = Configuracion.getInstance().getServiceManager();
    private InscriptoCompetenciaController inscriptoCompetenciaController = new InscriptoCompetenciaController();
    //es una unica instancia para toda la competencia
    private Torneo torneo;
    //esta instancia ira cambiando en el tiempo (:
    private Competencia competenciaActual;
    //inscriptos que estan actualmente en uso
    private List<InscriptoCompetencia> inscriptosCorriendo = new ArrayList<InscriptoCompetencia>();
    //son aquellos que ya fueron seleccionados y que no necesariamente estan corriendo
    //seleccionados.size() >= corriendo.size()
    private List<InscriptoCompetencia> inscriptosSeleccionados = new ArrayList<InscriptoCompetencia>();
    private List<com.carreras.dominio.modelo.Carril> carriles = new ArrayList<com.carreras.dominio.modelo.Carril>();

    /** Creates new form frmCarrera */
    public frmCarrera() {

        initComponents();
//        //initial config
        init();
//        //frame config
        //setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                //cierro el arduino
                finaliza_comunicacion();
                System.exit(0);
            }
        });
        setTitle(TITLE);
        setLocationRelativeTo(null);
    }

    private void init() {

        ardmgr = new ArduinoManager(createEventListener());
        torneo = new Torneo();
        torneo.setFechaHora(new Date(System.currentTimeMillis()));
        torneo.setId(serviceManager.saveTorneo(torneo));
        competenciaActual = new Competencia();
        competenciaActual.setNumeroRonda(1);
        competenciaActual.setTipoCompetencia(TipoCompetencia.LIBRE);
        competenciaActual.setTorneo(torneo);
        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
        cargaCategorias();
        recargaTblCorredores();
        initComunication();
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
    }

    private void cargaCategorias() {
        DefaultComboBoxModel comboboxModel = new DefaultComboBoxModel();
        List<Categoria> categorias = serviceManager.getCategoriasEnUso(competenciaActual);
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
                switch (respuesta_event.getNro_evento()) {
                    case RespuestaEvent.CARRIL:
                        if (respuesta_event.getDatos().getTipo() == Datos.CARRIL) {
                            tiemposCarril(respuesta_event);
                        } else if (respuesta_event.getDatos().getTipo() == Datos.ADELANTAMIENTO) {
                            CarrerasLogger.info(frmCarrera.class, "hubo adelantamiento");
                            adelantamientoCarril(respuesta_event);
                        } else if (respuesta_event.getDatos().getTipo() == Datos.ROTURA) {
                            CarrerasLogger.info(frmCarrera.class, "hubo roturas que");
                            roturaAuto(respuesta_event);
                        }
                        break;
                    case RespuestaEvent.ERROR:
                        muestraEstado(respuesta_event.getError().getMensaje());
                        //reinicia arduino :D
                        reinicia_comunicacion();
                        break;
                }
            }
        };
    }

    private void tiemposCarril(final RespuestaEvent respuesta_event) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {

                    muestraEstado("Recibiendo datos...");
                    arduino.entidades.Carril carrilDatos = (arduino.entidades.Carril) respuesta_event.getDatos();
                    CarrerasLogger.info(frmCarrera.class, "actualizando tiempos de carril: " + carrilDatos.getNro_carril());
                    for (com.carreras.dominio.modelo.Carril carril : carriles) {
                        if (carril.getNumero() == carrilDatos.getNro_carril()) {
                            //debemos actualizar los tiempos en la db (:
                            Tiempos tiemposDatos = carrilDatos.getTiempoV();
                            for (Tiempo t : tiemposDatos) {
                                double dTiempo = t.getTiempo();
                                int tipoTiempo = t.getTipo_tiempo();
                                TipoTiempo tipoTiempoModel = serviceManager.getTipoTiempo(tipoTiempo);
                                com.carreras.dominio.modelo.Tiempo tiempoModel = new com.carreras.dominio.modelo.Tiempo();
                                tiempoModel.setCarril(carril);
                                tiempoModel.setTiempo((float) dTiempo);
                                tiempoModel.setTipoTiempo(tipoTiempoModel);
                                tiempoModel.setId(serviceManager.saveTiempo(tiempoModel));

                                Float tiempoMinimo = carril.getInscriptoCompetencia().getCategoria().getTiempoMaximo();
                                //TODO: caida de tiempo 
                                if (tipoTiempo == Tiempo.TIEMPO_FIN
                                        && tiempoMinimo > 1
                                        && dTiempo < tiempoMinimo) {
                                    Boolean caido = true;
                                }

                            }
                        }
                    }
                    recargaTblTiempos();
                } catch (IllegalArgumentException ex) {
                    //ignorada, me llego datos de un carril que nada que ver xF
                }
            }
        });
    }

    private void adelantamientoCarril(final RespuestaEvent respuesta_event) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {

                    Adelantamiento adelantamiento = (Adelantamiento) respuesta_event.getDatos();
                    for (com.carreras.dominio.modelo.Carril carril : carriles) {
                        if (carril.getNumero() == adelantamiento.getNro_carril()) {
                            carril.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.ADELANTADO);
                            serviceManager.saveInscriptoCompetencia(carril.getInscriptoCompetencia());
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    //ignorada, me llego datos de un carril que nada que ver xF
                }
            }
        });

    }

    private void roturaAuto(final RespuestaEvent respuesta_event) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    Rotura rotura = (Rotura) respuesta_event.getDatos();
                    for (com.carreras.dominio.modelo.Carril carril : carriles) {
                        if (carril.getNumero() == rotura.getNro_carril()) {
                            carril.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.ROTO);
                            serviceManager.saveInscriptoCompetencia(carril.getInscriptoCompetencia());
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    //ignorada, me llego datos de un carril que nada que ver xF
                }
            }
        });
    }

    /**
     * actualizo los tiempos de los corredores
     */
    private void recargaTblTiempos() {
        //TODO: cambiar esto por algun algoritmo que chequee que no hay tiempos.
        final DefaultTableModel tmodel = new NotEditableTableModel();
        if (carriles.isEmpty()) {
            tmodel.setColumnIdentifiers(new Object[]{"Sin Datos - Agrege un inscripto"});
            tblTiempos.setModel(tmodel);
        } else {
            tmodel.setColumnIdentifiers(new Object[]{"Carril", "Nro", "Patente", "TipoTiempo", "Tiempo"});
            Boolean isCarreraOver = true;
            for (Carril carrilModelo : carriles) {
                if (carrilModelo.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.ADELANTADO) {
                    tmodel.addRow(new Object[]{EstadoInscriptoCompetenciaCarrera.ADELANTADO, "", "", "", ""});
                } else if (carrilModelo.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.ROTO) {
                    tmodel.addRow(new Object[]{EstadoInscriptoCompetenciaCarrera.ROTO, "", "", "", ""});
                } else {
                    //tiempos!
                    List<com.carreras.dominio.modelo.Tiempo> tiemposModel = serviceManager.getTiemposCarril(carrilModelo);
                    if (tiemposModel.isEmpty()) {
                        //si al menos hay uno sin tiempos, la carrera NO termino!
                        isCarreraOver = false;
                    }
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
            if (isCarreraOver) {
                final TipoTiempo decisorio = serviceManager.getTipoTiempo(TipoTiempo.ID_TIEMPO_FIN);
                //db consistance checker
                if (decisorio == null) {
                    throw new IllegalArgumentException("bad configuration tipoTiempos, idTiempoFin desconocido: " + TipoTiempo.ID_TIEMPO_FIN);
                }
                Collections.sort(carriles, new Comparator<Carril>() {

                    @Override
                    public int compare(Carril t1, Carril t2) {

                        com.carreras.dominio.modelo.Tiempo tCarril1 = serviceManager.getTiempo(t1, decisorio);
                        com.carreras.dominio.modelo.Tiempo tCarril2 = serviceManager.getTiempo(t2, decisorio);

                        return tCarril1.getTiempo().compareTo(tCarril2.getTiempo());

                    }
                });
                //chequeamos una caida de tiempo, de darse eso, el siguiente con menor tiempo es ganador.
                chequeaCaidaTiempo(carriles);
                int i = 0;
                for (Carril cc : carriles) {
                    //TODO: check el tiempo que saco (:
                    //una vez ordenados, el primero --> break! naa chequear el maximo 
                    if (i == 0 && cc.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                        cc.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);

                    }//no es el primer por comparacion de tiempos, y esta corriendo (no tiene asignado estado) 
                    else if (cc.getInscriptoCompetencia().getEstado() == EstadoInscriptoCompetenciaCarrera.CORRIENDO) {
                        cc.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.PERDEDOR);
                    }
                    //actualizo su estado final en la db
                    //si se quiere el estado puede cambiar en el tiempo y se hace un save con el tstamp
                    serviceManager.updateInscriptoCompetencia(cc.getInscriptoCompetencia());
                    i++;
                }
                javax.swing.JOptionPane.showMessageDialog(this, "Carrera finalizada! "
                        + "\nGanador: " + carriles.get(0).getInscriptoCompetencia().getNumeroGenerado()
                        + "\nNombre: " + carriles.get(0).getInscriptoCompetencia().getInscripto().getCorredor().getNombre());
                btnNextBattle.setEnabled(true);
                recargaTblCorredores();
                if (chkProxCarrAuto.isSelected()) {
                    proximaCarrera();
                }

            }
        }

    }

    private void chequeaCaidaTiempo(List<Carril> carriles) {
        final TipoTiempo decisorio = serviceManager.getTipoTiempo(TipoTiempo.ID_TIEMPO_FIN);
        for (Carril c : carriles) {
            com.carreras.dominio.modelo.Tiempo tCarril1 = serviceManager.getTiempo(c, decisorio);
            if (tCarril1.getTiempo() < c.getInscriptoCompetencia().getCategoria().getTiempoMaximo()) {
                //listo, se cayo el pobrecin!
                c.getInscriptoCompetencia().setEstado(EstadoInscriptoCompetenciaCarrera.CAIDO_CATEGORIA);
            }
        }
    }

    private void decrementaRondasRestantes(InscriptoCompetencia inscriptoCompetencia) {
        if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
            inscriptoCompetencia.decrementaRondasRestantes();
        }
    }

    private void muestraEstado(String mensaje) {
        CarrerasLogger.info(frmCarrera.class, mensaje);
        lblEstado.setText("Estado: " + mensaje);
    }

    /**
     * agrega un nuevo corredor a la lista de corredores que 
     * pueden o no, estar jugando una linda carrera :P
     */
    private void agregarCorredor() {
        diagABMInscripto diag = new diagABMInscripto(this, inscriptosSeleccionados);
        diag.setVisible(true);
        diag.dispose();
        if (!diagABMInscripto.CANCELADO) {
            Inscripto inscripto = diag.getInscripto();
            Integer nroRondas = diag.getNroRondas();
            InscriptoCompetencia inscriptoCompetencia = new InscriptoCompetencia();
            inscriptoCompetencia.setCategoria(serviceManager.getCategoria(Categoria.ID_CATEGORIA_LIBRE));
            inscriptoCompetencia.setRondasRestantes(nroRondas);
            inscriptoCompetencia.setCompetencia(competenciaActual);
            inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_NO_DEFINIDA);
            inscriptoCompetencia.setInscripto(inscripto);
            inscriptoCompetencia.generaNumero();
            if (nroRondas > 0) {
                inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
            } else {
                inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.NO_CORRE);
            }
            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
            //save and add to inscriptosSeleccionados (:
            inscriptosSeleccionados.add(inscriptoCompetencia);
            if (nroRondas > 0) {
                inscriptosCorriendo.add(inscriptoCompetencia);
                agregaCarril(inscriptoCompetencia);
                recargaTblCorredores();
                cargaCategorias();
            }//si no participa ni me gasto (:
        }
    }

    private void proximaCarrera() {
        //reinicio att.
        carreraActual = null;
        carriles = new ArrayList<Carril>();

        for (InscriptoCompetencia ic : inscriptosCorriendo) {
            if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ESPERANDO && !agregaCarril(ic)) {
                break;
            }
        }
        if (carriles.isEmpty()) {
            int status = javax.swing.JOptionPane.showConfirmDialog(this, "Se ha finalizado con todas las carreras disponibles \nGenere una nueva ronda o competencia", "Seleccione Accion", javax.swing.JOptionPane.OK_CANCEL_OPTION);
            if (status == javax.swing.JOptionPane.OK_OPTION) {
                nueva_ronda();
            }
            return;
        }
        recargaTblCorredores();
        recargaCarriles();
        recargaTblTiempos();
    }

    /**
     * agrega carril s/sea necesario
     * @return true carril agregado, false otherwise
     */
    private boolean agregaCarril(InscriptoCompetencia inscriptoCompetencia) {
        if (carreraActual == null) {
            carreraActual = new Carrera();
            carreraActual.setId(serviceManager.saveCarrera(carreraActual));
        }
        //se agrega el carril en cuestion
        //averiguo primero si no es el ganador.
        Integer sizeInscriptosCategoria = serviceManager.getInscriptosCompetencia(competenciaActual, inscriptoCompetencia.getCategoria()).size();
        //si no puedo agregar inscriptos y para mi categoria soy el unico --> GANADOR!
        if (!btnAgregarCorredor.isEnabled() && sizeInscriptosCategoria == 1) {//es el ganador, FIN
            //ganador final! --algun estado en particular?
            //TODO: estado particular!
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.GANADOR);
            return false;

        } else if (carriles.isEmpty()) {
            //agrego el primer carril
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);
            recargaCarriles();
            return true;
        }//agrego los demas carriles, si hay carriles disponibles
        else if (carriles.size() < Configuracion.getInstance().getCant_carriles()
                //y son de la misma categoria y competencia
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getCategoria().getId().equals(inscriptoCompetencia.getCategoria().getId())
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getEstadoCompetencia().getId().equals(inscriptoCompetencia.getEstadoCompetencia().getId())) {
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);
            recargaCarriles();
            return true;
        } else if (carriles.size() == 1
                //y son de la misma categoria pero distinto estado inicial de comp.
                && carriles.get(carriles.size() - 1).getInscriptoCompetencia().getCategoria().getId().equals(inscriptoCompetencia.getCategoria().getId())
                && !carriles.get(carriles.size() - 1).getInscriptoCompetencia().getEstadoCompetencia().getId().equals(inscriptoCompetencia.getEstadoCompetencia().getId())
                //chequeo que sean los ultimos dos
                && sizeInscriptosCategoria == 2) {
            //OPC A
            //LOOPEAR EN UNA NUEVA COMPETENCIA DONDE SIMPLEMENTE ESTEN JUGANDO ESTOS DOS MAMOCHOS
            //al finalizar la 3er ronda --> ganador de categoria!
            //setear el nro de ronda final xq siempre los va a elegir 
            //como c/ronda es independiente, se genera un tipo de ronda final
            //elegirlos indefinidamente (probar eso)
            //eliminarlos cuando hayan llegado al nro-ronda final

            //OPC B
            //MANDARLOS A UNA COLA DE FINALES
            com.carreras.dominio.modelo.Carril carril = new com.carreras.dominio.modelo.Carril();
            carril.setCarrera(carreraActual);
            carril.setInscriptoCompetencia(inscriptoCompetencia);
            carril.setNumero(carriles.size() + 1);
            carril.setId(serviceManager.saveCarril(carril));
            carriles.add(carril);
            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.CORRIENDO);
            recargaCarriles();
            return true;
        }
        return false;
    }

    /**
     * actualiza la tabla de corredores con sus estados corresp.
     */
    private void recargaTblCorredores() {
        recargaTblCorredores(inscriptosCorriendo);
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
                tblEstadoCorredores.getColumnModel().getColumn(COLUMNA_CORREDOR_ESTADO).setCellRenderer(new CorredoresAutosEstadoRenderer());
                tblEstadoCorredores.getColumnModel().getColumn(COLUMNA_CORREDOR_CATEGORIA).setCellRenderer(new CorredoresAutosCategoriaRenderer());
                Utilidades.scrollToVisible(tblEstadoCorredores, linea_corriendo, 0);
            }
        }
    }

    /**
     * actualizo los datos de los corredores en los carriles corresp.
     */
    private void recargaCarriles() {
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
    //TODO: check if current ronda tiene jugadores sin participar
    //TODO: enable nuevaRondaBtn.

    private void nueva_ronda() {


//        Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
//        inscriptosCorriendo.clear();
//        carriles.clear();
//        System.err.println("[HARD DEBUG] - trayendo inscriptos de comp actual y cat.selected");
//        //traigo todos los inscriptos filtrados s/la competencia y categoria que acaba de terminar
//        List<InscriptoCompetencia> nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual,categoriaSeleccionada);
//        //mini arbol de decision:
//        //si es libre, como siempre 
////        if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
////            nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual);
////        }else{//eliminatoria, final whatsoever.
////            nuevosInscriptos = serviceManager.getInscriptosCompetencia(competenciaActual,categoriaSeleccionada);
////            //chequear que onda aca tmb... 
////        }
//        System.err.println("[HARD DEBUG] - inscriptos.size() " + nuevosInscriptos.size());
//        //si no es libre, check categoria seleccionada
//        if (nuevosInscriptos.isEmpty()) {
//            javax.swing.JOptionPane.showMessageDialog(rootPane, "No quedan jugadores con Rondas Restantes");
//            if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
//                finInscripcion();
//            }
//            return;
//        }
//        int rondaActual = competenciaActual.getNumeroRonda();
//        TipoCompetencia tipoCompetencia = competenciaActual.getTipoCompetencia();
//        competenciaActual = new Competencia();
//        competenciaActual.setNumeroRonda(rondaActual + 1);
//        competenciaActual.setTipoCompetencia(tipoCompetencia);
//        competenciaActual.setTorneo(torneo);
//        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
//        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
//
//        for (InscriptoCompetencia inscriptoCompetencia : nuevosInscriptos) {
//            //estan iniciando una nueva ronda, decrementamos
//            decrementaRondasRestantes(inscriptoCompetencia);
//            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
//            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
//            inscriptoCompetencia.generaNumero();
//            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
//            inscriptosCorriendo.add(inscriptoCompetencia);
//            agregaCarril(inscriptoCompetencia);
//        }
        Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
        Map modelMap = inscriptoCompetenciaController.generaNuevaRonda(torneo, competenciaActual, categoriaSeleccionada);
        inscriptosCorriendo = null;
        carriles = null;
        inscriptosCorriendo = (List<InscriptoCompetencia>) modelMap.get("inscriptos");
        competenciaActual = (Competencia) modelMap.get("competencia");
        carriles = (List<Carril>) modelMap.get("carriles");
        InscriptoCompetencia ganador = (InscriptoCompetencia) modelMap.get("ganadorCompetencia");
        if(competenciaActual.getTipoCompetencia() == TipoCompetencia.FINAL && competenciaActual.getNumeroRonda() == 3){
            javax.swing.JOptionPane.showMessageDialog(rootPane, "Estamos en la final!!");
        }
        if(ganador!=null){
            javax.swing.JOptionPane.showMessageDialog(rootPane, "el ganador es: "+ganador.getInscripto().getCorredor().getNombre());
            //con auto etc.
            //TODO: eliminar la categoria actual si no hay mas cat. disp. chau o sino ir a la proxima (:
        }
        if (inscriptosCorriendo.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(rootPane, "No quedan jugadores con Rondas Restantes");
            if (competenciaActual.getTipoCompetencia() == TipoCompetencia.LIBRE) {
                finInscripcion();
            }
            return;
        }
        for (InscriptoCompetencia inscriptoCompetencia : inscriptosCorriendo) {
            agregaCarril(inscriptoCompetencia);
        }
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
        recargaTblCorredores();
        recargaTblTiempos();
    }

    /**
     * cuando concluye la inscripcion ya no se puede hacer mas nada
     * se le generan las categorias a los inscriptos, y se pueden modificar mediante gui
     * luego de eso, mantinen la categoria hasta finalizado el encuentro
     */
    private void finInscripcion() {
//        diagEditCategoria diag = new diagEditCategoria(this, true, competenciaActual);
//        diag.setVisible(true);
//        diag.dispose();
//        inscriptosCorriendo.clear();
//        carriles.clear();
//        competenciaActual = new Competencia();
//        competenciaActual.setNumeroRonda(1);
//        competenciaActual.setTipoCompetencia(TipoCompetencia.ELIMINATORIA);
//        competenciaActual.setTorneo(torneo);
//        competenciaActual.setId(serviceManager.saveCompetencia(competenciaActual));
//        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
//        //actualizamos los inscriptos para la nueva competencia generada 
//        //TODO: check this!
//        //porque de la recategorizacion obtengo los ganadores / perdedores..?
//        //xq es un.. ?
//        for (InscriptoCompetencia inscriptoCompetencia : diag.getInscriptosCompetencia()) {
////            if(inscriptoCompetencia.getCategoria().getId() == Categoria.ID_CATEGORIA_NO_CORRE){
////                continue ;
////            }
//            inscriptoCompetencia.setCompetencia(competenciaActual);//actualizo la competencia
//            inscriptoCompetencia.setEstado(EstadoInscriptoCompetenciaCarrera.ESPERANDO);
//            inscriptoCompetencia.generaNumero();
//            inscriptoCompetencia.setId(serviceManager.saveInscriptoCompetencia(inscriptoCompetencia));
//            inscriptosCorriendo.add(inscriptoCompetencia);
//            agregaCarril(inscriptoCompetencia);
//
//            if (inscriptoCompetencia.getEstado() == EstadoInscriptoCompetenciaCarrera.GANADOR) {
//                inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_GANADORES);
//            } else {
//                inscriptoCompetencia.setEstadoCompetencia(EstadoCompetencia.COMPETENCIA_PERDEDORES);
//            }
//
//        }
        diagEditCategoria diag = new diagEditCategoria(this, true, competenciaActual);
        diag.setVisible(true);
        diag.dispose();
        Map modelMap = inscriptoCompetenciaController.finalizaInscripcion(torneo, competenciaActual, diag.getInscriptosCompetencia());
        inscriptosCorriendo = null;
        carriles = null;
        inscriptosCorriendo = (List<InscriptoCompetencia>) modelMap.get("inscriptos");
        competenciaActual = (Competencia) modelMap.get("competencia");
        carriles = (List<Carril>) modelMap.get("carriles");
        if (inscriptosCorriendo.isEmpty()) {
            throw new IllegalArgumentException("al generar finalizar la competencia libre no pueden quedarse sin inscriptos!!");
        }
        for (InscriptoCompetencia inscriptoCompetencia : inscriptosCorriendo) {
            agregaCarril(inscriptoCompetencia);
        }
        lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
        recargaTblCorredores();
        recargaTblTiempos();
        cargaCategorias();
        btnFinInscripcion.setEnabled(false);
        btnAgregarCorredor.setEnabled(false);
        cmbCategoria.setSelectedIndex(0);
    }

    private void filtraCorredores() {
        try {
            Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
            //override la competencia actual, por la ultima para la categoria seleccionada y luego, traer a los que estan juegando
            //tener en cuenta que hace dentro el manager (:
            //System.err.println("proxima carrera... [competencia]");
            competenciaActual = serviceManager.getCompetenciaActual(categoriaSeleccionada);
            lblEstadoGlobal.setText("Estado Actual: " + competenciaActual.getTipoCompetencia().getDescripcion() + " Competicion Ronda: " + competenciaActual.getNumeroRonda());
            //System.err.println("proxima carrera... [inscriptos]");
            inscriptosCorriendo = serviceManager.getEstadoInscriptosCompetencia(competenciaActual, EstadoInscriptoCompetenciaCarrera.ESPERANDO, categoriaSeleccionada);
            // es mas o menos la misma logica de prox. carrera.. 
            //reinicio att.
            carreraActual = null;
            carriles = new ArrayList<Carril>();

            for (InscriptoCompetencia ic : inscriptosCorriendo) {
                if (ic.getEstado() == EstadoInscriptoCompetenciaCarrera.ESPERANDO && agregaCarril(ic)) {
                    //System.err.println("proxima carrera... [carril agregado!]");
                } else {
                    break;
                }
            }
            if (carriles.isEmpty()) {
                int status = javax.swing.JOptionPane.showConfirmDialog(this, "Se ha finalizado con todas las carreras disponibles \nCambie Categoria", "Seleccione Accion", javax.swing.JOptionPane.OK_CANCEL_OPTION);
                return;
            }
            //System.err.println("proxima carrera... [tblReload]");
            recargaTblCorredores(inscriptosCorriendo);
            //System.err.println("proxima carrera... [carrilReload]");
            recargaCarriles();
            //System.err.println("proxima carrera... [tiemposReload]");
            recargaTblTiempos();
            //System.err.println("fin prox. carrera...");
        } catch (ClassCastException ex) {
            //ignored
            CarrerasLogger.debug(frmCarrera.class, "classCastException: " + ex.getMessage());
        }
    }

    private void initComunication() {
        try {
            btnIniciarCarrera.setEnabled(false);
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
    private void finaliza_comunicacion() {

        try {
            btnIniciarCarrera.setEnabled(true);
            ardmgr.finaliza_arduino();
        } catch (NullPointerException ex) {
            System.out.println("nunca inicie el arduino");
        } catch (Throwable t) {
            CarrerasLogger.warn(frmCarrera.class, "throwable: " + t.getMessage());
        }
    }

    /**
     * intenta reinicar comunicacion con el arduino
     */
    private void reinicia_comunicacion() {
        finaliza_comunicacion();
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

        pnlCorredores = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEstadoCorredores = new javax.swing.JTable();
        pnlOpcCorredores = new javax.swing.JPanel();
        btnAgregarCorredor = new javax.swing.JButton();
        btnFinInscripcion = new javax.swing.JButton();
        btnNuevaRonda = new javax.swing.JButton();
        cmbCategoria = new javax.swing.JComboBox();
        pnlCarreraActual = new javax.swing.JPanel();
        pnlTiemposCompetidores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTiempos = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
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
        jPanel3 = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        btnIniciarCarrera = new javax.swing.JButton();
        btnNextBattle = new javax.swing.JButton();
        chkProxCarrAuto = new javax.swing.JCheckBox();
        pnlEstadoGlobal = new javax.swing.JPanel();
        lblEstadoGlobal = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlCorredores.setBorder(javax.swing.BorderFactory.createTitledBorder("Corredores"));

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

        btnAgregarCorredor.setText("Agregar Corredor");
        btnAgregarCorredor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCorredorActionPerformed(evt);
            }
        });
        pnlOpcCorredores.add(btnAgregarCorredor);

        btnFinInscripcion.setText("Fin Prueba Libre");
        btnFinInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinInscripcionActionPerformed(evt);
            }
        });
        pnlOpcCorredores.add(btnFinInscripcion);

        btnNuevaRonda.setText("Nueva Ronda");
        btnNuevaRonda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaRondaActionPerformed(evt);
            }
        });
        pnlOpcCorredores.add(btnNuevaRonda);

        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlCorredoresLayout = new org.jdesktop.layout.GroupLayout(pnlCorredores);
        pnlCorredores.setLayout(pnlCorredoresLayout);
        pnlCorredoresLayout.setHorizontalGroup(
            pnlCorredoresLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlOpcCorredores, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
            .add(pnlCorredoresLayout.createSequentialGroup()
                .addContainerGap()
                .add(cmbCategoria, 0, 408, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlCorredoresLayout.setVerticalGroup(
            pnlCorredoresLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCorredoresLayout.createSequentialGroup()
                .add(pnlOpcCorredores, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbCategoria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
        );

        pnlCarreraActual.setBorder(javax.swing.BorderFactory.createTitledBorder("Carrera Actual"));

        pnlTiemposCompetidores.setBorder(javax.swing.BorderFactory.createTitledBorder("Tiempos Corredores"));
        pnlTiemposCompetidores.setLayout(new java.awt.BorderLayout());

        tblTiempos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblTiempos);

        pnlTiemposCompetidores.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel2.setLayout(new java.awt.GridLayout(0, 2));

        jLabel2.setText("Carril");
        jPanel2.add(jLabel2);

        jLabel9.setText("1");
        jPanel2.add(jLabel9);

        jLabel4.setText("Numero");
        jPanel2.add(jLabel4);
        jPanel2.add(lblC1Numero);

        jLabel1.setText("Nombre");
        jPanel2.add(jLabel1);
        jPanel2.add(lblC1Nombre);

        jLabel3.setText("Apellido");
        jPanel2.add(jLabel3);
        jPanel2.add(lblC1Apellido);

        jLabel5.setText("Categoria");
        jPanel2.add(jLabel5);
        jPanel2.add(lblC1Cat);

        jLabel7.setText("Auto");
        jPanel2.add(jLabel7);
        jPanel2.add(lblC1Auto);

        jPanel7.add(jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel3.setLayout(new java.awt.GridLayout(0, 2));

        jLabel10.setText("Carril");
        jPanel3.add(jLabel10);

        jLabel11.setText("2");
        jPanel3.add(jLabel11);

        jLabel6.setText("Numero");
        jPanel3.add(jLabel6);
        jPanel3.add(lblC2Numero);

        jLabel12.setText("Nombre");
        jPanel3.add(jLabel12);
        jPanel3.add(lblC2Nombre);

        jLabel14.setText("Apellido");
        jPanel3.add(jLabel14);
        jPanel3.add(lblC2Apellido);

        jLabel16.setText("Categoria");
        jPanel3.add(jLabel16);
        jPanel3.add(lblC2Cat);

        jLabel18.setText("Auto");
        jPanel3.add(jLabel18);
        jPanel3.add(lblC2Auto);

        jPanel7.add(jPanel3);

        btnIniciarCarrera.setText("Inicia Comunicacion");
        btnIniciarCarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarCarreraActionPerformed(evt);
            }
        });
        jPanel1.add(btnIniciarCarrera);

        btnNextBattle.setText("Proxima Carrera");
        btnNextBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextBattleActionPerformed(evt);
            }
        });
        jPanel1.add(btnNextBattle);

        chkProxCarrAuto.setText("ProxCarr.Auto");
        jPanel1.add(chkProxCarrAuto);

        org.jdesktop.layout.GroupLayout pnlCarreraActualLayout = new org.jdesktop.layout.GroupLayout(pnlCarreraActual);
        pnlCarreraActual.setLayout(pnlCarreraActualLayout);
        pnlCarreraActualLayout.setHorizontalGroup(
            pnlCarreraActualLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTiemposCompetidores, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
            .add(pnlCarreraActualLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                .add(8, 8, 8))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
        );
        pnlCarreraActualLayout.setVerticalGroup(
            pnlCarreraActualLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCarreraActualLayout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 263, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTiemposCompetidores, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .add(14, 14, 14)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(26, 26, 26))
        );

        lblEstadoGlobal.setText("Estado Actual: Prueba Libre Nro: N / Competicion Ronda: N");
        pnlEstadoGlobal.add(lblEstadoGlobal);

        lblEstado.setText("Estado Comunicacion");
        lblEstado.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblEstado, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1237, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(pnlCorredores, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlCarreraActual, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlEstadoGlobal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1237, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlEstadoGlobal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlCorredores, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnlCarreraActual, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblEstado, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarCorredorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCorredorActionPerformed
        agregarCorredor();
    }//GEN-LAST:event_btnAgregarCorredorActionPerformed

    private void btnFinInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinInscripcionActionPerformed
        finInscripcion();
    }//GEN-LAST:event_btnFinInscripcionActionPerformed

    private void btnIniciarCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarCarreraActionPerformed
        initComunication();
    }//GEN-LAST:event_btnIniciarCarreraActionPerformed

    private void btnNuevaRondaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaRondaActionPerformed
        nueva_ronda();
    }//GEN-LAST:event_btnNuevaRondaActionPerformed

    private void btnNextBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextBattleActionPerformed
        proximaCarrera();
    }//GEN-LAST:event_btnNextBattleActionPerformed

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaActionPerformed
        filtraCorredores();
    }//GEN-LAST:event_cmbCategoriaActionPerformed

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
            java.util.logging.Logger.getLogger(frmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCarrera.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new frmCarrera().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCorredor;
    private javax.swing.JButton btnFinInscripcion;
    private javax.swing.JButton btnIniciarCarrera;
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
    private javax.swing.JPanel jPanel7;
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
    private javax.swing.JPanel pnlCarreraActual;
    private javax.swing.JPanel pnlCorredores;
    private javax.swing.JPanel pnlEstadoGlobal;
    private javax.swing.JPanel pnlOpcCorredores;
    private javax.swing.JPanel pnlTiemposCompetidores;
    private javax.swing.JTable tblEstadoCorredores;
    private javax.swing.JTable tblTiempos;
    // End of variables declaration//GEN-END:variables
//se podria implementar con un dtmodel que internamente haga un invokelater
//src: http://www.javakb.com/Uwe/Forum.aspx/java-gui/4968/Exception-in-Swing-code-using-a-JTable

    private class TiemposTableModel extends DefaultTableModel {

        public TiemposTableModel(Object object[], int row) {
            super(object, row);
        }

        public TiemposTableModel() {
            super();
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class CorredoresAutosEstadoRenderer implements TableCellRenderer {

        private DefaultTableCellRenderer default_cell_renderer;

        public CorredoresAutosEstadoRenderer() {
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

        public CorredoresAutosCategoriaRenderer() {
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
