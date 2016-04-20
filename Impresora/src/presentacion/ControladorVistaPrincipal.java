package presentacion;

import edu.ncsa.model.graphics.jogl.ModelViewer;
import java.io.File;
import javax.media.j3d.BranchGroup;
import javax.swing.JDesktopPane;
import modelo.ControladorFichero;
import modelo.ControladorVision;

/**
 * Controlador de la vista Principal
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
     *
     * @param vista Vista correspondiente para crear el controlador
     */
    public ControladorVistaPrincipal(VistaPrincipal vista) {
        this.vista = vista;
        controlFichero = new ControladorFichero(this);
        controlMouse = new ControladorVision(this);
        controlRotar = new ControladorVistaRotar(this);
        controlEscalar = new ControladorVistaEscalar(this);
    }

    public BranchGroup abrirArchivosO1(String pathFile) {
        return controlFichero.abrirArchivosO1(pathFile);
    }

    public ModelViewer abrirArchivosO2(String pathFile) {
        return controlFichero.abrirArchivosO2(pathFile);
    }

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

    public void rotarManual(String nombre, JDesktopPane desktop) {
        controlRotar.rotarManual(nombre, desktop);
    }

    public void showFile1(BranchGroup abrirArchivosO1, String name) {
        vista.showFile1(abrirArchivosO1, name);
    }

    public void showFile2(ModelViewer abrirArchivosO2, String name) {
        vista.showFile2(abrirArchivosO2, name);
    }

    public void rotarAutomatico(String title, JDesktopPane desktop) {
        controlRotar.rotarAutomatico(title, desktop);
    }

    public void cancelarRotacion() {
        vista.cancelarRotacion();
    }

    public void cancelarEscalado() {
        vista.cancelarEscalado();
    }

    public void escalarManual(String title, JDesktopPane desktop) {
        controlEscalar.escalarManual(title, desktop);
    }

    public void escalarAutomatico(String title, JDesktopPane desktop) {
        controlEscalar.escalarAutomatico(title, desktop);
    }
}
