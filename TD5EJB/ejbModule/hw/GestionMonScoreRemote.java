package hw;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface GestionMonScoreRemote {
	public Score NouvellePartie(String pseudo);
	public Score NouvellePartie(String pseudo, int newScore);
	public Score UpdateScore(int newScore);
	public Score UpdateOnDatabase();
	public Score setPseudo(String pseudo);
	public List<Score> getHighscore();
	
}
