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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Attila Bágyoni (ba-sz-at@users.sourceforge.net)
 * @since 2013-01-14
 */
public class OrigamiIO {

    final static private double[][] Origins = new double[][]{
        new double[]{0, 0, 0},
        new double[]{400, 0, 0},
        new double[]{0, 400, 0},
        new double[]{0, 0, 400}
    };

    static public void write_gen2(Origami origami, String filename) throws Exception {

        if (!(origami instanceof OrigamiGen2)) {
            write_gen1(origami, filename);
            return;
        }
        try {

            File ori = new File(filename + "~");
            if (ori.exists()) {
                ori.delete();
            }
            //OE3D
            try (FileOutputStream str = new FileOutputStream(ori)) {
                //OE3D
                str.write(0x4f);
                str.write(0x45);
                str.write(0x33);
                str.write(0x44);
                
                //version 3, compressed
                str.write(3);
                str.write(0x63);
                //paper type
                str.write((int) origami.papertype().toChar());
                //corners
                if (origami.papertype() == Origami.PaperType.Custom) {
                    
                    str.write(origami.corners().size());
                    for (int i = 0; i < origami.corners().size(); i++) {
                        
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 24);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 16);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 8);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]));
                        
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 24);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 16);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 8);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]));
                    }
                } else {
                    str.write(0);
                }
                
                //command blocks
                for (int i = 0; i < origami.history_pointer(); i++) {
                    
                    int anguloInclinacion = 0;
                    if (origami.history.get(i)[0] == 2.0) {
                        
                        anguloInclinacion += (int) origami.history.get(i)[7];
                        while (i < origami.history.size() - 1) {
                            
                            if (origami.history.get(i + 1)[0] == 2.0
                                    && origami.history.get(i + 1)[1] == origami.history.get(i)[1]
                                    && origami.history.get(i + 1)[2] == origami.history.get(i)[2]
                                    && origami.history.get(i + 1)[3] == origami.history.get(i)[3]
                                    && origami.history.get(i + 1)[4] == origami.history.get(i)[4]
                                    && origami.history.get(i + 1)[5] == origami.history.get(i)[5]
                                    && origami.history.get(i + 1)[6] == origami.history.get(i)[6]) {
                                i++;
                                anguloInclinacion += (int) origami.history.get(i)[7];
                            } else {
                                break;
                            }
                        }
                    } else if (origami.history.get(i)[0] == 4.0) {
                        
                        anguloInclinacion += (int) origami.history.get(i)[7];
                        while (i < origami.history.size() - 1) {
                            
                            if (origami.history.get(i + 1)[0] == 4.0
                                    && origami.history.get(i + 1)[1] == origami.history.get(i)[1]
                                    && origami.history.get(i + 1)[2] == origami.history.get(i)[2]
                                    && origami.history.get(i + 1)[3] == origami.history.get(i)[3]
                                    && origami.history.get(i + 1)[4] == origami.history.get(i)[4]
                                    && origami.history.get(i + 1)[5] == origami.history.get(i)[5]
                                    && origami.history.get(i + 1)[6] == origami.history.get(i)[6]
                                    && origami.history.get(i + 1)[8] == origami.history.get(i)[8]) {
                                i++;
                                anguloInclinacion += (int) origami.history.get(i)[7];
                            } else {
                                break;
                            }
                        }
                    }
                    
                    double[] puntoPlana = new double[]{origami.history.get(i)[1], origami.history.get(i)[2], origami.history.get(i)[3]};
                    double[] siknv = new double[]{origami.history.get(i)[4], origami.history.get(i)[5], origami.history.get(i)[6]};
                    
                    double distacnciaMaxima = -1;
                    int origoUtilizado = 0; //Origo viene de Origami
                    int terfekUsada = 0;
                    double[] sikpontnv = new double[]{0, 0, 0};
                    double arte = puntoPlana[0] * siknv[0] + puntoPlana[1] * siknv[1] + puntoPlana[2] * siknv[2];
                    
                    for (int ii = 0; ii < Origins.length; ii++) {
                        
                        double[] iranyvek = siknv;
                        double X = Origins[ii][0];
                        double Y = Origins[ii][1];
                        double Z = Origins[ii][2];
                        double U = iranyvek[0];
                        double V = iranyvek[1];
                        double W = iranyvek[2];
                        double A = siknv[0];
                        double B = siknv[1];
                        double C = siknv[2];
                        double t = -(A * X + B * Y + C * Z - arte) / (A * U + B * V + C * W);
                        
                        double[] puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
                        if (Origami.vector_length(Origami.vector(puntoPie, Origins[ii])) > distacnciaMaxima) {
                            
                            sikpontnv = Origami.vector(puntoPie, Origins[ii]);
                            distacnciaMaxima = Origami.vector_length(sikpontnv);
                            origoUtilizado = ii;
                        }
                    }
                    
                    //inner: 1, outer: 0
                    if (Origami.scalar_product(siknv, sikpontnv) < 0) {
                        terfekUsada = 1;
                    }
                    
                    int comando = 0;
                    int nucleo = 65535;
                    
                    switch ((int) origami.history.get(i)[0]) {
                        
                        case 1:
                            comando = 1;
                            break;
                            
                        case 2:
                            while (anguloInclinacion < 0) {
                                anguloInclinacion += 360;
                            }
                            anguloInclinacion %= 360;
                            if (anguloInclinacion <= 180) {
                                comando = 2;
                            } else {
                                
                                comando = 3;
                                anguloInclinacion = 360 - anguloInclinacion;
                            }
                            break;
                            
                        case 3:
                            comando = 4;
                            nucleo = (int) origami.history.get(i)[7];
                            break;
                            
                        case 4:
                            while (anguloInclinacion < 0) {
                                anguloInclinacion += 360;
                            }
                            anguloInclinacion %= 360;
                            if (anguloInclinacion <= 180) {
                                comando = 5;
                            } else {
                                
                                comando = 6;
                                anguloInclinacion = 360 - anguloInclinacion;
                            }
                            nucleo = (int) origami.history.get(i)[8];
                            break;
                            
                        case 5:
                            comando = 7;
                            break;
                            
                        case 6:
                            comando = 0;
                            break;
                            
                        case 7:
                            comando = 0;
                            nucleo = (int) origami.history.get(i)[7];
                            break;
                    }
                    
                    int Xe = (int) sikpontnv[0];
                    int Ye = (int) sikpontnv[1];
                    int Ze = (int) sikpontnv[2];
                    
                    int Xt = (int) Math.round((Math.abs(sikpontnv[0] - Xe)) * 256 * 256);
                    int Yt = (int) Math.round((Math.abs(sikpontnv[1] - Ye)) * 256 * 256);
                    int Zt = (int) Math.round((Math.abs(sikpontnv[2] - Ze)) * 256 * 256);
                    
                    //header
                    str.write(terfekUsada * 32 + origoUtilizado * 8 + comando);
                    str.write(anguloInclinacion);
                    str.write(nucleo >>> 8);
                    str.write(nucleo);
                    
                    //body
                    str.write(Xe >>> 8);
                    str.write(Xe);
                    str.write(Xt >>> 8);
                    str.write(Xt);
                    
                    str.write(Ye >>> 8);
                    str.write(Ye);
                    str.write(Yt >>> 8);
                    str.write(Yt);
                    
                    str.write(Ze >>> 8);
                    str.write(Ze);
                    str.write(Zt >>> 8);
                    str.write(Zt);
                }
                
                //EOF
                str.write(0x0A);
                str.write(0x45);
                str.write(0x4f);
                str.write(0x46);
            }
            origamieditor3d.compression.LZW.compress(new File(filename + "~"), new File(filename));
            ori.delete();

        } catch (Exception ex) {

            throw OrigamiException.H002;
        }
    }

    static public void write_gen1(Origami origami, String filename) throws Exception {

        try {

            File ori = new File(filename + "~");
            if (ori.exists()) {
                ori.delete();
            }
            //OE3D
            try (FileOutputStream str = new FileOutputStream(ori)) {
                //OE3D
                str.write(0x4f);
                str.write(0x45);
                str.write(0x33);
                str.write(0x44);
                
                //version 2, compressed
                str.write(2);
                str.write(0x63);
                //paper type
                str.write((int) origami.papertype().toChar());
                //corners
                if (origami.papertype() == Origami.PaperType.Custom) {
                    
                    str.write(origami.corners().size());
                    for (int i = 0; i < origami.corners().size(); i++) {
                        
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 24);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 16);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]) >>> 8);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[0]));
                        
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 24);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 16);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]) >>> 8);
                        str.write(Float.floatToIntBits((float) origami.corners().get(i)[1]));
                    }
                } else {
                    str.write(0);
                }
                
                //command blocks
                for (int i = 0; i < origami.history_pointer(); i++) {
                    
                    int anguloInclinacion = 0;
                    if (origami.history.get(i)[0] == 2.0) {
                        
                        anguloInclinacion += (int) origami.history.get(i)[7];
                        while (i < origami.history.size() - 1) {
                            
                            if (origami.history.get(i + 1)[0] == 2.0
                                    && origami.history.get(i + 1)[1] == origami.history.get(i)[1]
                                    && origami.history.get(i + 1)[2] == origami.history.get(i)[2]
                                    && origami.history.get(i + 1)[3] == origami.history.get(i)[3]
                                    && origami.history.get(i + 1)[4] == origami.history.get(i)[4]
                                    && origami.history.get(i + 1)[5] == origami.history.get(i)[5]
                                    && origami.history.get(i + 1)[6] == origami.history.get(i)[6]) {
                                i++;
                                anguloInclinacion += (int) origami.history.get(i)[7];
                            } else {
                                break;
                            }
                        }
                    } else if (origami.history.get(i)[0] == 4.0) {
                        
                        anguloInclinacion += (int) origami.history.get(i)[7];
                        while (i < origami.history.size() - 1) {
                            
                            if (origami.history.get(i + 1)[0] == 4.0
                                    && origami.history.get(i + 1)[1] == origami.history.get(i)[1]
                                    && origami.history.get(i + 1)[2] == origami.history.get(i)[2]
                                    && origami.history.get(i + 1)[3] == origami.history.get(i)[3]
                                    && origami.history.get(i + 1)[4] == origami.history.get(i)[4]
                                    && origami.history.get(i + 1)[5] == origami.history.get(i)[5]
                                    && origami.history.get(i + 1)[6] == origami.history.get(i)[6]
                                    && origami.history.get(i + 1)[8] == origami.history.get(i)[8]) {
                                i++;
                                anguloInclinacion += (int) origami.history.get(i)[7];
                            } else {
                                break;
                            }
                        }
                    }
                    
                    double[] sikpont = new double[]{origami.history.get(i)[1], origami.history.get(i)[2], origami.history.get(i)[3]};
                    double[] siknv = new double[]{origami.history.get(i)[4], origami.history.get(i)[5], origami.history.get(i)[6]};
                    
                    double distanciaMaxima = -1;
                    int origoUtilizado = 0;
                    int terfelUsada = 0;
                    double[] sikpontnv = new double[]{0, 0, 0};
                    double arte = sikpont[0] * siknv[0] + sikpont[1] * siknv[1] + sikpont[2] * siknv[2];
                    
                    for (int ii = 0; ii < Origins.length; ii++) {
                        
                        double[] iranyvek = siknv;
                        double X = Origins[ii][0];
                        double Y = Origins[ii][1];
                        double Z = Origins[ii][2];
                        double U = iranyvek[0];
                        double V = iranyvek[1];
                        double W = iranyvek[2];
                        double A = siknv[0];
                        double B = siknv[1];
                        double C = siknv[2];
                        double t = -(A * X + B * Y + C * Z - arte) / (A * U + B * V + C * W);
                        
                        double[] puntoPie = new double[]{X + t * U, Y + t * V, Z + t * W};
                        if (Origami.vector_length(Origami.vector(puntoPie, Origins[ii])) > distanciaMaxima) {
                            
                            sikpontnv = Origami.vector(puntoPie, Origins[ii]);
                            distanciaMaxima = Origami.vector_length(sikpontnv);
                            origoUtilizado = ii;
                        }
                    }
                    
                    //inner: 1, outer: 0
                    if (Origami.scalar_product(siknv, sikpontnv) < 0) {
                        terfelUsada = 1;
                    }
                    
                    int comando = 0;
                    int nucleo = 65535;
                    
                    switch ((int) origami.history.get(i)[0]) {
                        
                        case 1:
                            comando = 1;
                            break;
                            
                        case 2:
                            while (anguloInclinacion < 0) {
                                anguloInclinacion += 360;
                            }
                            anguloInclinacion %= 360;
                            if (anguloInclinacion <= 180) {
                                comando = 2;
                            } else {
                                
                                comando = 3;
                                anguloInclinacion = 360 - anguloInclinacion;
                            }
                            break;
                            
                        case 3:
                            comando = 4;
                            nucleo = (int) origami.history.get(i)[7];
                            break;
                            
                        case 4:
                            while (anguloInclinacion < 0) {
                                anguloInclinacion += 360;
                            }
                            anguloInclinacion %= 360;
                            if (anguloInclinacion <= 180) {
                                comando = 5;
                            } else {
                                
                                comando = 6;
                                anguloInclinacion = 360 - anguloInclinacion;
                            }
                            nucleo = (int) origami.history.get(i)[8];
                            break;
                            
                        case 5:
                            comando = 7;
                            break;
                            
                        case 6:
                            comando = 0;
                            break;
                            
                        case 7:
                            comando = 0;
                            nucleo = (int) origami.history.get(i)[7];
                            break;
                    }
                    
                    int Xe = (int) sikpontnv[0];
                    int Ye = (int) sikpontnv[1];
                    int Ze = (int) sikpontnv[2];
                    
                    int Xt = (int) Math.round((Math.abs(sikpontnv[0] - Xe)) * 256 * 256);
                    int Yt = (int) Math.round((Math.abs(sikpontnv[1] - Ye)) * 256 * 256);
                    int Zt = (int) Math.round((Math.abs(sikpontnv[2] - Ze)) * 256 * 256);
                    
                    //header
                    str.write(terfelUsada * 32 + origoUtilizado * 8 + comando);
                    str.write(anguloInclinacion);
                    str.write(nucleo >>> 8);
                    str.write(nucleo);
                    
                    //body
                    str.write(Xe >>> 8);
                    str.write(Xe);
                    str.write(Xt >>> 8);
                    str.write(Xt);
                    
                    str.write(Ye >>> 8);
                    str.write(Ye);
                    str.write(Yt >>> 8);
                    str.write(Yt);
                    
                    str.write(Ze >>> 8);
                    str.write(Ze);
                    str.write(Zt >>> 8);
                    str.write(Zt);
                }
                
                //EOF
                str.write(0x0A);
                str.write(0x45);
                str.write(0x4f);
                str.write(0x46);
            }
            origamieditor3d.compression.LZW.compress(new File(filename + "~"), new File(filename));
            ori.delete();

        } catch (Exception ex) {

            throw OrigamiException.H002;
        }
    }

    static public Origami read_gen2(java.io.ByteArrayInputStream ori) throws Exception {

        try {

            OrigamiGen2 origami;
            ori.reset();
            java.io.InputStream str = origamieditor3d.compression.LZW.extract(ori);

            int membrete1 = str.read();
            membrete1 <<= 8;
            membrete1 += str.read();
            membrete1 <<= 8;
            membrete1 += str.read();
            membrete1 <<= 8;
            membrete1 += str.read();

            if (membrete1 != 0x4f453344) {

                str.close();
                throw OrigamiException.H005;
            } else {

                int membrete2 = str.read();
                membrete2 <<= 8;
                membrete2 += str.read();

                if (membrete2 != 0x0363) {

                    str.close();
                    return read_gen1(ori);
                } else {

                    int papel = str.read();

                    if (Origami.PaperType.forChar((char) papel) != Origami.PaperType.Custom) {

                        origami = new OrigamiGen2(Origami.PaperType.forChar((char) papel));
                        str.read();
                    } else {

                        ArrayList<double[]> esquinas = new ArrayList<>(Arrays.asList(new double[][]{}));
                        int esquina = str.read();

                        for (int i = 0; i < esquina; i++) {

                            int Xint = str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            float X = Float.intBitsToFloat(Xint);

                            int Yint = str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            float Y = Float.intBitsToFloat(Yint);

                            esquinas.add(new double[]{(double) X, (double) Y});
                        }

                        origami = new OrigamiGen2(esquinas);
                    }

                    int cabeceraComando = str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    while (cabeceraComando != 0x0A454f46) {

                        short Xint, Yint, Zint;
                        int Xfrac, Yfrac, Zfrac;

                        Xint = (short) str.read();
                        Xint <<= 8;
                        Xint += str.read();
                        Xfrac = str.read();
                        Xfrac <<= 8;
                        Xfrac += str.read();
                        double X = Xint + Math.signum(Xint) * (double) Xfrac / 256 / 256;

                        Yint = (short) str.read();
                        Yint <<= 8;
                        Yint += str.read();
                        Yfrac = str.read();
                        Yfrac <<= 8;
                        Yfrac += str.read();
                        double Y = Yint + Math.signum(Yint) * (double) Yfrac / 256 / 256;

                        Zint = (short) str.read();
                        Zint <<= 8;
                        Zint += str.read();
                        Zfrac = str.read();
                        Zfrac <<= 8;
                        Zfrac += str.read();
                        double Z = Zint + Math.signum(Zint) * (double) Zfrac / 256 / 256;

                        double[] sikpont = new double[3];
                        double[] siknv = new double[3];
                        sikpont[0] = (double) X + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][0];
                        sikpont[1] = (double) Y + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][1];
                        sikpont[2] = (double) Z + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][2];
                        siknv[0] = X;
                        siknv[1] = Y;
                        siknv[2] = Z;

                        //Elecciones de partido cuadrados
                        
                        if (((cabeceraComando >>> 24) - ((cabeceraComando >>> 24) % 32)) / 32 == 1) {

                            siknv = new double[]{-siknv[0], -siknv[1], -siknv[2]};
                        }

                        double[] comando;
                        if ((cabeceraComando >>> 24) % 8 == 1) {

                            //ref. fold
                            comando = new double[7];
                            comando[0] = 1;
                        } else if ((cabeceraComando >>> 24) % 8 == 2) {

                            //positive rot. fold
                            comando = new double[8];
                            comando[0] = 2;
                            comando[7] = (cabeceraComando >>> 16) % 256;
                        } else if ((cabeceraComando >>> 24) % 8 == 3) {

                            //negative rot. fold
                            comando = new double[8];
                            comando[0] = 2;
                            comando[7] = -(cabeceraComando >>> 16) % 256;
                        } else if ((cabeceraComando >>> 24) % 8 == 4) {

                            //partial ref. fold
                            comando = new double[8];
                            comando[0] = 3;
                            comando[7] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 5) {

                            //positive partial rot. fold
                            comando = new double[9];
                            comando[0] = 4;
                            comando[7] = (cabeceraComando >>> 16) % 256;
                            comando[8] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 6) {

                            //negative partial rot. fold
                            comando = new double[9];
                            comando[0] = 4;
                            comando[7] = (double) -(cabeceraComando >>> 16) % 256;
                            comando[8] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 7) {

                            //crease
                            comando = new double[7];
                            comando[0] = 5;
                        } else if (cabeceraComando % 65536 == 65535) {

                            //cut
                            comando = new double[7];
                            comando[0] = 6;
                        } else {

                            //partial cut
                            comando = new double[8];
                            comando[0] = 7;
                            comando[7] = (double) (cabeceraComando % 65536);
                        }

                        comando[1] = sikpont[0];
                        comando[2] = sikpont[1];
                        comando[3] = sikpont[2];
                        comando[4] = siknv[0];
                        comando[5] = siknv[1];
                        comando[6] = siknv[2];

                        origami.history.add(comando);

                        cabeceraComando = str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                    }
                    origami.redoAll();
                    str.close();
                    return origami;
                }
            }
        } catch (Exception ex) {
            throw OrigamiException.H005;
        }
    }

    static public Origami read_gen1(java.io.ByteArrayInputStream ori) throws Exception {

        try {

            Origami origami;
            ori.reset();
            java.io.InputStream str = origamieditor3d.compression.LZW.extract(ori);

            int membrete1 = str.read();
            membrete1 <<= 8;
            membrete1 += str.read();
            membrete1 <<= 8;
            membrete1 += str.read();
            membrete1 <<= 8;
            membrete1 += str.read();

            if (membrete1 != 0x4f453344) {

                str.close();
                throw OrigamiException.H005;
            } else {

                int membrete2 = str.read();
                membrete2 <<= 8;
                membrete2 += str.read();

                if (membrete2 != 0x0263) {

                    str.close();
                    throw OrigamiException.H005;
                } else {

                    int papel = str.read();

                    if (Origami.PaperType.forChar((char) papel) != Origami.PaperType.Custom) {

                        origami = new Origami(Origami.PaperType.forChar((char) papel));
                        str.read();
                    } else {

                        ArrayList<double[]> esquinas = new ArrayList<>(Arrays.asList(new double[][]{}));
                        int esquina = str.read();

                        for (int i = 0; i < esquina; i++) {

                            int Xint = str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            Xint <<= 8;
                            Xint += str.read();
                            float X = Float.intBitsToFloat(Xint);

                            int Yint = str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            Yint <<= 8;
                            Yint += str.read();
                            float Y = Float.intBitsToFloat(Yint);

                            esquinas.add(new double[]{(double) X, (double) Y});
                        }

                        origami = new Origami(esquinas);
                    }

                    int cabeceraComando = str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    cabeceraComando <<= 8;
                    cabeceraComando += str.read();
                    while (cabeceraComando != 0x0A454f46) {

                        short Xint, Yint, Zint;
                        int Xfrac, Yfrac, Zfrac;

                        Xint = (short) str.read();
                        Xint <<= 8;
                        Xint += str.read();
                        Xfrac = str.read();
                        Xfrac <<= 8;
                        Xfrac += str.read();
                        double X = Xint + Math.signum(Xint) * (double) Xfrac / 256 / 256;

                        Yint = (short) str.read();
                        Yint <<= 8;
                        Yint += str.read();
                        Yfrac = str.read();
                        Yfrac <<= 8;
                        Yfrac += str.read();
                        double Y = Yint + Math.signum(Yint) * (double) Yfrac / 256 / 256;

                        Zint = (short) str.read();
                        Zint <<= 8;
                        Zint += str.read();
                        Zfrac = str.read();
                        Zfrac <<= 8;
                        Zfrac += str.read();
                        double Z = Zint + Math.signum(Zint) * (double) Zfrac / 256 / 256;

                        double[] sikpont = new double[3];
                        double[] siknv = new double[3];
                        sikpont[0] = (double) X + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][0];
                        sikpont[1] = (double) Y + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][1];
                        sikpont[2] = (double) Z + Origins[(((cabeceraComando >>> 24) % 32) - ((cabeceraComando >>> 24) % 8)) / 8][2];
                        siknv[0] = X;
                        siknv[1] = Y;
                        siknv[2] = Z;

                        //Elecciones partido cuadrados
                        if (((cabeceraComando >>> 24) - ((cabeceraComando >>> 24) % 32)) / 32 == 1) {

                            siknv = new double[]{-siknv[0], -siknv[1], -siknv[2]};
                        }

                        double[] comando;
                        if ((cabeceraComando >>> 24) % 8 == 1) {

                            //ref. fold
                            comando = new double[7];
                            comando[0] = 1;
                        } else if ((cabeceraComando >>> 24) % 8 == 2) {

                            //positive rot. fold
                            comando = new double[8];
                            comando[0] = 2;
                            comando[7] = (cabeceraComando >>> 16) % 256;
                        } else if ((cabeceraComando >>> 24) % 8 == 3) {

                            //negative rot. fold
                            comando = new double[8];
                            comando[0] = 2;
                            comando[7] = -(cabeceraComando >>> 16) % 256;
                        } else if ((cabeceraComando >>> 24) % 8 == 4) {

                            //partial ref. fold
                            comando = new double[8];
                            comando[0] = 3;
                            comando[7] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 5) {

                            //positive partial rot. fold
                            comando = new double[9];
                            comando[0] = 4;
                            comando[7] = (cabeceraComando >>> 16) % 256;
                            comando[8] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 6) {

                            //negative partial rot. fold
                            comando = new double[9];
                            comando[0] = 4;
                            comando[7] = (double) -(cabeceraComando >>> 16) % 256;
                            comando[8] = (double) (cabeceraComando % 65536);
                        } else if ((cabeceraComando >>> 24) % 8 == 7) {

                            //crease
                            comando = new double[7];
                            comando[0] = 5;
                        } else if (cabeceraComando % 65536 == 65535) {

                            //cut
                            comando = new double[7];
                            comando[0] = 6;
                        } else {

                            //partial cut
                            comando = new double[8];
                            comando[0] = 7;
                            comando[7] = (double) (cabeceraComando % 65536);
                        }

                        comando[1] = sikpont[0];
                        comando[2] = sikpont[1];
                        comando[3] = sikpont[2];
                        comando[4] = siknv[0];
                        comando[5] = siknv[1];
                        comando[6] = siknv[2];

                        origami.history.add(comando);

                        cabeceraComando = str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                        cabeceraComando <<= 8;
                        cabeceraComando += str.read();
                    }
                    origami.redoAll();
                    str.close();
                    return origami;
                }
            }
        } catch (Exception ex) {
            throw OrigamiException.H005;
        }
    }
}
