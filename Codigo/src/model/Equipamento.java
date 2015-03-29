package model;

import exception.PatrimonioException;

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
