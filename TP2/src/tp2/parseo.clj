(ns tp2.parseo)

(def comandos-validos
		"Conjunto de todos los comandos válidos según la especificación."
		#{\X \Y \T \N \C \D \P \S \R \M
			\! \+ \- \* \^ \& \| \= \< \>
			\/ \% \[ \]
			\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(def max-code-length 1024)

(defn caracter-valido?
		"Verifica si un carácter es un comando válido de la VM.
		 Retorna true si el carácter está en comandos-validos, false en caso contrario."
		[caracter]
		(contains? comandos-validos caracter))

(defn parseo-codigo
		"Valida que el código no supere la longitud máxima y que todos sus caracteres sean comandos válidos.
		 Si todos los caracteres son válidos, retorna el código sin modificar; en caso contrario lanza excepción con información del error."
		[codigo]
		(let [codigo (or codigo "")
					len (count codigo)]
				(when (> len max-code-length)
						(throw (ex-info "Código demasiado largo"
														{:tipo :codigo-largo :longitud len :max max-code-length})))
				(let [caracteres (seq codigo)
							invalido (first (remove caracter-valido? caracteres))]
						(if invalido
								(throw (ex-info (str "Comando inválido: " invalido)
																{:tipo :error-parseo :caracter invalido}))
								codigo))))