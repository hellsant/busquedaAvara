package com.aisearch;

import java.util.Date;
import java.awt.event.*;
import java.awt.*;

public class AISearch extends Frame {

    private static final long serialVersionUID = 1L;

    Dimension pantalla;
    int ancho, alto, x, y;

    MenuBar MMenu;
    MenuItem MAbrir, MSalir, MAcerca, MLimpiar;
    Menu MTemporizador;
    CheckboxMenuItem CMITemporizador, CMIPaso, CMILeyenda;
    CheckboxMenuItem CMIMuyRapido, CMIRapido, CMIMedio, CMILento;
    Choice CBusqueda;
    Button BEjecutar, BReiniciar;
    TextArea TAInformacion;
    Panel PNorte, PSur;
    FileDialog FDAbrir;
    Dialog DAcerca;

    String ArchivoGrafo;
    private final Grafo grafo;
    private Grafo.Avara avara;

    // clase main
    public static void main(String arg[]) {
        // instancia una ventana
        AISearch aiSearch = new AISearch();
    }

    // constructor de la clase (ventana del programa)
    public AISearch() {
        // ventana principal
        super();
        this.setTitle("Busqueda de iglesias");
        this.setLayout(new BorderLayout());
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        // incorpora el controlador de eventos a la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // menu de la ventana principal
        MMenu = new MenuBar();
        Menu marchivo = new Menu("Archivo"), mver = new Menu("Vista"), mejecucion = new Menu("Ejecutar"), macerca = new Menu("Ayuda");
        MTemporizador = new Menu("Temporisador");
        MAbrir = new MenuItem("Abrir");
        MSalir = new MenuItem("Salir");
        MLimpiar = new MenuItem("limpiar");
        MAcerca = new MenuItem("informacion");
        CMILeyenda = new CheckboxMenuItem("Legenda de los Nodos");
        CMILeyenda.setState(true);
        CMIPaso = new CheckboxMenuItem("paso a paso");
        CMITemporizador = new CheckboxMenuItem("tiempo");
        CMIMuyRapido = new CheckboxMenuItem("veloz (0s)");
        CMIRapido = new CheckboxMenuItem("rapido (0.1s)");
        CMIMedio = new CheckboxMenuItem("Medio (0.5s)");
        CMILento = new CheckboxMenuItem("lento (1s)");
        CMIPaso.setState(true);
        CMITemporizador.setState(false);
        CMIMuyRapido.setState(false);
        CMIRapido.setState(false);
        CMIMedio.setState(false);
        CMILento.setState(true);
        MTemporizador.setEnabled(false);
        marchivo.add(MAbrir);
        marchivo.addSeparator();
        marchivo.add(MSalir);
        mver.add(CMILeyenda);
        mver.addSeparator();
        mver.add(MLimpiar);
        mejecucion.add(CMIPaso);
        mejecucion.add(CMITemporizador);
        mejecucion.addSeparator();
        mejecucion.add(MTemporizador);
        MTemporizador.add(CMIMuyRapido);
        MTemporizador.add(CMIRapido);
        MTemporizador.add(CMIMedio);
        MTemporizador.add(CMILento);
        macerca.add(MAcerca);
        MMenu.add(marchivo);
        MMenu.add(mver);
        MMenu.add(mejecucion);
        MMenu.add(macerca);
        MAbrir.addActionListener(new ControladorMenuItem(MAbrir));
        MSalir.addActionListener(new ControladorMenuItem(MSalir));
        MLimpiar.addActionListener(new ControladorMenuItem(MLimpiar));
        MAcerca.addActionListener(new ControladorMenuItem(MAcerca));
        CMILeyenda.addItemListener(new ControladorCheckBox(CMILeyenda));
        CMIPaso.addItemListener(new ControladorCheckBox(CMIPaso));
        CMITemporizador.addItemListener(new ControladorCheckBox(CMITemporizador));
        CMIMuyRapido.addItemListener(new ControladorCheckBox(CMIMuyRapido));
        CMIRapido.addItemListener(new ControladorCheckBox(CMIRapido));
        CMIMedio.addItemListener(new ControladorCheckBox(CMIMedio));
        CMILento.addItemListener(new ControladorCheckBox(CMILento));

        // ventana de abrir archivos
        FDAbrir = new FileDialog(this, "abrir archivo del grafico", FileDialog.LOAD);
        ArchivoGrafo = "";

        // ventana de acerca
        DAcerca = new Dialog(this, "infiormacion");
        DAcerca.setLayout(new GridLayout(12, 1));
        pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        ancho = 400;
        alto = 250;
        x = (pantalla.width - ancho) / 2;
        y = (pantalla.height - alto) / 2;
        DAcerca.setBounds(x, y, ancho, alto);
        DAcerca.setResizable(false);
        DAcerca.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DAcerca.setVisible(false);
            }
        });

        // incorpora los elementos al dialog
        DAcerca.add(new Label(""));
        DAcerca.add(new Label(""));
        DAcerca.add(new Label("proyecto de busqueda avara "));
        DAcerca.add(new Label(" UMSS"));
        DAcerca.add(new Label(" Inteligencia Artificial "));
        DAcerca.add(new Label(" 1-2018"));
        DAcerca.add(new Label("como recorrer la ciudad de una iglesia a otra"));
        DAcerca.add(new Label("o como tambien llegar de un punto a la iglesia"));
        DAcerca.add(new Label(""));

        // lista de seleccion
        CBusqueda = new Choice();
        CBusqueda.addItem("Avara");
        CBusqueda.addItemListener(new ControladorChoice(CBusqueda));
        CBusqueda.setEnabled(false);

        // botones
        BEjecutar = new Button(" sigiente paso ");
        BReiniciar = new Button("reset busqueda");
        BEjecutar.setEnabled(false);
        BReiniciar.setEnabled(false);
        BEjecutar.addActionListener(new ControladorButton(BEjecutar));
        BReiniciar.addActionListener(new ControladorButton(BReiniciar));

        // area de texto
        TAInformacion = new TextArea("", 5, 70, TextArea.SCROLLBARS_VERTICAL_ONLY);
        TAInformacion.setEditable(false);
        TAInformacion.setBackground(Color.white);

        // paneles contenedores
        PNorte = new Panel();
        PNorte.setLayout(new FlowLayout(FlowLayout.LEFT));
        PNorte.setBackground(Color.lightGray);
        PNorte.add(CBusqueda);
        PNorte.add(BEjecutar);
        PNorte.add(BReiniciar);
        PSur = new Panel();
        PSur.setLayout(new BorderLayout());
        PSur.setBackground(Color.lightGray);
        PSur.add("North", new Label("Panel de Mensajes"));
        PSur.add("Center", TAInformacion);

        // componente grafo extiende de panel
        grafo = new Grafo();
        grafo.setBackground(new Color(90, 138, 212));
        // incorpora componentes a la venta principal
        this.setMenuBar(MMenu);
        this.add("North", PNorte);
        this.add("Center", grafo);
        this.add("South", PSur);

        // muestra la ventana principal
        this.setVisible(true);
    }

    // clases para controlar eventos
    class ControladorMenuItem implements ActionListener {

        MenuItem mi;

        ControladorMenuItem(MenuItem mip) {
            mi = mip;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (mi.equals(MAbrir)) {
                FDAbrir.setVisible(true);
                if (FDAbrir.getFile() != null) {
                    ArchivoGrafo = FDAbrir.getDirectory() + FDAbrir.getFile();
                    setTitle("busqueda avara - " + FDAbrir.getFile());
                    try {
                        // carga el grafo del archivo
                        grafo.loadGrafo(ArchivoGrafo);
                        TAInformacion.append((new Date()).toString() + ": se cargo exitosamente el archivo.\n");
                        // por defecto primer algoritmo de busqueda
                        CBusqueda.select(0);
                        avara = null;
                        avara = grafo.new Avara();
                        TAInformacion.append("\n" + (new Date()).toString() + ": busqueda avara.\n");
                        // activa los controles
                        CBusqueda.setEnabled(true);
                        BEjecutar.setEnabled(true);
                        BReiniciar.setEnabled(true);
                        // redibuja el grafo
                        grafo.repaint();
                    } catch (Exception exception) {
                        CBusqueda.setEnabled(false);
                        BEjecutar.setEnabled(false);
                        BReiniciar.setEnabled(false);
                        TAInformacion.append((new Date()).toString() + ": no se pudo cargar el arcivole.\n");
                    }

                }
            } else if (mi.equals(MLimpiar)) {
                TAInformacion.setText("");
            } else if (mi.equals(MAcerca)) {
                DAcerca.setVisible(true);
            } else if (mi.equals(MSalir)) {
                System.exit(0);
            }
        }
    }

    class ControladorChoice implements ItemListener {

        Choice c;

        ControladorChoice(Choice cp) {
            c = cp;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            // resetea el grafo y los algoritmos
            grafo.reset();
            avara = null;
            // establece el algoritmo seleccionado
            switch (c.getSelectedIndex()) {
                case 0:
                    avara = grafo.new Avara();
                    TAInformacion.append("\n" + (new Date()).toString() + ": busqueda avara.\n");
                    break;

            }
            // redibuja el grafo
            grafo.repaint();
            // activa el boton de ejecucion
            BEjecutar.setEnabled(true);
        }
    }

    class ControladorCheckBox implements ItemListener {

        CheckboxMenuItem cmi;

        ControladorCheckBox(CheckboxMenuItem cmip) {
            cmi = cmip;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (cmi.equals(CMILeyenda)) {
                grafo.setVerLeyenda(cmi.getState());
                grafo.repaint();
            } else if (cmi.equals(CMITemporizador)) {
                CMITemporizador.setState(true);
                MTemporizador.setEnabled(true);
                CMIPaso.setState(false);
                BEjecutar.setLabel("inicio");
            } else if (cmi.equals(CMIPaso)) {
                CMIPaso.setState(true);
                CMITemporizador.setState(false);
                MTemporizador.setEnabled(false);
                BEjecutar.setLabel("siguiente paso");
            } else if (cmi.equals(CMIMuyRapido)) {
                CMIMuyRapido.setState(true);
                CMIRapido.setState(false);
                CMIMedio.setState(false);
                CMILento.setState(false);
                grafo.setTemporizador(0);
            } else if (cmi.equals(CMIRapido)) {
                CMIMuyRapido.setState(false);
                CMIRapido.setState(true);
                CMIMedio.setState(false);
                CMILento.setState(false);
                grafo.setTemporizador(100);
            } else if (cmi.equals(CMIMedio)) {
                CMIMuyRapido.setState(false);
                CMIRapido.setState(false);
                CMIMedio.setState(true);
                CMILento.setState(false);
                grafo.setTemporizador(500);
            } else if (cmi.equals(CMILento)) {
                CMIMuyRapido.setState(false);
                CMIRapido.setState(false);
                CMIMedio.setState(false);
                CMILento.setState(true);
                grafo.setTemporizador(1000);
            }
        }
    }

    class ControladorButton implements ActionListener {

        Button b;

        ControladorButton(Button bp) {
            b = bp;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            String cadena;
            String[] estadisticas;
            if (b.equals(BEjecutar)) {
                switch (CBusqueda.getSelectedIndex()) {
                    case 0:
                        if (CMIPaso.getState()) {
                            cadena = avara.getAbierta();
                            if (cadena != "") {
                                TAInformacion.append((new Date()).toString() + ": " + cadena + "\n");
                            }
                            cadena = avara.getCerrada();
                            if (cadena != "") {
                                TAInformacion.append((new Date()).toString() + ": " + cadena + "\n");
                            }
                            if (!avara.step()) {
                                // muestra estatidsticas
                                estadisticas = grafo.getEstadisticas();
                                TAInformacion.append((new Date()).toString() + ": Busqueda avara.\n");
                                TAInformacion.append((new Date()).toString() + ": " + estadisticas[0] + "\n");
                                TAInformacion.append((new Date()).toString() + ": " + estadisticas[1] + "\n");
                                TAInformacion.append((new Date()).toString() + ": " + estadisticas[2] + "\n");
                                TAInformacion.append((new Date()).toString() + ": " + estadisticas[3] + "\n");
                                // desactiva el boton de ejecucion
                                BEjecutar.setEnabled(false);
                            }
                        } else {
                            avara.run();
                            // muestra estatidsticas
                            estadisticas = grafo.getEstadisticas();
                            TAInformacion.append((new Date()).toString() + ": Busqueda avara.\n");
                            TAInformacion.append((new Date()).toString() + ": " + estadisticas[0] + "\n");
                            TAInformacion.append((new Date()).toString() + ": " + estadisticas[1] + "\n");
                            TAInformacion.append((new Date()).toString() + ": " + estadisticas[2] + "\n");
                            TAInformacion.append((new Date()).toString() + ": " + estadisticas[3] + "\n");
                            // desactiva el boton de ejecucion
                            BEjecutar.setEnabled(false);
                        }
                        break;
                }
            }
            if (b.equals(BReiniciar)) {
                grafo.reset();
                switch (CBusqueda.getSelectedIndex()) {
                    case 0:
                        avara.reset();
                        TAInformacion.append("\n" + (new Date()).toString() + ": Busqueda avara reiniciada.\n");
                        break;
                }
                grafo.repaint();
                // activa el boton de ejecucion
                BEjecutar.setEnabled(true);
            }
        }
    }
}
