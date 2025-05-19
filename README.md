# Proyecto Cine - Rama MrkFinal

Este proyecto es mi proyecto final(mi TFG) del DAM,es una aplicación para la gestión de reservas en un cine o mas bien un cine en si. Fue desarrollado utilizando JavaFX para la interfaz gráfica. A continuación, se detallan las instrucciones para poner en marcha el programa y las funcionalidades de cada una de sus pestañas.

---

###La base de datos
Esta alojada en un servidor en la nube, en concreto de MondoDB Atlas asi que en teoría debería estar los datos subidos

## Iniciar el Programa

1. **Requisitos Previos**:
   - Asegúrate de tener Java 11 o superior instalado.
   - Asegúrate de que las dependencias para JavaFX estén configuradas si estás ejecutando el programa desde la terminal.

2. **Ejecución**:
   - El programa se inicia desde la clase `main` en el paquete `Main`. Este archivo se encuentra en:
     ```
     src/main/java/Main/main.java
     ```
   - Puedes iniciar el programa ejecutando el método `main` en esta clase:
     ```java
     public static void main(String[] args) {
         launch(args);
     }
     ```

   - La interfaz inicial será la pantalla de **Login**, donde los usuarios pueden iniciar sesión o crear una nueva cuenta.

3. **Configuración del Entorno**:
   - Si usas un IDE como IntelliJ IDEA o Eclipse, asegúrate de establecer el archivo `main` como punto de entrada.
   - Si ejecutas desde la terminal, navega al directorio del proyecto y usa:
     ```
     java -classpath out/production/ProyectoCine Main.main
     ```

---

##IMPORTANTE

	-EL usuario mas importante es admin@gmail.com con contraseña 12345678, ya que con el se debloquean las opciones de
	Crear, eliminar y modificar las peliculas disponibles

## Funcionalidades Básicas

### 1. **Pantalla de Login**
   - **Archivo FXML**: `login.fxml`.
   - **Controlador**: `LoginController`.
   - **Funciones**:
     - Iniciar sesión con un correo y contraseña válidos.
     - Opción para recordar al usuario y guardar su correo en un archivo de configuración.
     - Crear una nueva cuenta si no existe.

### 2. **Pantalla Principal**
   - **Archivo FXML**: `principal.fxml`.
   - **Descripción**:
     - Una vez iniciado sesión, el usuario es redirigido a la pantalla principal.
     - Contiene opciones para navegar entre las diferentes funcionalidades de la aplicación.

### 3. **Gestión de Reservas**
   - **Funcionalidad**:
     - Permite al usuario seleccionar asientos en una sala de cine.
     - Los asientos están representados visualmente en una cuadrícula:
       - **Rojo**: Asiento disponible.
       - **Verde**: Asiento seleccionado.
     - Calcula el costo total de las entradas seleccionadas.

### 4. **Gestión de Usuarios**
   - **Funcionalidad**:
     - Crear, modificar o eliminar cuentas de usuario.
     - Actualizar el saldo de los usuarios registrados.

### 5. **Pantalla de Facturación**
   - **Funcionalidad**:
     - Generar un archivo PDF con el detalle de las reservas realizadas, incluyendo los asientos seleccionados y el costo total.

---

## Dependencias y Librerías Externas

- **MongoDB Driver**: Utilizado para la conexión con la base de datos.
- **JavaFX**: Para el desarrollo de la interfaz gráfica.
- **iText**: Para la generación de facturas en formato PDF.

---

## Notas Adicionales

Si experimentas algún problema al ejecutar el programa o encuentras errores en las funcionalidades, por favor revisa la configuración y las dependencias instaladas. Si tienes dudas, no dudes en abrir un issue en este repositorio.
