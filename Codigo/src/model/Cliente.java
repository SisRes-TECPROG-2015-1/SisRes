package model;

import exception.ClienteException;

public abstract class Cliente {

	/**
	 * All the attributes are setted by String type because it will be simpler
	 * to validade and catch the system data
	 */
	private String nome;
	private String cpf;
	private String telefone;
	private String email;
	protected String matricula;

	/**
	 * Defining constants for error messages and alerts. Constants
	 * 'TELEFONE_BRANCO', 'EMAIL_INVALIDO', 'EMAIL_BRANCO' were already marked as
	 * comment. It will stay like that for future analyse
	 */
	private final String NOME_INVALIDO = "Nome Invalido.";
	private final String NOME_BRANCO = "Nome em Branco.";
	private final String NOME_NULO = "Nome esta Nulo.";
	private final String CPF_INVALIDO = "CPF Invalido.";
	private final String CPF_BRANCO = "CPF em Branco.";
	private final String CPF_NULO = "CPF esta Nulo.";
	private final String TELEFONE_INVALIDO = "Telefone Invalido.";
	// private final String TELEFONE_BRANCO = "Telefone em Branco.";
	private final String TELEFONE_NULO = "Telefone esta Nulo.";
	// private final String EMAIL_INVALIDO = "E-mail Invalido.";
	// private final String EMAIL_BRANCO = "E-mail em Branco.";
	private final String EMAIL_NULO = "E-mail esta Nulo.";

	/**
	 * Constructor methd for class Cliente
	 * 
	 * @param nome
	 * @param cpf
	 * @param matricula
	 * @param telefone
	 * @param email
	 * @throws ClienteException
	 */
	public Cliente(String nome, String cpf, String matricula, String telefone,
			String email) throws ClienteException {
		this.setNome(nome);
		this.setCpf(cpf);
		this.setMatricula(matricula);
		this.setTelefone(telefone);
		this.setEmail(email);
	}

