package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

	public static void main(String[] args) {
		if(args.length !=1){
			System.err.println("Port non specifié");
		}
		/*creation serversocket*/
		try {
			@SuppressWarnings("resource")
			final ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
			/*fin creation serversocket*/
			Socket socket =null;
			/*accepter une connection passer la main a un thread*/
			while(true){
				socket = ss.accept();
				Thread tFtp =new Thread(new RequestFTP(socket));
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
