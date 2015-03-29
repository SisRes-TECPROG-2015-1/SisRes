package model;

import exception.ReservaException;

public class ReservaSala extends Reserva {

	private Sala sala;
	private String finalidade;

	// Message for exception
	private final String SALA_NULO = "A sala esta nula.";
	private final String FINALIDADE_NULA = "A finalidade esta nula.";
	private final String FINALIDADE_BRANCO = "A finalidade esta em branco.";

	/**
	 * Constructor method for ReservaSala model class
	 * @param data
	 * @param hora
	 * @param sala
	 * @param finalidade
	 * @throws ReservaException
	 */
	public ReservaSala( String data, String hora, Sala sala, String finalidade )
			throws ReservaException {
		super( data, hora );
		this.setSala( sala );
		this.setFinalidade( finalidade );
	}

	/**
	 * Getter method for attribute 'sala'
	 * @return
	 */
	public Sala getSala() {
		return this.sala;
	}

	/**
	 * Getter method for attribute 'finalidade'
	 * @return
	 */
	public String getFinalidade() {
		return this.finalidade;
	}

	/**
	 * Setter method for attribute 'sala'
	 * @return
	 */
	public void setSala( Sala sala ) throws ReservaException {
		if ( sala == null )
			throw new ReservaException( SALA_NULO );
		this.sala = sala;
	}

	/**
	 * Setter method for attribute 'finalidade'
	 * @return
	 */
	public void setFinalidade( String finalidade ) throws ReservaException {
		if ( finalidade == null )
			throw new ReservaException( FINALIDADE_NULA );

		finalidade = finalidade.trim();
		if ( finalidade.equals( "" ) )
			throw new ReservaException( FINALIDADE_BRANCO );
		else
			this.finalidade = finalidade;
	}

	/**
	 * Function to validate if an object passed is equal to an instancied object
	 * @param obj
	 * @return
	 */
	public boolean equals( ReservaSala obj ) {
		return ( super.equals( obj ) && this.getSala().equals( obj.getSala() ) && this
				.getFinalidade().equals( obj.getFinalidade() ) );
	}

	@Override
	/**
	 * To string method for ReservaSala class
	 */
	public String toString() {
		return "\n" + this.getSala().toString() + "\nFinalidade="
				+ this.getFinalidade() + super.toString();
	}

}
