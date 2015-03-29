package exception;

@SuppressWarnings("serial")
public class ReservaException extends Exception {

	/**
	 * Empty constructor method for exception 'ReservaException'
	 */
	public ReservaException() {
		super();
	}

	/**
	 * Constructor method for exception 'ReservaException'
	 */
	public ReservaException( String msg ) {
		super( msg );
	}
}
