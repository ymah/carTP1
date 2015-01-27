package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class RequestFTP implements Runnable {

	private HashMap<String, String> usersList;
	private Socket socket;
	private String user;
	private String pass;
	
	private boolean connected;
	private String action;
	
	public RequestFTP(Socket s,HashMap<String, String> ul){
		this.socket = s;
		this.connected= false;
		this.usersList =  ul ;
	}
	public void processRequest() {
		String buffer;
		send("220");
		while(true){
			try{
				
				InputStream is = this.socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				buffer = br.readLine();
				checkRequest(buffer);

			}catch (IOException e){
				
			}
		}
		
	}

	private void checkRequest(String buffer){
		String[] split = buffer.split(" ");
		switch(split[0]){
		case "USER":
			if(split.length >= 2){
				this.action = split[1];
				processUser();
			}else {
				send("USER <pseudo>");
			}
			break;
		case "PASS":
			if(split.length >= 2){
				this.action = split[1];
				processUser();
			}else {
				send("USER <pseudo>");
			}
			break;
		default:
			send("505");
			
		}
	}
	
	private void send(String mess){
		System.out.println("Send de : "+mess);
		OutputStream os;
		try {
			os = this.socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.write(mess.toCharArray());
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void processUser() {
		HashMap<String, String> users = this.usersList;
		if(users.containsKey(this.action)){
			this.user = this.action;
			send("331");
		}else {
			send("332");
		}
	}

	public void processPass() {
		HashMap<String, String> users = this.usersList;
		if(users.containsValue(this.action)){
			this.pass = this.action;
			send("230");
		}else {
			send("332");
		}
	}

	public void processRetr() {

	}

	public void processStor() {

	}

	public void processList() {

	}

	public void processQuit() {

	}

	public void run() {

		this.processRequest();
	}

}
