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
public class ControladorVistaRotar {

//    private VistaPrincipal vistaP;
    private VistaRotarManual vistaRM;
    private VistaRotarAutomatico vistaRA;
    private String nombreFichero;
    private ControladorVision controlV;
    private ControladorVistaPrincipal controlVP;
    private JDesktopPane desktop;

    ControladorVistaRotar(ControladorVistaPrincipal controlVP) {
        this.controlVP = controlVP;
        vistaRM = new VistaRotarManual(this);
        controlV = new ControladorVision(this);
        vistaRA = new VistaRotarAutomatico(this);
    }

    public void rotarManual(String nombre, JDesktopPane desktop) {
        vistaRM.setVisible(true);
        this.nombreFichero = nombre;
        this.desktop = desktop;
    }

    public void rotarAutomatico(String title, JDesktopPane desktop) {
        vistaRA.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }

    public void cancelarRotacion() {
        if (vistaRM.isVisible()) {
            vistaRM.setVisible(false);
            vistaRM.dispose();
        }
        if (vistaRA.isVisible()) {
            vistaRA.setVisible(false);
            vistaRA.dispose();
        }
        controlVP.cancelarRotacion();
    }

    public void rotarManual(String x, String y, String z) {
        float xValor, yValor, zValor;
        if (x.isEmpty()) {
            xValor = 0f;
        } else {
            xValor = Float.parseFloat(x);
        }
        if (y.isEmpty()) {
            yValor = 0f;
        } else {
            yValor = Float.parseFloat(y);
        }
        if (z.isEmpty()) {
            zValor = 0f;
        } else {
            zValor = Float.parseFloat(z);
        }
        controlV.rotarManual(nombreFichero, xValor, yValor, zValor, controlVP);
        vistaRM.setVisible(false);
        vistaRM.dispose();
    }

    public void rotarAutomatico(String angulo) {
        float a;
        if(angulo.isEmpty()){
            a = 0f;
        }else{
            a = Float.parseFloat(angulo);
        }
        controlV.rotarAutomatico(nombreFichero, a,controlVP);
        vistaRA.setVisible(false);
        vistaRA.dispose();
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

}
