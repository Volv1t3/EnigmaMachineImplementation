package com.evolvlabs.enigmamachine;


import com.evolvlabs.enigmabackend.EnigmaMachineImplementation;
import com.evolvlabs.enigmabackend.PlugBoardImplementation;
import com.evolvlabs.enigmabackend.ReflectorImplementation;
import com.evolvlabs.enigmabackend.RotorImplementation;
import io.github.kamilszewc.javaansitextcolorizer.Colorizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
/**
 * @author : Santiago Arellano
 * @Date : December 4rd, 2024
 * @Description : El presente archivo contiene un modelo de maquina enigma para consola en el cual se puede encriptar y desencriptar
 * mensajes basados en parametros enviados a la maquina en linea de comando. Las maquinas se pueden configurar tanto para
 * el tipo de rotores usados, la posicion de los rotores, la posicion del ring Setting, y por supuesto la cadena de ingreso.
 * <br><br>
 * La idea del proceso es que esta maquina tenga la capacidad de, dada las mismas entradas y parametros, sea capaz de convertir
 * un ciphertext al plaintext original.
 */
public class EnigmaThroughConsole {

    //! Private Variable Declarations
    private static final HashMap<String,ReflectorImplementation> reflectors = new HashMap<>(){{
        put("B", ReflectorImplementation.createReflector("B"));
        put("C", ReflectorImplementation.createReflector("C"));
    }};

    private static  EnigmaMachineImplementation enigmaMachineImplementation;


    /**
     * Centra el texto dado en un ancho especifico, utilizando un caracter de relleno predeterminado ('-').
     * Si el texto es igual o mayor al ancho especificado, se devuelve sin modificaciones.
     *
     * @param text El texto que se desea centrar.
     * @param width El ancho total disponible en el que se centrara el texto.
     * @return El texto centrado, rodeado por caracteres de relleno si es necesario.
     */
    public static String centerText (String text,int width){
        return centerTextWithFiller(text, width, '-');
    }

    /**
     * Centra el texto dado en un ancho especifico, utilizando un caracter de relleno especificado.
     * Si el texto es igual o mayor al ancho especificado, se devuelve sin modificaciones.
     *
     * El metodo calcula el espacio de relleno en ambos lados del texto basado en la diferencia
     * entre el ancho deseado y la longitud del texto. Si el ancho no es simetrico, se agrega
     * un caracter adicional al final.
     *
     * @param text El texto que se desea centrar.
     * @param width El ancho total disponible en el que se centrara el texto.
     * @param filler El caracter utilizado para rellenar los espacios alrededor del texto.
     * @return El texto centrado, rodeado por caracteres de relleno si es necesario.
     */
    public static String centerText (String text,int width, char filler){
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        String paddingStr = new String(new char[padding]).replace('\0', filler);
        return paddingStr + text + paddingStr + ((width - text.length()) % 2 == 0 ? "" : filler);
    }

    /**
     * Centra el texto dado en un ancho especifico, utilizando un caracter de relleno especificado.
     * Si el texto es igual o mayor al ancho especificado, se devuelve sin modificaciones.
     *
     * Este metodo calcula espacios de relleno de manera simetrica en ambos lados del texto
     * y, si el ancho especificado no es par, agrega un caracter adicional de relleno al final.
     *
     * @param text El texto que se desea centrar.
     * @param width El ancho total disponible en el que se centrara el texto.
     * @param filler El caracter utilizado para rellenar los espacios alrededor del texto.
     * @return El texto centrado, rodeado por caracteres de relleno si es necesario.
     */
    public static String centerTextWithFiller (String text,int width, char filler){
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        String paddingStr = new String(new char[padding]).replace('\0', filler);
        return paddingStr + text + paddingStr + ((width - text.length()) % 2 == 0 ? "" : filler);
    }

