(ns tp2.vm)

(def max-stack-size 8)
(def int-min -2147483648)
(def int-max 2147483647)

(defn clamp
		"Limita un valor al rango [0, 255].
		 Retorna un número entre 0 y 255 inclusive."
		[v]
		(cond
				(< v 0) 0
				(> v 255) 255
				:else v))

(defn error-vm
		"Lanza una excepción de error de la máquina virtual."
		([msg]
		 (throw (ex-info msg {:tipo :error-vm})))
		([msg modo]
		 (throw (ex-info msg {:tipo :error-vm :modo modo}))))

(defn puede-apilar?
		"Verifica si hay espacio disponible en la pila de datos (DS).
		 Retorna true si DS tiene menos de 'max-stack-size' elementos, false en caso contrario."
		[estado]
		(let [tamano-actual (count (:ds estado))]
				(< tamano-actual max-stack-size)))

(defn puede-desapilar?
		"Verifica si hay suficientes elementos en la pila de datos (DS).
		 Retorna true si DS tiene al menos 'n' elementos, false en caso contrario."
		[estado n]
		(let [tamano-actual (count (:ds estado))]
				(>= tamano-actual n)))

(defn push-value
		"Apila un valor en la pila de datos (DS).
		 Retorna estado con 'v' en el tope de DS, si no hay espacio, lanza error."
		[estado v]
		(if (puede-apilar? estado)
				(let [ds-actual (:ds estado)
							ds-nuevo (cons v ds-actual)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila llena")))

(defn push-x
		"Apila el valor de la coordenada X en DS.
		 Retorna estado con X apilado en DS."
		[estado]
		(push-value estado (:x estado)))

(defn push-y
		"Apila el valor de la coordenada Y en DS.
		 Retorna estado con Y apilado en DS."
		[estado]
		(push-value estado (:y estado)))

(defn push-t
		"Apila el valor del tiempo T en DS.
		 Retorna estado con T apilado en DS."
		[estado]
		(push-value estado (:t estado)))

(defn push-n
		"Apila el valor 0 en DS.
		 Retorna estado con 0 apilado en DS."
		[estado]
		(push-value estado 0))

(defn push-digit
		"Construye números decimales multiplicando el tope por 10 y sumando el dígito. Si DS está vacía, simplemente apila el dígito.
		 Retorna estado con el número modificado o apilado en DS."
		[estado d]
		(let [ds-actual (:ds estado)]
				(if (empty? ds-actual)
						(push-value estado d)
						(let [tope (first ds-actual)
									resto (rest ds-actual)
									nuevo-valor (+ (* 10 tope) d)
									ds-nuevo (cons nuevo-valor resto)]
								(assoc estado :ds ds-nuevo)))))

(defn cmd-clamp
		"Comando C: Aplica clamp al tope de DS limitándolo al rango [0, 255].
		 Si DS no está vacía, retorna estado con tope clampeado, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 1)
				(let [ds-actual (:ds estado)
							tope (first ds-actual)
							resto (rest ds-actual)
							tope-clampeado (clamp tope)
							ds-nuevo (cons tope-clampeado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila vacía")))

(defn cmd-dup
		"Comando D: Duplica el valor en el tope de DS.
		 Si DS tiene al menos 1 elemento y hay espacio, duplica el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 1)
				(if (puede-apilar? estado)
						(let [ds-actual (:ds estado)
									tope (first ds-actual)
									ds-nuevo (cons tope ds-actual)]
								(assoc estado :ds ds-nuevo))
						(error-vm "Pila llena"))
				(error-vm "Pila vacía")))

(defn cmd-pop
		"Comando P: Desapila y descarta el tope de DS.
		 Si DS no está vacía, retorna estado sin el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 1)
				(let [ds-actual (:ds estado)
							ds-nuevo (rest ds-actual)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila vacía")))

(defn cmd-swap
		"Comando S: Intercambia los dos valores en el tope de DS.
		 Si DS tiene al menos 2 elementos, intercambia a y b, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							a (first ds-actual)
							b (second ds-actual)
							resto (rest (rest ds-actual))
							ds-nuevo (cons b (cons a resto))]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-rot
		"Comando R: Rota los tres valores en el tope de DS.
		 Si DS tiene al menos 3 elementos, rota los 3 del tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 3)
				(let [ds-actual (:ds estado)
							a (first ds-actual)
							b (second ds-actual)
							c (nth ds-actual 2)
							resto (rest (rest (rest ds-actual)))
							ds-nuevo (cons c (cons a (cons b resto)))]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-not
		"Comando !: Negación lógica. Desapila a, apila 1 si a = 0, sino apila 0.
		 Si DS no está vacía, reemplaza tope con su negación lógica, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 1)
				(let [ds-actual (:ds estado)
							a (first ds-actual)
							resto (rest ds-actual)
							resultado (if (zero? a) 1 0)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila vacía")))

(defn cmd-add
		"Comando +: Suma los dos valores del tope de DS.
		 Si DS tiene al menos 2 elementos y no hay desbordamiento, retorna estado con la suma en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (+' a b)]
						(if (and (>= resultado int-min)
										 (<= resultado int-max))
								(assoc estado :ds (cons resultado resto))
								(error-vm "Desbordamiento en suma")))
				(error-vm "Pila insuficiente")))

(defn cmd-sub
		"Comando -: Resta los dos valores del tope de DS.
		 Si DS tiene al menos 2 elementos y no hay desbordamiento, retorna estado con la resta en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (-' a b)]
						(if (and (>= resultado int-min)
										 (<= resultado int-max))
								(assoc estado :ds (cons resultado resto))
								(error-vm "Desbordamiento en resta")))
				(error-vm "Pila insuficiente")))

(defn cmd-mul
		"Comando *: Multiplica los dos valores del tope de DS.
		 Si DS tiene al menos 2 elementos y no hay desbordamiento, retorna estado con el producto en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (*' a b)]
						(if (and (>= resultado int-min)
										 (<= resultado int-max))
								(assoc estado :ds (cons resultado resto))
								(error-vm "Desbordamiento en multiplicación")))
				(error-vm "Pila insuficiente")))

(defn cmd-xor
		"Comando ^: XOR bit a bit. Desapila b, desapila a, apila (a XOR b).
		 Si DS tiene al menos 2 elementos, retorna estado con XOR en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (bit-xor a b)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-and
		"Comando &: AND bit a bit. Desapila b, desapila a, apila (a AND b).
		 Si DS tiene al menos 2 elementos, retorna estado con AND en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (bit-and a b)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-or
		"Comando |: OR bit a bit. Desapila b, desapila a, apila (a OR b).
		 Si DS tiene al menos 2 elementos, retorna estado con OR en el tope, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (bit-or a b)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-eq
		"Comando =: Igualdad. Desapila 2 valores, apila 1 si son iguales, 0 si no.
		 Si DS tiene al menos 2 elementos, retorna estado con resultado de comparación, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (if (= a b) 1 0)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-lt
		"Comando <: Menor que. Desapila b, desapila a, apila 1 si a < b, 0 si no.
		 Si DS tiene al menos 2 elementos, retorna estado con resultado de comparación, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (if (< a b) 1 0)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-gt
		"Comando >: Mayor que. Desapila b, desapila a, apila 1 si a > b, 0 si no.
		 Si DS tiene al menos 2 elementos, retorna estado con resultado de comparación, en caso contrario lanza error."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							resultado (if (> a b) 1 0)
							ds-nuevo (cons resultado resto)]
						(assoc estado :ds ds-nuevo))
				(error-vm "Pila insuficiente")))

(defn cmd-div
		"Comando /: División entera. Desapila b, desapila a, apila (a / b).
		 Si DS tiene al menos 2 elementos y b != 0, retorna estado con división, en caso contrario lanza error con información del modo M:
		 		- Modo 0: lanza error;
		 		- Modo 1: termina ejecución devolviendo RGB = [0, 0, 0];
		 		- Modo 2: termina ejecución devolviendo RGB = [255, 0, 0]."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							m (:m estado)]
						(if (zero? b)
								(error-vm "División por cero" m)
								(let [resultado (quot a b)
											ds-nuevo (cons resultado resto)]
										(assoc estado :ds ds-nuevo))))
				(error-vm "Pila insuficiente")))

