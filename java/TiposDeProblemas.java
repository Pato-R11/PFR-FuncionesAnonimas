// =============================================================================
// TiposDeProblemas.java - Parte 3 (Java 17 o superior)
// Los 7 tipos de problemas resueltos con lambdas ( p -> ... ) y Streams.
//
// Ejecutar:  java java/TiposDeProblemas.java
// =============================================================================

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TiposDeProblemas {

    // record = clase inmutable con sus campos (parecido a la case class de Scala)
    record Producto(String nombre, String categoria, double precio, int stock) {}

    static List<Producto> productos = List.of(
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

    // Muestra un double con 2 decimales y punto (no coma)
    static String f2(double x) {
        return String.format(Locale.US, "%.2f", x);
    }

    public static void main(String[] args) {

        // 1. SELECCIÓN (filter)
        System.out.println("=== 1. SELECCION (filter) ===");
        List<Producto> stockBajo = productos.stream()
            .filter(p -> p.stock() < 10)
            .toList();
        stockBajo.forEach(p -> System.out.println("  - " + p.nombre() + " (" + p.stock() + " u.)"));

        // 2. TRANSFORMACIÓN (map)
        System.out.println("=== 2. TRANSFORMACION (map) ===");
        List<String> conDescuento = productos.stream()
            .map(p -> p.nombre() + ": " + f2(p.precio()) + " -> " + f2(p.precio() * 0.8))
            .toList();
        conDescuento.forEach(linea -> System.out.println("  " + linea));

        // 3. VALIDACIÓN: en Java exists = anyMatch, forall = allMatch
        System.out.println("=== 3. VALIDACION (exists / forall) ===");
        boolean hayAgotados    = productos.stream().anyMatch(p -> p.stock() == 0);
        boolean preciosValidos = productos.stream().allMatch(p -> p.precio() > 0);
        boolean hogarCaro      = productos.stream().anyMatch(p -> p.categoria().equals("Hogar") && p.precio() > 200);
        System.out.println("  Hay algun producto agotado?        " + hayAgotados);
        System.out.println("  Todos tienen precio > 0?           " + preciosValidos);
        System.out.println("  Hay algun producto de Hogar > 200? " + hogarCaro);

        // 4. CONTEO: count() no recibe lambda, por eso primero se filtra
        System.out.println("=== 4. CONTEO (count) ===");
        long tecnoCaros = productos.stream()
            .filter(p -> p.categoria().equals("Tecnologia") && p.precio() > 50)
            .count();
        System.out.println("  Productos de Tecnologia con precio > 50: " + tecnoCaros);

        // 5. EXTREMOS: max/min reciben una lambda de DOS productos que los compara
        System.out.println("=== 5. EXTREMOS (maxBy / minBy) ===");
        Producto mayorInventario = productos.stream()
            .max((a, b) -> Double.compare(a.precio() * a.stock(), b.precio() * b.stock()))
            .get();
        Producto masBarato = productos.stream()
            .min((a, b) -> Double.compare(a.precio(), b.precio()))
            .get();
        System.out.println("  Mayor valor en inventario: " + mayorInventario.nombre()
            + " (" + f2(mayorInventario.precio() * mayorInventario.stock()) + ")");
        System.out.println("  Producto mas barato:       " + masBarato.nombre()
            + " (" + f2(masBarato.precio()) + ")");

        // 6. APLANAMIENTO (flatMap): la lambda debe devolver un Stream
        System.out.println("=== 6. APLANAMIENTO (flatMap) ===");
        List<String> palabras = productos.stream()
            .flatMap(p -> Arrays.stream(p.nombre().toLowerCase().split(" ")))
            .toList();
        System.out.println("  Total de palabras: " + palabras.size());
        System.out.println("  " + String.join(", ", palabras));

        // 7. ACUMULACIÓN: reduce hace lo mismo que foldLeft
        System.out.println("=== 7. ACUMULACION (foldLeft / scanLeft) ===");
        List<Double> valores = productos.stream()
            .map(p -> p.precio() * p.stock())
            .toList();
        double total = valores.stream().reduce(0.0, (acc, v) -> acc + v);
        System.out.println("  Valor total del inventario: " + f2(total));

        // Java NO tiene scanLeft: para cada posición i se suman los primeros i valores
        List<Double> evolucion = IntStream.rangeClosed(0, valores.size())
            .mapToObj(i -> valores.subList(0, i).stream().reduce(0.0, (acc, v) -> acc + v))
            .toList();
        System.out.println("  Evolucion: "
            + evolucion.stream().map(v -> f2(v)).collect(Collectors.joining(", ")));
    }
}
