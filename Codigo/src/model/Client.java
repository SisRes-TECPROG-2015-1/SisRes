package model;

import exception.ClientException;

public abstract class Client {

	/**
	 * All the attributes are setted by String type because it will be simpler
	 * to validade and catch the system data
	 */
	private String name;
	private String cpf;
	private String fone;
	private String email;
	protected String registration;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'TELEFONE_BRANCO', 'EMAIL_INVALIDO', 'EMAIL_BRANCO' were already marked as
	 * comment. It will stay like that for future analyse
	 */
	private final String NAME_INVALID = "Nome Invalido.";
	private final String NAME_WHITE = "Nome em Branco.";
	private final String NAME_NULL = "Nome esta Nulo.";
	private final String CPF_INVALID = "CPF Invalido.";
	private final String CPF_WHITE = "CPF em Branco.";
	private final String CPF_NULL = "CPF esta Nulo.";
	private final String FONE_INVALID = "Telefone Invalido.";
	// private final String TELEFONE_BRANCO = "Telefone em Branco.";
	private final String FONE_NULL = "Telefone esta Nulo.";
	// private final String EMAIL_INVALIDO = "E-mail Invalido.";
	// private final String EMAIL_BRANCO = "E-mail em Branco.";
	private final String EMAIL_NULL = "E-mail esta Nulo.";

	/**
	 * Constructor methd for class Cliente
	 * 
	 * @param name
	 * @param cpf
	 * @param registration
	 * @param fone
	 * @param email
	 * @throws ClientException
	 */
	public Client( String name, String cpf, String registration, String fone,
			String email ) throws ClientException {
		this.setNome( name );
		this.setCpf( cpf );
		this.setRegistration( registration );
		this.setTelefone( fone );
		this.setEmail( email );
	}

	/**
	 * Getter function for 'Name' attribute
	 * 
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter function for 'CPF' attribute
	 * 
	 * @return String cpf
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * Getter function for 'Telefone' attribute
	 * 
	 * @return String telefone
	 */
	public String getFone() {
		return fone;
	}

	/**
	 * Getter function for 'Email' attribute
	 * 
	 * @return String email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Getter function for 'Registration' attribute
	 * 
	 * @return String registration
	 */
	public String getRegistration() {
		return registration;
	}

	/**
	 * Setter method for 'Name' attribute
	 * 
	 * @param name
	 * @throws ClientException
	 */
	public void setNome( String name ) throws ClientException {
		if ( name == null ) {
			throw new ClientException( NAME_NULL );
		} else if ( "".equals( name.trim() ) ) {
			throw new ClientException( NAME_WHITE );
		} else if ( name.trim().matches( "[a-zA-Z][a-zA-Z\\s]+" ) ) {
			this.name = name.trim();
		} else {
			throw new ClientException( NAME_INVALID );
		}
	}

	/**
	 * Setter method for 'CPF' attribute
	 * 
	 * @param cpf
	 * @throws ClientException
	 */
	public void setCpf( String cpf ) throws ClientException {
		if ( cpf == null ) {
			throw new ClientException( CPF_NULL );
		} else if ( "".equals(cpf) ) {
			throw new ClientException( CPF_WHITE );
		} else if ( cpf.matches( "[\\d]{3,3}.[\\d]{3,3}.[\\d]{3,3}-[\\d]{2,2}$" ) ) {
			if ( this.validateCpf( cpf.split( "[\\. | -]" )[0]
					+ cpf.split( "[\\. | -]" )[1] + cpf.split( "[\\. | -]" )[2]
					+ cpf.split( "[\\. | -]" )[3] ) ) {
				this.cpf = cpf;
			} else {
				throw new ClientException(CPF_INVALID);
			}
		} else {
			throw new ClientException(CPF_INVALID);
		}
	}

