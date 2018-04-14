package com.aisearch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.util.Vector;
import java.util.LinkedList;

public class Grafo extends Panel {

    private static final long serialVersionUID = 1L;

    private Vector<String> IndiceNodos;
    private Vector<Nodo> ListaNodos;
    private String NodoInicial;
    private String NodoObjetivo;

    private Boolean VerLeyenda;
    private long Temporizador;

    // constructor
    public Grafo() {
        // constructor padre
        super();
        // inicializa
        IndiceNodos = new Vector<>();
        ListaNodos = new Vector<>();
        NodoInicial = "";
        NodoObjetivo = "";
        VerLeyenda = true;
        Temporizador = 1000;
    }

    public Vector<Nodo> getListaNodos() {
        return ListaNodos;
    }

    public void setListaNodos(Vector<Nodo> ListaNodos) {
        this.ListaNodos = ListaNodos;
    }

    public Vector<String> getIndiceNodos() {
        return IndiceNodos;
    }

    public void setIndiceNodos(Vector<String> IndiceNodos) {
        this.IndiceNodos = IndiceNodos;
    }

    // metodo carga un grafo de un archivo
    public void loadGrafo(String configuracion) throws Exception {
        Nodo nodo;
        LeerNodos entrada;
        int numNodos, i;

        // inicializa el grafo
        ListaNodos.clear();
        IndiceNodos.clear();
        NodoInicial = "";
        NodoObjetivo = "";

        // lectura de datos
        entrada = new LeerNodos();
        numNodos = entrada.abrir(configuracion);
        // lee los nodos indicados en cabecera
        for (i = 0; i < numNodos; i++) {
            nodo = entrada.leer();
            // agrega la clave de busqueda del nodo
            IndiceNodos.add(nodo.toString());
            // agrega el nodo a la lista de nodos
            ListaNodos.add(nodo);
        }
        entrada.cerrar();
        // establece el nodo inicial
        NodoInicial = IndiceNodos.get(0);
        // lo selecciona como canditato a la basura
        nodo = null;
    }

    // metodo para reiniciar el grafo
    public void reset() {
        // reinicia elementos cambiantes de cada nodo
        int i;
        Nodo nodo;
        for (i = 0; i < ListaNodos.size(); i++) {
            nodo = ListaNodos.get(i);
            nodo.setEstado(Nodo.TEstado.NOGENERADO);
            nodo.setIdPuntero("");
            nodo.setCostePuntero(0);
            nodo.setCosteCamino(0);
        }
        nodo = null;
        NodoObjetivo = "";
    }

    // metodos para gestionar la finromacion del grafo
    public String getNodoInicial() {
        return NodoInicial;
    }

    public String getNodoObjetivo() {
        return NodoObjetivo;
    }

    public String[] getEstadisticas() {
        String cadena[] = new String[4];
        int temp;
        int nogenerado, abierto, cerrado;
        // inicializa
        nogenerado = 0;
        abierto = 0;
        cerrado = 0;
        // bucle para contar
        for (temp = 0; temp < ListaNodos.size(); temp++) {
            switch ((((Nodo) (ListaNodos.get(temp))).getEstado()).ordinal()) {
                case 0:
                    nogenerado++;
                    break;
                case 2:
                    abierto++;
                    break;
                case 3:
                    cerrado++;
                    break;
            }
        }
        // construye la cadena
        cadena[0] = "NOT GENERATED: " + Integer.toString(nogenerado)
                + " <not visited nodes>";
        cadena[1] = "OPENED: " + Integer.toString(abierto)
                + " <visited and not expanded nodes>";
        cadena[2] = "CLOSED: " + Integer.toString(cerrado)
                + " <visited and expanded nodes>";
        cadena[3] = "GENERATED: " + Integer.toString(abierto + cerrado)
                + " <total visited nodes>";

        return cadena;
    }

    public void setVerLeyenda(boolean VerLeyenda) {
        this.VerLeyenda = VerLeyenda;
    }

    public void setTemporizador(long Temporizador) {
        this.Temporizador = Temporizador;
    }

    // metodos para pintar
    @Override
    public void paint(Graphics g) {
        paintGrafo();
        paintLeyenda();
    }

