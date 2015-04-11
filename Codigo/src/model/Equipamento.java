package model;

import exception.PatrimonyException;

public class Equipamento extends Patrimonio {

	/**
	 * Constructor method for class Equipamento
	 * @param codigo
	 * @param descricao
	 * @throws PatrimonyException
	 */
	public Equipamento( String codigo, String descricao )
			throws PatrimonyException {
		super( codigo, descricao );
	}

}
