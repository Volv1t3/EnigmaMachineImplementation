package com.evolvlabs.enigmabackend;

import com.evolvlabs.enigmaDecriptor.EnigmaKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : Santiago Arellano
 * @Date: December 2nd, 2024
 * @Description: El presente archivo incluye una implementacion directa de la maquina de enigma con varios metodos para probar
 * tanto las maquinas como las entradas que se pueden dar . En especifico, la maquina permite utilizar una entrada de otra
 * maquina para poder copiar configuraciones rapidas, ademas permite realizar encriptados y desencriptados con la misma funcion. Como
 * la maquina enigma es simetrica si conocemos todas las configuraciones originales podemos encriptar y desencriptar un texto
 * hacia la cadena original. Para esto trabajamos con arreglos de caracteres, caracteres o indices en el rango de[0, 26]
 * para representar letras del abecedario.
 */
@Setter(value = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
public class EnigmaMachineImplementation {

    /**
     * Instancia interna usada para representar el rotor izquierdo de la maquina (aquel que rota mas lento)
     */
    private RotorImplementation e_rotorIzquierdo;
    /**
     * Instancia interna usada para representar el rotor medio de la maquina (aquel que gira medio)
     */
    private RotorImplementation e_rotorMedio;
    /**
     * Instancia interna usada para representar el rotor derecho de la maquina (aquel que gira rapido)
     */
    private RotorImplementation e_rotorDerecho;
    /**
     * Instancia interna usada para representar el reflector que usa la maquina (B o C)
     */
    private ReflectorImplementation e_reflectorInterno;
    /**
     * Instancia interna usada para representar el Plug board (si se usa) de la maquina
     */
    private PlugBoardImplementation e_plugBoardInterno;

    //! Constructores
    /**
     * Constructor de la maquina de enigma, recibe los 5 parametros para inicializar las instancias internas
     * @param rotorIzquierdo Rotor izquierdo de la maquina
     * @param rotorMedio Rotor medio de la maquina
     * @param rotorDerecho Rotor derecho de la maquina
     * @param reflector Reflector de la maquina
     * @param plugBoard Plug board de la maquina
     */
    public EnigmaMachineImplementation(RotorImplementation rotorIzquierdo, RotorImplementation rotorMedio, RotorImplementation rotorDerecho, ReflectorImplementation reflector, PlugBoardImplementation plugBoard){
        this.setE_rotorIzquierdo(rotorIzquierdo);
        this.setE_rotorMedio(rotorMedio);
        this.setE_rotorDerecho(rotorDerecho);
        this.setE_reflectorInterno(reflector);
        this.setE_plugBoardInterno(plugBoard);
    }

    /**
     * Constructor de copia para la maquina Enigma.
     * Este constructor crea una nueva instancia de EnigmaMachineImplementation
     * copiando las configuraciones de otra instancia existente.
     *
     * @param anotherEnigmaMachine Otra instancia de EnigmaMachineImplementation
     *                             de la cual se copiaran las configuraciones internas.
     */
    public EnigmaMachineImplementation(EnigmaMachineImplementation anotherEnigmaMachine){
        this.setE_rotorIzquierdo(anotherEnigmaMachine.getE_rotorIzquierdo());
        this.setE_rotorMedio(anotherEnigmaMachine.getE_rotorMedio());
        this.setE_rotorDerecho(anotherEnigmaMachine.getE_rotorDerecho());
        this.setE_reflectorInterno(anotherEnigmaMachine.getE_reflectorInterno());
        this.setE_plugBoardInterno(anotherEnigmaMachine.getE_plugBoardInterno());
    }

    /**
     * Constructor de la maquina de enigma, recibe los 5 parametros para inicializar las instancias internas de todas las variables
     * en una sola instanciacion.
     * @param externalRotorConfigurations: Arreglo de strings con las configuraciones de los rotores
     * @param externalReflectorConfiguration: String con la configuracion del reflector
     * @param externalInitialRotorPositions: Arreglo de enteros con las posiciones iniciales de los rotores
     * @param externalInitialRingPositions: Arreglo de enteros con las configuraciones de los anillos de los rotores
     * @param externalPlugBoardConfiguration: String con la configuracion del plug board
     */
    public EnigmaMachineImplementation(String[] externalRotorConfigurations, String externalReflectorConfiguration,
                                       int[] externalInitialRotorPositions, int[] externalInitialRingPositions,
                                       String externalPlugBoardConfiguration)
    {
        this.setE_rotorIzquierdo(RotorImplementation.createRotor(externalRotorConfigurations[0],
                externalInitialRotorPositions[0],externalInitialRingPositions[0]));
        this.setE_rotorMedio(RotorImplementation.createRotor(externalRotorConfigurations[1],
                externalInitialRotorPositions[1],externalInitialRingPositions[1]));
        this.setE_rotorDerecho(RotorImplementation.createRotor(externalRotorConfigurations[2],
                externalInitialRotorPositions[2],externalInitialRingPositions[2]));

        //! Create Reflectors
        this.setE_reflectorInterno(ReflectorImplementation.createReflector(externalReflectorConfiguration));

        //! Pass in the plugboard
        this.setE_plugBoardInterno(new PlugBoardImplementation(externalPlugBoardConfiguration));
    }

    public EnigmaMachineImplementation() {

    }

    public EnigmaMachineImplementation(EnigmaKey newKey) {
        this.setE_rotorIzquierdo(RotorImplementation.createRotor(newKey.rotors[0],
                newKey.indicators[0],newKey.rings[0]));
        this.setE_rotorMedio(RotorImplementation.createRotor(newKey.rotors[1],
                newKey.indicators[1],newKey.rings[1]));
        this.setE_rotorDerecho(RotorImplementation.createRotor(newKey.rotors[2],
                newKey.indicators[2],newKey.rings[2]));

        //! Create Reflectors
        this.setE_reflectorInterno(ReflectorImplementation.createReflector("B"));

        //! Pass in the plugboard
        this.setE_plugBoardInterno(new PlugBoardImplementation(newKey.plugboard));
    }


    /**
     * Realiza la rotacion requerida de los rotores internos de la maquina Enigma.
     * Este metodo simula el paso doble, verificando si los rotores de la maquina necesitan girar,
     * especificamente si el rotor medio o el derecho están en la posicion de muesca.<br><br>
     * - Si el rotor medio esta en la muesca, se activa el giro del rotor medio e izquierdo.<br>
     * - Si solo el rotor derecho esta en la muesca, se activa el giro del rotor medio.<br>
     * - Independientemente de las posiciones de los otros rotores, el rotor derecho siempre girara.<br>
     */
    public void rotacionRequeridaPorRotoresInternos(){
        //! Simulacion de double stepping si la seccion interna de la maquina enigma tiene que rotar
        if (this.e_rotorMedio.isANotch()){
            this.e_rotorMedio.turnover();
            this.e_rotorIzquierdo.turnover();
        }
        //! Revision si el rotor derecho es el que necesita rotacion
        else if (this.e_rotorDerecho.isANotch()) {
            this.e_rotorMedio.turnover();
        }
        //! Realizamos una rotacion al rotor derecho
        this.e_rotorDerecho.turnover();
    }

    //! Implementaciones para el encriptado de cadenas basadas en la configuracion de la maquina enigma



    /**
     * Este metodo realiza el encriptado de un caracter basado en su indice dentro del alfabeto.
     * Toma un entero que representa la posicion del caracter en base cero y retorna el
     * caracter encriptado resultante. Internamente llama a encriptadoDeCaracterHelper para
     * manejar el flujo a traves de los rotores y el reflector, implementando la logica completa
     * de la maquina Enigma.
     *
     * @param externalCharacterMarkerZeroBased posicion del caracter en el alfabeto (0-25)
     * @return el caracter encriptado resultante
     */
    public char encriptadoDeCaracter(int externalCharacterMarkerZeroBased){
        int encriptadoDeCaracterHelper = this.encriptadoDeCaracterHelper(externalCharacterMarkerZeroBased);
        return (char) (encriptadoDeCaracterHelper + 65);
    }

    /**
     * Este metodo realiza el encriptado de un arreglo de caracteres basado en su indice dentro del alfabeto.
     * Toma un arreglo de enteros que representan las posiciones de los caracteres en base cero y retorna un
     * arreglo de caracteres encriptados resultantes. Internamente llama a encriptadoDeCaracter para cada
     * caracter en el arreglo, implementando la logica completa de la maquina Enigma.
     *
     * @param externalCharacterMarkersZeroBased arreglo de enteros que representan las posiciones de los caracteres en base cero
     * @return arreglo de caracteres encriptados resultantes
     */
    public char[] encriptadoDeCaracter(int[] externalCharacterMarkersZeroBased){
        char[] retornoDeEncriptado = new char[externalCharacterMarkersZeroBased.length];
        for (int i = 0; i < externalCharacterMarkersZeroBased.length; i++){
            retornoDeEncriptado[i] = this.encriptadoDeCaracter(externalCharacterMarkersZeroBased[i]);
        }
        return retornoDeEncriptado;
    }

    /**
     * Este metodo realiza el encriptado de un arreglo de caracteres basado en su indice dentro del alfabeto.
     * Toma un arreglo de caracteres que representan las posiciones de los caracteres en base cero y retorna un
     * arreglo de caracteres encriptados resultantes. Internamente llama a encriptadoDeCaracter para cada
     * caracter en el arreglo, implementando la logica completa de la maquina Enigma.
     *
     * @param externalCharactersMarkerZeroBased arreglo de caracteres que representan las posiciones de los caracteres en base cero
     * @return arreglo de caracteres encriptados resultantes
     */
    public char[] encriptadodeCaracter(char[] externalCharactersMarkerZeroBased){
        int[] retornoDeEncriptado = new int[externalCharactersMarkerZeroBased.length];
        for (int i = 0; i < externalCharactersMarkerZeroBased.length; i++){
            if (!Character.isWhitespace(externalCharactersMarkerZeroBased[i])) {
                retornoDeEncriptado[i] = this.encriptadoDeCaracterHelper(externalCharactersMarkerZeroBased[i] - 65);
            }
            else {
                retornoDeEncriptado[i] = -1;
            }
        }
        char[] retornoDeEncriptadoChar = new char[retornoDeEncriptado.length];
        for (int i = 0; i < retornoDeEncriptado.length; i++){
            if (retornoDeEncriptado[i] != -1) {
                retornoDeEncriptadoChar[i] = (char) (retornoDeEncriptado[i] + 65);
            }
            else {
                retornoDeEncriptadoChar[i] = ' ';
            }
        }
        return retornoDeEncriptadoChar;
    }

    private int encriptadoDeCaracterHelper(int externalCharacterMarkerZerobased) {

        //! Realizamos la rotacion antes de movernos en el encriptado
        this.rotacionRequeridaPorRotoresInternos();


        /*
         * Cuando tenemos un caracter, primero debemos de asegurarnos que este entre 0 y 25 para tener
         * las invariantes del lenguaje.
         */
        if (externalCharacterMarkerZerobased < 0 || externalCharacterMarkerZerobased > 25) {
            throw new IllegalStateException("The integer value passed for character is incorrect");
        }


        //! Revision de la Plugboard
        var plugBoardVariation = this.e_plugBoardInterno
                .getForwardWiringBasedOnCharacter(externalCharacterMarkerZerobased);

        //! Realizamos la propagacion hacia el frente
        int conversionFromRealToRightRotor = this.e_rotorDerecho
                .forward(plugBoardVariation);
        int conversionFromRightToMiddleRotor = this.e_rotorMedio
                .forward(conversionFromRealToRightRotor);
        int conversionFromMiddleToLeftRotor = this.e_rotorIzquierdo
                .forward(conversionFromRightToMiddleRotor);

        //! Reflexion del valor encontrado
        int reflectedRightToLeftEncryption = this.e_reflectorInterno
                .getForwardLinkBasedOnCharacter(conversionFromMiddleToLeftRotor);
        //! Retorno por los caminos de los rotores
        int backtrackFromLeftToMiddleRotor = this.e_rotorIzquierdo
                .backward(reflectedRightToLeftEncryption);
        int backtrackFromMiddleToRightRotor = this.e_rotorMedio
                .backward(backtrackFromLeftToMiddleRotor);
        int backtrackFromLeftToRealValue = this.e_rotorDerecho
                .backward(backtrackFromMiddleToRightRotor);
        //! Aplicamos plugboard (si existe)
        return this.e_plugBoardInterno
                .getForwardWiringBasedOnCharacter(backtrackFromLeftToRealValue);
    }

}
