package model;

import exception.PatrimonioException;

public class Patrimonio {

	private String codigo;
	private String descricao;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'CODIGO_INVALIDO' and 'DESCRICAO_INVALIDO' were already marked as
	 * comment. It will stay like that for future analyse
	 */
	// private final String CODIGO_INVALIDO = "Codigo Invalido.";
	private final String CODIGO_BRANCO = "Codigo em Branco.";
	private final String CODIGO_NULO = "Codigo esta Nulo.";
	// private final String DESCRICAO_INVALIDO = "Descricao Invalido.";
	private final String DESCRICAO_BRANCO = "Descricao em Branco.";
	private final String DESCRICAO_NULO = "Descricao esta Nula.";

	/**
	 * Constructor method for class Patrimonio
	 * @param codigo
	 * @param descricao
	 * @throws PatrimonioException
	 */
	public Patrimonio(String codigo, String descricao)
			throws PatrimonioException {
		this.setCodigo(codigo);
		this.setDescricao(descricao);
	}

	/**
	 * Getter method to attribute 'Codigo'
	 * @return codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Getter method to attribute 'Descricao'
	 * @return descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * Setter method for attribute 'Codigo'
	 * @param codigo
	 * @throws PatrimonioException
	 */
	public void setCodigo(String codigo) throws PatrimonioException {
		if ( codigo == null ) {
			throw new PatrimonioException(CODIGO_NULO);
		} else if ( "".equals(codigo.trim()) ) {
			throw new PatrimonioException(CODIGO_BRANCO);
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
		 
		this.codigo = codigo;
	}

	/**
	 * Setter method for attribute 'Descricao'
	 * @param descricao
	 * @throws PatrimonioException
	 */
	public void setDescricao(String descricao) throws PatrimonioException {
		if ( descricao == null ) { 
			throw new PatrimonioException(DESCRICAO_NULO);
		} else if ( "".equals(descricao.trim()) ) {
			throw new PatrimonioException(DESCRICAO_BRANCO);
		} else { 
			// Nothing to do 
		}
		this.descricao = descricao;
	}

	/**
	 * Function to validade if a passed Patrimonio object is equals to 
	 * the new instanciated Patrimonio object
	 * @param Patrimonio e
	 * @return boolean
	 */
	public boolean equals(Patrimonio e) {
		if ( this.getCodigo().equals(e.getCodigo())
				&& this.getDescricao().equals(e.getDescricao()) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	/**
	 * To String method of class Patrimonio
	 * @return String
	 */
	public String toString() {
		return "Codigo=" + codigo + "\nDescricao=" + descricao;
	}
}
