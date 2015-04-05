/**
* Equipment and Rooms Reservation System
* This file contains the exceptions related to the patrimony.
*/

package exception;

@SuppressWarnings("serial")
public class PatrimonioException extends Exception {

	/**
	 * Empty constructor method for exception 'PatrimonioException'
	 */
	public PatrimonioException() {
		super();
	}

	/**
	 * Constructor method for exception 'PatrimonioException'
	 */
	public PatrimonioException( String msg ) {
		super( msg );
	}

}
