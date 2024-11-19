import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Représente un thread pour gérer la communication avec un client.
 */
public class ServerThread implements Runnable {

	private Socket socket; // Socket associé au client
	private ServerMain server_main; // Instance du serveur principal
	private String clientName; // Nom du client
	private boolean broadcastMode = false; // Indique si le client est en mode broadcast

	/**
	 * Constructeur pour initialiser le thread client.
	 *
	 * @param socket       le socket du client
	 * @param server_main  l'instance du serveur principal
	 */
	public ServerThread(Socket socket, ServerMain server_main) {
		this.socket = socket;
		this.server_main = server_main;
	}

	@Override
	public void run() {
		try {
			int client_number = server_main.getClientNumber(); // Numéro unique pour chaque client
			System.out.println("Client " + client_number + " has connected.");

			// Flux de communication
			BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			// Demande du nom du client
			out_socket.println("Welcome! You are client number " + client_number + ". What's your name?");
			clientName = in_socket.readLine();
			server_main.addClient(clientName, this); // Ajoute le client à la liste gérée par le serveur
			System.out.println("Client " + client_number + " is named: " + clientName);

			// Boucle principale de chat
			while (true) {
				if (!broadcastMode) {
					out_socket.println("Hello " + clientName + ", what do you want to say?");
				} else {
					out_socket.println("You are in broadcast mode. Your message will be sent to all clients.");
				}

				String clientMessage = in_socket.readLine(); // Message reçu du client

				// Gestion des commandes spécifiques
				if (clientMessage == null || clientMessage.equalsIgnoreCase("exit")) {
					break; // Déconnexion du client
				} else if (clientMessage.equalsIgnoreCase("list")) {
					out_socket.println(server_main.getClientsList()); // Liste des clients connectés
				} else if (clientMessage.equalsIgnoreCase("all")) {
					broadcastMode = true; // Mode broadcast activé
					out_socket.println("You are now in broadcast mode.");
				} else if (clientMessage.equalsIgnoreCase("private")) {
					broadcastMode = false; // Mode privé activé
					out_socket.println("You are now in private mode.");
				} else if (clientMessage.startsWith("@")) {
					// Envoi d'un message privé à un autre client
					String[] parts = clientMessage.split(" ", 2);
					if (parts.length < 2) {
						out_socket.println("Invalid private message format. Use @ClientName message.");
						continue;
					}
					String targetClient = parts[0].substring(1); // Nom du destinataire
					String privateMessage = parts[1]; // Contenu du message

					ServerThread targetThread = server_main.getClientThread(targetClient);
					if (targetThread != null) {
						targetThread.sendMessage("Private message from " + clientName + ": " + privateMessage);
						out_socket.println("Private message sent to " + targetClient + ".");
					} else {
						out_socket.println("Client " + targetClient + " not found.");
					}
				} else {
					if (broadcastMode) {
						System.out.println("Broadcast from " + clientName + ": " + clientMessage); // Log
						server_main.broadcastMessage(clientMessage, clientName); // Message diffusé à tous
					} else {
						System.out.println("Client " + clientName + " says: " + clientMessage);
					}
				}
			}

			// Déconnexion et nettoyage
			server_main.removeClient(clientName); // Supprime le client de la liste
			socket.close();
			System.out.println("Client " + clientName + " has disconnected.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le nom du client.
	 *
	 * @return le nom du client
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Envoie un message à ce client.
	 *
	 * @param message le message à envoyer
	 */
	public void sendMessage(String message) {
		try {
			PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			out_socket.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
