# Dragon Fractal

LWGJL app to view iterations of the dragon fractal

### Building:
A requirement of building is to have the LWJGL jars in the classpath. This can be done by setting the CLASSPATH 
environment variable to include the `jar` folder or setting it during the build:

`javac -cp LWJGL_2.9.1/jar/* DragonFractal.java`

### Running:
In addition to the CLASSPATH, to run the display, the native libraries must be added to the VM

`java -cp .;LWJGL_2.9.1/jar/* -Djava.library.path=./LWJGL_2.9.1/native/<platform>/ DragonFractal`