	/**
	 * Setter method for 'Fone' attribute
	 * 
	 * @param fone
	 * @throws ClientException
	 */
	public void setTelefone( String fone ) throws ClientException {
		if ( fone == null ) {
			throw new ClientException( FONE_NULL );
		} else if ( "".equals( fone ) ) {
			this.fone = fone;
		} else if ( fone
				.matches( "(\\([ ]*[\\d]{2,3}[ ]*\\))?[ ]*[\\d]{4,4}[ ]*-?[ ]*[\\d]{4,4}[ ]*$" ) ) {
			// The phone number will be saved without blank spaces
			this.fone = fone.replaceAll( " ", "" );
		} else {
			throw new ClientException( FONE_INVALID );
		}
	}

	/**
	 * Setter method for 'Email' attribute
	 * 
	 * @param email
	 * @throws ClientException
	 */
	public void setEmail( String email ) throws ClientException {
		if ( email == null ) { 
			throw new ClientException( EMAIL_NULL );
		} else {
			this.email = email;
		}
	}

	/**
	 * Setter method for 'Registration' attribute
	 * 
	 * @param registration
	 * @throws ClientException
	 */
	public abstract void setRegistration( String registration ) throws ClientException;

	@Override
	/**
	 * To String method of class Client
	 * 
	 * @return String 
	 */
	public String toString() {
		return "Nome: " + name + "\nCpf: " + cpf + "\nTelefone: " + fone
				+ "\nEmail: " + email + "\nMatricula: " + registration;
	}

	/**
	 * Function to validate if a passed client object is equals to 
	 * the new instanciated client object
	 *  
	 * @param Client b
	 * @return boolean
	 */
	public boolean equals( Client b ) {
		if ( this.getName().equals( b.getName() )
				&& this.getCpf().equals( b.getCpf() )
				&& this.getRegistration().equals( b.getRegistration() )
				&& this.getFone().equals( b.getFone() )
				&& this.getEmail().equals( b.getEmail() ) ) {

			return true;
		}
		return false;
	}

	/**
	 * Function to validade if the setted cpf is valid or not
	 * 
	 * @param cpf
	 * @return boolean
	 */
	private boolean validateCpf( String cpf ) {

		int d1, d2;
		int digit1, digit2, resto;
		int digitoCPF;
		String nDigResult;

		d1 = d2 = 0;
		digit1 = digit2 = resto = 0;

		for ( int nCount = 1; nCount < cpf.length() - 1; nCount++ ) {
			digitoCPF = Integer.valueOf( cpf.substring( nCount - 1, nCount ) )
					.intValue();

			/**
			 * In this line is made a multiplication of the lasts digits 
			 * by 2 for the last, 3 for the antepenult and so on
			 */
			d1 = d1 + ( 11 - nCount ) * digitoCPF;

			/**
			 * For the second digit it will repeat the operation made
			 * in the previous line
			 */
			d2 = d2 + ( 12 - nCount ) * digitoCPF;
		}

		/**
		 *  Calculating the first rest of division per 11.
		 *  11 is the quantity of digits in a cpf
		 */
		resto = ( d1 % 11 );

		/**
		 * If the result is 0 or 1, the first verification digit is 0,
		 * else the first verification digit is 11 least the rest of the division
		 */
		if ( resto < 2 ){
			digit1 = 0;
		} else {
			digit1 = 11 - resto;
		}
		
		d2 += 2 * digit1;

		//Calculating the second rest of division per 11.
		resto = ( d2 % 11 );

		/**
		 * If the result is 0 or 1, the second verification digit is 0,
		 * else the second verification digit is 11 least the rest of the division
		 */
		if ( resto < 2 ) {
			digit2 = 0;
		} else {
			digit2 = 11 - resto;
		}
		
		// Generating the validation digit for the analysed cpf
		String nDigVerific = cpf.substring( cpf.length() - 2, cpf.length() );

		// Concatenating the firs and the second verification digit to make the validation digit
		nDigResult = String.valueOf( digit1 ) + String.valueOf( digit2 );

		// Comparing the two validation digits to know if the cpf is valid
		return nDigVerific.equals( nDigResult );

	}

}