    private void paintLeyenda() {
        if (VerLeyenda) {
            // Dibuja la leyenda
            Graphics g = this.getGraphics();
            int esquinax = this.getWidth() - 115;
            int esquinay = 15;
            g.setColor(new Color(255, 255, 225));
            g.fillRect(esquinax, esquinay, 100, 95);
            g.setColor(Color.black);
            g.drawRect(esquinax, esquinay, 100, 95);
            Font fuente = g.getFont();
            g.setFont(new Font(fuente.getFontName(), Font.BOLD, fuente
                    .getSize()));
            g.setColor(Color.black);
            g.drawString("legenda:", esquinax + 5, esquinay + 15);
            g.setFont(fuente);
            g.setColor(Color.gray);
            g.drawString("No visitado", esquinax + 20, esquinay + 32);
            g.fillRect(esquinax + 10, esquinay + 26, 5, 5);
            g.setColor(Color.red.darker());
            g.drawString("Actual", esquinax + 20, esquinay + 48);
            g.fillRect(esquinax + 10, esquinay + 42, 5, 5);
            g.setColor(Color.green.darker());
            g.drawString("Abierto", esquinax + 20, esquinay + 65);
            g.fillRect(esquinax + 10, esquinay + 59, 5, 5);
            g.setColor(Color.orange.darker());
            g.drawString("Cerrado", esquinax + 20, esquinay + 82);
            g.fillRect(esquinax + 10, esquinay + 76, 5, 5);
        }
    }

    private void paintGrafo() {
        if (ListaNodos.size() > 0) {
            paintArcos();
            paintCamino();
            paintNodos();
        }
    }

    private void paintNodos() {
        Nodo nodo;
        int i, n;
        Graphics g = this.getGraphics();
        // recorre los nodos
        n = ListaNodos.size();
        // pinta el el grafo
        for (i = 0; i < n; i++) {
            nodo = ListaNodos.get(i);
            // pinta el nodo actual
            nodo.pintarNodo(g);
        }
        nodo = null;
    }

    private void paintArco(String IdOrigen, String IdDestino, Color color, double Coste) {
        Nodo nodo;
        int numero;
        int xo, yo, xd, yd;
        int xc, yc;

        // resuelve el nodo orgien
        numero = IndiceNodos.indexOf(IdOrigen);
        nodo = ListaNodos.get(numero);
        xo = nodo.getX();
        yo = nodo.getY();

        // resuelve el nodo destino
        numero = IndiceNodos.indexOf(IdDestino);
        nodo = ListaNodos.get(numero);
        xd = nodo.getX();
        yd = nodo.getY();

        // pinta el arco
        Graphics g = this.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(color);

        // pinta la linea y la flecha
        xc = (int) ((distanciaCoords(xo, yo, xd, yo) * 10) / distanciaCoords(
                xo, yo, xd, yd));
        yc = (int) ((distanciaCoords(xo, yo, xo, yd) * 10) / distanciaCoords(
                xo, yo, xd, yd));
        if ((xo > xd) && (yo <= yd)) {
            xc = xd + xc;
            yc = yd - yc;
        } else if ((xo <= xd) && (yo > yd)) {
            xc = xd - xc;
            yc = yd + yc;
        } else if ((xo <= xd) && (yo <= yd)) {
            xc = xd - xc;
            yc = yd - yc;
        } else if ((xo > xd) && (yo > yd)) {
            xc = xd + xc;
            yc = yd + yc;
        }
        drawArrow(g2, xo, yo, xc, yc);

        // pita el coste
        g2.setColor(color);
        xc = (xo + xd) / 2;
        yc = (yo + yd) / 2;
        g2.drawString(Double.toString(Coste), xc, yc);

        // objetos usados para la basura
        nodo = null;
    }

    private void paintArcos() {
        Nodo nodo;
        int i, j, n, m;
        // recorre los nodos
        n = ListaNodos.size();
        // pinta el el grafo
        for (i = 0; i < n; i++) {
            nodo = ListaNodos.get(i);
            // recorre los sucesores
            m = nodo.maxSucesores();
            for (j = 0; j < m; j++) {
                // pinta el arco
                paintArco(nodo.toString(), nodo.getIdSucesor(j), Color.white,
                        nodo.getCosteSucesor(j));
            }
        }
        // objetos usados para la basura
        nodo = null;
    }

