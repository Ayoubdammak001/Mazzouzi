import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe principale pour gérer les connexions et la communication avec les clients.
 */
public class ServerMain {

    private final Map<String, ServerThread> clients = Collections.synchronizedMap(new HashMap<>());
    private int clientnumber = 1; // Compteur pour identifier les clients

    public ServerMain() throws Exception {
        ServerSocket server_socket = new ServerSocket(2021);
        System.out.println("Port 2021 is now open.");

        while (true) {
            Socket socket = server_socket.accept(); // Accepter les nouvelles connexions
            ServerThread server_thread = new ServerThread(socket, this);
            new Thread(server_thread).start(); // Lancer un thread pour le client
        }
    }

    public int getClientNumber() {
        return clientnumber++;
    }

    public void addClient(String clientName, ServerThread serverThread) {
        clients.put(clientName, serverThread);
    }

    public void removeClient(String clientName) {
        clients.remove(clientName);
    }

    public String getClientsList() {
        synchronized (clients) {
            return clients.isEmpty() ? "No clients are currently connected."
                    : "Connected clients: " + String.join(", ", clients.keySet());
        }
    }

    public void broadcastMessage(String message, String fromClient) {
        synchronized (clients) {
            for (ServerThread clientThread : clients.values()) {
                if (!clientThread.getClientName().equals(fromClient)) {
                    clientThread.sendMessage("Message from " + fromClient + ": " + message);
                }
            }
        }
    }

    public ServerThread getClientThread(String clientName) {
        synchronized (clients) {
            return clients.get(clientName);
        }
    }

    public static void main(String[] args) {
        try {
            new ServerMain();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
