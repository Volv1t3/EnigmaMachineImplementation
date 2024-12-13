package com.evolvlabs.enigmaDecriptor;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : Clase que implementa la interfaz FitnessFunction para evaluar el puntaje de aptitud de un texto
 * basado en la frecuencia relativa de letras en el texto.
 */
public class ImprovedNGram implements FitnessFunction {
    private final SinglegramFitnessFunction singlegrams;
    private final BigramFitnessFunction bigrams;
    private final TrigramFitnessFunction trigrams;
    private final QuagramFitnessFunction quadgrams;

    /**
     * <b>Constructor de la clase ImprovedNGram.</b>
     * <p>Este metodo inicializa las instancias de las funciones de aptitud para gramaticas de diferentes tamanos
     * (mono-gramas, bi-gramas, tri-gramas y quad-gramas), las cuales son utilizadas posteriormente
     * en la evaluacion de textos dentro de la implementacion de la interfaz FitnessFunction.</p>
     *
     * <p>Funcionamiento interno:</p>
     * <ul>
     *   <li>Se crea una nueva instancia de SinglegramFitnessFunction para evaluar el puntaje basado en letras individuales.</li>
     *   <li>Se crea una nueva instancia de BigramFitnessFunction para evaluar el puntaje basado en pares de letras consecutivas.</li>
     *   <li>Se crea una nueva instancia de TrigramFitnessFunction para evaluar el puntaje basado en tres letras consecutivas.</li>
     *   <li>Se crea una nueva instancia de QuagramFitnessFunction para evaluar el puntaje basado en cuatro letras consecutivas.</li>
     * </ul>
     */
    public ImprovedNGram() {
        this.singlegrams = new SinglegramFitnessFunction();
        this.bigrams = new BigramFitnessFunction();
        this.trigrams = new TrigramFitnessFunction();
        this.quadgrams = new QuagramFitnessFunction();
    }

    /**
     * <p>Calcula un puntaje de aptitud para un texto dado basado en la frecuencia relativa de gramaticas de diferentes tamanos
     * (mono-gramas, bi-gramas, tri-gramas y quad-gramas). El metodo pondera las contribuciones de cada tamano de gramatica
     * dependiendo de la longitud del texto.</p>
     * <br>
     * @param text Un arreglo de caracteres que representa el texto que sera evaluado.
     * @return Un valor flotante que representa el puntaje de aptitud normalizado por la longitud del texto.
     * <br>
     * El metodo tiene las siguientes reglas:<br>
     * - Si la longitud del texto es menor a 4 caracteres, excluye los quad-gramas.<br>
     * - Si la longitud del texto esta entre 4 y 5 caracteres, incluye un peso menor para los quad-gramas.<br>
     * - Si la longitud del texto esta entre 6 y 10 caracteres, distribuye los pesos de manera uniforme en las gramatica disponibles.<br>
     * - Para textos mayores a 10 caracteres, otorga mayor peso a los quad-gramas.<br>
     */
    @Override
    public float score(char[] text) {
        float length = text.length;
        if (length < 4) {
            return (singlegrams.score(text) * 0.5f +
                    bigrams.score(text) * 0.3f +
                    trigrams.score(text) * 0.2f) / length;
        }
        else if (length <= 5) {
            return (singlegrams.score(text) * 0.4f +
                    bigrams.score(text) * 0.3f +
                    trigrams.score(text) * 0.2f +
                    quadgrams.score(text) * 0.1f) / length;
        }
        else if (length <= 10) {
            return (singlegrams.score(text) * 0.2f +
                    bigrams.score(text) * 0.3f +
                    trigrams.score(text) * 0.3f +
                    quadgrams.score(text) * 0.2f) / length;
        }
        else {
            return (singlegrams.score(text) * 0.1f +
                    bigrams.score(text) * 0.2f +
                    trigrams.score(text) * 0.3f +
                    quadgrams.score(text) * 0.4f) / length;
        }
    }
}