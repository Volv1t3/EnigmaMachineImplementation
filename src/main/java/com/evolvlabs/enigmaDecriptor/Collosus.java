package com.evolvlabs.enigmaDecriptor;


import com.evolvlabs.enigmabackend.EnigmaMachineImplementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author : Santiago Arellano
 * @Date : December 10, 2024
 * @Description : El presente archivo contiene la implementacion de la maquina, alegremente llamada Collosus, que contiene
 * varios metodos disenados para romper la maquina engima y su implementacion en com.evolvLabs.enigmabackend. Para realizar estas
 * transformaciones hemos tomado parte del codigo presente en el github perteneciente a Michael Pound, assitant CS professor
 * en la Universidad de Nottingham. El codigo que implementa utilizar varias funciones para realizar fitness testing y
 * romper el codigo enigma en base a estas pruebas.
 */
public class Collosus {
    public enum AvailableRotors {
        THREE, FIVE, EIGHT
    }

    private static final List<String> reflectorOptions = List.of("B", "C");

    /**
     * Este metodo devuelve una lista de rotores disponibles segun la opcion proporcionada.
     *
     * @param rotors Enumeracion que indica el numero de rotores disponibles. Puede tomar los valores: <br>
     *               - THREE: Devuelve una lista con los rotores "I", "II", y "III".<br>
     *               - FIVE: Devuelve una lista con los rotores "I", "II", "III", "IV", y "V".<br>
     *               - EIGHT: Devuelve una lista con los rotores "I", "II", "III", "IV", "V", "VI", "VII", y "VIII".<br>
     * @return Una lista de cadenas de texto donde cada cadena representa un rotor disponible para la configuracion
     *         de la maquina Enigma.
     *<br><br>
     * Internamente, se inicializa una lista vacia y se utiliza una estructura de switch para determinar los rotores a agregar,
     * basandose en el valor del parametro 'rotors'. Estos se agregan a la lista con base en el caso especifico.
     */
    private static List<String> getAvailableRotorList(AvailableRotors rotors) {
        List<String> availableRotors = new ArrayList<>();
        switch (rotors) {
            case THREE -> availableRotors.addAll(Arrays.asList("I", "II", "III"));
            case FIVE -> availableRotors.addAll(Arrays.asList("I", "II", "III", "IV", "V"));
            case EIGHT -> availableRotors.addAll(Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII"));
        }
        return availableRotors;
    }

    /**
     * Este metodo genera todas las posibles combinaciones de tres rotores diferentes a partir de una lista de rotores dada.
     * <br><br>
     * Este metodo utiliza tres bucles anidados para recorrer los elementos de la lista. Asegura que cada combinacion de tres rotores sea unica
     * al comparar los elementos actuales y evitar combinaciones donde algun rotor se repita en la misma configuracion.
     *
     * @param rotors Una lista de cadenas que representan los rotores disponibles (por ejemplo, "I", "II", "III", etc.).
     * @return Una lista de arreglos de cadenas donde cada arreglo contiene exactamente tres rotores, siendo todos diferentes entre si.
     */
    private static List<String[]> getThreeRotorCombinations(List<String> rotors) {
        List<String[]> combinations = new ArrayList<>();
        for (String left : rotors) {
            for (String middle : rotors) {
                if (middle.equals(left)) continue;
                for (String right : rotors) {
                    if (right.equals(left) || right.equals(middle)) continue;
                    combinations.add(new String[]{left, middle, right});
                }
            }
        }
        return combinations;
    }

    /**
     * Este metodo determina un conjunto de umbrales adaptativos basados en la longitud del texto cifrado.
     * <br><br>
     * La logica del metodo analiza el valor del parametro 'textLength' y selecciona distintos conjuntos
     * de valores predefinidos que representan los umbrales para el procesamiento. Los conjuntos de umbrales
     * varian segun el tamano del texto y reflejan niveles de precision en las pruebas de fitness ejecutadas.
     *
     * @param textLength La longitud del texto cifrado para el cual se estableceran los umbrales.
     *                  <ul>
     *                      <li>Si 'textLength' es menor o igual a 3: retorna { -1e8f, -1e6f, -1e4f }.</li>
     *                      <li>Si 'textLength' esta entre 4 y 5 (inclusive): retorna { -1e12f, -1e10f, -1e8f }.</li>
     *                      <li>Si 'textLength' es mayor a 5: retorna { -1e20f, -1e15f, -1e10f }.</li>
     *                  </ul>
     * @return Un arreglo de flotantes donde cada elemento representa un nivel de umbral para pruebas
     *         de fitness en el procesamiento de rotores de la maquina Enigma.
     */
    private static float[] determineThresholds(int textLength) {
        if (textLength <= 3) {
            return new float[]{-1e8f, -1e6f, -1e4f};
        } else if (textLength <= 5) {
            return new float[]{-1e12f, -1e10f, -1e8f};
        } else {
            return new float[]{-1e20f, -1e15f, -1e10f};
        }
    }

    /**
     * Este metodo encuentra las configuraciones de rotores que mejor descifran un texto cifrado.
     * <br><br>
     * El algoritmo analiza combinaciones de rotores, posiciones iniciales y reflectores usando un metodo paralelo. Para cada
     * combinacion, se evalua el fitness basado en una funcion proporcionada por el usuario, y determina las configuraciones
     * con mejores resultados.
     *
     * @param ciphertext El texto cifrado que sera analizado.
     * @param rotors El conjunto de rotores disponibles que deberan ser usados en el proceso,
     *               definido mediante la enumeracion {@link AvailableRotors}.
     * @param plugboard La configuracion inicial del plugboard a usar para la maquina Enigma.
     * @param requiredKeys El numero de configuraciones (claves) que se desean devolver basadas en el rendimiento.
     * @param f La funcion de fitness utilizada para evaluar las configuraciones desencriptadas.
     * @return Un arreglo de objetos {@link ScoredEnigmaKey} que contienen las configuraciones mas optimas evaluadas, segun
     *         el valor de fitness.
     */
    public static ScoredEnigmaKey[] findRotorConfiguration(
            char[] ciphertext,
            AvailableRotors rotors,
            String plugboard,
            int requiredKeys,
            FitnessFunction f) {

        List<String[]> threeRotorCombinations = getThreeRotorCombinations(getAvailableRotorList(rotors));
        final List<ScoredEnigmaKey> keySet = Collections.synchronizedList(new ArrayList<>());
        float[] thresholds = determineThresholds(ciphertext.length);

        for (String reflector : reflectorOptions) {
            System.out.println("Processing reflector: " + reflector);
            threeRotorCombinations.parallelStream().forEach(combination -> {
                try {

                    processRotorCombination(
                            combination,
                            ciphertext,
                            plugboard,
                            reflector,
                            f,
                            thresholds,
                            keySet
                    );
                } catch (Exception e) {
                    System.err.println("Error processing combination: " + Arrays.toString(combination));
                    e.printStackTrace();
                }
            });
        }


        return processResults(keySet, requiredKeys);
    }


    /**
     * Este metodo realiza el analisis detallado de una combinacion especifica de rotores para descifrar un texto cifrado
     * utilizando un reflector especifico. Se basa en una funcion de fitness para evaluar las configuraciones y encontrar
     * aquellas con mejor desempeño.
     *
     * @param rotorCombination Un arreglo de cadenas que representa la configuracion seleccionada de tres rotores, e.g. {"I", "II", "III"}.
     * @param ciphertext Un arreglo de caracteres que contiene el texto cifrado a descifrar.
     * @param plugboard Una cadena que denota la configuracion del enchufe (plugboard) de la maquina Enigma, e.g. "AB CD EF".
     * @param reflector Un reflector especifico a utilizar para la configuracion actual, e.g. "B" o "C".
     * @param f Una instancia de la interfaz {@link FitnessFunction}, que se utiliza para calificar las configuraciones desencriptadas.
     * @param thresholds Un arreglo de valores flotantes que representan los umbrales de puntaje para filtrar configuraciones candidatas.
     * @param keySet Una lista sincronizada que se utiliza para almacenar las configuraciones que superan los umbrales establecidos.
     * <br>
     * <p>Internamente, el metodo analiza todas las combinaciones posibles de posiciones iniciales
     * para los tres rotores configurados. Cada combinacion se evalua utilizando la funcion de 
     * fitness y, si supera ciertos umbrales, se agrega a la lista de configuraciones 'keySet'. 
     * Tambien mantiene un registro del mejor puntaje encontrado durante el analisis.</p>
     * <br>
     * <p>El metodo utiliza estructuras sincronizadas y paralelismo para optimizar el analisis,
     * pues este implica un gran numero de combinaciones debido a la naturaleza del algoritmo de Enigma.</p>
     */
    private static void processRotorCombination(
            String[] rotorCombination,
            char[] ciphertext,
            String plugboard,
            String reflector,
            FitnessFunction f,
            float[] thresholds,
            List<ScoredEnigmaKey> keySet) {

        int[] defaultRingSettings = new int[]{0, 0, 0};
        List<ScoredEnigmaKey> configurationScores = new ArrayList<>();

        EnigmaMachineImplementation e = new EnigmaMachineImplementation(
                rotorCombination,
                reflector,
                new int[]{0, 0, 0},
                defaultRingSettings,
                plugboard
        );

        // Pre-allocate decryption array
        char[] decryption = new char[ciphertext.length];
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                for (int k = 0; k < 26; k++) {
                    e.getE_rotorIzquierdo().setE_rotorPosition(i);
                    e.getE_rotorMedio().setE_rotorPosition(j);
                    e.getE_rotorDerecho().setE_rotorPosition(k);

                    System.arraycopy(ciphertext, 0, decryption, 0, ciphertext.length);
                    e.encriptadodeCaracter(decryption);
                    float fitness = f.score(decryption);

                    if (fitness > bestScore) {
                        bestScore = fitness;
                    }

                    if (fitness > thresholds[0]) {
                        configurationScores.add(new ScoredEnigmaKey(
                                new EnigmaKey(
                                        rotorCombination.clone(),
                                        new int[]{i, j, k},
                                        defaultRingSettings.clone(),
                                        plugboard,
                                        reflector
                                ),
                                fitness
                        ));
                    }
                }
            }
        }


