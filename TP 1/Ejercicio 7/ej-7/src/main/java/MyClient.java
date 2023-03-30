import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;


public class MyClient {

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final String URL = "http://localhost:8080/executeRemoteTask";

  private static final OkHttpClient client = new OkHttpClient();
  private static final Gson gson = new Gson();

  public static void main(String[] args) {
    // Crear un objeto TareaEjemplo
    TareaEjemplo tarea = new TareaEjemplo("parametro1", 2, true);

    // Convertir la tarea a un objeto JSON
    String tareaJson = gson.toJson(tarea);

    // Crear una solicitud POST con el objeto JSON en el cuerpo
    RequestBody requestBody = RequestBody.create(JSON, tareaJson);
    Request request = new Request.Builder()
        .url(URL)
        .post(requestBody)
        .build();

    // Enviar la solicitud y procesar la respuesta
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      System.out.println(response.body().string());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

class TareaEjemplo implements TareaGenerica {

  private String parametro1;
  private int parametro2;
  private boolean parametro3;

  public TareaEjemplo(String parametro1, int parametro2, boolean parametro3) {
    this.parametro1 = parametro1;
    this.parametro2 = parametro2;
    this.parametro3 = parametro3;
  }

  // Implementar el método de la interfaz TareaGenerica
  public String ejecutar() {
    // Realizar la operatoria con los parámetros recibidos
    return "Se ejecutó la tarea con los parámetros " + parametro1 + ", " + parametro2 + " y " + parametro3;
  }

  // Getters y setters

}

