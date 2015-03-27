package model;

import exception.ClienteException;

public class Aluno extends Cliente {
	
	/**
	 * Defining constants for error messages and alerts. Constant 'MATRICULA_INVALIDO'
	 * was already marked as comment. 
	 * It will stay like that for future analyse
	 */
	
	// private final String MATRICULA_INVALIDO = "Matricula Invalido.";
	private final String MATRICULA_BRANCO = "Matricula em Branco.";
	private final String MATRICULA_NULO = "Matricula esta Nula.";
	
	/**
	 * Constructor of class Aluno
	 * @param nome
	 * @param cpf
	 * @param matricula
	 * @param telefone
	 * @param email
	 * @throws ClienteException
	 */
	public Aluno(String nome, String cpf, String matricula, String telefone,
			String email) throws ClienteException {
		super(nome, cpf, matricula, telefone, email);
	}
	
	/**
	 * Setter method for 'matricula'
	 * @throws ClientException
	 */
	public void setMatricula(String matricula) throws ClienteException {
		if ( matricula == null ) {
			throw new ClienteException(MATRICULA_NULO);
		} else if ( "".equals( matricula.trim() ) ) { 
			throw new ClienteException(MATRICULA_BRANCO);
		} else{
			// Nothing to do here
		}
		/**
		 * This block of code was already commented. It will stay like this
		 * for future analyse. 
		 * 
		 * else if(matricula.matches("^[\\d]{2,2}/[\\d]{5,7}$"))
		 * super.matricula = matricula;
		 * throw new ClienteException(MATRICULA_INVALIDO); 
		 */
		 
		super.matricula = matricula;//
	}
}