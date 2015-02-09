package ftp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class DataFTP implements Runnable {

	private Socket socket;
	
	public DataFTP(Socket socket) {
		this.socket = socket;
	}
	
	private void processData() {
		String buffer;
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
		case "LIST":
			processList();
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
        ArrayList<File> files=new ArrayList<File>();
        
        send("150 Here comes the directory listing");
	}

	@Override
	public void run() {
		this.processData();
	}

}
