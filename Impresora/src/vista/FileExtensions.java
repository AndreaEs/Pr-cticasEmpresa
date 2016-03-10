/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

/**
 *
 * @author andreaescribano
 */
public class FileExtensions {
    
    private final String[] extensions = {"igs", "stp", "ply", "wrl", "stl", "obj"};
    private final String[] descriptions = {"IGS", "STP", "PLY", "WRL", "STL", "OBJ"};
    //private final String[] extensions = {"png"};
    //private final String[] descriptions = {"PNG"};

    public String getIgsDescription() {
        return descriptions[0];
    }

    public String getStpDescription() {
        return descriptions[1];
    }

    public String getPlyDescription() {
        return descriptions[2];
    }

    public String getWrlDescription() {
        return descriptions[3];
    }

    public String getStlDescription() {
        return descriptions[4];
    }

    public String getObjDescription() {
        return descriptions[5];
    }

    public String getIgsExtension() {
        return extensions[0];
    }

    public String getStpExtension() {
        return extensions[1];
    }

    public String getPlyExtension() {
        return extensions[2];
    }

    public String getWrlExtension() {
        return extensions[3];
    }

    public String getStlExtension() {
        return extensions[4];
    }

    public String getObjExtension() {
        return extensions[5];
    }

    public String[] getExtensions() {
        return extensions;
    }

    public String[] getDescriptions() {
        return descriptions;
    }
    
}
