# TP1 - Yet Another Battle City
> **Universidad de Buenos Aires, Facultad de Ingeniería**  
> **[_Paradigmas de Programación (TB025) - Curso Essaya_](https://algoritmos3ce.github.io/)**

### Docentes
- Diego Nicolás Essaya
- Santiago Maraggi Casabal

##### Corrector
- Lihuén Carranza

### Integrantes del Grupo "Morfeo Zerbi - Valencia"
- 112563 - [Avril Victoria Morfeo Zerbi](https://github.com/AvrilMZ)
- 112776 - [Nicolás Valencia](https://github.com/nicoValencia25)

## Descripción

Este proyecto consiste en el desarrollo de una versión propia del clásico juego [Battle City](https://en.wikipedia.org/wiki/Battle_City), utilizando Java como lenguaje principal, Maven para la gestión del proyecto, JavaFX para la interfaz gráfica y un parser XML para la carga de niveles.
El juego permitirá a los jugadores controlar tanques, enfrentarse a enemigos controlados por la computadora y proteger su base. Se busca implementar una jugabilidad fluida, distintos niveles y una interfaz gráfica sencilla.

El desarrollo se realiza en el marco de la materia Paradigmas de Programación, integrando conocimientos teóricos y prácticos adquiridos durante el curso.

### Reglas de Juego

#### Objetivo
El jugador debe controlar un tanque para defender la base (representada por un águila) ubicada en el mapa, eliminando todos los tanques enemigos presentes en cada nivel. El juego finaliza con victoria al superar los 3 niveles, o con derrota si la base es destruida o todos los jugadores pierden sus vidas.

#### Jugabilidad
- **Modalidad:** individual o cooperativa (dos jugadores simultáneos).
- **Movimiento:** cuatro direcciones (arriba, abajo, izquierda, derecha).
  - Jugador 1: W (arriba), S (abajo), D (derecha) y A (izquierda).
  - Jugador 2: ↑ (arriba), ↓ (abajo), → (derecha) y ← (izquierda).
- **Disparo:** cada jugador puede tener un solo disparo activo a la vez.
  - Jugador 1: Barra espaciadora.
  - Juagor 2: Tecla enter.
- **Vidas:** cada jugador inicia cada nivel con 3 vidas (cada impacto recibido resta una vida).
- **Colisiones:** los disparos pueden destruirse entre sí al colisionar.
- Si un jugador pierde todas sus vidas, el otro puede continuar.
- Si un jugador impacta a otro con un disparo, el afectado queda inmovilizado temporalmente (puede seguir disparando).
- Si la base es destruida por cualquier jugador, la partida finaliza inmediatamente.

#### Entorno de juego
- El mapa se compone de bloques de distintos tipos:
  - Ladrillos: destructibles (requieren 3 impactos).
  - Acero: indestructibles.
  - Agua: impide el paso, pero no bloquea disparos.
  - Bosque: oculta visualmente los tanques, sin afectar movimiento ni disparos.
- La base puede estar protegida por ladrillos, según el diseño del nivel.

#### Enemigos
- Los enemigos están definidos para cada nivel y pueden generarse por aparición (spawning) desde la parte superior del mapa.
- Tipos de enemigos:
  - Básico: lento y débil.
  - Rápido: mayor velocidad de movimiento.
  - Potente: mayor velocidad de disparo.
  - Blindado: requiere 3 impactos para ser destruido.

#### Power-ups
- Al destruir un enemigo, existe un 20% de probabilidad de que aparezca un power-up en una posición aleatoria del mapa.
- Los power-ups se activan al ser recogidos por un jugador y su efecto es inmediato.
- Tipos:
  - Granada: elimina todos los enemigos en pantalla.
  - Casco: otorga invulnerabilidad temporal (10 segundos).
  - Estrella: mejora el disparo, permitiendo destruir cualquier tanque de un solo impacto.
  - Pala: refuerza la base con bloques de acero.
