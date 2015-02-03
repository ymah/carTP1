package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import sun.security.util.Length;

public class RequestFTP implements Runnable {

	private HashMap<String, String> usersList;
	private Socket socket;
	private Socket socketData;
	private String user;
	private String pass;
	private String repo;
	
	private boolean connected;
	private String action;
	
	public RequestFTP(Socket s,HashMap<String, String> ul){
		this.socket = s;
		this.connected= false;
		this.usersList =  ul ;		
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
		case "LIST":
			processList();
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
	
	public void processUser(){
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
			this.socketData = new Socket(IP, port);
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

	}

	public void processStor() {

	}
	
	public void listerRepertoire(File repertoire){

		String [] listefichiers;

		int i;
		listefichiers=repertoire.list();
		for(i=0;i<listefichiers.length;i++){
			if(listefichiers[i].endsWith(".java")==true){

				System.out.println(listefichiers[i].substring(0,listefichiers[i].length()-5));// on choisit la sous chaine - les 5 derniers caracteres ".java"
			}
		}
	}

	public void processList() {
		System.out.println("En attente de connection");
        // En attente d'une connection sur le nouveau port pour le canal de donnée
        Socket canal=this.socketData.accept();
        ArrayList<File> files=new ArrayList<File>();
        
        // On envoie les commandes par le canal de commande
        send("150 Here comes the directory listing");
	}

	public void processQuit(){
		send("221");
	}

	public void run() {
		this.processRequest();
	}

}
