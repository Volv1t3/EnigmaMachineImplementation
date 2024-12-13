package com.evolvlabs.enigmamachine;

import com.evolvlabs.enigmaDecriptor.*;
import com.evolvlabs.enigmabackend.EnigmaMachineImplementation;
import io.github.kamilszewc.javaansitextcolorizer.Colorizer;

import java.util.Arrays;
import java.util.Scanner;

public class EnigmaDecriptorRunner {
    
    /**
     * Metodo principal que ejecuta el programa para realizar tareas relacionadas con la configuracion,
     * encriptacion y evaluacion de ataques a una maquina Enigma.
     *
     * <br><br>
     * Funcionamiento:<br>
     * - Lista diferentes funciones de evaluacion de aptitud para el proceso de desencriptacion.<br>
     * - Permite al usuario ingresar configuraciones de la maquina Enigma, incluyendo rotores, posiciones,<br>
     *   reflector y configuraciones de anillo.<br>
     * - Encripta una cadena de texto ingresada por el usuario y aplica diversos metodos de evaluacion para encontrar<br>
     *   configuraciones posibles de rotores y posiciones.<br>
     * - Realiza una serie de ataques para intentar deducir la configuracion utilizada para la encriptacion,<br>
     *   mostrando resultados preliminares con base en los datos conocidos.<br>
     * - Incluye manejo de excepciones para errores potenciales durante la ejecucion.<br><br>
     */
    public static void main(String[] args) {

         final FitnessFunction[] fullLengthFunctions = {
                 new SinglegramFitnessFunction(),
                 new BigramFitnessFunction(),
                 new TrigramFitnessFunction(),
                 new QuagramFitnessFunction(),
                 new IoCFitnessFunction(),
                 new ImprovedNGram(),
                 new CombinedFrequencyFitnessFunction(),
                 new HybridNGramFitnessFunction()};

        int userOption = 0;
        do {
            System.out.println("Ingrese un numero aleatorio para continuar [-1 para salir]: ");
            Scanner scanner = new Scanner(System.in);
            userOption = scanner.nextInt();
            if (userOption != -1) {
                try {
                    String readLine;
                    do {
                        scanner.nextLine();
                        System.out.println("Ingrese una cadena para encriptar sin espacios [ Asegurese de tener una longitud mayor a tres caracteres ]: ");
                        readLine = scanner.nextLine();

                    } while (readLine.length() < 3);
                    var rotors = EnigmaThroughConsole.getRotors();
                    var rotorPositions = EnigmaThroughConsole.getRotorPositions();
                    var ringSettings = EnigmaThroughConsole.getRingSettings();
                    var reflector = EnigmaThroughConsole.getReflector();
                    EnigmaMachineImplementation enigmaMachineImplementation = new EnigmaMachineImplementation(
                            rotors,
                            reflector,
                            rotorPositions,
                            ringSettings,
                            "");
                    System.out.println();
                    System.out.println(EnigmaThroughConsole.centerText(Colorizer.color("Configuracion Actual", Colorizer.Color.RED_UNDERLINED),120, '-'));
                    System.out.println("Rotores Seleccionados = " + Arrays.toString(rotors));
                    System.out.println("Reflector Seleccionado = " + reflector);
                    System.out.println("Posicion de los Rotores = " + Arrays.toString(rotorPositions));
                    System.out.println("Configuracion de los Rotores = " + Arrays.toString(ringSettings));
                    System.out.println();

                    char[] plaintext = readLine.replaceAll(" ", "").toUpperCase().toCharArray();
                    char[] encryptedText = enigmaMachineImplementation.encriptadodeCaracter(plaintext);
                    enigmaMachineImplementation = null;

                    System.out.println();
                    System.out.println(EnigmaThroughConsole.centerText(
                            Colorizer.color("Resultados Preliminares Sin Configuracion Conocida, Busqueda Completa",
                                                 Colorizer.Color.RED_UNDERLINED),
                            120,
                            '-'));
                    for(FitnessFunction f : fullLengthFunctions){
                        var results = Collosus.findRotorConfiguration(encryptedText, Collosus.AvailableRotors.THREE, "", 30, f);
                        System.out.println("Fitness Function: " + f.getClass().getCanonicalName());
                        for(ScoredEnigmaKey result : results){
                            if (Arrays.equals(result.rotors, rotors) && Arrays.equals(result.indicators, rotorPositions)){
                                System.out.printf("%s %s %s / %d %d %d / %s / %f%n",
                                        result.rotors[0], result.rotors[1], result.rotors[2],
                                        result.indicators[0], result.indicators[1], result.indicators[2],result.reflector,
                                        result.getScore());
                            }
                        }
                    }

                    System.out.println(EnigmaThroughConsole.centerText(
                            Colorizer.color("Resultados Preliminares Busqueda Conociendo Posiciones de Rotor y Reflector",
                                    Colorizer.Color.RED_UNDERLINED),
                            120,
                            '-'));
                    for(FitnessFunction function : fullLengthFunctions){
                        var results = Collosus.findRotorConfigurationKnowingInitialPositionsAndReflector(encryptedText,
                                Collosus.AvailableRotors.THREE, "",Arrays.stream(rotorPositions).boxed().toArray(Integer[]::new), reflector, function, 30);
                        System.out.println("Fitness Function: " + function.getClass().getCanonicalName());
                        for(ScoredEnigmaKey result : results){
                            if (Arrays.equals(result.indicators, rotorPositions) && Arrays.equals(rotors, result.rotors)){
                                System.out.printf("%s %s %s / %d %d %d / %s / %f%n",
                                        result.rotors[0], result.rotors[1], result.rotors[2],
                                        result.indicators[0], result.indicators[1], result.indicators[2],
                                        result.reflector,
                                        result.getScore());
                            }
                        }
                    }

                    //! Anadimos seccion para trabajar conociendo los rotores usados y el reflector
                    System.out.println(EnigmaThroughConsole.centerText(
                            Colorizer.color("Resultados Preliminares Busqueda Conociendo Rotores y Reflector Usados",
                                    Colorizer.Color.RED_UNDERLINED),
                            120,
                            '-'));
                    for(FitnessFunction function : fullLengthFunctions){
                        var results = Collosus.findRotorConfigurationKnowingInitialReflectorAndRotors(encryptedText,
                                rotors, "",reflector,  function, 30);
                        System.out.println("Fitness Function: " + function.getClass().getCanonicalName());
                        for(ScoredEnigmaKey result : results){
                            if (Arrays.equals(result.indicators, rotorPositions) && Arrays.equals(rotors, result.rotors)){
                                System.out.printf("%s %s %s / %d %d %d / %s / %f%n",
                                        result.rotors[0], result.rotors[1], result.rotors[2],
                                        result.indicators[0], result.indicators[1], result.indicators[2],
                                        result.reflector,
                                        result.getScore());
                            }
                        }
                    }

                    //! Trabajamos conociendo rotores usados, y parte del plaintext
                    System.out.println(EnigmaThroughConsole.centerText(
                            Colorizer.color("Resultados Preliminares Busqueda Conociendo Rotores Usades y Parte del Plaintext",
                                    Colorizer.Color.RED_UNDERLINED),
                            120,
                            '-'));
                    var results = Collosus.findRotorConfigurationKnowingCipherPartTextRotors(encryptedText, plaintext, rotors, 30);
                    System.out.println("Plaintext Known Attack Results");
                    for(ScoredEnigmaKey result : results){
                            System.out.printf("%s %s %s / %d %d %d /  %s / %f%n",
                                    result.rotors[0], result.rotors[1], result.rotors[2],
                                    result.indicators[0], result.indicators[1], result.indicators[2],
                                    result.reflector,
                                    result.getScore());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        } while (userOption != -1);
    }
}
