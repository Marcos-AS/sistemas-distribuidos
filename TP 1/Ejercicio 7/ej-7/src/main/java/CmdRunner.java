import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CmdRunner {
    
    String os ;
    Map<String, String> envs;

    public CmdRunner () throws Exception {
        this.os = System.getProperty("os.name").toLowerCase();
    }

    public String runCommand(String path, String command) throws Exception {
        System.out.println("Running in: " + path);
        System.out.println("Command: " + command);

        
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(path));
    

        if (this.os.startsWith("Windows")){
            builder.command("cmd.exe", "/c", command); 
        }else{
            builder.command("sh", "-c", command);
        }
        

        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        String strResponseMessage = printStream(inputStream).trim();
        
        printStream(errorStream);

        boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished) {
            process.destroyForcibly();
        }

        return strResponseMessage;
    }

    private static String printStream(InputStream inputStream) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String totalLines = "";
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                totalLines+=line;
            }
            return totalLines;

        }
    }

    
}
