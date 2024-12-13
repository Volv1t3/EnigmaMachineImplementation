package com.evolvlabs.enigmaDecriptor;

public class ScoredEnigmaKey extends EnigmaKey implements Comparable<ScoredEnigmaKey>{

    float score;

    
    /**
     * <b>Constructor para la clase ScoredEnigmaKey.</b>
     * <br>
     * <p>Este constructor inicializa un objeto ScoredEnigmaKey con una clave de Enigma y un puntaje asociado.
     * Utiliza el constructor de la clase base EnigmaKey para inicializar la parte heredada y asigna 
     * el valor del puntaje al campo de la clase.</p>
     * 
     * @param key   El objeto EnigmaKey utilizado para inicializar la base de esta clase.
     * @param score El puntaje (de tipo flotante) asociado con esta clave, que es usado 
     *              para realizar comparaciones entre diferentes claves.
     */
    public ScoredEnigmaKey(EnigmaKey key, float score) {
        super(key);
        this.score = score;
    }

    public float getScore() { return this.score; }

    @Override
    public int compareTo(ScoredEnigmaKey o) {
        return Float.compare(this.score, o.score);
    }
}
