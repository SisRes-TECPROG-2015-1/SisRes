package persistence;

import model.Aluno;

import java.sql.*;
import java.util.Vector;

import exception.ClienteException;

public class StudentDAO {

		private static final String existentStudent = "O Aluno ja esta cadastrado."; // Indicates a student already registered
		private static final String nullStudent = "O Aluno esta nulo."; // Indicates the student is null
		private static final String noExistentStudent = "O Aluno nao esta cadastrado."; // Student not registered
		private static final String studentInUse = "Sala esta sendo utilizada em uma reserva."; // Student already registered into a room
		private static final String existentCPF = "Ja existe um aluno cadastrado com esse CPF."; // Already exist a student with this cpf
		private static final String ExistentRegistration = "Ja existe um aluno cadastrado com essa matricula."; // Already exists a student with this registration
	
		private static StudentDAO instance;
	
		/**
		 * Empty constructor
		 */
		private StudentDAO() {
		}
		public static StudentDAO getInstance() {
			if( instance == null ) {
				instance = new StudentDAO();
			}
			return instance;
		}
	
	/**
	 * Method to include a new student
	 * @param aluno: represents a student object
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void includeNewStudent( Aluno student ) throws SQLException, ClienteException {
		if ( student == null ) {
			throw new ClienteException(nullStudent);
		} else if ( this.inDBCpf( student.getCpf() ) ){
			throw new ClienteException( existentCPF );
		} else if ( this.inDBMatricula( student.getMatricula() ) ){
				throw new ClienteException( ExistentRegistration );
		} else if ( !this.inDB( student ) ) {
			this.updateQuery( "INSERT INTO " +
					"aluno ( nome, cpf, telefone, email, matricula ) VALUES (" +
					"\"" + student.getNome() + "\", " +
					"\"" + student.getCpf()+ "\", " +
					"\"" + student.getTelefone() + "\", " +
					"\"" + student.getEmail() + "\", " +
					"\"" + student.getMatricula() + "\"); "
					);
		} else {
			throw new ClienteException( existentStudent );
		}
	}
	
	/**
	 * Method to modify a student into the database
	 * @param oldStudent: indicates the student before the modifying
	 * @param newStudent: indicates the student after the changes
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void modifyStudent( Aluno oldStudent, Aluno newStudent ) throws SQLException, ClienteException {
		if( oldStudent == null ) {
			throw new ClienteException( nullStudent );
		}
		if( newStudent == null ) {
			throw new ClienteException( nullStudent );
		}
		
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;
		
		if( !this.inDB( oldStudent ) ) {
			throw new ClienteException( noExistentStudent );
		} else if ( this.inOtherDB( oldStudent ) ) {
			throw new ClienteException( studentInUse );
		} else if ( !oldStudent.getCpf().equals( newStudent.getCpf() ) && this.inDBCpf( newStudent.getCpf() ) ){
			throw new ClienteException( existentCPF );
		} else if ( !oldStudent.getMatricula().equals( newStudent.getMatricula() ) && this.inDBMatricula( newStudent.getMatricula() ) ) {
				throw new ClienteException( ExistentRegistration );
		} else if( !this.inDB( newStudent ) ) {
			String msg = "UPDATE aluno SET " +
				"nome = \"" + newStudent.getNome() + "\", " +
				"cpf = \"" + newStudent.getCpf() + "\", " +
				"telefone = \"" + newStudent.getTelefone() + "\", " +
				"email = \"" + newStudent.getEmail() + "\", " +
				"matricula = \"" + newStudent.getMatricula() + "\""+
				" WHERE " +
				"aluno.nome = \"" + oldStudent.getNome() + "\" and " +
				"aluno.cpf = \"" + oldStudent.getCpf() + "\" and " +
				"aluno.telefone = \"" + oldStudent.getTelefone() + "\" and " +
				"aluno.email = \"" + oldStudent.getEmail() + "\" and " +
				"aluno.matricula = \"" + oldStudent.getMatricula() + "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new ClienteException( existentStudent );
		}

		pst.close();
		con.close();
	}
	
	/**
	 * Delete a student into the database
	 * @param student
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void deleteStudent( Aluno student ) throws SQLException, ClienteException {
		if ( student == null ) {
			throw new ClienteException( nullStudent );
		}
		else if ( this.inOtherDB( student ) ) {
			throw new ClienteException( studentInUse );
		} else if ( this.inDB( student ) ) {
			this.updateQuery( "DELETE FROM aluno WHERE " +
				"aluno.nome = \"" + student.getNome() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getTelefone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getMatricula() + "\";"
				);
		} else {
			throw new ClienteException( noExistentStudent );
		}
	}

	
	/**
     * Captures the students
     * @return Vector - All the students
     */
	public Vector<Aluno> captureStudents() throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno;" );
	}
	
	/**
     * Captures the students by their name.
     * @return Vector - Students
     */
	public Vector<Aluno> searchByName( String nameValue ) throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno WHERE nome = " + "\"" + nameValue + "\";" );
	}
	
	/**
     * Captures the students by their cpf.
     * @return Vector - Students
     */
	public Vector<Aluno> searchByCpf( String cpfValue ) throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno WHERE cpf = " + "\"" + cpfValue + "\";" );
	}
	
	/**
     * Captures the students by their matricula
     * @return Vector - Students
     */
	public Vector<Aluno> searchByRegistration( String registrationValue ) throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno WHERE matricula = " + "\"" + registrationValue + "\";" );
	}
	
	/**
     * Captures the students by their e-mail.
     * @return Vector - Students
     */
	public Vector<Aluno> searchByEmail( String emailValue ) throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno WHERE email = " + "\"" + emailValue + "\";" );
	}
	
	/**
     * Captures the students by their telephone.
     * @return Vector - Students
     */
	public Vector<Aluno> searchByPhoneNumber( String phoneNumberValue ) throws SQLException, ClienteException {
		return this.search( "SELECT * FROM aluno WHERE telefone = " + "\"" + phoneNumberValue + "\";" );
	}
	
	
	//Metodos Privados
	/**
     * Searches for a student by a given query
     * @return Vector - Students
     */
	private Vector<Aluno> search( String query ) throws SQLException, ClienteException {
		Vector<Aluno> vet = new Vector<Aluno>();
		
		Connection con =  FactoryConnection.getInstance().getConnection();
		
		PreparedStatement pst = con.prepareStatement(query);
		ResultSet rs = pst.executeQuery();
		
		while ( rs.next() ) {
			vet.add( this.fetchAluno( rs ) );
		}
		
		pst.close();
		rs.close();
		con.close();
		return vet;
	}
	
	/**
     * Verifies if there is a query
     * @return Boolean - Existence of a query
     */
	private boolean inDBGeneric( String query ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( query );
		ResultSet rs = pst.executeQuery();
		
		if( !rs.next() ) {
			rs.close();
			pst.close();
			con.close();
			return false;
		} else {
			rs.close();
			pst.close();
			con.close();
			return true;
		}
	}
	
	/**
     * Verifies if the given student exists in database
     * @return Boolean - Existence of a student 
     */
	private boolean inDB( Aluno student ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + student.getNome() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getTelefone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getMatricula() + "\";" );
	}
	
	/**
     * Verifies if the student exists in database by his cpf
     * @return Boolean - Existence of a student 
     */
	private boolean inDBCpf( String CPFCode ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.cpf = \"" + CPFCode + "\";" );
	}
	
	
	/**
     * Verifies if the student exists in database by his matricula
     * @return Boolean - Existence of a student 
     */
	private boolean inDBMatricula( String registrationCode ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.matricula = \"" + registrationCode + "\";" );
	}
	
	/**
     * Verifies if the student exists in database
     * @return Boolean - Existence of an student 
     */
	private boolean inOtherDB( Aluno student ) throws SQLException, ClienteException {
		return this.inDBGeneric(
				"SELECT * FROM reserva_sala_aluno WHERE " +
				"id_aluno = (SELECT id_aluno FROM aluno WHERE " +
				"aluno.nome = \"" + student.getNome() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getTelefone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getMatricula() + "\");" );
	}
	
	/**
     * Captures the next student resulted of the query made before 
     * @return Aluno - Student  
     */
	private Aluno fetchAluno( ResultSet resultSet ) throws ClienteException, SQLException {
		return new Aluno( resultSet.getString( "nome" ), resultSet.getString( "cpf" ), resultSet.getString( "matricula" ),
				resultSet.getString( "telefone" ), resultSet.getString( "email" ) );
	}
	
	/**
	 * Method to update the query
	 * @param queryMessage
	 * @throws SQLException
	 */
	private void updateQuery( String queryMessage ) throws SQLException {
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( queryMessage );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
