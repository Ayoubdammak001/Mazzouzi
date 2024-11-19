import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public Client() throws Exception {
		// Connect to the server
		Socket socket = new Socket("localhost", 2021);
		System.out.println("Successful connection to the server.");

		// I/O streams
		BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		Scanner keyboard = new Scanner(System.in);

		// Start a thread to handle incoming messages
		new Thread(() -> {
			try {
				while (true) {
					String serverMessage = in_socket.readLine();
					if (serverMessage == null) break; // Server disconnected
					System.out.println("\n" + serverMessage); // Display the message
					System.out.print("You: "); // Keep the prompt
				}
			} catch (Exception e) {
				System.out.println("Connection lost: " + e.getMessage());
			}
		}).start();

		// Send the client's name to the server
		String message = in_socket.readLine();
		System.out.println("Server: " + message);
		String name = keyboard.nextLine();
		out_socket.println(name);

		// Loop for chat
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
