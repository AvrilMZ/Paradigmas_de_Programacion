package org.game.modelo;

import org.game.modelo.powerups.PowerUp;
import org.game.modelo.entidades.Bala;
import org.game.modelo.entidades.Tanque;
import org.game.modelo.entidades.TanqueEnemigo;
import org.game.modelo.entidades.TanqueJugador;
import org.game.modelo.terreno.Bloque;
import org.game.modelo.terreno.BloqueBase;
import org.game.niveles.Nivel;
import org.game.niveles.NivelLoader;
import org.game.visualizacion.GestorSonido;

import java.io.File;
import java.util.List;

public class Juego {
    private static final int NIVEL_INICIAL = 1;
    private static final double VELOCIDAD_TANQUE_JUGADOR = 150.0;
    private static final int SALUD_TANQUE_JUGADOR = 3;
    private static final int TIEMPO_POR_DISPARO_JUGADOR = 2;

    private NivelLoader nivelLoader;
    private GestorSonido gestorSonido;
    private Nivel nivelActual;
    private int nroNivelActual;
    private TanqueJugador jugador1;
    private TanqueJugador jugador2;
    private EstadoJuego estadoJuego;

    /**
     * Crea una nueva instancia de Juego, inicializando el cargador de niveles y el gestor de sonido.
     * Llama a la inicialización del juego con la cantidad de jugadores indicada.
     * @param cantJugadores cantidad de jugadores (1 o 2)
     * @param gestorSonido gestor de sonidos para reproducir efectos durante el juego
     */
    public Juego(int cantJugadores, GestorSonido gestorSonido) {
        this.nivelLoader = new NivelLoader(gestorSonido);
        this.gestorSonido = gestorSonido;
        this.nroNivelActual = NIVEL_INICIAL;
        inicializarJuego(cantJugadores);
    }

    /**
     * Inicializa el juego cargando el primer nivel y agregando los jugadores al nivel actual.
     * Si hay dos jugadores, agrega ambos tanques en sus posiciones iniciales.
     * @param cantJugadores cantidad de jugadores (1 o 2)
     */
    private void inicializarJuego(int cantJugadores) {
        cargarNivel(nroNivelActual);
        Posicion p1;
        if (nivelActual.obtenerPosInicialJugador1() != null) {
            p1 = nivelActual.obtenerPosInicialJugador1();
        } else {
            p1 = new Posicion(250, 500);
        }
        this.jugador1 = new TanqueJugador(1, p1, gestorSonido, VELOCIDAD_TANQUE_JUGADOR, SALUD_TANQUE_JUGADOR, TIEMPO_POR_DISPARO_JUGADOR, "TANQUE_PRIMER_JUGADOR_0", "TANQUE_PRIMER_JUGADOR_1");
        this.nivelActual.agregarTanque(jugador1);
        if (cantJugadores == 2) {
            Posicion p2;
            if (nivelActual.obtenerPosInicialJugador2() != null) {
                p2 = nivelActual.obtenerPosInicialJugador2();
            } else {
                p2 = new Posicion(550, 500);
            }
            this.jugador2 = new TanqueJugador(2, p2, gestorSonido, VELOCIDAD_TANQUE_JUGADOR, SALUD_TANQUE_JUGADOR, TIEMPO_POR_DISPARO_JUGADOR, "TANQUE_SEGUNDO_JUGADOR_0", "TANQUE_SEGUNDO_JUGADOR_1");
            this.nivelActual.agregarTanque(jugador2);
        }
        this.estadoJuego = EstadoJuego.CORRIENDO;
    }

    /**
     * Carga el nivel especificado por número. Si ocurre un error o no existe el nivel,
     * el estado del juego se establece en VICTORIA.
     * @param nroNivel número de nivel a cargar
     */
    public void cargarNivel(int nroNivel) {
        this.nroNivelActual = nroNivel;
        try {
            this.nivelActual = nivelLoader.load(nroNivel);
        } catch (RuntimeException e) {
            this.nivelActual = null;
            this.estadoJuego = EstadoJuego.VICTORIA;
            return;
        }
        if (this.nivelActual == null) {
            this.estadoJuego = EstadoJuego.VICTORIA;
        }
    }

    /**
     * Actualiza el estado del juego si está corriendo: actualiza el nivel, gestiona disparos enemigos,
     * verifica colisiones y el estado general del juego.
     * @param tiempoDelta tiempo transcurrido desde la última actualización (en segundos)
     */
    public void update(double tiempoDelta) {
        if (estadoJuego != EstadoJuego.CORRIENDO) {
            return;
        }
        nivelActual.update(tiempoDelta);
        disparoEnemigos();
        verficarColisiones();
        verficarEstadoJuego();
    }

