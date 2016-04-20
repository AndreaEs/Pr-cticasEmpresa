package modelo;

import presentacion.ControladorVistaEscalar;
import presentacion.ControladorVistaPrincipal;
import presentacion.ControladorVistaRotar;

public class ControladorVision {
    
    private Vision vision;
    private ControladorVistaPrincipal controladorVista;
    private ControladorVistaRotar controladorRotar;
    private ControladorVistaEscalar controladorEscalar;
    
    public ControladorVision(ControladorVistaPrincipal controladorVista) {
        this.controladorVista = controladorVista;
        vision = new Vision();
    }
    
    public ControladorVision(ControladorVistaRotar controladorRotar) {
        this.controladorRotar = controladorRotar;
        vision = new Vision();
    }
    
    public ControladorVision(ControladorVistaEscalar controladorEscalar) {
        this.controladorEscalar = controladorEscalar;
        vision = new Vision();
    }
    
    public void rotarManual(String nombre, float x, float y, float z, ControladorVistaPrincipal controlVista){
        vision.rotarManual(nombre, x, y, z, controlVista);
    }

    public void escalarManual(String nombreFichero, float xValor, float yValor, float zValor, ControladorVistaPrincipal controlVP) {
        vision.escalarManual(nombreFichero, xValor, yValor, zValor, controlVP);
    }

    public void rotarAutomatico(String nombreFichero, float a, ControladorVistaPrincipal controlVP) {
        vision.rotarAutomatico(nombreFichero, a, controlVP);
    }
    
}
