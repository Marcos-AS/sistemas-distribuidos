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
        List<Nodo> extremos = new ArrayList<>();
        Nodo nodo1 = new Nodo("localhost", 8081);
        Nodo nodo2 = new Nodo("localhost", 8081);
        Maestro maestro = new Maestro(extremos);
        maestro.iniciar();

        // Iniciar nodo extremo
        List<String> maestros = new ArrayList<>();
        maestros.add("localhost:8080");
        Extremo extremo = new Extremo(maestros);
        extremo.iniciar();
    }
}
