/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author andreaescribano
 */
class FileTypeFilter extends FileFilter {
    private String extension;
    private String description;

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