    private void paintCamino() {
        // variables para el procesamiento
        int n;
        double c;
        Nodo no, nd;

        if (!"".equals(NodoObjetivo)) {
            // localiza el nodo objetivo
            n = IndiceNodos.indexOf(NodoObjetivo);
            nd = ListaNodos.get(n);
            // pinta el camino que tenga
            while (!"".equals(nd.getIdPuntero())) {
                n = IndiceNodos.indexOf(nd.getIdPuntero());
                no = ListaNodos.get(n);
                c = nd.getCostePuntero();
                paintArco(no.toString(), nd.toString(), Color.red, c);
                nd = no;
            }
        }
        // objetos usados para la basura
        nd = null;
        no = null;
    }

    // Clase para el algoritmo: Primero el mejor
    public class Avara {
        // variables del algoritmo

        int j, n, m, s;
        int Step;
        String miObjetivo;
        Nodo nodo, suc;
        Vector<String> abiertos;
        Vector<String> cerrados;
        Graphics g;

        // inicializa el algoritmo
        public Avara() {
            // inicializa el algoritmo en caso de que haya uno cargado
            if (ListaNodos.size() > 0) {
                inicio();
            }
        }

        // metodos para gestionar variables
        public String getAbierta() {
            int temp;
            String cadena;
            int itemp;
            Nodo ntemp;
            if (Step == 0) {
                cadena = "OPENED={";
                for (temp = 0; temp < abiertos.size(); temp++) {
                    cadena += abiertos.get(temp) + "(";
                    itemp = IndiceNodos.indexOf(abiertos.get(temp));
                    ntemp = ListaNodos.get(itemp);
                    cadena += Double.toString(ntemp.getValor());
                    cadena += ")";
                    if (temp < abiertos.size() - 1) {
                        cadena += ", ";
                    }
                }
                cadena += "}";
            } else {
                cadena = "";
            }
            return cadena;
        }

        public String getCerrada() {
            int temp;
            String cadena;
            if (Step == 0) {
                cadena = "CLOSED={";
                for (temp = 0; temp < cerrados.size(); temp++) {
                    cadena += cerrados.get(temp);
                    if (temp < cerrados.size() - 1) {
                        cadena += ", ";
                    }
                }
                cadena += "}";
            } else {
                cadena = "";
            }
            return cadena;
        }

        // reinicia el algoritmo
        public void reset() {
            // inicia el algoritmo;
            if (ListaNodos.size() > 0) {
                inicio();
            }
        }

        // ejecuta el algoritmo en modo temporizador
        public void run() {
            while (Step < 2) {
                switch (Step) {
                    case 0:
                        paso0();
                        break;
                    case 1:
                        paso1();
                        break;
                    default:
                        Grafo.this.NodoObjetivo = miObjetivo;
                        Grafo.this.paintGrafo();
                }
                // retardo de un segundo
                try {
                    Thread.sleep(Temporizador);
                } catch (Exception e) {
                }
            }
            Grafo.this.NodoObjetivo = miObjetivo;
            Grafo.this.paintGrafo();
        }

        // ejecuta el algoritmo en modo paso por paso
        public boolean step() {
            boolean ejecutandose = true;
            switch (Step) {
                case 0:
                    paso0();
                    break;
                case 1:
                    paso1();
                    break;
                default: {
                    ejecutandose = false;
                    Grafo.this.NodoObjetivo = miObjetivo;
                    Grafo.this.paintGrafo();
                }
            }
            return ejecutandose;
        }

        // metodos para desglosar el algoritmo en pasos
        private void inicio() {
            // inicializa el objetivo
            miObjetivo = "";
            // inicializa variables para explorar sucesores
            j = 0;
            m = 0;
            nodo = null;
            suc = null;
            // inicializa objetos
            abiertos = new Vector<String>();
            cerrados = new Vector<String>();
            g = Grafo.this.getGraphics();
            // comienza por el primer nodo
            nodo = ListaNodos.get(0);
            // encola el primer nodo
            abiertos.add(nodo.toString());
            // siguiente paso
            Step = 0;
        }

