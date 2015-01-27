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
			final ServerSocket ss = new ServerSocket(2783);
			/*fin creation serversocket*/
			Socket socket =null;
			/*accepter une connection passer la main a un thread*/
			while(true){
				socket = ss.accept();
				Thread tFtp =new Thread(new RequestFTP(socket,usersList));
				tFtp.start();
			}
		} catch (NumberFormatException e) {
			System.err.println("Argument erreur");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("creation serveur erreur");
			e.printStackTrace();
		}
	}	

}
