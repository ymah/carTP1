Réalistion d'un serveur FTP MultiThreadé
Mahieddine Yaker - Dylan Forest
16/12/15

Ce logiciel sert de serveur FTP, il devra être utilisable via un logiciel client type sur le protocle RFC 959.

Une première classe Server génère la liste des utilisateurs, un seul est disponnible, mah avec comme mot de passe
toto. 
A savoir que pour fonctionner, côté serveur, il faut un dossier 'userPath' dans lequel se trouve les dossier des 
différents utilisateurs. Ici, un dossier 'userPath/mah/' est nécessaire. Ils seront fournis dans l'archive.
Cette classe crée ensuite le serveur sur une socket (ici 2121) et attend la connexion de clients. Chaque nouvelle
connexion entraine la création d'une nouveau Thread de la seconde classe présente dans ce package, RequestFTP.

Cette seconde classe sert aux traitements des différentes requêtes clientes. Dès sa création, c'est à dire dès qu'un
client s'est connecté au serveur, cette classe envoi via la méthode suivante le message de bienvenu '220'.

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
		System.out.println("Erreur envoi de message");
	}
}

Après quoi il attendra chaque message du dit client afin de les traiter. Chaque commande est sujete à une méthode dans
cette classe côté serveur.
On aura notamment les méthodes processUser() et processPass() qui parcoureront la table des utilisateurs et retourneront
un message d'acceptation ou de refus au client via la précédente méthode.

Pour ce qui est des commandes clients permettant des interactions concernant les fichiers (envoi/réception) il faut avant
toutes chose ouvrir une socket côté serveur vers le client. Pour ce faire, et à chaque demande, un client ftp envoi au serveur
la commande PORT suivie de son adresse ainsi que du port sur lequel il souhaite effectuer les échanges.

private void processPrt() {
	...
	port = Integer.parseInt(process[process.length-2]) * 256 + Integer.parseInt(process[process.length-1]);
	try {
		InetAddress addr = InetAddress.getByName(IP);
		SocketAddress sckadd = new InetSocketAddress(addr,port);
		int timeout = 2000;
		this.socketData = new Socket();
		socketData.connect(sckadd,timeout);
		send("200");
	}
	...
}

Une fois reçue, elle est traitée côté serveur afin d'ouvrir la socket. L'envoi du code 200 permet de renseigner le client que
le serveur a bien ouvert la Socket et qu'il est prêt à traiter ses requêtes.

Ainsi le client est en mesure d'envoyer les commande suivante traitée par notre serveur :
- LIST
- RETR 
- STOR 
- PWD
- CWD
- CDUP
- QUIT
