package spring;

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
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);

        // Iniciar nodo maestro
        //List<Nodo> extremos = new ArrayList<>();
        //Nodo nodo1 = new Nodo("localhost", 8081);
        //Nodo nodo2 = new Nodo("localhost", 8082);
        //extremos.add(nodo1);
        //extremos.add(nodo2);
        Maestro maestro = new Maestro("localhost", 9000);
        maestro.iniciar();

        // Iniciar nodo extremo 1
        List<Maestro> maestros = new ArrayList<>();
       // Nodo nodo3 = new Nodo("localhost", 8083);     
        maestros.add(maestro);   
        Extremo extremo1 = new Extremo(maestros, "localhost", 8082 );
        extremo1.iniciar();

        // Iniciar nodo extremo 2
        Extremo extremo2 = new Extremo(maestros, "localhost", 8083 );
        extremo2.iniciar();

        maestro.mostrarRecursos();
    }
}
