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
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a three-dimensional rigid origami model consisting of convex
 * polygonal faces. Provides methods to manipulate the model by various types of
 * transformations.
 *
 * @author Attila Bágyoni (ba-sz-at@users.sourceforge.net)
 * @since 2013-01-14
 */
public class Origami {

    /**
     * Creates a new origami model.
     * <br>
     * The new model will be initialized with an empty
     * {@link #history() histroy}, a {@link #papertype() papertype} of the
     * specified {@link PaperType}, and will be {@link #reset() reset}
     * immediately afterwards.
     *
     * @param papertype	The {@link #papertype() papertype} of the new instance.
     */
    public Origami(PaperType papertype) {

        vertices = (vertices2d = new ArrayList<>(Arrays.asList(new double[][]{})));
        vertices_size = 0;
        polygons = new ArrayList<>();
        polygons_size = 0;
        history = new ArrayList<>(Arrays.asList(new double[][]{}));
        history_pointer = 0;
        this.papertype = papertype;
        reset();
    }

    /**
     * Creates a new origami model.
     * <br>
     * The new model will be initialized with a {@link #history() history} that
     * is the specified {@link ArrayList}, a {@link #papertype() papertype} of
     * the specified {@link PaperType}, and will be {@link #reset() reset}
     * immediately afterwards.
     * <br>
     * The {@link #execute() execute()} method will not be called in this
     * constructor; you will have to call it explicitly to fold the model based
     * on its {@link #history() history}.
     *
     * @param papertype	The {@link #papertype() papertype} of the new instance.
     * @param history	The {@link #history() history} of the new instance.
     */
    public Origami(PaperType papertype, ArrayList<double[]> history) {

        vertices = (vertices2d = new ArrayList<>(Arrays.asList(new double[][]{})));
        vertices_size = 0;
        polygons = new ArrayList<>();
        polygons_size = 0;
        this.history = history;
        history_pointer = this.history.size();
        this.papertype = papertype;
        reset();
    }

    /**
     * Creates a new origami model.
     * <br>
     * The new model will be initialized with an empty
     * {@link #history() history}, a {@link #papertype() papertype} of
     * {@link PaperType#Custom}, a {@link #corners() corners} list that is the
     * {@link #ccwWindingOrder(ArrayList) ccwWindingOrder} of the specified
     * {@link ArrayList}, and will be {@link #reset() reset} immediately
     * afterwards.
     * 
     * @param corners The {@link #corners() corners} list of the new instance
     * in an arbitrary order.
     * @throws Exception if {@code !isConvex(ccwWindingOrder(corners))}
     */
    public Origami(ArrayList<double[]> corners) throws Exception {

        vertices = (vertices2d = new ArrayList<>(Arrays.asList(new double[][]{})));
        vertices_size = 0;
        polygons = new ArrayList<>();
        polygons_size = 0;
        history = new ArrayList<>(Arrays.asList(new double[][]{}));
        history_pointer = 0;
        papertype = PaperType.Custom;
        this.corners = ccwWindingOrder(corners);
        if (!isConvex(this.corners)) {
            throw new Exception("Unexpected concave polygon");
        }
        reset();
    }

    /**
     * Creates a new origami model.
     * <br>
     * The new model will be initialized with a {@link #history() history} that
     * is the specified {@link ArrayList}, a {@link #papertype() papertype} of
     * {@link PaperType#Custom}, a {@link #corners() corners} list that is the
     * {@link #ccwWindingOrder(ArrayList) ccwWindingOrder} of the specified
     * {@link ArrayList}, and will be {@link #reset() reset} immediately
     * afterwards.
     * <br>
     * The {@link #execute() execute()} method will not be called in this
     * constructor; you will have to call it explicitly to fold the model based
     * on its {@link #history() history}.
     * 
     * @param corners The {@link #corners() corners} list of the new instance.
     * @param history The {@link #history() history} of the new instance.
     * 
     * @throws Exception if {@code !isConvex(ccwWindingOrder(corners))}
     */
    public Origami(ArrayList<double[]> corners, ArrayList<double[]> history) throws Exception {

        vertices = (vertices2d = new ArrayList<>(Arrays.asList(new double[][]{})));
        vertices_size = 0;
        polygons = new ArrayList<>();
        polygons_size = 0;
        this.history = history;
        history_pointer = this.history.size();
        papertype = PaperType.Custom;
        this.corners = ccwWindingOrder(corners);
        if (!isConvex(this.corners)) {
            throw new Exception("Unexpected concave polygon");
        }
        reset();
    }
    
    public int generation() {
        return 1;
    }

    protected ArrayList<double[]> vertices = new ArrayList<>(Arrays.asList(new double[][]{}));

    /**
     * Returns a list of all the vertices in this origami, regardless of whether
     * they are used in any of the {@link #polygons() polygons} or not.
     * The elements in this list are {@code double[]}s, each one representing
     * the coordinates of a vertex in the origami space, i. e. the 3-dimensional
     * space where this origami is edited.
     * <br>
     * In this list, every vertex has a corresponding preimage in the
     * {@link #vertices2d() vertices2d} list that has the same index.
     * 
     * @return An {@link ArrayList} representing the vertices of this origami in
     * the origami space.
     */
    public ArrayList<double[]> vertices() {
        return vertices;
    }

    protected int vertices_size = 0;

    /**
     * Returns the number of vertices in this origami, which is expected to be
     * the {@link ArrayList#size() size} of the {@link #vertices() vertices}
     * and the {@link #vertices2d() vertices2d} list of this origami at the same
     * time.
     * 
     * @return The number of vertices in this origami.
     */
    public int vertices_size() {
        return vertices_size;
    }

    protected ArrayList<ArrayList<Integer>> polygons = new ArrayList<>();

    /**
     * Returns a list of all the polygons in this origami.
     * Each element in this list is an {@link ArrayList} that stores the
     * vertices of one of this origami's polygons in the form of indices
     * pointing into both the {@link #vertices() vertices} and the
     * {@link #vertices2d() vertices2d} list. In each polygon, the vertices are
     * arranged in a counter-clockwise winding order.
     * <br>
     * The polygons themselves do not have any particular order.
     * 
     * @return As described in the above.
     */
    public ArrayList<ArrayList<Integer>> polygons() {
        return polygons;
    }

    protected int polygons_size = 0;

    /**
     * Returns the number of polygons in this origami.
     * 
     * @return The number of polygons in this origami.
     */
    public int polygons_size() {
        return polygons_size;
    }

    protected ArrayList<double[]> history = new ArrayList<>(Arrays.asList(new double[][]{}));

    /**
     * Returns an {@link ArrayList} containing information about some of the
     * methods previously called on this origami. <b>This member is intended for
     * private use</b>, although it may be replaced by a more straightforward
     * and human-readable format in later versions.
     * 
     * @return As described in the above.
     */
    public ArrayList<double[]> history() {
        return history;
    }

    protected int history_pointer;
    
    public int history_pointer() {
    	return history_pointer;
    }

