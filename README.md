
<h4 align="center">
:construction: Proyecto en construcción :construction:
</h4>

## Versión de Android

El proyecto está dirigido a dispositivos con Android API nivel 28 o superior.

## Plataformas Soportadas

El proyecto es compatible con dispositivos Android.

## Documentación

Para más detalles sobre Kotlin, puedes consultar la [documentación oficial de Kotlin](https://kotlinlang.org/docs/home.html).

## :hammer: Funcionalidades del proyecto

- `Funcionalidad 1`: Recarga de una tarjeta Trasncaribe efectivo o tarjeta
- `Funcionalidad 2`: Consulta de saldo de las tarjetas enroladas en Trasncaribe SA por medio de su operador SONDA
- `Funcionalidad 3`: Reporte de ventas del ususario autenticado enviado la informacion via POST
- `Funcionalidad 4`: Consulta de Saldo de tarjeta trascaribe ingresando el Número
- 
# Estructura del proyecto

En este proyecto Kotlin, se ha seguido las mejores prácticas de organización de archivos, distribuyendo los componentes de acuerdo a su funcionalidad principal:

- **data**: Contiene las clases relacionadas con el manejo de datos.
- **api**:
  Aquí se encuentran las clases encargadas de las llamadas a servicios externos o consultas a la base de datos, como AuthenticationService, DeviceService, TCService y WebPostService.
  Estas clases se han separado con el propósito de facilitar la incorporación de nuevos servicios en el futuro. Por ejemplo, TCService contiene los servicios que interactúan con Transcaribe S.A., puedes obtener más información sobre Transcaribe [aquí](https://transcaribe.gov.co/).
  Además, en DeviceService se encuentran los servicios para interactuar con los periféricos del dispositivo, tales como la impresora, la cámara, el NFC y la venta.
    - **model**: En esta carpeta se definen las clases de modelos de datos, tales como Compra, Factura, Report, TCard, Ticket, Transacciones, TransationResponse y User.
    - **UserRepository**: Carpeta destinada a las clases relacionadas con el repositorio de usuarios.

- **di**: Contiene las clases relacionadas con la inyección de dependencias.

- **domain**: Aquí se encuentran las clases relacionadas con la lógica del dominio de la aplicación.

- **libs**: Esta carpeta almacena archivos de librerías externas, como credibancoSDK-v1.0.0.1-RELEASE.aar.

- **ui**: Contiene las clases relacionadas con la interfaz de usuario.
    - **components**: Aquí se encuentran componentes reutilizables de la interfaz de usuario, como CustomBottomNavigationBar, errorMessage, HomeButtons, popUpComponent, SideBar y TopAppBar.
    - **config**: Carpeta destinada a configuraciones de la interfaz de usuario, como Settings.
    - **home**: Contiene la clase home, relacionada con la pantalla principal de la aplicación.
    - **login**: Aquí se encuentra la clase LoginScreen, relacionada con la pantalla de inicio de sesión.
    - **medios**: Contiene la clase MediosDePagos, relacionada con la gestión de medios de pagos.
    - **recargas_tc**: Carpeta relacionada con la funcionalidad de recargas de tarjetas, con la clase Recartas_TC.
    - **reports**: En esta carpeta se encuentra la clase Totals, relacionada con los informes de transacciones.
    - **theme**: Contiene clases relacionadas con la definición del tema de la aplicación, como Color, Theme y Type.
    - **user**: Aquí se encuentra la clase Profile, relacionada con el perfil de usuario.

Esta estructura organizativa facilita la navegación y mantenimiento del proyecto, permitiendo una clara separación de responsabilidades y una fácil identificación de los componentes.


kapi_n86
├── data
│   ├── api
│   │   ├── AuthenticationService.kt
│   │   ├── DeviceService.kt
│   │   ├── TCService.kt
│   │   └── WebPostService.kt
│   ├── model
│   │   ├── Compra.kt
│   │   ├── Factura.kt
│   │   ├── Report.kt
│   │   ├── TCard.kt
│   │   ├── Ticket.kt
│   │   ├── Transacciones.kt
│   │   └── User.kt
│   │   └── TransationResponse.kt
│   └── UserRepository
├── di
├── domain
├── libs
│   ├──credibancoSDK-v1.0.0.1-RELEASE.aar (SDK provisto por credibanco para interactuar con el perfiérico)
└── ui
├── components
│   ├── CustomBottomNavigationBar.kt
│   ├── errorMessage.kt
│   ├── HomeButtons.kt
│   ├── popUpComponent.kt
│   ├── SideBar.kt
│   └── TopAppBar.kt
├── config
│   └── Settings.kt
├── home
│   └── home.kt
├── login
│   └── LoginScreen.kt
├── medios
│   └── MediosDePagos.kt
├── recargas_tc
│   └── Recartas_TC.kt
├── reports
│   └── Totals.kt
├── theme
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── user
│    └── Profile.kt


## :book: Documentación del backend

Puedes encontrar más información en la [documentación del backend](https://webpos.kapi.com.co/doc).
