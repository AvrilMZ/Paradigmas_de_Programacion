# TP2 - Generador de Animaciones

> **Universidad de Buenos Aires, Facultad de Ingeniería**  
> **[_Paradigmas de Programación (TB025) - Curso Essaya_](https://algoritmos3ce.github.io/)**

### Docentes

- Diego Nicolás Essaya
- Santiago Maraggi Casabal

##### Corrector

- Lihuén Carranza

### Integrantes del Grupo "MorfeoZerbi Valencia"

- 112563 - [Avril Victoria Morfeo Zerbi](https://github.com/AvrilMZ)
    - Video: https://youtu.be/c30ReSIrF4k
- 112776 - [Nicolás Valencia](https://github.com/nicoValencia25)
    - Video: https://youtu.be/G9cp7FNTTnY

## Descripción

El objetivo del trabajo práctico es desarrollar un programa en Clojure que permita generar una animación de 256 cuadros
controlando los valores RGB de cada píxel.

El usuario puede ingresar un código en un lenguaje propio que la aplicación interpreta mediante una máquina virtual.
Esta máquina evalúa, para cada píxel y cuadro de tiempo `t`, una secuencia de comandos que definen los valores de color
`(R, G, B)`.

La animación se reproduce a ~10 cuadros por segundo, mostrando el resultado del código ingresado.

### Dependencias

- [Clojure](https://clojure.org/) - Versión recomendada 1.12.X
- [Leiningen](https://leiningen.org/) - Versión recomendada 2.10.X

### Ejecución

- Iniciar la aplicación con el campo de texto **vacío**:

  ```bash
  lein run
  ```

- Inicializar el campo de texto con el **código indicado**:

    ```bash
    lein run <codigo>
    lein run <codigo> <x> <y> <t>
    ```

- Testing:

    ```bash
    lein test
    ```

### Máquina Virtual

Cada píxel se calcula con una función pura que recibe:

```bash
código, x, y, t
```

donde:

- `código`: String a ejecutar de hasta 1024 caracteres
- `x`, `y`: Las coordenadas del pixel (0 - 255)
- `t`: El tiempo actual (0 - 255)

devolviendo `(R, G, B)` (0 - 255)

El estado de la máquina virtual incluye:

- **IDX**: contador de instrucciones.
- **DS**: pila de datos (hasta 8 enteros 32 bits).
- **LS**: pila de ciclos (capacidad 8).
- **M**: modo de manejo de división por 0 (0, 1 o 2).

| Comando | Descripción                                       |
|---------|---------------------------------------------------|
| X       | Apila `x`                                         |
| Y       | Apila `y`                                         |
| T       | Apila `t`                                         |
| N       | Apila 0                                           |
| 0 - 9   | Multiplica el tope de DS por 10 y suma el dígito  |
| C       | Clamp (0 - 255) sobre el tope de DS               |
| D       | Duplica el tope de DS                             |
| P       | Desapila y descarta                               |
| S       | Intercambia los dos valores en el tope de DS      |
| R       | Rota los tres valores del tope de DS              |
| !       | Desapila `a`, apila 1 si a = 0, 0 si a != 0       |
| +       | Suma los dos topes de DS                          |
| -       | Resta los dos topes de DS                         |
| *       | Multiplica los dos topes de DS                    |
| ^       | XOR bit a bit de los dos topes                    |
| &       | AND bit a bit de los dos topes                    |
| \|      | OR bit a bit de los dos topes                     |
| =       | Apila 1 si los dos topes son iguales, 0 si no     |
| <       | Apila 1 si a < b, 0 si no                         |
| \>      | Apila 1 si a > b, 0 si no                         |
| /       | División entera (manejo según M si divisor = 0)   |
| %       | Módulo euclídeo (manejo según M si divisor = 0)   |
| M       | Incrementa modo de manejo de división por 0       |
| [       | Inicio de ciclo (desapila contador)               |
| ]       | Fin de ciclo (decrementa contador y repite si >0) |
