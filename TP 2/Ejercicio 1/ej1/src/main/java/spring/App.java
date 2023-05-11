package spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import spring.model.Extremo;
import spring.model.Maestro;
import spring.model.Nodo;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        SpringApplication.run(App.class, args);
        //String rutaParaExtremo = "C:/Users/leo_2/OneDrive/Documentos/GitHub/sistemas-distribuidos/TP 2/Ejercicio 1/ej1/src/main/java/spring/model/";
        String rutaParaExtremo1 = "C:/Users/leo_2/OneDrive/Documentos/GitHub/sistemas-distribuidos/TP 2/Ejercicio 1/ej1/src/main/java/spring/model";
        String rutaParaExtremo2 = "C:/Users/leo_2/OneDrive/Documentos/GitHub/sistemas-distribuidos/TP 2/Ejercicio 1/ej1/src/main/java/spring/controller";

        // Iniciar nodo maestro
        //List<Nodo> extremos = new ArrayList<>();
        //Nodo nodo1 = new Nodo("localhost", 8081);
        //Nodo nodo2 = new Nodo("localhost", 8082);
        //extremos.add(nodo1);
        //extremos.add(nodo2);
        //Maestro maestro = new Maestro("localhost", 8080);
        
        // Iniciar nodo extremo 1
        List<Nodo> maestros = new ArrayList<>();
       // Nodo nodo3 = new Nodo("localhost", 8083);     
        //maestros.add(maestro); 
        maestros.add(new Nodo("localhost",8080));
        Extremo extremo1 = new Extremo(maestros, "localhost", 8084, rutaParaExtremo1);
        extremo1.iniciar();

        // Iniciar nodo extremo 2
        Extremo extremo2 = new Extremo(maestros, "localhost", 8085, rutaParaExtremo2);
        extremo2.iniciar();

        //maestro.mostrarRecursos();

        //extremo1.consultar("MasterServerController.java");
    }
}
