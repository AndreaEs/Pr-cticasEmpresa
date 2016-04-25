package presentacion;

import javax.swing.JDesktopPane;
import modelo.ControladorVision;

/**
 * Controlador del escalado de un objeto
 * @author A3D Ingeniería
 */
public class ControladorVistaEscalar {
    
    //Atributos
    //private VistaPrincipal vistaP;
    private VistaEscalarManual vistaEM;
    private VistaEscalarAutomatico vistaEA;
    private String nombreFichero;
    private ControladorVision controlV;
    private ControladorVistaPrincipal controlVP;
    private JDesktopPane desktop;
    
    /**
     * Constructor del controlador de la vista para escalar
     * @param controlVP Controlador de la vista principal que es el que se ha comunicado con él
     */
    ControladorVistaEscalar(ControladorVistaPrincipal controlVP) {
        this.controlVP = controlVP;
        vistaEM = new VistaEscalarManual(this);
        controlV = new ControladorVision(this);
        vistaEA = new VistaEscalarAutomatico(this);
    }

    /**
     * Cancela el escalado del objeto
     */
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

    /**
     * Activa el escalado manual del objeto
     * @param title nombre del fichero que se va a escalar
     * @param desktop Panel que contiene el frame donde se muestra el objeto
     */
    public void escalarManual(String title, JDesktopPane desktop) {
        vistaEM.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }

    /**
     * Activa el escalado automático del objeto
     * @param title nombre del fichero que se va a escalar
     * @param desktop Panel que contiene el frame donde se muestra el objeto
     */
    public void escalarAutomatico(String title, JDesktopPane desktop) {
        vistaEA.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }
    
    /**
     * Escala manualmente con las coordenadas (X, Y, Z)
     * @param x coordenada correspondiente al eje X
     * @param y coordenada correspondiente al eje Y
     * @param z coordenada correspondiente al eje z
     */
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

    /**
     * Obtiene el panel que contiene el frame del objeto
     * @return el panel
     */
    public JDesktopPane getDesktop() {
        return desktop;
    }

    /**
     * Escalar automáticamente un objeto
     * @param text un string que contiene el tanto por ciento que se escalará
     */
    public void escalarAutomatico(String text) {
        
    }
    
    
}
