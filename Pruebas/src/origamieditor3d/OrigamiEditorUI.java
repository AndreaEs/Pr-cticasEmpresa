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
package origamieditor3d;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import origamieditor3d.origami.Camera;
import origamieditor3d.origami.OrigamiScriptTerminal;
import origamieditor3d.origami.Origami;
import origamieditor3d.origami.OrigamiIO;
import origamieditor3d.resources.Dictionary;
import origamieditor3d.resources.BaseFolds;
import origamieditor3d.resources.Models;

public class OrigamiEditorUI extends javax.swing.JFrame {

    final static private long serialVersionUID = 1L;
    //final static private String Version = "1.2.3";
    private Integer mouseX, mouseY;
    private int scroll_angle;
    private Integer liner1X, liner1Y, liner2X, liner2Y;
    private OrigamiScriptTerminal terminal;
    private ControlState EditorState/*, SecondaryState*/;
    private boolean alignOn;
    private int alignment_radius;
    private boolean zoomOnScroll;
    private boolean alwaysInMiddle;
    //private boolean neusisOn;
    //private int foldNumber;
    final private String oPanel1_tip1 = Dictionary.getString("tooltip1");
    //final private String oPanel1_tip2 = Dictionary.getString("tooltip2");
    private String fileName;
    final private javax.swing.JFrame settings;
    private javax.swing.JFileChooser save;
    private javax.swing.JFileChooser open;
    private javax.swing.JFileChooser ctm_export;
    private javax.swing.JFileChooser pdf_export;
    private javax.swing.JFileChooser gif_export;
    private javax.swing.JFileChooser jar_export;
    private javax.swing.JFileChooser png_export;
    private javax.swing.JFileChooser texture_open;
    //private final java.awt.image.BufferedImage tex;
    private boolean saved;
    //private boolean changeListenerShutUp;
    final javax.swing.JPopupMenu foldingops;
    private int snap2, snap3, snap4;
    //private boolean targetOn;

    private enum ControlState {

        ESPERA, LOCKED, REGLA1, REGLA2, ANGULO,
        UNIRSE0, UNIRSE1, UNIRSE2, UNIRSE3, ANGULO_MONTAJE,
        AFFIN_ALTER, ANGULARBISECTOR
    }

    @SuppressWarnings("empty-statement")
    public OrigamiEditorUI() {

        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/icon.png")));
        
        /*
        Check new updates
        try {
            java.util.Scanner inf = new java.util.Scanner(
                    new java.net.URL("http://origamieditor3d.sourceforge.net/info.txt").openStream());
            String line;
            while (!(line = inf.nextLine().replace(" ", "")).startsWith("latest_version="));
            String ver = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
            if (!Version.equals(ver)) {
                Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
                if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("update"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == javax.swing.JOptionPane.YES_OPTION) {
                    inf.reset();
                    while (!(line = inf.nextLine().replace(" ", "")).startsWith("download_link="));
                    Desktop.getDesktop().browse(new java.net.URI(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))));
                    System.exit(0);
                }
            }
        } catch (IOException | HeadlessException | URISyntaxException ex) {
        }*/

        initComponents();

        final BaseFolds bases = new BaseFolds();
        final java.util.ArrayList<String> basenames = bases.names();

        for (int i = 0; i < basenames.size(); i++) {
            final int ind = i;
            final javax.swing.JMenuItem baseitem = new javax.swing.JMenuItem(Dictionary.getString(basenames.get(i)));
            baseitem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (!saved) {
                        Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
                        if (javax.swing.JOptionPane.showOptionDialog(OrigamiEditorUI.this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    fileName = null;
                    try (final java.io.InputStream fis = bases.getFile(basenames.get(ind))) {
                        java.util.ArrayList<Byte> bytesb = new java.util.ArrayList<>();
                        int fisbyte;
                        while ((fisbyte = fis.read()) != -1) {
                            bytesb.add((byte) fisbyte);
                        }
                        byte[] bytes = new byte[bytesb.size()];
                        for (int i1 = 0; i1 < bytesb.size(); i1++) {
                            bytes[i1] = bytesb.get(i1);
                        }
                        terminal.TerminalOrigami = OrigamiIO.read_gen2(new java.io.ByteArrayInputStream(bytes));
                        terminal.historyReset();
                        oPanel1.update(terminal.TerminalOrigami);
                        oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
                        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                            oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
                        }
                        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                        }
                        if (alwaysInMiddle) {
                            oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                        }
                        oPanel1.PanelCamera.setOrthogonalView(0);
                        scroll_angle = 0;
                        defaultify();
                        saved = true;
                    }catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("h005"));
                    }
                    //foldNumber = terminal.TerminalOrigami.history_pointer();
                    //changeListenerShutUp = true;
                    //changeListenerShutUp = false;
                }
            });
            ui_file_new_bases.add(baseitem);
        }
        final Models models = new Models();
        final java.util.ArrayList<String> modnames = models.names();
        for (int i = 0; i < modnames.size(); i++) {
            final int ind = i;
            final javax.swing.JMenuItem modelitem = new javax.swing.JMenuItem(Dictionary.getString(modnames.get(i)));
            modelitem.addActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (!saved) {
                        Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
                        if (javax.swing.JOptionPane.showOptionDialog(OrigamiEditorUI.this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    fileName = null;
                    try (final java.io.InputStream fis = models.getFile(modnames.get(ind))) {
                        java.util.ArrayList<Byte> bytesb = new java.util.ArrayList<>();
                        int fisbyte;
                        while ((fisbyte = fis.read()) != -1) {
                            bytesb.add((byte) fisbyte);
                        }
                        byte[] bytes = new byte[bytesb.size()];
                        for (int i1 = 0; i1 < bytesb.size(); i1++) {
                            bytes[i1] = bytesb.get(i1);
                        }
                        terminal.TerminalOrigami = OrigamiIO.read_gen2(new java.io.ByteArrayInputStream(bytes));
                        terminal.historyReset();
                        oPanel1.update(terminal.TerminalOrigami);
                        oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
                        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                            oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
                        }
                        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                        }
                        if (alwaysInMiddle) {
                            oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                        }
                        oPanel1.PanelCamera.setOrthogonalView(0);
                        scroll_angle = 0;
                        defaultify();
                        saved = true;
                    }catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("h005"));
                    }
                    //foldNumber = terminal.TerminalOrigami.history_pointer();
                    //changeListenerShutUp = true;
                    //changeListenerShutUp = false;
                }
            });
            ui_file_sample.add(modelitem);
        }

        ui_file.setText(Dictionary.getString("file"));
//        ui_file_new.setText(Dictionary.getString("new"));
//        ui_file_new_square.setText(Dictionary.getString("newsquare"));
//        ui_file_new_a4.setText(Dictionary.getString("newa4"));
//        ui_file_new_hexagonal.setText(Dictionary.getString("newhex"));
//        ui_file_new_dollar.setText(Dictionary.getString("newdollar"));
//        ui_file_new_bases.setText(Dictionary.getString("bases"));
        ui_file_sample.setText(Dictionary.getString("samples"));
        ui_file_open.setText(Dictionary.getString("open"));
        ui_file_save.setText(Dictionary.getString("save"));
        ui_file_saveas.setText(Dictionary.getString("saveas"));
//        ui_file_export.setText(Dictionary.getString("export"));
//        ui_file_export_topdf.setText(Dictionary.getString("exportpdf"));
//        ui_file_export_toopenctm.setText(Dictionary.getString("exportctm"));
//        ui_file_export_togif.setText(Dictionary.getString("exportgif"));
//        ui_file_export_togif_revolving.setText(Dictionary.getString("revolving-gif"));
//        ui_file_export_togif_folding.setText(Dictionary.getString("folding-gif"));
//        ui_file_export_toself.setText(Dictionary.getString("exportjar"));
//        ui_file_export_crease.setText(Dictionary.getString("exportpng"));
        ui_file_properties.setText(Dictionary.getString("properties"));
        ui_edit.setText(Dictionary.getString("edit"));
        ui_edit_undo.setText(Dictionary.getString("undo"));
        ui_edit_redo.setText(Dictionary.getString("redo"));
        ui_view.setText(Dictionary.getString("view"));
