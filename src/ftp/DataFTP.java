package ftp;

import java.io.File;

public class DataFTP{


	
	public DataFTP() {
	}

	public String[] listerRepertoire(String repertoire){
		System.out.println(repertoire);
		File directory = new File(repertoire);
		String[] flist = directory.list();
		return flist;
	}

}
