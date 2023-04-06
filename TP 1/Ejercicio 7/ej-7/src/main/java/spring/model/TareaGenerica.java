package spring.model;

import java.io.Serializable;
import java.util.HashMap;

public class TareaGenerica implements Serializable {

    private Long id;
    private Long ClientID;
    private String taskName;
    private String fullContainerImage;
    private String apiPath;
    private String methodPath;
    private HashMap<String, String> parameters = new HashMap<String, String>();


    public TareaGenerica() {

    }

    public String getMethodPath() {
        return this.methodPath;
    }

    public void setMethodPath(String methodPath) {
        this.methodPath = methodPath;
    }
    

    public String getApiPath() {
        return this.apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
    

    public Long getClientID() {
        return this.ClientID;
    }

    public void setClientID(Long ClientID) {
        this.ClientID = ClientID;
    }

    
    
    public TareaGenerica(String taskName, String fullContainerImage, HashMap<String, String> parameters) {
        this.taskName = taskName;
        this.fullContainerImage = fullContainerImage;
        this.parameters = parameters;
    }   

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFullContainerImage() {
        return this.fullContainerImage;
    }

    public void setFullContainerImage(String fullContainerImage) {
        this.fullContainerImage = fullContainerImage;
    }

    public HashMap<String,String> getParameters() {
        return this.parameters;
    }

    public void setParameters(HashMap<String,String> parameters) {
        this.parameters = parameters;
    }



}
