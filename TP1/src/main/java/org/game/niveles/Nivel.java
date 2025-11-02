package org.game.niveles;

import org.game.modelo.TipoPowerUp;
import org.game.modelo.Posicion;
import org.game.modelo.entidades.*;
import org.game.modelo.powerups.*;
import org.game.modelo.terreno.Bloque;
import org.game.modelo.terreno.BloqueBase;
import org.game.visualizacion.GestorSonido;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Nivel {
    private static final double PROBABILIDAD_POWERUP = 0.2;

    private List<Bloque> bloques;
    private List<Tanque> tanques;
    private List<Bala> balas;
    private List<PowerUp> powerUpsActivos;
    private Posicion posInicialJugador1;
    private Posicion posInicialJugador2;
    private GestorSonido gestorSonido;

    /**
     * Constructor de la clase Nivel. Inicializa las listas de bloques, tanques, balas y power-ups activos.
     * @param gestorSonido gestor de sonidos para reproducir efectos durante el juego
     */
    public Nivel(GestorSonido gestorSonido) {
        this.bloques = new ArrayList<>();
        this.tanques = new ArrayList<>();
        this.balas = new ArrayList<>();
        this.powerUpsActivos = new ArrayList<>();
        this.gestorSonido = gestorSonido;
    }

    /**
     * Actualiza el estado de todos los elementos del nivel (tanques, balas, bloques).
     * Gestiona colisiones, elimina entidades destruidas y reproduce sonidos de eventos.
     * @param tiempoDelta tiempo transcurrido desde la última actualización (en segundos)
     */
    public void update(double tiempoDelta) {
        for (Tanque tanque: tanques) {
            if (!tanque.estaVivo()) {
                unEnemigoFueDestruido();
                gestorSonido.reproducir("MUERTE_TANQUE");
            }
            tanque.actualizarEstado();
            if (tanque.esEnemigo()) {
                ((TanqueEnemigo)tanque).actualizar(tiempoDelta, this);
            } else {
                tanque.actualizar(tiempoDelta);
            }
        }
        tanques.removeIf(tanque -> !tanque.estaVivo());
        bloques.removeIf(bloque -> !bloque.existe());

        for (int i = 0; i < balas.size(); i++) {
            Bala bala = balas.get(i);
            bala.movimiento(tiempoDelta);
            for (Bloque bloque: bloques) {
                bala.colisionarConBloque(bloque);
            }
            for (Tanque tanque: tanques) {
                if (bala.getPropietario() != null && bala.getPropietario() == tanque) {
                    continue;
                }
                bala.colisionarConTanque(tanque);
            }
            for (int j = 0; j < balas.size(); j++) {
                if (i != j) {
                    bala.colisionarConBala(balas.get(j));
                }
            }
        }
        balas.removeIf(bala -> !bala.estaActiva());
    }

    /**
     * Lógica a ejecutar cuando un enemigo es destruido. Puede generar un power-up aleatorio.
     */
    public void unEnemigoFueDestruido() {
        if (deberiaGenerarPowerUp(PROBABILIDAD_POWERUP)) {
            generarPowerUpAleatorio();
        }
    }

    /**
     * Determina si se debe generar un power-up según una probabilidad dada.
     * @param probabilidad valor entre 0 y 1 que indica la probabilidad de generar un power-up
     * @return true si se debe generar, false en caso contrario
     */
    private boolean deberiaGenerarPowerUp(double probabilidad) {
        return new Random().nextDouble() < probabilidad;
    }

    /**
     * Genera un power-up aleatorio en una posición vacía del nivel.
     */
    private void generarPowerUpAleatorio() {
        TipoPowerUp tipo = TipoPowerUp.values()[new Random().nextInt(TipoPowerUp.values().length)];
        Posicion pos = encontrarPosicionVaciaAleatoria();
        switch (tipo){
            case CASCO:
                powerUpsActivos.add(new Casco(pos));
                break;
            case GRANADA:
                powerUpsActivos.add(new Granada(pos));
                break;
            case ESTRELLA:
                powerUpsActivos.add(new Estrella(pos));
                break;
            case PALA:
                powerUpsActivos.add(new Pala(pos));
                break;
        }

    }

    /**
     * Busca una posición vacía aleatoria en el nivel donde colocar un power-up.
     * @return una posición válida sin colisiones
     */
    private Posicion encontrarPosicionVaciaAleatoria() {
        Random rand = new Random();
        int x, y;
        boolean posicionValida;
        do {
            x = rand.nextInt(800);
            y = rand.nextInt(600);
            Posicion pos = new Posicion(x, y);
            posicionValida = true;
            for (Bloque bloque: bloques) {
                if (new Rectangle((int)pos.x(), (int)pos.y(), 20, 20).intersects(bloque.obtenerArea())) {
                    posicionValida = false;
                    break;
                }
            }
            for (Tanque tanque: tanques) {
                if (new Rectangle((int)pos.x(), (int)pos.y(), 20, 20).intersects(tanque.obtenerArea())) {
                    posicionValida = false;
                    break;
                }
            }
        } while (!posicionValida);
        return new Posicion(x, y);
    }

    /**
     * Indica si el nivel está completo (sin tanques enemigos restantes).
     * @return true si no quedan enemigos, false en caso contrario
     */
    public boolean nivelCompleto() {
        return obtenerTanquesEnemigos().isEmpty();
    }

    /**
     * Agrega un bloque al nivel si no existe previamente.
     * @param bloque bloque a agregar
     */
    public void agregarBloque(Bloque bloque) {
        if (!this.bloques.contains(bloque)) {
            this.bloques.add(bloque);
        }
    }

    /**
     * Agrega un tanque (jugador o enemigo) al nivel.
     * @param tanque tanque a agregar
     */
    public void agregarTanque(Tanque tanque) {
        this.tanques.add(tanque);
    }

    /**
     * Agrega un tanque enemigo al nivel.
     * @param enemigo tanque enemigo a agregar
     */
    public void agregarEnemigo(TanqueEnemigo enemigo) {
        this.tanques.add(enemigo);
    }

    /**
     * Agrega una bala al nivel.
     * @param bala bala a agregar
     */
    public void agregarBala(Bala bala) {
        this.balas.add(bala);
    }

    /**
     * Elimina un power-up activo del nivel.
     * @param powerUp power-up a eliminar
     */
    public void eliminarPowerUp(PowerUp powerUp) {
        this.powerUpsActivos.remove(powerUp);
    }

    /**
     * Obtiene la lista de bloques del nivel.
     * @return lista de bloques
     */
    public List<Bloque> obtenerBloques() {
        return this.bloques;
    }

    /**
     * Obtiene la lista de tanques del nivel.
     * @return lista de tanques
     */
    public List<Tanque> obtenerTanques() {
        return this.tanques;
    }

    /**
     * Obtiene la lista de balas activas en el nivel.
     * @return lista de balas
     */
    public List<Bala> obtenerBalas() {
        return this.balas;
    }

    /**
     * Obtiene la lista de power-ups activos en el nivel.
     * @return lista de power-ups
     */
    public List<PowerUp> obtenerPowerUps() {
        return this.powerUpsActivos;
    }

    /**
     * Devuelve la base del nivel si existe.
     * @return bloque base o null si no existe
     */
    public BloqueBase obtenerBase() {
        for (Bloque bloque: bloques) {
            if (bloque.esBase()) {
                return (BloqueBase)bloque;
            }
        }
        return null;
    }

    /**
     * Obtiene la lista de tanques enemigos presentes en el nivel.
     * @return lista de tanques enemigos
     */
    public List<TanqueEnemigo> obtenerTanquesEnemigos() {
        List<TanqueEnemigo> tanquesEnemigos = new ArrayList<>();
        for (Tanque tanque: this.tanques) {
            if (tanque.esEnemigo()) {
                tanquesEnemigos.add((TanqueEnemigo)tanque);
            }
        }
        return tanquesEnemigos;
    }

    /**
     * Devuelve la posición inicial del Jugador 1.
     * @return posición inicial del Jugador 1
     */
    public Posicion obtenerPosInicialJugador1() {
        return posInicialJugador1;
    }

    /**
     * Devuelve la posición inicial del Jugador 2.
     * @return posición inicial del Jugador 2
     */
    public Posicion obtenerPosInicialJugador2() {
        return posInicialJugador2;
    }

    /**
     * Establece la posición inicial del Jugador 1.
     * @param p posición a establecer
     */
    public void setPosInicialJugador1(Posicion p) {
        this.posInicialJugador1 = p;
    }

    /**
     * Establece la posición inicial del Jugador 2.
     * @param p posición a establecer
     */
    public void setPosInicialJugador2(Posicion p) {
        this.posInicialJugador2 = p;
    }
}
