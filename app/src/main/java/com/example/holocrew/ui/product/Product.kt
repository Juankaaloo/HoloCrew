/**
 * Product.kt
 *
 * Modelos de datos para los productos de la tienda HoloCrew.
 * Contiene las data classes que representan un producto y sus opciones de color,
 * así como una lista centralizada de datos mock que se reutiliza en todas las
 * pantallas de la app (Available, ProductDetail, etc.).
 *
 * Al ser un proyecto solo front-end, todos los datos están definidos aquí
 * de forma estática. En una versión futura con backend, estos datos vendrían
 * de una API o base de datos.
 */
package com.example.holocrew.ui.product

import androidx.compose.ui.graphics.Color
import com.example.holocrew.R

// ══════════════════════════════════════════════════════════════════════════════
// MODELOS DE DATOS
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Representa las opciones de color disponibles para un producto.
 *
 * @param name Nombre del color que se muestra al usuario (ej: "Negro", "Azul")
 * @param colorHex Código hexadecimal del color (ej: "#000000") usado para
 *                 parsear y mostrar el círculo de color en la UI
 */
data class ColorOption(
    val name: String,
    val colorHex: String
) {
    /**
     * Convierte el string hexadecimal a un objeto Color de Compose.
     * Se usa directamente en los composables para pintar el círculo de color.
     */
    val color: Color
        get() = Color(android.graphics.Color.parseColor(colorHex))
}

/**
 * Modelo completo de un producto de la tienda.
 * Contiene toda la información necesaria para mostrarlo tanto en listados
 * (Available) como en la pantalla de detalle (ProductDetail).
 *
 * @param id Identificador único del producto (usado para navegación)
 * @param title Nombre principal del producto
 * @param subtitle Descripción corta o tipo de producto
 * @param category Categoría del producto (Ropa, Denim, Accesorios, etc.)
 * @param imageRes ID del recurso drawable para la imagen del producto
 * @param status Estado actual (Nuevo, Exclusivo, Más vendido, En oferta, etc.)
 * @param price Precio actual formateado como string (ej: "$129.99")
 * @param originalPrice Precio original antes del descuento (null si no hay oferta)
 * @param brand Marca del producto
 * @param description Descripción detallada del producto
 * @param rating Puntuación media del producto (0.0 a 5.0)
 * @param reviewCount Número total de reseñas
 * @param sizes Lista de tallas disponibles (ej: ["XS", "S", "M", "L", "XL"])
 * @param colors Lista de opciones de color disponibles
 * @param features Lista de características destacadas del producto
 * @param material Composición del material
 * @param careInstructions Instrucciones de cuidado y lavado
 * @param sku Código SKU del producto (referencia interna)
 * @param publishedDate Fecha de publicación del producto
 * @param isFavorite Si el producto está marcado como favorito por el usuario
 */
data class ProductDetail(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val imageRes: Int,
    val status: String,
    val price: String,
    val originalPrice: String? = null,
    val brand: String = "Holo Crew",
    val description: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val sizes: List<String> = emptyList(),
    val colors: List<ColorOption> = emptyList(),
    val features: List<String> = emptyList(),
    val material: String = "",
    val careInstructions: String = "",
    val sku: String = "",
    val publishedDate: String = "",
    val isFavorite: Boolean = false
)

// ══════════════════════════════════════════════════════════════════════════════
// DATOS MOCK - Lista centralizada de productos
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Lista estática de todos los productos disponibles en la tienda.
 *
 * Esta lista se usa como fuente de datos única en toda la app:
 *  - AvailableScreen la usa para mostrar el catálogo con filtros
 *  - ProductDetailScreen busca un producto por ID en esta lista
 *
 * Cada producto tiene información completa: imagen, precio, tallas, colores,
 * descripción, características, etc.
 *
 * NOTA: Las imágenes hacen referencia a drawables en res/drawable/.
 * Asegúrate de tener todos los recursos de imagen disponibles en el proyecto.
 */
