/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import javax.swing.JDesktopPane;
import modelo.ControladorVision;

/**
 *
 * @author andreaescribano
 */
public class ControladorVistaEscalar {
    //private VistaPrincipal vistaP;
    private VistaEscalarManual vistaEM;
    private VistaEscalarAutomatico vistaEA;
    private String nombreFichero;
    private ControladorVision controlV;
    private ControladorVistaPrincipal controlVP;
    private JDesktopPane desktop;
    
    ControladorVistaEscalar(ControladorVistaPrincipal controlVP) {
        this.controlVP = controlVP;
        vistaEM = new VistaEscalarManual(this);
        controlV = new ControladorVision(this);
        vistaEA = new VistaEscalarAutomatico(this);
    }

    public void cancelarEscalado() {
        if (vistaEM.isVisible()) {
            vistaEM.setVisible(false);
            vistaEM.dispose();
        }
        if (vistaEA.isVisible()) {
            vistaEA.setVisible(false);
            vistaEA.dispose();
        }
        controlVP.cancelarEscalado();
    }

    public void escalarManual(String title, JDesktopPane desktop) {
        vistaEM.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }

    public void escalarAutomatico(String title, JDesktopPane desktop) {
        vistaEA.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }
    
    public void escalarManual(String x, String y, String z){
        float xValor, yValor, zValor;
        if(x.isEmpty()){
            xValor = 0f;
        }else {
            xValor = Float.parseFloat(x);
        }
        if(y.isEmpty()){
            yValor = 0f;
        }else{
            yValor = Float.parseFloat(y);
        }
        if(z.isEmpty()){
            zValor = 0f;
        }else{
            zValor = Float.parseFloat(z);
        }
        controlV.escalarManual(nombreFichero, xValor, yValor, zValor, controlVP);
        vistaEM.setVisible(false);
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

    public void escalarAutomatico(String text) {
        
    }
    
    
}
