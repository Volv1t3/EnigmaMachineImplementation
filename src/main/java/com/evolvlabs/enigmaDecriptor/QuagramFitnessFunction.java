package com.evolvlabs.enigmaDecriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : Clase que implementa la interfaz FitnessFunction para calcular el puntaje de una cadena de texto
 * basado en el índice de coincidencia de quadgramas (cuatro letras) en un texto dado.
 */
public class QuagramFitnessFunction implements FitnessFunction {
    private float[] e_quadGrams;
    private static final int QUAD_SIZE = 4;
    private static final int ALPHABET_SIZE = 26;

    
    /**
     * Calcula el indice unico para un quadgrama (una secuencia de cuatro letras) basado en 
     * sus valores numericos correspondientes dentro de un alfabeto de tamano fijo.
     *
     * Este metodo toma 4 letras representadas numericamente como los parametros a, b, c y d.
     * Luego calcula un indice unico mediante la suma jerarquica: el primer caracter tiene el peso mayor, 
     * seguido por el segundo, tercero y cuarto caracter, basados en potencias del tamano del alfabeto.
     *
     * @param a Representa numericamente la primera letra del quadgrama, donde 'A' equivale a 0.
     * @param b Representa numericamente la segunda letra del quadgrama, donde 'A' equivale a 0.
     * @param c Representa numericamente la tercera letra del quadgrama, donde 'A' equivale a 0.
     * @param d Representa numericamente la cuarta letra del quadgrama, donde 'A' equivale a 0.
     * @return El indice unico calculado para la combinacion de letras usando la jerarquia y formula numerica.
     */
    private static int quadIndex(int a, int b, int c, int d) {
        
        return a * (ALPHABET_SIZE * ALPHABET_SIZE * ALPHABET_SIZE) +
                b * (ALPHABET_SIZE * ALPHABET_SIZE) +
                c * ALPHABET_SIZE +
                d;
    }


    /**
     * <b> Constructor de la clase QuagramFitnessFunction.</b>
     * <br>
     * <p>Este constructor inicializa el arreglo que almacenara los puntajes de los quadgramas,
     * con un tamano especifico basado en todas las combinaciones posibles de 4 letras en un
     * alfabeto de tamano fijo. Posteriormente, llama al metodo initializeQuadgrams para cargar
     * los valores de los puntajes de quadgramas desde un archivo externo.</p>
     * <br>
     * <p>El tamano del arreglo e_quadGrams se determina como ALPHABET_SIZE elevado a la cuarta potencia
     *   (ALPHABET_SIZE^4), lo que representa todas las combinaciones posibles de quadgramas.</p>
     * <br>
     * <p>Una vez inicializado el arreglo, el metodo initializeQuadgrams se encarga de llenarlo con
     *   valores de puntaje leidos de un archivo CSV.</p>
     */
    public QuagramFitnessFunction() {
        // Tamano correcto: 26^4 = 456,976
        this.e_quadGrams = new float[ALPHABET_SIZE * ALPHABET_SIZE * ALPHABET_SIZE * ALPHABET_SIZE];
        initializeQuadgrams();
    }


