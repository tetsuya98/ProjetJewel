package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import connexion.Appels;
import hw.GestionMonScoreRemote;
import hw.Score;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

public class Jewels extends JFrame {

	private JPanel contentPane;
	private String pseudo = "";
	//private GestionMonScoreRemote scores;
	public JTextArea MS;
	public static String url= "http://localhost:8080/TD5Web/Jewel";

	
	public static void NewGame(String pseudo) {
		try
		{
			// Connexion à la servlet
			URL url=new URL("http://localhost:8080/TD5Web/Jewel");
			URLConnection connexion=url.openConnection();
			connexion.setDoOutput(true);
			// Récupération du flux de sortie
			ObjectOutputStream fluxsortie = new ObjectOutputStream(connexion.getOutputStream());
			// Envoi du nom à rechercher
			fluxsortie.writeObject(pseudo);
			// Récupération du flux d’entrée
			ObjectInputStream fluxentree = new ObjectInputStream(connexion.getInputStream());
			// Récupération du résultat de la requête
			String retourServlet=(String) fluxentree.readObject();
			// affichage du résultat
			//labelRetour.setText(retourServlet);
			System.out.println(retourServlet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public void EnregistrerPartie(int score) {
		try {
			Appels.EnregistrerPartie(this.pseudo, score);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void FinPartie(int score) {
		try {
			
			
			
			StringBuilder contentHighscore= new StringBuilder();
			for (Score res : Appels.getMeilleursScrores()) {
				contentHighscore.append(res.getPseudo()+":"+res.getScore()+"\n");
				
			}
			JOptionPane.showMessageDialog(this, "Votre score: "+ score +"\n Meilleurs scores : \n"+contentHighscore.toString());
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					
					Jewels frame = new Jewels();
					frame.setPseudo(JOptionPane.showInputDialog(frame, "Quel est votre nom?", null ));
					//frame.setPseudo(JOptionPane.showInputDialog(frame, "Quel est votre nom?", null ));
					System.out.println("frame pseudo:"+frame.getPseudo());
					frame.setVisible(true);
					frame.repaint();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Jewels() {
		try {
			URL lien = new URL("http://localhost:8080/TD5Web/Jewel");
			//scores = lookupRemoteStatelessGestionDeMonScore();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//scores.NouvellePartie(this.getPseudo());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 483, 417);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		PanelJeu panelJeu = new PanelJeu(this.pseudo);
		panelJeu.jeu= this;
		panelJeu.setBounds(10, 83, 448, 292);
		contentPane.add(panelJeu);
		panelJeu.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 448, 61);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel labelPseudo = new JLabel(this.getPseudo());
		labelPseudo.setFont(new Font("Tahoma", Font.BOLD, 14));
		labelPseudo.setBounds(10, 11, 222, 23);
		panel.add(labelPseudo);
		
		MS= new JTextArea("");
		MS.setBounds(10, 11, 290, 60);
		MS.setLineWrap( true);
		MS.setWrapStyleWord(true);
		panel.add(MS);
		
		JButton btnMeilleursScores = new JButton("Meilleurs Scores");
		btnMeilleursScores.setBounds(303, 11, 135, 23);
		btnMeilleursScores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Score> result= new ArrayList<Score>();
				try {
					result = Appels.getMeilleursScrores();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MS.setText("");
				for (Score res : result) {
					MS.setText(MS.getText()+" | "+res.getPseudo()+":"+res.getScore());
					
				}
				
			}
		});
		panel.add(btnMeilleursScores);
		panelJeu.start();
	}
	
	public String getPseudo() {
		return this.pseudo;
	}

	public void setPseudo(String pseudo) {
		System.out.println(pseudo);
		this.pseudo = pseudo;
		System.out.println(this.pseudo);
		//scores.setPseudo(pseudo);
		//NewGame(pseudo);
	}

	private static GestionMonScoreRemote lookupRemoteStatelessGestionDeMonScore() throws NamingException {
		GestionMonScoreRemote remote=null;
	     try {
	    	 final Hashtable jndiProperties = new Hashtable();
	 		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	        try { 
	        	InitialContext ctx = new InitialContext(jndiProperties);
	        	remote = (GestionMonScoreRemote) ctx.lookup("ejb:/TD5EJB/GestionMonScore!hw.GestionMonScoreRemote?stateful");
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
	         
	         
	          } catch (Exception e) {
	         	 e.printStackTrace();
	           }      
	     return remote;
	 }
}
