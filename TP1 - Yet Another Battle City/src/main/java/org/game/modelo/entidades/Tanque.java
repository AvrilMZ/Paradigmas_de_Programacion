package org.game.modelo.entidades;

import org.game.modelo.ConstantesTablero;
import org.game.modelo.Direccion;
import org.game.modelo.Posicion;
import org.game.modelo.terreno.Bloque;
import org.game.visualizacion.GestorSonido;

import java.awt.*;

public abstract class Tanque {
    protected GestorSonido gestorSonido;
    protected Posicion posicion;
    protected Posicion posicionAnterior;
    protected Direccion direccion;
    protected int limiteX;
    protected int limiteY;
    protected int salud;
    protected double velocidad;
    protected double tiempoPorDisparo;
    protected boolean seMueve;
    protected boolean congelado;
    private boolean instaKill;
    private boolean balaActiva;
    private boolean esInvulnerable;
    public boolean puedeDisparar;
    private long tiempoDescongelacion;
    private long tiempoInvulnerabilidad;

    /**
     * Crea un tanque con la posición, velocidad, salud y gestor de sonido indicados.
     * @param posicionInicial Posición inicial del tanque
     * @param velocidad Velocidad de movimiento del tanque
     * @param saludInicial Salud inicial del tanque
     * @param tiempoPorDisparo Tiempo mínimo entre disparos
     * @param gestorSonido Gestor de sonidos para efectos del tanque
     */
    public Tanque(Posicion posicionInicial, double velocidad, int saludInicial, int tiempoPorDisparo, GestorSonido gestorSonido) {
        this.gestorSonido = gestorSonido;
        this.posicion = posicionInicial;
        this.posicionAnterior = posicionInicial;
        this.direccion = Direccion.ARRIBA;
        this.limiteX = ConstantesTablero.ANCHO_SUBTABLERO;
        this.limiteY = ConstantesTablero.ALTURA_SUBTABLERO;
        this.salud = saludInicial;
        this.velocidad = velocidad;
        this.tiempoPorDisparo = tiempoPorDisparo;
        this.seMueve = false;
        this.congelado = false;
        this.instaKill = false;
        this.balaActiva = false;
        this.esInvulnerable = false;
        this.puedeDisparar = true;
        this.tiempoDescongelacion = 0;
        this.tiempoInvulnerabilidad = 0;
    }

    /**
     * Actualiza el estado del tanque según la lógica específica de la subclase.
     * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
     */
    public abstract void actualizar(double tiempoDelta);

    /**
     * Mueve el tanque en la dirección indicada durante el tiempo especificado, si puede moverse.
     * Actualiza la posición y la dirección del tanque.
     * @param direccion Dirección de movimiento
     * @param tiempoDelta Tiempo transcurrido
     */
    public void movimiento(Direccion direccion, double tiempoDelta) {
        if (this.puedeMoverse()) {
            this.direccion = direccion;
            Posicion nuevaPosicion = posicion.mover(direccion, velocidad * tiempoDelta);

            double x = Math.max(0, Math.min(nuevaPosicion.x(), limiteX - Bloque.ANCHO_BLOQUE));
            double y = Math.max(0, Math.min(nuevaPosicion.y(), limiteY - Bloque.ALTURA_BLOQUE));
            nuevaPosicion = new Posicion(x, y);

            this.posicionAnterior = this.posicion;
            this.posicion = nuevaPosicion;
            this.seMueve = true;
        }
    }

    /**
     * Actualiza el estado de movimiento del tanque.
     * @param estaRecibiendoMovimiento true si se está intentando mover, false si no
     */
    public void actualizarMovimiento(boolean estaRecibiendoMovimiento) {
        if (this.puedeMoverse()) {
            this.seMueve = estaRecibiendoMovimiento;
        }
    }

    /**
     * Dispara una bala en la dirección indicada (o en la dirección actual si es null).
     * Calcula la posición inicial de la bala según la posición y dirección del tanque.
     * @param direccion Dirección del disparo
     * @return Nueva instancia de Bala
     */
    public Bala disparar(Direccion direccion) {
        Direccion dir;
        if (direccion != null) {
            dir = direccion;
        } else {
            dir = this.direccion;
        }
        int balaSize = Bala.ANCHO_BALA;
        double x = posicion.x() + (Bloque.ANCHO_BLOQUE - balaSize) / 2.0;
        double y = posicion.y() + (Bloque.ALTURA_BLOQUE - balaSize) / 2.0;
        int offset = 6;
        switch (dir) {
            case ARRIBA:
                y = posicion.y() - offset;
                break;
            case ABAJO:
                y = posicion.y() + Bloque.ALTURA_BLOQUE - balaSize + offset;
                break;
            case IZQUIERDA:
                x = posicion.x() - offset;
                break;
            case DERECHA:
                x = posicion.x() + Bloque.ANCHO_BLOQUE - balaSize + offset;
                break;
        }
        return new Bala(new Posicion(x, y), dir, 400, this, gestorSonido);
    }

