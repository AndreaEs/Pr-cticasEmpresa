/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;

/**
 *
 * @author andreaescribano
 */
public class MouseGroup {

    public MouseGroup() {
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
}
