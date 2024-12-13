package com.evolvlabs.enigmaDecriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author : Santiago Arellano
 * @Date : December 3rd, 2024
 * @Description : El presente archivo muestra la implementacion de una clase que provee metodos para realizar un
 * analisis bigrama sobre un texto, es decir, usamos un analysis de bigramas para obtener en base a pares de letras
 * una posible aproximacion al resultado. EL resultado se retorna a traves de la funcion score(char[] text), a la cual
 * se le envia el valor de un texto.
 * @TheoreticalComponents : La base teorica de ests metodos se puede estudaiar en [1]   
 */
public class BigramFitnessFunction implements FitnessFunction{

    private float[] e_BiGrams;

    
    /**
     * <p>Este metodo calcula el indice de un bigrama basado en dos parametros enteros (a y b).</p>
     * <p>Ambos parametros representan letras en el rango de A-Z, donde A se corresponde con 0, B con 1, y asi sucesivamente.</p>
     * <br>
     * <p>El calculo combina los valores usando operaciones a nivel de bits:</p>
     * <ul>
     *   <li>El numero "a" se desplaza 5 posiciones a la izquierda (a << 5), lo cual equivale a multiplicarlo por 32.</li>
     *   <li>Se aplica una operacion OR entre el valor desplazado de "a" y el valor de "b". Esto genera un indice unico para el bigrama.</li>
     * </ul>
     * <br>
     * <p>Ejemplo: Si a = 1 (representando la letra B) y b = 2 (representando la letra C), el indice se calcula como (1 << 5) | 2 = 32 | 2 = 34.</p>
     * 
     * @param a Representa el indice entero de la primera letra (A-Z, donde A=0, B=1, etc.).
     * @param b Representa el indice entero de la segunda letra (A-Z, donde A=0, B=1, etc.).
     * @return El indice entero unico del bigrama generado.
     */
    private static int biIndex(int a, int b){
        return (a << 5) | b;
    }


    /**
     * <b>Constructor de la clase BigramFitnessFunction.</b><br>
     * <p>Este constructor inicializa el array {@code e_BiGrams} con valores predeterminados y
     * luego carga las frecuencias de bigramas desde un archivo CSV ubicado en
     * {@code src/main/resources/fitnessFunction_BiGrams.csv}.</p>
     *
     * <p>El metodo realiza los siguientes pasos:</p>
     * <ul>
     *   <li>Inicializa {@code e_BiGrams} con tamano 826 y llena todos los valores con el logaritmo
     *       en base 10 del epsilon definido en {@code getEpsilon()}.</li>
     *   <li>Abre el archivo CSV en modo lectura y utiliza un {@code BufferedReader} para leerlo linea a linea.</li>
     *   <li>Divide cada linea en un arreglo de strings, donde:
     *       <ul>
     *           <li>El primer elemento representa un bigrama (por ejemplo, "AB").</li>
     *           <li>El segundo elemento representa la frecuencia asociada a ese bigrama.</li>
     *       </ul>
     *   </li>
     *   <li>Convierte cada bigrama a su indice correspondiente utilizando el metodo {@code biIndex},
     *       donde las letras son transformadas en enteros (A-Z corresponden a los indices 0-25).</li>
     *   <li>Almacena el valor convertido de frecuencia en el indice calculado dentro de {@code e_BiGrams}.</li>
     * </ul>
     *
     * <p>Si ocurre alguna excepcion, se imprime la pila de errores y se lanza una {@code RuntimeException}.</p>
     */
    public BigramFitnessFunction() {
        //! Calculando los Bigramas posibles
        this.e_BiGrams = new float[826];
        Arrays.fill(this.e_BiGrams, (float) Math.log10(getEpsilon()));
        try (
                final Reader reader = new FileReader("src/main/resources/fitnessFunction_BiGrams.csv");
                final BufferedReader bufferedReader = new BufferedReader(reader);
                final Stream<String> lines = bufferedReader.lines()) {
            lines.map(lns -> lns.split(",")).forEach(strings -> {
                String key = strings[0];
                int i = biIndex(key.charAt(0) - 65, key.charAt(1) - 65);
                this.e_BiGrams[i] = Float.parseFloat(strings[1]);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Este metodo calcula el puntaje (fitness) basado en un analisis de bigramas en un texto determinado.
     * El texto es evaluado caracter por caracter, considerando las parejas de letras consecutivas
     * para calcular un puntaje acumulativo.
     * <br>
     * <p>Funcionamiento interno:</p>
     * <ul>
     *     <li>El metodo itera sobre el texto caracter por caracter, ignorando cualquier caracter que 
     *         no este en el rango de letras A-Z (de 0 a 25 al usar sus indices correspondents).</li>
     *     <li>Los valores asociados a cada bigrama se extraen del arreglo {@code e_BiGrams},
     *         utilizando el indice generado por el metodo {@code biIndex} para calcular el puntaje acumulado.</li>
     *     <li>Se lleva un contador para determinar el numero de bigramas validos sumados.</li>
     *     <li>Si no hay bigramas validos (contador = 0), el metodo devuelve {@code Float.NEGATIVE_INFINITY}.</li>
     *     <li>Si hay bigramas validos, se calcula un promedio ponderado del puntaje acumulado,
     *         ajustado con un factor basado en el numero de bigramas encontrados.</li>
     * </ul>
     *
     * @param text Un arreglo de caracteres representando el texto a analizar. Cada caracter debe
     *             corresponder a letras en el rango de 'A' a 'Z'.
     * @return El puntaje (fitness) calculado como un promedio ponderado del puntaje de bigramas, o 
     *         {@code Float.NEGATIVE_INFINITY} si no se identificaron bigramas validos.
     */
    @Override
    public float score(char[] text) {
        float fitness = 0;
        float count = 0;
        int current = 0;
        int next = text[0] - 65;
        for(int i = 1; i < text.length; i++){
            current = next;
            next = text[i] -65;
            if (current < 0 || current >= 26 || next < 0 || next >= 26) continue;
    
            fitness += this.e_BiGrams[biIndex(current, next)];
            count++;
        }
    
        if (count == 0) return Float.NEGATIVE_INFINITY;
        return (fitness / count)* (1.0f + Math.min(0.4f, count/8.0f));
    }
}
