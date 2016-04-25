package modelo;

/**
 * Extensiones permitidas de los ficheros
 * @author A3D Ingeniería
 */
public class FileExtensions {
    
    //Atributos
    private final String[] extensions = {"igs", "stp", "ply", "wrl", "stl", "obj"};
    private final String[] descriptions = {"IGS", "STP", "PLY", "WRL", "STL", "OBJ"};
    //private final String[] extensions = {"png"};
    //private final String[] descriptions = {"PNG"};

    /**
     * Obtener la descripción de .igs
     * @return una cadena con la descripción de .igs
     */
    public String getIgsDescription() {
        return descriptions[0];
    }

    /**
     * Obtener la descripción de .stp
     * @return una cadena con la descripción de .stp
     */
    public String getStpDescription() {
        return descriptions[1];
    }

    /**
     * Obtener la descripción de .ply
     * @return una cadena con la descripción de .ply
     */
    public String getPlyDescription() {
        return descriptions[2];
    }

    /**
     * Obtener la descripción de .wrl
     * @return una cadena con la descripción de .wrl
     */
    public String getWrlDescription() {
        return descriptions[3];
    }

    /**
     * Obtener la descripción de .stl
     * @return una cadena con la descripción de .stl
     */
    public String getStlDescription() {
        return descriptions[4];
    }

    /**
     * Obtener la descripción de .obj
     * @return una cadena con la descripción de .obj
     */
    public String getObjDescription() {
        return descriptions[5];
    }

    /**
     * Obtener la extensión de .igs
     * @return una cadena con la extensión de .igs
     */
    public String getIgsExtension() {
        return extensions[0];
    }

    /**
     * Obtener la extensión de .stp
     * @return una cadena con la extensión de .stp
     */
    public String getStpExtension() {
        return extensions[1];
    }

    /**
     * Obtener la extensión de .ply
     * @return una cadena con la extensión de .ply
     */
    public String getPlyExtension() {
        return extensions[2];
    }

    /**
     * Obtener la extensión de .wrl
     * @return una cadena con la extensión de .wrl
     */
    public String getWrlExtension() {
        return extensions[3];
    }

    /**
     * Obtener la extensión de .stl
     * @return una cadena con la extensión de .stl
     */
    public String getStlExtension() {
        return extensions[4];
    }

    /**
     * Obtener la extensión de .obj
     * @return una cadena con la extensión de .obj
     */
    public String getObjExtension() {
        return extensions[5];
    }

    /**
     * Obtiene todas las extensiones
     * @return un array de cadena con todas las extensiones
     */
    public String[] getExtensions() {
        return extensions;
    }

    /**
     * Obtener las descripciones de las extensiones
     * @return un array de String con todas las descripciones de las extensiones
     */
    public String[] getDescriptions() {
        return descriptions;
    }
    
}
