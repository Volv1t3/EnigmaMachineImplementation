package com.evolvlabs.enigmabackend;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : Santiago Arellano
 * @Date : December 2, 2024
 * @Description : El presente archivo incluye los principales metodos necesarios para trabajar con un PlugBoard en una
 * maquina enigma. En las maquinas enigma reales, el plug board se utilizaba en el primer y ultimo paso de la encriptacion
 * o desencriptacion para variar aun mas las letras y la codificacion que se obtenia de estas.
 * <br><br>
 * Como se menciona en el documento adjunto para este informe, el ejercito aleman usaba este tipo de equipo para modificar
 * las letras que entraban o salian de la maquina para incrementar la dificultad de descifrado de una clave que generasen. En este sentido,
 * la clase implementa varios metodos que permiten al usuario (en este caso a traves de la interfaz visual) ingresar pares ordenados
 * separados por espacios (esto se puede manejar con la interfaz o sin implementarla si es necesario).
 */
public class PlugBoardImplementation {

    /**
     * Variable en la cual se guardan las configuraciones dentro
     */
    protected int[] e_forwardWiringBasedOnNotches;

    /**
     * Metodo que permite al usuario ingresar un string con pares ordenados de letras separados por espacios
     * y devuelve un array de enteros que representa las conexiones de las letras en el plug board
     * @param keyCharacterPairs: String Representativa de los pares. 
     */
    public PlugBoardImplementation(String keyCharacterPairs){
        this.e_forwardWiringBasedOnNotches = decodePlugBoard(keyCharacterPairs);
    }

    /**
     * Retrieves the forward wiring based on the character's zero-based index.
     * @param characterInputZeroBased the zero-based index of the character for which forward wiring is needed
     * @return the mapped integer value of the wiring configuration at the specified index
     */
    public int getForwardWiringBasedOnCharacter(int characterInputZeroBased){
        return this.e_forwardWiringBasedOnNotches[characterInputZeroBased];
    }

    
    /**
     * Genera una configuracion de enchufes de identidad donde cada letra esta mapeada a si misma.
     * @return un array de enteros que representa una matriz de identidad para el plug board
     */
    protected static int[] identityPlugBoard(){
        int[] mappingForIdentityMatrix = new int[26];
        for(int i = 0; i <26; i++){
            mappingForIdentityMatrix[i] = i;
        }
        return mappingForIdentityMatrix;
    }


    /**
     * Metodo para obtener el conjunto de caracteres que no estan conectados en el plugboard.
     * Este metodo toma una representacion del plugboard como una cadena y devuelve un conjunto de enteros
     * que representan los caracteres que no se han utilizado en ningun par de conexion.
     *
     * @param externalPlugBoard la configuracion externa del plugboard como cadena, que contiene pares de letras conectadas
     * @return un conjunto de enteros que representa los indices de los caracteres que no tienen conexion
     *
     * El metodo inicializa un conjunto con todos los indices del alfabeto (0 a 25), luego procesa cada par de letras
     * en la configuracion del plugboard para remover del conjunto aquellos caracteres que estan conectados.
     * La cadena de entrada se divide en pares utilizando caracteres no alfabeticos como delimitadores.
     */
    public static Set<Integer> obtenerCaracteresNoConectados(String externalPlugBoard) {
        Set<Integer> caracteresNoConectados = new HashSet<>();
        for (int i = 0; i < 26; i++) {
            caracteresNoConectados.add(i);
        }
        if (externalPlugBoard.isEmpty()) {
            return caracteresNoConectados;
        }
        String[] pares = externalPlugBoard.split("[^a-zA-Z]");
        for (String par : pares) {
            int caracter1 = par.charAt(0) - 65;
            int caracter2 = par.charAt(1) - 65;

            caracteresNoConectados.remove(caracter1);
            caracteresNoConectados.remove(caracter2);
        }

        return caracteresNoConectados;
    }

    public static int[] decodePlugBoard(String plugboard) {
        if (plugboard == null || plugboard.isEmpty()) {
            return identityPlugBoard();
        }

        String[] pairings = plugboard.split("[^a-zA-Z]");
        Set<Integer> pluggedCharacters = new HashSet<>();
        int[] mapping = identityPlugBoard();

        // Validate and create mapping
        for (String pair : pairings) {
            if (pair.length() != 2)
                return identityPlugBoard();

            int c1 = pair.charAt(0) - 65;
            int c2 = pair.charAt(1) - 65;

            if (pluggedCharacters.contains(c1) || pluggedCharacters.contains(c2)) {
                return identityPlugBoard();
            }

            pluggedCharacters.add(c1);
            pluggedCharacters.add(c2);

            mapping[c1] = c2;
            mapping[c2] = c1;
        }

        return mapping;
    }


}
