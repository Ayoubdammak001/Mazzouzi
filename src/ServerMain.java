import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {

    // Synchronized map to store clients' names and their corresponding threads
    private final Map<String, ServerThread> clients = Collections.synchronizedMap(new HashMap<>());

    public ServerMain() throws Exception {
        ServerSocket server_socket = new ServerSocket(2021);
        System.out.println("Port 2021 is now open.");

        // Infinite while loop: wait for new connections
        while (true) {
            Socket socket = server_socket.accept();
            ServerThread server_thread = new ServerThread(socket, this);
            Thread thread = new Thread(server_thread);
            thread.start();
        }
    }

    private int clientnumber = 1;

    public int getClientNumber() {
        return clientnumber++;
    }

    // Method to add a client to the list
    public void addClient(String clientName, ServerThread serverThread) {
        clients.put(clientName, serverThread);
    }

    // Method to remove a client from the list
    public void removeClient(String clientName) {
        clients.remove(clientName);
    }

    // Method to return a list of connected clients
    public String getClientsList() {
        synchronized (clients) {
            if (clients.isEmpty()) {
                return "No clients are currently connected.";
            } else {
                return "Connected clients: " + String.join(", ", clients.keySet());
            }
        }
    }

    // Method to send a message to all clients except the sender
    public void broadcastMessage(String message, String fromClient) {
        synchronized (clients) {
            for (ServerThread clientThread : clients.values()) {
                if (clientThread.getClientName().equals(fromClient)) {
                    continue; // Skip the sender to avoid self-message
                }
                clientThread.sendMessage("Message from " + fromClient + ": " + message);
            }
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
