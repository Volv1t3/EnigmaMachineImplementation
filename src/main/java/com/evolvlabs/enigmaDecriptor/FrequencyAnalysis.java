package com.evolvlabs.enigmaDecriptor;

import java.util.*;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : This class is used to analyze the frequency of characters in a given text.
 * It provides methods to analyze the text, calculate the chi-squared statistic,
 * and retrieve the frequencies of characters.
 */
public class FrequencyAnalysis {
    //Taken straight from wikipedia ^^
    private static final float[] ENGLISH_FREQUENCIES = {
            0.082f, 0.015f, 0.028f, 0.043f, 0.127f, 0.022f, 0.020f, 0.061f, 0.070f,
            0.002f, 0.008f, 0.040f, 0.024f, 0.067f, 0.075f, 0.019f, 0.001f, 0.060f,
            0.063f, 0.091f, 0.028f, 0.0098f, 0.023f, 0.001f, 0.020f, 0.074f
    };

    private float[] counts;
    private int totalChars;
    private boolean normalized;

    
    /**
     * <b>Constructor de la clase FrequencyAnalysis.</b><br>
     * Este metodo inicializa las variables internas para preparar la instancia para analizar la frecuencia de caracteres.<br>
     * - "counts" se inicializa como un arreglo de flotantes de tamano 26, que representa las frecuencias de cada letra del abecedario.<br>
     * - "totalChars" se establece en 0 para contar la cantidad de caracteres procesados.<br>
     * - "normalized" se establece en "false" indicando que las frecuencias no estan normalizadas inicialmente.<br>
     */
    public FrequencyAnalysis() {
        this.counts = new float[26];
        this.totalChars = 0;
        this.normalized = false;
    }

    /**
     * <p>Este metodo analiza un arreglo de caracteres para calcular la frecuencia de cada letra en el texto.</p>
     * <br>
     * Funcionamiento Interno:
     * <ul>
     * <li>Si el parametro "text" es null, el metodo termina sin hacer nada.</li>
     * <li>Reinicia el estado interno al establecer todos los valores de "counts" en 0, 
     *     "totalChars" en 0 y "normalized" en false.</li>
     * <li>Itera sobre el arreglo "text" y por cada caracter modifica su representacion a mayuscula 
     *     y calcula el indice correspondiente en base al alfabeto (0 para 'A', 1 para 'B', etc.).</li>
     * <li>Aumenta el contador de la letra en "counts" y el total de caracteres procesados ("totalChars"). 
     *     La verificacion asegura que solo se consideren caracteres del alfabeto ingles (A-Z).</li>
     * </ul>
     *
     * @param text Arreglo de caracteres que representa el texto a analizar. 
     *         Solo se consideran letras del alfabeto ingles (case-insensitive).
     */
    public void analyze(char[] text) {
        if (text == null) return;
        Arrays.fill(counts, 0);
        totalChars = 0;
        normalized = false;
        
        for (char c : text) {
            int index = Character.toUpperCase(c) - 'A';
            if (index >= 0 && index < 26) {
                counts[index]++;
                totalChars++;
            }
        }
    }

    /**
     * <p>Metodo que normaliza las frecuencias de las letras procesadas.</p>
     *
     * <br>
     * Funcionamiento Interno:
     * <ul>
     * <li>Verifica si la cantidad total de caracteres procesados ("totalChars") es mayor a 0.</li>
     * <li>Divide cada valor del arreglo "counts" por la cantidad total de caracteres
     *     para obtener la frecuencia relativa de cada letra.</li>
     * <li>Establece la bandera "normalized" a "true" indicando que las frecuencias han sido normalizadas.</li>
     * </ul>
     */
    private void normalize() {
        if (totalChars > 0) {
            for (int i = 0; i < counts.length; i++) {
                counts[i] /= totalChars;
            }
            normalized = true;
        }
    }

    /**
     * <p>Este metodo calcula un puntaje basado en la frecuencia de cada letra del texto procesado
     * comparado con las frecuencias teoricas del ingles.</p>
     * 
     * <ul>
     * <li>Si las frecuencias internas aun no se han normalizado, primero llama al metodo "normalize()".</li>
     * <li>Calcula la diferencia absoluta entre la frecuencia observada de cada letra y las frecuencias teoricas del ingles.</li>
     * <li>Estas diferencias se restan de un "score" inicial, penalizando desviaciones respecto a las frecuencias esperadas.</li>
     * <li>Finalmente, ajusta el resultado a un rango mas util de 0 a 1 con la formula "(score + 1) / 2".</li>
     * </ul>
     *
     * @return Un valor flotante en el rango [0,1] representando cuan similar es la distribucion de caracteres
     * del texto procesado con respecto al ingles.
     */
    public float calculateFrequencyScore() {
        if (!normalized) {
            normalize();
        }
    
        float score = 0;
        for (int i = 0; i < 26; i++) {

            float difference = Math.abs(counts[i] - ENGLISH_FREQUENCIES[i]);
            score -= difference;
        }

        return (score + 1) / 2;
    }

}