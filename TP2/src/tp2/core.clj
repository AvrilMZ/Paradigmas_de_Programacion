(ns tp2.core
		(:require [tp2.parseo :as parseo]
							[tp2.vm :as vm]
							[tp2.render :as r])
		(:import (clojure.lang ExceptionInfo)
						 (javax.swing JFrame JPanel JTextArea JLabel WindowConstants JScrollPane SwingUtilities)
						 (javax.swing.border EmptyBorder)
						 (java.awt.image BufferedImage)
						 (java.awt Dimension BorderLayout))
		(:gen-class))

(defn eval-pixel
		"Delega en eval-pixel de render."
		[codigo x y t]
		(r/eval-pixel codigo x y t))

(defn- rgb-int
		"Convierte [r g b] a entero RGB 24-bit.
		 Verifica que cada valor esté en el rango [0-255] y lanza una excepción si no."
		[r g b]
		(when-not (and (number? r) (number? g) (number? b)
									 (<= 0 r 255) (<= 0 g 255) (<= 0 b 255))
				(throw (ex-info (str "Valores RGB fuera de rango: r=" r " g=" g " b=" b)
												{:tipo :rgb-fuera-rango :r r :g g :b b})))
		(-> (bit-shift-left (int r) 16)
				(bit-or (bit-shift-left (int g) 8))
				(bit-or (int b))))

(def app-state
		"Estado global de la aplicación."
		(atom
				{:code           ""
				 :compiled-code  nil
				 :t              0
				 :error          nil
				 :running-thread nil
				 :gui-components nil
				 }))

(def target-frame-ms 100)

(defn- sleep-ms-after
		"Devuelve los ms a dormir para respetar el target de frame desde start-nano."
		[start-nano target-ms]
		(let [elapsed-ms (/ (- (System/nanoTime) start-nano) 1000000.0)]
				(long (max 0 (- target-ms elapsed-ms)))))

(defn- update-ui-frame
		"Dibuja un frame en el BufferedImage."
		[frame-data t]
		(when-let [gui (:gui-components @app-state)]
				(let [^BufferedImage image (:image-buffer gui)
							^JLabel time-label (:time-label gui)
							animation-panel (:animation-panel gui)]

						(dotimes [y r/image-size]
								(dotimes [x r/image-size]
										(let [[r0 g0 b0] (get-in frame-data [y x] [255 0 255])
													r (if (number? r0) (vm/clamp r0) 0)
													g (if (number? g0) (vm/clamp g0) 0)
													b (if (number? b0) (vm/clamp b0) 0)
													rgb-int (rgb-int r g b)]
												(.setRGB image x y rgb-int))))

						(.setText time-label (str "t = " t))
						(.repaint animation-panel))))

(defn- update-ui-error
		"Muestra un mensaje de error en la etiqueta."
		[error-msg]
		(when-let [gui (:gui-components @app-state)]
				(let [^JLabel time-label (:time-label gui)]
						(.setText time-label (str "Error: " error-msg)))))

(defn- animation-loop
		"Corre en un hilo separado (Worker Thread). Recibe código precompilado (vector de instrucciones)."
		[compiled-code]
		(try
				(loop [t 0]
						(when (not (.isInterrupted (Thread/currentThread)))
								(let [start-time (System/nanoTime)
											anim-thread (Thread/currentThread)]

										(try
												(let [frame (r/render-frame compiled-code t)]
														(SwingUtilities/invokeLater
																#(when (= anim-thread (:running-thread @app-state))
																		 (update-ui-frame frame t))))

												(catch Exception e
														(let [msg (or (.getMessage e) "Error en la VM")]
																(SwingUtilities/invokeLater
																		#(when (= anim-thread (:running-thread @app-state))
																				 (update-ui-error msg))))
														(throw (InterruptedException. "Error de VM."))))

										(Thread/sleep (sleep-ms-after start-time target-frame-ms))

										(recur (mod (inc t) 256)))))

				(catch InterruptedException _
						(println "Animation thread stopped."))))

(defn- restart-animation-thread!
		"Interrumpe el hilo actual (si existe) y arranca uno nuevo con el código compilado."
		[compiled]
		(let [old-thread (:running-thread @app-state)
					new-thread (Thread. #(animation-loop compiled))]
				(when old-thread
						(.interrupt old-thread))
				(swap! app-state assoc :running-thread new-thread)
				(.start new-thread)))

(defn start-animation!
		"Compila el código actual del área de texto y arranca (o reinicia) el hilo de animación."
		[]
		(let [gui (:gui-components @app-state)
					new-code-string (-> gui :text-area .getText)]
				(try
						(parseo/parseo-codigo new-code-string)
						(let [compiled (vm/compile-code new-code-string)]
								(swap! app-state assoc :code new-code-string :compiled-code compiled :t 0 :error nil)
								(restart-animation-thread! compiled))
						(catch ExceptionInfo e
								(update-ui-error (.getMessage e))
								(when-let [old-thread (:running-thread @app-state)]
										(.interrupt old-thread)
										(swap! app-state assoc :running-thread nil))))))

(defn- create-animation-panel
		"Crea el panel que dibuja la animación."
		[]
		(let [image-buffer (BufferedImage. r/image-size r/image-size BufferedImage/TYPE_INT_RGB)
					panel (proxy [JPanel] []
										(paintComponent [g]
												(proxy-super paintComponent g)
												(.drawImage g image-buffer 0 0 nil))

										(getPreferredSize []
												(Dimension. r/image-size r/image-size))
										(getMinimumSize []
												(Dimension. r/image-size r/image-size))
										(getMaximumSize []
												(Dimension. r/image-size r/image-size)))]
				{:panel panel :image-buffer image-buffer}))

(defn- build-gui!
		"Crea y muestra la ventana principal."
		[initial-code]
		(let [frame (JFrame. "TP2 - Animador")
					main-panel (JPanel. (BorderLayout. 10 10))
					text-area (JTextArea.)
					scroll-pane (JScrollPane. text-area)
					time-label (JLabel. "t = 0")
					{:keys [panel image-buffer]} (create-animation-panel)
					controls-panel (JPanel. (BorderLayout. 5 5))]

				(.setRows text-area 0.5)
				(.setLineWrap text-area true)
				(.setWrapStyleWord text-area true)
				(.setText text-area (or initial-code ""))

				(let [doc (.getDocument text-area)]
						(.addDocumentListener doc
																	(proxy [javax.swing.event.DocumentListener] []
																			(insertUpdate [e] (start-animation!))
																			(removeUpdate [e] (start-animation!))
																			(changedUpdate [e] (start-animation!)))))

				(.add controls-panel scroll-pane BorderLayout/CENTER)
				(.add controls-panel time-label BorderLayout/SOUTH)

				(.setBorder main-panel (EmptyBorder. 10 10 10 10))
				(.add main-panel controls-panel BorderLayout/NORTH)
				(.add main-panel panel BorderLayout/CENTER)

				(swap! app-state assoc :gui-components {:text-area       text-area
																								:time-label      time-label
																								:image-buffer    image-buffer
																								:animation-panel panel})

				(.setContentPane frame main-panel)
				(.setDefaultCloseOperation frame WindowConstants/EXIT_ON_CLOSE)
				(.setResizable frame false)
				(.pack frame)
				(.setLocationRelativeTo frame nil)
				(.setVisible frame true)

				(when (not (empty? initial-code))
						(start-animation!))))

(defn -main
		"Punto de entrada de 'lein run'."
		[& args]
		(let [initial-code (first args)]
				(SwingUtilities/invokeLater
						#(build-gui! initial-code))))