package model;

import exception.ReservaException;

public class ReservaSalaProfessor extends ReservaSala {

	private Professor professor;

	// Message for exception
	private final String PROFESSOR_NULO = "O professor esta nulo.";

	/**
	 * Constructor method for class ReservaSalaProfessor
	 * @param data
	 * @param hora
	 * @param sala
	 * @param finalidade
	 * @param professor
	 * @throws ReservaException
	 */
	public ReservaSalaProfessor( String data, String hora, Sala sala,
			String finalidade, Professor professor ) throws ReservaException {
		super( data, hora, sala, finalidade );
		this.setProfessor( professor );
	}

	/**
	 * Getter method for attribute 'professor'
	 * @return
	 */
	public Professor getProfessor() {
		return this.professor;
	}

	/**
	 * Setter method for attribute 'professor'
	 * @return
	 */
	public void setProfessor( Professor professor ) throws ReservaException {
		if ( professor == null ) {
			throw new ReservaException( PROFESSOR_NULO );
		} else { 
			// Nothing to do
		}
		this.professor = professor;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( ReservaSalaProfessor obj ) {
		return ( super.equals( obj ) && this.getProfessor().equals(
				obj.getProfessor() ) );
	}

	@Override
	/**
	 * To string method for class ReservaSalaProfessor
	 */
	public String toString() {
		return "ReservaSalaProfessor [professor="
				+ this.getProfessor().toString() + ", toString()="
				+ super.toString() + "]";
	}

}