    /**
     * Devuelve el área rectangular ocupada por el tanque para detección de colisiones.
     * @return Rectángulo con la posición y tamaño del tanque
     */
    public Rectangle obtenerArea() {
        if (posicion == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        int offsetX = (int)((Bloque.ANCHO_BLOQUE - Bloque.ANCHO_TANQUE) / 2.0);
        int offsetY = (int)((Bloque.ALTURA_BLOQUE - Bloque.ALTURA_TANQUE) / 2.0);
        return new Rectangle(
                (int) posicion.x() + offsetX,
                (int) posicion.y() + offsetY,
                Bloque.ANCHO_TANQUE,
                Bloque.ALTURA_TANQUE
        );
    }

    /**
     * Gestiona la colisión del tanque con un bloque. Si colisiona y el bloque impide el paso,
     * restaura la posición anterior y detiene el movimiento.
     * @param bloque Bloque con el que colisiona
     */
    public void colisionarConBloque(Bloque bloque) {
        if (bloque.impidePaso() && obtenerArea().intersects(bloque.obtenerArea())) {
            this.posicion = this.posicionAnterior;
            seMueve = false;
        }
    }

    /**
     * Gestiona la colisión del tanque con otro tanque. Si colisiona, restaura la posición anterior y detiene el movimiento.
     * @param tanque Otro tanque con el que colisiona
     */
    public void colisionarConTanque(Tanque tanque) {
        if (this != tanque && obtenerArea().intersects(tanque.obtenerArea())) {
            this.posicion = this.posicionAnterior;
            this.seMueve = false;
        }
    }

    /**
     * Congela el tanque durante 3 segundos, impidiendo que se mueva.
     */
    public void congelar() {
        this.congelado = true;
        this.tiempoDescongelacion = System.currentTimeMillis() + 3000; // 3 segundos
    }

    /**
     * Actualiza el estado del tanque.
     */
    public void actualizarEstado() {
        if (congelado && System.currentTimeMillis() >= tiempoDescongelacion) {
            congelado = false;
        }
        if (esInvulnerable && System.currentTimeMillis() >= tiempoInvulnerabilidad) {
            esInvulnerable = false;
        }
    }

    /**
     * Hace al tanque invulnerable durante 10 segundos.
     */
    public void hacerInvulnerable() {
        this.esInvulnerable = true;
        this.tiempoInvulnerabilidad = System.currentTimeMillis() + 10000; // 10 segundos
    }

    /**
     * Indica si el tanque es invulnerable actualmente.
     * @return true si es invulnerable, false en caso contrario
     */
    public boolean esInvulnerable() {
        return this.esInvulnerable;
    }

    /**
     * Activa el modo insta-kill para el tanque (mata enemigos al disparar).
     */
    public void activarInstaKill() {
        this.instaKill = true;
    }

    /**
     * Indica si el tanque tiene activo el modo insta-kill.
     * @return true si tiene insta-kill, false en caso contrario
     */
    public boolean tieneInstaKill() {
        return this.instaKill;
    }

    /**
     * Marca que el tanque tiene una bala activa en pantalla.
     */
    public void activarBala() {
        this.balaActiva = true;
    }

    /**
     * Marca que el tanque ya no tiene una bala activa en pantalla.
     */
    public void desactivarBala() {
        this.balaActiva = false;
    }

    /**
     * Indica si el tanque tiene una bala activa en pantalla.
     * @return true si tiene una bala activa, false en caso contrario
     */
    public boolean balaActiva() {
        return this.balaActiva;
    }

    /**
     * Indica si el tanque puede moverse.
     * @return true si puede moverse, false si está congelado
     */
    public boolean puedeMoverse() {
        return !congelado;
    }

    /**
     * Devuelve la posición actual del tanque.
     * @return Posición del tanque
     */
    public Posicion obtenerPosicion() {
        return posicion;
    }

    /**
     * Devuelve la dirección actual del tanque.
     * @return Dirección del tanque
     */
    public Direccion obtenerDireccion() {
        return direccion;
    }

    /**
     * Indica si el tanque está vivo.
     * @return true si está vivo, false si está destruido
     */
    public boolean estaVivo() {
        return salud > 0;
    }

    /**
     * Mata al tanque.
     */
    public void matarEnemigo(){
        this.salud = 0;
    }

    /**
     * Indica si el tanque es un enemigo.
     * @return true si es enemigo, false si es jugador
     */
    public boolean esEnemigo() {
        return false;
    }

    /**
     * Indica si el tanque es el primer jugador.
     * @return true si es el primer jugador, false en caso contrario
     */
    public boolean esPrimerJugador() {
        return false;
    }

    /**
     * Indica si el tanque es el segundo jugador.
     * @return true si es el segundo jugador, false en caso contrario
     */
    public boolean esSegundoJugador() {
        return false;
    }

    /**
     * Indica si el tanque es blindado.
     * @return true si es blindado, false en caso contrario
     */
    public boolean esBlindado() {
        return false;
    }

    /**
     * Devuelve la clave de imagen asociada al tanque para su visualización.
     * @return Clave de imagen
     */
    public abstract String obtenerClaveImagen();
}
