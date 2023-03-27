import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MultiClientServer {
    private Map<String, Queue<Message>> queues;
    private ServerSocket serverSocket;

    public MultiClientServer(int port) throws IOException, JMSException {
        queues = new HashMap<>();
        serverSocket = new ServerSocket(port);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createTopic("Messages");

        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String queueName = message.getStringProperty("QueueName");
                    if (!queues.containsKey(queueName)) {
                        queues.put(queueName, new LinkedList<>());
                    }
                    Queue<Message> queue = queues.get(queueName);
                    queue.offer(message);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, queues);
            handler.start();
        }
    }

    public static void main(String[] args) throws IOException, JMSException {
        MultiClientServer server = new Server(12345);
        server.start();
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private Map<String, Queue<Message>> queues;

    public ClientHandler(Socket clientSocket, Map<String, Queue<Message>> queues) {
        this.clientSocket = clientSocket;
        this.queues = queues;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            while (true) {
                String queueName = in.readUTF();
                Queue<Message> queue = queues.get(queueName);
                if (queue != null) {
                    Message message = queue.poll();
                    if (message != null) {
                        out.writeObject(message);
                    } else {
                        out.writeObject(null);
                    }
                } else {
                    out.writeObject(null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