    /**
     * Este metodo inicializa el arreglo de puntajes para los quadgramas leyendo los datos desde
     * un archivo externo en formato CSV. Si un quadgrama tiene un formato valido (cuatro letras),
     * se calcula un indice unico y se actualiza el puntaje para dicho indice en el arreglo.
     * <br>
     * <p>El archivo debe estar ubicado en la ruta especificada y tener un formato de texto con dos
     * columnas separadas por comas. La primera columna debe contener el quadgrama (cuatro letras),
     * mientras que la segunda columna contiene el puntaje asociado.</p>
     * 
     * <p>Por defecto, todos los valores en el arreglo son inicializados con una probabilidad baja
     * obtenida mediante {@link #getEpsilon()} antes de cargar los valores desde el archivo.</p>
     *
     * <p>En caso de error al cargar el archivo, se imprime un mensaje de error y el arreglo
     * de quadgramas se establece en null.</p>
     */
    private void initializeQuadgrams() {
        //! Inicializa el arreglo con la probabilidad base
        Arrays.fill(this.e_quadGrams, this.getEpsilon());
    
        //! Carga el archivo desde la carpeta de resources del programa
        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/fitnessFunction_QuadGrams.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String quad = parts[0].toUpperCase();
                    float score = Float.parseFloat(parts[1]);
    
                    if (quad.length() == QUAD_SIZE) {
                        int a = quad.charAt(0) - 'A';
                        int b = quad.charAt(1) - 'A';
                        int c = quad.charAt(2) - 'A';
                        int d = quad.charAt(3) - 'A';
    
                        if (a >= 0 && a < ALPHABET_SIZE &&
                                b >= 0 && b < ALPHABET_SIZE &&
                                c >= 0 && c < ALPHABET_SIZE &&
                                d >= 0 && d < ALPHABET_SIZE) {
    
                            int index = quadIndex(a, b, c, d);
                            if (index >= 0 && index < e_quadGrams.length) {
                                this.e_quadGrams[index] = score;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading quadgrams: " + e.getMessage());
            this.e_quadGrams = null;
        }
    }

    /**
     * Calcula la puntuacion de aptitud de un texto dado basado en los quadgramas contenidos en el.
     * <br><br>
     * <p>Este metodo analiza el texto caracter por caracter, creando todos los posibles conjuntos
     * de cuatro caracteres contiguos (quadgramas). Luego, calcula la suma total de las puntuaciones
     * correspondientes a esos quadgramas, utilizando un arreglo predefinido de valores. Si el texto
     * no contiene suficientes caracteres para formar un quadgrama valido o el arreglo esta sin 
     * inicializar, se devuelve Float.NEGATIVE_INFINITY.</p>
     * 
     * @param text Arreglo de caracteres que representa el texto que se va a analizar.
     *             Debe tener como minimo cuatro caracteres para contener al menos un quadgrama.
     * @return La puntuacion promedio de aptitud calculada para todos los quadgramas
     *         validos en el texto. Si no hay quadgramas validos, retorna Float.NEGATIVE_INFINITY.
     */
    @Override
    public float score(char[] text) {
        if (text == null || text.length < QUAD_SIZE || this.e_quadGrams == null) {
            return Float.NEGATIVE_INFINITY;
        }
    
        float fitness = 0;
        int count = 0;
    
        // Process all possible quadgrams in the text
        for (int i = 0; i <= text.length - QUAD_SIZE; i++) {
            int a = normalizeChar(text[i]);
            int b = normalizeChar(text[i + 1]);
            int c = normalizeChar(text[i + 2]);
            int d = normalizeChar(text[i + 3]);
    
            if (a >= 0 && b >= 0 && c >= 0 && d >= 0) {
                int index = quadIndex(a, b, c, d);
                if (index >= 0 && index < e_quadGrams.length) {
                    fitness += e_quadGrams[index];
                    count++;
                }
            }
        }
    
        return count > 0 ? fitness / count : Float.NEGATIVE_INFINITY;
    }


    /**
     * Normaliza un caracter convirtiendolo a mayuscula y calculando su posicion dentro del alfabeto.
     * <br><br>
     * <p>Este metodo toma como entrada un caracter, lo convierte a mayuscula y determina su
     * desplazamiento numerico con respecto a la letra 'A'. Si el caracter no es una letra
     * valida dentro del rango del alfabeto definido, devuelve -1.</p>
     *
     * @param c El caracter que se desea normalizar.
     *          Puede ser cualquier caracter unicode.
     * @return Un entero que representa la posicion 0-indexada del caracter en el alfabeto,
     *         o -1 si el caracter no pertenece al alfabeto.
     */
    private int normalizeChar(char c) {
        int normalized = Character.toUpperCase(c) - 'A';
        return (normalized >= 0 && normalized < ALPHABET_SIZE) ? normalized : -1;
    }


}