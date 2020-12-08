package hw;

import java.util.List;

import javax.ejb.AccessTimeout;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Session Bean implementation class GestionScore
 */
@Singleton
@LocalBean
@AccessTimeout(0)
public class GestionScore implements GestionScoreLocal {

    /**
     * Default constructor. 
     */
    public GestionScore() {
        // TODO Auto-generated constructor stub
    }

    @PersistenceContext
    EntityManager em;
    
    @Override
    @Lock(LockType.WRITE)
    public Score ajouter(Score score) {
    	em.persist(score);
    	return score;
    }
	@Override
	@Lock(LockType.READ)
	public List<Score> listerMeilleursScores() {
		// TODO Auto-generated method stub
		return em.createNamedQuery("findScores").getResultList();
	}
}