val mockProducts = listOf(
    ProductDetail(
        id = 1,
        title = "Holo Pannel Hoodie",
        subtitle = "Hoodie - Hombre",
        category = "Ropa",
        imageRes = R.drawable.pannels_hoodie,
        status = "Nuevo",
        price = "$129.99",
        originalPrice = "$149.99",
        brand = "Holo Crew",
        description = "Este hoodie resistente, fabricado con tejido premium, es perfecto para el día a día.",
        rating = 4.5f,
        reviewCount = 128,
        sizes = listOf("XS", "S", "M", "L", "XL", "XXL"),
        colors = listOf(
            ColorOption("Negro", "#000000"),
            ColorOption("Gris", "#808080"),
            ColorOption("Verde", "#4CAF50")
        ),
        features = listOf("Capucha ajustable", "Bolsillo canguro", "Material premium", "Corte moderno"),
        material = "Algodón 80%, Poliéster 20%",
        careInstructions = "Lavar a máquina con agua fría. No usar lejía.",
        sku = "HOLO-HD-001",
        publishedDate = "27 de octubre de 2025"
    ),
    ProductDetail(
        id = 2,
        title = "Denim Bison Holo",
        subtitle = "Denim premium edición limitada",
        category = "Denim",
        imageRes = R.drawable.bison_denim_holo,
        status = "Exclusivo",
        price = "$89.99",
        originalPrice = "$109.99",
        brand = "Holo Crew",
        description = "Denim premium de edición limitada con acabados exclusivos de la colección HoloCrew.",
        rating = 4.7f,
        reviewCount = 342,
        sizes = listOf("28", "30", "32", "34", "36"),
        colors = listOf(
            ColorOption("Azul", "#2196F3"),
            ColorOption("Negro", "#000000")
        ),
        features = listOf("Denim premium", "Edición limitada", "Corte slim", "Acabados exclusivos"),
        material = "Algodón 100%",
        careInstructions = "Lavar del revés con agua fría. No usar secadora.",
        sku = "HOLO-DN-002",
        publishedDate = "15 de marzo de 2025"
    ),
    ProductDetail(
        id = 3,
        title = "Boxer Holo White",
        subtitle = "Boxer Holo - Corte ajustado",
        category = "Ropa Interior",
        imageRes = R.drawable.boxer_holo_white,
        status = "Más vendido",
        price = "$30.99",
        brand = "Holo Crew",
        description = "Boxer de corte ajustado con tejido transpirable y elástico premium.",
        rating = 4.3f,
        reviewCount = 89,
        sizes = listOf("S", "M", "L", "XL"),
        colors = listOf(
            ColorOption("Blanco", "#FFFFFF"),
            ColorOption("Negro", "#000000"),
            ColorOption("Gris", "#808080")
        ),
        features = listOf("Tejido transpirable", "Elástico premium", "Corte ajustado", "Secado rápido"),
        material = "Algodón 95%, Elastano 5%",
        careInstructions = "Lavar a máquina 30°C. No usar lejía.",
        sku = "HOLO-BX-003",
        publishedDate = "10 de noviembre de 2024"
    ),
    ProductDetail(
        id = 4,
        title = "Glory Holo Polo",
        subtitle = "Polo premium",
        category = "Ropa",
        imageRes = R.drawable.glory_holo_polo,
        status = "En oferta",
        price = "$199.99",
        originalPrice = "$249.99",
        brand = "Holo Crew",
        description = "Polo premium de la línea Glory con bordado exclusivo y corte elegante.",
        rating = 4.6f,
        reviewCount = 215,
        sizes = listOf("S", "M", "L", "XL", "XXL"),
        colors = listOf(
            ColorOption("Blanco", "#FFFFFF"),
            ColorOption("Negro", "#000000"),
            ColorOption("Azul", "#1565C0")
        ),
        features = listOf("Bordado exclusivo", "Tejido piqué", "Corte elegante", "Cuello reforzado"),
        material = "Algodón piqué 100%",
        careInstructions = "Lavar a máquina con agua fría. Planchar a temperatura baja.",
        sku = "HOLO-PL-004",
        publishedDate = "5 de septiembre de 2025"
    ),
    ProductDetail(
        id = 5,
        title = "Shoulder Bag",
        subtitle = "Bag Holo BlackLeather",
        category = "Accesorios",
        imageRes = R.drawable.shoulder_bag_holo_blackleather,
        status = "Exclusivo",
        price = "$249.99",
        brand = "Holo Crew",
        description = "Bolso de hombro en piel negra con detalles metálicos de la colección HoloCrew.",
        rating = 4.8f,
        reviewCount = 67,
        sizes = listOf("Único"),
        colors = listOf(
            ColorOption("Negro", "#000000"),
            ColorOption("Marrón", "#795548")
        ),
        features = listOf("Piel genuina", "Detalles metálicos", "Compartimentos internos", "Correa ajustable"),
        material = "Piel genuina, herrajes metálicos",
        careInstructions = "Limpiar con paño húmedo. Aplicar crema para piel.",
        sku = "HOLO-BG-005",
        publishedDate = "20 de agosto de 2025"
    ),
    ProductDetail(
        id = 6,
        title = "Racing Cap",
        subtitle = "Exclusive cap - Solo 500 unidades",
        category = "Accesorios",
        imageRes = R.drawable.offroad_racing_cap,
        status = "Limitado",
        price = "$299.99",
        brand = "Holo Crew",
        description = "Gorra exclusiva de la línea Racing, edición limitada a 500 unidades numeradas.",
        rating = 4.9f,
        reviewCount = 45,
        sizes = listOf("S/M", "L/XL"),
        colors = listOf(
            ColorOption("Negro", "#000000"),
            ColorOption("Rojo", "#F44336")
        ),
        features = listOf("Edición limitada 500 uds", "Numerada", "Bordado premium", "Cierre ajustable"),
        material = "Algodón canvas, Bordado premium",
        careInstructions = "Lavar a mano con agua fría. No retorcer.",
        sku = "HOLO-CP-006",
        publishedDate = "1 de diciembre de 2025"
    ),
    ProductDetail(
        id = 7,
        title = "Holo Crew T-Shirt",
        subtitle = "Camiseta básica logo HOLO",
        category = "Ropa",
        imageRes = R.drawable.tops,
        status = "Básico",
        price = "$49.99",
        brand = "Holo Crew",
        description = "Camiseta básica con el logo icónico de HoloCrew. Perfecta para el día a día.",
        rating = 4.2f,
        reviewCount = 530,
        sizes = listOf("XS", "S", "M", "L", "XL", "XXL"),
        colors = listOf(
            ColorOption("Blanco", "#FFFFFF"),
            ColorOption("Negro", "#000000"),
            ColorOption("Gris", "#9E9E9E")
        ),
        features = listOf("Logo estampado", "Algodón suave", "Corte regular", "Costuras reforzadas"),
        material = "Algodón 100%",
        careInstructions = "Lavar a máquina 30°C. Planchar del revés.",
        sku = "HOLO-TS-007",
        publishedDate = "15 de julio de 2025"
    ),
    ProductDetail(
        id = 8,
        title = "Premium Denim Jacket",
        subtitle = "Chaqueta denim premium",
        category = "Denim",
        imageRes = R.drawable.newdenims,
        status = "Premium",
        price = "$189.99",
        originalPrice = "$219.99",
        brand = "Holo Crew",
        description = "Chaqueta denim premium con lavado vintage y detalles exclusivos de la marca.",
        rating = 4.5f,
        reviewCount = 178,
        sizes = listOf("S", "M", "L", "XL"),
        colors = listOf(
            ColorOption("Azul claro", "#64B5F6"),
            ColorOption("Azul oscuro", "#1565C0")
        ),
        features = listOf("Lavado vintage", "Botones metálicos", "Forro interior", "Bolsillos laterales"),
        material = "Denim 100% algodón",
        careInstructions = "Lavar del revés con agua fría. No usar secadora.",
        sku = "HOLO-DJ-008",
        publishedDate = "3 de octubre de 2025"
    )
)