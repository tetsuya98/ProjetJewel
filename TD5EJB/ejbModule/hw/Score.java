package hw;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(
		name="findScores", query="SELECT s FROM Score s ORDER BY s.score DESC "
		)
})
public class Score implements Serializable{
	@Id
	@GeneratedValue (strategy=GenerationType.AUTO) 
	private int id;
	private String pseudo="";
	private int score=0;
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getId() {
		return id;
	}
	
	
}