        private void paso0() {
            if (abiertos.size() != 0) {
                // extrae el primer nodo de la pila abiertos
                n = IndiceNodos.indexOf(abiertos.remove(0));
                nodo = ListaNodos.get(n);
                nodo.setEstado(Nodo.TEstado.ACTUAL);
                nodo.pintarNodo(g);
                if (!nodo.getEsObjetivo()) {
                    // se inserta en la lista de cerrados
                    cerrados.add(nodo.toString());
                    // se prepara para explorar los sucesores
                    m = nodo.maxSucesores();
                    j = 0;
                    // siguiente paso
                    Step = 1;
                } else {
                    // establece el objetivo encontrado
                    miObjetivo = nodo.toString();
                    // termina con exito
                    Step = 2;
                }
            } else {
                // termina con fracaso
                Step = 2;
            }
        }

        private void paso1() {
            if (j < m) {
                // obtiene el nodo sucesor
                s = IndiceNodos.indexOf(nodo.getIdSucesor(j));
                suc = ListaNodos.get(s);
                if (suc.getEstado() == Nodo.TEstado.NOGENERADO) {
                    // establece puntero a nodo si el sucesor no se ha generado
                    suc.setIdPuntero(nodo.toString());
                    suc.setCostePuntero(nodo.getCosteSucesor(j));
                    suc.setCosteCamino(nodo.getCosteSucesor(j)
                            + nodo.getCosteCamino());
                    // inserta el elemento en la lista ABIERTOS
                    suc.setEstado(Nodo.TEstado.ABIERTO);
                    suc.pintarNodo(g);
                    // inserta el sucesor en la lista de abiertos
                    abiertos.add(suc.toString());
                } else if ((suc.getEstado() == Nodo.TEstado.ABIERTO)) {
                    // modifica el puntero del sucesor a nodo
                    suc.setIdPuntero(nodo.toString());
                    suc.setCostePuntero(nodo.getCosteSucesor(j));
                    suc.setCosteCamino(nodo.getCosteSucesor(j)
                            + nodo.getCosteCamino());
                    suc.pintarNodo(g);
                } else if ((suc.getEstado() == Nodo.TEstado.CERRADO)) {
                    // no se que hacer
                }
                j++;
            } else {
                if (abiertos.size() != 0) {
                    // pinta el nodo
                    nodo.setEstado(Nodo.TEstado.CERRADO);
                    nodo.pintarNodo(g);
                    // reordena la lista de abiertos en funcion de: f = h + g
                    reordenar(abiertos);
                    // siguiente paso
                    Step = 0;
                } else {
                    // termina con fracaso
                    Step = 2;
                }
            }
        }

        private void reordenar(Vector<String> vector) {
            // Ordenacion por seleccion
            Nodo ntemp;
            int itemp;
            String nodoi, nodoj, minn;
            double valori, valorj;
            int minj;
            double minx;
            int N = vector.size();
            int i, j;
            for (i = 0; i < N - 1; i++) {
                nodoi = (String) vector.get(i);
                itemp = IndiceNodos.indexOf(nodoi);
                ntemp = ListaNodos.get(itemp);
                valori = ntemp.getValor();

                minj = i;
                minx = valori;
                minn = nodoi;
                for (j = i; j < N; j++) {
                    nodoj = vector.get(j);
                    itemp = IndiceNodos.indexOf(nodoj);
                    ntemp = ListaNodos.get(itemp);
                    valorj = ntemp.getValor();
                    if (valorj <= minx) {
                        minj = j;
                        minx = valorj;
                        minn = nodoj;
                    }
                }
                vector.set(minj, nodoi);
                vector.set(i, minn);
            }
        }
    }
   

    // METODOS PARA PINTAR FLECHAS
    int al = 10; // Arrow length
    int aw = 9; // Arrow width
    int haw = aw / 2; // Half arrow width
    int xValues[] = new int[3];
    int yValues[] = new int[3];

