package model;

import exception.PatrimonioException;

/**
 * Equipment and rooms reservation system
 * Sala class extending Patrimonio class
 */
public class Sala extends Patrimonio {

	private String capacidade;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'CAPACIDADE_NEGATIVA' WAS already marked as comment.
	 * It will stay like that for future analyse
	 */
	private final String CAPACIDADE_INVALIDO = "Capacidade Invalida.";
	private final String CAPACIDADE_BRANCO = "Capacidade em Branco.";
	private final String CAPACIDADE_NULA = "Capacidade esta nula.";

	// private final String CAPACIDADE_NEGATIVA = "Capacidade negativa.";

	/**
	 * Constructor method for class Sala
	 * @param codigo
	 * @param descricao
	 * @param capacidade
	 * @throws PatrimonioException
	 */
	public Sala( String codigo, String descricao, String capacidade )
			throws PatrimonioException {
		super( codigo, descricao );
		this.setCapacidade( capacidade );
	}

	/**
	 * Getter method for attribute 'capacidade'
	 * @return
	 */
	public String getCapacidade() {
		return capacidade;
	}

	/**
	 * Setter method for attribute 'capacidade'
	 * @return
	 */
	public void setCapacidade( String capacidade ) throws PatrimonioException {
		if ( capacidade == null ) { 
			throw new PatrimonioException( CAPACIDADE_NULA );
		} else if ( "".equals( capacidade.trim() ) ) {
			throw new PatrimonioException( CAPACIDADE_BRANCO );
		} else if ( capacidade.matches( "[\\d]+" ) ) {
			this.capacidade = capacidade;
		} else {
			throw new PatrimonioException( CAPACIDADE_INVALIDO );
		}
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param b
	 * @return
	 */
	public boolean equals( Sala b ) {
		if ( super.equals( b )
				&& this.getCapacidade().equals( b.getCapacidade() ) ) {
			return true;
		} else { 
			// Nothing to do
		}

		return false;
	}
}
