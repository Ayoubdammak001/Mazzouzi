import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket socket;
	private ServerMain server_main;
	private String clientName;
	private boolean broadcastMode = false; // Flag to check if client is in broadcast mode

	public ServerThread(Socket socket, ServerMain server_main) {
		this.socket = socket;
		this.server_main = server_main;
	}

	@Override
	public void run() {
		try {
			int client_number = server_main.getClientNumber();

			System.out.println("Client " + client_number + " has connected.");

			// I/O buffers
			BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			// Ask the client for their name
			out_socket.println("Welcome! You are client number " + client_number + ". What's your name?");
			clientName = in_socket.readLine();
			server_main.addClient(clientName, this); // Add client to the list
			System.out.println("Client " + client_number + " is named: " + clientName);

			// Loop for chat
			while (true) {
				if (!broadcastMode) {
					out_socket.println("Hello " + clientName + ", what do you want to say?");
				} else {
					out_socket.println("You are in broadcast mode. Your message will be sent to all clients.");
				}

				String clientMessage = in_socket.readLine();

				if (clientMessage == null || clientMessage.equalsIgnoreCase("exit")) {
					break;
				} else if (clientMessage.equalsIgnoreCase("list")) {
					// If the client types "list", show all connected clients
					out_socket.println(server_main.getClientsList());
				} else if (clientMessage.equalsIgnoreCase("all")) {
					// Switch to broadcast mode
					broadcastMode = true;
					out_socket.println("You are now in broadcast mode. All your messages will be sent to all clients.");
				} else if (clientMessage.equalsIgnoreCase("private")) {
					// Switch back to private mode
					broadcastMode = false;
					out_socket.println("You are now in private mode. Messages will only be sent to the server.");
				} else if (clientMessage.startsWith("@")) {
					// Private message logic
					String[] parts = clientMessage.split(" ", 2);
					if (parts.length < 2) {
						out_socket.println("Invalid private message format. Use @ClientName message.");
						continue;
					}
					String targetClient = parts[0].substring(1); // Extract client name
					String privateMessage = parts[1]; // Extract message

					ServerThread targetThread = server_main.getClientThread(targetClient);

					if (targetThread != null) {
						targetThread.sendMessage("Private message from " + clientName + ": " + privateMessage);
						out_socket.println("Private message sent to " + targetClient + ".");
					} else {
						out_socket.println("Client " + targetClient + " not found.");
					}
				} else {
					if (broadcastMode) {
						// If in broadcast mode, send the message to all clients including logging on the server
						System.out.println("Broadcast from " + clientName + ": " + clientMessage); // Log on server
						server_main.broadcastMessage(clientMessage, clientName); // Send to all clients
					} else {
						// Normal private message to server
						System.out.println("Client " + clientName + " says: " + clientMessage);
					}
				}
			}

			// Remove client from the list and close socket
			server_main.removeClient(clientName);
			socket.close();
			System.out.println("Client " + clientName + " has disconnected.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getter for clientName
	public String getClientName() {
		return clientName;
	}

	// Method to send a message to this client
	public void sendMessage(String message) {
		try {
			PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			out_socket.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
