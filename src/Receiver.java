import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver {
    public static void main(String[] args) {
        try {
            // Définition du port d'écoute
            int port = 5000;

            // Adresse IP du groupe multicast
            InetAddress group = InetAddress.getByName("230.0.0.0");

            // Création d'un socket multicast
            MulticastSocket socket = new MulticastSocket(port);

            // Rejoindre le groupe multicast
            socket.joinGroup(group);

            // Préparation d'un buffer pour recevoir les données
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Réception d'un paquet envoyé au groupe multicast
            socket.receive(packet);

            // Extraction et affichage du message reçu
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Message reçu : " + receivedMessage);

            // Quitter le groupe multicast
            socket.leaveGroup(group);

            // Fermer le socket
            socket.close();
        } catch (Exception e) {
            // Gestion des exceptions
            e.printStackTrace();
        }
    }
}
