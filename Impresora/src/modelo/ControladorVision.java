package modelo;

import presentacion.ControladorVistaEscalar;
import presentacion.ControladorVistaPrincipal;
import presentacion.ControladorVistaRotar;

/**
 * Controlador de la vision de los objetos
 * @author A3D Ingeniería
 */
public class ControladorVision {
    
    //Atributos
    private Vision vision;
    private ControladorVistaPrincipal controladorVista;
    private ControladorVistaRotar controladorRotar;
    private ControladorVistaEscalar controladorEscalar;
    
    /**
     * Constructor del controlador a partir de la vista principal
     * @param controladorVista Controlador de la Vista principal
     */
    public ControladorVision(ControladorVistaPrincipal controladorVista) {
        this.controladorVista = controladorVista;
        vision = new Vision();
    }
    
    /**
     * Constructor del controlador a partir de la vista rotar
     * @param controladorRotar Controlador de la Vista rotar
     */
    public ControladorVision(ControladorVistaRotar controladorRotar) {
        this.controladorRotar = controladorRotar;
        vision = new Vision();
    }
    
    /**
     * Constructor del controlador a partir de la vista escalar
     * @param controladorEscalar Controlador de la Vista escalar
     */
    public ControladorVision(ControladorVistaEscalar controladorEscalar) {
        this.controladorEscalar = controladorEscalar;
        vision = new Vision();
    }
    
    /**
     * Rotar manualmente el objeto a través de las coordenadas
     * @param nombre nombre del archivo que se rotará
     * @param x coordenada del eje X
     * @param y coordenada del eje Y
     * @param z coordenada del eje Z
     * @param controlVista controlador de la vista principal
     */
    public void rotarManual(String nombre, float x, float y, float z, ControladorVistaPrincipal controlVista){
        vision.rotarManual(nombre, x, y, z, controlVista);
    }

    /**
     * Escalar manualmente el objeto a través de las coordenadas
     * @param nombreFichero nombre del archivo que se escalará
     * @param xValor coordenada del eje X
     * @param yValor coordenada del eje Y
     * @param zValor coordenada del eje Z
     * @param controlVP controlador de la vista principal
     */
    public void escalarManual(String nombreFichero, float xValor, float yValor, float zValor, ControladorVistaPrincipal controlVP) {
        vision.escalarManual(nombreFichero, xValor, yValor, zValor, controlVP);
    }

    /**
     * Rotar automáticamente el objeto a través de un ángulo
     * @param nombreFichero nombre del archivo que se rotará
     * @param a ángulo para rotar
     * @param controlVP controlador de la vista principal
     */
    public void rotarAutomatico(String nombreFichero, float a, ControladorVistaPrincipal controlVP) {
        vision.rotarAutomatico(nombreFichero, a, controlVP);
    }
    
}
