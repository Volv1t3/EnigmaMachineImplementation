package com.evolvlabs.enigmaDecriptor;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10, dec. 2024
 * @Description : This class implements the FitnessFunction interface and provides a combined score
 *                 based on the IoC score and frequency analysis score of a given text.
 */
public class CombinedFrequencyFitnessFunction implements FitnessFunction {
    private final IoCFitnessFunction iocScorer;
    private final FrequencyAnalysis freqAnalyzer;

    /**
     * <b>Constructor de la clase CombinedFrequencyFitnessFunction.</b><br>
     * 
     * Este constructor inicializa dos componentes principales:<br>
     *  - iocScorer: Un evaluador para calcular el puntaje IoC (Indice de Coincidencia) de un texto.<br>
     *  - freqAnalyzer: Una herramienta para analizar la frecuencia de caracteres en un texto.<br>
     * <br>Ambos componentes son esenciales para calcular un puntaje combinado basado en el IoC y el analisis
     * de frecuencias en textos ingresados.
     */
    public CombinedFrequencyFitnessFunction() {
        this.iocScorer = new IoCFitnessFunction();
        this.freqAnalyzer = new FrequencyAnalysis();
    }

    /**
     * Calcula un puntaje para el texto ingresado basandose en una combinacion de dos factores:
     * el puntaje IoC (Indice de Coincidencia) y un analisis de frecuencia de caracteres.
     * 
     * @param text Un arreglo de caracteres que representa el texto a analizar. No debe ser nulo
     *             y debe contener al menos dos caracteres para un analisis valido.
     * 
     * @return Un puntaje flotante calculado como una combinacion ponderada entre el puntaje
     *         IoC y el resultado del analisis de frecuencias. Si el texto ingresado es invalido,
     *         retorna Float.NEGATIVE_INFINITY.
     * <br><br>
     *  El metodo realiza los siguientes pasos internamente:<br>
     *          1. Si el texto es nulo o tiene menos de dos caracteres, se retorna un valor negativo infinito.<br>
     *          2. Se calcula el puntaje IoC utilizando el evaluador IoC.<br>
     *          3. Se analiza la frecuencia de caracteres en el texto y se calcula un puntaje de frecuencia.<br>
     *          4. Se ponderan ambos puntajes tomando en cuenta la longitud del texto, asignando mayor peso<br>
     *             al analisis de frecuencias para textos mas largos.<br>
     */
    @Override
    public float score(char[] text) {
        if (text == null || text.length < 2) {
            return Float.NEGATIVE_INFINITY;
        }

        float iocScore = iocScorer.score(text);

        freqAnalyzer.analyze(text);
        float freqScore = freqAnalyzer.calculateFrequencyScore();

        float lengthWeight = Math.min(1.0f, text.length / 10.0f);
        return (iocScore * (1 - lengthWeight) + freqScore * lengthWeight);
    }
}