package modelo;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import java.io.FileNotFoundException;
import javax.media.j3d.BranchGroup;

public class FicheroOBJ {

    protected String extension;
    protected float eyeOffset = 0.03F;

    public String getExtension() {
        return extension;
    }

    public FicheroOBJ(String extension) {
        this.extension = extension;
    }

    public FicheroOBJ() {
    }

    public BranchGroup createSceneGraph(String fichero) {
        System.out.println("Creating scene for: " + fichero);
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        Vision v = new Vision();
        Scene s = null;
        try {
            //URL url = new URL(fichero) ;
            
            ObjectFile f = new ObjectFile();
            f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            System.out.println("About to load");

            s = f.load(fichero);
            
        } catch (FileNotFoundException t) {
            System.err.println("Error: " + t);
        } catch (IncorrectFormatException t) {
            System.err.println("Error: " + t);
        } catch (ParsingErrorException t) {
            System.err.println("Error: " + t);
        }
        return v.createMouseBehaviorsGroup(s);
    }

    

}
