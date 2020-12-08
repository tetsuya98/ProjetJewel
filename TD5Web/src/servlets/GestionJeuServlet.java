package servlets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

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

/**
 * Servlet implementation class GestionJeuServlet
 */
@WebServlet("/Jewel")
public class GestionJeuServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// private static final String SESSION_KEY = "MonEJB";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GestionJeuServlet() {
		super();
		// TODO Auto-generated constructor stub

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		GestionMonScoreRemote ejb = null;
		try {
			System.out.println("new ejb");
			ejb = LookupEJB.lookupRemoteStatelessGestionDeMonScore();
			// req.getSession().setAttribute(SESSION_KEY,ejb);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String pseudo;
		int score;
		try {
			// R�cup�ration du flux d'entr�e envoy� par l'applet
			ObjectInputStream entree = new ObjectInputStream(req.getInputStream());
			// R�cup�ration de l'objet envoy� par le client
			// pseudo=(String)entree.readObject();
			// Pr�paration du flux de sortie
			pseudo = req.getParameter("pseu");
			score = Integer.valueOf(req.getParameter("sco"));

			ObjectOutputStream sortie = new ObjectOutputStream(resp.getOutputStream());
			// Travail � r�aliser sur les donn�es en entr�e
			ejb.NouvellePartie(pseudo, score);
			String chaineBienvenue = "Bienvenue " + pseudo;
			// Envoi du r�sultat au client
			sortie.writeObject("c bon");
		} catch (Exception ex) {
			System.out.println("Erreur d'ex�cution de la requ�te SQL : " + ex);
		}

		// response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
