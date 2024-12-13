import com.evolvlabs.enigmabackend.EnigmaMachineImplementation;
import com.evolvlabs.enigmabackend.PlugBoardImplementation;
import com.evolvlabs.enigmabackend.ReflectorImplementation;
import com.evolvlabs.enigmabackend.RotorImplementation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class EnigmaBackendTest {

    private final RotorImplementation rotorI = RotorImplementation.createRotor("I",
            0,
            0);
    private final RotorImplementation rotorII = RotorImplementation.createRotor("II",
            0,
            0);
    private final RotorImplementation rotorIII = RotorImplementation.createRotor("III",
            0,
            0);
    private final ReflectorImplementation reflectB = ReflectorImplementation.createReflector("B");
    private EnigmaMachineImplementation enigmaMachine;
    @Test
    @DisplayName("Test Suite #1 | Test #1.0 | Basic Configuration (Rotors I, II, III) | Configuracion de Rotor y Ring base")
    @Tag("BasicConfiguration")
    @Tag("PlainTextToCipherText")
    public void testOneDotOneBasicConfiguration(){

        //! Using Basic Configuration to Encrypt the following word
        String plaintext = "Hello World";
        String ciphertext = "ilbda amtaz";
        enigmaMachine = new EnigmaMachineImplementation(rotorI, rotorII, rotorIII, reflectB, new PlugBoardImplementation(""));
        Assertions.assertArrayEquals(ciphertext.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(plaintext.toUpperCase().toCharArray()));

        //! Opening File For Review
        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTests.csv"))){
            String line = br.readLine();

            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                plaintext = inputValues[0];
                ciphertext = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {rotorI, rotorII, rotorIII};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + ciphertext);
                System.out.println("2. Plaintext Cargado de .csv= " + plaintext);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                Assertions.assertArrayEquals(ciphertext.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(plaintext.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Suite #1 | Test #1.1 | Basic Configuration (Rotors II, III, IV) | Configuracion de Rotor y Ring base")
    @Tag("BasicConfiguration")
    @Tag("PlainTextToCipherText")
    public void testOneDotTwoBasicConfiguration(){
        //! Configure internal enigma machine
        this.enigmaMachine = new EnigmaMachineImplementation(RotorImplementation.createRotor("II",0,0), RotorImplementation.createRotor("III",0,0),
                RotorImplementation.createRotor("IV", 0,0),this.reflectB,new PlugBoardImplementation(""));

        //! Opening File For Review
        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTestsTwo.csv"))){
            String line = br.readLine();

            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                String plaintext = inputValues[0];
                String ciphertext = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {this.enigmaMachine.getE_rotorIzquierdo(), this.enigmaMachine.getE_rotorMedio(), this.enigmaMachine.getE_rotorDerecho()};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + ciphertext);
                System.out.println("2. Plaintext Cargado de .csv= " + plaintext);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                Assertions.assertArrayEquals(ciphertext.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(plaintext.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Suite #1 | Test #1.2 | Basic Configuration (Rotors III, IV, V) | Configuracion de Rotor y Ring base")
    @Tag("BasicConfiguration")
    @Tag("PlainTextToCipherText")
    public void testOneDotThreeBasicConfiguration(){
        //! Define new enigma machine
        this.enigmaMachine = new EnigmaMachineImplementation(RotorImplementation.createRotor("III", 0, 0), RotorImplementation.createRotor("IV", 0, 0),
                RotorImplementation.createRotor("V", 0, 0),this.reflectB,new PlugBoardImplementation(""));

        //! Opening File For Review
        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTestsThree.csv"))){
            String line = br.readLine();
            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                String plaintext = inputValues[0];
                String ciphertext = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {this.enigmaMachine.getE_rotorIzquierdo(), this.enigmaMachine.getE_rotorMedio(), this.enigmaMachine.getE_rotorDerecho()};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + ciphertext);
                System.out.println("2. Plaintext Cargado de .csv= " + plaintext);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                Assertions.assertArrayEquals(ciphertext.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(plaintext.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Suite #2 | Test #2.1 | Basic Configuration (Rotors I, II, III) | Configuracion de Rotor y Ring base")
    @Tag("BasicConfiguration")
    @Tag("CipherTextToPlainText")
    public void testTwoDotOneBasicConfiguration(){
        String cipherText;
        String plainText;

        this.enigmaMachine = new EnigmaMachineImplementation(rotorI,rotorII,rotorIII, reflectB, new PlugBoardImplementation(""));
        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTests.csv"))){
            String line = br.readLine();

            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                plainText = inputValues[0];
                cipherText = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {rotorI, rotorII, rotorIII};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + cipherText);
                System.out.println("2. Plaintext Cargado de .csv= " + plainText);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                //! Probamos lo opuesto aqui, si tenemos el plaintext y el ciphertext, si mando ciphertext tengo que obtener el char[] del plaintext
                Assertions.assertArrayEquals(plainText.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(cipherText.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Suite #2 | Test #2.2 | Basic Configuration (Rotors II, III, IV) | Configuracion de Rotor y Ring Base")
    @Tag("BasicConfiguration")
    @Tag("CipherTextToPlainText")
    public void testTwoDotTwoBasicConfiguration(){
        String cipherText;
        String plainText;

        this.enigmaMachine = new EnigmaMachineImplementation(RotorImplementation.createRotor("II", 0, 0), RotorImplementation.createRotor("III", 0, 0),
                RotorImplementation.createRotor("IV", 0, 0),this.reflectB,new PlugBoardImplementation(""));

        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTestsTwo.csv"))){
            String line = br.readLine();

            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                plainText = inputValues[0];
                cipherText = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {this.enigmaMachine.getE_rotorIzquierdo(), this.enigmaMachine.getE_rotorMedio(), this.enigmaMachine.getE_rotorDerecho()};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + cipherText);
                System.out.println("2. Plaintext Cargado de .csv= " + plainText);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                //! Probamos lo opuesto aqui, si tenemos el plaintext y el ciphertext, si mando ciphertext tengo que obtener el char[] del plaintext
                Assertions.assertArrayEquals(plainText.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(cipherText.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test Suite #2 | Test #2.3 | Basic Configuration (Rotors III, IV, V) | Configuracion de Rotor y Ring Base")
    @Tag("BasicConfiguration")
    @Tag("CipherTextToPlainText")
    public void testTwoDotThreeBasicConfiguration(){
        String cipherText;
        String plainText;

        this.enigmaMachine = new EnigmaMachineImplementation(RotorImplementation.createRotor("III", 0, 0), RotorImplementation.createRotor("IV", 0, 0),
                RotorImplementation.createRotor("V", 0, 0),this.reflectB,new PlugBoardImplementation(""));

        try(BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/basicConfigurationTestsThree.csv"))){
            String line = br.readLine();

            //! Execute variations based on the loaded values.
            while ((line = br.readLine()) != null){
                String[] inputValues = line.split(",");
                //? the structure follows [plaintext][ciphertext][rotor configuration][ring configuration]
                plainText = inputValues[0];
                cipherText = inputValues[1];
                var tempArray = inputValues[2].split(" ");
                var tempArrayTwo = inputValues[3].split(" ");
                RotorImplementation[] holder = {this.enigmaMachine.getE_rotorIzquierdo(), this.enigmaMachine.getE_rotorMedio(), this.enigmaMachine.getE_rotorDerecho()};
                for(int i = 0 ; i < tempArray.length; i++){
                    holder[i].setE_rotorPosition(Integer.parseInt(tempArray[i]));
                    holder[i].setE_RingSetting(Integer.parseInt(tempArrayTwo[i]));
                }
                System.out.println("1. Ciphertext Cargado de .csv = " + cipherText);
                System.out.println("2. Plaintext Cargado de .csv= " + plainText);
                System.out.println("3. Rotor Configuration Cargado de .csv= " + Arrays.toString(tempArray));
                System.out.println("4. Ring Configuration Cargado de .csv= " + Arrays.toString(tempArrayTwo));
                //! Probamos lo opuesto aqui, si tenemos el plaintext y el ciphertext, si mando ciphertext tengo que obtener el char[] del plaintext
                Assertions.assertArrayEquals(plainText.toUpperCase().toCharArray(), enigmaMachine.encriptadodeCaracter(cipherText.toUpperCase().toCharArray()));
                System.out.println("Ingreso de Datos Correcto, Unit Test Pasado Para Configuracion Anterior\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLinearProgressionForEncryption(){
        this.enigmaMachine = new EnigmaMachineImplementation(RotorImplementation.createRotor("I",0,0),
                RotorImplementation.createRotor("II",0,0),
                RotorImplementation.createRotor("III",0,0),
                ReflectorImplementation.createReflector("B"), new PlugBoardImplementation(""));

        var plaintext = "class";
        System.out.println("Plaintext 'class' <=> ciphertext " + Arrays.toString(this.
                enigmaMachine
                .encriptadodeCaracter(plaintext.toUpperCase().toCharArray())));
        System.err.println(this.enigmaMachine.getE_rotorIzquierdo().getE_rotorPosition());
        System.err.println(this.enigmaMachine.getE_rotorMedio().getE_rotorPosition());
        System.err.println(this.enigmaMachine.getE_rotorDerecho().getE_rotorPosition());
        var plaintext2 = "if";
        System.out.println("Plaintext 'if' <=> ciphertext " + Arrays.toString(this.
                enigmaMachine
                .encriptadodeCaracter(plaintext2.toUpperCase().toCharArray())));
        System.err.println(this.enigmaMachine.getE_rotorIzquierdo().getE_rotorPosition());
        System.err.println(this.enigmaMachine.getE_rotorMedio().getE_rotorPosition());
        System.err.println(this.enigmaMachine.getE_rotorDerecho().getE_rotorPosition());
    }

    private String[] readLine(String line){
        return line.split(",");
    }

}