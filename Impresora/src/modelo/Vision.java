package modelo;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import presentacion.ControladorVistaPrincipal;

public class Vision {

    private final Map<String, Float> valoresRotar;
    private final Map<String, Float> valoresEscalar;

//    private final Bounds behaviorBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public Vision() {
        this.valoresRotar = new LinkedHashMap<String, Float>();
        this.valoresEscalar = new LinkedHashMap<String, Float>();
    }

    public BranchGroup createMouseBehaviorsGroup(Scene s) {

        BranchGroup objRoot = new BranchGroup();
        try {
            TransformGroup objTrans = new TransformGroup();
            objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            Transform3D myTransform3D;
            if (valoresRotar.isEmpty() || valoresEscalar.isEmpty()) {
                myTransform3D = new Transform3D();
                myTransform3D.setTranslation(new Vector3f(0.0f, -0.15f, -5f));
            } else {
                myTransform3D = new Transform3D();
                //Inicio Rotar
                int contadorRotar = 0;
                for (Entry<String, Float> valor : valoresRotar.entrySet()) {
                    if (valor.getKey().equals("T1") || valor.getKey().equals("T2") || valor.getKey().equals("T3")) {
                        contadorRotar++;
                    }else if(valor.getKey().equals("T4")){
                        contadorRotar++;
                    }
                }
                if (contadorRotar == 1) {
                    for (Entry<String, Float> valor : valoresRotar.entrySet()) {
                        if(valor.getKey().equals("T4")){
                            
                        }
                    }
                } else {
                    Vector3f vector = new Vector3f();
                    for (Entry<String, Float> valor : valoresRotar.entrySet()) {
                        String key = valor.getKey();
                        if (key.equals("T1")) {
                            vector.x = valoresRotar.get(key);
                        }else if(key.equals("T2")){
                            vector.y = valoresRotar.get(key);
                        }else if(key.equals("T3")){
                            vector.z = valoresRotar.get(key);
                        }
                    }
                }

                //Fin Rotar
//                myTransform3D.setTranslation(new Vector3f(valoresRotar[0], valoresRotar[1], valoresRotar[2])); //Mirarlo con apuntes de mates 
                
                
                //Inicio Escalar
                int contadorEscalar = 0;
                for (Entry<String, Float> valor : valoresEscalar.entrySet()) {
                    if (valor.getKey().equals("T1") || valor.getKey().equals("T2") || valor.getKey().equals("T3")) {
                        contadorEscalar++;
                    }else if(valor.getKey().equals("T4")){
                        contadorEscalar++;
                    }
                }
                if (contadorEscalar == 1) {
                    for (Entry<String, Float> valor : valoresEscalar.entrySet()) {
                        String key = valor.getKey();
                        if(key.equals("T4")){
                            myTransform3D.setScale(valoresEscalar.get(key));
                        }
                    }
                } else {
                    Vector3d vector = new Vector3d();
                    for (Entry<String, Float> valor : valoresEscalar.entrySet()) {
                        String key = valor.getKey();
                        if (key.equals("T1")) {
                            vector.x = valoresEscalar.get(key);
                        }else if(key.equals("T2")){
                            vector.y = valoresEscalar.get(key);
                        }else if(key.equals("T3")){
                            vector.z = valoresEscalar.get(key);
                        }
                    }
                    myTransform3D.setScale(vector);
                }
                //Fin Escalar
            }

            objTrans.setTransform(myTransform3D);

            Hashtable table = s.getNamedObjects();

            for (Enumeration e = table.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                System.out.println(key);

                Object obj = table.get(key);
                System.out.println(obj.getClass().getName());

                Shape3D shape = (Shape3D) obj;

                Appearance ap = new Appearance();

                Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

                Color3f grey = new Color3f(Color.LIGHT_GRAY);

                ap.setMaterial(new Material(grey, black, grey, black, 1.0f));

                float transparencyValue = 0.5f;

                TransparencyAttributes t_attr = new TransparencyAttributes(TransparencyAttributes.BLENDED, transparencyValue, TransparencyAttributes.BLEND_SRC_ALPHA, TransparencyAttributes.BLEND_ONE);
                ap.setTransparencyAttributes(t_attr);
                ap.setRenderingAttributes(new RenderingAttributes());
                shape.setAppearance(ap);
                objTrans.addChild(new Shape3D(shape.getGeometry(), ap));
            }
            System.out.println("Finished Loading");

            BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

            Color3f light1Color = new Color3f(.9f, 0.9f, 0.9f);

            Vector3f light1Direction = new Vector3f(-0.1f, -0f, -1f);

            DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
            light1.setInfluencingBounds(bounds);
            objTrans.addChild(light1);

            Color3f ambientColor = new Color3f(Color.DARK_GRAY);

            AmbientLight ambientLightNode = new AmbientLight(ambientColor);
            ambientLightNode.setInfluencingBounds(bounds);
            objTrans.addChild(ambientLightNode);

            objRoot.addChild(objTrans);

        } catch (IncorrectFormatException t) {
            System.err.println("Error: " + t);
        } catch (ParsingErrorException t) {
            System.err.println("Error: " + t);
        }
        return objRoot;
    }

    public TransformGroup createMouseBehaviorsGroup() {
        TransformGroup examineGroup = new TransformGroup();
        examineGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        examineGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Bounds behaviorBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        MouseRotate mr = new MouseRotate(examineGroup);
        mr.setSchedulingBounds(behaviorBounds);
        examineGroup.addChild(mr);

        MouseTranslate mt = new MouseTranslate(examineGroup);
        mt.setSchedulingBounds(behaviorBounds);
        examineGroup.addChild(mt);

        MouseZoom mz = new MouseZoom(examineGroup);
        mz.setSchedulingBounds(behaviorBounds);
        examineGroup.addChild(mz);

        return examineGroup;
    }

//    public Node rotarManual(TransformGroup examineGroup) {
//        MouseRotate mr = new MouseRotate(examineGroup);
//        mr.setSchedulingBounds(behaviorBounds);
//        return mr;
//    }
//    public Node trasladar(TransformGroup examineGroup) {
//        MouseTranslate mt = new MouseTranslate(examineGroup);
//        mt.setSchedulingBounds(behaviorBounds);
//        return mt;
//    }
//    public Node zoom(TransformGroup examineGroup) {
//        MouseZoom mz = new MouseZoom(examineGroup);
//        mz.setSchedulingBounds(behaviorBounds);
//        return mz;
//    }
    public void rotarManual(String nombre, float x, float y, float z, ControladorVistaPrincipal controlVista) {
        valoresRotar.put("T1", x);
        valoresRotar.put("T2", y);
        valoresRotar.put("T3", z);
        ControladorFichero control = new ControladorFichero(controlVista);
        control.abrirArchivos(nombre);
    }

    public void escalarManual(String nombreFichero, float x, float y, float z, ControladorVistaPrincipal controlVP) {
        valoresEscalar.put("T1", x);
        valoresEscalar.put("T2", y);
        valoresEscalar.put("T3", z);
        ControladorFichero control = new ControladorFichero(controlVP);
        control.abrirArchivos(nombreFichero);
    }

    public void rotarAutomatico(String nombreFichero, float a, ControladorVistaPrincipal controlVP) {
        valoresRotar.put("T4", a);
        ControladorFichero control = new ControladorFichero(controlVP);
        control.abrirArchivos(nombreFichero);
    }
}
