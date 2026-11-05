// Parte 3: los 7 tipos de problemas resueltos con lambdas ( p -> ... )
// Java 17 o superior. Ejecutar: java java/TiposDeProblemas.java

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TiposDeProblemas {

    // record = clase inmutable con sus campos (parecido a la case class de Scala)
    record Producto(String nombre, String categoria, double precio, int stock) {}

    public static void main(String[] args) {

        List<Producto> productos = List.of(
            new Producto("Laptop Gamer X15", "Tecnologia", 1250.00, 5),
            new Producto("Mouse Inalambrico", "Tecnologia", 18.50, 40),
            new Producto("Monitor Curvo 27", "Tecnologia", 289.99, 12),
            new Producto("Audifonos Bluetooth", "Tecnologia", 45.90, 0),
            new Producto("Teclado Mecanico", "Tecnologia", 75.00, 18),
            new Producto("Arroz Integral 2kg", "Alimentos", 3.80, 120),
            new Producto("Cafe Molido Loja", "Alimentos", 7.25, 60),
            new Producto("Chocolate Amargo 70", "Alimentos", 2.90, 0),
            new Producto("Silla Ergonomica", "Hogar", 159.00, 7),
            new Producto("Lampara LED Escritorio", "Hogar", 24.99, 30),
            new Producto("Cafetera Espresso", "Hogar", 135.50, 4),
            new Producto("Camiseta Algodon", "Ropa", 12.00, 75),
            new Producto("Chaqueta Impermeable", "Ropa", 64.90, 15),
            new Producto("Zapatos Deportivos", "Ropa", 89.99, 0)
        );

        // 1. SELECCION (filter): productos con menos de 10 unidades
        List<String> stockBajo = productos.stream()
            .filter(p -> p.stock() < 10)
            .map(p -> p.nombre())
            .toList();
        System.out.println("stockBajo = " + stockBajo);

        // 2. TRANSFORMACION (map): precio con 50% de descuento
        List<String> conDescuento = productos.stream()
            .map(p -> p.nombre() + ": " + p.precio() * 0.5)
            .toList();
        System.out.println("conDescuento = " + conDescuento);

        // 3. VALIDACION: en Java exists = anyMatch y forall = allMatch
        boolean hayAgotados = productos.stream().anyMatch(p -> p.stock() == 0);
        boolean preciosValidos = productos.stream().allMatch(p -> p.precio() > 0);
        boolean hogarCaro = productos.stream().anyMatch(p -> p.categoria().equals("Hogar") && p.precio() > 200);
        System.out.println("hayAgotados = " + hayAgotados);
        System.out.println("preciosValidos = " + preciosValidos);
        System.out.println("hogarCaro = " + hogarCaro);

        // 4. CONTEO: count() no recibe lambda, por eso primero se filtra
        long tecnoCaros = productos.stream()
            .filter(p -> p.categoria().equals("Tecnologia") && p.precio() > 50)
            .count();
        System.out.println("tecnoCaros = " + tecnoCaros);

        // 5. EXTREMOS: max y min reciben una lambda que compara DOS productos
        String mayorInventario = productos.stream()
            .max((a, b) -> Double.compare(a.precio() * a.stock(), b.precio() * b.stock()))
            .get().nombre();
        String masBarato = productos.stream()
            .min((a, b) -> Double.compare(a.precio(), b.precio()))
            .get().nombre();
        System.out.println("mayorInventario = " + mayorInventario);
        System.out.println("masBarato = " + masBarato);

        // 6. APLANAMIENTO (flatMap): la lambda debe devolver un Stream
        List<String> palabras = productos.stream()
            .flatMap(p -> Arrays.stream(p.nombre().toLowerCase().split(" ")))
            .toList();
        System.out.println("palabras = " + palabras);
        System.out.println("totalPalabras = " + palabras.size());

        // 7. ACUMULACION: reduce hace lo mismo que foldLeft
        int totalUnidades = productos.stream()
            .map(p -> p.stock())
            .reduce(0, (acc, s) -> acc + s);
        System.out.println("totalUnidades = " + totalUnidades);

        // Java NO tiene scanLeft: para cada posicion i se suman las unidades de los primeros i productos
        List<Integer> evolucion = IntStream.rangeClosed(0, productos.size())
            .mapToObj(i -> productos.subList(0, i).stream().map(p -> p.stock()).reduce(0, (acc, s) -> acc + s))
            .toList();
        System.out.println("evolucion = " + evolucion);
    }
}
