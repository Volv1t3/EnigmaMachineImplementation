import com.evolvlabs.enigmaDecriptor.*;
import com.evolvlabs.enigmabackend.EnigmaMachineImplementation;
import com.evolvlabs.enigmabackend.PlugBoardImplementation;
import com.evolvlabs.enigmabackend.ReflectorImplementation;
import com.evolvlabs.enigmabackend.RotorImplementation;
import com.evolvlabs.enigmamachine.EnigmaThroughConsole;
import io.github.kamilszewc.javaansitextcolorizer.Colorizer;
import org.apache.commons.math3.genetics.Fitness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnigmaDecriptionTest {

    private final FitnessFunction[] fullLengthFunctions = {new CombinedFrequencyFitnessFunction(),new ImprovedNGram(), new SinglegramFitnessFunction(),new BigramFitnessFunction(), new TrigramFitnessFunction(), new QuagramFitnessFunction(), new IoCFitnessFunction()};
    private final FitnessFunction[] shortFunctions = {new CombinedFrequencyFitnessFunction(),new ImprovedNGram(), new SinglegramFitnessFunction(), new IoCFitnessFunction(), new BigramFitnessFunction()};



    @Test
    @DisplayName("Test Suite 2 | Test # 2.1 | Testing For Basic Configuration (I, II, III)")
    @Tag("TestSuite2")
    public void testFindRotorConfiguration_case1() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTests.csv"))) {
            // Skip header
            bufferedReader.readLine();
            String readLine;

            // Keep track of success rates
            int totalTests = 0;
            Map<String, Integer> successByFunction = new HashMap<>();
            Map<Integer, Integer> successByLength = new HashMap<>();

            while ((readLine = bufferedReader.readLine()) != null) {
                String[] partitionedString = readLine.split(",");
                String plaintext = partitionedString[0];
                int[] arr1 = Arrays.stream(partitionedString[2].split(" ")).mapToInt(Integer::parseInt).toArray();
                int[] arr2 = Arrays.stream(partitionedString[3].split(" ")).mapToInt(Integer::parseInt).toArray();

                EnigmaMachineImplementation machineImplementation = new EnigmaMachineImplementation(
                        new String[]{"I","II","III"},
                        "B",
                        arr1,
                        arr2,
                        "");

                char[] ciphertext = machineImplementation.encriptadodeCaracter(plaintext.toUpperCase().toCharArray());
                totalTests++;

                // Choose fitness functions based on text length and characteristics
                List<FitnessFunction> selectedFunctions = selectFitnessFunctions(plaintext.length());

                boolean found = false;
                for (FitnessFunction function : selectedFunctions) {
                    var arrayResult = Collosus.findRotorConfiguration(
                            ciphertext, Collosus.AvailableRotors.THREE, "", 100, function);

                    for (ScoredEnigmaKey key : arrayResult) {
                        if (Arrays.equals(key.rotors, new String[]{"I", "II", "III"}) &&
                                Arrays.equals(key.indicators, arr1)) {

                            // Record success
                            String functionName = function.getClass().getSimpleName();
                            successByFunction.merge(functionName, 1, Integer::sum);
                            successByLength.merge(plaintext.length(), 1, Integer::sum);

                            System.out.println("\nSuccess for: " + plaintext);
                            System.out.println("Length: " + plaintext.length());
                            System.out.println("Function: " + functionName);
                            System.out.println("Rotor Configuration: " + Arrays.toString(key.rotors));
                            System.out.println("Indicators: " + Arrays.toString(key.indicators));

                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }

                if (!found) {
                    System.out.println("\nFailed to find configuration for: " + plaintext);
                    System.out.println("Length: " + plaintext.length());
                }
            }

            // Print statistics
            System.out.println("\n=== Test Statistics ===");
            System.out.println("Total tests: " + totalTests);
            System.out.println("\nSuccess by function:");
            int finalTotalTests = totalTests;
            successByFunction.forEach((function, count) ->
                    System.out.printf("%s: %.2f%%\n", function, (count * 100.0 / finalTotalTests)));

            System.out.println("\nSuccess by text length:");
            int finalTotalTests1 = totalTests;
            successByLength.forEach((length, count) ->
                    System.out.printf("Length %d: %.2f%%\n", length, (count * 100.0 / finalTotalTests1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<FitnessFunction> selectFitnessFunctions(int textLength) {
        List<FitnessFunction> functions = new ArrayList<>();

        // Always try ImprovedNGram first
        functions.add(new ImprovedNGram());

        if (textLength <= 3) {
            functions.add(new SinglegramFitnessFunction());
            functions.add(new IoCFitnessFunction());
        } else if (textLength <= 5) {
            functions.add(new BigramFitnessFunction());
            functions.add(new SinglegramFitnessFunction());
            functions.add(new IoCFitnessFunction());
        } else {
            functions.add(new TrigramFitnessFunction());
            functions.add(new BigramFitnessFunction());
            functions.add(new QuagramFitnessFunction());
            functions.add(new IoCFitnessFunction());
        }

        return functions;
    }

    @Test
    @DisplayName("Test Suite 2 | Test # 2.2 | Testing For Basic Configuration (II, III, IV)")
    @Tag("TestSuite2")
    public void testFindRotorConfiguration_case2() {
        try(
                FileReader fileReader = new FileReader("./src/main/resources/basicConfigurationTestsTwo.csv");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
        )
        {
            //! Reading Each Line From The File Sequentially.
            String readLine;
            bufferedReader.readLine();
            while((readLine = bufferedReader.readLine()) != null){
                String[] partitionedString = readLine.split(",");
                int[] arr1 = Arrays.stream(partitionedString[2].split(" ")).mapToInt(Integer::parseInt).toArray();
                int[] arr2 = Arrays.stream(partitionedString[3].split(" ")).mapToInt(Integer::parseInt).toArray();
                EnigmaMachineImplementation machineImplementation = new EnigmaMachineImplementation(
                        new String[]{"I","II","III"},
                        "B",
                        arr1,
                        arr2,
                        "");

                String plaintext = partitionedString[0];
                char[] ciphertext = machineImplementation.encriptadodeCaracter(plaintext.toUpperCase().toCharArray());

                boolean keyFound = false;
                if (plaintext.length() < 3){
                    for(FitnessFunction function: this.shortFunctions) {
                        var arrayResult = Collosus.findRotorConfiguration(
                                ciphertext, Collosus.AvailableRotors.FIVE, "", 100, function);
                        if (keyFound){continue;}
                        for(ScoredEnigmaKey key : arrayResult){
                            if (Arrays.equals(key.rotors, new String[]{"I", "II", "III"}) && Arrays.equals(key.indicators, arr1) ) {
                                System.out.println("Configuration found for the following key");
                                System.out.println("key.rotors = " + Arrays.toString(key.rotors));
                                System.out.println("key.indicators = " + Arrays.toString(key.indicators));
                                System.out.println("key.rings = " + Arrays.toString(key.rings));
                                System.out.println("function = " + function.getClass().getCanonicalName());
                                keyFound = true;
                            }
                        }
                    }
                }
                else {
                    for(FitnessFunction function: this.fullLengthFunctions) {
                        var arrayResult = Collosus.findRotorConfiguration(
                                ciphertext, Collosus.AvailableRotors.FIVE, "", 100, function);
                        if (keyFound){continue;}
                        for(ScoredEnigmaKey key : arrayResult){
                            if (Arrays.equals(key.rotors, new String[]{"I", "II", "III"}) && Arrays.equals(key.indicators, arr1)) {
                                System.out.println("Configuration found for the following key");
                                System.out.println("ciphertext = " + Arrays.toString(ciphertext));
                                System.out.println("plaintext = " + plaintext);
                                System.out.println("key.rotors = " + Arrays.toString(key.rotors));
                                System.out.println("key.indicators = " + Arrays.toString(key.indicators));
                                System.out.println("key.rings = " + Arrays.toString(key.rings));
                                System.out.println("function = " + function.getClass().getCanonicalName());
                                keyFound = true;
                            }
                        }
                    }
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
