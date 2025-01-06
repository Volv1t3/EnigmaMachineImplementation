<body style="font-family: Consolas, sans-serif; font-weight: normal; font-size: 12pt; color: beige">

<blockquote style="font-style: italic; color: whitesmoke"> <blockquote style="font-style: italic; color: whitesmoke; font-size: 9pt; text-align: center"> Hi there! I’m a huge fan of Markdown documents, so apologies in 

advanced for structuring this as one </blockquote>

***

<h3 style="text-align: center; font-size: large"> Enigma Machine Implementation: Extensible, Modular, and Tested Enigma Implementation with Modern Attack Facilities</h3>

<h4 style="text-align: center; font-size: medium"> This is my version of the Enigma Machine with various methods and extensibility options</h4>

***

<div style="display: flex; justify-content: center; align-content: center"> 

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

![JavaFX](https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white)

![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

<blockquote style="font-style: italic; color: whitesmoke">

<h2 style="color: beige; font-size: 14pt">&boxUR; Repository Description &boxUL;  </h2>

<p>Herein lies my adaptation, modification, and extension on the work done by Assistant Professor Mike Pound of the university of Nottingham. His work on the repository linked 
<a href="https://github.com/mikepound/enigma">here</a>, has served as a basis for the extension and improvements I have made to the fitness function methods, and Enigma Machine Implementation.
<br><br>
Although I kept his original structure and most of the enigma machine implementation that he provided, I have modified and included more verbose naming and Javadoc, to allow any user to understand and 
extend the work in both repositories. Moreover, I have added JUnit tests for encryption, decryption, and two java console applications that can be used to test the various methods I have implemented for decryption.
</p>
<br>
<p>The repository is split into three folders<code>.mvn, .idea and src</code>. The first two folders are details related to the buildsystem and the environment I used to develop this code. While the third folder
contains all the information required to make the code run. All classes are held within the folders stored in <code>src/main/java/com.evolvlabs</code>, where each part of the machine and the implementation is 
stored in a relevant package.
<br>
<br>
Here are some important details for those who wish to delve into the files!
</p>
<ul>
<code>File Structure</code>
<li><b>src</b>: As mentioned, contains all files related to the project, it includes tests, backend code, and the runners both for the encryption console application, 
and its decryption counterpart. </li>
</ul>

</blockquote>

***

<blockquote style="font-style: italic; color: whitesmoke">

<h2 style="color: beige; font-size: 14pt">&boxUR; Methodology &boxUL;  </h2>

<p>Within the files stored in the backend of this application, there are various classes separated into three sections, <code>com.evolvlabs.enigmabackend,
com.evolvlabs.enigmaDecriptor. and com.evolvlabs.enigmamachine</code>, these files contain, as their naming suggests, information related to the actual machine implementation, 
the decryption methods, and the visual (to do) and console implementation (done).
<br><br>
Most of the code presented in the <code>com.evolvlabs.enigmabackend</code> package is mirrored from Mike Pounds implementation as I did not notice any need for modification aside from verbose methods,
  variable naming, and Javadoc to make the code readable and to help others understand what each method does directly from the main source code. This means that the encryption process generally behaves in the same way 
  as his implementation did, and is capable of following a sequential and incremental encryption process based on notch rotations.
  <br><br>
  On the other hand, the work done in the package <code>com.evolvlabs.enigmaDecriptor</code> includes both his base implementations and extension, combinations and improvements in the decryption process. It 
  includes his base implementations for single, dual, triple and tetra grams, while adding checks for inputs, and including modifications to make the code readable, and generally behave 
  better when working with my implementation of the Colossus Machine.
  <br>
  <br>
  The colossus machine, included in Collosus.java, presents a series of methods to attempt decryption of a piece of text in the following ways.

</p>

  <ol>
  <li>Full Decryption Attempt: no parameter known, tries all combinations in parallel</li>

  <li>Decryption Attempt: only knowing plaintext</li>

  <li>Decryption Attempt: knowing plaintext, and initial rotor configuration</li>

  <li>Decryption Attempt: knowing initial reflector and rotor configuration</li>

  <li>Decryption Attempt: knowing initial rotor positions and reflector</li>

  </ol>

  <p>The idea behind these methods is to showcase the speed and efficiency of different parameters known, while these methods usually present a good way of decrypting a message, 
  it is important to note the <b>possibilities for improvement in the fitness functions used, implemented methods, and other combinations we can review</b></p>

</blockquote>

***

<blockquote style="font-style: italic; color: whitesmoke">

<h2 style="color: beige; font-size: 14pt">&boxUR; Libraries Used and How to Run &boxUL;  </h2>

  <p>At the moment, the project includes various libraries</p>
  <ul>

  <li>From Junit.org Junit5 for testing</li>

  <li>From OpenJDK.org JavaFX 21 for future UI developments</li>

  <li>From Commons.apache.org Commons CSV, and Math3 for future fitness function developments</li>

  <li>From Github.org Kamilszewc's java-ansi-colorizer for console color printing</li>

  </ul>

  <p>To run the code, you can go ahead and load the project into IntelliJ such that all project folders are correctly structured (remember I have
  various resources required for some fitness functions to work), load the Maven project and allow some time for Maven to sync the project's dependencies. Finally, to run 
  the application outside of the testing environment, head on to <code>com.evolvlabs.enigmamachine</code>, where <b><code>EnigmaThroughConsole.java, and EnigmaDecriptorRunner.java</code></b> 
  files are present for you to test out the implementation</p>

</blockquote>

</body>
