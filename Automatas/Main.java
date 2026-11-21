import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {

    private enum Modo { AFD, PILA, LBA, TURING }

    private static final Color FONDO_APP = new Color(238, 242, 247);
    private static final Color TARJETA = Color.WHITE;
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color AZUL_SUAVE = new Color(219, 234, 254);
    private static final Color MORADO = new Color(109, 40, 217);
    private static final Color MORADO_SUAVE = new Color(237, 233, 254);
    private static final Color AMBAR = new Color(217, 119, 6);
    private static final Color AMBAR_SUAVE = new Color(254, 243, 199);
    private static final Color VERDE = new Color(21, 128, 61);
    private static final Color VERDE_SUAVE = new Color(220, 252, 231);
    private static final Color ROJO = new Color(185, 28, 28);
    private static final Color CONSOLA_BG = new Color(15, 23, 42);
    private static final Color CONSOLA_TXT = new Color(226, 232, 240);

    private final CardLayout cards = new CardLayout();
    private final JPanel vistas = new JPanel(cards);
    private final Map<Modo, JButton> menuBtns = new EnumMap<>(Modo.class);
    private final JLabel badgeNivel = new JLabel();

    public Main() {
        setTitle("Jerarquía de Chomsky — Simulador Interactivo");
        setSize(1120, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(FONDO_APP);

        add(crearHeader(), BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(4, 1, 10, 10));
        menu.setPreferredSize(new Dimension(245, 0));
        menu.setBackground(FONDO_APP);
        menu.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 4));
        add(menu, BorderLayout.WEST);

        vistas.setBackground(FONDO_APP);
        vistas.setBorder(BorderFactory.createEmptyBorder(2, 4, 10, 10));
        add(vistas, BorderLayout.CENTER);

        agregarModo(menu, Modo.AFD, "Nivel 3 · Regular", "AFD · Termina en \"ab\"", new String[][]{
                {"ab (Válida)", "ab"}, {"babab (Válida)", "babab"}, {"a (Inválida)", "a"}, {"aba (Inválida)", "aba"}
        });
        agregarModo(menu, Modo.PILA, "Nivel 2 · Libre Contexto", "Pila LIFO · aⁿbⁿ", new String[][]{
                {"ab (Válida)", "ab"}, {"aaabbb (Válida)", "aaabbb"}, {"aab (Inválida)", "aab"}, {"abba (Inválida)", "abba"}
        });
        agregarModo(menu, Modo.LBA, "Nivel 1 · Sensible Contexto", "LBA · aⁿbⁿcⁿ [ ]", new String[][]{
                {"abc (Válida)", "abc"}, {"aabbcc (Válida)", "aabbcc"}, {"aabcc (Inválida)", "aabcc"}, {"acb (Inválida)", "acb"}
        });
        agregarModo(menu, Modo.TURING, "Nivel 0 · Máquina Turing", "Suma unaria · Cinta B", new String[][]{
                {"11+11 (4)", "11+11"}, {"111+11 (5)", "111+11"}, {"1+111 (4)", "1+111"}, {"11++1 (Error)", "11++1"}
        });

        irA(Modo.AFD);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), 0, new Color(30, 58, 138)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        JLabel tit = new JLabel("Jerarquía de Chomsky · Simulador Interactivo");
        tit.setFont(new Font("SansSerif", Font.BOLD, 19));
        tit.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Visualización paso a paso de lenguajes y modelos de cómputo");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(191, 219, 254));
        textos.add(tit);
        textos.add(sub);

        badgeNivel.setFont(new Font("SansSerif", Font.BOLD, 12));
        badgeNivel.setOpaque(true);
        badgeNivel.setBackground(new Color(59, 130, 246));
        badgeNivel.setForeground(Color.WHITE);
        badgeNivel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        header.add(textos, BorderLayout.WEST);
        header.add(badgeNivel, BorderLayout.EAST);
        return header;
    }

    private void agregarModo(JPanel menu, Modo modo, String titulo, String subtitulo, String[][] ejemplos) {
        JButton btn = new JButton("<html><b>" + titulo + "</b><br><span style='font-size:11px'>" + subtitulo + "</span></html>");
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> irA(modo));
        menuBtns.put(modo, btn);
        menu.add(btn);
        vistas.add(new PanelSimulador(modo, titulo + " · " + subtitulo, ejemplos), modo.name());
    }

    private void irA(Modo modo) {
        cards.show(vistas, modo.name());
        badgeNivel.setText("ACTIVO: " + modo.name());
        menuBtns.forEach((m, b) -> {
            boolean act = m == modo;
            b.setBackground(act ? AZUL : TARJETA);
            b.setForeground(act ? Color.WHITE : new Color(30, 41, 59));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(act ? new Color(29, 78, 216) : new Color(203, 213, 225), act ? 2 : 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
        });
    }

    private static class PanelSimulador extends JPanel {
        private final Modo modo;
        private final JTextField txtEntrada = new JTextField(14);
        private final JLabel lblEstado = new JLabel();
        private final JLabel lblResultado = new JLabel();
        private final JTextArea log = new JTextArea(8, 45);
        private final JButton btnPaso = new JButton("Siguiente paso");
        private final JButton btnTodo = new JButton("Ejecutar todo");
        private final JButton btnReset = new JButton("Reiniciar");

        private final LienzoAfd lienzoAfd = new LienzoAfd();
        private final LienzoPila lienzoPila = new LienzoPila();
        private final LienzoCinta lienzoCinta = new LienzoCinta();

        private String cadena = "", evaluada = "", estado = "", fase = "", dir = "DERECHA";
        private int idx = 0, cabezal = 1, paso = 0;
        private boolean fin = false, error = false;
        private final Deque<String> pila = new ArrayDeque<>();
        private final List<String> cinta = new ArrayList<>();

        PanelSimulador(Modo modo, String titulo, String[][] ejemplos) {
            this.modo = modo;
            setLayout(new BorderLayout(10, 10));
            setBackground(FONDO_APP);

            JPanel top = new JPanel(new GridLayout(3, 1, 6, 6));
            top.setBackground(TARJETA);
            top.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));

            JLabel lblTit = new JLabel(titulo + "  ·  " + descripcionModo(modo));
            lblTit.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTit.setForeground(new Color(15, 23, 42));

            btnPaso.setBackground(AZUL);
            btnPaso.setForeground(Color.WHITE);
            btnPaso.setFocusPainted(false);
            btnTodo.setBackground(MORADO_SUAVE);
            btnTodo.setForeground(MORADO);
            btnTodo.setFocusPainted(false);
            btnReset.setBackground(AMBAR_SUAVE);
            btnReset.setForeground(new Color(146, 64, 14));
            btnReset.setFocusPainted(false);

            txtEntrada.setFont(new Font("Monospaced", Font.BOLD, 14));
            txtEntrada.setText(ejemplos[1][1]);
            txtEntrada.addActionListener(e -> ejecutarTodo());

            JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            fila1.setBackground(TARJETA);
            fila1.add(new JLabel("Entrada:"));
            fila1.add(txtEntrada);
            fila1.add(btnPaso);
            fila1.add(btnTodo);
            fila1.add(btnReset);

            JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            fila2.setBackground(TARJETA);
            fila2.add(new JLabel("Casos:"));
            for (String[] ej : ejemplos) {
                JButton b = new JButton(ej[0]);
                b.setBackground(new Color(241, 245, 249));
                b.setFocusPainted(false);
                b.addActionListener(e -> { txtEntrada.setText(ej[1]); reiniciar(); });
                fila2.add(b);
            }
            top.add(lblTit); top.add(fila1); top.add(fila2);
            add(top, BorderLayout.NORTH);

            JPanel centro = new JPanel(new BorderLayout(10, 10));
            centro.setBackground(FONDO_APP);

            JPanel visual = new JPanel(new BorderLayout(8, 8));
            visual.setBackground(TARJETA);
            visual.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            if (modo == Modo.AFD) visual.add(lienzoAfd, BorderLayout.CENTER);
            else if (modo == Modo.PILA) visual.add(lienzoPila, BorderLayout.CENTER);
            else visual.add(lienzoCinta, BorderLayout.CENTER);

            JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
            barra.setBackground(new Color(248, 250, 252));
            barra.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
            lblEstado.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblEstado.setForeground(AZUL);
            lblResultado.setFont(new Font("SansSerif", Font.BOLD, 14));
            barra.add(lblEstado);
            barra.add(lblResultado);
            visual.add(barra, BorderLayout.SOUTH);

            log.setEditable(false);
            log.setFont(new Font("Monospaced", Font.PLAIN, 13));
            log.setBackground(CONSOLA_BG);
            log.setForeground(CONSOLA_TXT);
            log.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            JScrollPane scroll = new JScrollPane(log);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

            centro.add(visual, BorderLayout.NORTH);
            centro.add(scroll, BorderLayout.CENTER);
            add(centro, BorderLayout.CENTER);

            btnPaso.addActionListener(e -> paso());
            btnTodo.addActionListener(e -> ejecutarTodo());
            btnReset.addActionListener(e -> reiniciar());
            reiniciar();
        }

        private String descripcionModo(Modo m) {
            return switch (m) {
                case AFD -> "Σ={a,b}, termina en \"ab\"";
                case PILA -> "L={aⁿbⁿ : n≥1}, memoria LIFO";
                case LBA -> "L={aⁿbⁿcⁿ : n≥1}, cinta acotada [ ]";
                case TURING -> "Suma unaria 1ᵐ+1ᵏ, cinta extendible {1,+,B}";
            };
        }

        private void reiniciar() {
            cadena = txtEntrada.getText();
            evaluada = cadena;
            idx = 0; paso = 0; fin = false; error = false;
            estado = "q0"; fase = "BUSCAR_A"; cabezal = 1; dir = "DERECHA";
            log.setText("");
            lblResultado.setText("Resultado: En proceso...");
            lblResultado.setForeground(new Color(100, 116, 139));
            btnPaso.setEnabled(true); btnTodo.setEnabled(true);

            if (modo == Modo.AFD) {
                lienzoAfd.set(estado, error);
            } else if (modo == Modo.PILA) {
                pila.clear(); pila.push("Z0");
                lienzoPila.set(new ArrayList<>(pila));
            } else if (modo == Modo.LBA) {
                cinta.clear(); cinta.add("[");
                for (char c : cadena.toCharArray()) cinta.add(String.valueOf(c));
                cinta.add("]");
                lienzoCinta.set(cinta, cabezal, dir, false, false);
            } else {
                String trim = cadena.trim();
                cinta.clear(); cinta.add("B");
                for (char c : trim.toCharArray()) cinta.add(String.valueOf(c));
                cinta.add("B");
                if (!trim.matches("1+\\+1+")) {
                    terminar("ERROR / RECHAZADA", ROJO, "Entrada inválida para suma unaria.");
                    return;
                }
                lienzoCinta.set(cinta, cabezal, dir, true, false);
            }
            escribir("Inicio · Cadena: \"" + cadena + "\"");
            actualizarEstadoLabel();
        }

        private void paso() {
            if (!txtEntrada.getText().equals(evaluada)) reiniciar();
            if (fin) return;
            paso++;

            switch (modo) {
                case AFD -> pasoAfd();
                case PILA -> pasoPila();
                case LBA -> pasoLba();
                case TURING -> pasoTuring();
            }
            actualizarEstadoLabel();
        }

        private void pasoAfd() {
            if (idx >= cadena.length()) {
                boolean ok = !error && !cadena.isEmpty() && "q2".equals(estado);
                terminar(ok ? "ACEPTADA" : "RECHAZADA", ok ? VERDE : ROJO, ok ? "Termina en q2." : "No termina en q2.");
                return;
            }
            char c = cadena.charAt(idx++);
            if (c != 'a' && c != 'b') { error = true; terminar("RECHAZADA", ROJO, "Símbolo '" + c + "' inválido."); return; }
            String ant = estado;
            estado = switch (estado) {
                case "q0" -> c == 'a' ? "q1" : "q0";
                case "q1" -> c == 'a' ? "q1" : "q2";
                default -> c == 'a' ? "q1" : "q0";
            };
            lienzoAfd.set(estado, false);
            escribir("Paso " + paso + ": '" + c + "' (" + ant + " -> " + estado + ")");
            if (idx >= cadena.length()) pasoAfd();
        }

        private void pasoPila() {
            if (idx >= cadena.length()) {
                boolean ok = !error && !cadena.isEmpty() && "q1".equals(estado) && pila.size() == 1 && "Z0".equals(pila.peek());
                terminar(ok ? "ACEPTADA" : "RECHAZADA", ok ? VERDE : ROJO, ok ? "Pila vacía salvo Z0." : "Condición LIFO no cumplida.");
                return;
            }
            char c = cadena.charAt(idx++);
            if (c != 'a' && c != 'b') { error = true; terminar("RECHAZADA", ROJO, "Símbolo '" + c + "' inválido."); return; }
            if ("q0".equals(estado)) {
                if (c == 'a') { pila.push("A"); escribir("Paso " + paso + ": 'a' -> PUSH A | " + pila); }
                else if ("A".equals(pila.peek())) { pila.pop(); estado = "q1"; escribir("Paso " + paso + ": 'b' -> POP A, a q1 | " + pila); }
                else { error = true; terminar("RECHAZADA", ROJO, "Falta 'A' al leer 'b'."); }
            } else {
                if (c == 'b' && "A".equals(pila.peek())) { pila.pop(); escribir("Paso " + paso + ": 'b' -> POP A | " + pila); }
                else { error = true; terminar("RECHAZADA", ROJO, "Orden o cantidad inválida en q1."); }
            }
            lienzoPila.set(new ArrayList<>(pila));
            if (!fin && idx >= cadena.length()) pasoPila();
        }

        private void pasoLba() {
            for (int i = 1; i < cinta.size() - 1; i++) {
                if (!"a".equals(cinta.get(i)) && !"b".equals(cinta.get(i)) && !"c".equals(cinta.get(i))
                        && !"X".equals(cinta.get(i)) && !"Y".equals(cinta.get(i)) && !"Z".equals(cinta.get(i))) {
                    terminar("RECHAZADA", ROJO, "Símbolo '" + cinta.get(i) + "' inválido.");
                    return;
                }
            }
            String cur = cinta.get(cabezal);
            switch (fase) {
                case "BUSCAR_A" -> {
                    if ("a".equals(cur)) {
                        cinta.set(cabezal, "X"); fase = "BUSCAR_B"; dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": 'a' -> 'X', pasa a BUSCAR_B (DERECHA)");
                    } else if ("X".equals(cur) || "Y".equals(cur) || "Z".equals(cur)) {
                        dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": Salta marca '" + cur + "' en BUSCAR_A");
                    } else if ("]".equals(cur)) {
                        verificarLba();
                    } else {
                        terminar("RECHAZADA", ROJO, "Símbolo inesperado '" + cur + "' en BUSCAR_A.");
                    }
                }
                case "BUSCAR_B" -> {
                    if ("b".equals(cur)) {
                        cinta.set(cabezal, "Y"); fase = "BUSCAR_C"; dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": 'b' -> 'Y', pasa a BUSCAR_C (DERECHA)");
                    } else if ("a".equals(cur) || "Y".equals(cur) || "Z".equals(cur)) {
                        dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": Avanza sobre '" + cur + "' en BUSCAR_B");
                    } else {
                        terminar("RECHAZADA", ROJO, "Esperaba 'b', halló '" + cur + "'.");
                    }
                }
                case "BUSCAR_C" -> {
                    if ("c".equals(cur)) {
                        cinta.set(cabezal, "Z"); fase = "VOLVER"; dir = "IZQUIERDA"; moverLba(-1);
                        escribir("Paso " + paso + ": 'c' -> 'Z', regresa a '[' (IZQUIERDA)");
                    } else if ("b".equals(cur) || "Z".equals(cur)) {
                        dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": Avanza sobre '" + cur + "' en BUSCAR_C");
                    } else {
                        terminar("RECHAZADA", ROJO, "Esperaba 'c', halló '" + cur + "'.");
                    }
                }
                case "VOLVER" -> {
                    if ("[".equals(cur)) {
                        fase = "BUSCAR_A"; dir = "DERECHA"; moverLba(1);
                        escribir("Paso " + paso + ": Toca frontera '[', inicia BUSCAR_A");
                    } else {
                        dir = "IZQUIERDA"; moverLba(-1);
                        escribir("Paso " + paso + ": Retrocede sobre '" + cur + "' hacia '['");
                    }
                }
            }
            if (!fin) lienzoCinta.set(cinta, cabezal, dir, false, false);
        }

        private void verificarLba() {
            int x = 0, y = 0, z = 0;
            for (int i = 1; i < cinta.size() - 1; i++) {
                switch (cinta.get(i)) {
                    case "X" -> x++; case "Y" -> y++; case "Z" -> z++;
                    default -> { terminar("RECHAZADA", ROJO, "Quedó '" + cinta.get(i) + "' sin marcar."); return; }
                }
            }
            if (x >= 1 && x == y && y == z) terminar("ACEPTADA (n=" + x + ")", VERDE, "Emparejados " + x + " grupos de a, b y c.");
            else terminar("RECHAZADA", ROJO, "No cumple a^n b^n c^n.");
        }

        private void moverLba(int d) { cabezal = Math.max(0, Math.min(cinta.size() - 1, cabezal + d)); }

        private void pasoTuring() {
            String cur = cinta.get(cabezal);
            switch (estado) {
                case "q0" -> {
                    if ("1".equals(cur)) {
                        dir = "DERECHA"; moverTuring(1);
                        escribir("Paso " + paso + " [q0]: Lee '1', avanza a DERECHA");
                    } else if ("+".equals(cur)) {
                        cinta.set(cabezal, "1"); estado = "q1"; dir = "DERECHA"; moverTuring(1);
                        escribir("Paso " + paso + " [q0]: Lee '+' -> escribe '1', pasa a q1");
                    } else {
                        terminar("ERROR", ROJO, "Símbolo inesperado en q0.");
                    }
                }
                case "q1" -> {
                    if ("1".equals(cur)) {
                        dir = "DERECHA"; moverTuring(1);
                        escribir("Paso " + paso + " [q1]: Lee '1', avanza a DERECHA");
                    } else if ("B".equals(cur)) {
                        estado = "q2"; dir = "IZQUIERDA"; moverTuring(-1);
                        escribir("Paso " + paso + " [q1]: Lee blanco 'B', retrocede a q2");
                    } else {
                        terminar("ERROR", ROJO, "Símbolo inesperado en q1.");
                    }
                }
                case "q2" -> {
                    if ("1".equals(cur)) {
                        cinta.set(cabezal, "B"); estado = "q_aceptar";
                        StringBuilder res = new StringBuilder();
                        for (String s : cinta) if ("1".equals(s)) res.append("1");
                        terminar("SUMA: " + res + " (" + res.length() + ")", VERDE, "Resultado unario: " + res);
                    } else {
                        terminar("ERROR", ROJO, "No había '1' para borrar.");
                    }
                }
            }
            if (!fin) lienzoCinta.set(cinta, cabezal, dir, true, false);
        }

        private void moverTuring(int d) {
            int n = cabezal + d;
            if (n < 0) { cinta.add(0, "B"); cabezal = 0; }
            else if (n >= cinta.size()) { cinta.add("B"); cabezal = n; }
            else cabezal = n;
        }

        private void terminar(String res, Color col, String msg) {
            fin = true; btnPaso.setEnabled(false); btnTodo.setEnabled(false);
            lblResultado.setText("Resultado: " + res);
            lblResultado.setForeground(col);
            escribir(">>> " + msg + " <<<");
            if (modo == Modo.AFD) lienzoAfd.set(estado, error);
            if (modo == Modo.LBA) lienzoCinta.set(cinta, cabezal, dir, false, col == VERDE);
            if (modo == Modo.TURING) lienzoCinta.set(cinta, cabezal, dir, true, col == VERDE);
            actualizarEstadoLabel();
        }

        private void ejecutarTodo() {
            if (!txtEntrada.getText().equals(evaluada)) reiniciar();
            int lim = 500;
            while (!fin && lim-- > 0) paso();
        }

        private void actualizarEstadoLabel() {
            lblEstado.setText(modo == Modo.LBA
                    ? "Fase: " + (fin ? lblResultado.getText().replace("Resultado: ", "") : fase) + " · Pos " + cabezal
                    : "Estado: " + estado + (modo == Modo.TURING ? " · Pos " + cabezal : ""));
        }

        private void escribir(String t) { log.append(t + "\n"); log.setCaretPosition(log.getDocument().getLength()); }
    }

    private static class LienzoAfd extends JPanel {
        private String estado = "q0";
        private boolean error;
        LienzoAfd() { setPreferredSize(new Dimension(720, 125)); setBackground(new Color(248, 250, 252)); }
        void set(String estado, boolean error) { this.estado = estado; this.error = error; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            String[] ids = {"q0", "q1", "q2"};
            String[] desc = {"Inicio / sin 'a'", "Leyó 'a'", "Acepta \"ab\""};
            int w = 180, gap = 24, x0 = Math.max(16, (getWidth() - (3 * w + 2 * gap)) / 2), y = 26;
            for (int i = 0; i < 3; i++) {
                int x = x0 + i * (w + gap);
                boolean act = !error && ids[i].equals(estado);
                g2.setColor(act ? (i == 2 ? VERDE_SUAVE : AZUL_SUAVE) : TARJETA);
                g2.fillRoundRect(x, y, w, 68, 12, 12);
                g2.setColor(act ? (i == 2 ? VERDE : AZUL) : new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(act ? 2.5f : 1.2f));
                g2.drawRoundRect(x, y, w, 68, 12, 12);
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString(ids[i] + (i == 2 ? " (Final)" : ""), x + 16, y + 28);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(71, 85, 105));
                g2.drawString(desc[i], x + 16, y + 50);
            }
        }
    }

    private static class LienzoPila extends JPanel {
        private List<String> items = List.of("Z0");
        LienzoPila() { setPreferredSize(new Dimension(720, 155)); setBackground(new Color(248, 250, 252)); }
        void set(List<String> items) { this.items = items; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(AZUL);
            g2.drawString("▼ TOPE (LIFO)", 24, 24);
            g2.setColor(AMBAR);
            g2.drawString("BASE ▲", 24, getHeight() - 14);

            int w = 64, h = 34, gap = 6;
            int x0 = 135, y = 14;
            for (int i = 0; i < items.size(); i++) {
                int x = x0 + i * (w + gap);
                if (x + w > getWidth() - 16) break;
                String val = items.get(i);
                boolean tope = i == 0, base = "Z0".equals(val);
                g2.setColor(tope ? AZUL_SUAVE : (base ? AMBAR_SUAVE : MORADO_SUAVE));
                g2.fillRoundRect(x, y, w, h, 8, 8);
                g2.setColor(tope ? AZUL : (base ? AMBAR : MORADO));
                g2.setStroke(new BasicStroke(tope ? 2.2f : 1.2f));
                g2.drawRoundRect(x, y, w, h, 8, 8);
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2.drawString(val, x + 20, y + 22);
                if (tope) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g2.setColor(AZUL);
                    g2.drawString("TOPE", x + 16, y + h + 13);
                }
            }
        }
    }

    private static class LienzoCinta extends JPanel {
        private List<String> celdas = List.of();
        private int pos = 1; private String dir = "DERECHA"; private boolean turing, exito;
        LienzoCinta() { setPreferredSize(new Dimension(720, 130)); setBackground(new Color(248, 250, 252)); }
        void set(List<String> celdas, int pos, String dir, boolean turing, boolean exito) {
            this.celdas = new ArrayList<>(celdas); this.pos = pos; this.dir = dir; this.turing = turing; this.exito = exito; repaint();
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (celdas.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = 48, x0 = Math.max(12, (getWidth() - celdas.size() * w) / 2), y = 36;
            for (int i = 0; i < celdas.size(); i++) {
                int x = x0 + i * w;
                String v = celdas.get(i);
                boolean cab = i == pos, fron = !turing && ("[".equals(v) || "]".equals(v));
                boolean marcado = "X".equals(v) || "Y".equals(v) || "Z".equals(v) || (exito && "1".equals(v));

                g2.setColor(cab ? AZUL_SUAVE : (marcado ? VERDE_SUAVE : (fron ? AMBAR_SUAVE : TARJETA)));
                g2.fillRoundRect(x, y, w - 4, 42, 7, 7);
                g2.setColor(cab ? AZUL : (marcado ? VERDE : (fron ? AMBAR : new Color(148, 163, 184))));
                g2.setStroke(new BasicStroke(cab ? 2.4f : 1.2f));
                g2.drawRoundRect(x, y, w - 4, 42, 7, 7);

                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                g2.drawString(v, x + 16, y + 26);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(new Color(100, 116, 139));
                g2.drawString("#" + i, x + 15, y + 55);

                if (cab) {
                    g2.setColor(AZUL);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.drawString("DERECHA".equals(dir) ? "▼ ▶" : "▼ ◀", x + 8, y - 7);
                }
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}