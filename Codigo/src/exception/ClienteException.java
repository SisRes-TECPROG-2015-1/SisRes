/**
* Equipment and Rooms Reservation System
* This file contains the exceptions related to the client.
*/

package exception;

@SuppressWarnings("serial")
public class ClienteException extends Exception {

	/**
	 * Empty constructor method for exception 'ClienteException'
	 */
	public ClienteException() {
		super();
	}

	/**
	 * Constructor method for exception 'ClienteException'
	 */
	public ClienteException( String message ) {
		super( message );
	}

}
