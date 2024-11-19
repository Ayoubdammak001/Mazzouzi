import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe pour gérer un client connecté au serveur.
 */
public class Client {
	public Client() throws Exception {
		Socket socket = new Socket("localhost", 2021);
		System.out.println("Successful connection to the server.");

		BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		Scanner keyboard = new Scanner(System.in);

		// Thread pour recevoir les messages
		new Thread(() -> {
			try {
				while (true) {
					String serverMessage = in_socket.readLine();
					if (serverMessage == null) break;
					System.out.println("\n" + serverMessage);
				}
			} catch (Exception e) {
				System.out.println("Connection lost: " + e.getMessage());
			}
		}).start();

		System.out.println("Server: " + in_socket.readLine());
		out_socket.println(keyboard.nextLine()); // Envoie du nom au serveur

		while (true) {
			System.out.print("You: ");
			String clientMessage = keyboard.nextLine();
			out_socket.println(clientMessage);

			if (clientMessage.equalsIgnoreCase("exit")) {
				break;
			}
		}

		socket.close();
		System.out.println("Socket closed.");
	}

	public static void main(String[] args) {
		try {
			new Client();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
