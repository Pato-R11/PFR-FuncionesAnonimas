# Funciones Anónimas — Trabajo de Consulta

**Materia:** Programación Funcional · **Lenguajes:** Scala · Java · Python  
**Autor:** Patricio Alexander Rivas Reinoso  
**Entrega:** Semana 5

> Repositorio que estudia las **funciones anónimas (lambdas)**: dónde se usan en sistemas reales, qué son, qué tipos de problemas resuelven sobre listas y cómo las implementan Scala, Java y Python.

---

## Tabla de contenidos

- [Estructura del repositorio](#estructura)
- [Cómo ejecutar el código](#ejecutar)
- [Datos usados en todos los ejemplos](#datos)
- [Parte 1 – Explorar: la programación funcional en el mundo real](#parte-1)
  - [Caso 1: Apache Spark](#caso-spark)
  - [Caso 2: Twitter y *Effective Scala*](#caso-twitter)
- [Parte 2 – Entender: ¿qué es una función anónima?](#parte-2)
- [Parte 3 – Aplicar: ¿en qué problemas sirven las funciones anónimas?](#parte-3)
  - [3.1 Catálogo de tipos de problemas](#p3-catalogo)
  - [1. Selección](#p3-seleccion) · [2. Transformación](#p3-transformacion) · [3. Validación](#p3-validacion) · [4. Conteo](#p3-conteo) · [5. Extremos](#p3-extremos) · [6. Aplanamiento](#p3-aplanamiento) · [7. Acumulación](#p3-acumulacion)
- [Parte 4 – Ampliar: cuatro operaciones nuevas](#parte-4)
- [Parte 5 – Comparar y concluir](#parte-5)
- [Referencias](#referencias)

---

<a id="estructura"></a>
## Estructura del repositorio

```
PFR-FuncionesAnonimas/
├── README.md                         <- documento principal (partes 1 a 5)
├── scala/
│   ├── Producto.scala                <- case class Producto + lista de 14 productos (compartida)
│   └── TiposDeProblemas.scala        <- Parte 3: los 7 tipos de problemas en Scala
├── java/
│   └── TiposDeProblemas.java         <- Parte 3: los 7 tipos de problemas con Streams
├── python/
│   └── tipos_de_problemas.py         <- Parte 3: los 7 tipos de problemas con lambda
├── operaciones/
│   └── OperacionesNuevas.scala       <- Parte 4: sortBy, groupBy, partition, zipWithIndex + combinación
└── img/                              <- capturas de ejecución
```

<a id="ejecutar"></a>
## Cómo ejecutar el código

Todos los comandos se ejecutan **desde la raíz del repositorio**.

| Lenguaje | Requisito | Comando |
|---|---|---|
| Scala (Parte 3) | Scala 3 (o 2.13) con [scala-cli](https://scala-cli.virtuslab.org/) | `scala-cli run scala/` |
| Java (Parte 3) | JDK 17 o superior | `java java/TiposDeProblemas.java` |
| Python (Parte 3) | Python 3.8 o superior | `python python/tipos_de_problemas.py` |
| Scala (Parte 4) | Scala 3 (o 2.13) | `scala-cli run scala/Producto.scala operaciones/OperacionesNuevas.scala` |

Sin scala-cli, con el compilador clásico:

```bash
scalac -d out scala/*.scala && scala -classpath out TiposDeProblemas
scalac -d out scala/Producto.scala operaciones/OperacionesNuevas.scala && scala -classpath out OperacionesNuevas
```

En IntelliJ IDEA basta con abrir la carpeta como proyecto Scala y ejecutar el `main` de `TiposDeProblemas` u `OperacionesNuevas`.

> Las tres versiones de la Parte 3 producen **exactamente la misma salida** (se comprobó con `diff`). Para lograrlo, los precios se imprimen con 2 decimales y punto decimal (`Locale.US`), y los booleanos de Python se imprimen en minúscula.

<a id="datos"></a>
## Datos usados en todos los ejemplos

Siguiendo la sugerencia del enunciado, todos los ejemplos (Partes 3 y 4) trabajan sobre **una sola lista de 14 productos** de una tienda:

```scala
case class Producto(nombre: String, categoria: String, precio: Double, stock: Int)
```

| # | nombre | categoria | precio (USD) | stock |
|---|---|---|---:|---:|
| 1 | Laptop Gamer X15 | Tecnologia | 1250.00 | 5 |
| 2 | Mouse Inalambrico | Tecnologia | 18.50 | 40 |
| 3 | Monitor Curvo 27 | Tecnologia | 289.99 | 12 |
| 4 | Audifonos Bluetooth | Tecnologia | 45.90 | 0 |
| 5 | Teclado Mecanico | Tecnologia | 75.00 | 18 |
| 6 | Arroz Integral 2kg | Alimentos | 3.80 | 120 |
| 7 | Cafe Molido Loja | Alimentos | 7.25 | 60 |
| 8 | Chocolate Amargo 70 | Alimentos | 2.90 | 0 |
| 9 | Silla Ergonomica | Hogar | 159.00 | 7 |
| 10 | Lampara LED Escritorio | Hogar | 24.99 | 30 |
| 11 | Cafetera Espresso | Hogar | 135.50 | 4 |
| 12 | Camiseta Algodon | Ropa | 12.00 | 75 |
| 13 | Chaqueta Impermeable | Ropa | 64.90 | 15 |
| 14 | Zapatos Deportivos | Ropa | 89.99 | 0 |

Equivalentes en los otros lenguajes: `record Producto(...)` en Java y `@dataclass(frozen=True) class Producto` en Python (ambos inmutables, como la `case class`).

---

<a id="parte-1"></a>
## Parte 1 – Explorar: la programación funcional en el mundo real

<a id="caso-spark"></a>
### Caso 1: Apache Spark

| Aspecto | Detalle |
|---|---|
| **Qué problema resuelve** | Procesar volúmenes de datos tan grandes que no caben en una sola máquina (logs, transacciones, datos de sensores), repartiendo el trabajo entre decenas o cientos de nodos de un clúster. |
| **Lenguaje / herramienta** | Spark está escrito en **Scala** y ofrece APIs en Scala, Java, Python (PySpark) y R. |
| **Ventaja del enfoque funcional** | El programador no escribe bucles ni gestiona hilos: describe *qué* hacer con cada elemento pasando funciones anónimas a operaciones como `filter`, `map`, `flatMap` o `reduce`. Como esas lambdas no modifican estado compartido y los *Datasets* son inmutables, Spark puede enviarlas a cada nodo, ejecutarlas en paralelo sobre su porción de datos y combinar los resultados. El mismo código sirve para un archivo de 100 líneas o para un clúster entero. |
| **Fuente** | Documentación oficial: *Spark Quick Start* — https://spark.apache.org/docs/latest/quick-start.html |

**Fragmento de código real** (tomado de la guía *Quick Start* de Spark, en Scala):

```scala
// ¿Cuántas palabras tiene la línea más larga del archivo?
textFile.map(line => line.split(" ").size).reduce((a, b) => if (a > b) a else b)

// Conteo de palabras (patrón MapReduce)
val wordCounts = textFile.flatMap(line => line.split(" ")).groupByKey(identity).count()
```

**Qué hace:**
- `line => line.split(" ").size` es una lambda que convierte cada línea en su número de palabras (una **transformación**); luego `(a, b) => if (a > b) a else b` es otra lambda que, aplicada por `reduce`, se queda siempre con el mayor de dos valores (una **acumulación** que encuentra el **extremo**).
- En el conteo de palabras, `line => line.split(" ")` devuelve *varias* palabras por línea y `flatMap` las junta en un solo Dataset (**aplanamiento**); después se agrupan las palabras iguales y se cuentan.

Este ejemplo inspiró el problema de [aplanamiento](#p3-aplanamiento) de la Parte 3, donde se construye un índice de palabras clave a partir de los nombres de los productos.

<a id="caso-twitter"></a>
### Caso 2: Twitter y *Effective Scala*

| Aspecto | Detalle |
|---|---|
| **Qué problema resuelve** | Construir servicios de **alto volumen** que forman sistemas distribuidos (cada tweet que se publica o consulta pasa por muchos servicios internos que se comunican entre sí). |
| **Lenguaje / herramienta** | **Scala** sobre la JVM, junto con bibliotecas propias como **Finagle** (sistema RPC) y `com.twitter.util.Future`. Twitter publicó su guía interna de buenas prácticas, *Effective Scala*. |
| **Ventaja del enfoque funcional** | La guía recomienda la *programación orientada a valores*: transformar valores en lugar de mutar estado. Esto hace el código más fácil de razonar, y las colecciones inmutables son seguras entre hilos sin sincronización extra. Para la concurrencia, las operaciones remotas devuelven `Future`s que se encadenan con lambdas (`flatMap`, `map`), de modo que los errores se propagan solos y no hace falta controlar hilos a mano. |
| **Fuente** | Eriksen, M. *Effective Scala*, Twitter Inc. — https://twitter.github.io/effectivescala/ (repositorio: https://github.com/twitter/effectivescala) |

**Fragmento de código real** (sección *Collections* de *Effective Scala*):

```scala
val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10))
val orderedVotes = votes
  .groupBy(_._1)
  .map { case (which, counts) =>
    (which, counts.foldLeft(0)(_ + _._2))
  }.toSeq
  .sortBy(_._2)
  .reverse
```

Al ejecutarlo se obtiene `List((scala,12), (python,10), (java,4))`.

**Qué hace:** cuenta los votos por lenguaje. `groupBy(_._1)` agrupa las tuplas por nombre de lenguaje usando la lambda `_._1` (forma abreviada de `t => t._1`); la lambda `{ case (which, counts) => ... }` transforma cada grupo en un par `(lenguaje, total)`, donde `foldLeft(0)(_ + _._2)` **acumula** los votos; finalmente `sortBy(_._2)` ordena por total y `reverse` deja primero al más votado. La guía usa este ejemplo para advertir que, aunque la cadena es compacta, conviene nombrar los resultados intermedios cuando la lógica crece. Esta misma idea (agrupar → resumir → ordenar) es la que se usa en la [combinación de operaciones de la Parte 4](#p4-combinacion).

---

<a id="parte-2"></a>
## Parte 2 – Entender: ¿qué es una función anónima?

### 2.1 ¿Qué es una función anónima y de dónde viene el nombre *lambda*?

Una función anónima es una función que **se escribe justo donde se necesita y no recibe un nombre**: solo declara sus parámetros y la expresión que devuelve. Es útil cuando la lógica es corta y se usa una sola vez, típicamente como argumento de otra función.

El nombre *lambda* viene del **cálculo lambda (λ-cálculo)**, el sistema formal creado por Alonzo Church en la década de 1930 para estudiar qué es una función computable. En esa notación una función se escribe sin nombre, por ejemplo `λx. x + 1` (“la función que recibe x y devuelve x + 1”). Lenguajes como Lisp, Python, Java y C# heredaron la palabra *lambda* para referirse a estas funciones sin nombre.

```scala
(x: Int) => x + 1          // Scala: equivalente directo de λx. x + 1
```

### 2.2 ¿En qué se diferencia de una función nombrada?

Una función nombrada se declara aparte (con `def`), se puede reutilizar y llamar desde cualquier parte. Una función anónima es un **valor**: se define en el mismo lugar donde se usa (o se guarda en una variable) y no ocupa un nombre en el programa. Misma lógica de ambas formas:

```scala
// Función nombrada
def esCaro(p: Producto): Boolean = p.precio > 100
productos.filter(esCaro)

// Función anónima: la misma lógica escrita en el lugar donde se usa
productos.filter(p => p.precio > 100)
productos.filter(_.precio > 100)      // forma abreviada con el marcador _
```

La versión nombrada conviene cuando la lógica se repite o es larga; la anónima, cuando es corta y solo tiene sentido en esa línea.

### 2.3 ¿Qué significa que las funciones sean *ciudadanos de primera clase*?

Significa que el lenguaje trata a las funciones **igual que a cualquier otro valor** (un `Int` o un `String`): se pueden guardar en variables, pasar como argumento a otra función y devolver como resultado.

```scala
val esCaro: Producto => Boolean = p => p.precio > 100   // guardada en una variable
productos.filter(esCaro)                                // pasada como argumento

def mayorA(limite: Double): Producto => Boolean =       // devuelta como resultado
  p => p.precio > limite
productos.filter(mayorA(50))
```

Por eso se puede pasar una lambda a `map` o `filter`: estos métodos son **funciones de orden superior**, es decir, su parámetro es una función. La firma de `filter` en una `List[Producto]` es `filter(p: Producto => Boolean)`: pide “algo de tipo función de `Producto` a `Boolean`”, y una lambda es exactamente un valor de ese tipo.

### 2.4 ¿Qué es un *closure*?

Un closure (clausura) es una función que **“captura” variables definidas fuera de ella** y las recuerda aunque se ejecute en otro momento o lugar.

```scala
val iva = 0.15                                   // variable definida FUERA de la lambda
val precioConIva = (p: Producto) => p.precio * (1 + iva)   // la lambda usa `iva`

productos.map(precioConIva)   // cada llamada usa el valor capturado de iva
```

La lambda no recibe `iva` como parámetro: lo toma de su entorno. En la Parte 3 aparece el mismo mecanismo cuando el criterio de un filtro depende de un valor externo (por ejemplo el `limite` de `mayorA` en la sección anterior, que queda capturado dentro de la lambda devuelta).

---

<a id="parte-3"></a>
## Parte 3 – Aplicar: ¿en qué problemas sirven las funciones anónimas?

Se documentan **los 7 tipos de problemas** de la tabla del enunciado. El código completo y comentado está en [`scala/TiposDeProblemas.scala`](scala/TiposDeProblemas.scala), [`java/TiposDeProblemas.java`](java/TiposDeProblemas.java) y [`python/tipos_de_problemas.py`](python/tipos_de_problemas.py); aquí se muestra solo la parte esencial de cada solución.

<a id="p3-catalogo"></a>
### 3.1 Catálogo de tipos de problemas

| # | Tipo | Pregunta que responde | Scala | Java (Streams) | Python | Lambda recibida |
|---|---|---|---|---|---|---|
| 1 | [Selección](#p3-seleccion) | ¿Qué elementos cumplen una condición? | `filter` | `filter` | `filter` | `Producto => Boolean` |
| 2 | [Transformación](#p3-transformacion) | ¿Cómo convierto cada elemento en otro valor? | `map` | `map` | `map` | `Producto => B` |
| 3 | [Validación](#p3-validacion) | ¿Existe alguno? ¿Todos cumplen? | `exists`, `forall` | `anyMatch`, `allMatch` | `any`, `all` + `map` | `Producto => Boolean` |
| 4 | [Conteo](#p3-conteo) | ¿Cuántos cumplen una condición? | `count` | `filter` + `count` | `len` + `filter` | `Producto => Boolean` |
| 5 | [Extremos](#p3-extremos) | ¿Cuál es el mejor/peor según un criterio? | `maxBy`, `minBy` | `max`/`min` + `Comparator` | `max`/`min` con `key` | `Producto => Double` |
| 6 | [Aplanamiento](#p3-aplanamiento) | ¿Cómo junto los resultados de cada elemento? | `flatMap` | `flatMap` | `chain.from_iterable` + `map` | `Producto => List[B]` |
| 7 | [Acumulación](#p3-acumulacion) | ¿Qué valor resume la lista? ¿Cómo evoluciona? | `foldLeft`, `scanLeft` | `reduce`, `collect` | `reduce`, `accumulate` | `(Acum, Producto) => Acum` |

Capturas de la ejecución completa de cada versión: [Scala](img/parte3_scala_completa.png) · [Java](img/parte3_java_completa.png) · [Python](img/parte3_python_completa.png).

---

<a id="p3-seleccion"></a>
### 1. Selección — `filter`

**Situación real:** el encargado de bodega necesita la lista de productos con **menos de 10 unidades** para enviar la orden de reposición al proveedor antes de que se agoten (o porque ya se agotaron).

**Solución:**

```scala
// Scala
val stockBajo = productos.filter(p => p.stock < 10)
```

```java
// Java
List<Producto> stockBajo = productos.stream()
    .filter(p -> p.stock() < 10)
    .toList();
```

```python
# Python
stock_bajo = list(filter(lambda p: p.stock < 10, productos))
```

**Salida (idéntica en los tres lenguajes):**

```
=== 1. SELECCION (filter) ===
  - Laptop Gamer X15 (5 u.)
  - Audifonos Bluetooth (0 u.)
  - Chocolate Amargo 70 (0 u.)
  - Silla Ergonomica (7 u.)
  - Cafetera Espresso (4 u.)
  - Zapatos Deportivos (0 u.)
```

![Selección en Scala, Java y Python](img/p3_1_seleccion.png)

---

<a id="p3-transformacion"></a>
### 2. Transformación — `map`

**Situación real:** en *Black Friday* la tienda aplica un **20 % de descuento** a todo el catálogo y la página web debe mostrar, para cada producto, el precio original y el precio final.

**Solución:**

```scala
// Scala
val conDescuento = productos.map(p => s"${p.nombre}: ${f2(p.precio)} -> ${f2(p.precio * 0.8)}")
```

```java
// Java
List<String> conDescuento = productos.stream()
    .map(p -> p.nombre() + ": " + f2(p.precio()) + " -> " + f2(p.precio() * 0.8))
    .toList();
```

```python
# Python
con_descuento = list(map(lambda p: f"{p.nombre}: {f2(p.precio)} -> {f2(p.precio * 0.8)}", productos))
```

(`f2` es una función auxiliar que formatea con 2 decimales.)

**Salida (idéntica en los tres lenguajes):**

```
=== 2. TRANSFORMACION (map) ===
  Laptop Gamer X15: 1250.00 -> 1000.00
  Mouse Inalambrico: 18.50 -> 14.80
  Monitor Curvo 27: 289.99 -> 231.99
  Audifonos Bluetooth: 45.90 -> 36.72
  Teclado Mecanico: 75.00 -> 60.00
  Arroz Integral 2kg: 3.80 -> 3.04
  Cafe Molido Loja: 7.25 -> 5.80
  Chocolate Amargo 70: 2.90 -> 2.32
  Silla Ergonomica: 159.00 -> 127.20
  Lampara LED Escritorio: 24.99 -> 19.99
  Cafetera Espresso: 135.50 -> 108.40
  Camiseta Algodon: 12.00 -> 9.60
  Chaqueta Impermeable: 64.90 -> 51.92
  Zapatos Deportivos: 89.99 -> 71.99
```

![Transformación en Scala, Java y Python](img/p3_2_transformacion.png)

---

<a id="p3-validacion"></a>
### 3. Validación — `exists` / `forall`

**Situación real:** antes de publicar el catálogo en la tienda en línea, un proceso automático verifica que **no haya productos agotados** a la venta y que **todos tengan un precio válido** (mayor que cero). También se consulta si existe algún artículo de Hogar que supere los $200.

**Solución:**

```scala
// Scala
val hayAgotados    = productos.exists(p => p.stock == 0)
val preciosValidos = productos.forall(p => p.precio > 0)
val hogarCaro      = productos.exists(p => p.categoria == "Hogar" && p.precio > 200)
```

```java
// Java
boolean hayAgotados    = productos.stream().anyMatch(p -> p.stock() == 0);
boolean preciosValidos = productos.stream().allMatch(p -> p.precio() > 0);
boolean hogarCaro      = productos.stream().anyMatch(p -> p.categoria().equals("Hogar") && p.precio() > 200);
```

```python
# Python
hay_agotados    = any(map(lambda p: p.stock == 0, productos))
precios_validos = all(map(lambda p: p.precio > 0, productos))
hogar_caro      = any(map(lambda p: p.categoria == "Hogar" and p.precio > 200, productos))
```

**Salida (idéntica en los tres lenguajes):**

```
=== 3. VALIDACION (exists / forall) ===
  Hay algun producto agotado?        true
  Todos tienen precio > 0?           true
  Hay algun producto de Hogar > 200? false
```

![Validación en Scala, Java y Python](img/p3_3_validacion.png)

---

<a id="p3-conteo"></a>
### 4. Conteo — `count`

**Situación real:** el gerente quiere saber **cuántos productos de Tecnología cuestan más de $50** para decidir cuántos espacios de la vitrina de gama media/alta necesita.

**Solución:**

```scala
// Scala
val tecnoCaros = productos.count(p => p.categoria == "Tecnologia" && p.precio > 50)
```

```java
// Java (count() de Streams no recibe predicado: se filtra antes)
long tecnoCaros = productos.stream()
    .filter(p -> p.categoria().equals("Tecnologia") && p.precio() > 50)
    .count();
```

```python
# Python (no hay count con predicado: se cuenta lo que deja pasar filter)
tecno_caros = len(list(filter(lambda p: p.categoria == "Tecnologia" and p.precio > 50, productos)))
```

**Salida (idéntica en los tres lenguajes):**

```
=== 4. CONTEO (count) ===
  Productos de Tecnologia con precio > 50: 3
```

![Conteo en Scala, Java y Python](img/p3_4_conteo.png)

---

<a id="p3-extremos"></a>
### 5. Extremos — `maxBy` / `minBy`

**Situación real:** el área de seguros necesita saber qué producto concentra **más dinero inmovilizado en bodega** (precio × stock) para asegurarlo; marketing quiere el **producto más barato** para usarlo como gancho en una promoción.

**Solución:**

```scala
// Scala
val mayorInventario = productos.maxBy(p => p.precio * p.stock)
val masBarato       = productos.minBy(p => p.precio)
```

```java
// Java (la lambda construye un Comparator)
Producto mayorInventario = productos.stream()
    .max(Comparator.comparingDouble(p -> p.precio() * p.stock()))
    .orElseThrow();
Producto masBarato = productos.stream()
    .min(Comparator.comparingDouble(p -> p.precio()))
    .orElseThrow();
```

```python
# Python
mayor_inventario = max(productos, key=lambda p: p.precio * p.stock)
mas_barato       = min(productos, key=lambda p: p.precio)
```

**Salida (idéntica en los tres lenguajes):**

```
=== 5. EXTREMOS (maxBy / minBy) ===
  Mayor valor en inventario: Laptop Gamer X15 (6250.00)
  Producto mas barato:       Chocolate Amargo 70 (2.90)
```

![Extremos en Scala, Java y Python](img/p3_5_extremos.png)

---

<a id="p3-aplanamiento"></a>
### 6. Aplanamiento — `flatMap`

**Situación real:** el buscador de la tienda en línea necesita un **índice de palabras clave** construido a partir de los nombres de los productos. Cada producto aporta *varias* palabras y todas deben quedar en una sola lista (es la primera etapa del *WordCount* de [Apache Spark](#caso-spark)).

**Solución:**

```scala
// Scala: la lambda devuelve una LISTA por producto; flatMap las une
val palabras = productos.flatMap(p => p.nombre.toLowerCase.split(" ").toList)
```

```java
// Java: la lambda debe devolver un Stream
List<String> palabras = productos.stream()
    .flatMap(p -> List.of(p.nombre().toLowerCase().split(" ")).stream())
    .toList();
```

```python
# Python: no existe flatMap; map aplica la lambda y chain.from_iterable aplana
palabras = list(chain.from_iterable(map(lambda p: p.nombre.lower().split(" "), productos)))
```

**Salida (idéntica en los tres lenguajes):**

```
=== 6. APLANAMIENTO (flatMap) ===
  Total de palabras: 34
  laptop, gamer, x15, mouse, inalambrico, monitor, curvo, 27, audifonos, bluetooth, teclado, mecanico, arroz, integral, 2kg, cafe, molido, loja, chocolate, amargo, 70, silla, ergonomica, lampara, led, escritorio, cafetera, espresso, camiseta, algodon, chaqueta, impermeable, zapatos, deportivos
```

Con `map` en lugar de `flatMap` se obtendría una lista de 14 listas; `flatMap` produce directamente las 34 palabras.

![Aplanamiento en Scala, Java y Python](img/p3_6_aplanamiento.png)

---

<a id="p3-acumulacion"></a>
### 7. Acumulación — `foldLeft` / `scanLeft`

**Situación real:** contabilidad necesita el **valor total del inventario** (suma de precio × stock) para el cierre de mes, y además quiere ver **cómo crece ese total** a medida que se registra cada producto (para detectar qué productos aportan más).

**Solución:**

```scala
// Scala
val total     = productos.foldLeft(0.0)((acc, p) => acc + p.precio * p.stock)
val evolucion = productos.scanLeft(0.0)((acc, p) => acc + p.precio * p.stock)
```

```java
// Java: reduce(identidad, acumulador, combinador) equivale a foldLeft
double total = productos.stream()
    .reduce(0.0, (acc, p) -> acc + p.precio() * p.stock(), Double::sum);

// Streams no tiene scanLeft: se simula con collect y una lista que guarda los parciales
List<Double> evolucion = productos.stream()
    .map(p -> p.precio() * p.stock())
    .collect(() -> new ArrayList<>(List.of(0.0)),
             (lista, v) -> lista.add(lista.get(lista.size() - 1) + v),
             ArrayList::addAll);
```

```python
# Python
total     = reduce(lambda acc, p: acc + p.precio * p.stock, productos, 0.0)
evolucion = list(accumulate(productos, lambda acc, p: acc + p.precio * p.stock, initial=0.0))
```

**Salida (idéntica en los tres lenguajes):**

```
=== 7. ACUMULACION (foldLeft / scanLeft) ===
  Valor total del inventario: 16989.08
  Evolucion: 0.00, 6250.00, 6990.00, 10469.88, 10469.88, 11819.88, 12275.88, 12710.88, 12710.88, 13823.88, 14573.58, 15115.58, 16015.58, 16989.08, 16989.08
```

El último valor de `scanLeft` coincide con el resultado de `foldLeft`; los valores repetidos corresponden a productos agotados (aportan 0).

![Acumulación en Scala, Java y Python](img/p3_7_acumulacion.png)

---

<a id="parte-4"></a>
## Parte 4 – Ampliar: cuatro operaciones nuevas

Código: [`operaciones/OperacionesNuevas.scala`](operaciones/OperacionesNuevas.scala) (usa la misma lista de productos de [Datos](#datos)).

<a id="p4-fichas"></a>
### 4.1 Operaciones elegidas

| Familia | Operación | ¿Recibe lambda? |
|---|---|---|
| Ordenar | `sortBy` | Sí: `Producto => B` (clave de orden) |
| Agrupar | `groupBy` | Sí: `Producto => K` (clave del grupo) |
| Dividir una lista | `partition` | Sí: `Producto => Boolean` |
| Combinar listas | `zipWithIndex` | No: se encadena con `map` |

### 4.2 Fichas

#### Ficha 1 — `sortBy`

1. **Qué hace:** devuelve una nueva lista ordenada de menor a mayor según una clave que calcula la lambda.
2. **Lambda que recibe:** `Producto => B`, donde `B` es cualquier tipo que se pueda ordenar (`Int`, `Double`, `String`, tuplas…). Devuelve la clave, no un booleano. Negar la clave (`-p.precio`) invierte el orden.
3. **Ejemplo:**
   ```scala
   val porPrecioDesc = productos.sortBy(p => -p.precio)
   ```
   ```
      1250.00  Laptop Gamer X15
       289.99  Monitor Curvo 27
       159.00  Silla Ergonomica
       ...
         3.80  Arroz Integral 2kg
         2.90  Chocolate Amargo 70
   ```
4. **Problema que resuelve:** mostrar en la web el catálogo “de mayor a menor precio” o un ranking de ventas. Se parece a [Extremos](#p3-extremos): `maxBy` entrega solo el primero, mientras que `sortBy` ordena toda la lista según el mismo tipo de criterio.

#### Ficha 2 — `groupBy`

1. **Qué hace:** reparte los elementos en grupos según una clave y devuelve un `Map[clave, List[elementos]]`.
2. **Lambda que recibe:** `Producto => K`; devuelve la clave del grupo al que pertenece cada producto (aquí, su categoría).
3. **Ejemplo:**
   ```scala
   val porCategoria: Map[String, List[Producto]] = productos.groupBy(p => p.categoria)
   ```
   ```
   Alimentos (3): Arroz Integral 2kg, Cafe Molido Loja, Chocolate Amargo 70
   Hogar (3): Silla Ergonomica, Lampara LED Escritorio, Cafetera Espresso
   Ropa (3): Camiseta Algodon, Chaqueta Impermeable, Zapatos Deportivos
   Tecnologia (5): Laptop Gamer X15, Mouse Inalambrico, Monitor Curvo 27, Audifonos Bluetooth, Teclado Mecanico
   ```
4. **Problema que resuelve:** reportes por categoría, por cliente o por mes (“ventas por sucursal”). Es un **tipo nuevo, *Agrupación***, que no está en la tabla 3.1: no selecciona ni transforma, sino que clasifica. Suele combinarse con [Acumulación](#p3-acumulacion) para resumir cada grupo.

#### Ficha 3 — `partition`

1. **Qué hace:** divide la lista en dos en **una sola pasada**: los que cumplen la condición y los que no.
2. **Lambda que recibe:** `Producto => Boolean`. Devuelve una tupla `(cumplen, noCumplen)`.
3. **Ejemplo:**
   ```scala
   val (disponibles, agotados) = productos.partition(p => p.stock > 0)
   ```
   ```
   Disponibles (11): Laptop Gamer X15, Mouse Inalambrico, Monitor Curvo 27, Teclado Mecanico, ...
   Agotados (3): Audifonos Bluetooth, Chocolate Amargo 70, Zapatos Deportivos
   ```
4. **Problema que resuelve:** separar transacciones aprobadas y rechazadas, alumnos aprobados y reprobados, o productos que se publican y los que se ocultan. Es una variante de [Selección](#p3-seleccion) que conserva también los elementos descartados, sin recorrer la lista dos veces.

#### Ficha 4 — `zipWithIndex`

1. **Qué hace:** une cada elemento con su posición (empezando en 0) y produce una lista de pares `(elemento, índice)`.
2. **Lambda que recibe:** **ninguna**. Se encadena con `map`, que recibe la lambda `{ case (p, i) => ... }` y usa el índice.
3. **Ejemplo:**
   ```scala
   val catalogoNumerado = productos
     .zipWithIndex
     .map { case (p, i) => s"${i + 1}. ${p.nombre}" }
   ```
   ```
   1. Laptop Gamer X15
   2. Mouse Inalambrico
   ...
   14. Zapatos Deportivos
   ```
4. **Problema que resuelve:** numerar un catálogo, una factura o un ranking, o generar códigos secuenciales. Combinado con `map` es una [Transformación](#p3-transformacion) que además necesita conocer la posición de cada elemento.

Captura de la ejecución de las fichas y de la combinación:

![Parte 4 en Scala](img/parte4_operaciones.png)

<a id="p4-combinacion"></a>
### 4.3 Combinar operaciones

**Pregunta:** *¿Cuál es el ranking de categorías según el valor de su inventario (precio × stock), y cuántos productos agotados tiene cada una?*

```scala
val ranking = productos
  .groupBy(p => p.categoria)                                   // 1. nueva
  .map { case (cat, ps) =>                                     // 2. vista en clase
    (cat,
     ps.foldLeft(0.0)((acc, p) => acc + p.precio * p.stock),
     ps.count(p => p.stock == 0))
  }
  .toList
  .sortBy { case (_, valor, _) => -valor }                     // 3. nueva
  .zipWithIndex                                                // 4. nueva
  .map { case ((cat, valor, agot), i) => s"${i + 1}. $cat: ${f2(valor)} (agotados: $agot)" }  // 5. vista en clase
```

**Salida:**

```
1. Tecnologia: 11819.88 (agotados: 1)
2. Hogar: 2404.70 (agotados: 0)
3. Ropa: 1873.50 (agotados: 1)
4. Alimentos: 891.00 (agotados: 1)
```

**Qué hace cada paso:**

1. **`groupBy` (nueva):** la lambda `p => p.categoria` reparte los 14 productos en un `Map` con 4 grupos.
2. **`map` (vista en clase):** la lambda `{ case (cat, ps) => ... }` resume cada grupo en una tupla `(categoría, valor, agotados)`. Dentro se usan otras dos operaciones vistas en clase: `foldLeft` acumula precio × stock y `count` cuenta los productos con stock 0.
3. **`toList` + `sortBy` (nueva):** el `Map` no tiene orden, así que se convierte en lista y se ordena con la lambda `{ case (_, valor, _) => -valor }` (de mayor a menor valor).
4. **`zipWithIndex` (nueva):** agrega la posición de cada categoría en el ranking.
5. **`map` (vista en clase):** da formato a cada línea usando la posición (`i + 1`).

Se usaron **tres operaciones nuevas** (`groupBy`, `sortBy`, `zipWithIndex`) y **tres vistas en clase** (`map`, `foldLeft`, `count`). Es el mismo patrón agrupar → resumir → ordenar del ejemplo de [Twitter](#caso-twitter).

---

<a id="parte-5"></a>
## Parte 5 – Comparar y concluir

<a id="p5-tabla"></a>
### 5.1 Scala, Java y Python frente a frente

La comparación se basa en las soluciones de la [Parte 3](#parte-3).

| Criterio | Scala | Java | Python |
|---|---|---|---|
| **Sintaxis de la lambda** | `p => p.stock < 10`, o abreviada `_.stock < 10`. Puede tener varias líneas entre llaves `{ ... }` y usar *pattern matching*: `{ case (cat, ps) => ... }`. | `p -> p.stock() < 10`. Para varias líneas: `p -> { ...; return x; }`. También referencias a métodos: `Double::sum`. | `lambda p: p.stock < 10`. Solo una expresión en una línea. |
| **Cómo se declara el tipo** | Lo infiere el compilador a partir del método (`filter` espera `Producto => Boolean`). Se puede escribir explícito: `(p: Producto) => ...`. Las funciones tienen tipo propio: `Producto => Boolean`. | La lambda no tiene tipo propio: toma el tipo de una **interfaz funcional** según el contexto (`Predicate<Producto>`, `Function<Producto,String>`, `Comparator<Producto>`, `BiFunction`…). | Tipado dinámico: no se declara nada. Opcionalmente se anota con `Callable[[Producto], bool]`, pero no se verifica al ejecutar. |
| **Operaciones con listas disponibles** | Todas como métodos de `List`: `map`, `filter`, `exists`, `forall`, `count`, `maxBy`, `minBy`, `flatMap`, `foldLeft`, `scanLeft`, `groupBy`, `partition`… Las 7 soluciones fueron de una línea. | Hay que pasar por `stream()` y terminar con una operación final (`toList`, `count`, `max`…). Faltan `count(pred)`, `maxBy` (se usa `Comparator`), `scanLeft` y `exists`/`forall` cambian de nombre (`anyMatch`/`allMatch`). | Funciones sueltas, no métodos: `map`, `filter`, `any`, `all`, `max`/`min` con `key`, `functools.reduce`, `itertools.accumulate`. No existen `flatMap` ni `count(pred)`. `map`/`filter` devuelven iteradores perezosos (hay que envolver en `list`). |
| **Restricción del lenguaje** | Pocas; la principal es no abusar de la notación `_` porque puede volverse críptica. | Las variables locales que captura una lambda deben ser *effectively final* (no se pueden reasignar). Un stream solo se puede recorrer una vez. Las lambdas no pueden lanzar excepciones *checked* si la interfaz no lo declara. | El cuerpo de una `lambda` debe ser **una sola expresión**: no admite asignaciones, bucles ni varias sentencias. Para lógica más compleja hay que usar `def` o comprensiones de listas. |
| **Legibilidad (mi opinión)** | La más clara: el código se lee casi como la pregunta (“productos que existen con stock 0”). | Correcta pero más verbosa: `stream()`, `toList()`, los paréntesis de los *getters* del `record` y el `Comparator` añaden ruido. | Muy legible para casos simples (`max(..., key=...)` es excelente), pero anidar `list(filter(lambda ...))` se lee de adentro hacia afuera. |

**¿En qué lenguaje fue más natural resolver los problemas?**
En **Scala**. Las siete operaciones de la tabla 3.1 existen con ese mismo nombre como métodos de `List`, así que cada problema se resolvió con una sola llamada y una lambda corta, y el compilador infirió todos los tipos. En Java hubo que “traducir” algunas operaciones (`exists` → `anyMatch`, `maxBy` → `max(Comparator...)`), y en Python combinar varias funciones (`len(list(filter(...)))` para contar, `chain.from_iterable(map(...))` para aplanar).

**¿En qué tipo de problema hubo más diferencias y por qué?**
En la **[Acumulación](#p3-acumulacion)**, concretamente en `scanLeft`. Scala lo trae listo; Python lo tiene en la biblioteca estándar como `itertools.accumulate` (con el parámetro `initial` para el 0 inicial); pero **Java Streams no tiene ninguna operación equivalente**, y hubo que simularla con `collect` y una lista que se va llenando dentro de la lambda. Esa solución funciona en secuencia pero no sería correcta en paralelo, porque los Streams están pensados para operaciones que se puedan combinar en cualquier orden, y un acumulado paso a paso depende justamente del orden. También hubo diferencias claras en el **[Aplanamiento](#p3-aplanamiento)**: en Java la lambda de `flatMap` debe devolver un `Stream` (no una lista) y en Python ni siquiera existe `flatMap`.

<a id="p5-conclusion"></a>
### 5.2 Conclusión

Las funciones anónimas son la pieza que hace que las operaciones con listas sean **genéricas y reutilizables**: `filter`, `map`, `foldLeft` o `groupBy` definen *cómo* se recorre la lista, y la lambda define *qué* se quiere hacer con cada elemento. Gracias a eso los siete tipos de problemas de la [Parte 3](#parte-3) se resolvieron sin escribir un solo bucle ni variable mutable: cambiar la pregunta (“menos de 10 unidades” por “más de $50”) solo exige cambiar la lambda, no el algoritmo. Además, al poder encadenarse (como en la [Parte 4](#p4-combinacion)), cada paso queda pequeño y fácil de leer, y el resultado es el mismo en Scala, Java o Python porque el concepto es el mismo en los tres.

Esa separación entre *recorrer* y *qué hacer* es exactamente lo que aprovecha **[Apache Spark](#caso-spark)**: como las lambdas que el programador pasa a `map`, `flatMap` o `reduce` no dependen de un estado compartido, Spark puede enviarlas a cientos de máquinas y ejecutarlas en paralelo sobre millones de registros. El índice de palabras clave del problema de aplanamiento usa la misma lambda que el *WordCount* de Spark; la diferencia es solo el tamaño de la lista. Por eso las funciones anónimas no son un detalle de sintaxis, sino la forma en que se expresa la lógica de negocio tanto en un ejercicio de clase como en sistemas de procesamiento de datos a gran escala.

---

<a id="referencias"></a>
## Referencias

1. Apache Software Foundation. *Quick Start — Spark Documentation*. https://spark.apache.org/docs/latest/quick-start.html
2. Eriksen, M. *Effective Scala*. Twitter Inc. https://twitter.github.io/effectivescala/ — Repositorio: https://github.com/twitter/effectivescala
3. Documentación oficial de Scala. *Scala 3 Book — Anonymous Functions* y *Collections Methods*. https://docs.scala-lang.org
4. Oracle. *Lambda Expressions — The Java Tutorials*. https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
5. Oracle. *java.util.stream (Java SE 17 API)*. https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/package-summary.html
6. Python Software Foundation. *Functional Programming HOWTO*. https://docs.python.org/3/howto/functional.html
7. Python Software Foundation. *itertools — accumulate, chain*. https://docs.python.org/3/library/itertools.html
8. Alama, J. y Korbmacher, J. *The Lambda Calculus*. Stanford Encyclopedia of Philosophy. https://plato.stanford.edu/entries/lambda-calculus/
9. Winitzki, S. *The Science of Functional Programming: A Tutorial, with Examples in Scala*. https://github.com/winitzki/sofp
10. **Uso de herramientas de IA:** se utilizó Claude (Anthropic) como apoyo para estructurar el repositorio, redactar borradores del README, generar y verificar el código de ejemplo y producir las capturas de ejecución. El contenido fue revisado, ejecutado y ajustado por el autor.
