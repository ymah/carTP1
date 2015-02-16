package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {


	
	public static void main(String[] args) {
		HashMap<String, String> usersList = new HashMap<String,String>();
		usersList.put("mah", "toto");
		/*creation serversocket*/
		try {
			@SuppressWarnings("resource")
			final ServerSocket ss = new ServerSocket(2121);
			/*fin creation serversocket*/
			Socket socket =null;
			/*accepter une connexion passer la main a un thread*/
			while(true){
				socket = ss.accept();
				Thread tFtp = new Thread(new RequestFTP(socket,usersList));
				tFtp.start();
			}
		} catch (NumberFormatException e) {
			System.out.println("Erreur de port");
		} catch (IOException e) {
			System.out.println("Erreur de création de socket");
		}
	}	

}
