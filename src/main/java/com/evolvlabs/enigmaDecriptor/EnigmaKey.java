package com.evolvlabs.enigmaDecriptor;

import java.util.Arrays;

public class EnigmaKey {
    public String[] rotors;
    public int[] indicators;
    public int[] rings;
    public String reflector;
    public String plugboard;

    /**
     * Constructor de la clase EnigmaKey. 
     * Inicializa los parametros de configuracion para una maquina Enigma.
     *
     * @param rotors Un arreglo de cadenas que representa los nombres de los rotores utilizados. 
     *               Si es null, se asigna por defecto {"I", "II", "III"}.
     * @param indicators Un arreglo de enteros que representa las posiciones iniciales de los rotores.
     *                   Si es null, se asigna por defecto {0, 0, 0}.
     * @param rings Un arreglo de enteros que representa los ajustes de anillos de los rotores.
     *              Si es null, se asigna por defecto {0, 0, 0}.
     * @param plugboardConnections Una cadena que especifica las conexiones del plugboard.
     *                             Si es null, se asigna por defecto una cadena vacia.
     * @param reflectorUsed Una cadena que representa el reflector utilizado.
     *                      Si es null, se asigna por defecto "B".
     */
    public EnigmaKey(String[] rotors, int[] indicators, int[] rings, String plugboardConnections, String reflectorUsed) {
        this.rotors = rotors == null ? new String[] {"I", "II", "III"} : rotors;
        this.indicators = indicators == null ? new int[] {0,0,0} : indicators;
        this.rings = rings == null ? new int[] {0,0,0} : rings;
        this.reflector = reflectorUsed == null ? "B" : reflectorUsed;
        this.plugboard = plugboardConnections == null ? "" : plugboardConnections;
    }

    
    /**
     * Constructor de copia de la clase EnigmaKey.
     * Permite inicializar un nuevo objeto de tipo EnigmaKey copiando los 
     * parametros de configuracion de otro objeto existente.
     *
     * @param key El objeto EnigmaKey del cual se copiaran los valores de configuracion.
     *            Si algun parametro en el objeto proporcionado es null, se asignaran
     *            los valores predeterminados correspondientes:<br>
     *            - rotors: {"I", "II", "III"}<br>
     *            - indicators: {0, 0, 0}<br>
     *            - rings: {0, 0, 0}<br>
     *            - plugboard: cadena vacia ("")<br>
     *            - reflector: "B"<br>
     * <br>
     * Funcionamiento interno:<br>
     * Este constructor verifica cada parametro del objeto proporcionado.
     * Si el parametro es null, se asigna el valor predeterminado correspondiente.
     * En el caso de arreglos no nulos, se copian los valores para evitar mutaciones entre instancias.
     */
    public EnigmaKey(EnigmaKey key) {
        this.rotors = key.rotors == null ? new String[] {"I", "II", "III"} : new String[] {key.rotors[0], key.rotors[1], key.rotors[2]};
        this.indicators = key.indicators == null ? new int[] {0,0,0} : Arrays.copyOf(key.indicators, 3);
        this.rings = key.rings == null ? new int[] {0,0,0} : Arrays.copyOf(key.rings,3);
        this.plugboard = key.plugboard == null ? "" : key.plugboard;
        this.reflector = key.reflector == null ? "B" : key.reflector;
    }
}
