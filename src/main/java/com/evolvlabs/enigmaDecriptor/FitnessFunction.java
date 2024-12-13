package com.evolvlabs.enigmaDecriptor;

/**
 * @author : Santiago Arellano
 * @Date : December 3rd, 2024
 * @Description : El presente archivo muestra el esqueleto, interface, de una funcion de fitness para el analysis y desencriptado
 * de una clave conociendo tanto las configuraciones iniciales o un plaintext y ciphertext. Para realizar esto se ha utilizado
 * la implementacion presentada en el repositorio llamado "enigma" de Michael Pound de la universidad de Nottingham. Con esta informacion
 * y recursos adicionales para comprender el funcionamiento de las mismas se han idealizado varios metodos que extienden a esta
 * interface, implementan sus metodos y facilitan el rompimiento de la encriptacion de enigma.
 * @TheoreticalComponents : La base teorica de esta interface y sus implementaciones es las funciones fitness, las cuales segun [1]
 * "Evolutionary computation, emcpoasses a variety of problem-solving methodologies that take inspiration from natural evolutionary and gentic
 * processes[...]these evolve a population of solutions to the problem at hand [...] with a fitness function measuring the fitness
 * of [the solution] within the context of the problem."
 * <br><br>
 * De esta forma, la forma en la que vamos a atacar el problema de desencriptacion es la utilizacion de fitness functions
 * para determinar tanto configuraciones de rotor, rings e inclusive ciphertext usado.
 */
public interface FitnessFunction {
    public abstract float score(char[] text);

    public default  float getEpsilon() {
        return 3E-10f;
    }
}