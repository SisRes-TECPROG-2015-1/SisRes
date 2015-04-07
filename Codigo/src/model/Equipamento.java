package model;

import exception.PatrimonioException;

/**
 * Equipment and rooms reservation system
 * Equipamento class extending Patrimonio class
 */
public class Equipamento extends Patrimonio {

	/**
	 * Constructor method for class Equipamento
	 * @param codigo
	 * @param descricao
	 * @throws PatrimonioException
	 */
	public Equipamento( String codigo, String descricao )
			throws PatrimonioException {
		super( codigo, descricao );
	}

}
