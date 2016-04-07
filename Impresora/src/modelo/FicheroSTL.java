package modelo;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Vector3d;

import org.j3d.loaders.stl.STLFileReader;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

public class FicheroSTL {

    private final Component itsParentComponent;
    private boolean itsShowProgress = false;

    public FicheroSTL() {
        itsParentComponent = null;
    }

    public FicheroSTL(final Component parentComponent) {
        itsParentComponent = parentComponent;
        itsShowProgress = true;
    }

    public BranchGroup load(String fileName) {
        BranchGroup objRoot = new BranchGroup();

        Bounds lightBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);

        AmbientLight ambLight = new AmbientLight(true, new Color3f(1.0f, 1.0f,
                1.0f));
        ambLight.setInfluencingBounds(lightBounds);
        objRoot.addChild(ambLight);

        DirectionalLight headLight = new DirectionalLight();
        headLight.setInfluencingBounds(lightBounds);
        objRoot.addChild(headLight);

        Scene scene = null;

        try {
            try {
                MouseGroup mG = new MouseGroup();
                TransformGroup mouseGroup = mG.createMouseBehaviorsGroup();
                scene = load(new File(fileName).toURI().toURL());
                BranchGroup sceneRoot = scene.getSceneGroup();
                if (sceneRoot != null) 
                    mouseGroup.addChild(sceneRoot);
                objRoot.addChild(mouseGroup);
                return objRoot;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FicheroSTL.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IncorrectFormatException ex) {
                Logger.getLogger(FicheroSTL.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (ParsingErrorException ex) {
                Logger.getLogger(FicheroSTL.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } catch (MalformedURLException e) {
            Logger.getLogger(FicheroSTL.class.getName()).log(Level.SEVERE, null, e);
            return null;

        }
    }

    /**
     * Loads a STL file from an URL. The data may be in ASCII or binary
     * format.<p>
     * The <code>getNamedObjects</code> method of the <code>Scene</code> object
     * will return <code>Shape3D</code> objects with no <code>Appearance</code>
     * set.
     *
     * @param url
     * @return <code>Scene object</code> of the content of <code>url</code> or
     * <code>null</code> if user cancelled loading (only possible if progress
     * monitoring is enabled).
     * @throws java.io.FileNotFoundException
     */
    public Scene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        STLFileReader reader = null;
        try {
            if (itsShowProgress) {
                reader = new STLFileReader(url, itsParentComponent);
            } else {
                reader = new STLFileReader(url);
            }
            return createScene(reader);
        } catch (InterruptedIOException ie) {
            // user cancelled loading
            return null;
        } catch (IOException e) {
            throw new IncorrectFormatException(e.toString());
        }
    }

    /**
     * Creates a <code>Scene</code> object with the contents of the STL file.
     * Closes the reader after finishing reading.
     *
     * @param reader <code>STLFileReader</code> object for reading the STL file.
     * @return
     */
    public static Scene createScene(final STLFileReader reader) throws IncorrectFormatException, ParsingErrorException {
        try {
            final SceneBase scene = new SceneBase();
            final BranchGroup bg = new BranchGroup();
            final int numOfObjects = reader.getNumOfObjects();
            final int[] numOfFacets = reader.getNumOfFacets();
            final String[] names = reader.getObjectNames();

            final double[] normal = new double[3];
            final float[] fNormal = new float[3];
            final double[][] vertices = new double[3][3];
            for (int i = 0; i < numOfObjects; i++) {
                final TriangleArray geometry = new TriangleArray(
                        3 * numOfFacets[i],
                        TriangleArray.NORMALS | TriangleArray.COORDINATES
                );
                int index = 0;
                for (int j = 0; j < numOfFacets[i]; j++) {
                    final boolean ok = reader.getNextFacet(normal, vertices);
                    if (ok) {
                        fNormal[0] = (float) normal[0];
                        fNormal[1] = (float) normal[1];
                        fNormal[2] = (float) normal[2];
                        if (fNormal[0] == 0
                                && fNormal[1] == 0
                                && fNormal[2] == 0) {
                            // Calculate normal
                            Vector3d v0 = new Vector3d(vertices[0]);
                            v0.negate();
                            Vector3d v1 = new Vector3d(vertices[1]);
                            v1.add(v0);
                            Vector3d v2 = new Vector3d(vertices[2]);
                            v2.add(v0);
                            Vector3d n = new Vector3d();
                            n.cross(v1, v2);
                            n.normalize();
                            fNormal[0] = (float) n.x;
                            fNormal[1] = (float) n.y;
                            fNormal[2] = (float) n.z;
                        }
                        for (int k = 0; k < 3; k++) {
                            geometry.setNormal(index, fNormal);
                            geometry.setCoordinate(index, vertices[k]);
                            index++;
                        }
                    } else {
                        throw new ParsingErrorException();
                    }
                }
                final Shape3D shape = new Shape3D(geometry);
                bg.addChild(shape);
                String name = names[i];
                if (name == null) {
                    name = "Unknown_" + i;
                }
                scene.addNamedObject(name, shape);
            }
            scene.setSceneGroup(bg);
            return scene;
        } catch (InterruptedIOException ie) {
            // user cancelled loading
            return null;
        } catch (IOException e) {
            throw new ParsingErrorException(e.toString());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.print("Error: " + e);
            }
        }
    }
}
