
/**
 * 
 * @author mikec
 *
 */

/*
 * 
 */
public interface OS_sim_interface {
	
	//This interface file MUST not be changed in anyway
	//Only the DEFAULT MAY BE USED 
	 
	public void set_number_of_processors(int nProcessors);
	
	public int reg (int priority); //Returns process ID
	public void start(int ID);
	public void schedule(int ID);
	public void terminate(int ID);
	
}
