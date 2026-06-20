# ServiceHub

Sistema de gestión de contrataciones de oficios desarrollado en Java puro como Trabajo Práctico de **Programación II — UTN Mar del Plata**.

## Descripción del proyecto

ServiceHub conecta clientes que necesitan contratar un oficio (plomería, electricidad, etc.) con empleados que lo ofrecen, gestionando todo el ciclo: alta de usuarios, contratación, calificación del servicio y persistencia de datos en archivos JSON.

## Funcionalidades principales

- **Gestión de usuarios**: alta y administración de Clientes y Empleados, cada uno con su dirección.
- **Gestión de oficios**: alta y búsqueda de oficios disponibles, con disponibilidad de empleados por oficio.
- **Contrataciones**: un cliente contrata a un empleado para un oficio determinado.
- **Calificaciones**: los clientes pueden calificar el servicio recibido.
- **Validaciones y excepciones custom**: control de datos de entrada y manejo de errores específico del dominio (ej. IDs inválidos).
- **Persistencia en JSON**: los datos se guardan y leen desde archivos JSON, sin base de datos.
- **Interfaces por rol**: menús de consola separados para Administrador, Cliente y Empleado.

## Stack tecnológico

- Java (POO puro, sin frameworks)
- Persistencia en archivos JSON
- Aplicación de consola

## Arquitectura

El proyecto está organizado por responsabilidad:

\`\`\`
src/
├── Clases/        ← Entidades del dominio (Cliente, Empleado, Oficio, Contrataciones, Calificacion, etc.)
├── Enums/         ← Enumerados (ej. DisponibilidadEmpleado)
├── Exceptions/    ← Excepciones custom del dominio
├── Interfaces/    ← Interfaces de contrato (ej. Identificable)
├── ManejoJSON/    ← Lectura/escritura de persistencia en JSON
└── Main.java      ← Punto de entrada de la aplicación
\`\`\`

## Integrantes

- Alan Arruti
- Iara Blandi

**Carrera:** Tecnicatura Universitaria en Programación — UTN Mar del Plata
**Materia:** Programación II
**Docente:** Lic. Juan Castillo

> Proyecto cerrado — entregado como trabajo práctico final de la cursada.
