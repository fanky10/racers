/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carreras.common.util;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author lacha
 */
public class LabelConFade {
Timer timer = null;
    javax.swing.JLabel lbl;
    String mensaje;
    static int maximo_creado=0;
    int cc=240;
    int id_creado=0;
    int color=0;
            
    public LabelConFade(javax.swing.JLabel lbl, String mensaje){
        this(lbl, mensaje, 0);
    }
    
    /**@param color 0: rojo 1: azul 3: verde
     */
    public LabelConFade(javax.swing.JLabel lbl, String mensaje, int color){
        this.lbl=lbl;
        this.mensaje=mensaje;
        ++maximo_creado;
        id_creado=maximo_creado;
        this.color=color;
    }
    
    public void go(){
        if(cc>=0){
            AccionDeTiempo adt=new AccionDeTiempo(lbl, mensaje, cc, id_creado);
            timer=new Timer (300, adt);
            adt.setTimer(timer);
            timer.start();
        }
    }
    
    class AccionDeTiempo implements ActionListener {
        Timer timer;
        javax.swing.JLabel lbl;
        String mensaje;
        int contador;
        int id_creado;
        
        AccionDeTiempo(javax.swing.JLabel lbl, String mensaje, int contador, int id_creado){
            this.lbl=lbl;
            this.mensaje=mensaje;
            this.contador=contador;
            this.id_creado=id_creado;
            setColor();
            lbl.setText(mensaje);
        }
        
        public void setTimer(Timer timer){
            this.timer=timer;
        }

        public void setColor(){
            switch(color){
                case 0:
                    lbl.setForeground(new Color(255, 240-contador, 240-contador));
                    break;
                case 1:
                    lbl.setForeground(new Color(240-contador, 240-contador, 255));
                    break;
                case 2:
                    lbl.setForeground(new Color(240-contador, 220, 240-contador));
                    break;
            }
        }
        
        public void actionPerformed(ActionEvent e){
            if(contador>0 && maximo_creado==id_creado){
                lbl.setText(mensaje);
                contador-=7;
                lbl.updateUI();
                setColor();
            }else{
                lbl.setText("");
                if(timer!=null){
                    timer.stop();
                }
            }
        }
    }
}

    