//        ui_view_paper.setText(Dictionary.getString("papertex"));
//        ui_view_paper_image.setText(Dictionary.getString("teximage"));
//        ui_view_paper_gradient.setText(Dictionary.getString("texgradient"));
//        ui_view_paper_plain.setText(Dictionary.getString("texplain"));
//        ui_view_paper_none.setText(Dictionary.getString("texnone"));
        ui_view_zoom.setText(Dictionary.getString("zoomonscroll"));
        ui_view_best.setText(Dictionary.getString("bestfit"));
        ui_view_options.setText(Dictionary.getString("options"));
        ui_help.setText(Dictionary.getString("help"));
        ui_help_about.setText(Dictionary.getString("about"));
        ui_tutorials.setText(Dictionary.getString("tutorials"));
        ui_tutorials_internet.setText(Dictionary.getString("tutorial_videos"));
        jTabbedPane1.setTitleAt(0, Dictionary.getString("editor"));
        oPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(Dictionary.getString("3dview")));

        setTitle("Origami Editor 3D");
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (!saved) {
                    Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
                    if (javax.swing.JOptionPane.showOptionDialog(OrigamiEditorUI.this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                settings.dispose();
                OrigamiEditorUI.this.dispose();
                System.exit(0);
            }
        });
        saved = true;
        mouseX = null;
        mouseY = null;
        scroll_angle = 0;
        liner1X = null;
        liner1Y = null;
        liner2X = null;
        liner2Y = null;
        EditorState = (/*SecondaryState = */ControlState.ESPERA);
        alignment_radius = 100;
        zoomOnScroll = true;
        alwaysInMiddle = true;
        //neusisOn = false;
        terminal = new OrigamiScriptTerminal(OrigamiScriptTerminal.Access.USER);
        oPanel1.setFrontColor(Camera.paper_front_color);
        oPanel1.PanelCamera.xshift = oPanel1.getWidth() / 2;
        oPanel1.PanelCamera.yshift = oPanel1.getHeight() / 2;
        try {

            terminal.execute("version 1");
            terminal.execute("paper square new");
        } catch (Exception exc) {
        }
        oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
        oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());


        oPanel1.update(terminal.TerminalOrigami);
        oPanel1.repaint();

        javax.swing.ToolTipManager.sharedInstance().setInitialDelay(0);
        javax.swing.ToolTipManager.sharedInstance().setDismissDelay(1000);
        oPanel1.setToolTipText(oPanel1_tip1);
        

        oPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        this.setMinimumSize(new java.awt.Dimension(200, 200));

        fileName = null;

        //color chooser init
        final javax.swing.JColorChooser paletta = new javax.swing.JColorChooser(new java.awt.Color(Camera.paper_front_color));
        //options init
        settings = new javax.swing.JFrame(Dictionary.getString("options"));
        settings.setIconImage(getIconImage());
        java.awt.GridBagConstraints c;
        settings.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        settings.getContentPane().setLayout(new java.awt.GridBagLayout());
        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.NORTH;
        settings.getContentPane().add(new javax.swing.JLabel(Dictionary.getString("radius")), c);
        final javax.swing.JSlider alineacionTrepador = new javax.swing.JSlider();
        alineacionTrepador.setMinimum(5);
        alineacionTrepador.setMaximum(20);
        alineacionTrepador.setValue((int) Math.sqrt(alignment_radius));
        alineacionTrepador.setLabelTable(alineacionTrepador.createStandardLabels(15));
        alineacionTrepador.setPaintLabels(true);
        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.CENTER;
        settings.getContentPane().add(alineacionTrepador, c);
        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        javax.swing.JLabel etiqueta2 = new javax.swing.JLabel(Dictionary.getString("papercolor"));
        settings.getContentPane().add(etiqueta2, c);
        paletta.setPreviewPanel(new javax.swing.JPanel());
        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.gridheight = 2;
        settings.getContentPane().add(paletta, c);
        javax.swing.JButton ok = new javax.swing.JButton();
        ok.setText("OK");
        ok.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                alignment_radius = alineacionTrepador.getValue() * alineacionTrepador.getValue();
                oPanel1.setFrontColor(paletta.getColor().getRGB());
                oPanel1.repaint();
                settings.dispose();
            }
        });
        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        settings.getContentPane().add(ok, c);
        settings.setMinimumSize(
                new java.awt.Dimension(
                        paletta.getMinimumSize().width,
                        paletta.getMinimumSize().height + alineacionTrepador.getMinimumSize().height + ok.getMinimumSize().height + etiqueta2.getMinimumSize().height + 30));
        settings.setResizable(false);
        settings.setLocationRelativeTo(null);
        settings.pack();

        try {
            //texture dialog init
            texture_open = new javax.swing.JFileChooser();
            texture_open.setAcceptAllFileFilterUsed(false);
            texture_open.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("img"), javax.imageio.ImageIO.getReaderFormatNames()));
            //open dialog init
            open = new javax.swing.JFileChooser();
            open.setAcceptAllFileFilterUsed(false);
            open.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("ori"), "ori"));
            open.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("txt"), "txt"));
            //save dialog init
            save = new javax.swing.JFileChooser();
            save.setAcceptAllFileFilterUsed(false);
            save.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("ori"), "ori"));
            //ctm dialog init
            ctm_export = new javax.swing.JFileChooser();
            ctm_export.setAcceptAllFileFilterUsed(false);
            ctm_export.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("ctm"), "ctm"));
            //pdf dialog init
            pdf_export = new javax.swing.JFileChooser();
            pdf_export.setAcceptAllFileFilterUsed(false);
            pdf_export.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("pdf"), "pdf"));
            //gif dialog init
            gif_export = new javax.swing.JFileChooser();
            gif_export.setAcceptAllFileFilterUsed(false);
            gif_export.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("gif"), "gif"));
            //jar dialog init
            jar_export = new javax.swing.JFileChooser();
            jar_export.setAcceptAllFileFilterUsed(false);
            jar_export.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("ori.jar"), "ori.jar"));
            //png dialog init
            png_export = new javax.swing.JFileChooser();
            png_export.setAcceptAllFileFilterUsed(false);
            png_export.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(Dictionary.getString("png"), "png"));

        } catch (Exception ex) {
            ui_file_open.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_save.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_saveas.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_view_paper_image.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_export_toopenctm.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_export_topdf.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_export_togif_revolving.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_export_togif_folding.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
            ui_file_export_crease.addActionListener((ActionEvent e) -> {
                javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("sandbox"));
            });
        }
        //texture init
        //tex = null;
        
        //foldNumber = 0;
        
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                super.componentResized(e);
            }
        });
        
        //changeListenerShutUp = false;
        foldingops = new javax.swing.JPopupMenu();
        final javax.swing.JMenuItem reflect = new javax.swing.JMenuItem(Dictionary.getString("reflect"));
        reflect.addActionListener((java.awt.event.ActionEvent evt) -> {
            foldingops_reflect_actionPerformed(evt);
        });
        final javax.swing.JMenuItem rotate = new javax.swing.JMenuItem(Dictionary.getString("rotate"));
        rotate.addActionListener((java.awt.event.ActionEvent evt) -> {
            foldingops_rotate_actionPerformed(evt);
        });
        final javax.swing.JMenuItem cut = new javax.swing.JMenuItem(Dictionary.getString("cut"));
        cut.addActionListener((java.awt.event.ActionEvent evt) -> {
            foldingops_cut_actionPerformed(evt);
        });
        foldingops.add(reflect);
        foldingops.add(rotate);
        foldingops.add(cut);

        
        //targetOn = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        oPanel1 = new origamieditor3d.OrigamiPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        ui_file = new javax.swing.JMenu();
        ui_file_new = new javax.swing.JMenu();
        ui_file_new_square = new javax.swing.JMenuItem();
        ui_file_new_a4 = new javax.swing.JMenuItem();
        ui_file_new_hexagonal = new javax.swing.JMenuItem();
        ui_file_new_dollar = new javax.swing.JMenuItem();
        ui_file_new_bases = new javax.swing.JMenu();
        ui_file_sample = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ui_file_open = new javax.swing.JMenuItem();
        ui_file_save = new javax.swing.JMenuItem();
        ui_file_saveas = new javax.swing.JMenuItem();
        ui_file_export = new javax.swing.JMenu();
        ui_file_export_topdf = new javax.swing.JMenuItem();
        ui_file_export_toopenctm = new javax.swing.JMenuItem();
        ui_file_export_togif = new javax.swing.JMenu();
        ui_file_export_togif_revolving = new javax.swing.JMenuItem();
        ui_file_export_togif_folding = new javax.swing.JMenuItem();
        ui_file_export_toself = new javax.swing.JMenuItem();
        ui_file_export_crease = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        ui_file_properties = new javax.swing.JMenuItem();
        ui_edit = new javax.swing.JMenu();
        ui_edit_undo = new javax.swing.JMenuItem();
        ui_edit_redo = new javax.swing.JMenuItem();
        ui_view = new javax.swing.JMenu();
        ui_view_paper = new javax.swing.JMenu();
        ui_view_paper_image = new javax.swing.JCheckBoxMenuItem();
        ui_view_paper_gradient = new javax.swing.JCheckBoxMenuItem();
        ui_view_paper_plain = new javax.swing.JCheckBoxMenuItem();
        ui_view_paper_none = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        ui_view_zoom = new javax.swing.JCheckBoxMenuItem();
        ui_view_best = new javax.swing.JCheckBoxMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        ui_view_options = new javax.swing.JMenuItem();
        ui_help = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        ui_help_about = new javax.swing.JMenuItem();
        ui_tutorials = new javax.swing.JMenu();
        ui_tutorials_internet = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        oPanel1.setBackground(new java.awt.Color(255, 255, 255));
        oPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("3D View"));
        oPanel1.setMinimumSize(new java.awt.Dimension(170, 34));
        oPanel1.setPreferredSize(new java.awt.Dimension(400, 400));
        oPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                oPanel1MouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                oPanel1MouseDragged(evt);
            }
        });
        oPanel1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                oPanel1MouseWheelMoved(evt);
            }
        });
        oPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                oPanel1MousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                oPanel1MouseClicked(evt);
            }
        });
        oPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                oPanel1ComponentResized(evt);
            }
        });
        jTabbedPane1.addTab("Editor", oPanel1);

        getContentPane().add(jTabbedPane1);

        ui_file.setText("File");

        ui_file_new.setIcon(javax.swing.UIManager.getIcon("FileView.fileIcon"));
        ui_file_new.setText("New");

        ui_file_new_square.setText("Square origami");
        ui_file_new_square.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_new_squareActionPerformed(evt);
            }
        });
        ui_file_new.add(ui_file_new_square);

        ui_file_new_a4.setText("A4 origami");
        ui_file_new_a4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_new_a4ActionPerformed(evt);
            }
        });
        ui_file_new.add(ui_file_new_a4);

        ui_file_new_hexagonal.setText("Hexagonal origami");
        ui_file_new_hexagonal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_new_hexagonalActionPerformed(evt);
            }
        });
        ui_file_new.add(ui_file_new_hexagonal);

        ui_file_new_dollar.setText("Dollar bill origami");
        ui_file_new_dollar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_new_dollarActionPerformed(evt);
            }
        });
        ui_file_new.add(ui_file_new_dollar);

        ui_file_new_bases.setText("Bases");
        ui_file_new.add(ui_file_new_bases);

        ui_file.add(ui_file_new);

        ui_file_sample.setText("Sample figures");
        ui_file.add(ui_file_sample);
        ui_file.add(jSeparator1);

        ui_file_open.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        ui_file_open.setIcon(javax.swing.UIManager.getIcon("FileView.directoryIcon"));
        ui_file_open.setText("Open...");
        ui_file_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_openActionPerformed(evt);
            }
        });
        ui_file.add(ui_file_open);

        ui_file_save.setIcon(javax.swing.UIManager.getIcon("FileView.floppyDriveIcon"));
        ui_file_save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        ui_file_save.setText("Save");
        ui_file_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_saveActionPerformed(evt);
            }
        });
        ui_file.add(ui_file_save);

        ui_file_saveas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        ui_file_saveas.setText("Save As...");
        ui_file_saveas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_saveasActionPerformed(evt);
            }
        });
        ui_file.add(ui_file_saveas);

        ui_file_export.setText("Export");

        ui_file_export_topdf.setText("To PDF...");
        ui_file_export_topdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_topdfActionPerformed(evt);
            }
        });
        ui_file_export.add(ui_file_export_topdf);

        ui_file_export_toopenctm.setText("To OpenCTM 3D File...");
        ui_file_export_toopenctm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_toopenctmActionPerformed(evt);
            }
        });
        ui_file_export.add(ui_file_export_toopenctm);

        ui_file_export_togif.setText("To GIF");

        ui_file_export_togif_revolving.setText("Revolving animation...");
        ui_file_export_togif_revolving.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_togif_revolvingActionPerformed(evt);
            }
        });
        ui_file_export_togif.add(ui_file_export_togif_revolving);

        ui_file_export_togif_folding.setText("Folding process...");
        ui_file_export_togif_folding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_togif_foldingActionPerformed(evt);
            }
        });
        ui_file_export_togif.add(ui_file_export_togif_folding);

        ui_file_export.add(ui_file_export_togif);

        ui_file_export_toself.setText("To self-displaying ORI...");
        ui_file_export_toself.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_toselfActionPerformed(evt);
            }
        });
        ui_file_export.add(ui_file_export_toself);

        ui_file_export_crease.setText("Crease pattern to PNG...");
        ui_file_export_crease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_export_creaseActionPerformed(evt);
            }
        });
        ui_file_export.add(ui_file_export_crease);

        ui_file.add(ui_file_export);
        ui_file.add(jSeparator6);

        ui_file_properties.setText("Properties");
        ui_file_properties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_file_propertiesActionPerformed(evt);
            }
        });
        ui_file.add(ui_file_properties);

        jMenuBar1.add(ui_file);

        ui_edit.setText("Edit");

        ui_edit_undo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        ui_edit_undo.setText("Undo");
        ui_edit_undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_edit_undoActionPerformed(evt);
            }
        });
        ui_edit.add(ui_edit_undo);

        ui_edit_redo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        ui_edit_redo.setText("Redo");
        ui_edit_redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_edit_redoActionPerformed(evt);
            }
        });
        ui_edit.add(ui_edit_redo);

        jMenuBar1.add(ui_edit);

        ui_view.setText("View");

        ui_view_paper.setText("Paper texture");

        ui_view_paper_image.setText("Image");
        ui_view_paper_image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_paper_imageActionPerformed(evt);
            }
        });
        ui_view_paper.add(ui_view_paper_image);

        ui_view_paper_gradient.setSelected(true);
        ui_view_paper_gradient.setText("Gradient");
        ui_view_paper_gradient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_paper_gradientActionPerformed(evt);
            }
        });
        ui_view_paper.add(ui_view_paper_gradient);

        ui_view_paper_plain.setText("Plain");
        ui_view_paper_plain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_paper_plainActionPerformed(evt);
            }
        });
        ui_view_paper.add(ui_view_paper_plain);

        ui_view_paper_none.setText("None");
        ui_view_paper_none.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_paper_noneActionPerformed(evt);
            }
        });
        ui_view_paper.add(ui_view_paper_none);

        ui_view.add(ui_view_paper);
        ui_view.add(jSeparator5);

        ui_view_zoom.setSelected(true);
        ui_view_zoom.setText("Zoom on scroll");
        ui_view_zoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_zoomActionPerformed(evt);
            }
        });
        ui_view.add(ui_view_zoom);

        ui_view_best.setSelected(true);
        ui_view_best.setText("Always in the middle");
        ui_view_best.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_bestActionPerformed(evt);
            }
        });
        ui_view.add(ui_view_best);
        ui_view.add(jSeparator4);

        ui_view_options.setText("Options");
        ui_view_options.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_view_optionsActionPerformed(evt);
            }
        });
        ui_view.add(ui_view_options);

        jMenuBar1.add(ui_view);

        ui_help.setText("Help");

        jMenuItem1.setText("The OrigamiScript documentation");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        ui_help.add(jMenuItem1);

        ui_help_about.setText("About");
        ui_help_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_help_aboutActionPerformed(evt);
            }
        });
        ui_help.add(ui_help_about);

        jMenuBar1.add(ui_help);

        ui_tutorials.setText("Tutorials");

        ui_tutorials_internet.setText("On the Internet");
        ui_tutorials_internet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ui_tutorials_internetActionPerformed(evt);
            }
        });
        ui_tutorials.add(ui_tutorials_internet);

        jMenuBar1.add(ui_tutorials);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Hacer origami refejar
    private void foldingops_reflect_actionPerformed(java.awt.event.ActionEvent evt) {

        /*if (EditorState == ControlState.VONALZO2) {

            double pontX = ((double) liner2X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
            double pontY = ((double) liner2Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();
            double pont1X = ((double) liner1X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
            double pont1Y = ((double) liner1Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();

            double[] vonalzoNV = new double[]{
                oPanel1.PanelCamera.axis_x[0] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[0] * (liner1X - liner2X),
                oPanel1.PanelCamera.axis_x[1] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[1] * (liner1X - liner2X),
                oPanel1.PanelCamera.axis_x[2] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[2] * (liner1X - liner2X)
            };
            double[] vonalzoPT = new double[]{
                oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[0],
                oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[1],
                oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[2]
            };
            double[] vonalzoPT1 = new double[]{
                oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[0],
                oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[1],
                oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[2]
            };
            if (neusisOn) {
                vonalzoNV = Origami.vector(vonalzoPT, vonalzoPT1);
            }
            if (Origami.scalar_product(oPanel1.PanelCamera.camera_pos, vonalzoNV) - Origami.scalar_product(vonalzoPT, vonalzoNV) > 0) {
                //vonalzoNV = new double[]{-vonalzoNV[0], -vonalzoNV[1], -vonalzoNV[2]};
            }

            
            if (alwaysInMiddle) {
                oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            }
            defaultify();
            saved = false;
        } else if (EditorState == ControlState.ILLESZTES3) {

            String pszo = "";
            if (SecondaryState == ControlState.AFFIN_ALTER) {
                pszo = "planethrough";
            }
            if (SecondaryState == ControlState.SZOGFELEZO) {
                pszo = "angle-bisector";
            }
            
            if (alwaysInMiddle) {
                oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            }
            defaultify();
            saved = false;
        }

        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;
        if (alignOn) {
            oPanel1.resetAlignmentPoint();
        }*/
    }

    //Hacer origami rotar
    private void foldingops_rotate_actionPerformed(java.awt.event.ActionEvent evt) {

        /*if (EditorState == ControlState.VONALZO2) {

            EditorState = ControlState.SZOG;
            scroll_angle = 0;
            oPanel1.displayProtractor(scroll_angle);
            oPanel1.repaint();
        } else if (EditorState == ControlState.ILLESZTES3) {

            EditorState = ControlState.ILLESZTES_SZOG;
            scroll_angle = 0;
            oPanel1.displayProtractor(scroll_angle);
            oPanel1.repaint();
        }*/
    }

    //Hacer origami cortar
    private void foldingops_cut_actionPerformed(java.awt.event.ActionEvent evt) {

        /*if (EditorState == ControlState.VONALZO2) {

            double pontX = ((double) liner2X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
            double pontY = ((double) liner2Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();
            double pont1X = ((double) liner1X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
            double pont1Y = ((double) liner1Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();

            double[] vonalzoNV = new double[]{
                oPanel1.PanelCamera.axis_x[0] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[0] * (liner1X - liner2X),
                oPanel1.PanelCamera.axis_x[1] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[1] * (liner1X - liner2X),
                oPanel1.PanelCamera.axis_x[2] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[2] * (liner1X - liner2X)
            };
            double[] vonalzoPT = new double[]{
                oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[0],
                oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[1],
                oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[2]
            };
            double[] vonalzoPT1 = new double[]{
                oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[0],
                oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[1],
                oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[2]
            };
            if (neusisOn) {
                vonalzoNV = Origami.vector(vonalzoPT, vonalzoPT1);
            }
            if (Origami.scalar_product(oPanel1.PanelCamera.camera_pos, vonalzoNV) - Origami.scalar_product(vonalzoPT, vonalzoNV) > 0) {
                /*vonalzoNV = new double[]{-vonalzoNV[0], -vonalzoNV[1], -vonalzoNV[2]};*/
            /*}

            
            if (alwaysInMiddle) {
                oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            }
            defaultify();
            saved = false;
        } else if (EditorState == ControlState.ILLESZTES3) {

            String pszo = "";
            if (SecondaryState == ControlState.AFFIN_ALTER) {
                pszo = "planethrough";
            }
            if (SecondaryState == ControlState.SZOGFELEZO) {
                pszo = "angle-bisector";
            }
            EditorState = ControlState.KESZENLET;
            if (alwaysInMiddle) {
                oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            }
            defaultify();
            saved = false;
        }

        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;
        if (alignOn) {
            oPanel1.resetAlignmentPoint();
        }*/
    }

    //
    //  PDF EXPORT
    // Exoortar pdf
    private void ui_file_export_topdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_topdfActionPerformed

        /*if (pdf_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {

                final javax.swing.JDialog exporting = new javax.swing.JDialog(this);
                exporting.setUndecorated(true);
                javax.swing.JLabel loadmsg = new javax.swing.JLabel(Dictionary.getString("exporting..."));
                loadmsg.setForeground(Color.RED);
                exporting.setLayout(new java.awt.BorderLayout());
                exporting.getContentPane().add(loadmsg, java.awt.BorderLayout.CENTER);
                exporting.getContentPane().setBackground(Color.YELLOW);
                exporting.pack();
                exporting.setResizable(false);
                exporting.setLocationRelativeTo(null);
                exporting.setVisible(true);

                final Exception[] unreportedException = {null};

                new javax.swing.SwingWorker<Void, Integer>() {

                    @Override
                    protected Void doInBackground() {

                        try {

                            terminal.execute("title [" + pdf_export.getSelectedFile().getName().replace(".pdf", "")
                                    + "] filename [" + pdf_export.getSelectedFile().getPath()
                                    + (pdf_export.getSelectedFile().getPath().endsWith(".pdf") ? "] export-autopdf" : ".pdf] export-autopdf"), OrigamiScriptTerminal.Access.ROOT);
                            oPanel1.update(terminal.TerminalOrigami);
                            defaultify();
                            scroll_angle = 0;
                        } catch (Exception ex) {

                            unreportedException[0] = ex;
                            exporting.setVisible(false);
                            exporting.dispose();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {

                        exporting.setVisible(false);
                        exporting.dispose();
                        javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("export-finished"), "Message", javax.swing.JOptionPane.PLAIN_MESSAGE);
                    }
                }.execute();

                if (unreportedException[0] != null) {
                    throw unreportedException[0];
                }
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_topdfActionPerformed

    //
    //  UNDO
    //
    private void ui_edit_undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_edit_undoActionPerformed

        if (EditorState == ControlState.LOCKED) {
            return;
        }
        try {
            terminal.execute("undo");
        } catch (Exception exc) {
            System.err.println(exc);
        }

        oPanel1.update(terminal.TerminalOrigami);

        oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
        }
        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
        }
        if (alwaysInMiddle) {
            oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
        }
        defaultify();
        scroll_angle = 0;
        saved = false;
        //foldNumber = terminal.TerminalOrigami.history_pointer();
        //changeListenerShutUp = true;
        //changeListenerShutUp = false;
    }//GEN-LAST:event_ui_edit_undoActionPerformed

    //
    //  SAVE AS
    //
    private void ui_file_saveasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_saveasActionPerformed

        if (save.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            if (new java.io.File(save.getSelectedFile().getPath().endsWith(".ori")
                    ? save.getSelectedFile().getPath()
                    : save.getSelectedFile().getPath() + ".ori").exists()) {
                if (javax.swing.JOptionPane.showConfirmDialog(null, Dictionary.getString("overwrite"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.NO_OPTION) {
                    ui_file_saveasActionPerformed(evt);
                    return;
                }
            }

            if (save.getFileFilter() == save.getChoosableFileFilters()[0]) {

                try {
                    terminal.execute("filename [" + save.getSelectedFile().getPath()
                            + (save.getSelectedFile().getPath().endsWith(".ori") ? "] export-ori" : ".ori] export-ori"), OrigamiScriptTerminal.Access.ROOT);
                    oPanel1.update(terminal.TerminalOrigami);
                    defaultify();
                    scroll_angle = 0;
                    fileName = save.getSelectedFile().getPath();
                    setTitle(save.getSelectedFile().getName() + " - Origami Editor 3D");
                    saved = true;
                } catch (Exception ex) {
                    oPanel1.update(terminal.TerminalOrigami);
                    javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_ui_file_saveasActionPerformed

    //
    //  CTM EXPORT
    // Exportar ctm
    private void ui_file_export_toopenctmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_toopenctmActionPerformed

        /*if (ctm_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {
                terminal.execute("filename [" + ctm_export.getSelectedFile().getPath()
                        + (ctm_export.getSelectedFile().getPath().endsWith(".ctm") ? "] export-ctm" : ".ctm] export-ctm"), OrigamiScriptTerminal.Access.ROOT);
                oPanel1.update(terminal.TerminalOrigami);
                defaultify();
                scroll_angle = 0;
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_toopenctmActionPerformed

    //
    //  OPEN
    //
    private void ui_file_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_openActionPerformed

        if (!saved) {
            Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
            if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        if (open.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            if (open.getFileFilter() == open.getChoosableFileFilters()[0]) {

                try {
                    terminal.execute("filename [" + open.getSelectedFile().getPath() + "] open", OrigamiScriptTerminal.Access.ROOT);
                    oPanel1.update(terminal.TerminalOrigami);
                    //pPanel1.update(terminal.TerminalOrigami);

                    /*oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
                    if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                        oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
                    }*/
                    if (alwaysInMiddle) {
                        oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                    }
                    oPanel1.PanelCamera.setOrthogonalView(0);
                    scroll_angle = 0;
                    defaultify();
                    fileName = open.getSelectedFile().getPath();
                    setTitle(open.getSelectedFile().getName() + " - Origami Editor 3D");
                    saved = true;
                } catch (Exception ex) {
                    oPanel1.update(terminal.TerminalOrigami);
                    javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } else if (open.getFileFilter() == open.getChoosableFileFilters()[1]) {

                try {
                    terminal.execute("filename [" + open.getSelectedFile().getPath() + "] load", OrigamiScriptTerminal.Access.ROOT);
                    oPanel1.update(terminal.TerminalOrigami);

                    oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
                    if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                        oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
                    }
                    if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                    }
                    if (alwaysInMiddle) {
                        oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                    }
                    oPanel1.PanelCamera.setOrthogonalView(0);
                    scroll_angle = 0;
                    defaultify();
                    setTitle(open.getSelectedFile().getName() + " - Origami Editor 3D");
                    saved = true;
                } catch (Exception ex) {
                    oPanel1.update(terminal.TerminalOrigami);
                    javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        //foldNumber = terminal.TerminalOrigami.history_pointer();
        //changeListenerShutUp = true;
        //changeListenerShutUp = false;
    }//GEN-LAST:event_ui_file_openActionPerformed

    //
    //  REDO
    //
    private void ui_edit_redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_edit_redoActionPerformed

        if (EditorState == ControlState.LOCKED) {
            return;
        }
        try {

            if (!terminal.history().isEmpty()) {
                terminal.execute("redo");
            }
        } catch (Exception ex) {

            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        if (alwaysInMiddle) {
            oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
        }
        oPanel1.update(terminal.TerminalOrigami);
        defaultify();
        scroll_angle = 0;
        saved = false;
        //foldNumber = terminal.TerminalOrigami.history_pointer();
        //changeListenerShutUp = true;
        //changeListenerShutUp = false;
    }//GEN-LAST:event_ui_edit_redoActionPerformed

    //
    //  NEW HEXAGONAL PAPER 
    // Papel hexagonal
    private void ui_file_new_hexagonalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_new_hexagonalActionPerformed

        /*if (!saved) {
            Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
            if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {

            fileName = null;
            terminal.execute("paper hexagon new");

            oPanel1.update(terminal.TerminalOrigami);

            oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
            }
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            }

            defaultify();
            setTitle("Origami Editor 3D");
            saved = true;
        } catch (Exception ex) {

            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;*/
    }//GEN-LAST:event_ui_file_new_hexagonalActionPerformed

    //
    //  NEW SQUARE PAPER
    // Papel cuadrado
    private void ui_file_new_squareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_new_squareActionPerformed

        /*if (!saved) {
            Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
            if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {

            fileName = null;
            terminal.execute("paper square new");

            oPanel1.update(terminal.TerminalOrigami);

            oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
            }
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            }
            defaultify();
            setTitle("Origami Editor 3D");
            saved = true;
        } catch (Exception ex) {

            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;*/
    }//GEN-LAST:event_ui_file_new_squareActionPerformed

    //
    //  NEW A4 PAPER
    // Papel A4
    private void ui_file_new_a4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_new_a4ActionPerformed

        /*if (!saved) {
            Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
            if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {

            fileName = null;
            terminal.execute("paper a4 new");

            oPanel1.update(terminal.TerminalOrigami);

            oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
            }
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            }
            defaultify();
            setTitle("Origami Editor 3D");
            saved = true;
        } catch (Exception ex) {

            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;*/
    }//GEN-LAST:event_ui_file_new_a4ActionPerformed

    //
    //  NEW USD PAPER
    // Imagen rectangular
    private void ui_file_new_dollarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_new_dollarActionPerformed

        /*if (!saved) {
            Object[] options = {Dictionary.getString("yes"), Dictionary.getString("no")};
            if (javax.swing.JOptionPane.showOptionDialog(this, Dictionary.getString("nosave"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {

            fileName = null;
            terminal.execute("paper usd new");

            oPanel1.update(terminal.TerminalOrigami);

            oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
            }
            if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            }
            defaultify();
            setTitle("Origami Editor 3D");
            saved = true;
        } catch (Exception ex) {

            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;*/
    }//GEN-LAST:event_ui_file_new_dollarActionPerformed

    //
    //  OPTIONS MENU
    // Ajustes
    private void ui_view_optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_optionsActionPerformed
        settings.setVisible(true);
    }//GEN-LAST:event_ui_view_optionsActionPerformed

    //
    //  PLAIN PAPER
    // Papel plano
    private void ui_view_paper_plainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_paper_plainActionPerformed

        /*oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.SIMA);
        ui_view_paper_plain.setSelected(true);
        ui_view_paper_image.setSelected(false);
        ui_view_paper_none.setSelected(false);
        ui_view_paper_gradient.setSelected(false);
        oPanel1.repaint();*/
    }//GEN-LAST:event_ui_view_paper_plainActionPerformed

    //
    //  ZOOM ON SCROLL
    // Permite hacer zoom con la rueda del ratón. Activa esa acción.
    private void ui_view_zoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_zoomActionPerformed
        zoomOnScroll = ui_view_zoom.isSelected();
    }//GEN-LAST:event_ui_view_zoomActionPerformed

    //
    //  ABOUT
    //
    private void ui_help_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_help_aboutActionPerformed

        /* TODO!
        final javax.swing.JEditorPane html = new javax.swing.JEditorPane("text/html", "<html><body>"
                + "Origami Editor 3D Version " + Version + " <br>"
                + "Copyright © 2014 Bágyoni-Szabó Attila (ba-sz-at@users.sourceforge.net) <br>"
                + "<br>"
                + "Origami Editor 3D is licensed under the GNU General Public License version 3. <br>"
                + "<a href=\"/res/LICENSE.txt\">Click here for more information.</a>"
                + "</body></html>");

        html.setEditable(false);
        html.setHighlighter(null);
        html.addHyperlinkListener((javax.swing.event.HyperlinkEvent evt1) -> {
            if (evt1.getEventType().equals(javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)) {
                java.util.Scanner inf = new java.util.Scanner(OrigamiEditorUI.this.getClass().getResourceAsStream(evt1.getDescription()), "UTF-8");
                String text = "";
                while (inf.hasNextLine()) {
                    text += inf.nextLine() + (char) 10;
                }
                jTabbedPane1.setSelectedIndex(1);
                javax.swing.SwingUtilities.getWindowAncestor(html).dispose();
            }
        });
        javax.swing.JOptionPane.showMessageDialog(this, html);*/
    }//GEN-LAST:event_ui_help_aboutActionPerformed

    //
    // SAVE
    //
    private void ui_file_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_saveActionPerformed

        if (fileName == null) {
            if (save.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

                if (new java.io.File(save.getSelectedFile().getPath().endsWith(".ori")
                        ? save.getSelectedFile().getPath()
                        : save.getSelectedFile().getPath() + ".ori").exists()) {
                    if (javax.swing.JOptionPane.showConfirmDialog(null, Dictionary.getString("overwrite"), Dictionary.getString("question"), javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.NO_OPTION) {
                        ui_file_saveActionPerformed(evt);
                        return;
                    }
                }

                if (save.getFileFilter() == save.getChoosableFileFilters()[0]) {

                    try {
                        terminal.execute("filename [" + save.getSelectedFile().getPath()
                                + (save.getSelectedFile().getPath().endsWith(".ori") ? "] export-ori" : ".ori] export-ori"), OrigamiScriptTerminal.Access.ROOT);
                        oPanel1.update(terminal.TerminalOrigami);
                        defaultify();
                        scroll_angle = 0;
                        fileName = save.getSelectedFile().getPath();
                        setTitle(save.getSelectedFile().getName() + " - Origami Editor 3D");
                        saved = true;
                    } catch (Exception ex) {
                        oPanel1.update(terminal.TerminalOrigami);
                        javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            try {
                terminal.execute("filename [" + fileName + (fileName.endsWith(".ori") ? "] export-ori" : ".ori] export-ori"), OrigamiScriptTerminal.Access.ROOT);
                oPanel1.update(terminal.TerminalOrigami);
                defaultify();
                scroll_angle = 0;
                saved = true;
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_ui_file_saveActionPerformed

    //
    // ALWAYS IN THE MIDDLE
    //
    private void ui_view_bestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_bestActionPerformed

        /*alwaysInMiddle = !alwaysInMiddle;
        if (alwaysInMiddle) {
            oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            oPanel1.linerOff();
            oPanel1.repaint();
        } else {
            oPanel1.PanelCamera.unadjust(terminal.TerminalOrigami);
            oPanel1.linerOff();
            oPanel1.repaint();
        }*/
    }//GEN-LAST:event_ui_view_bestActionPerformed

    //
    // UV PAPER
    // Papel con imagen importada
    private void ui_view_paper_imageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_paper_imageActionPerformed

        /*ui_view_paper_image.setSelected(false);
        if (tex == null) {

            if (javax.swing.JOptionPane.showOptionDialog(
                    this,
                    Dictionary.getString("loadtex"),
                    Dictionary.getString("notex"),
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null) == 0) {

                if (texture_open.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

                    try {
                        terminal.execute("filename [" + texture_open.getSelectedFile().getPath() + "] load-texture");
                        tex = terminal.paper_texture();
                        if (tex.getHeight() < (int) terminal.TerminalOrigami.paperHeight() || tex.getWidth() < (int) terminal.TerminalOrigami.paperWidth()) {

                            String uzenet = Dictionary.getString("smalltex") + (char) 10
                                    + java.text.MessageFormat.format(Dictionary.getString("idealtex"), terminal.TerminalOrigami.paperWidth(), terminal.TerminalOrigami.paperHeight());
                            javax.swing.JOptionPane.showMessageDialog(this, uzenet, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        } else if (tex.getHeight() > (int) terminal.TerminalOrigami.paperHeight() || tex.getWidth() > (int) terminal.TerminalOrigami.paperWidth()) {

                            String uzenet = Dictionary.getString("largetex") + (char) 10
                                    + java.text.MessageFormat.format(Dictionary.getString("idealtex"), terminal.TerminalOrigami.paperWidth(), terminal.TerminalOrigami.paperHeight());
                            javax.swing.JOptionPane.showMessageDialog(this, uzenet, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        }
                        oPanel1.setTexture(tex);
                        oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.UV);
                        ui_view_paper_image.setSelected(true);
                        ui_view_paper_plain.setSelected(false);
                        ui_view_paper_none.setSelected(false);
                        ui_view_paper_gradient.setSelected(false);
                        oPanel1.update(terminal.TerminalOrigami);
                        oPanel1.repaint();

                    } catch (Exception ex) {
                        tex = null;
                        javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else if (oPanel1.displaymode() == OrigamiPanel.DisplayMode.UV) {

            if (javax.swing.JOptionPane.showOptionDialog(
                    this,
                    Dictionary.getString("loadtex"),
                    Dictionary.getString("havetex"),
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null) == 0) {

                if (texture_open.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

                    try {
                        terminal.execute("filename [" + texture_open.getSelectedFile().getPath() + "] load-texture");
                        tex = terminal.paper_texture();
                        if (tex.getHeight() < (int) terminal.TerminalOrigami.paperHeight() || tex.getWidth() < (int) terminal.TerminalOrigami.paperWidth()) {

                            String uzenet = Dictionary.getString("smalltex") + (char) 10
                                    + java.text.MessageFormat.format(Dictionary.getString("idealtex"), terminal.TerminalOrigami.paperWidth(), terminal.TerminalOrigami.paperHeight());
                            javax.swing.JOptionPane.showMessageDialog(this, uzenet, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        } else if (tex.getHeight() > (int) terminal.TerminalOrigami.paperHeight() || tex.getWidth() > (int) terminal.TerminalOrigami.paperWidth()) {

                            String uzenet = Dictionary.getString("largetex") + (char) 10
                                    + java.text.MessageFormat.format(Dictionary.getString("idealtex"), terminal.TerminalOrigami.paperWidth(), terminal.TerminalOrigami.paperHeight());
                            javax.swing.JOptionPane.showMessageDialog(this, uzenet, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        }
                        oPanel1.setTexture(tex);
                        oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.UV);
                        ui_view_paper_image.setSelected(true);
                        ui_view_paper_plain.setSelected(false);
                        ui_view_paper_none.setSelected(false);
                        ui_view_paper_gradient.setSelected(false);
                        oPanel1.update(terminal.TerminalOrigami);
                        oPanel1.repaint();
                    } catch (Exception ex) {
                        tex = null;
                        javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {

            oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.UV);
            ui_view_paper_image.setSelected(true);
            ui_view_paper_plain.setSelected(false);
            ui_view_paper_none.setSelected(false);
            ui_view_paper_gradient.setSelected(false);
            oPanel1.update(terminal.TerminalOrigami);
            oPanel1.repaint();
        }*/
    }//GEN-LAST:event_ui_view_paper_imageActionPerformed

    //
    //  EMPTY PAPER
    // papel sin nada
    private void ui_view_paper_noneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_paper_noneActionPerformed

        /*oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.SEMMI);
        ui_view_paper_none.setSelected(true);
        ui_view_paper_image.setSelected(false);
        ui_view_paper_plain.setSelected(false);
        ui_view_paper_gradient.setSelected(false);
        oPanel1.repaint();*/
    }//GEN-LAST:event_ui_view_paper_noneActionPerformed

    //
    //  TUTORIALS
    //
    @SuppressWarnings("empty-statement")
    private void ui_tutorials_internetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_tutorials_internetActionPerformed

        try {
            java.util.Scanner inf = new java.util.Scanner(
                    new java.net.URL("http://origamieditor3d.sourceforge.net/info.txt").openStream());
            String line;
            while (!(line = inf.nextLine()).startsWith("tutorials_link"));
            String url = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (IOException | URISyntaxException ex) {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=tHWfKmynoEc&list=PL6Ycz5VMP2kJk71uuJB9r2VqLZhUnnobX"));
            } catch (URISyntaxException | IOException ex2) {
                javax.swing.JOptionPane.showMessageDialog(this, Dictionary.getString("tutorials-fail"));
            }
        }
    }//GEN-LAST:event_ui_tutorials_internetActionPerformed

    //
    //  DOCUMENTATION 
    //
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        try {

            java.io.InputStream is = getClass().getResourceAsStream("/res/osdoc_en.html");
            java.io.File tmp = new java.io.File("osdoc_en.html");
            long ind = 0;
            while (tmp.exists()) {
                tmp = new java.io.File("osdoc_en" + ind + ".html");
                ++ind;
            }
            tmp.deleteOnExit();
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tmp)) {
                int b;
                while ((b = is.read()) != -1) {
                    fos.write(b);
                }
            }
            Desktop.getDesktop().browse(tmp.toURI());
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    //
    //  GRADIENT PAPER
    // papel con gradiente
    private void ui_view_paper_gradientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_view_paper_gradientActionPerformed

        /*oPanel1.setDisplaymode(OrigamiPanel.DisplayMode.GRADIENT);
        ui_view_paper_gradient.setSelected(true);
        ui_view_paper_none.setSelected(false);
        ui_view_paper_image.setSelected(false);
        ui_view_paper_plain.setSelected(false);

        oPanel1.repaint();*/
    }//GEN-LAST:event_ui_view_paper_gradientActionPerformed

    
    //Export gif revolving
    private void ui_file_export_togif_revolvingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_togif_revolvingActionPerformed

        /*if (gif_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {

                final javax.swing.JDialog exporting = new javax.swing.JDialog(this);
                exporting.setUndecorated(true);
                javax.swing.JLabel loadmsg = new javax.swing.JLabel(Dictionary.getString("exporting..."));
                loadmsg.setForeground(Color.RED);
                exporting.setLayout(new java.awt.BorderLayout());
                exporting.getContentPane().add(loadmsg, java.awt.BorderLayout.CENTER);
                exporting.getContentPane().setBackground(Color.YELLOW);
                exporting.pack();
                exporting.setResizable(false);
                exporting.setLocationRelativeTo(null);
                exporting.setVisible(true);

                final Exception[] unreportedException = {null};

                new javax.swing.SwingWorker<Void, Integer>() {

                    @Override
                    protected Void doInBackground() {

                        try {

                            terminal.execute("camera [" + oPanel1.PanelCamera.camera_dir[0] + " " + oPanel1.PanelCamera.camera_dir[1] + " " + oPanel1.PanelCamera.camera_dir[2] + "] "
                                    + "[" + oPanel1.PanelCamera.axis_x[0] + " " + oPanel1.PanelCamera.axis_x[1] + " " + oPanel1.PanelCamera.axis_x[2] + "] "
                                    + "[" + oPanel1.PanelCamera.axis_y[0] + " " + oPanel1.PanelCamera.axis_y[1] + " " + oPanel1.PanelCamera.axis_y[2] + "] "
                                    + "color " + oPanel1.getFrontColor() + " "
                                    + "filename [" + gif_export.getSelectedFile().getPath()
                                    + (gif_export.getSelectedFile().getPath().endsWith(".gif") ? "] export-revolving-gif" : ".gif] export-revolving-gif"),
                                    OrigamiScriptTerminal.Access.ROOT);
                            oPanel1.update(terminal.TerminalOrigami);
                            defaultify();
                            scroll_angle = 0;
                        } catch (Exception ex) {

                            unreportedException[0] = ex;
                            exporting.setVisible(false);
                            exporting.dispose();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {

                        exporting.setVisible(false);
                        exporting.dispose();
                        javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("export-finished"), "Message", javax.swing.JOptionPane.PLAIN_MESSAGE);
                    }
                }.execute();

                if (unreportedException[0] != null) {
                    throw unreportedException[0];
                }
            } catch (Exception ex) {

                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_togif_revolvingActionPerformed

    //Export gif folding
    private void ui_file_export_togif_foldingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_togif_foldingActionPerformed

        /*if (gif_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {

                final javax.swing.JDialog exporting = new javax.swing.JDialog(this);
                exporting.setUndecorated(true);
                javax.swing.JLabel loadmsg = new javax.swing.JLabel(Dictionary.getString("exporting..."));
                loadmsg.setForeground(Color.RED);
                exporting.setLayout(new java.awt.BorderLayout());
                exporting.getContentPane().add(loadmsg, java.awt.BorderLayout.CENTER);
                exporting.getContentPane().setBackground(Color.YELLOW);
                exporting.pack();
                exporting.setResizable(false);
                exporting.setLocationRelativeTo(null);
                exporting.setVisible(true);

                final Exception[] unreportedException = {null};

                new javax.swing.SwingWorker<Void, Integer>() {

                    @Override
                    protected Void doInBackground() {

                        try {

                            terminal.execute("camera [" + oPanel1.PanelCamera.camera_dir[0] + " " + oPanel1.PanelCamera.camera_dir[1] + " " + oPanel1.PanelCamera.camera_dir[2] + "] "
                                    + "[" + oPanel1.PanelCamera.axis_x[0] + " " + oPanel1.PanelCamera.axis_x[1] + " " + oPanel1.PanelCamera.axis_x[2] + "] "
                                    + "[" + oPanel1.PanelCamera.axis_y[0] + " " + oPanel1.PanelCamera.axis_y[1] + " " + oPanel1.PanelCamera.axis_y[2] + "] "
                                    + "color " + oPanel1.getFrontColor() + " "
                                    + "filename [" + gif_export.getSelectedFile().getPath()
                                    + (gif_export.getSelectedFile().getPath().endsWith(".gif") ? "] export-gif" : ".gif] export-gif"),
                                    OrigamiScriptTerminal.Access.ROOT);
                            oPanel1.update(terminal.TerminalOrigami);
                            defaultify();
                            scroll_angle = 0;
                        } catch (Exception ex) {

                            unreportedException[0] = ex;
                            exporting.setVisible(false);
                            exporting.dispose();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {

                        exporting.setVisible(false);
                        exporting.dispose();
                        javax.swing.JOptionPane.showMessageDialog(OrigamiEditorUI.this, Dictionary.getString("export-finished"), "Message", javax.swing.JOptionPane.PLAIN_MESSAGE);
                    }
                }.execute();

                if (unreportedException[0] != null) {
                    throw unreportedException[0];
                }
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_togif_foldingActionPerformed

    //Export png
    private void ui_file_export_creaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_creaseActionPerformed

        /*if (png_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {
                terminal.execute("filename [" + png_export.getSelectedFile().getPath()
                        + (png_export.getSelectedFile().getPath().endsWith(".png") ? "] export-png" : ".png] export-png"),
                        OrigamiScriptTerminal.Access.ROOT);
                oPanel1.update(terminal.TerminalOrigami);
                defaultify();
                scroll_angle = 0;
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_creaseActionPerformed

    // file properties: Version, papertype, number of steps, difficulty
    private void ui_file_propertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_propertiesActionPerformed

        javax.swing.JDialog properties = new javax.swing.JDialog(this, Dictionary.getString("properties"));
        properties.getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.NORTH;
        properties.getContentPane().add(new javax.swing.JLabel(Dictionary.getString("origami-version")), c);
        c.gridy = 1;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 2;
        properties.getContentPane().add(new javax.swing.JLabel(Dictionary.getString("origami-papertype")), c);
        c.gridy = 3;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 4;
        properties.getContentPane().add(new javax.swing.JLabel(Dictionary.getString("origami-steps")), c);
        c.gridy = 5;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 6;
        properties.getContentPane().add(new javax.swing.JLabel(Dictionary.getString("origami-difficulty")), c);
        c.gridy = 7;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridx = 1;
        c.gridy = 0;
        properties.getContentPane().add(new javax.swing.JLabel("Generation " + terminal.TerminalOrigami.generation()), c);
        c.gridy = 1;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 2;
        //properties.getContentPane().add(new javax.swing.JLabel(Dictionary.getString(terminal.TerminalOrigami.papertype().toString())), c);
        c.gridy = 3;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 4;
        properties.getContentPane().add(new javax.swing.JLabel(Integer.toString(terminal.TerminalOrigami.history().size())), c);
        c.gridy = 5;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);
        c.gridy = 6;
        int dif = Origami.difficultyLevel(terminal.TerminalOrigami.difficulty());
        String difname = null;
        switch (dif) {
            case 0:
                difname = Dictionary.getString("level0");
                break;
            case 1:
                difname = Dictionary.getString("level1");
                break;
            case 2:
                difname = Dictionary.getString("level2");
                break;
            case 3:
                difname = Dictionary.getString("level3");
                break;
            case 4:
                difname = Dictionary.getString("level4");
                break;
            case 5:
                difname = Dictionary.getString("level5");
                break;
            case 6:
                difname = Dictionary.getString("level6");
                break;
        }
        properties.getContentPane().add(new javax.swing.JLabel(String.format(Dictionary.getString("level"), dif, difname)), c);
        c.gridy = 7;
        properties.getContentPane().add(new javax.swing.JLabel(" "), c);

        properties.setResizable(false);
        properties.pack();
        properties.setLocationRelativeTo(this);
        properties.setVisible(true);
    }//GEN-LAST:event_ui_file_propertiesActionPerformed

    
    //Export ori.jar
    private void ui_file_export_toselfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ui_file_export_toselfActionPerformed

        /*if (jar_export.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {
                terminal.execute("filename [" + jar_export.getSelectedFile().getPath()
                        + (jar_export.getSelectedFile().getPath().endsWith(".ori.jar") ? "] export-jar" : ".ori.jar] export-jar"),
                        OrigamiScriptTerminal.Access.ROOT);
                oPanel1.update(terminal.TerminalOrigami);
                defaultify();
                scroll_angle = 0;
            } catch (Exception ex) {
                oPanel1.update(terminal.TerminalOrigami);
                javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage().replace('/', (char) 10), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }*/
    }//GEN-LAST:event_ui_file_export_toselfActionPerformed

    //
    //  RESIZE
    //
    private void oPanel1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_oPanel1ComponentResized

        /* No se muy bien para que vale
        if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
            oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
        }*/
        oPanel1.PanelCamera.xshift = oPanel1.getWidth() / 2;
        oPanel1.PanelCamera.yshift = oPanel1.getHeight() / 2;
    }//GEN-LAST:event_oPanel1ComponentResized

    //
    //  EDITING (3D)
    // Reflejar, rotar una parte para hacer Origami
    private void oPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_oPanel1MouseClicked

        /*if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {

            if (EditorState == ControlState.KESZENLET
                || EditorState == ControlState.ILLESZTES0
                || EditorState == ControlState.ILLESZTES1
                || EditorState == ControlState.ILLESZTES2
                || EditorState == ControlState.ILLESZTES3) {

                if (terminal.TerminalOrigami.circumscribedSquareSize() > 0) {
                    oPanel1.PanelCamera.setZoom(0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize());
                }
                oPanel1.PanelCamera.nextOrthogonalView();
                oPanel1.repaint();
            } else if (EditorState == ControlState.SZOG) {

                double pontX = ((double) liner2X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
                double pontY = ((double) liner2Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();
                double pont1X = ((double) liner1X - oPanel1.PanelCamera.xshift) / oPanel1.PanelCamera.zoom();
                double pont1Y = ((double) liner1Y - oPanel1.PanelCamera.yshift) / oPanel1.PanelCamera.zoom();

                double[] vonalzoNV = new double[]{
                    oPanel1.PanelCamera.axis_x[0] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[0] * (liner1X - liner2X),
                    oPanel1.PanelCamera.axis_x[1] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[1] * (liner1X - liner2X),
                    oPanel1.PanelCamera.axis_x[2] * (liner2Y - liner1Y) + oPanel1.PanelCamera.axis_y[2] * (liner1X - liner2X)
                };
                double[] vonalzoPT = new double[]{
                    oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[0],
                    oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[1],
                    oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pontX + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pontY + oPanel1.PanelCamera.camera_pos[2]
                };
                double[] vonalzoPT1 = new double[]{
                    oPanel1.PanelCamera.axis_x[0] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[0] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[0],
                    oPanel1.PanelCamera.axis_x[1] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[1] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[1],
                    oPanel1.PanelCamera.axis_x[2] / oPanel1.PanelCamera.zoom() * pont1X + oPanel1.PanelCamera.axis_y[2] / oPanel1.PanelCamera.zoom() * pont1Y + oPanel1.PanelCamera.camera_pos[2]
                };
                if (neusisOn) {
                    vonalzoNV = Origami.vector(vonalzoPT, vonalzoPT1);
                }
                if (Origami.scalar_product(oPanel1.PanelCamera.camera_pos, vonalzoNV) - Origami.scalar_product(vonalzoPT, vonalzoNV) > 0) {
                    //vonalzoNV = new double[]{-vonalzoNV[0], -vonalzoNV[1], -vonalzoNV[2]};
                }

                
                if (alwaysInMiddle) {
                    oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                }
                scroll_angle = 0;
                oPanel1.hideProtractor();
                defaultify();
                saved = false;
            } else if (EditorState == ControlState.ILLESZTES_SZOG) {

                String pszo = "";
                if (SecondaryState == ControlState.AFFIN_ALTER) {
                    pszo = "planethrough";
                }
                if (SecondaryState == ControlState.SZOGFELEZO) {
                    pszo = "angle-bisector";
                }
                
                if (alwaysInMiddle) {
                    oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                }
                defaultify();
                saved = false;
            } else {
                if (alwaysInMiddle) {
                    oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
                }
                defaultify();
            }
        } else if (EditorState == ControlState.KESZENLET) {

            liner1X = evt.getX();
            liner1Y = evt.getY();
            if (alignOn) {
                Igazit1(alignment_radius);
            }
            EditorState = ControlState.VONALZO1;
            oPanel1.setToolTipText(oPanel1_tip2);
            javax.swing.ToolTipManager.sharedInstance().mouseMoved(
                new java.awt.event.MouseEvent(
                    oPanel1, 0, System.currentTimeMillis(), 0, evt.getX(), evt.getY(), 0, false));
        } else if (EditorState == ControlState.VONALZO1) {

            liner2X = evt.getX();
            liner2Y = evt.getY();
            if (alignOn) {
                Igazit2(alignment_radius);
            }
            EditorState = ControlState.VONALZO2;
            oPanel1.setToolTipText(null);

            foldingops.show(oPanel1, evt.getX(), evt.getY());

        } else if (EditorState == ControlState.VONALZO2 || EditorState == ControlState.ILLESZTES3) {
            foldingops.show(oPanel1, evt.getX(), evt.getY());
        } else {
            if (alwaysInMiddle) {
                oPanel1.PanelCamera.adjust(terminal.TerminalOrigami);
            }
            defaultify();
        }
        foldNumber = terminal.TerminalOrigami.history_pointer();
        changeListenerShutUp = true;
        changeListenerShutUp = false;
        if (alignOn) {
            oPanel1.resetAlignmentPoint();
        }*/
    }//GEN-LAST:event_oPanel1MouseClicked

    //Hacer click sobre la imagen
    private void oPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_oPanel1MousePressed

        mouseX = evt.getX();
        mouseY = evt.getY();
    }//GEN-LAST:event_oPanel1MousePressed

    //
    //  SCROLL OVER 3D VIEW 
    // Hacer zoom a la figura
    private void oPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_oPanel1MouseWheelMoved

        if (EditorState != ControlState.REGLA1
            && EditorState != ControlState.REGLA2
            && EditorState != ControlState.ANGULO
            && EditorState != ControlState.ANGULO_MONTAJE) {

            if (zoomOnScroll
                && oPanel1.PanelCamera.zoom() - 0.1 * evt.getWheelRotation() <= Camera.maximal_zoom
                && oPanel1.PanelCamera.zoom() - 0.1 * evt.getWheelRotation() >= 0.8 * Math.min(oPanel1.getWidth(), oPanel1.getHeight()) / terminal.TerminalOrigami.circumscribedSquareSize()) {
                oPanel1.PanelCamera.setZoom(oPanel1.PanelCamera.zoom() - 0.1 * evt.getWheelRotation());
            }
            oPanel1.repaint();
        }
    }//GEN-LAST:event_oPanel1MouseWheelMoved

    //
    //  DRAG 
    // Girar la figura
    private void oPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_oPanel1MouseDragged

        if (EditorState == ControlState.ESPERA
            || EditorState == ControlState.UNIRSE0
            || EditorState == ControlState.UNIRSE1
            || EditorState == ControlState.UNIRSE2
            || EditorState == ControlState.UNIRSE3
            || EditorState == ControlState.LOCKED) {

            oPanel1.resetAlignmentPoint();
            oPanel1.PanelCamera.rotate((mouseX - evt.getX()) / (float) oPanel1.PanelCamera.zoom() / 2, (evt.getY() - mouseY) / (float) oPanel1.PanelCamera.zoom() / 2);
            oPanel1.repaint();
            mouseX = evt.getX();
            mouseY = evt.getY();
        }
    }//GEN-LAST:event_oPanel1MouseDragged

    //
    //  MOUSE MOVEMENT OVER 3D VIEW / EGÉRMOZGÁS A 3D NÉZET FELETT
    //
    private void oPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_oPanel1MouseMoved

        if (EditorState == ControlState.ESPERA && alignOn) {

            liner1X = evt.getX();
            liner1Y = evt.getY();
            if (Igazit1(alignment_radius)) {
                oPanel1.setAlignmentPoint(liner1X, liner1Y);
                oPanel1.setAlignmentRadius(alignment_radius);
            } else {
                oPanel1.resetAlignmentPoint();
            }
        } else if (EditorState == ControlState.REGLA1) {

            liner2X = evt.getX();
            liner2Y = evt.getY();
            if (alignOn) {
                if (Igazit2(alignment_radius)) {
                    oPanel1.setAlignmentPoint(liner2X, liner2Y);
                    oPanel1.setAlignmentRadius(alignment_radius);
                } else {
                    oPanel1.resetAlignmentPoint();
                }
            }
            oPanel1.linerOn(null, liner1X, liner1Y, liner2X, liner2Y);
        } else if (EditorState == ControlState.ANGULO || EditorState == ControlState.ANGULO_MONTAJE) {

            if (evt.getX() != oPanel1.getWidth() / 2 || evt.getY() != oPanel1.getHeight() / 2) {

                double r = Math.max(Math.sqrt((evt.getX() - oPanel1.getWidth() / 2) * (evt.getX() - oPanel1.getWidth() / 2) + (evt.getY() - oPanel1.getHeight() / 2) * (evt.getY() - oPanel1.getHeight() / 2)), 1);
                scroll_angle = evt.getX() > oPanel1.getWidth() / 2
                ? (int) (Math.acos((oPanel1.getHeight() / 2 - evt.getY()) / r) * 180. / Math.PI)
                : -(int) (Math.acos((oPanel1.getHeight() / 2 - evt.getY()) / r) * 180. / Math.PI);
                oPanel1.displayProtractor(scroll_angle);
            }
        }
        oPanel1.repaint();
    }//GEN-LAST:event_oPanel1MouseMoved

    /**
     * @param args the command line arguments
     */
    static public void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrigamiEditorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new OrigamiEditorUI().setVisible(true);
        });
    }

    private boolean Igazit1(int sugar) {

        int v1ujX = -1;
        int v1ujY = -1;

        for (int[] posicionRepartidor : oPanel1.PanelCamera.alignmentPoints(terminal.TerminalOrigami, snap2, snap3, snap4)) {

            if ((liner1X - oPanel1.PanelCamera.xshift - posicionRepartidor[0])
                    * (liner1X - oPanel1.PanelCamera.xshift - posicionRepartidor[0])
                    + (liner1Y - oPanel1.PanelCamera.yshift - posicionRepartidor[1])
                    * (liner1Y - oPanel1.PanelCamera.yshift - posicionRepartidor[1]) < sugar) {

                v1ujX = posicionRepartidor[0] + oPanel1.PanelCamera.xshift;
                v1ujY = posicionRepartidor[1] + oPanel1.PanelCamera.yshift;
                break;
            }
        }
        if (v1ujX != -1) {
            liner1X = v1ujX;
            liner1Y = v1ujY;
            return true;
        }
        return false;
    }

    // Igazit = correcta
    private boolean Igazit2(int sugar) {

        int v2ujX = -1;
        int v2ujY = -1;

        for (int[] posicionRepartidor : oPanel1.PanelCamera.alignmentPoints(terminal.TerminalOrigami, snap2, snap3, snap4)) {

            if ((liner2X - oPanel1.PanelCamera.xshift - posicionRepartidor[0])
                    * (liner2X - oPanel1.PanelCamera.xshift - posicionRepartidor[0])
                    + (liner2Y - oPanel1.PanelCamera.yshift - posicionRepartidor[1])
                    * (liner2Y - oPanel1.PanelCamera.yshift - posicionRepartidor[1]) < sugar) {

                v2ujX = posicionRepartidor[0] + oPanel1.PanelCamera.xshift;
                v2ujY = posicionRepartidor[1] + oPanel1.PanelCamera.yshift;
                break;
            }
        }
        if (v2ujX != -1) {
            liner2X = v2ujX;
            liner2Y = v2ujY;
            return true;
        }
        return false;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private origamieditor3d.OrigamiPanel oPanel1;
    private javax.swing.JMenu ui_edit;
    private javax.swing.JMenuItem ui_edit_redo;
    private javax.swing.JMenuItem ui_edit_undo;
    private javax.swing.JMenu ui_file;
    private javax.swing.JMenu ui_file_export;
    private javax.swing.JMenuItem ui_file_export_crease;
    private javax.swing.JMenu ui_file_export_togif;
    private javax.swing.JMenuItem ui_file_export_togif_folding;
    private javax.swing.JMenuItem ui_file_export_togif_revolving;
    private javax.swing.JMenuItem ui_file_export_toopenctm;
    private javax.swing.JMenuItem ui_file_export_topdf;
    private javax.swing.JMenuItem ui_file_export_toself;
    private javax.swing.JMenu ui_file_new;
    private javax.swing.JMenuItem ui_file_new_a4;
    private javax.swing.JMenu ui_file_new_bases;
    private javax.swing.JMenuItem ui_file_new_dollar;
    private javax.swing.JMenuItem ui_file_new_hexagonal;
    private javax.swing.JMenuItem ui_file_new_square;
    private javax.swing.JMenuItem ui_file_open;
    private javax.swing.JMenuItem ui_file_properties;
    private javax.swing.JMenu ui_file_sample;
    private javax.swing.JMenuItem ui_file_save;
    private javax.swing.JMenuItem ui_file_saveas;
    private javax.swing.JMenu ui_help;
    private javax.swing.JMenuItem ui_help_about;
    private javax.swing.JMenu ui_tutorials;
    private javax.swing.JMenuItem ui_tutorials_internet;
    private javax.swing.JMenu ui_view;
    private javax.swing.JCheckBoxMenuItem ui_view_best;
    private javax.swing.JMenuItem ui_view_options;
    private javax.swing.JMenu ui_view_paper;
    private javax.swing.JCheckBoxMenuItem ui_view_paper_gradient;
    private javax.swing.JCheckBoxMenuItem ui_view_paper_image;
    private javax.swing.JCheckBoxMenuItem ui_view_paper_none;
    private javax.swing.JCheckBoxMenuItem ui_view_paper_plain;
    private javax.swing.JCheckBoxMenuItem ui_view_zoom;
    // End of variables declaration//GEN-END:variables

    private void defaultify() {

        EditorState = (/*SecondaryState = */ControlState.ESPERA);
        //targetOn = true;
        
        oPanel1.linerOff();
        oPanel1.reset();
        oPanel1.repaint();
        oPanel1.setToolTipText(oPanel1_tip1);
    }
}