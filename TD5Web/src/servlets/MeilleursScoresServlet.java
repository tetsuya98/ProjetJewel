package servlets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connexion.LookupEJB;
import hw.GestionMonScoreRemote;
import hw.Score;

/**
 * Servlet implementation class MeilleursScoresServlet
 */
@WebServlet("/MeilleursScores")
public class MeilleursScoresServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MeilleursScoresServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	private static GestionMonScoreRemote lookupRemoteStatelessGestionDeMonScore() throws NamingException {
		GestionMonScoreRemote remote=null;
	     try {
	    	 final Hashtable jndiProperties = new Hashtable();
	 		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	        try { 
	        	InitialContext ctx = new InitialContext(jndiProperties);
	        	remote = (GestionMonScoreRemote) ctx.lookup("java:global/TD5EAR/TD5EJB/GestionMonScore!hw.GestionMonScoreRemote");
	        	// ?stateful doesn't work
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
	         
	         
	          } catch (Exception e) {
	         	 e.printStackTrace();
	           }      
	     return remote;
	 }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		// TODO Auto-generated method stub
				GestionMonScoreRemote ejb=null;
				if ( ejb == null) {
		        	try {
		        		System.out.println("new ejb");
						ejb = LookupEJB.lookupRemoteStatelessGestionDeMonScore();
						//request.getSession().setAttribute(SESSION_KEY,ejb);
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				String pseudo;
				int score;
				try {
					// Récupération du flux d'entrée envoyé par l'applet
					//ObjectInputStream entree=new ObjectInputStream(request.getInputStream());
					// Récupération de l'objet envoyé par le client
					//pseudo=(String)entree.readObject();
					// Préparation du flux de sortie
					//pseudo= request.getParameter("pseu");
					//score= Integer.valueOf(request.getParameter("sco"));
					List<Score> scores= ejb.getHighscore();
					ObjectOutputStream sortie=new ObjectOutputStream(response.getOutputStream());
					// Travail à réaliser sur les données en entrée
					//ejb.NouvellePartie(pseudo, score);
					//String chaineBienvenue = "Bienvenue "+pseudo;
					// Envoi du résultat au client
					sortie.writeObject(scores);
					} catch (Exception ex) {
					System.out.println("Erreur d'exécution de la requête SQL : "+ex);
					}

				//response.getWriter().append("Served at: ").append(request.getContextPath());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
