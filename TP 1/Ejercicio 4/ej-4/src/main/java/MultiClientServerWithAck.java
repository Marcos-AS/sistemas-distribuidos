import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServerWithAck {

    private static final String QUEUE_NAME = "messageQueue";
    private static final String BROKER_URL = "tcp://localhost:61616";

    private HashMap<String, MessageConsumer> consumers = new HashMap();

    public void start() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE_NAME);

        MessageProducer producer = session.createProducer(queue);

        ServerSocket serverSocket = new ServerSocket(1234);
        ExecutorService executor = Executors.newCachedThreadPool();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(new ClientHandler(clientSocket, session, producer));
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private Session session;
        private MessageProducer producer;

        public ClientHandler(Socket clientSocket, Session session, MessageProducer producer) {
            this.clientSocket = clientSocket;
            this.session = session;
            this.producer = producer;
        }

        public void run() {
            try {
                ObjectMessage message = (ObjectMessage) session.createMessage();
                message.setObject(clientSocket.getInetAddress().getHostAddress());
                producer.send(message);

                MessageConsumer consumer = session.createConsumer(session.createTemporaryQueue());
                consumers.put(clientSocket.getInetAddress().getHostAddress(), consumer);

                while (true) {
                    ObjectMessage receivedMessage = (ObjectMessage) consumer.receive();
                    String messageText = receivedMessage.getStringProperty("message");
                    System.out.println("Received message: " + messageText);

                    MessageProducer replyProducer = session.createProducer(receivedMessage.getJMSReplyTo());
                    TextMessage replyMessage = session.createTextMessage();
                    replyMessage.setText("ACK");
                    replyProducer.send(replyMessage);

                    if (messageText.equals("END")) {
                        consumers.remove(clientSocket.getInetAddress().getHostAddress());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MultiClientServerWithAck server = new MultiClientServerWithAck();
        server.start();
    }
}
