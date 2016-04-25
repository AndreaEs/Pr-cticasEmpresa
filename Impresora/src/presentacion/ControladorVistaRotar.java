package presentacion;

import javax.swing.JDesktopPane;
import modelo.ControladorVision;

/**
 * Controla la vista de rotar un objeto
 * @author A3D Ingeniería
 */
public class ControladorVistaRotar {

    //Atributos
//    private VistaPrincipal vistaP;
    private VistaRotarManual vistaRM;
    private VistaRotarAutomatico vistaRA;
    private String nombreFichero;
    private ControladorVision controlV;
    private ControladorVistaPrincipal controlVP;
    private JDesktopPane desktop;

    /**
     * Crea un controlador para la vista de rotación
     * @param controlVP Controlador de la vista principal que es la que le llama
     */
    ControladorVistaRotar(ControladorVistaPrincipal controlVP) {
        this.controlVP = controlVP;
        vistaRM = new VistaRotarManual(this);
        controlV = new ControladorVision(this);
        vistaRA = new VistaRotarAutomatico(this);
    }

    /**
     * Activa empezar a rotar manualmente
     * @param nombre nombre del fichero que se va a rotar
     * @param desktop panel donde se encuentra el frame con el que se ve el objeto en 3D
     */
    public void rotarManual(String nombre, JDesktopPane desktop) {
        vistaRM.setVisible(true);
        this.nombreFichero = nombre;
        this.desktop = desktop;
    }

    /**
     * Activa empezar a rotar automáticamente
     * @param title nombre del fichero a rotar
     * @param desktop panel donde se muestra el frame con el que se ve el objeto en 3D
     */
    public void rotarAutomatico(String title, JDesktopPane desktop) {
        vistaRA.setVisible(true);
        this.nombreFichero = title;
        this.desktop = desktop;
    }

    /**
     * Maneja la opción de cancelar la rotación del objeto
     */
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

    /**
     * Controla rotación manual de las 3 coordenadas
     * @param x coordenada correspondiente al eje X
     * @param y coordenada correspondiente al eje Y
     * @param z coordenada correspondiente al eje Z
     */
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

    /**
     * Controla la rotación automática por medio de un ángulo
     * @param angulo cadena de caracteres que contiene el angulo en el que se rotará
     */
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

    /**
     * Devuelve el panel donde se encuentra el frame que muestra el objeto
     * @return JDesktopPane que tiene el frame con el objeto en 3D
     */
    public JDesktopPane getDesktop() {
        return desktop;
    }

}
