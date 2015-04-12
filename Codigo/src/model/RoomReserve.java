package model;

import exception.ReserveException;

public class RoomReserve extends Reserva {

	private Sala room;
	private String finality;

	// Message for exception
	private final String NULL_ROOM = "A sala esta nula.";
	private final String NULL_FINALITY = "A finalidade esta nula.";
	private final String BLANK_FINALITY = "A finalidade esta em branco.";

	/**
	 * Constructor method for ReservaSala model class
	 * @param date
	 * @param hour
	 * @param room
	 * @param finality
	 * @throws ReserveException
	 */
	public RoomReserve( String date, String hour, Sala room, String finality )
			throws ReserveException {
		super( date, hour );
		this.setSala( room );
		this.setFinality( finality );
	}

	/**
	 * Getter method for attribute 'room'
	 * @return
	 */
	public Sala getSala() {
		return this.room;
	}

	/**
	 * Getter method for attribute 'finality'
	 * @return
	 */
	public String getFinality() {
		return this.finality;
	}

	/**
	 * Setter method for attribute 'room'
	 * @return
	 */
	public void setSala( Sala room ) throws ReserveException {
		if ( room == null )
			throw new ReserveException( NULL_ROOM );
		this.room = room;
	}

	/**
	 * Setter method for attribute 'finality'
	 * @return
	 */
	public void setFinality( String finality ) throws ReserveException {
		if ( finality == null )
			throw new ReserveException( NULL_FINALITY );

		finality = finality.trim();
		if ( finality.equals( "" ) )
			throw new ReserveException( BLANK_FINALITY );
		else
			this.finality = finality;
	}

	/**
	 * Function to validate if an roomReserveObjectect passed is equal to an instancied roomReserveObjectect
	 * @param roomReserveObject
	 * @return
	 */
	public boolean equals( RoomReserve roomReserveObject ) {
		return ( super.equals( roomReserveObject ) && this.getSala().equals( roomReserveObject.getSala() ) && this
				.getFinality().equals( roomReserveObject.getFinality() ) );
	}

	@Override
	/**
	 * To string method for ReservaSala class
	 */
	public String toString() {
		return "\n" + this.getSala().toString() + "\nFinalidade="
				+ this.getFinality() + super.toString();
	}

}
