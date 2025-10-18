; Definir la función capicua? que reciba un número entero no negativo de hasta 5 dígitos y devuelva true si el número es capicúa; si no, false.

(defn cant-digitos-apropiada [numero]
			(<= (count (str numero)) 5))

(defn capicua? [numero]
			(if (cant-digitos-apropiada numero)
							(let [mitad (quot (count (str numero)) 2) ; quot: division entera
										pri-mitad (take mitad (str numero))
										seg-mitad (take mitad (drop (+ mitad (mod (count (str numero)) 2)) (str numero))) ; mod para ignorar el digito central si es impar
										rev (reverse seg-mitad)]
							(= pri-mitad rev))
			false))