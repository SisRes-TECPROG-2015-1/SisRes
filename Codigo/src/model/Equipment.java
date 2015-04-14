package model;

import exception.PatrimonyException;

public class Equipment extends Heritage {

	/**
	 * Constructor method for class Equipment
	 * @param code
	 * @param description
	 * @throws PatrimonyException
	 */
	public Equipment( String code, String description )
			throws PatrimonyException {
		super( code, description );
	}

}
