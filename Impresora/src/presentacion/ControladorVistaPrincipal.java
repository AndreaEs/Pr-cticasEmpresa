/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import edu.ncsa.model.graphics.jogl.ModelViewer;
import javax.media.j3d.BranchGroup;
import modelo.ControladorFichero;

/**
 *
 * @author andreaescribano
 */
public class ControladorVistaPrincipal {

    private VistaPrincipal vista;
    private ControladorFichero controlFichero;
    
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

}
