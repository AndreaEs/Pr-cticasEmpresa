package presentacion;

import edu.ncsa.model.graphics.jogl.ModelViewer;
import java.io.File;
import javax.media.j3d.BranchGroup;
import javax.swing.JDesktopPane;
import modelo.ControladorFichero;
import modelo.ControladorVision;

/**
 * Controlador de la vista Principal
 * @author A3D Ingeniería
 */
public class ControladorVistaPrincipal {

    //Atributos
    private VistaPrincipal vista;
    private ControladorFichero controlFichero;
    private ControladorVision controlMouse;
    private ControladorVistaRotar controlRotar;
    private ControladorVistaEscalar controlEscalar;

    /**
     * Constructor del controlador
     * @param vista Vista correspondiente para crear el controlador
     */
    public ControladorVistaPrincipal(VistaPrincipal vista) {
        this.vista = vista;
        controlFichero = new ControladorFichero(this);
        controlMouse = new ControladorVision(this);
        controlRotar = new ControladorVistaRotar(this);
        controlEscalar = new ControladorVistaEscalar(this);
    }

    /**
     * Primera opción para abrir archivos de objetos 3D
     * @param pathFile la ruta al fichero que se desea abrir
     * @return BanchGroup que contiene el objeto
     */
    public BranchGroup abrirArchivosO1(String pathFile) {
        return controlFichero.abrirArchivosO1(pathFile);
    }

    /**
     * Segunda opción para abrir archivos de objetos 3D
     * @param pathFile la ruta al fichero que se desea abrir
     * @return ModelViewer que contiene el objeto
     */
    public ModelViewer abrirArchivosO2(String pathFile) {
        return controlFichero.abrirArchivosO2(pathFile);
    }

    /**
     * Apertura de ficheros
     * @param fileSelec fichero que se desea abrir
     */
    public void abrirArchivos(File fileSelec) {
        controlFichero.abrirArchivos(fileSelec.getAbsolutePath());
    }
    
    //Obtiene la extensión de un fichero (File)
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Rotación manual del objeto
     * @param nombre nombre del fichero a rotar
     * @param desktop panel donde esta el frame en el que se encuentra el fichero abierto
     */
    public void rotarManual(String nombre, JDesktopPane desktop) {
        controlRotar.rotarManual(nombre, desktop);
    }

    /**
     * Primera forma de mostrar los ficheros
     * @param abrirArchivosO1 BanchGroup que contiene el objeto
     * @param name nombre del archivo que se abre
     */
    public void showFile1(BranchGroup abrirArchivosO1, String name) {
        vista.showFile1(abrirArchivosO1, name);
    }

    /**
     * Segunda forma de mostrar los ficheros
     * @param abrirArchivosO2 ModelViewer que contiene el objeto
     * @param name nombre del archivo que se abre
     */
    public void showFile2(ModelViewer abrirArchivosO2, String name) {
        vista.showFile2(abrirArchivosO2, name);
    }

    /**
     * Rotación automática de un objeto
     * @param title nombre del fichero que se rotará
     * @param desktop panel que contiene el frame donde aparece el objeto que se rotará
     */
    public void rotarAutomatico(String title, JDesktopPane desktop) {
        controlRotar.rotarAutomatico(title, desktop);
    }

    /**
     * Cancelar la rotación
     */
    public void cancelarRotacion() {
        vista.cancelarRotacion();
    }

    /**
     * Cancelar el escalado
     */
    public void cancelarEscalado() {
        vista.cancelarEscalado();
    }

    /**
     * Escalado manual de un objeto
     * @param title nombre del fichero que contiene el objeto
     * @param desktop panel que contiene el frame para mostrar el objeto 3D
     */
    public void escalarManual(String title, JDesktopPane desktop) {
        controlEscalar.escalarManual(title, desktop);
    }

    /**
     * Escalado automático de un objeto
     * @param title nombre del fichero que contiene el objeto
     * @param desktop panel que contiene el frame para mostrar el objeto 3D
     */
    public void escalarAutomatico(String title, JDesktopPane desktop) {
        controlEscalar.escalarAutomatico(title, desktop);
    }
}
