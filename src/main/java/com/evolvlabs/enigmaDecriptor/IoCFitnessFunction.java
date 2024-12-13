package com.evolvlabs.enigmaDecriptor;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : This class implements the FitnessFunction interface to evaluate the fitness of a given text
 *                based on the Index of Coincidence (IoC) of the text. The IoC is a measure of the randomness
 *                of a text, where a higher value indicates a higher level of randomness. The fitness score
 *                is calculated based on the closeness of the IoC to the expected IoC of an English text.
 *                The score is adjusted for the length of the text to prevent overfitting.
 */
public class IoCFitnessFunction implements FitnessFunction {
    private static final float ENGLISH_IOC = 0.0667f;
    private static final float IOC_TOLERANCE = 0.015f;

    
    public IoCFitnessFunction() {}

    /**
     * Calcula el puntaje de fitness para un texto basado en el Indice de Coincidencia (IoC).
     * Este metodo evalua la cercania del IoC del texto proporcionado al IoC esperado de un texto en ingles.
     * 
     * @param text Arreglo de caracteres que representa el texto a evaluar. Si el texto es nulo o tiene
     *             menos de 2 caracteres validos, el metodo retorna Float.NEGATIVE_INFINITY.
     * 
     * @return Un valor flotante que indica el puntaje de fitness del texto. Valores mas altos indican
     *         mayor similitud con la distribucion esperada de un texto en ingles, mientras que valores
     *         negativos indican desviaciones significativas o textos no validos.
     * <br><br>
     * Funcionamiento interno:<br>
     * - Primero, calcula un histograma de los caracteres validos ('A'-'Z') en el texto, ignorando<br>
     *   caracteres que no sean letras.<br>
     * - Luego, calcula el IoC usando las frecuencias de los caracteres y un formula estadistica.<br>
     * - Finalmente, evalua la desviacion del IoC calculado con respecto al IoC esperado de un texto<br>
     *   en ingles (0.0667), ajustando el puntaje de acuerdo a la cercania y la longitud del texto.<br>
     */
    @Override
    public float score(char[] text) {
        if (text == null || text.length < 2) {
            return Float.NEGATIVE_INFINITY;
        }

        int[] histogram = new int[26];
        int validChars = 0;

        for (char c : text) {
            int index = Character.toUpperCase(c) - 'A';
            if (index >= 0 && index < 26) {
                histogram[index]++;
                validChars++;
            }
        }

        if (validChars < 2) {
            return Float.NEGATIVE_INFINITY;
        }

        // Calculate IoC
        float total = 0.0f;
        for (int count : histogram) {
            total += count * (count - 1);
        }
        float ioc = total / (validChars * (validChars - 1));

        // Score based on closeness to English IoC
        float deviation = Math.abs(ioc - ENGLISH_IOC);
        float score;

        if (deviation <= IOC_TOLERANCE) {
            // Higher score for closer match to English IoC
            score = 1.0f - (deviation / IOC_TOLERANCE);
        } else {
            // Penalty for significant deviation
            score = -deviation * 2;
        }

        // Length-based adjustment
        float lengthBonus = 1.0f + Math.min(0.5f, validChars/20.0f);

        return score * lengthBonus;
    }

}
