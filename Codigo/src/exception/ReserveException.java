package exception;

@SuppressWarnings("serial")
public class ReserveException extends Exception {

	/**
	 * Empty constructor method for exception 'ReservaException'
	 */
	public ReserveException() {
		super();
	}

	/**
	 * Constructor method for exception 'ReservaException'
	 */
	public ReserveException( String msg ) {
		super( msg );
	}
}