        synchronized (keySet) {
            configurationScores.stream()
                    .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                    .limit(10)
                    .forEach(keySet::add);
        }
    }

    /**
     * Este metodo analiza y evalua una combinacion especifica de posiciones iniciales
     * para una maquina Enigma con una configuracion determinada. El proceso involucra
     * el uso de una funcion de fitness para calcular la calidad de descifrado basado
     * en el texto descifrado comparado con el plaintext conocido.
     *
     * @param e Una instancia de la implementacion de la maquina Enigma configurada
     *          con los rotores, configuracion del reflector, plugboard y configuracion
     *          de anillos.
     * @param i Posicion inicial del rotor izquierdo (0 a 25).
     * @param j Posicion inicial del rotor medio (0 a 25).
     * @param k Posicion inicial del rotor derecho (0 a 25).
     * @param ciphertext Arreglo de caracteres que contiene el texto cifrado que sera descifrado.
     * @param plaintext Arreglo de caracteres que representa el plaintext esperado; 
     *                  se compara contra el texto descifrado para evaluar el fitness.
     * @param rotorCombination Arreglo de cadenas con la combinacion de rotores seleccionada
     *                         (por ejemplo, {"I", "II", "III"}).
     * @param ringSettings Arreglo entero que contiene la configuracion de los anillos de los rotores.
     * @param plugboard Cadena que representa la configuracion del plugboard de la maquina Enigma.
     *                  Ejemplo: "AB CD EF".
     * @param configurationScores Lista sincronizada donde se almacenan los resultados de las configuraciones
     *                            que cumplen con ciertos criterios.
     * @param reflector Cadena que indica el reflector utilizado para el descifrado (por ejemplo, "B" o "C").
     *
     * <p>Funcionamiento interno:</p>
     * <ul>
     *   <li>Primero, se actualizan las posiciones iniciales de los rotores (i, j, k) en la
     *       instancia de la maquina Enigma proporcionada.</li>
     *   <li>Se descifra el texto cifrado ('ciphertext') usando la configuracion actual de la maquina Enigma.</li>
     *   <li>Se evalua el nivel de fitness mediante el uso de una funcion de fitness predefinida,
     *       basada en las diferencias entre el texto descifrado y el plaintext esperado ('plaintext').</li>
     *   <li>Se crea un objeto EnigmaKey que encapsula la configuracion actual de rotores, posiciones y
     *       configuracion del reflectante.</li>
     *   <li>Finalmente, se agrega un objeto ScoredEnigmaKey (que contiene la configuracion y el puntaje de fitness)
     *       a la lista 'configurationScores'.</li>
     * </ul>
     */
    private static void processRotorPosition(
            EnigmaMachineImplementation e,
            int i, int j, int k,
            char[] ciphertext,
            char[] plaintext,
            String[] rotorCombination,
            int[] ringSettings,
            String plugboard,
            List<ScoredEnigmaKey> configurationScores,
            String reflector) {

        e.getE_rotorIzquierdo().setE_rotorPosition(i);
        e.getE_rotorMedio().setE_rotorPosition(j);
        e.getE_rotorDerecho().setE_rotorPosition(k);

        char[] decryption = e.encriptadodeCaracter(ciphertext);
        float fitness = new KnownPlaintextFitnessFunction(plaintext).score(decryption);

        EnigmaKey currentKey = new EnigmaKey(
                rotorCombination,
                new int[]{i, j, k},
                ringSettings,
                plugboard,
                reflector
        );

        configurationScores.add(new ScoredEnigmaKey(currentKey, fitness));
    }

    /**
     * Este metodo filtra las configuraciones de rotores en base a un umbral de puntaje (threshold),
     * seleccionando solamente aquellas configuraciones que exceden este valor. Ordena las configuraciones
     * seleccionadas en orden descendente segun el puntaje y agrega las mejores configuraciones
     * a la lista proporcionada (keySet).
     *
     * @param configurationScores Una lista que contiene objetos de tipo {@link ScoredEnigmaKey}
     *                            representando diferentes configuraciones de la maquina Enigma junto con su puntaje asociado.
     * @param threshold El puntaje minimo necesario para considerar una configuracion de rotores como valida.
     * @param keySet Una lista sincronizada de configuraciones donde se almacenaran las configuraciones que
     *               cumplen con los criterios especificados.
     *
     * <br><br>
     * Funcionamiento interno:
     * <ul>
     *   <li>Se aplica un filtro sobre la lista proporcionada (configurationScores) para seleccionar las configuraciones con puntaje
     *       mayor al umbral definido (threshold).</li>
     *   <li>Las configuraciones filtradas se ordenan en orden descendente segun su puntaje.</li>
     *   <li>Se seleccionan las 10 configuraciones principales con los puntajes mas altos y estas se agregan
     *       a la lista proporcionada (keySet).</li>
     * </ul>
     */
    private static void addConfigurationsAboveThreshold(
            List<ScoredEnigmaKey> configurationScores,
            float threshold,
            List<ScoredEnigmaKey> keySet) {

        configurationScores.stream()
                .filter(key -> key.getScore() > threshold)
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                .limit(10)
                .forEach(keySet::add);
    }

    /**
     * Este metodo procesa el conjunto de configuraciones de rotores almacenados en la lista 'keySet',
     * ordenandolos segun su puntaje (de mayor a menor). Filtra aquellos elementos con configuraciones
     * duplicadas y selecciona las configuraciones especificadas por 'requiredKeys'.
     *
     * @param keySet Una lista que contiene objetos de tipo {@link ScoredEnigmaKey}, que representan
     *               configuraciones de la maquina Enigma junto con su puntaje asociado.
     * @param requiredKeys Numero de configuraciones principales deseadas basado en su puntaje.
     * @return Un arreglo de objetos {@link ScoredEnigmaKey} que contiene las mejores configuraciones
     *         ordenadas en base a su puntaje y eliminando duplicados.
     *
     * Funcionamiento interno:
     * <ul>
     *   <li>La lista proporcionada, 'keySet', es ordenada segun el puntaje de sus elementos en
     *       orden descendente (mayor a menor).</li>
     *   <li>Se eliminan configuraciones duplicadas utilizando el metodo {@link Stream#distinct()}.</li>
     *   <li>Los primeros 'requiredKeys' elementos son seleccionados y convertidos en un arreglo.</li>
     * </ul>
     */
    private static ScoredEnigmaKey[] processResults(List<ScoredEnigmaKey> keySet, int requiredKeys) {
        return keySet.stream()
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                .distinct()
                .limit(requiredKeys)
                .toArray(ScoredEnigmaKey[]::new);
    }
    //! Implementacion de un metodos statico que retorne una sola configuracion de rotores y posiciones iniciales basados en un
    //! ciphertext y un plaintext.


    /**
     * Este metodo encuentra una configuracion unica de rotores y posiciones iniciales de la maquina Enigma 
     * basandose en un texto cifrado y su texto en claro correspondiente. Usa combinaciones de rotores, 
     * plugboard, y posiciones iniciales para analizar posibles configuraciones que coincidan con el texto proporcionado.
     *
     * @param ciphertext Un arreglo de caracteres que contiene el texto cifrado a descifrar.
     * @param plaintext Un arreglo de caracteres que representa el plaintext conocido que se busca obtener tras descifrar.
     * @param rotors Enumeracion {@link AvailableRotors} que define el conjunto de rotores disponibles para la configuracion.
     * @param plugBoard Una cadena que representa las conexiones de enchufes (plugboard) en la maquina Enigma.
     *                 Ejemplo: "AB CD EF".
     * @return Un objeto {@link ScoredEnigmaKey} que contiene la configuracion unicamente correcta de rotores y 
     *         posiciones iniciales que permiten descifrar el texto.
     *<br><br>
     * Funcionamiento interno:
     * <ul>
     *   <li>Genera una lista de combinaciones de tres rotores usando {@link #getThreeRotorCombinations(List)}.</li>
     *   <li>Crea una instancia de la maquina Enigma para cada combinacion y prueba todas las posibles posiciones iniciales 
     *       de los rotores (0 a 25).</li>
     *   <li>Descifra el texto cifrado con cada combinacion y compara el resultado con el plaintext proporcionado.</li>
     *   <li>Si existe una coincidencia exacta, agrega la configuracion correspondiente a la lista de resultados.</li>
     *   <li>Regresa la primera coincidencia encontrada, ya que se asume una configuracion unica correcta.</li>
     * </ul>
     */
    public static ScoredEnigmaKey findRotorConfigurationKnowingPlainCipherText(char[] ciphertext, char[] plaintext, AvailableRotors rotors, String plugBoard) {

        //! Utilizamos el mismo metodo para producir la lista de rotores a revisar. Usamos una lista sincronizada para
        //! analisis paralelo
        List<String[]> threeRotorCombinations = getThreeRotorCombinations(getAvailableRotorList(rotors));
        final List<ScoredEnigmaKey> keySet = Collections.synchronizedList(new ArrayList<>());
        int[] defaultStartingPositions = new int[]{0, 0, 0};
        int[] defaultRingSettings = new int[]{0, 0, 0};

        threeRotorCombinations.parallelStream().forEach(combination -> {

            EnigmaMachineImplementation e = new EnigmaMachineImplementation(combination,
                    "B",
                    defaultStartingPositions,
                    defaultRingSettings, plugBoard);

            //! Define three for loops for each of the positions in a single loop
            for (int i = 0; i < 26; i++) {
                for (int j = 0; j < 26; j++) {
                    for (int k = 0; k < 26; k++) {
                        e.getE_rotorIzquierdo().setE_rotorPosition(i);
                        e.getE_rotorMedio().setE_rotorPosition(j);
                        e.getE_rotorDerecho().setE_rotorPosition(k);

                        //! Encriptamos ciphertext y buscamos coincidencia con plaintext
                        char[] decryptedCipherText = e.encriptadodeCaracter(ciphertext);
                        if (Arrays.equals(plaintext, decryptedCipherText)) {
                            keySet.add(
                                    new ScoredEnigmaKey(
                                            new EnigmaKey(
                                                    combination,
                                                    new int[]{i, j, k},
                                                    defaultRingSettings,
                                                    "",
                                                    "B"),
                                            1.0f)
                            );
                        }
                    }
                }
            }
        });
        return keySet.getFirst();
    }

    
    /**
     * Este metodo encuentra configuraciones optimas de rotores de la maquina Enigma basandose en un texto cifrado,
     * utilizando posiciones iniciales predeterminadas y un reflector conocido.
     * 
     * @param ciphertext Un arreglo de caracteres que contiene el texto cifrado que se desea descifrar.
     * @param rotors Enumeracion {@link AvailableRotors} que define el conjunto de rotores disponibles para la configuracion.
     * @param plugBoard Una cadena que representa la configuracion del plugboard (enchufes) en la maquina Enigma.
     *                 Ejemplo: "AB CD EF".
     * @param initialConfigurations Un arreglo de enteros que establece las posiciones iniciales de los rotores. 
     *                               Ejemplo: {0, 1, 2} para las posiciones iniciales 0, 1 y 2 de los rotores izquierdo, medio y derecho, respectivamente.
     * @param reflectorUsed Una cadena que especifica el reflector conocido que se utilizara. Ejemplo: "B" o "C".
     * @param function Una instancia de la interfaz {@link FitnessFunction}, que se usa para puntuar las configuraciones descifradas.
     * @param iterations Numero maximo de configuraciones que se desean devolver despues de filtrar y procesar los resultados.
     * @return Un arreglo de objetos {@link ScoredEnigmaKey} que contiene las configuraciones mas optimas evaluadas segun
     *         la funcion de fitness. Esta configuracion considera el reflector, rotores y posiciones iniciales proporcionadas.
     *
     * <br>
     * <p>Funcionamiento interno:</p>
     * <ul>
     *   <li>Genera una lista de combinaciones de rotores dependiendo de la enumeracion {@link AvailableRotors} solicitada.</li>
     *   <li>Crea una instancia de la maquina Enigma para cada combinacion y evalua la configuracion utilizando las posiciones iniciales y el reflector especifico.</li>
     *   <li>Descifra el texto cifrado ('ciphertext') utilizando dichas configuraciones, analiza los resultados con la funcion de fitness proporcionada, y almacena los puntajes obtenidos.</li>
     *   <li>Filtra las configuraciones que superan los umbrales definidos por el tamano del texto cifrado.</li>
     *   <li>Ordena y regresa las configuraciones mas optimas basadas en su puntaje, devolviendo solamente las 'iterations' requeridas como resultado.</li>
     * </ul>
     */
    public static ScoredEnigmaKey[] findRotorConfigurationKnowingInitialPositionsAndReflector(char[] ciphertext,
                                                                                              AvailableRotors rotors,
                                                                                              String plugBoard,
                                                                                              Integer[] initialConfigurations,
                                                                                              String reflectorUsed,
                                                                                              FitnessFunction function,
                                                                                              Integer iterations) {

        //! Utilizamos el mismo metodo para producir la lista de rotores a revisar. Usamos una lista sincronizada para
        //! analisis paralelo
        List<String[]> threeRotorCombinations = getThreeRotorCombinations(getAvailableRotorList(rotors));
        final List<ScoredEnigmaKey> keySet = Collections.synchronizedList(new ArrayList<>());
        int[] defaultRingSettings = new int[]{0, 0, 0};

        //! Analysis paralelo

        threeRotorCombinations.parallelStream().forEach(combination -> {

            //! Creamos una enigma
            EnigmaMachineImplementation implementation = new EnigmaMachineImplementation(combination,
                    reflectorUsed,
                    Arrays.stream(initialConfigurations).mapToInt(Integer::valueOf).toArray(),
                    defaultRingSettings,
                    plugBoard);

            //! Probando configuraciones de rotores, no de muescas
            char[] decripted_text = implementation.encriptadodeCaracter(ciphertext);


            keySet.add(new ScoredEnigmaKey(
                    new EnigmaKey(combination,
                            Arrays.stream(initialConfigurations).mapToInt(Integer::valueOf).toArray(),
                            defaultRingSettings,
                            plugBoard,
                            reflectorUsed),
                    function.score(decripted_text)));
        });

        //! Procesamos con el tamano basado en el del cifrado
        float[] thresholds = determineThresholds(ciphertext.length);
        List<ScoredEnigmaKey> thoseAboveTresh = new ArrayList<>();
        for (final float tresh :
                thresholds) {
            addConfigurationsAboveThreshold(keySet, tresh, thoseAboveTresh);
        }

        return processResults(thoseAboveTresh, iterations);
    }

    //! Implementamos un metodo de busqueda completo solo conociendo el reflector y los rotores usados para encontrar
    //! configuracion completa
    
    
    /**
     * Este metodo encuentra las configuraciones de rotores de la maquina Enigma dadas las combinaciones de rotores
     * especificadas, un reflector inicial conocido, y utilizando una funcion de fitness para evaluar los resultados.
     *
     * @param ciphertext Un arreglo de caracteres que contiene el texto cifrado de entrada.
     * @param rotorsUsed Un arreglo de cadenas que representa las combinaciones de rotores disponibles para el analisis.
     *                   Ejemplo: {"I", "II", "III"}.
     * @param plugboard Una cadena que define la configuracion del plugboard de la maquina Enigma.
     *                  Ejemplo: "AB CD EF".
     * @param reflectorUsed Una cadena que representa el reflector conocido a utilizar. Ejemplo: "B" o "C".
     * @param function Una instancia de la interfaz {@link FitnessFunction}, que puntua las configuraciones
     *                 desencriptadas para encontrar la mas optima.
     * @param iterations Numero maximo de configuraciones que deberan devolverse despues de filtrar los resultados.
     * @return Un arreglo de configuraciones {@link ScoredEnigmaKey} con las combinaciones mas altas
     *         evaluadas segun la funcion de fitness proporcionada.
     *
     * Funcionamiento interno:
     * <ul>
     *   <li>Utiliza {@link #processRotorCombination} para probar todas las configuraciones posibles de rotores, reflector y plugboard seleccionados.</li>
     *   <li>Evalua el texto cifrado contra una funcion de fitness y almacena cada configuracion con su puntaje de evaluacion.</li>
     *   <li>Filtra y ordena los resultados en base al puntaje obtenido y devuelve las mejores configuraciones limitadas por 'iterations'.</li>
     * </ul>
     */
    public static ScoredEnigmaKey[] findRotorConfigurationKnowingInitialReflectorAndRotors(char[] ciphertext,
                                                                                           String[] rotorsUsed,
                                                                                           String plugboard,
                                                                                           String reflectorUsed,
                                                                                           FitnessFunction function,
                                                                                           Integer iterations) {
        //! Usamos el mismo estilo de codigo que los otros metodos para escribir una lista sincronizada
        List<ScoredEnigmaKey> engimaKeyList = Collections.synchronizedList(new ArrayList<>());
        int[] defaultRotorConfiguration = {0, 0, 0};
        int[] defaultRingConfiguration = {0, 0, 0};

        //! Utilizamos funcion helper para iterar sobre las combinaciones posibles
        float[] treshes = determineThresholds(ciphertext.length);
        processRotorCombination(rotorsUsed,
                ciphertext,
                plugboard,
                reflectorUsed,
                function,
                treshes,
                engimaKeyList);

        //! Usando los resultados anteriores, procesamos aquellos mayores
        return processResults(engimaKeyList, iterations);
    }


    /**
     * Este metodo encuentra configuraciones de rotores de la maquina Enigma dado un texto cifrado parcial y su correspondiente
     * texto en claro parcial. Utiliza las combinaciones de rotores predefinidos para generar posibles configuraciones, 
     * evaluandolos para encontrar las mejores opciones basadas en un tamano maximo de iteraciones.
     *
     * @param ciphertext Arreglo de caracteres que representa el texto cifrado parcial que se desea descifrar.
     * @param plaintext Arreglo de caracteres que contiene el texto en claro parcial conocido que corresponde al texto cifrado.
     * @param rotorsUsed Arreglo de cadenas que representa los rotores disponibles para configuraciones. Ejemplo: {"I", "II", "III"}.
     * @param maxIterations Numero maximo de configuraciones que deben ser retornadas despues del procesamiento de resultados.
     * @return Un arreglo de objetos {@link ScoredEnigmaKey} con las configuraciones de rotores evaluadas como las mejores
     *         basado en los criterios proporcionados.
     *
     * Funcionamiento interno:
     * <ul>
     *   <li>Se inicializa una lista sincronizada para almacenar los resultados de configuraciones posibles, 
     *       asegurando thread-safety durante el analisis paralelo.</li>
     *   <li>Se generan umbrales de evaluacion basandose en la longitud del texto cifrado.</li>
     *   <li>Para cada opcion de reflector, se iteran las posiciones de los rotores (de 0 a 25) utilizando tres bucles anidados.</li>
     *   <li>Se crea una instancia de la maquina Enigma para cada combinacion y se descifra el texto cifrado parcial.</li>
     *   <li>El metodo {@link #processRotorPosition} es utilizado para evaluar cada posicion de rotor y almacenar resultados validos.</li>
     *   <li>Finalmente, la lista de configuraciones obtenidas es procesada y retornada utilizando {@link #processResults}
     *       para limitar los resultados a las iteraciones maximas deseadas.</li>
     * </ul>
     */
    public static ScoredEnigmaKey[] findRotorConfigurationKnowingCipherPartTextRotors(char[] ciphertext,
                                                                                      char[] plaintext,
                                                                                      String[] rotorsUsed,
                                                                                      int maxIterations) {
        //! Primer paso, conociendo los rotores generamos la lista sincronica para analisis paralelo
        List<ScoredEnigmaKey> synchroEnigmaResultsList = Collections.synchronizedList(new ArrayList<>());
        int[] defaultRingConfiguration = {0, 0, 0};
        float[] thresholds = determineThresholds(ciphertext.length);
        //! Parallel Iteration with the same machine over processing entries
        reflectorOptions.parallelStream().forEachOrdered(reflectorOptions -> {
            //! Print Default message
            System.out.println("Testing Rotor: " + reflectorOptions);

            //! Sequentially evaluate all posibilities for combinations
            for (int i = 0; i < 26; i++) {
                for (int j = 0; j < 26; j++) {
                    for (int k = 0; k < 26; k++) {
                        //! Create a new enigma machine
                        EnigmaMachineImplementation enigmaMachineImplementation = new EnigmaMachineImplementation(rotorsUsed,
                                reflectorOptions,
                                new int[]{i, j, k},
                                defaultRingConfiguration,
                                "");
                        processRotorPosition(enigmaMachineImplementation,
                                i,
                                j,
                                k,
                                ciphertext,
                                plaintext,
                                rotorsUsed,
                                defaultRingConfiguration, "",
                                synchroEnigmaResultsList,
                                reflectorOptions);
                    }
                }
            }
        });

        //! Third Method For Checking


        return processResults(synchroEnigmaResultsList, maxIterations);
    }


}