    /**
     * Metodo principal que inicializa la maquina enigma de consola para manejar diferentes operaciones
     * basadas en las opciones seleccionadas por el usuario mediante un menu interactivo en linea de comandos.
     * <p>
     * Las operaciones disponibles incluyen:
     * <ul>
     *   <li>Configuracion inicial de la maquina enigma con rotores, reflectores y settings.</li>
     *   <li>Encriptacion y desencriptacion de mensajes basados en la configuracion actual de la maquina.</li>
     *   <li>Revisar y modificar la configuracion actual.</li>
     * </ul>
     * El bucle principal del metodo permite una interaccion continua mientras el usuario no seleccione la opcion de salir.
     *
     * @param args Argumentos de la linea de comandos (actualmente no utilizados en este programa).
     */
    public static void main(String[] args) {
        //! Iniciamos con la presentacion del proyecto
        System.out.printf("%s", centerText("Enigma Machine 1.0.1 | Teoria de la Computacion | Santiago Arellano | USFQ", 120));
        System.out.println();
        //! Do while para manejar consola
        int userOption;
        do{
            System.out.println("\nPara trabajar con la maquina enigma podemos encriptar y desencriptar un mensaje conociendo " +
                    "la configuracion inicial\nSeleccione una de las siguientes opciones: ");
            System.out.print("1.Encriptar un Mensaje\n2.Desencriptar un Mensaje\n3.Revisar Configuracion Actual\n4.Cambiar Configuracion Actual\n5. Salir\nSu opcion:");
            Scanner scanner = new Scanner(System.in);
            
            //? Revision de input
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número válido:");
                scanner.next(); // Discard invalid input
            }
            userOption = scanner.nextInt();
            System.out.println();
            
            
            //! Switch para los casos
            switch(userOption){
                case 1 /*Encriptar un Mensaje*/: {
                    if (enigmaMachineImplementation != null){
                        // Machine is already configured
                        System.out.println(centerText(Colorizer.color("La maquina enigma ya está configurada; procediendo con la configuración existente.", Colorizer.Color.GREEN_BOLD_BRIGHT), 120));
                    } else {
                        System.out.println(centerText(Colorizer.color("No hay una maquina enigma configurada, por favor, configure una antes de continuar", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        System.out.println();
                        System.out.println(centerText(Colorizer.color("A continuacion, ingrese los rotores que desea usar", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var results = getRotors();
                        System.out.printf("%s", centerText(Colorizer.color("Seleccione el reflector para usar en la maquina", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var reflector = getReflector();

                        //! Con los valores actuales, pasamos a revisar la configuracion inicial de los rotors y del ringSetting
                        System.out.printf("%s", centerText(Colorizer.color(
                                "Ingrese las posiciones iniciales de cada rotor (los datos se registran por rotor izquierdo hacia derecho",
                                Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var rotorPositions = getRotorPositions();
                        System.out.printf("%s", centerText(Colorizer.color("Ingrese la posicion inicial del Ring Setting", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var ringSetting = getRingSettings();
                        System.out.printf("%s", centerText(Colorizer.color("...Configurando la Maquina Enigma...", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        System.out.println();
                        enigmaMachineImplementation = new EnigmaMachineImplementation();
                        //! Creamos rotores
                        enigmaMachineImplementation.setE_rotorIzquierdo(RotorImplementation.createRotor(results[0],
                                rotorPositions[0], ringSetting[0]));
                        enigmaMachineImplementation.setE_rotorMedio(RotorImplementation.createRotor(results[1],
                                rotorPositions[1], ringSetting[1]));
                        enigmaMachineImplementation.setE_rotorDerecho(RotorImplementation.createRotor(results[2],
                                rotorPositions[2], ringSetting[2]));
                        //! Creamos reflectores
                        enigmaMachineImplementation.setE_reflectorInterno(reflectors.get(reflector));
                        //! Creamos plugboard
                        enigmaMachineImplementation.setE_plugBoardInterno(new PlugBoardImplementation(""));
                    } 

                    
                    //! Registramos Texto a Convertir
                    System.out.printf("%s", centerText(Colorizer.color("A continuacion, ingrese el texto a encriptar", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                    System.out.println();
                    scanner.nextLine(); // Clear the buffer
                    var textToEncrypt = scanner.nextLine();
                    char[] encryptedText = enigmaMachineImplementation.encriptadodeCaracter(textToEncrypt.toUpperCase().toCharArray());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (char c : encryptedText) {
                        stringBuilder.append(Character.toUpperCase(c));
                    }
                    System.out.printf("%s", centerText(Colorizer.color("El texto encriptado es: " + stringBuilder , Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                    System.out.println();


                    break;
                }
                case 2 /*Desencriptar un Mensaje de la configuracion actual*/: {
                    System.out.println(centerText(Colorizer.color("Acaba de ingresar a la seccion de desencriptado, si existe una maquina enigma configurada, se usara esta", Colorizer.Color.BLUE_BOLD_BRIGHT),120));
                    if (enigmaMachineImplementation == null){
                        System.out.println(centerText(Colorizer.color("No hay una maquina enigma configurada, por favor, configure una antes de continuar", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        break;
                    }
                    else {
                        //! Como usamos la misma configuracion, tomamos en cuenta que la maquina encripta cada letra para regresar cada notch cinco posiciones
                        System.out.println(centerText(Colorizer.color("A continuacion ingrese la configuracion de rotores inicial usada para encriptar", Colorizer.Color.GREEN_BOLD_BRIGHT),120));
                        System.out.println();
                        var results = getRotorPositions();
                        //! Ingresamos el ring config
                        System.out.printf("%s", centerText(Colorizer.color("Ingrese la posicion inicial del Ring Setting", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var ringSetting = getRingSettings();
                        //! Save previous positions
                        int[] previousPositions = new int[3];
                        previousPositions[0] = enigmaMachineImplementation.getE_rotorIzquierdo().getE_rotorPosition();
                        previousPositions[1] = enigmaMachineImplementation.getE_rotorMedio().getE_rotorPosition();
                        previousPositions[2] = enigmaMachineImplementation.getE_rotorDerecho().getE_rotorPosition();
                        int[] getPreviousRings = new int[3];
                        getPreviousRings[0] = enigmaMachineImplementation.getE_rotorIzquierdo().getE_RingSetting();
                        getPreviousRings[1] = enigmaMachineImplementation.getE_rotorMedio().getE_RingSetting();
                        getPreviousRings[2] = enigmaMachineImplementation.getE_rotorDerecho().getE_RingSetting();
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_rotorPosition(results[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_rotorPosition(results[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_rotorPosition(results[2]);
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_RingSetting(ringSetting[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_RingSetting(ringSetting[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_RingSetting(ringSetting[2]);

                        //! Registramos Texto a Convertir
                        System.out.printf("%s", centerText(Colorizer.color("A continuacion, ingrese el texto a desencriptar", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        scanner.nextLine();
                         // Clear the buffer
                        var textToEncrypt = scanner.nextLine();
                        char[] encryptedText = enigmaMachineImplementation.encriptadodeCaracter(textToEncrypt.toUpperCase().toCharArray());
                        StringBuilder stringBuilder = new StringBuilder();
                        for (char c : encryptedText) {
                            stringBuilder.append(Character.toUpperCase(c));
                        }
                        System.out.printf("%s", centerText(Colorizer.color("El texto desencriptado es: " + stringBuilder, Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        //! Reconfigure
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_rotorPosition(previousPositions[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_rotorPosition(previousPositions[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_rotorPosition(previousPositions[2]);
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_RingSetting(getPreviousRings[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_RingSetting(getPreviousRings[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_RingSetting(getPreviousRings[2]);
                        break;
                    }
                }
                case 3 /*Revisar Configuracion Actual*/: {
                    if (enigmaMachineImplementation == null){
                        System.out.println(centerText(Colorizer.color("No hay una maquina enigma configurada, por favor, configure una antes de continuar", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        break;
                    }
                    else {
                        System.out.println(centerText(Colorizer.color("La configuracion actual de la maquina enigma es:", Colorizer.Color.GREEN_BOLD_BRIGHT), 120));
                        System.out.println();
                        System.out.println(centerText(Colorizer.color("Rotores", Colorizer.Color.GREEN_BOLD_BRIGHT), 120, '_'));
                        System.out.println();
                        System.out.println("Rotor Izquierdo = " + enigmaMachineImplementation.getE_rotorIzquierdo().getE_rotorName());
                        System.out.println("Rotor Izquierdo Posicion = " + enigmaMachineImplementation.getE_rotorIzquierdo().getE_rotorPosition());
                        System.out.println("Rotor Izquierdo Ring Setting = " + enigmaMachineImplementation.getE_rotorIzquierdo().getE_RingSetting());
                        System.out.println("Rotor Medio = " + enigmaMachineImplementation.getE_rotorMedio().getE_rotorName());
                        System.out.println("Rotor Medio Posicion = " + enigmaMachineImplementation.getE_rotorMedio().getE_rotorPosition());
                        System.out.println("Rotor Medio Ring Setting = " + enigmaMachineImplementation.getE_rotorMedio().getE_RingSetting());
                        System.out.println("Rotor Derecho = " + enigmaMachineImplementation.getE_rotorDerecho().getE_rotorName());
                        System.out.println("Rotor Derecho Posicion = " + enigmaMachineImplementation.getE_rotorDerecho().getE_rotorPosition());
                        System.out.println("Rotor Derecho Ring Setting = " + enigmaMachineImplementation.getE_rotorDerecho().getE_RingSetting());
                        System.out.println(centerText(Colorizer.color("Reflectores", Colorizer.Color.GREEN_BOLD_BRIGHT), 120, '_'));
                        System.out.println("Reflector Name = " + enigmaMachineImplementation.getE_reflectorInterno().getE_rotorName());
                    }
                    break;
                }
                case 4 /*Variar Configuracion Actual*/: {
                    if (enigmaMachineImplementation == null){
                        System.out.println(centerText(Colorizer.color("No hay una maquina enigma configurada, por favor, configure una antes de continuar", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        break;
                    }
                    else {
                        System.out.println(centerText(Colorizer.color("Acaba de ingresar a la seccion de variacion de configuracion, si existe una maquina enigma configurada, se usara esta", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        System.out.println(centerText(Colorizer.color("A continuacion ingrese la configuracion de rotores inicial", Colorizer.Color.GREEN_BOLD_BRIGHT), 120));
                        System.out.println();
                        var results = getRotorPositions();
                        System.out.printf("%s", centerText(Colorizer.color("Ingrese la posicion inicial del Ring Setting", Colorizer.Color.BLUE_BOLD_BRIGHT), 120));
                        System.out.println();
                        var ringSetting = getRingSettings();
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_rotorPosition(results[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_rotorPosition(results[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_rotorPosition(results[2]);
                        enigmaMachineImplementation.getE_rotorIzquierdo().setE_RingSetting(ringSetting[0]);
                        enigmaMachineImplementation.getE_rotorMedio().setE_RingSetting(ringSetting[1]);
                        enigmaMachineImplementation.getE_rotorDerecho().setE_RingSetting(ringSetting[2]);
                        System.out.println(centerText(Colorizer.color("...Configurando la Maquina Enigma...", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                        break;
                    }
                }
                case 5 /*Salir*/: {
                    System.out.println(centerText(Colorizer.color("Saliendo...", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                    break;
                }
                default: {
                    System.out.println(centerText(Colorizer.color("Opcion invalida, por favor, ingrese una opcion valida", Colorizer.Color.RED_BOLD_BRIGHT), 120));
                    break;
                }
            }
        }while (userOption != 5);
    }


    
    /**
     * Este metodo permite al usuario seleccionar rotores mientras interactua con el programa.
     * Utiliza una lista de rotores disponibles y permite al usuario seleccionar por su numero de indice.
     * El numero total de rotores seleccionados debe ser exactamente tres.
     * 
     * @return Un arreglo de cadenas que contiene los nombres de los rotores seleccionados por el usuario.
     * 
     * Funcionamiento interno:
     * <ul>
     *     1. <ul>
     * <li>Crea una lista de rotores disponibles.</li>
     *     </ul>
     *2. <ul> <!-- Interactua con el usuario para presentar las opciones de rotores. -->
     *   <li>Muestra las opciones como una lista identificada por índice.</li>
     *   <li>Utiliza ul y li en la salida para cada opción presentada.</li>
     * </ul>
     * 3. <ul> <!-- Maneja la entrada del usuario para asegurarse de que las selecciones sean válidas. -->
     *   <li>Verifica si la entrada es un número válido relacionado a los índices presentados.</li>
     * </ul>
     * 4. <ul> <!-- Elimina cada rotor seleccionado de la lista disponible para evitar duplicados. -->
     *   <li>Una vez seleccionado, el rotor ya no será una opción válida en la próxima iteración.</li>
     * </ul>
     * 5. <ul> <!-- Finaliza cuando el usuario ha seleccionado tres rotores o cancela la selección. -->
     *   <li>Comprueba si la condición de tres rotores seleccionados se cumple o se elige cancelar.</li>
     * </ul>
     * </ul>
     */
    public static String[] getRotors(){
        // Available rotors for selection
        ArrayList<String> availableRotors = new ArrayList<>(Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII"));
        ArrayList<String> selectedRotors = new ArrayList<>();
    
        Scanner scanner = new Scanner(System.in);
        do {
            for (int i = 0; i < availableRotors.size(); i++) {
                System.out.println((i + 1) + ". " + availableRotors.get(i));
            }
            System.out.print("Select a rotor by entering its number (1 to " + availableRotors.size() + "), or enter 0 to finish: ");
    
            // Input handling
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número válido:");
                scanner.next();
            }
            int choice = scanner.nextInt();
    
            if (choice == 0) {
                break;
            } else if (choice > 0 && choice <= availableRotors.size()) {
                selectedRotors.add(availableRotors.remove(choice - 1));
            } else {
                System.out.println("Seleccion invalida. Intentelo de nuevo.");
            }
        } while (selectedRotors.size() <= 2);
    
        return selectedRotors.toArray(new String[0]);
    }
    
    
    /**
     * Permite al usuario seleccionar un reflector interactuando con la consola.
     * Este metodo muestra una lista de reflectores disponibles para que el usuario
     * elija uno. Una vez seleccionado, el reflector queda configurado para la maquina Enigma.
     *
     * Funcionamiento interno:
     * <ul>
     *   <li>Se crea una lista de reflectores disponibles.</li>
     *   <li>Se muestra la lista junto con un indice numerico.</li>
     *   <li>El usuario selecciona un reflector ingresando el numero correspondiente.</li>
     *   <li>El programa valida la entrada del usuario para asegurar que corresponde a una seleccion valida.</li>
     *   <li>Si el usuario selecciona 0, el proceso finaliza sin seleccionar un reflector.</li>
     *   <li>Si la seleccion es valida, el reflector elegido se retorna como resultado.</li>
     * </ul>
     *
     * @return Un String que representa el nombre del reflector seleccionado.
     * @throws RuntimeException si ocurre un error inesperado durante el proceso.
     */
    public static String getReflector(){
        ArrayList<String> reflectorImplementationArrayList =new ArrayList<>(Arrays.asList("B","C"));
        ArrayList<String> selectedReflectors = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
    
          try {
              for (int i = 0; i < reflectorImplementationArrayList.size(); i++) {
                  System.out.println((i + 1) + ". " + reflectorImplementationArrayList.get(i));
              }
              System.out.print("Select a reflector by entering its number (1 to " + reflectorImplementationArrayList.size() + "), or enter 0 to finish: ");
              // Input handling
              while (!scanner.hasNextInt()) {
                  System.out.print("Por favor, ingrese un número válido:");
                  scanner.next();
              }
              int choice = scanner.nextInt();
              if (choice == 0){break;}
              else if (choice > 0 && choice <= reflectorImplementationArrayList.size()){
                  selectedReflectors.add(reflectorImplementationArrayList.remove(choice - 1));
              }
              else {
                  System.out.println("Seleccion invalida. Intentelo de nuevo.");
              }
          }  catch (Exception e){
              System.out.println((e.getMessage()));
              throw new RuntimeException(e);
          }
        }while (selectedReflectors.isEmpty());
    
        return selectedReflectors.getFirst();
    }
    
    
    /**
     * Este metodo permite al usuario ingresar las posiciones iniciales
     * para los tres rotores de la maquina Enigma. Cada posicion debe ser
     * un numero entre 1 y 26.
     *
     * Funcionamiento interno:
     * <ul>
     *   <li>Solicita al usuario un numero para representar la posicion
     *       de cada rotor de izquierda a derecha.</li>
     *   <li>Valida que el numero ingresado este dentro del rango especificado (1-26).</li>
     *   <li>Guarda la posicion en un arreglo y repite hasta completar
     *       la configuracion de los tres rotores.</li>
     * </ul>
     *
     * @return Un arreglo de enteros que contiene las posiciones iniciales (convertidas a indices de 0-25).
     */
    public static int[] getRotorPositions() {
        int[] positions = new int[3];
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Ingrese un numero (1-26) para la posicion del rotor: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un numero valido (1-26): ");
                scanner.next();
            }
            int num = scanner.nextInt();
            if (num >= 1 && num <= 26) {
                positions[count++] = num - 1;
            } else {
                System.out.println("Numero fuera de rango. Intente nuevamente.");
            }
        } while (count < 3);
        return positions;
    }
    
    
    /**
     * Este metodo permite obtener las configuraciones del anillo para los tres rotores de la maquina Enigma.
     * El usuario puede ingresar un numero dentro del rango de (1-26) para cada rotor. Internamente,
     * los valores se convierten a un indice basado en cero para el uso correcto en el sistema.
     * Funcionamiento interno:
     * <ul>
     *   <li>Se solicita al usuario un valor numerico (1-26) para cada rotor.</li>
     *   <li>El sistema valida las entradas para asegurar que esten dentro del rango permitido.</li>
     *   <li>Los valores ingresados se ajustan restando 1, para alinearlos con los indices del sistema basados en cero.</li>
     *   <li>El ciclo repite el proceso hasta que las configuraciones de tres rotores sean completadas.</li>
     * </ul>
     * 
     * @return Devuelve un arreglo de enteros con las configuraciones ajustadas del anillo de los tres rotores.
     */
    public static int[] getRingSettings() {
        int[] settings = new int[3];
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("Ingrese un número (1-26) para la configuración del anillo: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, ingrese un número válido (1-26): ");
                scanner.next();
            }
            int num = scanner.nextInt();
            if (num >= 1 && num <= 26) {
                settings[count++] = num - 1;
            } else {
                System.out.println("Número fuera de rango. Intente nuevamente.");
            }
        } while (count < 3);
        return settings;
    }
    
}
