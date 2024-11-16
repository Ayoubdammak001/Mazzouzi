import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public Client() throws Exception {
		Socket socket = new Socket("192.168.158.39", 2021);
		System.out.println("Successful connection to the server.");

		// I/O streams
		BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		Scanner keyboard = new Scanner(System.in);

		// Server asks for the client's name
		String message = in_socket.readLine();
		System.out.println("Server: " + message);
		String name = keyboard.nextLine();
		out_socket.println(name); // Send the client's name to the server

		// Loop for chat
		while (true) {
			// Read server's message
			message = in_socket.readLine();
			if (message == null || message.equalsIgnoreCase("exit")) {
				break;
			}
			System.out.println("Server: " + message);

			// Send message to server
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
