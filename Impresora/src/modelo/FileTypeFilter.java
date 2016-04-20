package modelo;

import modelo.FileExtensions;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Tipos de filtros para los ficheros que queremos buscar
 */
public class FileTypeFilter extends FileFilter {
    
    //Atributos
    private String extension;
    private String description;

    /**
     * Constructor de un Filtro de Fichero
     * @param extension extensión correspondiente a un fichero
     * @param description descripcion que tendrá ese fichero a partir de la extensión
     */
    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        if (extension != null) {
            if( extension.equals(new FileExtensions().getIgsExtension())
                    || extension.equals(new FileExtensions().getStpExtension())
                    || extension.equals(new FileExtensions().getPlyExtension())
                    || extension.equals(new FileExtensions().getWrlExtension())
                    || extension.equals(new FileExtensions().getStlExtension())
                    || extension.equals(new FileExtensions().getObjExtension()))
            //if(extension.equals("png"))
                return f.getName().toLowerCase().endsWith("."+extension);
        }

        return false;
    }

    @Override
    public String getDescription() {
        return description + String.format(" (*.%s)", extension);
    }
    
    
}
