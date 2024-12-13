package com.evolvlabs.enigmaDecriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : This class implements the FitnessFunction interface and calculates the fitness score of a given text 
 * based on the frequency of trigrams in the English language.
 */
public class TrigramFitnessFunction implements FitnessFunction{
    private float[] e_trigrams;

    /**
     * Metodo que calcula un indice unico para un trigram basado en tres enteros.
     * 
     * Este metodo toma tres enteros que representan las tres letras de un trigram.
     * Internamente, utiliza operaciones de desplazamiento de bits a la izquierda
     * y una operacion OR a nivel de bits para combinar los valores en un unico
     * entero, asegurando que cada trigram tenga un indice unico.
     *
     * @param a Entero que representa la primera letra del trigram (ajustada al rango esperado).
     * @param b Entero que representa la segunda letra del trigram (ajustada al rango esperado).
     * @param c Entero que representa la tercera letra del trigram (ajustada al rango esperado).
     * @return Un entero que representa el indice unico generado para el trigram.
     */
    private static int triIndex(int a, int b, int c ){
        return (a << 10) | (b << 5) | c;
    }


    /**
     * <b>Constructor que inicializa la funcion de aptitud basada en trigramas.</b>
     *<br><br>
     * <p>Este metodo inicializa el arreglo `e_trigrams` con valores predeterminados basados 
     * en el logaritmo en base 10 de un epsilon obtenido del metodo `getEpsilon`. Luego, 
     * utiliza un archivo CSV llamado "fitnessFunction_TriGrams.csv" para cargar los valores 
     * reales de las frecuencias de trigramas, asignandolos a los indices correspondientes 
     * en el arreglo `e_trigrams`.</p>
     *<br>
     * Funcionamiento interno:<br>
     * 1. Genera un arreglo de flotantes `e_trigrams` con tamano 26526.<br>
     * 2. Llena el arreglo con un valor predeterminado basado en el logaritmo del epsilon.<br>
     * 3. Lee un archivo CSV ubicado en "src/main/resources/fitnessFunction_TriGrams.csv":<br>
     *    - Cada linea del archivo debe tener un trigram y su valor de frecuencia, separados por una coma.<br>
     *    - El trigram es convertido en un indice unico con el metodo `triIndex`.<br>
     *    - El valor de la frecuencia es parseado y almacenado en el arreglo `e_trigrams` en el indice correspondiente.<br>
     *
     * @throws RuntimeException Si ocurre un error durante la lectura del archivo.
     */
    public TrigramFitnessFunction() {
        this.e_trigrams = new float[26526];
        Arrays.fill(this.e_trigrams, (float) Math.log10(this.getEpsilon()));
        try (
                final Reader reader = new FileReader("src/main/resources/fitnessFunction_TriGrams.csv");
                final BufferedReader bufferedReader = new BufferedReader(reader);
                final Stream<String> lines = bufferedReader.lines();
        ) {
            lines.map(lns -> lns.split(",")).forEach(
                    s -> {
                        String key = s[0];
                        int i = triIndex(key.charAt(0) - 65, key.charAt(1) - 65, key.charAt(2) - 65);
                        this.e_trigrams[i] = Float.parseFloat(s[1]);
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Este metodo calcula un puntaje de aptitud o "fitness" para un texto dado basado en trigramas.
     * <br><br>
     * Funcionamiento interno:<br>
     * <ol>
     *     <li>Define tres variables `current`, `next1` y `next2` para procesar el texto en bloques de tres caracteres.</li>
     *     <li>Inicializa las variables de manera que `next1` y `next2` contengan los valores numericos de las primeras dos letras del texto.</li>
     *     <li>Itera a lo largo del arreglo de texto, calculando el indice unico del trigram 
     *     usando el metodo `triIndex` y sumando el valor correspondiente desde el arreglo 
     *     `e_trigrams` al puntaje total `fitness`.</li>
     *     <li>Lleva un contador `count` del numero de trigramas procesados.</li>
     *     <li>Si no se procesa ningun trigram (es decir, el texto es demasiado corto), retorna 
     *     un puntaje de fitness negativo infinito.</li>
     *     <li>En otros casos, el puntaje final se normaliza dividiendo entre el contador 
     *     `count` y aplicando un ajuste basado en la longitud del texto.</li>
     * </ol>
     * 
     * @param text Un arreglo de caracteres que representa el texto a procesar.
     * @return Un valor flotante que representa el puntaje de aptitud calculado.
     */
    @Override
    public float score(char[] text) {
        float fitness = 0;
        float count = 0;
        int current = 0;
        int next1 = text[0] - 65;
        int next2 = text[1] - 65;
        for(int i = 2; i < text.length; i++){
            current = next1;
            next1 = next2;
            next2 = text[i] - 65;
            fitness += this.e_trigrams[triIndex(current, next1, next2)];
            count++;
        }

        if (count == 0){return Float.NEGATIVE_INFINITY;}

        return (fitness / count) * (1.0f + Math.min(0.3f, count/6.0f));

    }
}
