package presentacion;

import edu.ncsa.model.graphics.jogl.ModelViewer;
import java.io.File;
import javax.media.j3d.BranchGroup;
import modelo.ControladorFichero;

/**
 * Controlador de la vista Principal
 */
public class ControladorVistaPrincipal {

    //Atributos
    private VistaPrincipal vista;
    private ControladorFichero controlFichero;

    /**
     * Constructor del controlador
     *
     * @param vista Vista correspondiente para crear el controlador
     */
    public ControladorVistaPrincipal(VistaPrincipal vista) {
        this.vista = vista;
        controlFichero = new ControladorFichero(this);
    }

    public BranchGroup abrirArchivosO1(String pathFile) {
        return controlFichero.abrirArchivosO1(pathFile);
    }

    public ModelViewer abrirArchivosO2(String pathFile) {
        return controlFichero.abrirArchivosO2(pathFile);
    }

    public void abrirArchivo(File fileSelec) {
        if (getExtension(fileSelec).equals("obj") || getExtension(fileSelec).equals("wrl") || getExtension(fileSelec).equals("stl")) {
            vista.showFile1(controlFichero.abrirArchivosO1(fileSelec.getAbsolutePath()));
        } else {
            vista.showFile2(controlFichero.abrirArchivosO2(fileSelec.getAbsolutePath()));
        }
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
}
