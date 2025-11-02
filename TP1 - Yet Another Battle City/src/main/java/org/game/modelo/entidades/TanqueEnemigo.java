package org.game.modelo.entidades;

import org.game.modelo.Direccion;
import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;
import org.game.modelo.terreno.Bloque;
import org.game.modelo.ConstantesTablero;
import org.game.niveles.Nivel;

import java.util.Random;

public abstract class TanqueEnemigo extends Tanque {
    private double tiempoDeConductaRestante;
    private double tiempoAtascado;
    private Posicion ultimaPosicion;
    private Direccion ultimaDireccion;
    private final double tiempoDeRecarga;
    private double cooldownDisparo;

    /**
     * Crea un tanque enemigo con los parámetros indicados.
     * @param posicionInicial Posición inicial del tanque
     * @param velocidad Velocidad de movimiento
     * @param salud Salud inicial
     * @param tiempoPorDisparo Tiempo mínimo entre disparos
     * @param gestorSonido Gestor de sonidos para efectos del tanque
     */
    public TanqueEnemigo(Posicion posicionInicial, double velocidad, int salud, int tiempoPorDisparo, GestorSonido gestorSonido) {
        super(posicionInicial, velocidad, salud, tiempoPorDisparo, gestorSonido);

        this.posicion = new Posicion(posicionInicial.x(), posicionInicial.y());
        tiempoDeRecarga = tiempoPorDisparo;
        this.cooldownDisparo = new Random().nextDouble() * tiempoDeRecarga;
        sortearNuevaConducta();
    }

    /**
     * Verifica si el área de destino está ocupada por otro tanque vivo.
     * @param destino Posición destino a verificar
     * @param nivel Nivel actual del juego
     * @return true si el área está ocupada, false si está libre
     */
    private boolean areaDestinoOcupada(Posicion destino, Nivel nivel) {
        java.awt.Rectangle areaDestino = new java.awt.Rectangle(
            (int)destino.x() + (int)((Bloque.ANCHO_BLOQUE - Bloque.ANCHO_TANQUE) / 2.0),
            (int)destino.y() + (int)((Bloque.ALTURA_BLOQUE - Bloque.ALTURA_TANQUE) / 2.0),
            Bloque.ANCHO_TANQUE,
            Bloque.ALTURA_TANQUE
        );
        for (Tanque t: nivel.obtenerTanques()) {
            if (t != this && t.estaVivo() && areaDestino.intersects(t.obtenerArea())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Actualiza el estado del tanque enemigo: movimiento, dirección, disparo y atascos.
     * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
     * @param nivel Nivel actual del juego
     */
    public void actualizar(double tiempoDelta, Nivel nivel) {
        tiempoDeConductaRestante -= tiempoDelta;
        if (tiempoDeConductaRestante <= 0) {
            sortearNuevaConducta();
        }
        Posicion destino = posicion.mover(ultimaDireccion, velocidad * tiempoDelta);
        double x = Math.max(0, Math.min(destino.x(), ConstantesTablero.ANCHO_SUBTABLERO - Bloque.ANCHO_BLOQUE));
        double y = Math.max(0, Math.min(destino.y(), ConstantesTablero.ALTURA_SUBTABLERO - Bloque.ALTURA_BLOQUE));
        destino = new Posicion(x, y);
        if (!areaDestinoOcupada(destino, nivel)) {
            movimiento(ultimaDireccion, tiempoDelta);
        }
        actualizarMovimiento(true);
        if (estaEnLaMismaPosicion()) {
            tiempoAtascado += tiempoDelta;
        } else {
            tiempoAtascado = 0;
            this.ultimaPosicion = obtenerPosicion();
        }
        if (tiempoAtascado > 2.0) {
            ultimaDireccion = sortearNuevaDireccion();
            tiempoAtascado = 0;
            Posicion nuevoDestino = posicion.mover(ultimaDireccion, velocidad * tiempoDelta);
            x = Math.max(0, Math.min(nuevoDestino.x(), ConstantesTablero.ANCHO_SUBTABLERO - Bloque.ANCHO_BLOQUE));
            y = Math.max(0, Math.min(nuevoDestino.y(), ConstantesTablero.ALTURA_SUBTABLERO - Bloque.ALTURA_BLOQUE));
            nuevoDestino = new Posicion(x, y);
            if (!areaDestinoOcupada(nuevoDestino, nivel)) {
                movimiento(ultimaDireccion, tiempoDelta);
            }
        }
        if (cooldownDisparo > 0) {
            cooldownDisparo -= tiempoDelta;
        }
        if (puedeDisparar()) {
            reiniciarCooldownDisparo();
            this.puedeDisparar = true;
        } else {
            this.puedeDisparar = false;
        }
    }

    /**
     * Implementación vacía para compatibilidad con la interfaz de Tanque.
     * @param tiempoDelta Tiempo transcurrido
     */
    @Override
    public void actualizar(double tiempoDelta) {}

    /**
     * Indica si el tanque puede disparar.
     * @return true si puede disparar, false en caso contrario
     */
    public boolean puedeDisparar() {
        return cooldownDisparo <= 0;
    }

    /**
     * Reinicia el cooldown de disparo al valor de recarga.
     */
    public void reiniciarCooldownDisparo() {
        this.cooldownDisparo = tiempoDeRecarga;
    }

    /**
     * Sortea una nueva conducta (dirección y duración) para el tanque enemigo.
     */
    private void sortearNuevaConducta() {
        this.ultimaDireccion = sortearNuevaDireccion();
        Random random = new Random();
        this.tiempoDeConductaRestante = 1.0 + random.nextDouble() * 1.5;
    }

    /**
     * Sortea una nueva dirección aleatoria para el tanque enemigo.
     * @return Nueva dirección sorteada
     */
    private Direccion sortearNuevaDireccion() {
        Random random = new Random();
        int dirIndex = random.nextInt(4);

		return switch (dirIndex) {
			case 1 -> Direccion.ABAJO;
			case 2 -> Direccion.IZQUIERDA;
			case 3 -> Direccion.DERECHA;
			default -> Direccion.ARRIBA;
		};
    }

    /**
     * Indica si el tanque está atascado (no se ha movido desde la última actualización).
     * @return true si está atascado, false en caso contrario
     */
    private boolean estaEnLaMismaPosicion() {
        return obtenerPosicion().equals(ultimaPosicion);
    }

    /**
     * Indica que este tanque es un enemigo.
     * @return true
     */
    @Override
    public boolean esEnemigo() {
        return true;
    }
}
