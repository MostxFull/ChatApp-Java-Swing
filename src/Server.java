import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static List<ClientThread> clients = new ArrayList<>();
    private static int ids = 0;
    private static final int PORT = 3441;
    private static ServerSocket server = null;
    public static void main(String[] args) throws IOException {
        server = new ServerSocket(PORT);
        System.out.println("Le serveur écoute sur : " + server.getLocalSocketAddress());

        while (true) {
            Socket client = server.accept();
            System.out.println("Le client " + client.getRemoteSocketAddress() + " est connecté.");
            ClientThread Cthread = new ClientThread(client, ++ids);
            clients.add(Cthread);
            Cthread.start();
        }
    }

    // Diffusion des messages généraux
    public static void MessBroadcast(String message, Socket s) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getSocket() != s) {
                clients.get(i).messEnvoye(message);
            }
        }
    }
    public static String getport() {
        return PORT + "";
    }
    // Envoi de message privé à un client spécifié
    public static void MessPrivate(String message, String recipient, Socket senderSocket) {
        for (ClientThread client : clients) {
            if (client.getSocket() != senderSocket && client.getId() == Integer.parseInt(recipient)) {
                client.messEnvoye("Message privé de [Client " + getClientId(senderSocket) + "] : " + message);
                return;
            }
        }
        // Si le destinataire n'est pas trouvé, informer l'expéditeur
        for (ClientThread client : clients) {
            if (client.getSocket() == senderSocket) {
                client.messEnvoye("Erreur: Le client avec ID " + recipient + " n'a pas été trouvé.");

            }
    }

}


    // Recherche de l'ID du client basé sur son socket
    private static Long getClientId(Socket socket) {
        for (ClientThread client : clients) {
            if (client.getSocket() == socket) {
                return client.getId();
            }
        }
        return (long) -1; // Client non trouvé
    }

    public static boolean idMatchesSocket(String id) {
        try {
            long targetId = Long.parseLong(id);
            for (ClientThread client : clients) {
                if (client.getId() == targetId) {
                    return client.getSocket() != null;
                }
            }
        } catch (NumberFormatException e) {
            // ID is not a valid number
            return false;
        }
        return false; // No matching client found
    }

    static boolean getClientSocket(Long clientId) {
        for (ClientThread client : clients) {
            if (client.getId()==clientId) {
                return true;
            }
        }
        return false; // Client not found
    }
}