(defn cmd-mod
		"Comando %: Módulo euclídeo. Desapila b, desapila a, apila (a % b).
		 Si DS tiene al menos 2 elementos y b != 0, retorna estado con módulo, en caso contrario lanza error con información del modo M:
		 		- Modo 0: lanza error;
		 		- Modo 1: termina ejecución devolviendo RGB = [0, 0, 0];
		 		- Modo 2: termina ejecución devolviendo RGB = [255, 0, 0]."
		[estado]
		(if (puede-desapilar? estado 2)
				(let [ds-actual (:ds estado)
							b (first ds-actual)
							a (second ds-actual)
							resto (rest (rest ds-actual))
							m (:m estado)]
						(if (zero? b)
								(error-vm "División por cero" m)
								(let [b-abs (if (neg? b) (- b) b)
											resultado (mod (rem a b) b-abs)
											ds-nuevo (cons resultado resto)]
										(assoc estado :ds ds-nuevo))))
				(error-vm "Pila insuficiente")))

(defn cmd-inc-m
		"Comando M: Incrementa el modo de manejo de división por cero.
		 Retorna estado con M incrementado módulo 3."
		[estado]
		(let [m-actual (:m estado)
					m-nuevo (mod (+ m-actual 1) 3)]
				(assoc estado :m m-nuevo)))

(def comando-fns
		"Mapeo de cada comando a su función de ejecución."
		{"X" push-x
		 "Y" push-y
		 "T" push-t
		 "N" push-n
		 "C" cmd-clamp
		 "D" cmd-dup
		 "P" cmd-pop
		 "S" cmd-swap
		 "R" cmd-rot
		 "!" cmd-not
		 "+" cmd-add
		 "-" cmd-sub
		 "*" cmd-mul
		 "^" cmd-xor
		 "&" cmd-and
		 "|" cmd-or
		 "=" cmd-eq
		 "<" cmd-lt
		 ">" cmd-gt
		 "/" cmd-div
		 "%" cmd-mod
		 "M" cmd-inc-m})

