package model;

import exception.ClienteException;

public class Professor extends Cliente {

	/**
	 * Defining constants for error messages and alerts. Constant
	 * 'MATRICULA_INVALIDO' was already marked as comment. It will stay like
	 * that for future analyse.
	 */
	// private final String MATRICULA_INVALIDO = "Matricula Invalida.";
	private final String MATRICULA_BRANCO = "Matricula em Branco.";
	private final String MATRICULA_NULO = "Matricula esta Nula.";

	/**
	 * Constructor method for class Professor
	 * @param nome
	 * @param cpf
	 * @param matricula
	 * @param telefone
	 * @param email
	 * @throws ClienteException
	 */
	public Professor(String nome, String cpf, String matricula,
			String telefone, String email) throws ClienteException {
		super(nome, cpf, matricula, telefone, email);
	}

	/**
	 * Setter method for attribute 'Matricula'
	 * @throws ClienteException
	 */
	public void setMatricula(String matricula) throws ClienteException {
		if ( matricula == null ) {
			throw new ClienteException(MATRICULA_NULO);
		} else if ( "".equals(matricula.trim()) ) {
			throw new ClienteException(MATRICULA_BRANCO);
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
		super.matricula = matricula;//
	}

}
