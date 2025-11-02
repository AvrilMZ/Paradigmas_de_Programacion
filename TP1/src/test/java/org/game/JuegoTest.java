package org.game;

import org.game.modelo.Direccion;
import org.game.modelo.EstadoJuego;
import org.game.modelo.Juego;
import org.game.visualizacion.GestorSonido;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JuegoTest {
    private Juego juego;
    private GestorSonido gestorSonido;

    @Before
    public void setUp() {
        gestorSonido = new GestorSonido();
        juego = new Juego(1, gestorSonido);
    }

    // Estado inicial del juego.
    @Test
    public void testEstadoInicial() {
        assertEquals(1, juego.getNroNivelActual());
        assertEquals(EstadoJuego.CORRIENDO, juego.obtenerEstadoJuego());
        assertNotNull(juego.obtenerJugador1());
        assertNull(juego.obtenerJugador2());
        assertNotNull(juego.obtenerNivelActual());
        assertFalse(juego.esVictoria());
        assertFalse(juego.esFinDelJuego());
    }

    // Disparar con el jugador 1 no lanza excepción y genera una bala.
    @Test
    public void testDisparoJugadorNoLanzaExcepcion() {
        int balasAntes = juego.obtenerNivelActual().obtenerBalas().size();
        juego.disparoJugador(1, Direccion.ARRIBA);
        int balasDespues = juego.obtenerNivelActual().obtenerBalas().size();
        assertTrue("Debe haber al menos una bala después de disparar", balasDespues > balasAntes);
    }

    // Disparar con un jugador inexistente no lanza excepción ni agrega balas.
    @Test
    public void testDisparoJugadorInexistenteNoLanzaExcepcion() {
        int balasAntes = juego.obtenerNivelActual().obtenerBalas().size();
        juego.disparoJugador(2, Direccion.ARRIBA);
        int balasDespues = juego.obtenerNivelActual().obtenerBalas().size();
        assertEquals("No debe agregar balas para jugador inexistente", balasAntes, balasDespues);
    }

    // Obtención de jugadores en modo 1 y 2 jugadores.
    @Test
    public void testObtenerJugadores() {
        assertNotNull(juego.obtenerJugador1());
        assertNull(juego.obtenerJugador2());

        Juego juego2 = new Juego(2, gestorSonido);
        assertNotNull(juego2.obtenerJugador1());
        assertNotNull(juego2.obtenerJugador2());
    }

    // Avance de nivel y el cambio de estado.
    @Test
    public void testAvanzarNivel() {
        int nivelAntes = juego.getNroNivelActual();
        EstadoJuego estadoAntes = juego.obtenerEstadoJuego();
        juego.avanzarNivel();
        if (estadoAntes == EstadoJuego.CORRIENDO) {
            if (juego.obtenerEstadoJuego() == EstadoJuego.CORRIENDO) {
                assertEquals(nivelAntes + 1, juego.getNroNivelActual());
            } else {
                assertEquals(EstadoJuego.VICTORIA, juego.obtenerEstadoJuego());
            }
        }
    }

    // Perder la base termina el juego.
    @Test
    public void testPerderBaseTerminaJuego() {
        juego.obtenerNivelActual().obtenerBase().impactoBala();
        juego.obtenerNivelActual().obtenerBloques().removeIf(b -> !b.existe());
        juego.actualizarEstado();
        assertEquals(EstadoJuego.FIN, juego.obtenerEstadoJuego());
        assertTrue(juego.esFinDelJuego() || juego.obtenerEstadoJuego() == EstadoJuego.FIN);
        assertFalse(juego.esVictoria());
    }

    // El juego llega a victoria correctamente.
    @Test
    public void testVictoria() {
        while (juego.obtenerEstadoJuego() == EstadoJuego.CORRIENDO || juego.obtenerEstadoJuego() == EstadoJuego.NIVEL_COMPLETO) {
            juego.avanzarNivel();
            juego.actualizarEstado();
        }
        assertEquals(EstadoJuego.VICTORIA, juego.obtenerEstadoJuego());
        assertTrue(juego.esVictoria());
        assertTrue(juego.esFinDelJuego() || juego.obtenerEstadoJuego() == EstadoJuego.VICTORIA);
    }

    // El juego no está terminado al inicio.
    @Test
    public void testFinDelJuegoInicial() {
        assertFalse(juego.esFinDelJuego());
    }

    // Avanzar de nivel después de victoria no cambia el estado.
    @Test
    public void testAvanzarNivelDespuesDeVictoria() {
        while (juego.obtenerEstadoJuego() == EstadoJuego.CORRIENDO || juego.obtenerEstadoJuego() == EstadoJuego.NIVEL_COMPLETO) {
            juego.avanzarNivel();
        }
        int nivelAntes = juego.getNroNivelActual();
        juego.avanzarNivel();
        assertEquals("No debe avanzar de nivel tras victoria", nivelAntes, juego.getNroNivelActual());
        assertEquals(EstadoJuego.VICTORIA, juego.obtenerEstadoJuego());
    }
}
