// =============================================================================
// TiposDeProblemas.scala — Parte 3: los 7 tipos de problemas resueltos con
// funciones anónimas (lambdas) sobre la lista Datos.productos.
//
// Ejecutar (desde la raíz del repositorio):
//   scala-cli run scala/
// o bien, con el compilador clásico de Scala 3:
//   scalac -d out scala/*.scala && scala -classpath out TiposDeProblemas
// =============================================================================

import Datos.{productos, f2}

object TiposDeProblemas {

  def main(args: Array[String]): Unit = {

    // -------------------------------------------------------------------------
    // 1. SELECCIÓN — filter
    // Situación: bodega necesita saber qué productos tienen menos de 10
    // unidades para generar la orden de reposición al proveedor.
    // Lambda: Producto => Boolean
    // -------------------------------------------------------------------------
    println("=== 1. SELECCION (filter) ===")
    val stockBajo = productos.filter(p => p.stock < 10)
    stockBajo.foreach(p => println(s"  - ${p.nombre} (${p.stock} u.)"))

    // -------------------------------------------------------------------------
    // 2. TRANSFORMACIÓN — map
    // Situación: campaña de Black Friday con 20 % de descuento; la web debe
    // mostrar el precio final de cada producto.
    // Lambda: Producto => String
    // -------------------------------------------------------------------------
    println("=== 2. TRANSFORMACION (map) ===")
    val conDescuento = productos.map(p => s"${p.nombre}: ${f2(p.precio)} -> ${f2(p.precio * 0.8)}")
    conDescuento.foreach(linea => println(s"  $linea"))

    // -------------------------------------------------------------------------
    // 3. VALIDACIÓN — exists / forall
    // Situación: antes de publicar el catálogo en línea se verifica que no
    // haya productos agotados y que todos tengan un precio válido.
    // Lambda: Producto => Boolean
    // -------------------------------------------------------------------------
    println("=== 3. VALIDACION (exists / forall) ===")
    val hayAgotados     = productos.exists(p => p.stock == 0)
    val preciosValidos  = productos.forall(p => p.precio > 0)
    val hogarCaro       = productos.exists(p => p.categoria == "Hogar" && p.precio > 200)
    println(s"  Hay algun producto agotado?        $hayAgotados")
    println(s"  Todos tienen precio > 0?           $preciosValidos")
    println(s"  Hay algun producto de Hogar > 200? $hogarCaro")

    // -------------------------------------------------------------------------
    // 4. CONTEO — count
    // Situación: el gerente pregunta cuántos productos de Tecnología cuestan
    // más de $50 (gama media/alta) para planificar la vitrina.
    // Lambda: Producto => Boolean
    // -------------------------------------------------------------------------
    println("=== 4. CONTEO (count) ===")
    val tecnoCaros = productos.count(p => p.categoria == "Tecnologia" && p.precio > 50)
    println(s"  Productos de Tecnologia con precio > 50: $tecnoCaros")

    // -------------------------------------------------------------------------
    // 5. EXTREMOS — maxBy / minBy
    // Situación: el área de seguros necesita el producto con mayor capital
    // inmovilizado (precio * stock); marketing quiere el más barato.
    // Lambda: Producto => Double (el criterio de comparación)
    // -------------------------------------------------------------------------
    println("=== 5. EXTREMOS (maxBy / minBy) ===")
    val mayorInventario = productos.maxBy(p => p.precio * p.stock)
    val masBarato       = productos.minBy(p => p.precio)
    println(s"  Mayor valor en inventario: ${mayorInventario.nombre} (${f2(mayorInventario.precio * mayorInventario.stock)})")
    println(s"  Producto mas barato:       ${masBarato.nombre} (${f2(masBarato.precio)})")

    // -------------------------------------------------------------------------
    // 6. APLANAMIENTO — flatMap
    // Situación: el buscador de la tienda necesita un índice de palabras clave
    // a partir de los nombres (igual que el WordCount de Apache Spark).
    // Lambda: Producto => List[String]  (cada producto produce VARIAS palabras)
    // -------------------------------------------------------------------------
    println("=== 6. APLANAMIENTO (flatMap) ===")
    val palabras = productos.flatMap(p => p.nombre.toLowerCase.split(" ").toList)
    println(s"  Total de palabras: ${palabras.size}")
    println(s"  ${palabras.mkString(", ")}")

    // -------------------------------------------------------------------------
    // 7. ACUMULACIÓN — foldLeft / scanLeft
    // Situación: contabilidad necesita el valor total del inventario
    // (foldLeft) y cómo crece ese total producto a producto (scanLeft).
    // Lambda: (Double, Producto) => Double  (acumulador, elemento) => nuevo acumulador
    // -------------------------------------------------------------------------
    println("=== 7. ACUMULACION (foldLeft / scanLeft) ===")
    val total = productos.foldLeft(0.0)((acc, p) => acc + p.precio * p.stock)
    println(s"  Valor total del inventario: ${f2(total)}")
    val evolucion = productos.scanLeft(0.0)((acc, p) => acc + p.precio * p.stock)
    println(s"  Evolucion: ${evolucion.map(v => f2(v)).mkString(", ")}")
  }
}
