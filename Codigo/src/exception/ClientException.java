package exception;

@SuppressWarnings("serial")
public class ClientException extends Exception {

	/**
	 * Empty constructor method for exception 'ClienteException'
	 */
	public ClientException() {
		super();
	}

	/**
	 * Constructor method for exception 'ClienteException'
	 */
	public ClientException( String message ) {
		super( message );
	}

}
