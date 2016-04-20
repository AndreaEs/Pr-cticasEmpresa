package modelo;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.utils.picking.PickTool;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Morph;
import javax.media.j3d.Node;
import javax.media.j3d.SceneGraphObject;
import javax.media.j3d.Shape3D;

public class FicheroWRL {

    public FicheroWRL() {
    }

    public BranchGroup createSceneBranchGroup(String fichero) {
//        BranchGroup objRoot = new BranchGroup();
//
//        Bounds lightBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
//                100.0);
//
//        AmbientLight ambLight = new AmbientLight(true, new Color3f(1.0f, 1.0f,
//                1.0f));
//        ambLight.setInfluencingBounds(lightBounds);
//        objRoot.addChild(ambLight);
//
//        DirectionalLight headLight = new DirectionalLight();
//        headLight.setInfluencingBounds(lightBounds);
//        objRoot.addChild(headLight);
        
        Vision mG = new Vision();
        
        String vrmlFile = null;

        try {
            vrmlFile = fichero;
        } catch (Exception e) {
            e.printStackTrace();
        }

//        BranchGroup sceneRoot = loadVrmlFile(vrmlFile);


        return mG.createMouseBehaviorsGroup(loadVrmlFile(vrmlFile));
    }

    private Scene loadVrmlFile(String location) {
//        BranchGroup sceneGroup = null;
        Scene scene = null;

        VrmlLoader loader = new VrmlLoader();

        try {
            URL loadUrl = new URL(location);
            try {
                // load the scene
                scene = loader.load(new URL(location));
            } catch (Exception e) {
                System.out.println("Exception loading URL:" + e);
                e.printStackTrace();
            }
        } catch (MalformedURLException badUrl) {
            // location may be a path name
            try {
                // load the scene
                scene = loader.load(location);
            } catch (Exception e) {
                System.out.println("Exception loading file from path:" + e);
                e.printStackTrace();
            }
        }

//        if (scene != null) {
//            // get the scene group
//            sceneGroup = scene.getSceneGroup();
//
//            sceneGroup.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
//            sceneGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
//
//            Hashtable namedObjects = scene.getNamedObjects();
//            System.out.println("*** Named Objects in VRML file: \n"
//                    + namedObjects);
//
//      // recursively set the user data here
//            // so we can find our objects when they are picked
//            java.util.Enumeration enumValues = namedObjects.elements();
//            java.util.Enumeration enumKeys = namedObjects.keys();
//
//            if (enumValues != null) {
//                while (enumValues.hasMoreElements() != false) {
//                    Object value = enumValues.nextElement();
//                    Object key = enumKeys.nextElement();
//
//                    recursiveSetUserData(value, key);
//                }
//            }
//        }

        return scene;
    }

    private void recursiveSetUserData(Object value, Object key) {
        if (value instanceof SceneGraphObject != false) {
            // set the user data for the item
            SceneGraphObject sg = (SceneGraphObject) value;
            sg.setUserData(key);

            // recursively process group
            if (sg instanceof Group) {
                Group g = (Group) sg;

                // recurse on child nodes
                java.util.Enumeration enumKids = g.getAllChildren();

                while (enumKids.hasMoreElements() != false) {
                    recursiveSetUserData(enumKids.nextElement(), key);
                }
            } else if (sg instanceof Shape3D || sg instanceof Morph) {
                PickTool.setCapabilities((Node) sg, PickTool.INTERSECT_FULL);
            }
        }
    }
}