    /**
     * Enumerates the preset paper types of the {@link Origami} class.
     */
    public enum PaperType {

        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with vertices {0, 0, 0}, (424.3, 0, 0} {424.3, 300, 0} and 
         * {300, 0, 0} when {@link #reset() reset}.
         */
        A4('A'),
        
        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with vertices {0, 0, 0}, (400, 0, 0} {400, 400, 0} and
         * {0, 400, 0} when {@link #reset() reset}.
         */
        Square('N'),
        
        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with vertices {300, 346.41, 0}, {400, 173.205, 0},
         * {300, 0, 0}, {100, 0, 0}, {0, 173.205, 0} and {100, 346.41, 0} when
         * {@link #reset() reset}.
         */
        Hexagon('H'),
        
        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with vertices {0, 0, 0}, {400, 0, 0}, {400, 170, 0} and
         * {0, 170, 0} when {@link #reset() reset}.
         */
        Dollar('D'),
        
        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with vertices {0, 0, 0}, {400, 0, 0}, {400, 181.82, 0} and
         * {0, 181.82, 0} when {@link #reset() reset}.
         */
        Forint('F'),
        
        /**
         * An Origami of this {@link #papertype() papertype} will have a single
         * polygon with the vertices obtained from the
         * {@link #ccwWindingOrder(ArrayList) ccwWindingOrder} of its
         * {@link #corners() corners} when {@link #reset() reset}.
         */
        Custom('E');
        
        final private char ID;
        final static private HashMap<Character, PaperType> allid = new HashMap<>();

        static {

            for (PaperType p : PaperType.values()) {

                allid.put(p.ID, p);
            }
        }

        private PaperType(final char c) {

            ID = c;
        }

        /**
         * Returns the assigned {@code char} value of this PaperType.
         *
         * @return The {@code char} value of this PaperType.
         */
        public char toChar() {

            return ID;
        }

        /**
         * Returns the PaperType the specified {@code char} value is assigned
         * to.
         *
         * @param c The {@code char} value of the desired PaperType.
         * @return The desired PaperType.
         */
        static public PaperType forChar(char c) {

            return allid.get(c);
        }

        @Override
        public String toString() throws NullPointerException {

            if (super.equals(A4)) {

                return "A4";
            } else if (super.equals(Square)) {

                return "Square";
            } else if (super.equals(Hexagon)) {

                return "Regular hexagon";
            } else if (super.equals(Dollar)) {

                return "Dollar bill";
            } else if (super.equals(Forint)) {

                return "Forint bill";
            } else if (super.equals(Custom)) {

                return "Custom";
            } else {

                throw new NullPointerException();
            }
        }
    }
    
    protected PaperType papertype = PaperType.Square;

    /**
     * Returns the paper type of this origami. The {@link #reset() reset} method
     * will initialize the {@link #vertices() vertices} and
     * {@link #polygons() polygons} of this origami depending partly on this
     * value.
     * 
     * @return As described in the above.
     * @see PaperType
     */
    public PaperType papertype() {
        return papertype;
    }
    
    protected ArrayList<double[]> corners = new ArrayList<>(Arrays.asList(new double[][]{}));

    /**
     * Returns a copy of the original {@link #vertices() vertices} list of this
     * origami, as it was initialized after the last {@link #reset() reset}
     * call. If {@code (papertype() == PaperType.Custom)}, the {@link #reset()
     * reset} method will initialize the {@link #vertices() vertices} and the
     * {@link #vertices2d() vertices2d} lists as copies of this list.
     * 
     * @return As described in the above.
     */
    public ArrayList<double[]> corners() {
        return corners;
    }
    
    protected ArrayList<double[]> vertices2d = new ArrayList<>(Arrays.asList(new double[][]{}));

    /**
     * Returns a list of all the vertices in this origami, regardless of whether
     * they are used in any of the {@link #polygons() polygons} or not.
     * The elements in this list are {@code double[]}s, each one representing
     * the coordinates of a vertex in the paper space, i. e. the 2-dimensional
     * space where the vertices of this origami would be if it were unfolded.
     * <br>
     * In this list, every vertex has a corresponding image in the
     * {@link #vertices() vertices} list that has the same index.
     * 
     * @return An {@link ArrayList} representing the vertices of this origami in
     * the paper space.
     */
    public ArrayList<double[]> vertices2d() {
        return vertices2d;
    }

    /**
     * Adds a new vertex to the end of the {@link #vertices() vertices} list of
     * this origami.
     * 
     * @param point The 3-dimensional coordinates of the new vertex in the
     * origami space as {@code double}s.
     */
    protected void addVertex(double... point) {
        vertices.add(point);
        vertices_size++;
    }

    /**
     * Adds a new vertex to the end of the {@link #vertices2d() vertices2d} list
     * of this origami.
     * 
     * @param point The 2-dimensional coordinates of the new vertex in the paper
     * space as {@code double}s.
     */
    protected void add2dVertex(double... point) {
        vertices2d.add(point);
    }

    /**
     * Adds a new polygon to the end of the {@link #polygons() polygons} list
     * of this origami.
     * <br>
     * When adding a new polygon, {@code isConvex(polygon)} is expected (but not
     * checked) to be {@code true}.
     * 
     * @param polygon An {@link ArrayList} that contains zero-based indices 
     * pointing into the {@link #vertices() vertices} and the {@link 
     * #vertices2d() vertices2d} list of this origami in a counter-clockwise
     * winding order.
     */
    protected void addPolygon(ArrayList<Integer> polygon) {
        polygons.add(polygon);
        polygons_size++;
    }

    /**
     * Removes the polygon from this origami's {@link #polygons() polygons} at
     * the specified index.
     * 
     * @param polygonIndex The zero-base index at which the polygon to remove is
     * located in the {@link #polygons() polygons} list.
     */
    protected void removePolygon(int polygonIndex) {
        polygons.remove(polygonIndex);
        polygons_size--;
    }

    static public boolean plane_between_points(double[] ppoint, double[] pnormal, double[] A, double[] B) {

        double konst = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        boolean tanto = false, demasiado = false;

        if ((A[0] * pnormal[0] + A[1] * pnormal[1] + A[2] * pnormal[2] - konst) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) > 0.00000001) {
            tanto = true;
        }
        if ((A[0] * pnormal[0] + A[1] * pnormal[1] + A[2] * pnormal[2] - konst) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) < -0.00000001) {
            demasiado = true;
        }
        if ((B[0] * pnormal[0] + B[1] * pnormal[1] + B[2] * pnormal[2] - konst) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) > 0.00000001) {
            tanto = true;
        }
        if ((B[0] * pnormal[0] + B[1] * pnormal[1] + B[2] * pnormal[2] - konst) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) < -0.00000001) {
            demasiado = true;
        }

        return tanto && demasiado;
    }

    static public boolean point_on_plane(double[] ppoint, double[] pnormal, double[] A) {

        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        return (Math.abs(A[0] * pnormal[0] + A[1] * pnormal[1] + A[2] * pnormal[2] - constante) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) < 1);
    }
    final static public double[] nullvektor = new double[]{0, 0, 0};

    static public double[] vector_product(double[] v1, double[] v2) {

        return new double[]{(v1[1] * v2[2] - v1[2] * v2[1]), (v1[2] * v2[0] - v1[0] * v2[2]), (v1[0] * v2[1] - v1[1] * v2[0])};
    }

