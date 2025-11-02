package org.game.visualizacion;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.game.modelo.Direccion;
import org.game.modelo.EstadoJuego;
import org.game.modelo.Juego;
import org.game.modelo.entidades.Tanque;
import org.game.modelo.terreno.TanqueDestruido;
import org.game.modelo.powerups.PowerUp;
import org.game.modelo.terreno.Bloque;
import org.game.modelo.entidades.Bala;
import org.game.modelo.ConstantesTablero;

import java.util.ArrayList;
import java.util.List;

public class VistaJuego extends Pane {
    private static final long DURACION_CARTEL = 2000;
    private static final long DURACION_TRANSICION = 2000;
    private static final double TAMANIO_MENSAJE_CENTRAL = 70;
    private static final String MENSAJE_VICTORIA = "VICTORY";
    private static final String MENSAJE_DERROTA = "DEFEAT";
    private static final String MENSAJE_NIVEL = "LEVEL ";

    private List<Tanque> tanquesVivosPrevio;
    private String mensajeFin;
    private final Juego modeloJuego;
    private final Canvas canvas;
    private final GestorImagen gestorImagen;
    private boolean nivelAvanzado;
    private boolean mostrandoCartelNivel;
    private boolean enTransicionFin;
    private long tiempoCartelNivel;
    private long tiempoInicioTransicionFin;
    private int proximoNivel;

    /**
     * Constructor de la clase VistaJuego.
     * Inicializa las variables necesarias y configura el canvas para el dibujo.
     * @param modeloJuego El modelo del juego que contiene la lógica y datos del mismo
     * @param gestorImagen El gestor de imágenes encargado de cargar y proporcionar las imágenes para el juego
     */
    public VistaJuego(Juego modeloJuego, GestorImagen gestorImagen) {
        this.tanquesVivosPrevio = new ArrayList<>();
        this.mensajeFin = null;
        this.modeloJuego = modeloJuego;
        this.canvas = new Canvas(ConstantesTablero.ANCHO_SUBTABLERO, ConstantesTablero.ALTURA_SUBTABLERO);
        this.gestorImagen = gestorImagen;
        this.nivelAvanzado = false;
        this.mostrandoCartelNivel = false;
        this.enTransicionFin = false;
        this.tiempoCartelNivel = 0;
        this.tiempoInicioTransicionFin = 0;
        this.proximoNivel = -1;
        getChildren().add(canvas);
    }

    /**
     * Centra el tablero de juego dentro de la ventana principal, ajustando la posición del canvas.
     * @param anchoVentana Ancho total de la ventana
     * @param altoVentana Alto total de la ventana
     */
    public void centrarTablero(int anchoVentana, int altoVentana) {
        double offsetX = (anchoVentana - ConstantesTablero.ANCHO_SUBTABLERO) / 2.0;
        double offsetY = (altoVentana - ConstantesTablero.ALTURA_SUBTABLERO) / 2.0;
        canvas.setLayoutX(offsetX);
        canvas.setLayoutY(offsetY);
    }

    /**
     * Indica si ha finalizado la transición de pantalla.
     * @return true si la transición ha finalizado, false en caso contrario
     */
    public boolean isFinTransicion() {
        return enTransicionFin && (System.currentTimeMillis() - tiempoInicioTransicionFin > DURACION_TRANSICION);
    }

    /**
     * Reinicia el estado de transición de fin de juego o nivel, permitiendo mostrar nuevos mensajes o transiciones.
     */
    public void resetTransicionFin() {
        enTransicionFin = false;
        mensajeFin = null;
    }

