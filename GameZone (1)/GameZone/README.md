# GameZone - Sistema de Gestión de Videojuegos

## Integrantes
- [Nombre Integrante 1]
- [Nombre Integrante 2]

## Descripción
Sistema de gestión de videojuegos digitales y físicos para la tienda **GameZone**.
Implementado en Java con JavaFX, arquitectura en capas y persistencia en JSON.

## Tecnologías
- Java 21
- JavaFX 21.0.2
- org.json (persistencia JSON)
- Maven (gestión de dependencias)

## Arquitectura en Capas
```
gamezone/
├── interfaces/       ← Sellable, Tableable
├── model/            ← VideoGame (abstract), DigitalVideoGame, PhysicalVideoGame, Sale
├── repository/       ← VideoGameRepository, SaleRepository (CRUD + JSON)
├── service/          ← VideoGameService (lógica de negocio)
└── ui/               ← MainApp + Paneles JavaFX
```

## Cómo ejecutar
```bash
mvn javafx:run
```

## Datos
Los archivos JSON se generan automáticamente en la carpeta `data/`:
- `data/videogames.json` — catálogo de videojuegos
- `data/sales.json` — historial de ventas

## Funcionalidades
1. **Agregar videojuego** — CRUD completo (Crear, Listar, Editar, Eliminar)
2. **Listar todos los videojuegos** — tabla con todos los juegos
3. **Buscar por título** — búsqueda case-insensitive
4. **Buscar por plataforma** — filtrado por plataforma
5. **Realizar venta** — registra ventas, verifica stock y calcula total
6. **Mostrar ventas** — historial con estadísticas