    static public double scalar_product(double[] v1, double[] v2) {

        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    static public double[] scalar_multip(double[] v, double lambda) {

        return new double[]{v[0]*lambda, v[1]*lambda, v[2]*lambda};
    }

    static public double[] vector(double[] A, double[] B) {

        if (A.length == 3 && B.length == 3) {

            return new double[]{A[0] - B[0], A[1] - B[1], A[2] - B[2]};
        }

        return new double[]{A[0] - B[0], A[1] - B[1], 0};
    }

    static public double[] midpoint(double[] A, double[] B) {

        return new double[]{(A[0] + B[0]) / 2, (A[1] + B[1]) / 2, (A[2] + B[2]) / 2};
    }

    static public double[] length_to_100(double[] v) {

        double hossz = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]) * 0.01;
        return new double[]{v[0] / hossz, v[1] / hossz, v[2] / hossz};
    }

    static public double vector_length(double[] v) {

        double hossz = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return hossz;
    }

    static public double angle(double[] v1, double[] v2) {

        if (vector_length(v1) > 0 && vector_length(v2) > 0) {

            double arg1 = Math.acos(v1[0] / vector_length(v1));
            if (v1[1] < 0) {
                arg1 = 2 * Math.PI - arg1;
            }
            double arg2 = Math.acos(v2[0] / vector_length(v2));
            if (v2[1] < 0) {
                arg2 = 2 * Math.PI - arg2;
            }

            double szog = arg1 - arg2;
            while (szog < 0) {
                szog += 2 * Math.PI;
            }
            while (szog > 2 * Math.PI) {
                szog -= 2 * Math.PI;
            }

            return szog;

        }
        return 0;
    }

    /**
     * Checks if the polygon at the specified index in the {@link #polygons()
     * polygons} list is at least one-dimensional, i. e. it has two vertices
     * with a positive distance between them.
     * 
     * @param polygonIndex The zero-base index at which the polygon to check is
     * located in the {@link #polygons() polygons} list.
     * @return {@code false} iff the specified polygon is zero-dimensional.
     */
    public boolean isNonDegenerate(int polygonIndex) {

        if (polygons.get(polygonIndex).size() > 1) {
            for (int punto : polygons.get(polygonIndex)) {
                if (vector_length(vector(vertices.get(punto), vertices.get(polygons.get(polygonIndex).get(0)))) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isCut(double[] ppoint, double[] pnormal, int polygonIndex) {

        if (isNonDegenerate(polygonIndex)) {

            boolean uno = false, otro = false;
            for (int i = 0; i < polygons.get(polygonIndex).size(); i++) {
                if (scalar_product(vertices.get(polygons.get(polygonIndex).get(i)), pnormal) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) > scalar_product(ppoint, pnormal) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) + 0.00000001) {
                    uno = true;
                } else if (scalar_product(vertices.get(polygons.get(polygonIndex).get(i)), pnormal) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) < scalar_product(ppoint, pnormal) / Math.sqrt(Math.max(scalar_product(pnormal, pnormal), 1)) - 0.00000001) {
                    otro = true;
                }
                if (uno && otro) {
                    return true;
                }
            }
        }
        return false;
    }

    protected ArrayList<int[]> cutpolygon_nodes = new ArrayList<>(Arrays.asList(new int[][]{}));
    protected ArrayList<int[]> cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
    protected ArrayList<ArrayList<Integer>> last_cut_polygons = new ArrayList<>();

    /**
     * Removes the specified polygon from the {@link #polygons() polygons}
     * list, and adds one or two new polygons into it. One of new polygons is
     * obtained by intersecting the old polygon with the specified closed 
     * half-space, and the other one by intersecting it with the closure of that
     * half-space's complement. If one of these polygons is empty, only the
     * other one will be added.
     * <br>
     * If two new polygons have been generated, their common vertices will point
     * to the same object in the {@link #vertices() vertices} list, thus
     * becoming 'inseparable'.
     * 
     * @param ppoint An array containing the coordinates of a boundary point of
     * the half-space.
     * @param pnormal An array containing the coordinates of the normalvector of
     * the plane bounding the half-space.
     * @param polygonIndex The zero-base index at which the polygon to split is
     * located in the {@link #polygons() polygons} list.
     * @return {@code true} iff the polygon has been divided in two.
     */
    protected boolean cutPolygon(double[] ppoint, double[] pnormal, int polygonIndex) {

        if (isCut(ppoint, pnormal, polygonIndex)) {

            ArrayList<Integer> poligono1 = new ArrayList<>();
            ArrayList<Integer> poligono2 = new ArrayList<>();

            for (int i = 0; i < polygons.get(polygonIndex).size(); i++) {

                int j = (i + 1) % polygons.get(polygonIndex).size();
                if (point_on_plane(ppoint, pnormal, vertices.get(polygons.get(polygonIndex).get(i)))) {

                    poligono1.add(polygons.get(polygonIndex).get(i));
                    poligono2.add(polygons.get(polygonIndex).get(i));
                } else {

                    if (scalar_product(vertices.get(polygons.get(polygonIndex).get(i)), pnormal) > scalar_product(ppoint, pnormal)) {
                        poligono1.add(polygons.get(polygonIndex).get(i));
                    } else {
                        poligono2.add(polygons.get(polygonIndex).get(i));
                    }

                    if (plane_between_points(ppoint, pnormal, vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)))) {

                        freshcut:
                        {
                            for (int[] seccion : cutpolygon_nodes) {
                                if (seccion[0] == polygons.get(polygonIndex).get(i) && seccion[1] == polygons.get(polygonIndex).get(j)) {
                                    poligono1.add(seccion[2]);
                                    poligono2.add(seccion[2]);
                                    break freshcut;
                                } else if (seccion[0] == polygons.get(polygonIndex).get(j) && seccion[1] == polygons.get(polygonIndex).get(i)) {
                                    poligono1.add(seccion[2]);
                                    poligono2.add(seccion[2]);
                                    break freshcut;
                                }
                            }
                            double D = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];

                            double[] direccionVector = vector(vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)));
                            double X = vertices.get(polygons.get(polygonIndex).get(i))[0];
                            double Y = vertices.get(polygons.get(polygonIndex).get(i))[1];
                            double Z = vertices.get(polygons.get(polygonIndex).get(i))[2];
                            double U = direccionVector[0];
                            double V = direccionVector[1];
                            double W = direccionVector[2];
                            double A = pnormal[0];
                            double B = pnormal[1];
                            double C = pnormal[2];
                            double t = -(A * X + B * Y + C * Z - D) / (A * U + B * V + C * W);

                            double[] seccion = new double[]{X + t * U, Y + t * V, Z + t * W};
                            addVertex(seccion);

                            double peso1 = vector_length(vector(seccion, vertices.get(polygons.get(polygonIndex).get(j))));
                            double peso2 = vector_length(vector(seccion, vertices.get(polygons.get(polygonIndex).get(i))));
                            add2dVertex(new double[]{
                                (vertices2d.get(polygons.get(polygonIndex).get(i))[0] * peso1 + vertices2d.get(polygons.get(polygonIndex).get(j))[0] * peso2) / (peso1 + peso2),
                                (vertices2d.get(polygons.get(polygonIndex).get(i))[1] * peso1 + vertices2d.get(polygons.get(polygonIndex).get(j))[1] * peso2) / (peso1 + peso2),
                                0
                            });

                            poligono1.add(vertices_size - 1);
                            poligono2.add(vertices_size - 1);
                            cutpolygon_nodes.add(new int[]{polygons.get(polygonIndex).get(i), polygons.get(polygonIndex).get(j), vertices_size - 1});
                        }
                    }
                }
            }

            cutpolygon_pairs.add(new int[]{polygonIndex, polygons.size()});
            last_cut_polygons.add(polygons.get(polygonIndex));
            polygons.set(polygonIndex, poligono1);
            addPolygon(poligono2);
            return true;
        }
        return false;
    }

    public ArrayList<Integer> polygonSelect(double[] ppoint, double[] pnormal, int polygonIndex) {

        ArrayList<Integer> selecciones = new ArrayList<>(Arrays.asList(new Integer[]{}));
        selecciones.add(polygonIndex);
        for (int i = 0; i < selecciones.size(); i++) {

            int miembro = selecciones.get(i);
            for (int ii = 0; ii < polygons_size; ii++) {

                if (!selecciones.contains(ii)) {

                    for (int miembroSeccion : polygons.get(miembro)) {

                        if (polygons.get(ii).contains(miembroSeccion)) {
                            if (!point_on_plane(ppoint, pnormal, vertices.get(miembroSeccion))) {
                                selecciones.add(ii);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return selecciones;
    }

    public double[] polygonCenter(int polygonIndex) {

        double[] espalda = new double[]{Double.NaN, Double.NaN, Double.NaN};
        if (isNonDegenerate(polygonIndex)) {

            espalda = new double[]{0d, 0d, 0d};
            for (int ind : polygons.get(polygonIndex)) {
                espalda = new double[]{espalda[0] + vertices.get(ind)[0], espalda[1] + vertices.get(ind)[1], espalda[2] + vertices.get(ind)[2]};
            }

            java.util.Random desplazamiento = new java.util.Random(polygonIndex);
            espalda = new double[]{espalda[0] / polygons.get(polygonIndex).size() + desplazamiento.nextDouble() * 10 - 5, espalda[1] / polygons.get(polygonIndex).size() + desplazamiento.nextDouble() * 10 - 5, espalda[2] / polygons.get(polygonIndex).size() + desplazamiento.nextDouble() * 10 - 5};
        }
        return espalda;
    }

    /**
     * Performs a {@link #cutPolygon(double[], double[], int) cutPolygon} with
     * the specified plane on every polygon's index in this origami's {@link
     * #polygons() polygons}, and reflects some of the {@link #vertices()
     * vertices} over the plane. Every vertex that is on the same side of the
     * plane as where the specified normal vector is pointing to will be
     * reflected over the plane.
     * 
     * @param ppoint An array containing the 3-dimensional coordinates of a
     * point the plane goes through as {@code double}s.
     * @param pnormal An array containing the 3-dimensional coordinates the
     * plane's normalvector as {@code double}s.
     */
    protected void internalReflectionFold(double[] ppoint, double[] pnormal) {

        shrink(); //encoger

        cutpolygon_nodes = new ArrayList<>(Arrays.asList(new int[][]{}));
        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();
        int numeroPagina = polygons_size;
        for (int i = 0; i < numeroPagina; i++) {
            if (isNonDegenerate(i)) {
                cutPolygon(ppoint, pnormal, i);
            }
        }

        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        for (int i = 0; i < vertices_size; i++) {

            double[] puntoI = vertices.get(i);
            if (puntoI[0] * pnormal[0] + puntoI[1] * pnormal[1] + puntoI[2] * pnormal[2] - constante > 0) {

                double[] direccionVector = pnormal;
                double X = puntoI[0];
                double Y = puntoI[1];
                double Z = puntoI[2];
                double U = direccionVector[0];
                double V = direccionVector[1];
                double W = direccionVector[2];
                double A = pnormal[0];
                double B = pnormal[1];
                double C = pnormal[2];
                double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);

                double[] puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
                double[] imagen = new double[]{puntoPie[0] + vector(puntoPie, puntoI)[0], puntoPie[1] + vector(puntoPie, puntoI)[1], puntoPie[2] + vector(puntoPie, puntoI)[2]};
                vertices.set(i, imagen);
            }
        }
    }

    /**
     * Performs a {@link #cutPolygon(double[], double[], int) cutPolygon} with
     * the specified plane on every polygon's index in this origami's {@link
     * #polygons() polygons}, and if the intersection of the plane and the
     * origami is a non-degenerate line, rotates some of the {@link #vertices()
     * vertices} around that line by the specified angle. Otherwise, it calls
     * {@link #undo(int) undo}{@code (1)} to reset this origami to its last
     * valid state, but only if the specified angle is not zero.
     * <br>
     * In the first case, every vertex that is on the same side of the plane as
     * where the specified normal vector is pointing to will be rotated around
     * the line. As there is no well-defined 'clockwise' direction in a
     * 3-dimensional space, the rotation's direction will be decided on a whim.
     * 
     * @param ppoint An array containing the 3-dimensional coordinates of a
     * point the plane goes through as {@code double}s.
     * @param pnormal An array containing the 3-dimensional coordinates the
     * plane's normalvector as {@code double}s.
     * @param phi The angle of the rotation.
     * @return 0 if the rotation has been performed; 1 if it has not.
     */
    protected int internalRotationFold(double[] ppoint, double[] pnormal, int phi) {

        shrink();

        cutpolygon_nodes = new ArrayList<>(Arrays.asList(new int[][]{}));
        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();
        int numeroPagina = polygons_size;
        for (int i = 0; i < numeroPagina; i++) {
            if (isNonDegenerate(i)) {
                cutPolygon(ppoint, pnormal, i);
            }
        }

        ArrayList<Integer> puntosEngranaje = new ArrayList<>(Arrays.asList(new Integer[]{}));
        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];

        for (int i = 0; i < vertices_size; i++) {

            double[] puntoI = vertices.get(i);
            if (point_on_plane(ppoint, pnormal, puntoI)) {
                puntosEngranaje.add(i);
            }
        }

        boolean collin = false;
        int otroPunto = -1;
        double distanciaMaxima = -1;

        if (puntosEngranaje.size() >= 2) {
            for (int hp : puntosEngranaje) {
                if (vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0)))) > 0) {
                    collin = true;
                    if (vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0)))) > distanciaMaxima) {
                        otroPunto = hp;
                        distanciaMaxima = vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0))));
                    }
                }
            }
        }

        for (int i = 1; i < puntosEngranaje.size() && i != otroPunto; i++) {

            if (vector_length(vector_product(vector(vertices.get(puntosEngranaje.get(0)), vertices.get(puntosEngranaje.get(i))), vector(vertices.get(otroPunto), vertices.get(puntosEngranaje.get(i))))) > vector_length(vector(vertices.get(puntosEngranaje.get(0)), vertices.get(otroPunto)))) {
            	collin = false;
                break;
            }
        }

        if (collin) {

            double[] direccionVector = vector(vertices.get(puntosEngranaje.get(0)), vertices.get(otroPunto));
            double Cx = direccionVector[0] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double Cy = direccionVector[1] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double Cz = direccionVector[2] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double sinphi = Math.sin((double) phi * Math.PI / 180);
            double cosphi = Math.cos((double) phi * Math.PI / 180);

            for (int i = 0; i < vertices_size; i++) {

                double[] puntoI = vertices.get(i);
                if (puntoI[0] * pnormal[0] + puntoI[1] * pnormal[1] + puntoI[2] * pnormal[2] - constante > 0) {

                    double X = puntoI[0] - vertices.get(puntosEngranaje.get(0))[0];
                    double Y = puntoI[1] - vertices.get(puntosEngranaje.get(0))[1];
                    double Z = puntoI[2] - vertices.get(puntosEngranaje.get(0))[2];

                    double imagenX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
                    double imagenY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
                    double imagenZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

                    double[] imagen = new double[]{imagenX + vertices.get(puntosEngranaje.get(0))[0], imagenY + vertices.get(puntosEngranaje.get(0))[1], imagenZ + vertices.get(puntosEngranaje.get(0))[2]};
                    vertices.set(i, imagen);
                }
            }
            return 0;
        } else if (phi != 0) {
            undo(1);
            return 1;
        } else {
            return 1;
        }
    }

    /**
     * Performs a {@link #cutPolygon(double[], double[], int) cutPolygon} with
     * the specified plane on every polygon's index in this origami's {@link
     * #polygons() polygons}, and reflects some of the {@link #vertices()
     * vertices} over the plane. The elements of the {@link #polygons()
     * polygons} list whose zero-based indices appear in the
     * {@link Origami#polygonSelect(double[], double[], int) polygonSelect} of
     * the specified plane and polygon index will have all their vertices
     * reflected over the plane.
     * 
     * @param ppoint An array containing the 3-dimensional coordinates of a
     * point the plane goes through as {@code double}s.
     * @param pnormal An array containing the 3-dimensional coordinates the
     * plane's normalvector as {@code double}s.
     * @param polygonIndex The index of the polygon to include in
     * {@link Origami#polygonSelect(double[], double[], int) polygonSelect}.
     */
    protected void internalReflectionFold(double[] ppoint, double[] pnormal, int polygonIndex) {

        ArrayList<Integer> selecciones = polygonSelect(ppoint, pnormal, polygonIndex);

        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        for (int i = 0; i < vertices_size; i++) {

            for (int miembro : selecciones) {

                if (polygons.get(miembro).contains(i)) {

                    double[] puntoI = vertices.get(i);

                    double[] direccionVector = pnormal;
                    double X = puntoI[0];
                    double Y = puntoI[1];
                    double Z = puntoI[2];
                    double U = direccionVector[0];
                    double V = direccionVector[1];
                    double W = direccionVector[2];
                    double A = pnormal[0];
                    double B = pnormal[1];
                    double C = pnormal[2];
                    double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);

                    double[] puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
                    double[] imagen = new double[]{puntoPie[0] + vector(puntoPie, puntoI)[0], puntoPie[1] + vector(puntoPie, puntoI)[1], puntoPie[2] + vector(puntoPie, puntoI)[2]};
                    vertices.set(i, imagen);
                    break;
                }
            }
        }

        for (int i = 0; i < cutpolygon_pairs.size(); i++) {

            if (!(selecciones.contains(cutpolygon_pairs.get(i)[0]) || selecciones.contains(cutpolygon_pairs.get(i)[1]))) {
                polygons.set(cutpolygon_pairs.get(i)[0], last_cut_polygons.get(i));
            }
        }

        for (int[] par : cutpolygon_pairs) {

            if (!(selecciones.contains(par[0]) || selecciones.contains(par[1]))) {
                polygons.set(par[1], new ArrayList<Integer>());
            }
        }

        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();

        shrink(polygonIndex);
    }

    protected void internalRotationFold(double[] ppoint, double[] pnormal, int phi, int polygonIndex) {

        ArrayList<Integer> selecciones = polygonSelect(ppoint, pnormal, polygonIndex);

        ArrayList<Integer> puntosEngranaje = new ArrayList<>(Arrays.asList(new Integer[]{}));

        for (int i = 0; i < vertices_size; i++) {

            double[] puntoI = vertices.get(i);

            if (point_on_plane(ppoint, pnormal, puntoI)) {
                for (int miembro : selecciones) {
                    if (polygons.get(miembro).contains(i)) {
                        puntosEngranaje.add(i);
                        break;
                    }
                }
            }
        }

        boolean collin = false;
        int otroPunto = -1;
        double distanciaMaxima = -1;

        if (puntosEngranaje.size() >= 2) {
            for (int hp : puntosEngranaje) {
                if (vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0)))) > 0) {
                    collin = true;
                    if (vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0)))) > distanciaMaxima) {
                        otroPunto = hp;
                        distanciaMaxima = vector_length(vector(vertices.get(hp), vertices.get(puntosEngranaje.get(0))));
                    }
                }
            }
        }

        for (int i = 1; i < puntosEngranaje.size() && i != otroPunto; i++) {

            if (vector_length(vector_product(vector(vertices.get(puntosEngranaje.get(0)), vertices.get(puntosEngranaje.get(i))), vector(vertices.get(otroPunto), vertices.get(puntosEngranaje.get(i))))) > vector_length(vector(vertices.get(puntosEngranaje.get(0)), vertices.get(otroPunto)))) {
            	collin = false;
                break;
            }
        }

        if (collin) {

            double[] direccionVector = vector(vertices.get(puntosEngranaje.get(0)), vertices.get(otroPunto));
            double Cx = direccionVector[0] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double Cy = direccionVector[1] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double Cz = direccionVector[2] / Math.pow(direccionVector[0] * direccionVector[0] + direccionVector[1] * direccionVector[1] + direccionVector[2] * direccionVector[2], 0.5);
            double sinphi = Math.sin((double) phi * Math.PI / 180);
            double cosphi = Math.cos((double) phi * Math.PI / 180);

            for (int i = 0; i < vertices_size; i++) {

                for (int miembro : selecciones) {

                    if (polygons.get(miembro).contains(i)) {

                        double[] puntoI = vertices.get(i);

                        double X = puntoI[0] - vertices.get(puntosEngranaje.get(0))[0];
                        double Y = puntoI[1] - vertices.get(puntosEngranaje.get(0))[1];
                        double Z = puntoI[2] - vertices.get(puntosEngranaje.get(0))[2];

                        double imagenX = X * (cosphi + Cx * Cx * (1 - cosphi)) + Y * (Cx * Cy * (1 - cosphi) - Cz * sinphi) + Z * (Cx * Cz * (1 - cosphi) + Cy * sinphi);
                        double imagenY = X * (Cy * Cx * (1 - cosphi) + Cz * sinphi) + Y * (cosphi + Cy * Cy * (1 - cosphi)) + Z * (Cy * Cz * (1 - cosphi) - Cx * sinphi);
                        double imagenZ = X * (Cz * Cx * (1 - cosphi) - Cy * sinphi) + Y * (Cz * Cy * (1 - cosphi) + Cx * sinphi) + Z * (cosphi + Cz * Cz * (1 - cosphi));

                        double[] imagen = new double[]{imagenX + vertices.get(puntosEngranaje.get(0))[0], imagenY + vertices.get(puntosEngranaje.get(0))[1], imagenZ + vertices.get(puntosEngranaje.get(0))[2]};
                        vertices.set(i, imagen);
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < cutpolygon_pairs.size(); i++) {

            if (!(selecciones.contains(cutpolygon_pairs.get(i)[0]) || selecciones.contains(cutpolygon_pairs.get(i)[1]))) {
                polygons.set(cutpolygon_pairs.get(i)[0], last_cut_polygons.get(i));
            }

        }

        for (int[] par : cutpolygon_pairs) {

            if (!(selecciones.contains(par[0]) || selecciones.contains(par[1]))) {
                polygons.set(par[1], new ArrayList<Integer>());
            }
        }

        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();

        shrink(polygonIndex);
    }
    
    protected void internalMutilation(double[] ppoint, double[] pnormal) {
        
        shrink();

        cutpolygon_nodes = new ArrayList<>(Arrays.asList(new int[][]{}));
        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();
        int pnum = polygons_size;
        for (int i = 0; i < pnum; i++) {
            if (isNonDegenerate(i)) {
                cutPolygon(ppoint, pnormal, i);
            }
        }
        
        double constante = scalar_product(ppoint, pnormal);
        for (int i=0; i<polygons_size; i++) {
            for (int armadura : polygons.get(i)) {
                if (scalar_product(vertices.get(armadura), pnormal) > constante && !point_on_plane(ppoint, pnormal, vertices.get(armadura))) {
                    
                    polygons.set(i, new ArrayList<Integer>());
                    break;
                }
            }
        }
    }
    
    protected void internalMutilation(double[] ppoint, double[] pnormal, int polygonIndex) {
        
        ArrayList<Integer> selection = polygonSelect(ppoint, pnormal, polygonIndex);
        double constante = scalar_product(ppoint, pnormal);
        for (int i : selection) {
            ArrayList<Integer> poly = polygons.get(i);
            for (int vert : poly) {
                if (scalar_product(vertices.get(vert), pnormal) > constante) {

                    polygons.set(i, new ArrayList<Integer>());
                    break;
                }
            }
        }
        
        for (int i = 0; i < cutpolygon_pairs.size(); i++) {

            if (!(selection.contains(cutpolygon_pairs.get(i)[0]) || selection.contains(cutpolygon_pairs.get(i)[1]))) {
                polygons.set(cutpolygon_pairs.get(i)[0], last_cut_polygons.get(i));
            }
        }

        for (int[] pair : cutpolygon_pairs) {

            if (!(selection.contains(pair[0]) || selection.contains(pair[1]))) {
                polygons.set(pair[1], new ArrayList<Integer>());
            }
        }

        cutpolygon_pairs = new ArrayList<>(Arrays.asList(new int[][]{}));
        last_cut_polygons = new ArrayList<>();

        shrink(polygonIndex);
    }

    public void reflectionFold(double[] ppoint, double[] pnormal) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            1,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2]
        });
        history_pointer++;
        //horpasztás
        internalReflectionFold(ppoint, pnormal);
    }

    public int rotationFold(double[] ppoint, double[] pnormal, int phi) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            2,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2],
            (double) phi
        });
        history_pointer++;
        //disparar
        return internalRotationFold(ppoint, pnormal, phi);
    }

    public void reflectionFold(double[] ppoint, double[] pnormal, int polygonIndex) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            3,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2],
            (double) polygonIndex
        });
        history_pointer++;
        //horpasztás
        internalReflectionFold(ppoint, pnormal, polygonIndex);
    }

    public void rotationFold(double[] ppoint, double[] pnormal, int phi, int polygonIndex) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            4,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2],
            (double) phi,
            (double) polygonIndex
        });
        history_pointer++;
        //disparar
        internalRotationFold(ppoint, pnormal, phi, polygonIndex);
    }

    //crease = pliegue
    public void crease(double[] ppoint, double[] pnormal) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            5,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2]
        });
        history_pointer++;
        //disparar
        internalRotationFold(ppoint, pnormal, 0);
    }
    
    public void mutilation(double[] ppoint, double[] pnormal) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            6,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2]
        });
        history_pointer++;
        //disparar
        internalMutilation(ppoint, pnormal);
    }
    
    public void mutilation(double[] ppoint, double[] pnormal, int polygonIndex) {

        //tala
        history.subList(history_pointer, history.size()).clear();
        double[] p1 = planarPointRound(ppoint, pnormal);
        double[] n1 = normalvectorRound(ppoint, pnormal);
        ppoint = p1;
        pnormal = n1;
        history.add(new double[]{
            7,
            ppoint[0],
            ppoint[1],
            ppoint[2],
            pnormal[0],
            pnormal[1],
            pnormal[2],
            (double) polygonIndex
        });
        history_pointer++;
        //disparar
        internalMutilation(ppoint, pnormal, polygonIndex);
    }

    /**
     * Initializes the {@link #corners() corners}, {@link #vertices() vertices},
     * {@link #vertices2d() vertices2d} and {@link #polygons() polygons} lists
     * of this origami depending on the value of its {@link #papertype()
     * papertype}. The {@link #corners() corners}, {@link #vertices() vertices},
     * and {@link #vertices2d() vertices2d} lists will always have the same
     * initial vertices in the same order.
     */
    @SuppressWarnings("unchecked")
    public final void reset() {

        if (papertype == PaperType.A4) {

            vertices_size = 0;
            vertices.clear();
            addVertex(0, 0, 0);
            addVertex(424.3, 0, 0);
            addVertex(424.3, 300, 0);
            addVertex(0, 300, 0);
            vertices2d = (ArrayList<double[]>) vertices.clone();
            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            poligono0.add(0);
            poligono0.add(1);
            poligono0.add(2);
            poligono0.add(3);
            addPolygon(poligono0);
            corners = (ArrayList<double[]>) vertices.clone();
        }

        if (papertype == PaperType.Square) {

            vertices_size = 0;
            vertices.clear();
            addVertex(0, 0, 0);
            addVertex(400, 0, 0);
            addVertex(400, 400, 0);
            addVertex(0, 400, 0);
            vertices2d = (ArrayList<double[]>) vertices.clone();
            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            poligono0.add(0);
            poligono0.add(1);
            poligono0.add(2);
            poligono0.add(3);
            addPolygon(poligono0);
            corners = (ArrayList<double[]>) vertices.clone();
        }

        if (papertype == PaperType.Hexagon) {

            vertices_size = 0;
            vertices.clear();
            addVertex(300, 346.41, 0);
            addVertex(400, 173.205, 0);
            addVertex(300, 0, 0);
            addVertex(100, 0, 0);
            addVertex(0, 173.205, 0);
            addVertex(100, 346.41, 0);
            vertices2d = (ArrayList<double[]>) vertices.clone();
            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            poligono0.add(5);
            poligono0.add(4);
            poligono0.add(3);
            poligono0.add(2);
            poligono0.add(1);
            poligono0.add(0);
            addPolygon(poligono0);
            corners = (ArrayList<double[]>) vertices.clone();
        }

        if (papertype == PaperType.Dollar) {

            vertices_size = 0;
            vertices.clear();
            addVertex(0, 0, 0);
            addVertex(400, 0, 0);
            addVertex(400, 170, 0);
            addVertex(0, 170, 0);
            vertices2d = (ArrayList<double[]>) vertices.clone();
            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            poligono0.add(0);
            poligono0.add(1);
            poligono0.add(2);
            poligono0.add(3);
            addPolygon(poligono0);
            corners = (ArrayList<double[]>) vertices.clone();
        }

        if (papertype == PaperType.Forint) {

            vertices_size = 0;
            vertices.clear();
            addVertex(0, 0, 0);
            addVertex(400, 0, 0);
            addVertex(400, 181.82, 0);
            addVertex(0, 181.82, 0);
            vertices2d = (ArrayList<double[]>) vertices.clone();
            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            poligono0.add(0);
            poligono0.add(1);
            poligono0.add(2);
            poligono0.add(3);
            addPolygon(poligono0);
            corners = (ArrayList<double[]>) vertices.clone();
        }

        if (papertype == PaperType.Custom) {

            vertices_size = 0;
            vertices.clear();
            for (double[] pont : corners) {
                addVertex(pont[0], pont[1], 0);
            }
            vertices2d = (ArrayList<double[]>) vertices.clone();

            polygons_size = 0;
            polygons.clear();
            ArrayList<Integer> poligono0 = new ArrayList<>();
            for (int i = 0; i < vertices_size; i++) {
                poligono0.add(i);
            }
            addPolygon(poligono0);
        }
    }

    /**
     * Végrehajtja a this origami {@link #history}-jában szereplô összes
     * mûveletet index szerint növekvô sorrendben.
     * <p> Mivel a mûveleteket this origami aktuális állapotán végzi el,
     * általában egy {@linkplain #reset()} hívás elôzi meg.
     */
    public void execute() {

        for (int i=0; i<history_pointer; i++) {

            double[] comando = history.get(i);
            if (comando[0] == 1) {
                internalReflectionFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]});
            } else if (comando[0] == 2) {
                internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
            } else if (comando[0] == 3) {
                internalReflectionFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
            } else if (comando[0] == 4) {
                internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]), (int) (comando[8]));
            } else if (comando[0] == 5) {
                internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, 0);
            } else if (comando[0] == 6) {
                internalMutilation(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]});
            } else if (comando[0] == 7) {
                internalMutilation(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
            }
        }
    }

    /**
     * Végrehajt a megadott indextôl kezdve a megadott darabszámú, this origami
     * {@link #history}-jában egymást követô mûveletet this origamin index
     * szerint növekvô sorrendben.
     *
     * @param index Az elsô végrehajtandó mûvelet {@link history}-beli 0-alapú
     * indexe. Negatív érték esetén csak a mûveletek nemnegatív indexû része
     * lesz elvégezve.
     * @param steps A végrehajtandó mûveletek száma.
     * @throws Exception if (index+db &gt; history.size())
     */
    public void execute(int index, int steps) throws Exception {
        
        if (index + steps <= history.size()) {
            for (int i = index; i < index + steps && i >= 0; i++) {
                double[] comando = history.get(i);

                if (comando[0] == 1) {
                    internalReflectionFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]});
                } else if (comando[0] == 2) {
                    internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
                } else if (comando[0] == 3) {
                    internalReflectionFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
                } else if (comando[0] == 4) {
                    internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]), (int) (comando[8]));
                } else if (comando[0] == 5) {
                    internalRotationFold(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, 0);
                } else if (comando[0] == 6) {
                    internalMutilation(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]});
                } else if (comando[0] == 7) {
                    internalMutilation(new double[]{comando[1], comando[2], comando[3]}, new double[]{comando[4], comando[5], comando[6]}, (int) (comando[7]));
                }
            }
        } else {

            throw new Exception("Parameter must be smaller than history.size()");
        }
    }

    /**
     * Visszavonja az utoljára végrehajtott mûveletet this origamiban azáltal,
     * hogy törli {@link #history} utolsó elemét, majd meghívja a
     * {@linkplain #reset()} és {@linkplain #execute()} metódusokat.
     *
     * @since 2013-09-05
     */
    public void undo() {

        if (history_pointer > 0) {
            
            history_pointer--;
            while (0 < history_pointer ? history.get(history_pointer - 1)[0] == 5d : false) {
                history_pointer--;
            }
            reset();
            execute();
        }
    }

    /**
     * undo a paraméternek megfelelô számú mûveletet this origamiban
     * azáltal, hogy törli {@link #history} annyi utolsó elemét, majd meghívja a
     * {@linkplain #reset()} és {@linkplain #execute()} metódusokat.
     *
     * @param steps A visszavonandó mûveletek száma.
     * @since 2013-09-05
     */
    public void undo(int steps) {

        if (history_pointer >= steps) {
            
            history_pointer -= steps;
            reset();
            execute();
        }
    }

    public void redo() {

        if (history.size() > history_pointer) {

            history_pointer++;
            while(history.get(history_pointer - 1)[0] == 5d) {
                history_pointer++;
            }
            reset();
            execute();
        }
    }
    
    public void redo(int steps) {
    	
    	if (history_pointer + steps <= history.size()) {
            
            history_pointer += steps;
            reset();
            execute();
        }
    }

    public void redoAll() {

        if (history.size() > history_pointer) {

            history_pointer = history.size();
            reset();
            execute();
        }
    }

    /**
     * Pushes the empty polygons of this origami as close to the end of the
     * {@link #polygons() polygons} list as possible without moving the polygon
     * at the specified index.
     * 
     * @param polygonIndex The zero-based index at which the polygon in the
     * {@link #polygons() polygons} list should not be moved.
     * @since 2013-09-04
     */
    protected void shrink(int polygonIndex) {

        ArrayList<Integer> tmp = polygons.get(polygonIndex);
        removePolygon(polygonIndex);
        for (int i = 0; i < polygons_size; i++) {
            if (polygons.get(i) == new ArrayList<Integer>()
                    || polygons.get(i).isEmpty()) {

                removePolygon(i);
                i--;
            }
        }

        while (polygonIndex > polygons_size) {

            addPolygon(new ArrayList<Integer>());
        }
        polygons.add(polygonIndex, tmp);
        polygons_size++;
    }

    /**
     * Removes every empty list from the {@link #polygons() polygons} of this
     * origami.
     *
     * @since 2013-09-04
     */
    protected void shrink() {

        for (int i = 0; i < polygons_size; i++) {
            if (polygons.get(i) == new ArrayList<Integer>()
                    || polygons.get(i).isEmpty()) {

                removePolygon(i);
                i--;
            }
        }
    }

    /**
     * Arranges the planar points in the specified list in a counter-clockwise
     * winding order as viewed from their center.
     *
     * @param polygon An {@link ArrayList} whose each element is an array
     * containing the 2-dimensional coordinates of a point.
     * @return An {@link ArrayList} containing the same elements as {@code
     * polygon}, but in a counter-clockwise winding order.
     * @since 2013-10-11
     */
    static private ArrayList<double[]> ccwWindingOrder(ArrayList<double[]> polygon) {

        ArrayList<double[]> orden = new ArrayList<>(Arrays.asList(new double[][]{}));
        ArrayList<Double> angulo = new ArrayList<>();
        angulo.add(0d);

        if (polygon.size() > 0) {

            double[] nucleo = new double[]{0, 0};

            for (double[] pont : polygon) {

                nucleo = new double[]{
                    nucleo[0] + pont[0] / polygon.size(),
                    nucleo[1] + pont[1] / polygon.size()
                };
            }

            for (int i = 1; i < polygon.size(); i++) {

                angulo.add(angle(vector(polygon.get(i), nucleo),
                        vector(polygon.get(0), nucleo)));
            }

            while (orden.size() < polygon.size()) {

                double anguloMinimo = -1.0;
                double[] areaMinima = nullvektor;
                int mindex = -1;

                for (int i = 0; i < angulo.size(); i++) {

                    if ((angulo.get(i) < anguloMinimo || anguloMinimo == -1.0) && angulo.get(i) != -1.0) {

                        anguloMinimo = angulo.get(i);
                        areaMinima = polygon.get(i);
                        mindex = i;
                    }
                }

                orden.add(new double[]{areaMinima[0], areaMinima[1]});
                angulo.set(mindex, -1.0);
            }
        }
        return orden;
    }

    /**
     * Returns {@code true} iff the planar points in the specified list are the
     * vertices of a convex polygon listed in a counter-clockwise winding order.
     * 
     * @param polygon An {@link ArrayList} whose each element is an array
     * containing the 2-dimensional coordinates of a point.
     * @return As described in the above.
     * @since 2013-10-12
     */
    static private boolean isConvex(ArrayList<double[]> polygon) {

        if (polygon.size() > 3) {

            if (angle(vector(polygon.get(polygon.size() - 1), polygon.get(0)),
                    vector(polygon.get(1), polygon.get(0)))
                    > Math.PI) {
                return false;
            }

            for (int i = 1; i < polygon.size() - 1; i++) {

                if (angle(vector(polygon.get(i - 1), polygon.get(i)),
                        vector(polygon.get(i + 1), polygon.get(i)))
                        > Math.PI) {
                    return false;
                }
            }

            if (angle(vector(polygon.get(polygon.size() - 2), polygon.get(polygon.size() - 1)),
                    vector(polygon.get(0), polygon.get(polygon.size() - 1)))
                    > Math.PI) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the size of the smallest orthogonal square all the {@link
     * #corners() corners} of this origami can fit in.
     * 
     * @return As described in the above.
     * @since 2013-10-31
     */
    public double circumscribedSquareSize() {
        return Math.max(paperWidth(), paperHeight());
    }

    /**
     * Returns the difference of the largest and the smallest first coordinate
     * occuring within the {@link #corners() corners} list.
     * 
     * @return As described in the above.
     */
    public double paperWidth() {

        Double derecho = null, izquierda = null;
        for (double[] pont : corners) {

            izquierda = izquierda == null ? pont[0] : (izquierda > pont[0] ? pont[0] : izquierda);
            derecho = derecho == null ? pont[0] : (derecho < pont[0] ? pont[0] : derecho);
        }
        if (derecho == null) {
            derecho = 0d;
        }
        if (izquierda == null) {
            izquierda = 0d;
        }
        return derecho - izquierda;
    }

    /**
     * Returns the difference of the largest and the smallest second coordinate
     * occuring within the {@link #corners() corners} list.
     * 
     * @return As described in the above.
     */
    public double paperHeight() {

        Double menor = null, mayor = null;
        for (double[] pont : corners) {

            menor = menor == null ? pont[1] : (menor > pont[1] ? pont[1] : menor);
            mayor = mayor == null ? pont[1] : (mayor < pont[1] ? pont[1] : mayor);
        }
        if (menor == null) {
            menor = 0d;
        }
        if (mayor == null) {
            mayor = 0d;
        }
        return mayor - menor;
    }
    
    final static private double[][] Origins = new double[][]{
        new double[]{0, 0, 0},
        new double[]{400, 0, 0},
        new double[]{0, 400, 0},
        new double[]{0, 0, 400}
    };
    
    /**
     * Compresses the equation of the specified plane. After this compression,
     * the plane's equation will fit in 12 bytes and be ready to be stored in
     * ORI format without further loss of precision.
     *
     * @param ppoint An array containing the 3-dimensional coordinates of a
     * point the plane goes through as {@code double}s.
     * @param pnormal An array containing the 3-dimensional coordinates the
     * plane's normalvector as {@code double}s.
     * @return An array containing the 3-dimensional coordinates of a point the
     * compressed plane goes through.
     * @see OrigamiIO#write_gen2(Origami, String)
     */
    static protected double[] planarPointRound(double[] ppoint, double[] pnormal) {
        
        double distanciaMaxima = -1;
        int origoUtilizado = 0;
        double[] puntoNivelNV = new double[]{0, 0, 0};
        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        for (int ii = 0; ii < Origins.length; ii++) {
            double[] direccionVector = pnormal;
            double X = Origins[ii][0];
            double Y = Origins[ii][1];
            double Z = Origins[ii][2];
            double U = direccionVector[0];
            double V = direccionVector[1];
            double W = direccionVector[2];
            double A = pnormal[0];
            double B = pnormal[1];
            double C = pnormal[2];
            double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);
            double[] talppont = new double[]{X + t * U, Y + t * V, Z + t * W};
            if (Origami.vector_length(Origami.vector(talppont, Origins[ii])) > distanciaMaxima) {
                puntoNivelNV = Origami.vector(talppont, Origins[ii]);
                distanciaMaxima = Origami.vector_length(puntoNivelNV);
                origoUtilizado = ii;
            }
        }
        int Xe = (int) puntoNivelNV[0];
        int Ye = (int) puntoNivelNV[1];
        int Ze = (int) puntoNivelNV[2];
        int Xt = (int) Math.round((Math.abs(puntoNivelNV[0] - Xe)) * 256 * 256);
        int Yt = (int) Math.round((Math.abs(puntoNivelNV[1] - Ye)) * 256 * 256);
        int Zt = (int) Math.round((Math.abs(puntoNivelNV[2] - Ze)) * 256 * 256);
        return new double[]{(double) Xe + Math.signum(Xe) * Xt / 256 / 256 + Origins[origoUtilizado][0], (double) Ye + Math.signum(Ye) * Yt / 256 / 256 + Origins[origoUtilizado][1], (double) Ze + Math.signum(Ze) * Zt / 256 / 256 + Origins[origoUtilizado][2]};
    }

    /**
     * Compresses the equation of the specified plane. After this compression,
     * the plane's equation will fit in 12 bytes and be ready to be stored in
     * ORI format without further loss of precision.
     *
     * @param ppoint An array containing the 3-dimensional coordinates of a
     * point the plane goes through as {@code double}s.
     * @param pnormal An array containing the 3-dimensional coordinates the
     * plane's normalvector as {@code double}s.
     * @return An array containing the 3-dimensional coordinates of the
     * compressed plane's normalvector.
     * @see OrigamiIO#write_gen2(Origami, String)
     */
    static protected double[] normalvectorRound(double[] ppoint, double[] pnormal) {
        
        double distanciaMaxima = -1;
        double[] puntoNivelNV = new double[]{0, 0, 0};
        double constante = ppoint[0] * pnormal[0] + ppoint[1] * pnormal[1] + ppoint[2] * pnormal[2];
        for (double[] origo : Origins) {
            double[] direccionVector = pnormal;
            double X = origo[0];
            double Y = origo[1];
            double Z = origo[2];
            double U = direccionVector[0];
            double V = direccionVector[1];
            double W = direccionVector[2];
            double A = pnormal[0];
            double B = pnormal[1];
            double C = pnormal[2];
            double t = -(A * X + B * Y + C * Z - constante) / (A * U + B * V + C * W);
            double[] puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
            if (Origami.vector_length(Origami.vector(puntoPie, origo)) > distanciaMaxima) {
                puntoNivelNV = Origami.vector(puntoPie, origo);
                distanciaMaxima = Origami.vector_length(puntoNivelNV);
            }
        }
        double sale = 1;
        if (Origami.scalar_product(pnormal, puntoNivelNV) < 0) {
            sale = -1;
        }
        int Xe = (int) puntoNivelNV[0];
        int Ye = (int) puntoNivelNV[1];
        int Ze = (int) puntoNivelNV[2];
        int Xt = (int) Math.round((Math.abs(puntoNivelNV[0] - Xe)) * 256 * 256);
        int Yt = (int) Math.round((Math.abs(puntoNivelNV[1] - Ye)) * 256 * 256);
        int Zt = (int) Math.round((Math.abs(puntoNivelNV[2] - Ze)) * 256 * 256);
        return new double[]{sale * ((double) Xe + Math.signum(Xe) * Xt / 256 / 256), sale * ((double) Ye + Math.signum(Ye) * Yt / 256 / 256), sale * ((double) Ze + Math.signum(Ze) * Zt / 256 / 256)};
    }
    
    public ArrayList<double[]> foldingLine (double[] ppoint, double[] pnormal) {
        
        double[] ppoint1 = planarPointRound(ppoint, pnormal);
        double[] pnormal1 = normalvectorRound(ppoint, pnormal);
        ArrayList<double[]> line = new ArrayList<>();
        for (int polygonIndex = 0; polygonIndex < polygons_size; polygonIndex++) {
            
            if (isCut(ppoint1, pnormal1, polygonIndex)) {

                double[] start = null, end = null;
                for (int i = 0; i < polygons.get(polygonIndex).size(); i++) {

                    int j = (i + 1) % polygons.get(polygonIndex).size();
                    if (point_on_plane(ppoint1, pnormal1, vertices.get(polygons.get(polygonIndex).get(i)))) {

                        end = start;
                        start = vertices.get(polygons.get(polygonIndex).get(i));
                    } else {

                        if (plane_between_points(ppoint1, pnormal1, vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)))) {

                            double D = ppoint1[0] * pnormal1[0] + ppoint1[1] * pnormal1[1] + ppoint1[2] * pnormal1[2];

                            double[] iranyvek = vector(vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)));
                            double X = vertices.get(polygons.get(polygonIndex).get(i))[0];
                            double Y = vertices.get(polygons.get(polygonIndex).get(i))[1];
                            double Z = vertices.get(polygons.get(polygonIndex).get(i))[2];
                            double U = iranyvek[0];
                            double V = iranyvek[1];
                            double W = iranyvek[2];
                            double A = pnormal1[0];
                            double B = pnormal1[1];
                            double C = pnormal1[2];
                            double t = -(A * X + B * Y + C * Z - D) / (A * U + B * V + C * W);

                            end = start;
                            start = new double[]{X + t * U, Y + t * V, Z + t * W};
                        }
                    }
                }
                line.add(start);
                line.add(end);
            }
        }
        return line;
    }
    
    public ArrayList<double[]> foldingLine2d (double[] ppoint, double[] pnormal) {
        
        double[] ppoint1 = planarPointRound(ppoint, pnormal);
        double[] pnormal1 = normalvectorRound(ppoint, pnormal);
        ArrayList<double[]> line = new ArrayList<>();
        for (int polygonIndex = 0; polygonIndex < polygons_size; polygonIndex++) {
            
            if (isCut(ppoint1, pnormal1, polygonIndex)) {

                double[] start = null, end = null;
                for (int i = 0; i < polygons.get(polygonIndex).size(); i++) {

                    int j = (i + 1) % polygons.get(polygonIndex).size();
                    if (point_on_plane(ppoint1, pnormal1, vertices.get(polygons.get(polygonIndex).get(i)))) {

                        end = start;
                        start = vertices2d.get(polygons.get(polygonIndex).get(i));
                    } else {

                        if (plane_between_points(ppoint1, pnormal1, vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)))) {

                            double D = ppoint1[0] * pnormal1[0] + ppoint1[1] * pnormal1[1] + ppoint1[2] * pnormal1[2];

                            double[] iranyvek = vector(vertices.get(polygons.get(polygonIndex).get(i)), vertices.get(polygons.get(polygonIndex).get(j)));
                            double X = vertices.get(polygons.get(polygonIndex).get(i))[0];
                            double Y = vertices.get(polygons.get(polygonIndex).get(i))[1];
                            double Z = vertices.get(polygons.get(polygonIndex).get(i))[2];
                            double U = iranyvek[0];
                            double V = iranyvek[1];
                            double W = iranyvek[2];
                            double A = pnormal1[0];
                            double B = pnormal1[1];
                            double C = pnormal1[2];
                            double t = -(A * X + B * Y + C * Z - D) / (A * U + B * V + C * W);

                            double[] metszet = new double[]{X + t * U, Y + t * V, Z + t * W};

                            double suly1 = vector_length(vector(metszet, vertices.get(polygons.get(polygonIndex).get(j))));
                            double suly2 = vector_length(vector(metszet, vertices.get(polygons.get(polygonIndex).get(i))));
                            end = start;
                            start = new double[]{
                                (vertices2d.get(polygons.get(polygonIndex).get(i))[0] * suly1 + vertices2d.get(polygons.get(polygonIndex).get(j))[0] * suly2) / (suly1 + suly2),
                                (vertices2d.get(polygons.get(polygonIndex).get(i))[1] * suly1 + vertices2d.get(polygons.get(polygonIndex).get(j))[1] * suly2) / (suly1 + suly2),
                                0
                            };
                        }
                    }
                }
                line.add(start);
                line.add(end);
            }
        }
        return line;
    }
    
    public int complexity(int step) {
        
        Origami origami = clone();
        if (step > origami.history_pointer) {
            return 0;
        }
        if (origami.history.get(step)[0] == 1) {
            
            origami.undo(origami.history_pointer-step);
            origami.redo(1);
            ArrayList<int[]> pairs = (ArrayList<int[]>)origami.cutpolygon_pairs.clone();
            origami.undo(1);
            int maxcompl = 0;
            
            while (!pairs.isEmpty()) {
                
                ArrayList<int[]> pairs_local = new ArrayList<>();
                pairs_local.add(pairs.remove(0));
                for (int i=0; i<pairs_local.size(); i++) {
                    for (int ii=0; ii<pairs.size(); ii++) {
                        
                        for (int vert : origami.polygons.get(pairs.get(ii)[0])) {
                            if (origami.polygons.get(pairs_local.get(i)[0]).contains(vert)) {
                                
                                pairs_local.add(pairs.remove(ii));
                                ii--;
                                break;
                            }
                        }
                    }
                }
                if (pairs_local.size() - 1 > maxcompl) {
                    maxcompl = pairs_local.size() - 1;
                }
            }
            
            return maxcompl;
        }
        if (origami.history.get(step)[0] == 3) {
            
            origami.undo(origami.history_pointer-step+1);
            origami.redo(1);
            
            double[] point = new double[] {
                origami.history().get(step)[1],
                origami.history().get(step)[2],
                origami.history().get(step)[3]
            };
            double[] normal = new double[] {
                origami.history().get(step)[4],
                origami.history().get(step)[5],
                origami.history().get(step)[6]
            };
            int index = (int)origami.history().get(step)[7];
            
            ArrayList<int[]> pairs = (ArrayList<int[]>)origami.cutpolygon_pairs.clone();
            ArrayList<Integer> selection = origami.polygonSelect(point, normal, index);
            int compl = 0;
            
            for (int[] pair : pairs) {
                if (selection.contains(pair[0]) || selection.contains(pair[1])) {
                    compl ++;
                }
            }
            
            return compl > 0 ? compl - 1 : 0;
        }
        return 0;
    }
    
    public int difficulty() {
        
        Origami origami = clone();
        origami.redoAll();

        int sum = 0;
        for (int i=0; i<origami.history().size(); i++) {
            
            sum += origami.complexity(i);
        }
        return sum;
    }
    
    static public int difficultyLevel(int difficulty) {
        
        if (difficulty == 0) return 0;
        if (difficulty <= 50) return 1;
        if (difficulty <= 100) return 2;
        if (difficulty <= 200) return 3;
        if (difficulty <= 400) return 4;
        if (difficulty <= 800) return 5;
        return 6;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Origami clone() {

        Origami copy = new Origami(papertype);
        copy.corners = (ArrayList<double[]>) corners.clone();
        copy.history = (ArrayList<double[]>) history.clone();
        copy.history_pointer = history_pointer;
        copy.vertices_size = vertices_size;
        copy.vertices = (ArrayList<double[]>) vertices.clone();
        copy.vertices2d = (ArrayList<double[]>) vertices2d.clone();
        copy.polygons_size = polygons_size;
        copy.polygons = (ArrayList<ArrayList<Integer>>) polygons.clone();
        copy.last_cut_polygons = (ArrayList<ArrayList<Integer>>) last_cut_polygons.clone();
        copy.cutpolygon_nodes = (ArrayList<int[]>) cutpolygon_nodes.clone();
        copy.cutpolygon_pairs = (ArrayList<int[]>) cutpolygon_pairs.clone();
        return copy;
    }
}