    /**
     * Renderiza todos los elementos visuales del juego: fondo, tanques, power-ups, bloques y balas.
     * También gestiona la visualización de mensajes centrales y transiciones.
     */
    public void render() {
        if (manejarTransicionNivel()) {
            return;
        }
        if (manejarFinDelJuego()) {
            return;
        }
        if (manejarVictoria()) {
            return;
        }
        if (modeloJuego.obtenerEstadoJuego() == EstadoJuego.CORRIENDO) {
            nivelAvanzado = false;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        dibujarFondo(gc);
        dibujarTanques(gc);
        dibujarTanqueRotado(modeloJuego.obtenerJugador1(), gc);
        dibujarTanqueRotado(modeloJuego.obtenerJugador2(), gc);
        dibujarPowerUps(gc);
        dibujarBloques(gc);
        actualizarTanquesDestruidos();
        dibujarBalas(gc);
        gc.restore();
    }

    /**
     * Gestiona la transición visual y lógica al pasar de un nivel a otro, mostrando mensajes y avanzando el juego.
     * @return true si se está manejando una transición de nivel, false en caso contrario
     */
    private boolean manejarTransicionNivel() {
        if (modeloJuego.obtenerEstadoJuego() == EstadoJuego.NIVEL_COMPLETO && !nivelAvanzado) {
            int siguienteNivel = modeloJuego.getNroNivelActual() + 1;
            if (!modeloJuego.existeNivel(siguienteNivel)) {
                if (!enTransicionFin) {
                    mensajeFin = MENSAJE_VICTORIA;
                    tiempoInicioTransicionFin = System.currentTimeMillis();
                    enTransicionFin = true;
                }
                mostrarMensajeCentral(mensajeFin);
                return true;
            }
            if (!mostrandoCartelNivel) {
                proximoNivel = siguienteNivel;
                mostrarMensajeCentral(MENSAJE_NIVEL + proximoNivel);
                tiempoCartelNivel = System.currentTimeMillis();
                mostrandoCartelNivel = true;
                return true;
            } else if (System.currentTimeMillis() - tiempoCartelNivel < DURACION_CARTEL) {
                mostrarMensajeCentral(MENSAJE_NIVEL + proximoNivel);
                return true;
            } else {
                modeloJuego.avanzarNivel();
                nivelAvanzado = true;
                mostrandoCartelNivel = false;
                return true;
            }
        }
        return false;
    }

    /**
     * Gestiona la visualización y lógica cuando el juego termina por derrota.
     * @return true si se está mostrando el mensaje de fin de juego, false en caso contrario
     */
    private boolean manejarFinDelJuego() {
        if (modeloJuego.esFinDelJuego()) {
            if (!enTransicionFin) {
                mensajeFin = MENSAJE_DERROTA;
                tiempoInicioTransicionFin = System.currentTimeMillis();
                enTransicionFin = true;
            }
            mostrarMensajeCentral(mensajeFin);
            return true;
        }
        return false;
    }

    /**
     * Gestiona la visualización y lógica cuando el juego termina por victoria.
     * @return true si se está mostrando el mensaje de victoria, false en caso contrario
     */
    private boolean manejarVictoria() {
        if (modeloJuego.esVictoria()) {
            if (!enTransicionFin) {
                mensajeFin = MENSAJE_VICTORIA;
                tiempoInicioTransicionFin = System.currentTimeMillis();
                enTransicionFin = true;
            }
            mostrarMensajeCentral(mensajeFin);
            return true;
        }
        return false;
    }

    /**
     * Dibuja el fondo del tablero de juego.
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarFondo(GraphicsContext gc) {
        setStyle("-fx-background-color: grey");
        gc.clearRect(0, 0, ConstantesTablero.ANCHO_SUBTABLERO, ConstantesTablero.ALTURA_SUBTABLERO);
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, ConstantesTablero.ANCHO_SUBTABLERO, ConstantesTablero.ALTURA_SUBTABLERO);
        gc.save();
    }

    /**
     * Actualiza la lista de tanques destruidos para mostrar la animación o sprite correspondiente en el tablero.
     */
    private void actualizarTanquesDestruidos() {
        List<Tanque> tanquesActuales = modeloJuego.obtenerNivelActual().obtenerTanques();
        for (Tanque tanquePrevio: tanquesVivosPrevio) {
            boolean sigueVivo = false;
            for (Tanque tanqueActual: tanquesActuales) {
                if (tanqueActual == tanquePrevio) {
                    sigueVivo = true;
                    break;
                }
            }
            if (!sigueVivo) {
                modeloJuego.obtenerNivelActual().obtenerBloques().add(
                    new TanqueDestruido(tanquePrevio.obtenerPosicion())
                );
            }
        }
        tanquesVivosPrevio = new ArrayList<>(tanquesActuales);
    }

    /**
     * Dibuja todos los tanques vivos en el tablero.
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarTanques(GraphicsContext gc) {
        for (Tanque tanque: modeloJuego.obtenerNivelActual().obtenerTanques()) {
            if (tanque.estaVivo()) {
                dibujarTanqueRotado(tanque, gc);
            }
        }
    }

    /**
     * Dibuja todos los bloques del tablero de juego.
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarBloques(GraphicsContext gc) {
        for (Bloque bloque: modeloJuego.obtenerNivelActual().obtenerBloques()) {
            gc.drawImage(
                    gestorImagen.obtenerImagen(bloque.obtenerClaveImagen()),
                    bloque.obtenerPosicion().x(),
                    bloque.obtenerPosicion().y(),
                    bloque.getAncho(),
                    bloque.getAlto()
            );
        }
    }

    /**
     * Dibuja todas las balas activas en el tablero de juego.
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarBalas(GraphicsContext gc) {
        for (Bala bala: modeloJuego.obtenerNivelActual().obtenerBalas()) {
            if (bala.estaActiva()) {
                gc.drawImage(
                        gestorImagen.obtenerImagen(bala.obtenerClaveImagen()),
                        bala.obtenerPosicion().x(),
                        bala.obtenerPosicion().y(),
                        Bala.ANCHO_BALA,
                        Bala.ALTURA_BALA
                );
            }
        }
    }

    /**
     * Dibuja todos los power-ups activos en el tablero de juego.
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarPowerUps(GraphicsContext gc) {
        for (PowerUp powerUp: modeloJuego.obtenerNivelActual().obtenerPowerUps()) {
            gc.drawImage(
                    gestorImagen.obtenerImagen(powerUp.obtenerClaveImagen()),
                    powerUp.obtenerPosicion().x(),
                    powerUp.obtenerPosicion().y(),
                    Bloque.ANCHO_BLOQUE,
                    Bloque.ALTURA_BLOQUE
            );
        }
    }

    /**
     * Dibuja un tanque rotado según su dirección y estado de invulnerabilidad.
     * @param tanque Tanque a dibujar
     * @param gc Contexto gráfico sobre el que dibujar
     */
    private void dibujarTanqueRotado(Tanque tanque, GraphicsContext gc) {
        if (tanque != null && tanque.estaVivo()) {
            var imagenTanque = gestorImagen.obtenerImagen(tanque.obtenerClaveImagen());
            var imagenAnillo = gestorImagen.obtenerImagen("ANILLO");
            double x = tanque.obtenerPosicion().x();
            double y = tanque.obtenerPosicion().y();
            double ancho = Bloque.ANCHO_TANQUE;
            double alto = Bloque.ALTURA_TANQUE;
            double centroX = x + Bloque.ANCHO_BLOQUE / 2.0;
            double centroY = y + Bloque.ALTURA_BLOQUE / 2.0;
            double angulo = obtenerAnguloPorDireccion(tanque.obtenerDireccion());
            gc.save();
            gc.translate(centroX, centroY);
            gc.rotate(angulo);
            gc.drawImage(imagenTanque, -ancho / 2.0, -alto / 2.0, ancho, alto);
            if (tanque.esInvulnerable()){
                gc.drawImage(imagenAnillo, -ancho / 2.0, -alto / 2.0, ancho, alto);
            }
            gc.restore();
        }
    }

    /**
     * Devuelve el ángulo de rotación en grados correspondiente a la dirección indicada.
     * Si la dirección es null, retorna 0.
     * @param direccion Dirección del tanque
     * @return Ángulo en grados para la rotación del sprite
     */
    private double obtenerAnguloPorDireccion(Direccion direccion) {
        if (direccion == null) {
            return 0;
        }
		return switch (direccion) {
			case DERECHA -> 90;
			case ABAJO -> 180;
			case IZQUIERDA -> 270;
			default -> 0;
		};
    }

    /**
     * Muestra un mensaje central en pantalla.
     * Limpia la lista de tanques vivos previos para evitar duplicados visuales.
     * @param mensaje Mensaje a mostrar en el centro de la pantalla
     */
    public void mostrarMensajeCentral(String mensaje) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.setGlobalAlpha(0.7);
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setGlobalAlpha(1.0);
        javafx.scene.text.Font fuenteArcade = javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/tipografias/Arcade.ttf"), TAMANIO_MENSAJE_CENTRAL);
        if (fuenteArcade == null) {
            fuenteArcade = javafx.scene.text.Font.font("Monospaced", TAMANIO_MENSAJE_CENTRAL);
        }
        gc.setFont(fuenteArcade);
        javafx.scene.text.Text text = new javafx.scene.text.Text(mensaje);
        text.setFont(fuenteArcade);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        double x = (canvas.getWidth() - textWidth) / 2;
        double y = (canvas.getHeight() + textHeight) / 2 - 20;
        gc.setFill(Color.GREENYELLOW);
        gc.fillText(mensaje, x, y);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        gc.strokeText(mensaje, x, y);
        gc.restore();
        tanquesVivosPrevio.clear();
    }
}
