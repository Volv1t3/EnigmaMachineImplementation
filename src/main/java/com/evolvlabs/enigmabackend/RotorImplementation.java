package com.evolvlabs.enigmabackend;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author : Santiago Arellano 00328370
 * @Date : December 2nd, 2024
 * @Description : El presente archivo incluye una implementacion directa del funcionamiento interno de un rotor dentro
 * de una maquina enigma. Para registrar las posiciones de los caracteres que tiene que retornar basados en el que ingrese el usuario (u
 * otra parte de los rotores de la maquina) utilizamos un array de enteros representativos de los indices a los que tiene que linkear
 * basado en una configuracion inicial que se coloca dentro del generador de factory.
 * <br><br>
 * El rotor presenta un metodo interno que facilita el retorno en el caso de tener que ir de regreso basado en una clave
 * en un proceso de desencripcion.
 */
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class RotorImplementation {

    //! Implementation Variables
    //! Setters y Getters
    /**
     * Determina el tipo de rotor (usaremos I, II, III, IV o V)
     */
    @Getter
    protected String e_rotorName;
    /**
     * Determina las posiciones (indices en un arreglo) de la longitud interna de la cadena ingresada como el orden de los
     * notches
     */
    protected int[] e_forwardWiringBasedOnNotches;
    /**
     * Determina las posiciones (indices en un arreglo) inversos a los que tiene el forward notches
     */
    protected int[] e_backwardWiringInverseOfForward;

    /**
     * Variable entera encargada de almacenar las posiciones del rotor durante ejecucion.
     */
    @Getter
    protected int e_rotorPosition;
    /**
     * Variable entera encargada de almacenar la posicion del notch (letra 
     */
    @Getter
    protected int e_notchPosition;
    /**
     * Variable entera encargada de almacenar la configuracion del Rotor con respecto
     */
    @Getter
    protected int e_RingSetting;

    /**
     * Constante estatica usada para delimitar dentro de esta y otras clases el total de letras del alfabeto
     */
    public static final int MAX_ALPHABET_SIZE = 26;

    //! Metodos internos

    /**
     * Metodo encargado de realizar la configuracion inicial del rotor de la maquina enigma. Este metodo incluye configuraciones
     * para los parametros esenciales como el nombre del rotor usado, el encoding externo utilizado es decir las letras y sus posiciones
     * en la las muescas del rotor seleccionado en el anterior paramentro.
     * <br><br>
     * Rotor position permite al usuario modificar la forma en la que el rotor esta configurado como la primera muesca en la
     * que se encuentra el rotor, si bien tiene marcas de alineacion (como en la enigma original), las posiciones de los
     * rotores pueden ser cambiados para obtener una variacion a las condiciones iniciales como 'Q'E'V' en el caso de usar
     * los rotores I, II, III.
     * <br><br>
     * El metodo internamente recopila todos estos parametros y los asigna a la instancia de la clase que se este generando,
     * este constructor no es visible al usuario, estas instancias se manejan estaticamente a traves de un factory method
     * @param externalName: Nombre del rotor usado
     * @param externalEncoding: Encoding externo usado
     * @param externalRotorPosition: Posicion del rotor
     * @param externalnotchPosition: Posicion del notch
     */
    protected  RotorImplementation(String externalName, String externalEncoding,
                                int externalRotorPosition, int externalnotchPosition, int externalRingSetting) {
        this.e_rotorName = externalName;
        this.e_forwardWiringBasedOnNotches = transformarConexionesAIndices(externalEncoding);
        this.e_backwardWiringInverseOfForward = revertirConexionesParaReturn(this.e_forwardWiringBasedOnNotches);
        this.e_rotorPosition = externalRotorPosition;
        this.e_notchPosition = externalnotchPosition;
        this.e_RingSetting = externalRingSetting;
    }

    /**
     * Metodo factory estatico que permite al usuario generar una instancia de la clase RotorImplementation, sin tener que
     * llamar al constructor. Este metodo toma los parametros externos visibles para el usuario como son el rotor a usar,
     * la configuracion externa de la posicion inicial del rotor, y el desplazamiento o ring setting.
     * @param externalRotorName: Nombre del Rotor Seleccionado para Usar
     * @param externalRotorPosition: Valor entero representativo de la posicion inicial del rotor [0,26]
     * @param externalRingSetting: Valor entero representativo del ring setting del rotor [0,26]
     * @return Instancia de la clase RotorImplementation
     */
    public static RotorImplementation createRotor(String externalRotorName, int externalRotorPosition, int externalRingSetting) {

        //! Para la implementacion de este factory method trabajamos con un switch del nombre externo del rotor
        switch (externalRotorName) {
            case "I" /*Modelo I del rotor*/: {
                return new RotorImplementation("I",
                        "EKMFLGDQVZNTOWYHXUSPAIBRCJ",
                        externalRotorPosition,
                        16, externalRingSetting);
            }
            case "II": {
                return new RotorImplementation("II",
                        "AJDKSIRUXBLHWTMCQGZNPYFVOE",
                        externalRotorPosition,
                        4, externalRingSetting);
            }
            case "III": {
                return new RotorImplementation("III", "BDFHJLCPRTXVZNYEIWGAKMUSQO",
                        externalRotorPosition, 21, externalRingSetting);
            }
            case "IV": {
                return new RotorImplementation("IV", "ESOVPZJAYQUIRHXLNFTGKDCMWB", externalRotorPosition
                        , 9, externalRingSetting);
            }
            case "V": {
                return new RotorImplementation("V", "VZBRGITYUPSDNHLXAWMJQOFECK",
                        externalRotorPosition, 25, externalRingSetting);
            }
            case "VI": {
                return new RotorImplementation("VI", "JPGVOUMFYQBENHZRDKASXLICTW",
                        externalRotorPosition, 0, externalRingSetting){
                    @Override
                    public boolean isANotch() {
                        return this.e_rotorPosition == 12 || this.e_rotorPosition == 25;
                    }
                };
            }
            case "VII": {
                return new RotorImplementation("VII", "NZJHGRCXMYSWBOUFAIVLPEKQDT",
                        externalRotorPosition, 0, externalRingSetting) {
                    @Override
                    public boolean isANotch() {
                        return this.e_rotorPosition == 12 || this.e_rotorPosition == 25;
                    }
                };
            }
            case "VIII": {
                return new RotorImplementation("VIII" ,"FKQHTLXOCBJSPDZRAMEWNIUYGV",
                        externalRotorPosition, 0, externalRingSetting){
                    @Override
                    public boolean isANotch() {
                        return this.e_rotorPosition == 12 || this.e_rotorPosition == 25;
                    }
                };
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }


    //! Metodos para transformar una string de la secuencia de caracteres en una rueda (rotor) a un arreglo
    protected static int[] transformarConexionesAIndices(String externalEncoding){
        char[] conversionTemporalStringACharacters = externalEncoding.toCharArray();
        int[] retornoStringArrayAIntegerArray = new int[externalEncoding.length()];

        for(int i = 0; i < externalEncoding.length(); i++){
            retornoStringArrayAIntegerArray[i] = conversionTemporalStringACharacters[i] - 65;
        }
        return retornoStringArrayAIntegerArray;
    }

    protected  int[] revertirConexionesParaReturn(int[] externalCharToIntConections){
        int[] retornoCharToArrayReversed = new int[externalCharToIntConections.length];
        for(int i = 0; i < externalCharToIntConections.length; i++){
            int forwardLink = externalCharToIntConections[i];
            retornoCharToArrayReversed[forwardLink] = i;
        }
        return retornoCharToArrayReversed;
    }

    protected static int cifradoDeUnaClaveConociendoSuPosicion(int externalCharacterZeroBased, int externalRotorPosition,
                                                               int externalRingConfiguration, int[] externalForwardMapping){
        // Calculate shift considering ring setting
        int shift = (externalRotorPosition - externalRingConfiguration + 26) % 26;

        // Apply shift, perform substitution, and reverse shift
        int shifted = (externalCharacterZeroBased + shift + 26) % 26;
        int mapped = externalForwardMapping[shifted];
        return (mapped - shift + 26) % 26;
    }
    public int forward(int externalCharacterZeroBased){
        return cifradoDeUnaClaveConociendoSuPosicion(externalCharacterZeroBased, this.e_rotorPosition,
                this.e_RingSetting, this.e_forwardWiringBasedOnNotches);
    }
    public int backward(int externalCharacterZeroBased){
        return cifradoDeUnaClaveConociendoSuPosicion(externalCharacterZeroBased, this.e_rotorPosition,
                this.e_RingSetting, this.e_backwardWiringInverseOfForward);
    }

    public void turnover(){
        this.e_rotorPosition = (this.e_rotorPosition + 1) % 26;
    }
    public boolean isANotch(){
        return this.e_notchPosition == this.e_rotorPosition;
    }
}