    /**
     * Verifica si la base fue destruida o si ambos jugadores murieron, cambiando el estado del juego a FIN.
     * Si no quedan enemigos, cambia el estado a NIVEL_COMPLETO.
     */
    private void verficarEstadoJuego() {
        BloqueBase base = nivelActual.obtenerBase();
        if ((base != null && !base.existe()) || (!jugador1.estaVivo() && (jugador2 == null || !jugador2.estaVivo()))) {
            estadoJuego = EstadoJuego.FIN;
        }
        if (nivelActual.nivelCompleto()) {
            estadoJuego = EstadoJuego.NIVEL_COMPLETO;
        }
    }

    /**
     * Verifica y gestiona las colisiones entre tanques, bloques y power-ups.
     * Aplica los efectos correspondientes cuando un jugador recoge un power-up.
     */
    private void verficarColisiones() {
        List<Bloque> bloques = new java.util.ArrayList<>(nivelActual.obtenerBloques());
        List<Tanque> tanques = new java.util.ArrayList<>(nivelActual.obtenerTanques());
        List<PowerUp> powerUps = new java.util.ArrayList<>(nivelActual.obtenerPowerUps());
        for (Bloque bloque: bloques) {
            if (!bloque.existe()) {
                continue;
            }
            if (jugador1.obtenerArea().intersects(bloque.obtenerArea())) {
                jugador1.colisionarConBloque(bloque);
            }
            if (jugador2 != null && jugador2.obtenerArea().intersects(bloque.obtenerArea())) {
                jugador2.colisionarConBloque(bloque);
            }
            for (Tanque tanque: tanques) {
                tanque.obtenerArea().intersects(bloque.obtenerArea());
                tanque.colisionarConBloque(bloque);
            }
        }
        for (Tanque tanque: tanques) {
            if (jugador1.obtenerArea().intersects(tanque.obtenerArea())) {
                jugador1.colisionarConTanque(tanque);
            }
            if (jugador2 != null && jugador2.obtenerArea().intersects(tanque.obtenerArea())) {
                jugador2.colisionarConTanque(tanque);
            }
        }
        for (PowerUp powerUp: powerUps) {
            if (jugador1.obtenerArea().intersects(powerUp.obtenerArea())){
                efectoPowerUp(jugador1, powerUp);
            }
            if (jugador2 != null && jugador2.obtenerArea().intersects(powerUp.obtenerArea())){
                efectoPowerUp(jugador2, powerUp);
            }
        }
    }

    /**
     * Aplica el efecto del power-up recogido por un tanque y lo elimina del nivel.
     * @param tanque tanque que recoge el power-up
     * @param powerUp power-up recogido
     */
    private void efectoPowerUp(Tanque tanque, PowerUp powerUp) {
        switch (powerUp.obtenerTipo()) {
            case GRANADA:
                for (List<TanqueEnemigo> enemigos: List.of(nivelActual.obtenerTanquesEnemigos())) {
                    for (TanqueEnemigo enemigo: enemigos) {
                        enemigo.matarEnemigo();
                        }
                    }
                nivelActual.eliminarPowerUp(powerUp);
                break;
            case CASCO:
                tanque.hacerInvulnerable();
                nivelActual.eliminarPowerUp(powerUp);
                break;
            case ESTRELLA:
                tanque.activarInstaKill();
                nivelActual.eliminarPowerUp(powerUp);
                break;
            case PALA:
                nivelActual.eliminarPowerUp(powerUp);
                break;
        }
    }

    /**
     * Devuelve el jugador 1.
     * @return instancia de TanquePrimerJugador
     */
    public TanqueJugador obtenerJugador1() {
        return jugador1;
    }

    /**
     * Devuelve el jugador 2.
     * @return instancia de TanqueSegundoJugador
     */
    public TanqueJugador obtenerJugador2() {
        return jugador2;
    }

    /**
     * Devuelve el nivel actual en juego.
     * @return instancia de Nivel actual
     */
    public Nivel obtenerNivelActual() {
        return nivelActual;
    }

    /**
     * Mueve el tanque indicado en la dirección y tiempo especificados.
     * @param tanque tanque a mover
     * @param direccion dirección de movimiento
     * @param tiempoDelta tiempo transcurrido
     */
    public void moverTanque(Tanque tanque, Direccion direccion, double tiempoDelta) {
        if (tanque != null) {
            tanque.movimiento(direccion, tiempoDelta);
        }
    }

    /**
     * Realiza el disparo del jugador indicado en la dirección dada, si puede disparar.
     * @param nroJugador número de jugador (1 o 2)
     * @param direccion dirección del disparo
     */
    public void disparoJugador(int nroJugador, Direccion direccion) {
        Tanque jugador;
        if (nroJugador == 1) {
            jugador = jugador1;
        } else {
            jugador = jugador2;
        }
        if (jugador != null && jugador.estaVivo() && !jugador.balaActiva()) {
            Bala bala = jugador.disparar(direccion);
            nivelActual.agregarBala(bala);
            gestorSonido.reproducir("DISPARO");
            jugador.activarBala();
        }
    }

