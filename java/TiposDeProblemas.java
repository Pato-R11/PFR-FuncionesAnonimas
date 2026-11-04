// =============================================================================
// TiposDeProblemas.java — Parte 3: los 7 tipos de problemas resueltos con
// expresiones lambda y la API de Streams de Java.
//
// Requisitos: JDK 17 o superior (usa `record`).
// Ejecutar (desde la raíz del repositorio), sin compilar aparte:
//   java java/TiposDeProblemas.java
// o de la forma clásica:
//   javac -d out java/TiposDeProblemas.java && java -cp out TiposDeProblemas
// =============================================================================

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TiposDeProblemas {

    // Misma estructura que la case class de Scala: un record es inmutable.
    record Producto(String nombre, String categoria, double precio, int stock) {}

    // Los mismos 14 productos de scala/Producto.scala y python/tipos_de_problemas.py
    static final List<Producto> productos = List.of(
        new Producto("Laptop Gamer X15",       "Tecnologia", 1250.00,   5),
        new Producto("Mouse Inalambrico",      "Tecnologia",   18.50,  40),
        new Producto("Monitor Curvo 27",       "Tecnologia",  289.99,  12),
        new Producto("Audifonos Bluetooth",    "Tecnologia",   45.90,   0),
        new Producto("Teclado Mecanico",       "Tecnologia",   75.00,  18),
        new Producto("Arroz Integral 2kg",     "Alimentos",     3.80, 120),
        new Producto("Cafe Molido Loja",       "Alimentos",     7.25,  60),
        new Producto("Chocolate Amargo 70",    "Alimentos",     2.90,   0),
        new Producto("Silla Ergonomica",       "Hogar",       159.00,   7),
        new Producto("Lampara LED Escritorio", "Hogar",        24.99,  30),
        new Producto("Cafetera Espresso",      "Hogar",       135.50,   4),
        new Producto("Camiseta Algodon",       "Ropa",         12.00,  75),
        new Producto("Chaqueta Impermeable",   "Ropa",         64.90,  15),
        new Producto("Zapatos Deportivos",     "Ropa",         89.99,   0)
    );

    // 2 decimales con punto (Locale.US) para que la salida coincida con Scala y Python.
    static String f2(double x) {
        return String.format(Locale.US, "%.2f", x);
    }

    public static void main(String[] args) {

        // ---------------------------------------------------------------------
        // 1. SELECCIÓN — filter
        // Lambda: Predicate<Producto>  (Producto -> boolean)
        // ---------------------------------------------------------------------
        System.out.println("=== 1. SELECCION (filter) ===");
        List<Producto> stockBajo = productos.stream()
            .filter(p -> p.stock() < 10)
            .toList();
        stockBajo.forEach(p -> System.out.println("  - " + p.nombre() + " (" + p.stock() + " u.)"));

        // ---------------------------------------------------------------------
        // 2. TRANSFORMACIÓN — map
        // Lambda: Function<Producto, String>
        // ---------------------------------------------------------------------
        System.out.println("=== 2. TRANSFORMACION (map) ===");
        List<String> conDescuento = productos.stream()
            .map(p -> p.nombre() + ": " + f2(p.precio()) + " -> " + f2(p.precio() * 0.8))
            .toList();
        conDescuento.forEach(linea -> System.out.println("  " + linea));

        // ---------------------------------------------------------------------
        // 3. VALIDACIÓN — anyMatch (exists) / allMatch (forall)
        // Lambda: Predicate<Producto>
        // ---------------------------------------------------------------------
        System.out.println("=== 3. VALIDACION (exists / forall) ===");
        boolean hayAgotados    = productos.stream().anyMatch(p -> p.stock() == 0);
        boolean preciosValidos = productos.stream().allMatch(p -> p.precio() > 0);
        boolean hogarCaro      = productos.stream().anyMatch(p -> p.categoria().equals("Hogar") && p.precio() > 200);
        System.out.println("  Hay algun producto agotado?        " + hayAgotados);
        System.out.println("  Todos tienen precio > 0?           " + preciosValidos);
        System.out.println("  Hay algun producto de Hogar > 200? " + hogarCaro);

        // ---------------------------------------------------------------------
        // 4. CONTEO — filter + count  (Streams no tiene count(predicado))
        // Lambda: Predicate<Producto>
        // ---------------------------------------------------------------------
        System.out.println("=== 4. CONTEO (count) ===");
        long tecnoCaros = productos.stream()
            .filter(p -> p.categoria().equals("Tecnologia") && p.precio() > 50)
            .count();
        System.out.println("  Productos de Tecnologia con precio > 50: " + tecnoCaros);

        // ---------------------------------------------------------------------
        // 5. EXTREMOS — max / min con un Comparator construido desde una lambda
        // Lambda: ToDoubleFunction<Producto>  (Producto -> double)
        // ---------------------------------------------------------------------
        System.out.println("=== 5. EXTREMOS (maxBy / minBy) ===");
        Producto mayorInventario = productos.stream()
            .max(Comparator.comparingDouble(p -> p.precio() * p.stock()))
            .orElseThrow();
        Producto masBarato = productos.stream()
            .min(Comparator.comparingDouble(p -> p.precio()))
            .orElseThrow();
        System.out.println("  Mayor valor en inventario: " + mayorInventario.nombre()
            + " (" + f2(mayorInventario.precio() * mayorInventario.stock()) + ")");
        System.out.println("  Producto mas barato:       " + masBarato.nombre()
            + " (" + f2(masBarato.precio()) + ")");

        // ---------------------------------------------------------------------
        // 6. APLANAMIENTO — flatMap
        // Lambda: Function<Producto, Stream<String>>  (debe devolver un Stream)
        // ---------------------------------------------------------------------
        System.out.println("=== 6. APLANAMIENTO (flatMap) ===");
        List<String> palabras = productos.stream()
            .flatMap(p -> List.of(p.nombre().toLowerCase().split(" ")).stream())
            .toList();
        System.out.println("  Total de palabras: " + palabras.size());
        System.out.println("  " + String.join(", ", palabras));

        // ---------------------------------------------------------------------
        // 7. ACUMULACIÓN — reduce (foldLeft) / scanLeft "a mano"
        // reduce(identidad, acumulador, combinador):
        //   acumulador  -> BiFunction<Double, Producto, Double>
        //   combinador  -> BinaryOperator<Double> (solo se usa en paralelo)
        // ---------------------------------------------------------------------
        System.out.println("=== 7. ACUMULACION (foldLeft / scanLeft) ===");
        double total = productos.stream()
            .reduce(0.0, (acc, p) -> acc + p.precio() * p.stock(), Double::sum);
        System.out.println("  Valor total del inventario: " + f2(total));

        // Java Streams NO tiene scanLeft. Se simula con collect(): la lambda
        // acumuladora agrega a la lista el último total + el valor del producto.
        // (Válido para streams secuenciales; en paralelo el combinador no
        // reconstruiría correctamente los acumulados.)
        List<Double> evolucion = productos.stream()
            .map(p -> p.precio() * p.stock())
            .collect(
                () -> new ArrayList<>(List.of(0.0)),
                (lista, v) -> lista.add(lista.get(lista.size() - 1) + v),
                ArrayList::addAll);
        System.out.println("  Evolucion: "
            + evolucion.stream().map(v -> f2(v)).collect(Collectors.joining(", ")));
    }
}
