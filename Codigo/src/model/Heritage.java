package model;

import exception.PatrimonyException;

public class Heritage {

	private String code;
	private String description;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'CODIGO_INVALIDO' and 'DESCRICAO_INVALIDO' were already marked as
	 * comment. It will stay like that for future analyse
	 */
	// private final String CODIGO_INVALIDO = "Codigo Invalido.";
	private final String CODE_WHITE = "Codigo em Branco.";
	private final String CODE_NULL = "Codigo esta Nulo.";
	// private final String DESCRICAO_INVALIDO = "Descricao Invalido.";
	private final String DESCRIPTION_WHITE = "Descricao em Branco.";
	private final String DESCRIPTION_NULL = "Descricao esta Nula.";

	/**
	 * Constructor method for class Heritage
	 * @param code
	 * @param description
	 * @throws PatrimonyException
	 */
	public Heritage( String code, String description )
			throws PatrimonyException {
		this.setCode( code );
		this.setDescription( description );
	}

	/**
	 * Getter method to attribute 'Code'
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Getter method to attribute 'Description'
	 * @return description
	 */
	public  String getDescription() {
		return description;
	}

	/**
	 * Setter method for attribute 'Code'
	 * @param code
	 * @throws PatrimonyException
	 */
	public void setCode( String code ) throws PatrimonyException {
		if ( code == null ) {
			throw new PatrimonyException( CODE_NULL );
		} else if ( "".equals( code.trim() ) ) {
			throw new PatrimonyException( CODE_WHITE );
		}
		 /**
		  * This block of code was already commented. It will stay like this
		  * for future analyse.
		  * 
		  * else if(codigo.matches("PATTERN"))
		  * this.codigo = codigo;
		  * else
		  * throw new PatrimonioException(CODIGO_INVALIDO); 
		  */
		 
		this.code = code;
	}

	/**
	 * Setter method for attribute 'Descricao'
	 * @param description
	 * @throws PatrimonyException
	 */
	public void setDescription( String description ) throws PatrimonyException {
		if ( description == null ) { 
			throw new PatrimonyException( DESCRIPTION_NULL );
		} else if ( "".equals( description.trim() ) ) {
			throw new PatrimonyException( DESCRIPTION_WHITE );
		} else { 
			// Nothing to do 
		}
		this.description = description;
	}

	/**
	 * Function to validate if a passed Heritage object is equals to 
	 * the new instanciated Heritage object
	 * @param Heritage e
	 * @return boolean
	 */
	public boolean equals( Heritage e ) {
		if ( this.getCode().equals( e.getCode() )
				&& this.getDescription().equals( e.getDescription() ) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	/**
	 * To String method of class Heritage
	 * @return String
	 */
	public String toString() {
		return "Codigo=" + code + "\nDescricao=" + description;
	}
}
