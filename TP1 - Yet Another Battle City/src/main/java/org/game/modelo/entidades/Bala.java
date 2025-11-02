package org.game.modelo.entidades;

import org.game.modelo.Direccion;
import org.game.modelo.Posicion;
import org.game.modelo.terreno.Bloque;
import org.game.visualizacion.GestorSonido;

import java.awt.*;

public class Bala {
    public static int ANCHO_BALA = 10;
    public static int ALTURA_BALA = 10;

    protected Posicion posicion;
    protected double velocidad;
    protected Direccion direccion;
    private boolean activa;
    private Tanque propietario;
    private GestorSonido gestorSonido;

    /**
     * Crea una nueva bala con la posición, dirección, velocidad, propietario y gestor de sonido indicados.
     * @param posicion Posición inicial de la bala
     * @param direccion Dirección de movimiento
     * @param velocidad Velocidad de la bala
     * @param propietario Tanque que disparó la bala
     * @param gestorSonido Gestor de sonidos para efectos de la bala
     */
    public Bala(Posicion posicion, Direccion direccion, double velocidad, Tanque propietario, GestorSonido gestorSonido) {
        this.posicion = posicion;
        this.direccion = direccion;
        this.velocidad = velocidad;
        this.activa = true;
        this.propietario = propietario;
        this.gestorSonido = gestorSonido;
    }

    /**
     * Mueve la bala según su dirección y velocidad.
     * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
     */
    public void movimiento(double tiempoDelta) {
        if (direccion != null) {
            posicion = posicion.mover(direccion, velocidad * tiempoDelta);
        }
    }

    /**
     * Devuelve la posición actual de la bala en el tablero.
     * @return Posición de la bala
     */
    public Posicion obtenerPosicion() {
        return posicion;
    }

    /**
     * Devuelve el área rectangular ocupada por la bala para detección de colisiones.
     * @return Rectángulo con la posición y tamaño de la bala
     */
    public Rectangle obtenerArea() {
        return new Rectangle((int)posicion.x(), (int)posicion.y(), ANCHO_BALA, ALTURA_BALA);
    }

    /**
     * Gestiona la colisión de la bala con un bloque, actualizando su estado y reproduciendo sonido si corresponde.
     * @param bloque Bloque con el que colisiona
     */
    public void colisionarConBloque(Bloque bloque) {
        if (activa && (posicion.x() < 0 || posicion.x() > 800 || posicion.y() < 0 || posicion.y() > 600)) {
            impacto();
            return;
        }
        if (activa && obtenerArea().intersects(bloque.obtenerArea())) {
            if (bloque.impactoBala()) {
                impacto();
            }
            bloque.reproducirSonidoImpacto(gestorSonido);
        }
    }

    /**
     * Gestiona la colisión de la bala con un tanque, actualizando su estado y el del tanque.
     * @param tanque Tanque con el que colisiona
     */
    public void colisionarConTanque(Tanque tanque) {
        if (activa && obtenerArea().intersects(tanque.obtenerArea())) {
            impacto();
            if (tanque.esEnemigo() && propietario.esEnemigo()) {
                return;
            }
            if ((tanque.esPrimerJugador() && propietario.esSegundoJugador()) ||
                (tanque.esSegundoJugador() && propietario.esPrimerJugador())) {
                tanque.congelar();
            } else {
                if (!tanque.esInvulnerable()){
                    tanque.salud--;
                }
                if (tanque.esInvulnerable()){
                    gestorSonido.reproducir("IMPACTO_TANQUE_BLINDADO");
                }
                if (tanque.esBlindado() && !propietario.tieneInstaKill()) {
                    gestorSonido.reproducir("IMPACTO_TANQUE_BLINDADO");
                }
                if (propietario.tieneInstaKill()) {
                    tanque.salud = 0;
                }
            }
        }
    }

    /**
     * Gestiona la colisión de la bala con otra bala, desactivando ambas si colisionan.
     * @param otraBala Otra bala con la que colisiona
     */
    public void colisionarConBala(Bala otraBala) {
        if (activa && otraBala.estaActiva() && obtenerArea().intersects(otraBala.obtenerArea())) {
            this.impacto();
            otraBala.impacto();
        }
    }

    /**
     * Realiza la acción de impacto de la bala, desactivándola y notificando a su propietario.
     */
    public void impacto() {
        this.activa = false;
        this.propietario.desactivarBala();
    }

    /**
     * Indica si la bala está activa.
     * @return true si está activa, false si debe eliminarse
     */
    public boolean estaActiva() {
        return activa;
    }

    /**
     * Devuelve la clave de imagen asociada a la bala para su visualización.
     * @return Clave de imagen
     */
    public String obtenerClaveImagen() {
        return "DISPARO";
    }

    /**
     * Devuelve el propietario que disparó la bala.
     * @return Tanque propietario
     */
    public Tanque getPropietario() {
        return propietario;
    }
}
