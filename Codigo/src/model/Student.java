package model;

import exception.ClientException;

public class Student extends Client {
	
	/**
	 * Defining constants for error messages and alerts. Constant 'MATRICULA_INVALIDO'
	 * was already marked as comment. 
	 * It will stay like that for future analyse
	 */
	
	// private final String MATRICULA_INVALIDO = "Matricula Invalido.";
	private final String REGISTRATION_WHITE = "Matricula em Branco.";
	private final String REGISTRATION_NULL = "Matricula esta Nula.";
	
	/**
	 * Constructor of class Student
	 * @param name
	 * @param cpf
	 * @param registration
	 * @param telephone
	 * @param email
	 * @throws ClientException
	 */
	public Student( String name, String cpf, String registration, String telephone,
			String email ) throws ClientException {
		super(name, cpf, registration, telephone, email);
	}
	
	/**
	 * Setter method for registration
	 * @throws ClientException
	 */
	public void setRegistration( String registration ) throws ClientException {
		if ( registration == null ) {
			throw new ClientException( REGISTRATION_NULL );
		} else if ( "".equals( registration.trim() ) ) { 
			throw new ClientException( REGISTRATION_WHITE );
		} else{
			// Nothing to do here
		}
		/**
		 * This block of code was already commented. It will stay like this
		 * for future analyze. 
		 * 
		 * else if(matricula.matches("^[\\d]{2,2}/[\\d]{5,7}$"))
		 * super.matricula = matricula;
		 * throw new ClienteException(MATRICULA_INVALIDO); 
		 */
		 
		super.registration = registration;
	}
}