import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ProyectoOrdenamiento extends JFrame {

    int[] arreglo = new int[10];

    int pos1 = -1; 
    int pos2 = -1; 

    int comparaciones = 0;
    int intercambios = 0;
    int tiempoPausa = 400; 

    PanelDibujo lienzo;
    JTextArea txtConsola;
    JLabel lblContadores;
    JButton btnBurbuja, btnInsercion, btnSeleccion;

    public ProyectoOrdenamiento() {
        setTitle("Mini Tienda - Control y Ordenamiento de Folios de Venta (Corte de Caja)");
        setSize(880, 590);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new Color(230, 235, 240));

        btnBurbuja = new JButton("Burbuja");
        btnInsercion = new JButton("Inserción");
        btnSeleccion = new JButton("Selección");

        panelBotones.add(btnBurbuja);
        panelBotones.add(btnInsercion);
        panelBotones.add(btnSeleccion);

        add(panelBotones, BorderLayout.NORTH);

        lienzo = new PanelDibujo();
        add(lienzo, BorderLayout.CENTER);

        JPanel panelAbajo = new JPanel(new BorderLayout());
        lblContadores = new JLabel("  Comparaciones de folios: 0   |   Intercambios de posición: 0");
        lblContadores.setFont(new Font("Arial", Font.BOLD, 13));
        lblContadores.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        txtConsola = new JTextArea(8, 50);
        txtConsola.setEditable(false);
        txtConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtConsola);

        panelAbajo.add(lblContadores, BorderLayout.NORTH);
        panelAbajo.add(scroll, BorderLayout.CENTER);

        add(panelAbajo, BorderLayout.SOUTH);

        btnBurbuja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> metodoBurbuja()).start();
            }
        });

        btnInsercion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> metodoInsercion()).start();
            }
        });

        btnSeleccion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> metodoSeleccion()).start();
            }
        });

        llenarDatos();
    }

    class PanelDibujo extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            setBackground(Color.WHITE);

            int xInicial = 60;
            int anchoBarra = 55;
            int separacion = 75;
            int baseY = 260;

            for (int i = 0; i < arreglo.length; i++) {
                int x = xInicial + (i * separacion);
                int altura = arreglo[i] * 2;
                int y = baseY - altura;

                if (i == pos1) {
                    g.setColor(new Color(255, 102, 102)); 
                } else if (i == pos2) {
                    g.setColor(new Color(255, 230, 102)); 
                } else {
                    g.setColor(new Color(135, 206, 235)); 
                }

                g.fillRect(x, y, anchoBarra, altura);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, anchoBarra, altura);
                g.drawString("F-" + arreglo[i], x + 10, y - 6);

                int yCaja = baseY + 20;
                g.setColor(new Color(240, 240, 240));
                g.fillRect(x, yCaja, anchoBarra, 35);
                g.setColor(Color.BLACK);
                g.drawRect(x, yCaja, anchoBarra, 35);

                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("#" + arreglo[i], x + 16, yCaja + 22);
                g.setFont(new Font("Arial", Font.PLAIN, 11));
                g.drawString("[T-" + i + "]", x + 16, yCaja + 52);
            }

            g.setFont(new Font("Arial", Font.ITALIC, 12));
            g.drawString("Registro de folios de tickets en memoria (Turnos de llegada T-0 a T-9):", 60, baseY + 90);
        }
    }

    public void esperar() {
        try {
            lienzo.repaint();
            lblContadores.setText("  Comparaciones de folios: " + comparaciones + "   |   Intercambios de posición: " + intercambios);
            Thread.sleep(tiempoPausa);
        } catch (Exception e) {
            System.out.println("Error en la pausa de visualización");
        }
    }

    public boolean estaOrdenado() {
        for (int i = 0; i < arreglo.length - 1; i++) {
            if (arreglo[i] > arreglo[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public void metodoBurbuja() {
        activarBotones(false);
        if (estaOrdenado()) {
            llenarDatos();
            esperar();
        }

        comparaciones = 0;
        intercambios = 0;
        txtConsola.append("\n=======================================================\n");
        txtConsola.append(">>> INICIANDO ORDENAMIENTO BURBUJA (CORTE DE CAJA) <<<\n");
        txtConsola.append("=======================================================\n");

        int n = arreglo.length;
        int aux = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                pos1 = j;
                pos2 = j + 1;
                comparaciones++;
                txtConsola.append("Comparando Folio #" + arreglo[j] + " [Ticket " + j + "] con Folio #" + arreglo[j + 1] + " [Ticket " + (j + 1) + "]\n");
                esperar();

                if (arreglo[j] > arreglo[j + 1]) {
                    aux = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = aux;

                    intercambios++;
                    txtConsola.append(" -> Reubicación: Folio #" + arreglo[j + 1] + " era mayor, intercambiando con Folio #" + arreglo[j] + "\n");
                    esperar();
                }
            }
        }
        pos1 = -1;
        pos2 = -1;
        lienzo.repaint();
        txtConsola.append(">>> Fin de Ordenamiento Burbuja: Folios listos y ordenados de menor a mayor. <<<\n");
        activarBotones(true);
    }

    public void metodoInsercion() {
        activarBotones(false);
        if (estaOrdenado()) {
            llenarDatos();
            esperar();
        }

        comparaciones = 0;
        intercambios = 0;
        txtConsola.append("\n==========================================================\n");
        txtConsola.append(">>> INICIANDO ORDENAMIENTO POR INSERCIÓN (CORTE DE CAJA) <<<\n");
        txtConsola.append("==========================================================\n");

        for (int i = 1; i < arreglo.length; i++) {
            int actual = arreglo[i];
            int j = i - 1;
            pos1 = i;
            txtConsola.append("Analizando Folio #" + actual + " del Ticket [" + i + "] para insertarlo en su orden correspondiente...\n");
            esperar();

            while (j >= 0 && arreglo[j] > actual) {
                comparaciones++;
                pos2 = j;
                arreglo[j + 1] = arreglo[j]; 
                intercambios++;
                txtConsola.append(" -> Folio #" + arreglo[j] + " es mayor que Folio #" + actual + "; desplazando a Ticket [" + (j + 1) + "]\n");
                esperar();
                j--;
            }
            if (j >= 0) {
                comparaciones++; 
            }
            arreglo[j + 1] = actual;
            pos1 = j + 1;
            txtConsola.append(" -> Folio #" + actual + " insertado correctamente en el Ticket [" + (j + 1) + "]\n");
            esperar();
        }
        pos1 = -1;
        pos2 = -1;
        lienzo.repaint();
        txtConsola.append(">>> Fin de Inserción: Lista de folios ordenada para revisión de pagos. <<<\n");
        activarBotones(true);
    }

    public void metodoSeleccion() {
        activarBotones(false);
        if (estaOrdenado()) {
            llenarDatos();
            esperar();
        }

        comparaciones = 0;
        intercambios = 0;
        txtConsola.append("\n==========================================================\n");
        txtConsola.append(">>> INICIANDO ORDENAMIENTO POR SELECCIÓN (CORTE DE CAJA) <<<\n");
        txtConsola.append("==========================================================\n");

        for (int i = 0; i < arreglo.length - 1; i++) {
            int menor = i;
            pos1 = i;
            txtConsola.append("Buscando el folio menor a partir del Ticket [" + i + "]...\n");

            for (int j = i + 1; j < arreglo.length; j++) {
                pos2 = j;
                comparaciones++;
                esperar();

                if (arreglo[j] < arreglo[menor]) {
                    menor = j;
                    pos1 = menor;
                    txtConsola.append(" -> Menor folio detectado momentáneamente: Folio #" + arreglo[menor] + " en Ticket [" + menor + "]\n");
                }
            }

            if (menor != i) {
                int temp = arreglo[i];
                arreglo[i] = arreglo[menor];
                arreglo[menor] = temp;
                intercambios++;
                txtConsola.append(" -> Intercambio definitivo: Colocando Folio #" + arreglo[i] + " en el Ticket [" + i + "]\n");
                esperar();
            }
        }
        pos1 = -1;
        pos2 = -1;
        lienzo.repaint();
        txtConsola.append(">>> Fin de Selección: Folios de menor a mayor para el arqueo de caja. <<<\n");
        activarBotones(true);
    }

    public void llenarDatos() {
        Random r = new Random();
        txtConsola.setText("=== REGISTRO DEL DÍA: LLEGADA DE CLIENTES EN LA TIENDA ===\n");
        txtConsola.append("Se generaron 10 folios de tickets en desorden conforme fueron llegando los clientes:\n");

        for (int i = 0; i < arreglo.length; i++) {
            arreglo[i] = r.nextInt(85) + 15;
            txtConsola.append("Ticket[" + i + "]=#" + arreglo[i] + "  ");
        }
        txtConsola.append("\n\nSeleccione un método de ordenamiento para preparar el corte de caja.\n");

        pos1 = -1;
        pos2 = -1;
        comparaciones = 0;
        intercambios = 0;
        lblContadores.setText("  Comparaciones de folios: 0   |   Intercambios de posición: 0");
        lienzo.repaint();
    }

    public void activarBotones(boolean estado) {
        btnBurbuja.setEnabled(estado);
        btnInsercion.setEnabled(estado);
        btnSeleccion.setEnabled(estado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProyectoOrdenamiento ventana = new ProyectoOrdenamiento();
            ventana.setVisible(true);
        });
    }
}