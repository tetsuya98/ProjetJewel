package hw;

import java.util.List;

import javax.ejb.Local;

@Local
public interface GestionScoreLocal {
	public Score ajouter(Score score);
	public List<Score> listerMeilleursScores();
}
