/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import edu.ncsa.model.Mesh;
import edu.ncsa.model.graphics.jogl.ModelViewer;
import edu.ncsa.model.loaders.MeshLoader_PLY;
import edu.ncsa.model.loaders.MeshLoader_STL;
import edu.ncsa.model.loaders.MeshLoader_STP;
import edu.ncsa.model.loaders.MeshLoader_WRL;
import java.util.Vector;
import javax.media.j3d.BranchGroup;
import presentacion.ControladorVistaPrincipal;

/**
 *
 * @author andreaescribano
 */
public class ControladorFichero {

    private ControladorVistaPrincipal controladorVista;
    private FicheroOBJ fOBJ;
    private FicheroWRL fWRL;
    private FicheroSTL fSTL;
    private MeshLoader_STP mlSTP;

    public ControladorFichero(ControladorVistaPrincipal controladorVista) {
        this.controladorVista = controladorVista;
        fOBJ = new FicheroOBJ();
        fWRL = new FicheroWRL();
        fSTL = new FicheroSTL();
        mlSTP = new MeshLoader_STP();

    }

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

    public ModelViewer abrirArchivosO2(final String pathFile) {
        String ext = getExtension(pathFile);
        ModelViewer model = new ModelViewer();
//        if (ext.equals("igs")) {
//
//        } else {
            final Vector<ModelViewer> modelviewer = new Vector<ModelViewer>();
                Class Viewer = ModelViewer.class;
                try {
                    modelviewer.add((ModelViewer) Viewer.getDeclaredConstructor(String.class, int.class, int.class, boolean.class, boolean.class).newInstance("ModelViewer.ini", 600, 600, true, true));
                } catch (Exception e) {
                    e.printStackTrace();
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

    private String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
