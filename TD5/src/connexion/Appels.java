package connexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import hw.Score;

public final class Appels {
	
	private Appels() {
		
	}

	public static String EnregistrerPartie(String pseudo, int score) throws IOException, ClassNotFoundException {
		System.out.println("pseudo: "+pseudo);
		URL url = null;
		try {
			url = new URL("http://localhost:8080/TD5Web/Jewel?pseu="+pseudo+"&sco="+score);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection connexion= null;
		try {
			connexion = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connexion.setDoOutput(true);
		// Récupération du flux de sortie
		ObjectOutputStream fluxsortie = new ObjectOutputStream(connexion.getOutputStream());
		// Envoi du nom à rechercher
		//fluxsortie.writeObject(obj);;
		// Récupération du flux d’entrée
		ObjectInputStream fluxentree = new ObjectInputStream(connexion.getInputStream());
		// Récupération du résultat de la requête
		String retourServlet=(String) fluxentree.readObject();
		// affichage du résultat
		//labelRetour.setText(retourServlet);
		System.out.println(retourServlet);
		return retourServlet;
		
	}
	
	public static List<Score> getMeilleursScrores () throws IOException{
		URL url = null;
		try {
			url = new URL("http://localhost:8080/TD5Web/MeilleursScores");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection connexion= null;
		try {
			connexion = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connexion.setDoOutput(true);
		// Récupération du flux de sortie
		ObjectOutputStream fluxsortie = new ObjectOutputStream(connexion.getOutputStream());
		// Envoi du nom à rechercher
		//fluxsortie.writeObject(obj);;
		// Récupération du flux d’entrée
		ObjectInputStream fluxentree = new ObjectInputStream(connexion.getInputStream());
		// Récupération du résultat de la requête
		List<Score> retourServlet= new ArrayList<Score>();
		try {
			retourServlet = (List<Score>) fluxentree.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// affichage du résultat
		//labelRetour.setText(retourServlet);
		System.out.println("appel getMeilleursScores");
		System.out.println(retourServlet.toString());
		return retourServlet;
	}
}
