package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;



import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.awt.event.ActionEvent;

public class ClientServlet extends JFrame {

	private JPanel contentPane;
	private JTextField textNom;
	private JLabel lblRetour = new JLabel("");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientServlet frame = new ClientServlet();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientServlet() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textNom = new JTextField();
		textNom.setBounds(130, 103, 161, 20);
		contentPane.add(textNom);
		textNom.setColumns(10);
		
		JLabel lblPseudo = new JLabel("Votre Pseudo");
		lblPseudo.setBounds(47, 106, 73, 14);
		contentPane.add(lblPseudo);
		
		JButton btnNewButton = new JButton("Entrer");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executerServlet();
			}
		});
		btnNewButton.setBounds(301, 102, 89, 23);
		contentPane.add(btnNewButton);
		
		
		lblRetour.setBounds(47, 172, 322, 53);
		contentPane.add(lblRetour);
	}
	
	private void executerServlet()
	{
		String nom=textNom.getText();
		try
		{
			// Connexion à la servlet
			URL url=new URL("http://localhost:8080/ProjetServlet/hello");
			URLConnection connexion=url.openConnection();
			connexion.setDoOutput(true);
			// Récupération du flux de sortie
			ObjectOutputStream fluxsortie = new ObjectOutputStream(connexion.getOutputStream());
			// Envoi du nom à rechercher
			fluxsortie.writeObject(nom);
			// Récupération du flux d’entrée
			ObjectInputStream fluxentree = new ObjectInputStream(connexion.getInputStream());
			// Récupération du résultat de la requête
			String retourServlet=(String) fluxentree.readObject();
			// affichage du résultat
			lblRetour.setText(retourServlet);
		}
		catch (Exception e)
		{
			System.out.println("erreur "+e);
		}
	}

	
	
}
