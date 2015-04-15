package model;

import exception.ClientException;

public class Teacher extends Client {

	/**
	 * Defining constants for error messages and alerts. Constant
	 * 'MATRICULA_INVALIDO' was already marked as comment. It will stay like
	 * that for future analyse.
	 */
	// private final String MATRICULA_INVALIDO = "Matricula Invalida.";
	private final String REGISTRATION_WHITE = "Matricula em Branco.";
	private final String REGISTRATION_NULL = "Matricula esta Nula.";

	/**
	 * Constructor method for class Professor
	 * @param nome
	 * @param cpf
	 * @param matricula
	 * @param fone
	 * @param email
	 * @throws ClientException
	 */
	public Teacher( String nome, String cpf, String matricula,
			String fone, String email ) throws ClientException {
		super( nome, cpf, matricula, fone, email );
	}

	/**
	 * Setter method for attribute 'Matricula'
	 * @throws ClientException
	 */
	public void setRegistration( String registration ) throws ClientException {
		if ( registration == null ) {
			throw new ClientException( REGISTRATION_NULL );
		} else if ( "".equals( registration.trim() ) ) {
			throw new ClientException( REGISTRATION_WHITE );
		} else { 
			// Nothing to do 
		}
		
		/**
		 * This block of code was already commented. It will stay like this
		 * for future analyse. 
		 * 
		 * else if(matricula.matches("PATTERN"))
		 * super.matricula = matricula;
		 * else
		 * throw new ClienteException(MATRICULA_INVALIDO);
		 */
		super.registration = registration;//
	}

}
