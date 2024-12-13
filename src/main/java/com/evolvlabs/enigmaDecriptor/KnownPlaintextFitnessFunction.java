package com.evolvlabs.enigmaDecriptor;

/**
 * @Author : Santiago Arellano
 * @Email : sarellanoj@estud.usfq.edu.ec
 * @Date : 10/12/2024
 * @Description : Clase que implementa la interfaz FitnessFunction para calcular la puntuacion de similitud
 *                entre un texto proporcionado y un plaintext conocido.
 */
public class KnownPlaintextFitnessFunction implements FitnessFunction{

    char[] e_plainText;

    /**
     * Constructor que inicializa la funcion con un plaintext proporcionado externamente.
     *
     * @param external_plainText Un arreglo de caracteres que contiene el plaintext conocido.
     */
    public KnownPlaintextFitnessFunction(char[] external_plainText){
        this.e_plainText = external_plainText;
    }

    /**
     * Constructor que genera un plaintext basado en una lista de palabras y sus respectivos desplazamientos.
     *
     * @param words Un arreglo de cadenas de texto representando palabras que se incluiran en el plaintext.
     * @param offsets Un arreglo de enteros representando el desplazamiento de cada palabra dentro del plaintext.
     *                Cada valor indica la posicion inicial donde se insertara la palabra correspondiente.
     */
    public KnownPlaintextFitnessFunction(String[] words, int[] offsets){
        int length = 0;
        for(int i = 0; i < words.length;i++){
            int offset = offsets[i] + words[i].length();
            length = Math.max(offset, length);
        }
        this.e_plainText = new char[length];
    
        for(int i = 0; i < words.length; i++){
            System.arraycopy(words[i].toCharArray(), 0, this.e_plainText, offsets[i], words[i].length());
        }
    }

    /**
     * Metodo que calcula la puntuacion de similitud entre el texto proporcionado y el plaintext conocido.
     *
     * @param text Un arreglo de caracteres que representa el texto a comparar con el plaintext conocido.
     * @return Un valor flotante que indica el total de coincidencias exactas entre los caracteres
     *         del texto proporcionado y el plaintext conocido.
     */
    @Override
    public float score(char[] text) {
        int length = Math.min(this.e_plainText.length, text.length);
        int total = 0;
        for(int i = 0; i < length; i++){
            if (this.e_plainText[i] > 0){
                total += this.e_plainText[i] == text[i] ? 1 : 0;
            }
        }
        return total;
    }
}
