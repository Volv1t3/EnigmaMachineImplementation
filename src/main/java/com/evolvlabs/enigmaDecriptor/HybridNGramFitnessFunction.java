package com.evolvlabs.enigmaDecriptor;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : Clase que implementa la interfaz FitnessFunction para evaluar la calidad de un texto
 *                basado en patrones linguisticos n-gram.
 */
public class HybridNGramFitnessFunction implements FitnessFunction {
   
    private final QuagramFitnessFunction quadgrams;
    private final TrigramFitnessFunction trigrams;
    
    
    // Arreglo de trigramas comunes en ingles que reciben peso adicional en la puntuacion.
    // Estos trigramas ayudan a identificar patrones importantes o estructuras en el texto de entrada, mejorando la precision.
    private static final String[] IMPORTANT_TRIGRAMS = {
            "THE", "AND", "ING", "ION", "ENT", "FOR", "HER",
            "THA", "HIS", "ALL", "WIT", "ARE", "WAS", "ONE",
            "YOU", "OUT", "IGH", "UMP" 
    };

    /**
     * <b>Constructor de la clase HybridNGramFitnessFunction.</b>
     * <p>Inicializa las funciones de evaluacion de quadgramas y trigramas
     * utilizadas para calcular la puntuacion de un texto basado en patrones
     * linguisticos n-gram. </p>
     * 
     * <p>Los quadgramas proporcionan una evaluacion sobre secuencias de cuatro caracteres,
     * mientras que los trigramas trabajan con secuencias de tres caracteres.
     * Estos componentes se combinan para realizar un analisis mas robusto
     * del texto de entrada.</p>
     */
    public HybridNGramFitnessFunction() {
        this.quadgrams = new QuagramFitnessFunction();
        this.trigrams = new TrigramFitnessFunction();
    }

    /**
     * Evalua un texto representado como un arreglo de caracteres y calcula una puntuacion
     * basada en diferentes patrones linguisticos. Este metodo utiliza las funciones de
     * evaluacion de quadgramas, trigramas y patrones adicionales predefinidos para asignar
     * una puntuacion final al texto.
     *
     * @param text Un arreglo de caracteres que representa el texto a evaluar.
     *             Si el texto es nulo o tiene menos de 3 caracteres, el metodo devuelve
     *             Float.NEGATIVE_INFINITY.
     * @return La puntuacion calculada del texto basado en la combinacion de los
     *         puntajes de quadgramas, trigramas y patrones importantes.
     */
    @Override
    public float score(char[] text) {
        if (text == null || text.length < 3) {
            return Float.NEGATIVE_INFINITY;
        }
        
        //! Calculamos los pesos por tipo de prueba y el metodo propio patternScore
        float quadScore = text.length >= 4 ? quadgrams.score(text) : 0;
        float triScore = trigrams.score(text);
        float patternScore = calculatePatternScore(text);
        
        //! Retornamos la combinacion de los pesos de cada analisis y el texto
        return combineScores(quadScore, triScore, patternScore, text);
    }

    /**
     * <p>Calcula un puntaje basado en patrones linguisticos dentro del texto de entrada.<br>
     * Este metodo analiza la presencia de trigramas importantes, separacion de palabras
     * y sufijos comunes para asignar un puntaje adicional a las coincidencias encontradas.</p>
     *
     * @param text Un arreglo de caracteres que representa el texto a analizar.
     *             El texto se convierte a mayusculas para realizar comparaciones
     *             insensibles a minusculas/mayusculas.
     * @return Un valor flotante que representa el puntaje calculado basado
     *         en los patrones detectados en el texto.
     */
    private float calculatePatternScore(char[] text) {
        String upperText = new String(text).toUpperCase();
        float score = 0;
        
        //! Anadimos un peso adicional si encontramos un trigrama importante
        for (String trigram : IMPORTANT_TRIGRAMS) {
            if (upperText.contains(trigram)) {
                score += 2.0f; 
            }
        }
    
        //! Evaluamos la presencia de frases en un estilo camel case ej: HelloWorld, etc. Para dar un peso adicional
        for (int i = 1; i < text.length; i++) {
            if (Character.isUpperCase(text[i]) && Character.isLowerCase(text[i-1])) {
                score += 1.5f; 
            }
        }
    
        //! Buscamos terminaciones inglesas en la traduccion general del codigo, esto nos puede indicar que existen 
        //! palabras completas dentro del texto
        if (upperText.contains("ING") || upperText.contains("ED") ||
                upperText.contains("ES") || upperText.contains("S")) {
            score += 1.0f;
        }
    
        return score;
    }

    /**
     * Calcula una combinacion ponderada de las puntuaciones de quadgramas, trigramas y patrones
     * encontrados en el texto, y ajusta la puntuacion final basandose en la longitud del texto.
     *
     * @param quadScore La puntuacion obtenida a partir del analisis de quadgramas en el texto.
     * @param triScore La puntuacion obtenida del analisis de trigramas en el texto.
     * @param patternScore La puntuacion calculada a partir de patrones linguisticos adicionales en el texto.
     * @param text Un arreglo de caracteres que representa el texto analizado para calcular su puntuacion.
     * @return El puntaje final calculado combinando los diferentes puntajes y considerando la longitud del texto.
     */
    private float combineScores(float quadScore, float triScore, float patternScore, char[] text) {
        float finalScore;
        int length = text.length;
    
        if (length < 4) {
            finalScore = triScore * 0.8f + patternScore * 0.2f;
        } else if (length < 8) {
            finalScore = quadScore * 0.4f + triScore * 0.4f + patternScore * 0.2f;
        } else {
            finalScore = quadScore * 0.6f + triScore * 0.25f + patternScore * 0.15f;
        }

        float lengthMultiplier = Math.min(1.0f + (length / 20.0f), 2.0f);
        return finalScore * lengthMultiplier;
    }

}