; Definir la función segundos que reciba los cuatro valores (días, horas, minutos y segundos) del tiempo que dura un evento y devuelva el valor de ese tiempo expresado solamente en segundos.

(defn segundos [dias horas minutos segundos]
			(let [dias-a-segundos (* dias 86400)
						horas-a-segundos (* horas 3600)
						minutos-a-segundos (* minutos 60)]
					 (+ dias-a-segundos horas-a-segundos minutos-a-segundos segundos)))