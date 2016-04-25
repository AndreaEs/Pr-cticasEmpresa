package modelo;

import edu.ncsa.model.graphics.jogl.ModelViewer;
import edu.ncsa.model.loaders.MeshLoader_STP;
import java.util.Vector;
import javax.media.j3d.BranchGroup;
import presentacion.ControladorVistaPrincipal;

/**
 * Controlador de los ficheros
 * @author A3D Ingeniería
 */
public class ControladorFichero {

    //Atributos
    private ControladorVistaPrincipal controladorVista;
    private FicheroOBJ fOBJ;
    private FicheroWRL fWRL;
    private FicheroSTL fSTL;
    private MeshLoader_STP mlSTP;

    /**
     * Constructor del Controlador de los ficheros
     * @param controladorVista Controlador de la vista principal que lo llama
     */
    public ControladorFichero(ControladorVistaPrincipal controladorVista) {
        this.controladorVista = controladorVista;
        fOBJ = new FicheroOBJ();
        fWRL = new FicheroWRL();
        fSTL = new FicheroSTL();
        mlSTP = new MeshLoader_STP();

    }

    /**
     * Abrir archivos con la opción 1
     * @param pathFile ruta al fichero que se abrirá
     * @return BanchGroup con el objeto "dibujado"
     */
    public BranchGroup abrirArchivosO1(String pathFile) {
        String ext = getExtension(pathFile);
        BranchGroup scene = null;
        if (ext.equals("obj")) {
            scene = fOBJ.createSceneGraph(pathFile);
        } else if (ext.equals("wrl")) {
            scene = fWRL.createSceneBranchGroup(pathFile);
        } else if (ext.equals("stl")) {
            scene = fSTL.load(pathFile);
        }
        return scene;
    }

    /**
     * Abrir archivos con la opción 1
     * @param pathFile ruta al fichero que se abrirá
     * @return ModelViewer con el objeto "dibujado"
     */
    public ModelViewer abrirArchivosO2(final String pathFile) {
//        String ext = getExtension(pathFile);
//        ModelViewer model = new ModelViewer();
//        if (ext.equals("igs")) {
//
//        } else {
            final Vector<ModelViewer> modelviewer = new Vector<ModelViewer>();
                Class Viewer = ModelViewer.class;
                try {
                    modelviewer.add((ModelViewer) Viewer.getDeclaredConstructor(String.class, int.class, int.class, boolean.class, boolean.class).newInstance("ModelViewer.ini", 600, 600, true, true));
                } catch (Exception e) {
                    System.err.print("Error: "+e);
                }
                new Thread(new Runnable() {
                    public void run() {
                        modelviewer.get(0).load(pathFile, null);
                    }
                }).start();
                return modelviewer.get(0);
//            Mesh m = mlSTP.load(pathFile);
//            m.initialize();
//            model.load(pathFile);
//        } else if (ext.equals("ply")) {
//            //Mesh m = mlPLY.load(pathFile);
//            model.load(pathFile);
//        }
//        return model;

    }

    //Obtiene la extensión de un String
    private String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Abrir archivos
     * @param fileSelec Fichero que se va a abrir
     */
    public void abrirArchivos(String fileSelec) {
        if (getExtension(fileSelec).equals("obj") || getExtension(fileSelec).equals("wrl") || getExtension(fileSelec).equals("stl")) {
            controladorVista.showFile1(abrirArchivosO1(fileSelec), fileSelec);
        } else {
            controladorVista.showFile2(abrirArchivosO2(fileSelec), fileSelec);
        }
    }
}
