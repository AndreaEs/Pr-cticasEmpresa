// This file is part of Origami Editor 3D.
// Copyright (C) 2013, 2014, 2015 Bágyoni Attila <ba-sz-at@users.sourceforge.net>
// Origami Editor 3D is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http:// www.gnu.org/licenses/>.
package origamieditor3d.origami;

import java.util.ArrayList;

public class OrigamiTracker {

    private final Origami trackedOrigami;
    
    @SuppressWarnings("unchecked")
    public OrigamiTracker(Origami origami, double[] tracker) throws Exception {

    	if (origami instanceof OrigamiGen2) {
    		trackedOrigami = new OrigamiGen2(origami.corners(), origami.history){

    		    @Override
    		    protected boolean cutPolygon(double[] ppoint, double[] pnormal, int polygonIndex) {

    		        if (isNonDegenerate(polygonIndex)) {

    		            boolean tracker = false;
    		            if ((int) polygons.get(polygonIndex).get(polygons.get(polygonIndex).size() - 1) == trackerpont) {

    		                ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                poligono.remove(poligono.size() - 1);
    		                polygons.set(polygonIndex, poligono);
    		                tracker = true;
    		            }
    		            ArrayList<Integer> s0 = polygons.get(polygonIndex);

    		            boolean ajustado = super.cutPolygon(ppoint, pnormal, polygonIndex);

    		            if (ajustado) {

    		                if (tracker) {

    		                    if (point_on_plane(ppoint, pnormal, vertices.get(trackerpont))) {

    		                        ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                        poligono.add(trackerpont);
    		                        polygons.set(polygonIndex, poligono);
    		                    } else {
    		                        if (scalar_product(vertices.get(trackerpont), pnormal) > scalar_product(ppoint, pnormal)) {

    		                            ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                            poligono.add(trackerpont);
    		                            polygons.set(polygonIndex, poligono);

    		                        } else {

    		                            ArrayList<Integer> poligono = polygons.get(polygons_size - 1);
    		                            poligono.add(trackerpont);
    		                            polygons.set(polygons_size - 1, poligono);
    		                        }
    		                    }

    		                    s0.add(trackerpont);
    		                    last_cut_polygons.set(last_cut_polygons.size() - 1, s0);
    		                }
    		            } else {
    		                if (tracker) {

    		                    ArrayList<Integer> poligono0 = polygons.get(polygonIndex);
    		                    poligono0.add(trackerpont);
    		                    polygons.set(polygonIndex, poligono0);
    		                }
    		            }
    		            return ajustado;
    		        }
    		        return false;
    		    }
    		};
    	} else {
    		trackedOrigami = new Origami(origami.corners(), origami.history){

    		    @Override
    		    protected boolean cutPolygon(double[] ppoint, double[] pnormal, int polygonIndex) {

    		        if (isNonDegenerate(polygonIndex)) {

    		            boolean tracker = false;
    		            if ((int) polygons.get(polygonIndex).get(polygons.get(polygonIndex).size() - 1) == trackerpont) {

    		                ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                poligono.remove(poligono.size() - 1);
    		                polygons.set(polygonIndex, poligono);
    		                tracker = true;
    		            }
    		            ArrayList<Integer> s0 = polygons.get(polygonIndex);

    		            boolean ajustado = super.cutPolygon(ppoint, pnormal, polygonIndex);

    		            if (ajustado) {

    		                if (tracker) {

    		                    if (point_on_plane(ppoint, pnormal, vertices.get(trackerpont))) {

    		                        ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                        poligono.add(trackerpont);
    		                        polygons.set(polygonIndex, poligono);
    		                    } else {
    		                        if (scalar_product(vertices.get(trackerpont), pnormal) > scalar_product(ppoint, pnormal)) {

    		                            ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                            poligono.add(trackerpont);
    		                            polygons.set(polygonIndex, poligono);

    		                        } else {

    		                            ArrayList<Integer> poligono = polygons.get(polygons_size - 1);
    		                            poligono.add(trackerpont);
    		                            polygons.set(polygons_size - 1, poligono);
    		                        }
    		                    }

    		                    s0.add(trackerpont);
    		                    last_cut_polygons.set(last_cut_polygons.size() - 1, s0);
    		                }
    		            } else {
    		                if (tracker) {

    		                    ArrayList<Integer> poligono = polygons.get(polygonIndex);
    		                    poligono.add(trackerpont);
    		                    polygons.set(polygonIndex, poligono);
    		                }
    		            }
    		            return ajustado;
    		        }
    		        return false;
    		    }
    		};
    	}
    	
        trackerpont = trackedOrigami.vertices_size;
        trackedOrigami.history_pointer = origami.history_pointer;
        trackedOrigami.addVertex(new double[]{tracker[0], tracker[1], 0});
        trackedOrigami.add2dVertex(new double[]{tracker[0], tracker[1], 0});
        ArrayList<Integer> poligono = (ArrayList<Integer>) trackedOrigami.polygons.get(0).clone();
        poligono.add(trackerpont);
        trackedOrigami.polygons.set(0, poligono);
    }
    private int trackerpont;

    public int trackPolygon() {

    	trackedOrigami.execute();
        for (int i = 0; i < trackedOrigami.polygons_size; i++) {
            if ((int) trackedOrigami.polygons.get(i).get(trackedOrigami.polygons.get(i).size() - 1) == trackerpont) {
                return i;
            }
        }
        return -1;
    }

    public double[] trackPoint() {

    	trackedOrigami.execute();
        return trackedOrigami.vertices.get(trackerpont);
    }
}
