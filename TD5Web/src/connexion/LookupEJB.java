package connexion;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import hw.GestionMonScoreRemote;

public final class LookupEJB {

	private LookupEJB() {
		
	}
	
	public static GestionMonScoreRemote lookupRemoteStatelessGestionDeMonScore() throws NamingException {
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
}
