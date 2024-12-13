package com.evolvlabs.enigmabackend;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : Santiago Arellano 00328370
 * @Date : December 2nd, 2024
 * @Description : El presente archivo incluye una implementacion directa del funcionamiento interno de un reflector dentro
 * de una maquina enigma. Para registrar las posiciones de los caracteres que tiene que retornar basados en el que ingrese el usuario (u
 * otra parte de los rotores de la maquina) utilizamos un array de enteros representativos de los indices a los que tiene que linkear
 * basado en una configuracion inicial que se coloca dentro del generador de factory.
 * <br><br>
 * El reflector presenta un metodo interno que facilita el retorno en el caso de tener que ir de regreso basado en una clave
 * en un proceso de desencripcion.
 */
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ReflectorImplementation {

    /**
     * Determina las posiciones (indices en un arreglo) de la longitud interna de la cadena ingresada como el orden de los
     * notches
     */
    protected int[] e_forwardWiringBasedOnNotches;

    protected String e_rotorName;
    /**
     * Constructor public cuyo uso no es recomendado para pasar un encoding al reflector basado en un String generador
     * @param externalEncodingSequence: Secuencia de caracteres que representa el reflector
     */
    protected ReflectorImplementation(String externalEncodingSequence, String name){
        this.e_forwardWiringBasedOnNotches = RotorImplementation.transformarConexionesAIndices(externalEncodingSequence);
        this.e_rotorName = name;
    }

    /**
     * Factory method que permite crear una instancia de la clase ReflectorImplementation a partir de un nombre externo
     * @param externalReflectorIdentifierName: Nombre del reflector a usar
     * @return Instancia de la clase ReflectorImplementation
     */
    public static ReflectorImplementation createReflector(String externalReflectorIdentifierName){
        return switch (externalReflectorIdentifierName) {
            case "B" -> new ReflectorImplementation("YRUHQSLDPXNGOKMIEBFZCWVJAT", "B");
            case "C" -> new ReflectorImplementation("FVPJIAOYEDRZXWGCTKUQSBNMHL", "C");
            default -> throw new UnsupportedOperationException();
        };
    }

    /**
     * Metodo que permite obtener la posicion del caracter en la cadena de conexiones del reflector
     * @param externalCharacterIndexZeroBased: Posicion del caracter en la cadena de conexiones
     * @return Posicion del caracter en la cadena de conexiones
     */
    public int getForwardLinkBasedOnCharacter(int externalCharacterIndexZeroBased){
        return this.e_forwardWiringBasedOnNotches[externalCharacterIndexZeroBased];
    }
}