	/**
	 * Getter function for 'Nome' attribute
	 * 
	 * @return String nome
	 */
	public String getNome() {
		return nome;
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
	public String getTelefone() {
		return telefone;
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
	 * Getter function for 'Matricula' attribute
	 * 
	 * @return String matricula
	 */
	public String getMatricula() {
		return matricula;
	}

	/**
	 * Setter method for 'Nome' attribute
	 * 
	 * @param nome
	 * @throws ClienteException
	 */
	public void setNome(String nome) throws ClienteException {
		if ( nome == null ) {
			throw new ClienteException(NOME_NULO);
		} else if ( "".equals(nome.trim()) ) {
			throw new ClienteException(NOME_BRANCO);
		} else if ( nome.trim().matches("[a-zA-Z][a-zA-Z\\s]+") ) {
			this.nome = nome.trim();
		} else {
			throw new ClienteException(NOME_INVALIDO);
		}
	}

	/**
	 * Setter method for 'CPF' attribute
	 * 
	 * @param cpf
	 * @throws ClienteException
	 */
	public void setCpf(String cpf) throws ClienteException {
		if ( cpf == null ) {
			throw new ClienteException(CPF_NULO);
		} else if ( "".equals(cpf) ) {
			throw new ClienteException(CPF_BRANCO);
		} else if ( cpf.matches("[\\d]{3,3}.[\\d]{3,3}.[\\d]{3,3}-[\\d]{2,2}$") ) {
			if ( this.validarCpf(cpf.split("[\\. | -]")[0]
					+ cpf.split("[\\. | -]")[1] + cpf.split("[\\. | -]")[2]
					+ cpf.split("[\\. | -]")[3]) ) {
				this.cpf = cpf;
			} else {
				throw new ClienteException(CPF_INVALIDO);
			}
		} else {
			throw new ClienteException(CPF_INVALIDO);
		}
	}

	/**
	 * Setter method for 'Telefone' attribute
	 * 
	 * @param telefone
	 * @throws ClienteException
	 */
	public void setTelefone(String telefone) throws ClienteException {
		if ( telefone == null ) {
			throw new ClienteException(TELEFONE_NULO);
		} else if ( "".equals(telefone) ) {
			this.telefone = telefone;
		} else if ( telefone
				.matches("(\\([ ]*[\\d]{2,3}[ ]*\\))?[ ]*[\\d]{4,4}[ ]*-?[ ]*[\\d]{4,4}[ ]*$") ) {
			// The phone number will be saved without blank spaces
			this.telefone = telefone.replaceAll(" ", "");
		} else {
			throw new ClienteException(TELEFONE_INVALIDO);
		}
	}

	/**
	 * Setter method for 'Email' attribute
	 * 
	 * @param email
	 * @throws ClienteException
	 */
	public void setEmail(String email) throws ClienteException {
		if ( email == null ) { 
			throw new ClienteException(EMAIL_NULO);
		} else {
			this.email = email;
		}
	}

	/**
	 * Setter method for 'Matricula' attribute
	 * 
	 * @param matricula
	 * @throws ClienteException
	 */
	public abstract void setMatricula(String matricula) throws ClienteException;

	@Override
	/**
	 * To String method of class Cliente
	 * 
	 * @return String 
	 */
	public String toString() {
		return "Nome: " + nome + "\nCpf: " + cpf + "\nTelefone: " + telefone
				+ "\nEmail: " + email + "\nMatricula: " + matricula;
	}

	/**
	 * Function to validade if a passed client object is equals to 
	 * the new instanciated client object
	 *  
	 * @param Cliente b
	 * @return boolean
	 */
	public boolean equals(Cliente b) {
		if (this.getNome().equals(b.getNome())
				&& this.getCpf().equals(b.getCpf())
				&& this.getMatricula().equals(b.getMatricula())
				&& this.getTelefone().equals(b.getTelefone())
				&& this.getEmail().equals(b.getEmail())) {

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
	private boolean validarCpf(String cpf) {

		int d1, d2;
		int digito1, digito2, resto;
		int digitoCPF;
		String nDigResult;

		d1 = d2 = 0;
		digito1 = digito2 = resto = 0;

		for ( int nCount = 1; nCount < cpf.length() - 1; nCount++ ) {
			digitoCPF = Integer.valueOf(cpf.substring(nCount - 1, nCount))
					.intValue();

			/**
			 * In this line is made a multiplication of the lasts digits 
			 * by 2 for the last, 3 for the antepenult and so on
			 */
			d1 = d1 + (11 - nCount) * digitoCPF;

			/**
			 * For the second digit it will repeat the operation made
			 * in the previous line
			 */
			d2 = d2 + (12 - nCount) * digitoCPF;
		}

		/**
		 *  Calculating the first rest of division per 11.
		 *  11 is the quantity of digits in a cpf
		 */
		resto = (d1 % 11);

		/**
		 * If the result is 0 or 1, the first verification digit is 0,
		 * else the first verification digit is 11 least the rest of the division
		 */
		if ( resto < 2 ){
			digito1 = 0;
		} else {
			digito1 = 11 - resto;
		}
		
		d2 += 2 * digito1;

		//Calculating the second rest of division per 11.
		resto = (d2 % 11);

		/**
		 * If the result is 0 or 1, the second verification digit is 0,
		 * else the second verification digit is 11 least the rest of the division
		 */
		if ( resto < 2 ) {
			digito2 = 0;
		} else {
			digito2 = 11 - resto;
		}
		
		// Generating the validation digit for the analysed cpf
		String nDigVerific = cpf.substring(cpf.length() - 2, cpf.length());

		// Concatenating the firs and the second verification digit to make the validation digit
		nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

		// Comparing the two validation digits to know if the cpf is valid
		return nDigVerific.equals(nDigResult);

	}

}