(defn ejecutar-instruccion
		"Ejecuta una instrucción individual sobre el estado de la VM.
		 Retorna el nuevo estado después de ejecutar la instrucción o lanza error si el comando es inválido."
		[estado instruccion]
		(let [es-digito (re-matches #"[0-9]" instruccion)]
				(if es-digito
						(let [digito (read-string instruccion)]
								(push-digit estado digito))
						(let [funcion-cmd (get comando-fns instruccion)]
								(if funcion-cmd
										(funcion-cmd estado)
										(error-vm (str "Comando inválido: " instruccion)))))))

(defn buscar-fin-ciclo
		"Encuentra el corchete de cierre ] correspondiente a un [ en la posición 'idx'.
		 Retorna el índice del ] correspondiente o lanza error si no hay cierre."
		[instrucciones idx]
		(loop [i (+ idx 1)
					 nivel 1]
				(cond
						(>= i (count instrucciones))
						(error-vm "Ciclo sin cierre")

						(= (nth instrucciones i) "[")
						(recur (+ i 1) (+ nivel 1))

						(= (nth instrucciones i) "]")
						(if (= nivel 1)
								i
								(recur (+ i 1) (- nivel 1)))

						:else
						(recur (+ i 1) nivel))))

(defn ejecutar-vm
		"Ejecuta el código completo en la máquina virtual y retorna [R G B].
		Retorna vector [R G B] con los 3 valores del tope de DS valores faltantes se completan con 0."
		[instrucciones x y t]
		(let [estado-inicial {:ds [] :ls [] :m 0 :idx 0 :x x :y y :t t}]
				(loop [estado estado-inicial]
						(let [idx (:idx estado)
									tamano-codigo (count instrucciones)]

								(if (>= idx tamano-codigo)
										(let [ds-final (:ds estado)
													b (first ds-final)
													g (second ds-final)
													r (nth ds-final 2 nil)]
												[(if r r 0) (if g g 0) (if b b 0)])

										(let [instr (nth instrucciones idx)]
												(cond
														(= instr "[")
														(if (puede-desapilar? estado 1)
																(let [ds-actual (:ds estado)
																			contador (first ds-actual)
																			ds-sin-contador (rest ds-actual)]
																		(if (pos? contador)
																				(let [ls-actual (:ls estado)
																							tamano-ls (count ls-actual)]
																						(if (< tamano-ls max-stack-size)
																								(let [info-ciclo {:pos (+ idx 1) :count contador}
																											ls-nuevo (cons info-ciclo ls-actual)
																											estado-nuevo (assoc estado
																																			 :ds ds-sin-contador
																																			 :ls ls-nuevo
																																			 :idx (+ idx 1))]
																										(recur estado-nuevo))
																								(error-vm "Pila de ciclos llena")))
																				(let [idx-fin (buscar-fin-ciclo instrucciones idx)
																							estado-nuevo (assoc estado
																															 :ds ds-sin-contador
																															 :idx (+ idx-fin 1))]
																						(recur estado-nuevo))))
																(error-vm "Pila vacía"))

														(= instr "]")
														(let [ls-actual (:ls estado)]
																(if (empty? ls-actual)
																		(error-vm "Ciclo sin inicio")
																		(let [info-ciclo (first ls-actual)
																					contador-actual (:count info-ciclo)
																					nuevo-contador (- contador-actual 1)]
																				(if (pos? nuevo-contador)
																						(let [info-actualizada {:pos (:pos info-ciclo) :count nuevo-contador}
																									ls-nuevo (cons info-actualizada (rest ls-actual))
																									estado-nuevo (assoc estado
																																	 :ls ls-nuevo
																																	 :idx (:pos info-ciclo))]
																								(recur estado-nuevo))
																						(let [ls-nuevo (rest ls-actual)
																									estado-nuevo (assoc estado
																																	 :ls ls-nuevo
																																	 :idx (+ idx 1))]
																								(recur estado-nuevo))))))

														:else
														(let [estado-nuevo (ejecutar-instruccion estado instr)
																	estado-con-idx (assoc estado-nuevo :idx (+ idx 1))]
																(recur estado-con-idx)))))))))

(defn compile-code
		"Convierte un String de programa en Vector de instrucciones.
		 Asume que el código ya fue validado (longitud y caracteres) por parseo/parseo-codigo."
		[codigo]
		(vec (map str (seq (or codigo "")))))

(defn pixel-color-compiled
		"Calcula el color RGB de un pixel usando instrucciones precompiladas.
		Retorna vector [R G B] con valores enteros o lanza excepción en caso de error"
		[instrucciones x y t]
		(try
				(ejecutar-vm instrucciones x y t)
				(catch clojure.lang.ExceptionInfo e
						(let [datos-error (ex-data e)
									modo (:modo datos-error)]
								(if modo
										(case modo
												0 (throw e)
												1 [0 0 0]
												2 [255 0 0])
										(throw e))))))