    public void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        // Draw line
        g.drawLine(x1, y1, x2, y2);
        // Calculate x-y values for arrow head
        calcValues(x1, y1, x2, y2);
        g.fillPolygon(xValues, yValues, 3);
    }

    /* CALC VALUES: Calculate x-y values. */
    public void calcValues(int x1, int y1, int x2, int y2) {
        // North or south
        if (x1 == x2) {
            // North
            if (y2 < y1) {
                arrowCoords(x2, y2, x2 - haw, y2 + al, x2 + haw, y2 + al);
            } // South
            else {
                arrowCoords(x2, y2, x2 - haw, y2 - al, x2 + haw, y2 - al);
            }
            return;
        }
        // East or West
        if (y1 == y2) {
            // East
            if (x2 > x1) {
                arrowCoords(x2, y2, x2 - al, y2 - haw, x2 - al, y2 + haw);
            } // West
            else {
                arrowCoords(x2, y2, x2 + al, y2 - haw, x2 + al, y2 + haw);
            }
            return;
        }
        // Calculate quadrant
        calcValuesQuad(x1, y1, x2, y2);
    }

    /*
	 * CALCULATE VALUES QUADRANTS: Calculate x-y values where direction is not
	 * parallel to eith x or y axis.
     */
    public void calcValuesQuad(int x1, int y1, int x2, int y2) {
        double arrowAng = Math.toDegrees(Math.atan((double) haw / (double) al));
        double dist = Math.sqrt(al * al + aw);
        double lineAng = Math.toDegrees(Math.atan(((double) Math.abs(x1 - x2))
                / ((double) Math.abs(y1 - y2))));
        // Adjust line angle for quadrant
        if (x1 > x2) {
            // South East
            if (y1 > y2) {
                lineAng = 180.0 - lineAng;
            }
        } else {
            // South West
            if (y1 > y2) {
                lineAng = 180.0 + lineAng;
            } // North West
            else {
                lineAng = 360.0 - lineAng;
            }
        }
        // Calculate coords
        xValues[0] = x2;
        yValues[0] = y2;
        calcCoords(1, x2, y2, dist, lineAng - arrowAng);
        calcCoords(2, x2, y2, dist, lineAng + arrowAng);
    }

    /*
	 * CALCULATE COORDINATES: Determine new x-y coords given a start x-y and a
	 * distance and direction
     */
    public void calcCoords(int index, int x, int y, double dist, double dirn) {
        while (dirn < 0.0) {
            dirn = 360.0 + dirn;
        }
        while (dirn > 360.0) {
            dirn = dirn - 360.0;
        }
        // North-East
        if (dirn <= 90.0) {
            xValues[index] = x + (int) (Math.sin(Math.toRadians(dirn)) * dist);
            yValues[index] = y - (int) (Math.cos(Math.toRadians(dirn)) * dist);
            return;
        }
        // South-East
        if (dirn <= 180.0) {
            xValues[index] = x
                    + (int) (Math.cos(Math.toRadians(dirn - 90)) * dist);
            yValues[index] = y
                    + (int) (Math.sin(Math.toRadians(dirn - 90)) * dist);
            return;
        }
        // South-West
        if (dirn <= 90.0) {
            xValues[index] = x
                    - (int) (Math.sin(Math.toRadians(dirn - 180)) * dist);
            yValues[index] = y
                    + (int) (Math.cos(Math.toRadians(dirn - 180)) * dist);
        } // Nort-West
        else {
            xValues[index] = x
                    - (int) (Math.cos(Math.toRadians(dirn - 270)) * dist);
            yValues[index] = y
                    - (int) (Math.sin(Math.toRadians(dirn - 270)) * dist);
        }
    }

    // ARROW COORDS: Load x-y value arrays */
    public void arrowCoords(int x1, int y1, int x2, int y2, int x3, int y3) {
        xValues[0] = x1;
        yValues[0] = y1;
        xValues[1] = x2;
        yValues[1] = y2;
        xValues[2] = x3;
        yValues[2] = y3;
    }

    // DISTANCIA ENTRE DOS PUNTOS
    public double distanciaCoords(int xo, int yo, int xd, int yd) {
        return Math.sqrt(Math.pow(xd - xo, 2) + Math.pow(yd - yo, 2));
    }
}
