package org.game.visualizacion;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class VistaMenu extends VBox {
    private static final double TAMANIO_TITULO = 58;
    private static final double TAMANIO_OPCIONES = 24;
    private static final String TITULO = "BATTLE CITY";

    private final Label[] opciones;
    private String[] textos;
    private int indiceSeleccionado;

    private Runnable onSeleccion1Jugador;
    private Runnable onSeleccion2Jugadores;
    private Runnable onSeleccionSalir;

    /**
     * Constructor de VistaMenu. Inicializa el menú principal, carga fuentes, logo y configura las opciones.
     * También gestiona el manejo de eventos de teclado para la navegación y selección.
     * @param gestorImagen Gestor de imágenes para obtener el logo
     */
	public VistaMenu(GestorImagen gestorImagen) {
        this.textos = new String[]{"1 PLAYER", "2 PLAYERS", "EXIT"};
        this.indiceSeleccionado = 0;

		Font fuenteTitulo = Font.loadFont(getClass().getResourceAsStream("/tipografias/BattleCity.ttf"), TAMANIO_TITULO);
		Font fuenteRetro = Font.loadFont(getClass().getResourceAsStream("/tipografias/Arcade.ttf"), TAMANIO_OPCIONES);

        ImageView vistaLogo = new ImageView();
        Image logo = gestorImagen.obtenerImagen("LOGO");
        vistaLogo.setImage(logo);
        vistaLogo.setFitWidth(120);
        vistaLogo.setPreserveRatio(true);

        Label titulo = new Label(TITULO);
        if (fuenteTitulo != null) {
            titulo.setFont(fuenteTitulo);
        } else {
            titulo.setFont(Font.font("Monospaced", TAMANIO_TITULO));
        }
        titulo.setTextFill(Color.GREENYELLOW);
        titulo.setAlignment(Pos.CENTER);
        titulo.setMinWidth(320);
        titulo.setPadding(new Insets(10, 0, 30, 0));
        titulo.setStyle("");

        opciones = new Label[textos.length];
        for (int i = 0; i < textos.length; i++) {
            opciones[i] = new Label();
            if (fuenteRetro != null) {
                opciones[i].setFont(fuenteRetro);
            } else {
                opciones[i].setFont(Font.font("Monospaced", TAMANIO_OPCIONES));
            }
            opciones[i].setTextFill(Color.WHITE);
            opciones[i].setMinWidth(320);
            opciones[i].setAlignment(Pos.CENTER);
            opciones[i].setPadding(new Insets(8, 0, 8, 0));
            opciones[i].setStyle("");
        }
        actualizarSelector();

        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: BLACK;");
        this.setPadding(new Insets(40, 20, 40, 20));
        this.setMaxWidth(350);
        this.getChildren().clear();
        this.getChildren().addAll(vistaLogo, titulo);
        this.getChildren().addAll(opciones);

        // Manejo de teclado
        this.setFocusTraversable(true);
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.UP) {
                indiceSeleccionado = (indiceSeleccionado + opciones.length - 1) % opciones.length;
                actualizarSelector();
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                indiceSeleccionado = (indiceSeleccionado + 1) % opciones.length;
                actualizarSelector();
                e.consume();
            } else if (e.getCode() == KeyCode.ENTER) {
                manejarSeleccion();
                e.consume();
            }
        });
    }

    /**
     * Actualiza el selector visual del menú, resaltando la opción actualmente seleccionada.
     * Cambia el color y el prefijo de la opción activa.
     */
    private void actualizarSelector() {
        for (int i = 0; i < opciones.length; i++) {
            if (i == indiceSeleccionado) {
                opciones[i].setText("> " + textos[i]);
                opciones[i].setTextFill(Color.GREENYELLOW);
            } else {
                opciones[i].setText("  " + textos[i]);
                opciones[i].setTextFill(Color.WHITE);
            }
        }
    }

    /**
     * Ejecuta la acción correspondiente según la opción seleccionada en el menú.
     * Llama al Runnable asociado a la opción elegida.
     */
    private void manejarSeleccion() {
        switch (indiceSeleccionado) {
            case 0:
                if (onSeleccion1Jugador != null) {
                    onSeleccion1Jugador.run();
                }
                break;
            case 1:
                if (onSeleccion2Jugadores != null) {
                    onSeleccion2Jugadores.run();
                }
                break;
            case 2:
                if (onSeleccionSalir != null) {
                    onSeleccionSalir.run();
                }
                break;
        }
    }

    /**
     * Asigna la acción a ejecutar cuando se selecciona "1 PLAYER" en el menú.
     * @param r Acción a ejecutar
     */
    public void setOnSeleccion1Jugador(Runnable r) {
        this.onSeleccion1Jugador = r;
    }

    /**
     * Asigna la acción a ejecutar cuando se selecciona "2 PLAYERS" en el menú.
     * @param r Acción a ejecutar
     */
    public void setOnSeleccion2Jugadores(Runnable r) {
        this.onSeleccion2Jugadores = r;
    }

    /**
     * Asigna la acción a ejecutar cuando se selecciona "EXIT" en el menú.
     * @param r Acción a ejecutar
     */
    public void setOnSeleccionSalir(Runnable r) {
        this.onSeleccionSalir = r;
    }
}
