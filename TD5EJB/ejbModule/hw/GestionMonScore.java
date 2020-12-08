package hw;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class GestionMonScore
 */
@Stateless
@LocalBean
public class GestionMonScore implements GestionMonScoreRemote {
	
	@EJB
	GestionScore gestionScore;
	
	private Score monScore = new Score();
	private int count = 0;

    /**
     * Default constructor. 
     */
    public GestionMonScore() {
        // TODO Auto-generated constructor stub
    	
    	
    }

    public Score NouvellePartie(String pseudo) {
		monScore.setPseudo(pseudo);
		System.out.println(("pseudo:"+monScore.getPseudo()));
		count++;
		System.out.println("count= "+count);
		return monScore;
    
    }
    
    public Score NouvellePartie(String pseudo, int newScore) {
		monScore.setPseudo(pseudo);
		monScore.setScore(newScore);
		UpdateOnDatabase();
		System.out.println(("pseudo:"+monScore.getPseudo()));
		count++;
		System.out.println("count= "+count);
		return monScore;
    
    }

	@Override
	public Score UpdateScore(int newScore) {
		monScore.setScore(newScore);
		return monScore;
	}
	
	public Score UpdateOnDatabase() {
		
			Score result= new Score();
		try {
			result= gestionScore.ajouter(monScore);
			
		}
		catch(Exception e){
			System.out.println("oh oh");
			e.printStackTrace();
		}
		return result;
	}
	
	public Score setPseudo(String pseudo) {
		monScore.setPseudo(pseudo);
		return monScore;
	}
	
	public List<Score> getHighscore(){
		return gestionScore.listerMeilleursScores();
	}
	
	 @PreDestroy
     private void shutdown() {
        //TestEntity e = tableFacade.find("test");
        //do something
		 System.out.println("ejb deleted");

     }
}
