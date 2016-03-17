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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Camera {

    final static public int paper_back_color = 0xF7D6A6;
    final static public int paper_front_color = 0x0033CC;
    final static public int maximal_zoom = 4;

    public Camera(int x, int y, double zoom) {

        camera_pos = default_camera_pos.clone();
        camera_dir = default_camera_dir.clone();
        axis_x = default_axis_x.clone();
        axis_y = default_axis_y.clone();
        xshift = x;
        yshift = y;
        this.zoom = zoom;

        double[] xt = axis_x.clone();
        axis_x[0] = axis_x[0] / Origami.vector_length(xt) * zoom;
        axis_x[1] = axis_x[1] / Origami.vector_length(xt) * zoom;
        axis_x[2] = axis_x[2] / Origami.vector_length(xt) * zoom;

        double[] yt = axis_y.clone();
        axis_y[0] = axis_y[0] / Origami.vector_length(yt) * zoom;
        axis_y[1] = axis_y[1] / Origami.vector_length(yt) * zoom;
        axis_y[2] = axis_y[2] / Origami.vector_length(yt) * zoom;

        double[] zt = camera_dir.clone();
        camera_dir[0] = camera_dir[0] / Origami.vector_length(zt) * zoom;
        camera_dir[1] = camera_dir[1] / Origami.vector_length(zt) * zoom;
        camera_dir[2] = camera_dir[2] / Origami.vector_length(zt) * zoom;
    }
    public double[] camera_pos;
    public double[] camera_dir;
    public double[] axis_x;
    public double[] axis_y;
    static public double[] default_camera_pos = {200, 200, 0};
    static public double[] default_camera_dir = {0, 0, 1};
    static public double[] default_axis_x = {1, 0, 0};
    static public double[] default_axis_y = {0, 1, 0};
    public int xshift = 230;
    public int yshift = 230;
    private double zoom = 1.0;
    private double[][] space_buffer;
    public java.awt.image.BufferedImage texture;

    public double zoom() {

        return zoom;
    }

    public void setZoom(double value) {

        zoom = value;

        double[] xt = axis_x.clone();
        axis_x[0] = axis_x[0] / Origami.vector_length(xt) * zoom;
        axis_x[1] = axis_x[1] / Origami.vector_length(xt) * zoom;
        axis_x[2] = axis_x[2] / Origami.vector_length(xt) * zoom;

        double[] yt = axis_y.clone();
        axis_y[0] = axis_y[0] / Origami.vector_length(yt) * zoom;
        axis_y[1] = axis_y[1] / Origami.vector_length(yt) * zoom;
        axis_y[2] = axis_y[2] / Origami.vector_length(yt) * zoom;

        double[] zt = camera_dir.clone();
        camera_dir[0] = camera_dir[0] / Origami.vector_length(zt) * zoom;
        camera_dir[1] = camera_dir[1] / Origami.vector_length(zt) * zoom;
        camera_dir[2] = camera_dir[2] / Origami.vector_length(zt) * zoom;
    }
    protected byte orientation = 0;

    public double[] projection0(double[] point) {

        double constante = camera_pos[0] * camera_dir[0] + camera_pos[1] * camera_dir[1] + camera_pos[2] * camera_dir[2];

        double[] direccionvector = camera_dir;
        double X = point[0];
        double Y = point[1];
        double Z = point[2];
        double U = direccionvector[0];
        double V = direccionvector[1];
        double W = direccionvector[2];
        double A = camera_dir[0];
        double B = camera_dir[1];
        double C = camera_dir[2];
        double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);

        double[] puntoPie = {X + t * U, Y + t * V, Z + t * W};
        double[] espalda0 = {puntoPie[0] * axis_x[0] + puntoPie[1] * axis_x[1] + puntoPie[2] * axis_x[2],
            puntoPie[0] * axis_y[0] + puntoPie[1] * axis_y[1] + puntoPie[2] * axis_y[2]};
        return espalda0;
    }

    public double[] projection(double[] point) {
        double[] espalda0 = {projection0(point)[0] - projection0(camera_pos)[0], projection0(point)[1] - projection0(camera_pos)[1]};
        return espalda0;
    }

    public void rotate(float x, float y) {

        double sinX = Math.sin(x * Math.PI / 180);
        double cosX = Math.cos(x * Math.PI / 180);

        double Cx = axis_y[0];
        double Cy = axis_y[1];
        double Cz = axis_y[2];

        double X = camera_dir[0];
        double Y = camera_dir[1];
        double Z = camera_dir[2];

        double sinphi = sinX;
        double cosphi = cosX;

        double kepX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
        double kepY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
        double kepZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

        camera_dir[0] = kepX;
        camera_dir[1] = kepY;
        camera_dir[2] = kepZ;

        X = axis_x[0];
        Y = axis_x[1];
        Z = axis_x[2];

        kepX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
        kepY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
        kepZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

        axis_x[0] = kepX / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;
        axis_x[1] = kepY / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;
        axis_x[2] = kepZ / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;

        double sinY = Math.sin(y * Math.PI / 180);
        double cosY = Math.cos(y * Math.PI / 180);

        Cx = axis_x[0];
        Cy = axis_x[1];
        Cz = axis_x[2];

        X = camera_dir[0];
        Y = camera_dir[1];
        Z = camera_dir[2];

        sinphi = sinY;
        cosphi = cosY;

        kepX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
        kepY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
        kepZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

        camera_dir[0] = kepX / Origami.vector_length(new double[]{kepX, kepY, kepZ});
        camera_dir[1] = kepY / Origami.vector_length(new double[]{kepX, kepY, kepZ});
        camera_dir[2] = kepZ / Origami.vector_length(new double[]{kepX, kepY, kepZ});

        X = axis_y[0];
        Y = axis_y[1];
        Z = axis_y[2];

        kepX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
        kepY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
        kepZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

        axis_y[0] = kepX / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;
        axis_y[1] = kepY / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;
        axis_y[2] = kepZ / Origami.vector_length(new double[]{kepX, kepY, kepZ}) * zoom;
    }

    public java.util.List<int[]> alignmentPoints(Origami origami, int... denoms) {

        java.util.List<int[]> nsectors = new ArrayList<>();
        for (int i = 0; i < origami.vertices_size(); i++) {

            nsectors.add(new int[]{(int) projection(origami.vertices().get(i))[0],
                (int) projection(origami.vertices().get(i))[1]});
        }
        for (int n : denoms) {
            for (int i = 0; i < origami.polygons_size(); i++) {
                if (origami.isNonDegenerate(i)) {
                    for (int ii = 0; ii < origami.polygons().get(i).size() - 1; ii++) {

                        double[] p1 = origami.vertices().get(origami.polygons().get(i).get(ii));
                        double[] p2 = origami.vertices().get(origami.polygons().get(i).get(ii + 1));
                        for (int j = 1; j < n; j++) {

                            double[] nsect = new double[]{
                                (p1[0] * j + p2[0] * (n - j)) / n,
                                (p1[1] * j + p2[1] * (n - j)) / n,
                                (p1[2] * j + p2[2] * (n - j)) / n
                            };
                            nsectors.add(new int[]{(int) projection(nsect)[0], (int) projection(nsect)[1]});
                        }
                    }

                    double[] last1 = origami.vertices().get(origami.polygons().get(i).get(origami.polygons().get(i).size() - 1));
                    double[] last2 = origami.vertices().get(origami.polygons().get(i).get(0));
                    for (int j = 1; j < n; j++) {

                        double[] nsect = new double[]{
                            (last1[0] * j + last2[0] * (n - j)) / n,
                            (last1[1] * j + last2[1] * (n - j)) / n,
                            (last1[2] * j + last2[2] * (n - j)) / n
                        };
                        nsectors.add(new int[]{(int) projection(nsect)[0], (int) projection(nsect)[1]});
                    }
                }
            }
        }

        return nsectors;
    }

    public java.util.List<int[]> alignmentPoints2d(Origami origami) {

        java.util.List<int[]> espalda = new ArrayList<>();
        for (int i = 0; i < origami.vertices_size(); i++) {

            espalda.add(new int[]{(int) projection(origami.vertices2d().get(i))[0],
                (int) projection(origami.vertices2d().get(i))[1]});
        }

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (origami.isNonDegenerate(i)) {

                for (int ii = 0; ii < origami.polygons().get(i).size() - 1; ii++) {

                    double[] pont1 = origami.vertices2d().get(origami.polygons().get(i).get(ii));
                    double[] pont2 = origami.vertices2d().get(origami.polygons().get(i).get(ii + 1));
                    double[] felezo = Origami.midpoint(pont1, pont2);

                    espalda.add(new int[]{(int) projection(felezo)[0], (int) projection(felezo)[1]});
                }

                double[] puntoU1 = origami.vertices2d().get(origami.polygons().get(i).get(origami.polygons().get(i).size() - 1));
                double[] puntoU2 = origami.vertices2d().get(origami.polygons().get(i).get(0));
                double[] puntoMedio = Origami.midpoint(puntoU1, puntoU2);

                espalda.add(new int[]{(int) projection(puntoMedio)[0], (int) projection(puntoMedio)[1]});
            }
        }

        return espalda;
    }

    public void drawEdges(Graphics canvas, Color color, Origami origami) {

        canvas.setColor(color);

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                Polygon conductor = new Polygon();

                for (int ii = 0; ii < origami.polygons().get(i).size(); ii++) {

                    conductor.addPoint((short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + xshift,
                            (short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + yshift);
                }
                canvas.drawPolygon(conductor);
            }
        }
    }

    public String drawEdges(int x, int y, Origami origami) {

        String fuera = "1 w ";

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " m ";

                for (int ii = 1; ii < origami.polygons().get(i).size(); ii++) {
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + y);
                    fuera += " l ";
                }
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " l S ";
            }
        }

        return fuera;
    }

    public void drawPreview(Graphics canvas, Color color, Origami origami, double[] ppoint, double[] pnormal) {

        double[] vpt = camera_pos;
        double[] vnv = camera_dir;
        double[] xt = axis_x;
        double[] yt = axis_y;

        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        double[] puntoPie;
        double X, Y, Z, t;
        double[] direccionVector = pnormal;
        double U = direccionVector[0];
        double V = direccionVector[1];
        double W = direccionVector[2];
        double A = pnormal[0];
        double B = pnormal[1];
        double C = pnormal[2];

        X = camera_pos[0];
        Y = camera_pos[1];
        Z = camera_pos[2];
        t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);
        puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
        camera_pos = new double[]{
            puntoPie[0] + Origami.vector(puntoPie, camera_pos)[0],
            puntoPie[1] + Origami.vector(puntoPie, camera_pos)[1],
            puntoPie[2] + Origami.vector(puntoPie, camera_pos)[2]};

        X = camera_dir[0];
        Y = camera_dir[1];
        Z = camera_dir[2];
        t = -(A * X + B * Y + C * Z) / (A * U + B * V + C * W);
        puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
        camera_dir = new double[]{
            puntoPie[0] + Origami.vector(puntoPie, camera_dir)[0],
            puntoPie[1] + Origami.vector(puntoPie, camera_dir)[1],
            puntoPie[2] + Origami.vector(puntoPie, camera_dir)[2]};

        X = axis_x[0];
        Y = axis_x[1];
        Z = axis_x[2];
        t = -(A * X + B * Y + C * Z) / (A * U + B * V + C * W);
        puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
        axis_x = new double[]{
            puntoPie[0] + Origami.vector(puntoPie, axis_x)[0],
            puntoPie[1] + Origami.vector(puntoPie, axis_x)[1],
            puntoPie[2] + Origami.vector(puntoPie, axis_x)[2]};

        X = axis_y[0];
        Y = axis_y[1];
        Z = axis_y[2];
        t = -(A * X + B * Y + C * Z) / (A * U + B * V + C * W);
        puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
        axis_y = new double[]{
            puntoPie[0] + Origami.vector(puntoPie, axis_y)[0],
            puntoPie[1] + Origami.vector(puntoPie, axis_y)[1],
            puntoPie[2] + Origami.vector(puntoPie, axis_y)[2]};

        drawEdges(canvas, color, origami);
        camera_pos = vpt;
        camera_dir = vnv;
        axis_x = xt;
        axis_y = yt;
    }

    public String drawSelection(int x, int y, double[] ppoint, double[] pnormal, int polygonIndex, Origami origami) {

        String fuera = "0.8 0.8 0.8 rg ";

        ArrayList<Integer> selecciones = origami.polygonSelect(ppoint, pnormal, polygonIndex);
        for (int i : selecciones) {

            if (isDrawable(i, origami)) {

                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " m ";

                for (int ii = 1; ii < origami.polygons().get(i).size(); ii++) {
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + y);
                    fuera += " l ";
                }
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " l f ";
            }
        }

        return fuera;
    }

    public boolean isDrawable(int polygonIndex, Origami origami) {

        if (origami.polygons().get(polygonIndex).size() > 2) {

            for (int punto1ind : origami.polygons().get(polygonIndex)) {
                for (int pont2ind : origami.polygons().get(polygonIndex)) {

                    if (Origami.vector_length(Origami.vector_product(Origami.vector(origami.vertices().get(punto1ind), origami.vertices().get(origami.polygons().get(polygonIndex).get(0))),
                            Origami.vector(origami.vertices().get(pont2ind), origami.vertices().get(origami.polygons().get(polygonIndex).get(0))))) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isDrawable(int polygonIndex, Origami origami, int... ref) {

        if (origami.polygons().get(polygonIndex).size() > 2) {

            double maxSpace = 0;
            for (int i = 0; i < origami.polygons().get(polygonIndex).size(); i++) {

                int punto1ind = origami.polygons().get(polygonIndex).get(i);
                int punto0ind = origami.polygons().get(polygonIndex).get((i + 1) % origami.polygons().get(polygonIndex).size());
                int punto2ind = origami.polygons().get(polygonIndex).get((i + 2) % origami.polygons().get(polygonIndex).size());
                double space = Origami.vector_length(Origami.vector_product(Origami.vector(origami.vertices().get(punto1ind), origami.vertices().get(punto0ind)),
                        Origami.vector(origami.vertices().get(punto2ind), origami.vertices().get(punto0ind))));
                if (space > maxSpace) {
                    maxSpace = space;
                    ref[1] = punto1ind;
                    ref[2] = punto2ind;
                    ref[0] = punto0ind;
                }
            }
            if (maxSpace > 1) {
                return true;
            }
        }
        return false;
    }

    public void drawGradient(Graphics canvas, int rgb, Origami origami) {

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                double[] normalizado = Origami.vector_product(Origami.vector(origami.vertices().get(origami.polygons().get(i).get(0)),
                        origami.vertices().get(origami.polygons().get(i).get(1))),
                        Origami.vector(origami.vertices().get(origami.polygons().get(i).get(0)),
                                origami.vertices().get(origami.polygons().get(i).get(2))));

                double longitud = Origami.vector_length(normalizado);
                if (longitud != 0) {
                    normalizado[0] = normalizado[0] / longitud;
                    normalizado[1] = normalizado[1] / longitud;
                    normalizado[2] = normalizado[2] / longitud;
                }

                double alfa = 1 - Math.abs(Origami.scalar_product(camera_dir, normalizado));
                int sintesis = Origami.scalar_product(camera_dir, normalizado) > 0 ? (rgb & 0xFFFFFF) : paper_back_color;

                Polygon conductor = new Polygon();

                double[] close = null, far = null;

                for (int ii = 0; ii < origami.polygons().get(i).size(); ii++) {

                    conductor.addPoint((short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + xshift,
                            (short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + yshift);

                    double sc = Origami.scalar_product(origami.vertices().get(origami.polygons().get(i).get(ii)), camera_dir);
                    if (close == null ? true : sc > Origami.scalar_product(close, camera_dir)) {
                        close = origami.vertices().get(origami.polygons().get(i).get(ii));
                    }
                    if (far == null ? true : sc < Origami.scalar_product(far, camera_dir)) {
                        far = origami.vertices().get(origami.polygons().get(i).get(ii));
                    }
                }

                double constante = far[0] * normalizado[0] + far[1] * normalizado[1] + far[2] * normalizado[2];

                double X = camera_dir[0];
                double Y = camera_dir[1];
                double Z = camera_dir[2];
                double U = normalizado[0];
                double V = normalizado[1];
                double W = normalizado[2];
                double A = normalizado[0];
                double B = normalizado[1];
                double C = normalizado[2];
                double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);

                double[] grad_dir = {X + t * U, Y + t * V, Z + t * W};
                double lambda = (Origami.scalar_product(close, camera_dir) - Origami.scalar_product(far, camera_dir)) / Origami.scalar_product(grad_dir, camera_dir);

                close = new double[]{
                    far[0] + grad_dir[0] * lambda,
                    far[1] + grad_dir[1] * lambda,
                    far[2] + grad_dir[2] * lambda,};

                double dclose = Origami.scalar_product(Origami.vector(close, camera_pos), camera_dir) / Math.max(origami.circumscribedSquareSize() * Math.sqrt(2) / 2, 1);
                double dfar = Origami.scalar_product(Origami.vector(far, camera_pos), camera_dir) / Math.max(origami.circumscribedSquareSize() * Math.sqrt(2) / 2, 1);
                float[] hsb = Color.RGBtoHSB((sintesis >>> 16) % 0x100, (sintesis >>> 8) % 0x100, sintesis % 0x100, null);

                int rgb1 = Color.HSBtoRGB(hsb[0], Math.max(Math.min((float) (.5 - dclose * .5), 1f), 0f), 1f) & 0xFFFFFF;
                int rgb2 = Color.HSBtoRGB(hsb[0], Math.max(Math.min((float) (.5 - dfar * .5), 1f), 0f), hsb[2]) & 0xFFFFFF;

                Color c1, c2;
                try {
                    c1 = new Color((rgb1 >>> 16) % 0x100, (rgb1 >>> 8) % 0x100, rgb1 % 0x100, (int) (alfa * 64) + 100);
                } catch (Exception exc) {
                    c1 = new Color((rgb1 >>> 16) % 0x100, (rgb1 >>> 8) % 0x100, rgb1 % 0x100, 188);
                }
                try {
                    c2 = new Color((rgb2 >>> 16) % 0x100, (rgb2 >>> 8) % 0x100, rgb2 % 0x100, (int) (alfa * 64) + 100);
                } catch (Exception exc) {
                    c2 = new Color((rgb2 >>> 16) % 0x100, (rgb2 >>> 8) % 0x100, rgb2 % 0x100, 188);
                }
                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                        (float) projection(close)[0] + xshift,
                        (float) projection(close)[1] + yshift,
                        c1,
                        (float) projection(far)[0] + xshift,
                        (float) projection(far)[1] + yshift,
                        c2
                );
                ((java.awt.Graphics2D) canvas).setPaint((gp));

                canvas.fillPolygon(conductor);
            }
        }
    }

    public void drawFaces(Graphics canvas, int rgb, Origami origami) {

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                double[] normalizado = Origami.vector_product(Origami.vector(origami.vertices().get(origami.polygons().get(i).get(0)),
                        origami.vertices().get(origami.polygons().get(i).get(1))),
                        Origami.vector(origami.vertices().get(origami.polygons().get(i).get(0)),
                                origami.vertices().get(origami.polygons().get(i).get(2))));

                double longitud = Origami.vector_length(normalizado);
                if (longitud != 0) {
                    normalizado[0] = normalizado[0] / longitud;
                    normalizado[1] = normalizado[1] / longitud;
                    normalizado[2] = normalizado[2] / longitud;
                }

                double alfa = 1 - Math.abs(Origami.scalar_product(camera_dir, normalizado));
                int sintesis = Origami.scalar_product(camera_dir, normalizado) > 0 ? (rgb & 0xFFFFFF) : paper_back_color;

                try {
                    canvas.setColor(new Color((sintesis >>> 16) % 0x100, (sintesis >>> 8) % 0x100, sintesis % 0x100, (int) (alfa * 128) + 80));
                } catch (Exception exc) {
                    canvas.setColor(new Color((sintesis >>> 16) % 0x100, (sintesis >>> 8) % 0x100, sintesis % 0x100, 188));
                }

                Polygon conductor = new Polygon();

                for (int ii = 0; ii < origami.polygons().get(i).size(); ii++) {

                    conductor.addPoint((short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + xshift,
                            (short) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + yshift);
                }

                canvas.fillPolygon(conductor);
            }
        }
    }

    public String drawFaces(int x, int y, Origami origami) {

        String fuera = "0.8 0.8 0.8 rg ";

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " m ";

                for (int ii = 1; ii < origami.polygons().get(i).size(); ii++) {
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[0]) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(ii)))[1]) + y);
                    fuera += " l ";
                }
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[0]) + x);
                fuera += " ";
                fuera += Integer.toString((int) (projection(origami.vertices().get(origami.polygons().get(i).get(0)))[1]) + y);
                fuera += " l f ";
            }
        }

        return fuera;
    }

    public void drawCreasePattern(Graphics canvas, Color color, Origami origami) {

        canvas.setColor(color);

        for (int i = 0; i < origami.polygons_size(); i++) {

            if (isDrawable(i, origami)) {

                Polygon conductor = new Polygon();

                for (int ii = 0; ii < origami.polygons().get(i).size(); ii++) {

                    conductor.addPoint((short) (projection(origami.vertices2d().get(origami.polygons().get(i).get(ii)))[0]) + xshift,
                            (short) (projection(origami.vertices2d().get(origami.polygons().get(i).get(ii)))[1]) + yshift);
                }
                canvas.drawPolygon(conductor);
            }
        }
    }
    
    public void drawFoldingLine(Graphics canvas, Color color, double[] ppoint, double[] pnormal, Origami origami) {
        
        canvas.setColor(color);
        java.util.ArrayList<double[]> line = origami.foldingLine(ppoint, pnormal);
        for (int i = 0; i < line.size(); i += 2) {
            canvas.drawLine(
                    (short) (projection(line.get(i))[0] + xshift),
                    (short) (projection(line.get(i))[1] + yshift), 
                    (short) (projection(line.get(i+1))[0] + xshift),
                    (short) (projection(line.get(i+1))[1] + yshift));
        }
    }
    
    public void draw2dFoldingLine(Graphics canvas, Color color, double[] ppoint, double[] pnormal, Origami origami) {
        
        canvas.setColor(color);
        java.util.ArrayList<double[]> line = origami.foldingLine2d(ppoint, pnormal);
        for (int i = 0; i < line.size(); i += 2) {
            canvas.drawLine(
                    (short) (projection(line.get(i))[0] + xshift),
                    (short) (projection(line.get(i))[1] + yshift), 
                    (short) (projection(line.get(i+1))[0] + xshift),
                    (short) (projection(line.get(i+1))[1] + yshift));
        }
    }

    public String pfdLiner(int x, int y, double[] ppoint, double[] pnormal) {

        String fuera = "0.4 0.4 0.4 RG [5 5] 0 d ";
        double[] levelnv_2D = projection0(pnormal);
        double[] seccionPlana_2D = projection(ppoint);
        boolean lineto = false;
        double limit = 100;

        if (pdfLinerDir(pnormal) == 'J' || pdfLinerDir(pnormal) == 'B') {

            double[] leveliv_2D = new double[]{-levelnv_2D[1] / levelnv_2D[0], 1};

            if (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1]) <= limit
                    && seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1]) >= -limit) {

                fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1])) + x);
                fuera += " ";
                fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[1])) + y);
                fuera += " m ";
                lineto = true;
            }

            if (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1]) <= limit
                    && seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1]) >= -limit) {

                fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1])) + x);
                fuera += " ";
                fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[1])) + y);
                if (lineto) {

                    fuera += " l ";
                    lineto = false;
                } else {

                    fuera += " m ";
                    lineto = true;
                }
            }

            if (lineto) {

                leveliv_2D = new double[]{1, -levelnv_2D[0] / levelnv_2D[1]};

                if (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0]) <= limit
                        && seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0]) >= -limit) {

                    fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[0])) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0])) + y);
                    fuera += " l ";
                }

                if (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0]) <= limit
                        && seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0]) >= -limit) {

                    fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[0])) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0])) + y);
                    fuera += " l ";
                }
            }
        } else {

            double[] leveliv_2D = new double[]{1, -levelnv_2D[0] / levelnv_2D[1]};

            if (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0]) <= limit
                    && seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0]) >= -limit) {

                fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[0])) + x);
                fuera += " ";
                fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[0])) + y);
                fuera += " m ";
                lineto = true;
            }

            if (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0]) <= limit
                    && seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0]) >= -limit) {

                fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[0])) + x);
                fuera += " ";
                fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[0])) + y);
                if (lineto) {

                    fuera += " l ";
                    lineto = false;
                } else {

                    fuera += " m ";
                    lineto = true;
                }
            }

            if (lineto) {

                leveliv_2D = new double[]{-levelnv_2D[1] / levelnv_2D[0], 1};

                if (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1]) <= limit
                        && seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1]) >= -limit) {

                    fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (limit - seccionPlana_2D[1])) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (limit - seccionPlana_2D[1])) + y);
                    fuera += " l ";
                }

                if (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1]) <= limit
                        && seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1]) >= -limit) {

                    fuera += Integer.toString((int) (seccionPlana_2D[0] + leveliv_2D[0] * (-limit - seccionPlana_2D[1])) + x);
                    fuera += " ";
                    fuera += Integer.toString((int) (seccionPlana_2D[1] + leveliv_2D[1] * (-limit - seccionPlana_2D[1])) + y);
                    fuera += " l ";
                }
            }
        }

        fuera += "S [ ] 0 d 0.0 0.0 0.0 RG ";
        return fuera;
    }

    final static public int PDF_NORTH = 'F';
    final static public int PDF_SOUTH = 'L';
    final static public int PDF_WEST = 'B';
    final static public int PDF_EAST = 'J';

    public int pdfLinerDir(double[] pnormal) {

        double[] levelnv_2D = projection0(pnormal);

        if (levelnv_2D[0] < levelnv_2D[1]) {

            if (levelnv_2D[0] < -levelnv_2D[1]) {

                return PDF_WEST;
            } else {

                return PDF_NORTH;
            }
        } else {

            if (levelnv_2D[0] < -levelnv_2D[1]) {

                return PDF_SOUTH;
            } else {

                return PDF_EAST;
            }
        }
    }

    public java.util.ArrayList<int[]> centers(Origami origami) {

        java.util.ArrayList<int[]> espalda = new java.util.ArrayList<>(java.util.Arrays.asList(new int[][]{}));
        for (int i = 0; i < origami.polygons_size(); i++) {
            espalda.add(new int[]{(short) (projection(origami.polygonCenter(i))[0]) + xshift, (short) (projection(origami.polygonCenter(i))[1]) + yshift});
        }
        return espalda;
    }

    public int polygonSelect(int cursor_x, int cursor_y, Origami origami) {

        java.util.ArrayList<int[]> centro = centers(origami);

        int min = Integer.MAX_VALUE;
        int areaMinima = -1;
        for (int i = 0; i < origami.polygons_size(); i++) {

            if (origami.isNonDegenerate(i)) {

                int[] puntoK = centro.get(i);
                int distanciaAlCuadrado = (puntoK[0] - cursor_x) * (puntoK[0] - cursor_x) + (puntoK[1] - cursor_y) * (puntoK[1] - cursor_y);

                if (distanciaAlCuadrado < min) {
                    min = distanciaAlCuadrado;
                    areaMinima = i;
                }
            }
        }
        return areaMinima;
    }

    public void adjust(Origami origami) {

        Double a, f, b, j, h, e;
        f = (a = (j = (b = (e = (h = null)))));
        for (int i = 0; i < origami.vertices_size(); i++) {

            if (b == null || origami.vertices().get(i)[0] < b) {
                b = origami.vertices().get(i)[0];
            }
            if (j == null || origami.vertices().get(i)[0] > j) {
                j = origami.vertices().get(i)[0];
            }
            if (a == null || origami.vertices().get(i)[1] < a) {
                a = origami.vertices().get(i)[1];
            }
            if (f == null || origami.vertices().get(i)[1] > f) {
                f = origami.vertices().get(i)[1];
            }
            if (h == null || origami.vertices().get(i)[2] < h) {
                h = origami.vertices().get(i)[2];
            }
            if (e == null || origami.vertices().get(i)[2] > e) {
                e = origami.vertices().get(i)[2];
            }
        }

        if (origami.vertices_size() > 0) {
            camera_pos = new double[]{(b + j) / 2, (a + f) / 2, (h + e) / 2};
        }
    }

    public void unadjust(Origami origami) {

        double[] centroGravedad = new double[]{0.0, 0.0, 0.0};
        for (double[] pont : origami.corners()) {
            centroGravedad = new double[]{centroGravedad[0] + pont[0], centroGravedad[1] + pont[1], 0};
        }

        centroGravedad = new double[]{centroGravedad[0] / origami.corners().size(), centroGravedad[1] / origami.corners().size(), 0};
        camera_pos = centroGravedad;
    }
    
    public void setOrthogonalView(int orientation) {

        switch (orientation) {

            case 0:
                camera_dir[0] = 0;
                camera_dir[1] = 0;
                camera_dir[2] = 1 * zoom;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
            case 1:
                camera_dir[0] = 0;
                camera_dir[1] = 1 * zoom;
                camera_dir[2] = 0;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 0;
                axis_y[2] = -1 * zoom;
                break;
            case 2:
                camera_dir[0] = -1 * zoom;
                camera_dir[1] = 0;
                camera_dir[2] = 0;
                axis_x[0] = 0;
                axis_x[1] = 0;
                axis_x[2] = 1 * zoom;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
            default:
                camera_dir[0] = 0;
                camera_dir[1] = 0;
                camera_dir[2] = 1 * zoom;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
        }

        this.orientation = (byte)orientation;
    }

    public void nextOrthogonalView() {

        switch (orientation) {

            case 0:
                camera_dir[0] = 0;
                camera_dir[1] = 0;
                camera_dir[2] = 1 * zoom;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
            case 1:
                camera_dir[0] = 0;
                camera_dir[1] = 1 * zoom;
                camera_dir[2] = 0;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 0;
                axis_y[2] = -1 * zoom;
                break;
            case 2:
                camera_dir[0] = -1 * zoom;
                camera_dir[1] = 0;
                camera_dir[2] = 0;
                axis_x[0] = 0;
                axis_x[1] = 0;
                axis_x[2] = 1 * zoom;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
            default:
                camera_dir[0] = 0;
                camera_dir[1] = 0;
                camera_dir[2] = 1 * zoom;
                axis_x[0] = 1 * zoom;
                axis_x[1] = 0;
                axis_x[2] = 0;
                axis_y[0] = 0;
                axis_y[1] = 1 * zoom;
                axis_y[2] = 0;
                break;
        }

        orientation = (byte) ((orientation + 1) % 3);
    }

    public void setTexture(java.awt.image.BufferedImage texture) throws Exception {
        
        if (texture.getColorModel().hasAlpha()) {
            throw OrigamiException.H013;
        }
        this.texture = texture;
    }

    public void updateBuffer(Origami origami) {

        java.awt.image.BufferedImage mostrar = new java.awt.image.BufferedImage(texture.getWidth(), texture.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D canvas = mostrar.createGraphics();
        canvas.setBackground(Color.WHITE);
        canvas.clearRect(0, 0, texture.getWidth(), texture.getHeight());
        int[][] marco = new int[origami.polygons_size()][];

        for (int i = 0; i < origami.polygons_size(); i++) {

            int[] mensajes = new int[3];
            if (isDrawable(i, origami, mensajes)) {

                marco[i] = mensajes;

                Polygon conductor = new Polygon();
                for (int ii = 0; ii < origami.polygons().get(i).size(); ii++) {

                    conductor.addPoint((short) (new Camera(0, 0, 1d).projection(origami.vertices2d().get(origami.polygons().get(i).get(ii)))[0]) + 200,
                            (short) (new Camera(0, 0, 1d).projection(origami.vertices2d().get(origami.polygons().get(i).get(ii)))[1]) + 200);
                }
                canvas.setColor(new Color(i));
                canvas.fillPolygon(conductor);
            }
        }

        int[] crudo = ((java.awt.image.DataBufferInt) mostrar.getRaster().getDataBuffer()).getData();

        int len = texture.getHeight() * texture.getWidth();
        int fila = texture.getWidth();
        space_buffer = new double[len][];

        for (int i = 0; i < len; i++) {

            int sintesis = crudo[i] & 0xFFFFFF;
            if (sintesis != 0xFFFFFF) {

                try {
                    double x_1 = origami.vertices2d().get(marco[sintesis][1])[0] - origami.vertices2d().get(marco[sintesis][0])[0];
                    double x_2 = origami.vertices2d().get(marco[sintesis][1])[1] - origami.vertices2d().get(marco[sintesis][0])[1];
                    double y_1 = origami.vertices2d().get(marco[sintesis][2])[0] - origami.vertices2d().get(marco[sintesis][0])[0];
                    double y_2 = origami.vertices2d().get(marco[sintesis][2])[1] - origami.vertices2d().get(marco[sintesis][0])[1];
                    double a_1 = (double) (i % fila) - origami.vertices2d().get(marco[sintesis][0])[0];
                    double a_2 = (double) i / fila - origami.vertices2d().get(marco[sintesis][0])[1];

                    double lambda1 = (a_1 * y_2 - a_2 * y_1) / (x_1 * y_2 - x_2 * y_1);
                    double lambda2 = (a_1 * x_2 - a_2 * x_1) / (y_1 * x_2 - y_2 * x_1);
                    double[] v3d1 = Origami.vector(origami.vertices.get(marco[sintesis][1]), origami.vertices.get(marco[sintesis][0]));
                    double[] v3d2 = Origami.vector(origami.vertices.get(marco[sintesis][0]), origami.vertices.get(marco[sintesis][2]));
                    space_buffer[i] = Origami.vector(Origami.vector(Origami.scalar_multip(v3d1, lambda1), Origami.scalar_multip(v3d2, lambda2)), Origami.scalar_multip(origami.vertices.get(marco[sintesis][0]), -1));
                } catch (Exception ex) {
                }
            }
        }

        new Camera(200, 200, 1d).drawCreasePattern(texture.createGraphics(), Color.BLACK, origami);
    }

    public void drawTexture(Graphics canvas, int w, int h) {

        byte[] crudo = ((java.awt.image.DataBufferByte) texture.getRaster().getDataBuffer()).getData();
        Double[][] depth_buffer = new Double[w][h];
        java.awt.image.BufferedImage fuera = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D bleach = fuera.createGraphics(); //bleach = blanqueador
        bleach.setBackground(Color.WHITE);
        bleach.clearRect(0, 0, w, h);

        for (int i = 0; i < crudo.length; i += 3) {

            int sintesis = (crudo[i] & 0xFF) + ((crudo[i + 1] & 0xFF) << 8) + ((crudo[i + 2] & 0xFF) << 16);
            experimento:
            try {
                double[] punto = space_buffer[i / 3];
                if (punto == null) {
                    break experimento;
                }
                double[] cerda = projection(punto);
                short vetX = (short) (cerda[0] + xshift);
                short vetY = (short) (cerda[1] + yshift);
                if (vetX >= 0 && vetX < depth_buffer.length && vetY >= 0 && vetY < depth_buffer[0].length) {
                    if (depth_buffer[vetX][vetY] == null || Origami.scalar_product(punto, camera_dir) > depth_buffer[vetX][vetY]) {

                        depth_buffer[vetX][vetY] = Origami.scalar_product(punto, camera_dir);
                        fuera.setRGB(vetX, vetY, sintesis);
                    }
                }
            } catch (Exception ex) {
            }
        }
        canvas.drawImage(fuera, 0, 0, null);
    }
}
