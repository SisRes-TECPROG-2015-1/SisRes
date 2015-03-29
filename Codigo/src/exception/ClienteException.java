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
