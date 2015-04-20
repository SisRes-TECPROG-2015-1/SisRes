package model;

import java.text.SimpleDateFormat;
import java.util.Date;

import exception.ReserveException;

public class Reserve {

	private String hour;
	private String data;

	// Defining constants for error messages and alerts.
	private final String HOUR_NULL = "A hora esta nula.";
	//private final String HOUR_INVALID = "A hora eh invalida.";
	private final String HOUR_WHITE = "A hora esta em branco.";
	private final String HOUR_PATTERN = "^[012]?[\\d]:[0-5][\\d]$";
	private final String DATE_NULL = "A data esta nula.";
	private final String DATE_INVALID = "A data eh invalida.";
	private final String DATE_WHITE = "A data esta em branco.";
	private final String DATE_PATTERN = "^[0123]?[\\d]([./-])[01]?[\\d]\\1[\\d]{2,4}$";
	
	/**
	 * Constructor for class Reserve
	 * @param date
	 * @param hour
	 * @throws ReserveException
	 */
	public Reserve( String date, String hour ) throws ReserveException {
		this.setData( date );
		this.setHour( hour );
	}
	
	/**
	 * Function to get the time value form 'hora' attribute
	 * @return String hora
	 */
	public String getHour() {
		return this.hour;
	}

	/**
	 * Function to get the date value form 'data' attribute
	 * @return
	 */
	public String getDate() {
		return this.data;
	}

	/**
	 * Setter function for 'hour' attribute
	 * @param hour
	 * @throws ReserveException
	 */
	public void setHour( String hour ) throws ReserveException {
		if ( hour == null ) { 
			throw new ReserveException( HOUR_NULL );
		}
		
		hour = hour.trim();
		if ( hour.equals( "" ) ) { 
			throw new ReserveException( HOUR_WHITE );
		} else if ( hour.matches( HOUR_PATTERN ) ) {
			if ( hour.length() == 4 ) {
				this.hour = "0" + hour;
			} else {
				this.hour = hour;
			}
		} else { 
			throw new ReserveException( HOUR_WHITE );
		}
	}

	/**
	 * Setter function for 'date'attribute
	 * @param date
	 * @throws ReserveException
	 */
	public void setData( String date ) throws ReserveException {
		if ( date == null ) {
			throw new ReserveException(DATE_NULL);
		}else{
			// Nothing to do
		}

		date = date.trim();
		if ( date.equals( "" ) ) { 
			throw new ReserveException( DATE_WHITE );
		} else if ( date.matches( DATE_PATTERN ) ) {
			this.data = padronizarData( date );
		} else { 
			throw new ReserveException( DATE_INVALID );
		}
	}

	/**
	 * Function to validate if an object passed is equal to an instanced object
	 * @param obj
	 * @return
	 */
	public boolean equals( Reserve obj ) {
		return ( this.hour.equals( obj.getHour() ) && this.data.equals( obj
				.getDate() ) );
	}

	@Override
	/**
	 * To string method of Reserve class
	 */
	public String toString() {
		return "\nHora=" + this.hour + "\nData=" + this.data;
	}

	/**
	 * Method to standardize date format
	 * @param date
	 * @return String
	 */
	private static String padronizarData( String date ) {
		String now[] = actualDate().split( "[./-]" );
		String parts[] = date.split( "[./-]" );
		String dateInPattern = "";

		for ( int i = 0; i < 3; i++ ) {
			if ( i == 0 ) {
				dateInPattern += now[i].substring(0, now[i].length()
						- parts[i].length())
						+ parts[i];
			} else { 
				dateInPattern += "/"
						+ now[i].substring(0,
								now[i].length() - parts[i].length())
						+ parts[i];
			}
		}

		return dateInPattern;
	}

	/**
	 * Method to get the current date from system
	 * @return String
	 */
	private static String actualDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}
}
