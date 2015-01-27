package ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	
	public HashMap<String, String> users;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		int port = 1024;
		String usersFile = "usersFile.txt";
		ServerSocket Servs;
		Socket s;
		InputStream is;
		OutputStream os;
		
		initializeHash(usersFile);
		
		try {
			Servs = new ServerSocket(port);
			while(true){
				s = Servs.accept();
				
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void initializeHash(String usersFile) {
		//TODO 		
	}

}
