import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	private Socket client;
	private int id;
	private BufferedReader reader;
	private PrintWriter writer;

	public ClientThread(Socket c, int id) throws IOException {
		this.client = c;
		this.id = id;
		InputStream in = client.getInputStream();
		this.reader = new BufferedReader(new InputStreamReader(in));
		this.writer = new PrintWriter(client.getOutputStream(), true);
	}

	public Socket getSocket() {
		return this.client;
	}

	public long getId() {
		return this.id;
	}

	public void messEnvoye(String message) {
		writer.println(message);
	}


	@Override
	public void run() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				System.out.println("Le client " + this.client.getRemoteSocketAddress() + " envoie : " + message);

				// Si le message commence par "/prv", c'est un message privé
				if (message.startsWith("/prv")) {
					// Message privé : format "/prv <id_destinataire> <message>"
					String[] parts = message.split(" ", 3);
					if (parts.length == 3) {
						String recipient = parts[1];
						String privateMessage = parts[2];
						Server.MessPrivate(privateMessage, recipient, this.client);
					} else {
						// Format incorrect
						writer.println("-> Erreur: Format du message privé incorrect.");
					}
				} else {
					// Sinon, c'est un message général
					Server.MessBroadcast("[" + id + "] => " + message, this.client);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
