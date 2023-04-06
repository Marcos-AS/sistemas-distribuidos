package model;
import java.util.HashMap;

public class TareaGenerica {
    private String name;
    private String fullContainerImage;
    private String apiPath;
    private String methodPath;
    private HashMap<String, String> parameters = new HashMap<String, String>();

    public String getTaskName() {
        return this.name;
    }

    public String getFullContainerImage() {
        return this.fullContainerImage;
    }

    public String getApiPath() {
        return this.apiPath;
    }

    public String getMethodPath() {
        return this.methodPath;
    }

    public HashMap<String, String> getParameters() {
        return this.parameters;
    }

    public TareaGenerica(String taskName, String fullContainerImage, HashMap<String, String> parameters) {
        this.name = taskName;
        this.fullContainerImage = fullContainerImage;
        this.parameters = parameters;
    }  

}
