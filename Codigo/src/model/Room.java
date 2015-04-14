package model;

import exception.PatrimonyException;

public class Room extends Heritage {

	private String capacity;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'CAPACIDADE_NEGATIVA' WAS already marked as comment.
	 * It will stay like that for future analyse
	 */
	private final String CAPACITY_INVALID = "Capacidade Invalida.";
	private final String CAPACITY_WHITE = "Capacidade em Branco.";
	private final String CAPACITY_NULL = "Capacidade esta nula.";

	// private final String CAPACIDADE_NEGATIVA = "Capacidade negativa.";

	/**
	 * Constructor method for class Room
	 * @param code
	 * @param description
	 * @param capacity
	 * @throws PatrimonyException
	 */
	public Room( String code, String description, String capacity )
			throws PatrimonyException {
		super( code, description );
		this.setCapacity( capacity );
	}

	/**
	 * Getter method for attribute 'capacity'
	 * @return
	 */
	public String getCapacity() {
		return capacity;
	}

	/**
	 * Setter method for attribute 'capacity'
	 * @return
	 */
	public void setCapacity( String capacity ) throws PatrimonyException {
		if ( capacity == null ) { 
			throw new PatrimonyException( CAPACITY_NULL );
		} else if ( "".equals( capacity.trim() ) ) {
			throw new PatrimonyException( CAPACITY_WHITE );
		} else if ( capacity.matches( "[\\d]+" ) ) {
			this.capacity = capacity;
		} else {
			throw new PatrimonyException( CAPACITY_INVALID );
		}
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param b
	 * @return
	 */
	public boolean equals( Room b ) {
		if ( super.equals( b )
				&& this.getCapacity().equals( b.getCapacity() ) ) {
			return true;
		} else { 
			// Nothing to do
		}

		return false;
	}
}
