; Definir la función slice que reciba una cadena s y un número n y devuelva una lista con todas las subcadenas contiguas de s cuyo tamaño sea n. Por ejemplo:
; (slice "abcde" 3) → ("abc" "bcd" "cde")

(defn slice [s n]
				(for [i (range (- (count s) (dec n)))]
								(subs s i (+ i n))))