import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args) {
        try {
            // Création d'un socket pour envoyer des paquets multicast
            MulticastSocket socket = new MulticastSocket();

            // Adresse IP du groupe multicast
            InetAddress group = InetAddress.getByName("230.0.0.0");

            // Port utilisé pour envoyer les messages
            int port = 5000;

            // Lecture de l'entrée utilisateur
            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez le message à envoyer : ");
            String message = scanner.nextLine();

            // Conversion du message en tableau d'octets
            byte[] buffer = message.getBytes();

            // Création et envoi d'un paquet contenant le message
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            socket.send(packet);
            System.out.println("Message envoyé : " + message);

            // Pause (optionnelle) pour simuler un délai
            Thread.sleep(1000);

            // Fermeture des ressources
            socket.close();
            scanner.close();
        } catch (Exception e) {
            // Gestion des exceptions
            e.printStackTrace();
        }
    }
}
