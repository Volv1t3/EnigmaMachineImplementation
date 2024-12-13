package com.evolvlabs.enigmaDecriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Stream;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 3, december, 2024
 * @Description : This class implements the FitnessFunction interface and provides a scoring function based 
 * on single character frequencies in the English language.
 */
public class SinglegramFitnessFunction implements FitnessFunction {

    private float[] singles;

    /**
     * <b>Constructor de la clase SinglegramFitnessFunction.</b><br>
     *
     * <p>Este metodo inicializa el arreglo 'singles' con los valores de frecuencias de
     * los caracteres del alfabeto ingles almacenados en un archivo CSV. El archivo
     * debe tener un formato donde cada fila contenga una letra y su frecuencia separadas por coma.</p>
     *<br>
     * Funcionamiento interno:<br>
     * - El archivo "fitnessFunction_SingleGrams.csv" es leido utilizando BufferedReader y Stream.<br>
     * - Cada linea del archivo es dividida en un arreglo de Strings con la letra en la posicion 0
     *   y su frecuencia en la posicion 1.<br>
     * - La letra es convertida en un indice basado en su posicion en el alfabeto (A=0, B=1, ..., Z=25).<br>
     * - Los valores de frecuencia son parseados a float y almacenados en el arreglo 'singles'.<br>
     *<br>
     * <p>En caso de que haya una excepcion de tipo IOException durante la lectura del archivo,
     * el arreglo 'singles' se inicializa como null.</p>
     */
    public SinglegramFitnessFunction() {
        this.singles = new float[26];
        try (
                final Reader r = new FileReader("src/main/resources/fitnessFunction_SingleGrams.csv");
                final BufferedReader br = new BufferedReader(r);
                final Stream<String> lines = br.lines()) {
            lines.map(line -> line.split(","))
                    .forEach(s -> {
                        int i = s[0].charAt(0) - 65;
                        this.singles[i] = Float.parseFloat(s[1]);
                    });
        } catch (IOException e) {
            this.singles = null;
        }
    }

    /**
     * Metodo: score<br>
     * <p>
     * Este metodo evalua un arreglo de caracteres para calcular una puntuacion 
     * de adecuacion basado en las frecuencias individuales de cada caracter en ingles.
     * Se aplica un ajuste adicional basado en la longitud del texto.<br>
     * </p>
     * Funcionamiento Interno:<br>
     * 1. Si el texto es null o tiene longitud 0, retorna <code>Float.NEGATIVE_INFINITY</code>.<br>
     * 2. Recorre el arreglo, obteniendo la puntuacion acumulada de cada letra basada 
     * en el arreglo de frecuencias.<br>
     * 3. Ignora caracteres fuera de A-Z.<br>
     * 4. Calcula y aplica un bono basado en la longitud del texto.<br>
     * 5. Retorna la puntuacion final como una media ponderada con el bono de longitud.<br>
     *
     * @param text Arreglo de caracteres a analizar.
     * @return Puntuacion float basada en las frecuencias individuales del texto.
     */
    @Override
    public float score(char[] text) {

        //! Casos Base
        if (text == null || text.length == 0){
            return Float.NEGATIVE_INFINITY;
        }

        //! Analysis Interno
        float fitness = 0;
        float length = text.length;
        for(char c: text){
            int indexOfC = c - 65;
            if (indexOfC < 0 || indexOfC > 25) {continue;}
            fitness += this.singles[indexOfC];
        }
        float lengthBons =  (1.0f + Math.min(0.5f, length/ 10.0f));
        return (fitness / length) * lengthBons;
    }
}