    /**
     * Hace que todos los enemigos que pueden disparar lo hagan en su dirección actual.
     */
    public void disparoEnemigos() {
        List<TanqueEnemigo> enemigos = nivelActual.obtenerTanquesEnemigos();
        for (TanqueEnemigo enemigo: enemigos) {
            if (enemigo.estaVivo() && enemigo.puedeDisparar) {
                Direccion direccion = enemigo.obtenerDireccion();
                Bala bala = enemigo.disparar(direccion);
                nivelActual.agregarBala(bala);
                gestorSonido.reproducir("DISPARO");
                enemigo.reiniciarCooldownDisparo();
            }
        }
    }

    /**
     * Avanza al siguiente nivel si existe, reiniciando los jugadores y el estado del juego.
     * Si no existe el siguiente nivel, establece el estado en VICTORIA.
     */
    public void avanzarNivel() {
        int siguienteNivel = this.nroNivelActual + 1;
        String pathNivel = "src/main/resources/niveles/GeneratedLevels/Level" + siguienteNivel + ".xml";
        File archivoNivel = new File(pathNivel);
        if (!archivoNivel.exists()) {
            this.estadoJuego = EstadoJuego.VICTORIA;
            return;
        }
        cargarNivel(siguienteNivel);
        if (this.nivelActual == null) {
            this.estadoJuego = EstadoJuego.VICTORIA;
            return;
        }
        Posicion p1;
        if (nivelActual.obtenerPosInicialJugador1() != null) {
            p1 = nivelActual.obtenerPosInicialJugador1();
        } else {
            p1 = new Posicion(250, 500);
        }
        this.jugador1 = new TanqueJugador(1, p1, gestorSonido, VELOCIDAD_TANQUE_JUGADOR, SALUD_TANQUE_JUGADOR, TIEMPO_POR_DISPARO_JUGADOR, "TANQUE_PRIMER_JUGADOR_0", "TANQUE_PRIMER_JUGADOR_1");
        this.nivelActual.agregarTanque(jugador1);
        if (this.jugador2 != null) {
            Posicion p2;
            if (nivelActual.obtenerPosInicialJugador2() != null) {
                p2 = nivelActual.obtenerPosInicialJugador2();
            } else {
                p2 = new Posicion(550, 500);
            }
            this.jugador2 = new TanqueJugador(2, p2, gestorSonido, VELOCIDAD_TANQUE_JUGADOR, SALUD_TANQUE_JUGADOR, TIEMPO_POR_DISPARO_JUGADOR, "TANQUE_SEGUNDO_JUGADOR_0", "TANQUE_SEGUNDO_JUGADOR_1");
            this.nivelActual.agregarTanque(jugador2);
        }
        this.estadoJuego = EstadoJuego.CORRIENDO;
    }

    /**
     * Devuelve el estado actual del juego.
     * @return estado del juego
     */
    public EstadoJuego obtenerEstadoJuego() {
        return estadoJuego;
    }

    /**
     * Indica si el juego ha finalizado.
     * @return true si el estado es FIN
     */
    public boolean esFinDelJuego() {
        return estadoJuego == EstadoJuego.FIN;
    }

    /**
     * Indica si el juego ha sido ganado.
     * @return true si el estado es VICTORIA
     */
    public boolean esVictoria() {
        return estadoJuego == EstadoJuego.VICTORIA;
    }

    /**
     * Devuelve el número del nivel actual.
     * @return número de nivel
     */
    public int getNroNivelActual() {
        return nroNivelActual;
    }

    /**
     * Verifica si existe el archivo del nivel indicado.
     * @param nroNivel número de nivel a verificar
     * @return true si el archivo existe, false en caso contrario
     */
    public boolean existeNivel(int nroNivel) {
        String pathNivel = "src/main/resources/niveles/GeneratedLevels/Level" + nroNivel + ".xml";
        java.io.File archivoNivel = new java.io.File(pathNivel);
        return archivoNivel.exists();
    }

    /**
     * Actualiza el estado del juego según la existencia de la base, tanques y enemigos.
     * Cambia el estado a FIN, NIVEL_COMPLETO o VICTORIA según corresponda.
     */
    public void actualizarEstado() {
        if (nivelActual != null) {
            BloqueBase base = nivelActual.obtenerBase();
            if (base == null || !base.existe()) {
                this.estadoJuego = EstadoJuego.FIN;
                return;
            }
            if (nivelActual.nivelCompleto()) {
                this.estadoJuego = EstadoJuego.NIVEL_COMPLETO;
                return;
            }
        } else {
            this.estadoJuego = EstadoJuego.VICTORIA;
        }
    }
}
