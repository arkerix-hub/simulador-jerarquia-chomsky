# Simulador Interactivo de la Jerarquía de Chomsky

Aplicación de escritorio en **Java (Swing)** diseñada para simular y explicar de forma visual, paso a paso y en tiempo real los cuatro niveles de la **Jerarquía de Chomsky** mediante autómatas y modelos formales de cómputo.

El proyecto concentra toda la lógica y la interfaz gráfica en un único archivo (`Main.java`) con diseño limpio, navegación por pestañas/vistas (`CardLayout`), lienzos gráficos personalizados (`Graphics2D`), casos de prueba rápidos y consola de trazas numeradas.

---

## Modelos implementados (Jerarquía de Chomsky)

| Nivel | Tipo de lenguaje | Modelo computacional | Lenguaje / Función simulada | Memoria / Característica clave |
| :--- | :--- | :--- | :--- | :--- |
| **Nivel 3** | Lenguaje Regular | Autómata Finito Determinista (**AFD**) | Cadenas no vacías sobre $\Sigma = \{a, b\}$ que terminan en `"ab"` | Memoria constante (estados `q0`, `q1`, `q2`) |
| **Nivel 2** | Lenguaje Libre de Contexto | Autómata de Pila (**AP — LIFO**) | $L = \{a^n b^n \mid n \ge 1\}$ | Pila *Last In, First Out* con marcador base `Z0` |
| **Nivel 1** | Lenguaje Sensible al Contexto | Autómata Linealmente Acotado (**LBA**) | $L = \{a^n b^n c^n \mid n \ge 1\}$ | Cinta acotada entre `[` y `]`, cabezal bidireccional y marcas `X`, `Y`, `Z` |
| **Nivel 0** | Lenguaje Recursivamente Enumerable | **Máquina de Turing** | Suma unaria $1^m + 1^k \rightarrow 1^{m+k}$ | Cinta extendible con blancos `B` y alfabeto de cinta `{1, +, B}` |

---

## Características principales

- **Menú lateral con nivel activo:** permite cambiar entre los Niveles 3, 2, 1 y 0 mostrando claramente cuál está seleccionado en la cabecera y en el menú.
- **Control paso a paso y ejecución completa:**
  - **Siguiente paso:** avanza de forma controlada para explicar qué lee, qué escribe/apila/desapila o cómo se mueve el cabezal en cada paso.
  - **Ejecutar todo:** completa la simulación de principio a fin y desplaza automáticamente la consola hasta el resultado.
  - **Reiniciar:** restablece el estado inicial, la pila o la cinta y limpia la bitácora.
- **Casos de prueba predefinidos + entrada libre:** botones de un clic para probar cadenas válidas, inválidas o de error, además de un campo de texto editable (compatible con `Enter`).
- **Visualización gráfica + bitácora numerada:** cada nivel combina un lienzo gráfico (`LienzoAfd`, `LienzoPila`, `LienzoCinta`) con una consola de trazas que numera cada movimiento y transición.

---

## Requisitos

- **Java Development Kit (JDK)** 17 o superior (utiliza librerías estándar de Java SE y Swing, sin dependencias externas).
- **Editor o terminal:** Visual Studio Code (con *Extension Pack for Java*) o consola del sistema con `javac` y `java` configurados en el `PATH`.

---

## Instalación y ejecución

1. **Clona o descarga el repositorio:**
   ```bash
   git clone <URL_DE_TU_REPOSITORIO>
   cd <CARPETA_DEL_PROYECTO>
   ```

2. **Compila y ejecuta desde terminal:**
   ```bash
   javac Main.java
   java Main
   ```

3. **Ejecución desde Visual Studio Code:**
   - Abre la carpeta del proyecto en VS Code.
   - Abre el archivo `Main.java`.
   - Haz clic en **Run** sobre `Main` o ejecuta el método `main`.

---

## Estructura del proyecto

```text
.
├── Main.java       # Punto de entrada, navegación, lógica de los 4 niveles y lienzos gráficos
└── README.md       # Documentación general, casos de prueba y guía de ejecución
```

---

## Casos de prueba incluidos en la interfaz

| Nivel | Modelo | Ejemplos incluidos | Resultado esperado |
| :--- | :--- | :--- | :--- |
| **Nivel 3** | AFD (termina en `"ab"`) | `ab (Válida)`, `babab (Válida)` | **ACEPTADA** (termina en estado final `q2`) |
| **Nivel 3** | AFD (termina en `"ab"`) | `a (Inválida)`, `aba (Inválida)` | **RECHAZADA** |
| **Nivel 2** | Pila LIFO ($a^n b^n$) | `ab (Válida)`, `aaabbb (Válida)` | **ACEPTADA** (misma cantidad de `a` y `b`, pila con `Z0`) |
| **Nivel 2** | Pila LIFO ($a^n b^n$) | `aab (Inválida)`, `abba (Inválida)` | **RECHAZADA** |
| **Nivel 1** | LBA ($a^n b^n c^n$, `[` y `]`) | `abc (Válida)`, `aabbcc (Válida)` | **ACEPTADA** (misma cantidad de `a`, `b` y `c` marcadas) |
| **Nivel 1** | LBA ($a^n b^n c^n$, `[` y `]`) | `aabcc (Inválida)`, `acb (Inválida)` | **RECHAZADA** |
| **Nivel 0** | Máquina de Turing ($1^m + 1^k$) | `11+11`, `111+11`, `1+111` | Resultado en `1`s (`1111`, `11111`, `1111`) |
| **Nivel 0** | Máquina de Turing ($1^m + 1^k$) | `11++1 (Error)` | **ERROR / RECHAZADA** por formato inválido |

---

## Puntos clave para exposición / sustentación

- **Por qué un AFD no basta para $a^n b^n$:** un autómata finito tiene un número fijo de estados y no puede contar cantidades arbitrariamente grandes sin desbordarse; el autómata de pila resuelve esto usando memoria LIFO (`PUSH` con `'a'` y `POP` con `'b'`).
- **Por qué $a^n b^n c^n$ sube a Nivel 1 (LBA):** una sola pila no permite verificar tres bloques correlativos de forma limpia sin perder el orden; el LBA marca símbolos (`X`, `Y`, `Z`) sobre una cinta acotada entre `[` y `]` moviéndose en ambos sentidos.
- **Qué distingue al Nivel 0 (Máquina de Turing):** además de leer, escribir y moverse en ambas direcciones, permite extender la cinta con celdas en blanco (`B`) y realizar cómputos/funciones como la suma unaria.