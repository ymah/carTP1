package ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import sun.security.util.Length;

public class RequestFTP implements Runnable {

	private HashMap<String, String> usersList;
	private Socket socket;
	private Socket socketData;
	private String user;
	private String pass;
	private String repo;
	private String current;
	
	private boolean connected;
	private String action;
	
	public RequestFTP(Socket s,HashMap<String, String> ul){
		this.socket = s;
		this.connected= false;
		this.usersList =  ul ;	
		current = System.getProperty("user.dir");
	}
	public void processRequest(){
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

	private void checkRequest(String buffer) throws IOException{
		System.out.println(buffer);
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
				processPass();
			}else {
				send("PASS <pseudo>");
			}
			break;
		case "SYST":
			processSys();
			break;
		case "PORT":
			if(split.length >= 2){
				this.action = split[1];
				processPrt();
			}else {
				send("TODO error list");
			}
			break;
		case "bye":
			if(split.length >= 2){
				this.action = split[1];
				processQuit();
			}else {
				send("PASS <pseudo>");
			}
			break;
		case "LIST":
			if(split.length == 1){
				processList(false);
			}else if(split.length == 2) {
				this.action = split[1];
				processList(true);
			}else {
				send("PASS <pseudo>");
			}
			break;
		case "STOR":
			if(split.length == 2){
				this.action = split[1];
				processStor();
			}else {
				send("501 Erreur de syntaxe dans les paramètres et/ou arguments.");
			}
			break;
		case "RETR":
			if(split.length == 2){
				this.action = split[1];
				processRetr();
			}else {
				send("501 Erreur de syntaxe dans les paramètres et/ou arguments.");
			}
			break;
		default:
			System.out.println("NOT IMPLEMENTED " + buffer);
			//send("505");
		}
	}
	
	private void send(String mess){
		System.out.println("Send of "+mess);
		OutputStream os;
		String ss = new String(mess + "\r\n");
		try {
			os = this.socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeBytes(ss);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void sendData(String mess){
		System.out.println("Send of "+mess+" on "+this.socketData.toString());
		OutputStream os;
		String ss = new String(mess + "\r\n");
		try {
			os = this.socketData.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeBytes(ss);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public void processUser(){
		HashMap<String, String> users = this.usersList;
		
		if(users.containsKey(this.action)){
			this.user = this.action;
			send("331");
		}else {
			send("332");
		}
	}
	public void processList(boolean test){
		int i;
		String[] liste;
		if(test == true){
			DataFTP dftp = new DataFTP(this.socketData);	
			liste = dftp.listerRepertoire(this.current+this.action);
		}else{
			DataFTP dftp = new DataFTP(this.socketData);
			liste = dftp.listerRepertoire(this.current+"/.");
		}
		send("150 ASCII data connection");
		for(i=0;i<liste.length;i++){
			sendData(liste[i]);
		}
		send("226 ASCII Transfer complete.");
		try {
			this.socketData.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
	public void processPass() {
		HashMap<String, String> users = this.usersList;
		if(users.containsValue(this.action)){
			this.pass = this.action;
			send("230");
			this.current = "userPath/"+this.user+"/";
		}else {
			send("332");
		}
	}
	
	public void processSys() {
		send("215");
	}
	
	public void processPrt() {
		String[] process = this.action.split(",");
		StringBuilder builder = new StringBuilder();
		String IP;
		int port;
		for(int i = 0; i<process.length-2; i++){
			builder.append(process[i]);
			if(i != process.length-3)
				builder.append('.');
		}
		IP = builder.toString();
		port = Integer.parseInt(process[process.length-2]) * 256 + Integer.parseInt(process[process.length-1]);
		try {
			InetAddress addr = InetAddress.getByName(IP);
			SocketAddress sckadd = new InetSocketAddress(addr,port);
			int timeout = 2000;
			this.socketData = new Socket();
			socketData.connect(sckadd,timeout);
			send("200");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void processRetr() {
		try {
			send("150 ASCII data connection");
			File myFile = new File(this.current+this.action);
			byte[] mybytearray = new byte[(int) myFile.length()];
		    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
		    bis.read(mybytearray, 0, mybytearray.length);
		    OutputStream os = this.socketData.getOutputStream();
		    os.write(mybytearray, 0, mybytearray.length);
		    os.flush();			
		    send("226 ASCII Transfer complete.");
			this.socketData.close();		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processStor() {
		try {
			send("150 ASCII data connection");
			DataInputStream in = new DataInputStream(new BufferedInputStream(socketData.getInputStream()));
			byte[] bytes = new byte[1024];	
			in.read(bytes);
			FileOutputStream fos = new FileOutputStream(this.current+this.action);
		    fos.write(bytes);
		    send("226 ASCII Transfer complete.");
			this.socketData.close();		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processQuit(){
		send("221");
	}

	public void run() {
		this.processRequest();
	}

}
