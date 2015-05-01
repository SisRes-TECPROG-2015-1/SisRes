package persistence;

import model.Student;

import java.sql.*;
import java.util.Vector;

import exception.ClientException;


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
	public void includeNewStudent( Student student ) throws SQLException, ClientException {
		if ( student == null ) {
			throw new ClientException(nullStudent);
		} else if ( this.inDBCpf( student.getCpf() ) ){
			throw new ClientException( existentCPF );
		} else if ( this.inDBMatricula( student.getRegistration() ) ){
				throw new ClientException( ExistentRegistration );
		} else if ( !this.inDB( student ) ) {
			this.updateQuery( "INSERT INTO " +
					"aluno ( nome, cpf, telefone, email, matricula ) VALUES (" +
					"\"" + student.getName() + "\", " +
					"\"" + student.getCpf()+ "\", " +
					"\"" + student.getFone() + "\", " +
					"\"" + student.getEmail() + "\", " +
					"\"" + student.getRegistration() + "\"); "
					);
		} else {
			throw new ClientException( existentStudent );
		}
	}
	
	/**
	 * Method to modify a student into the database
	 * @param oldStudent: indicates the student before the modifying
	 * @param newStudent: indicates the student after the changes
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void modifyStudent( Student oldStudent, Student newStudent ) throws SQLException, ClientException {
		if( oldStudent == null || newStudent == null ) {
			throw new ClientException( nullStudent );
		}
				
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;
		
		if( !this.inDB( oldStudent ) ) {
			throw new ClientException( noExistentStudent );
		} else if ( this.inOtherDB( oldStudent ) ) {
			throw new ClientException( studentInUse );
		} else if ( !oldStudent.getCpf().equals( newStudent.getCpf() ) && this.inDBCpf( newStudent.getCpf() ) ){
			throw new ClientException( existentCPF );
		} else if ( !oldStudent.getRegistration().equals( newStudent.getRegistration() ) && this.inDBMatricula( newStudent.getRegistration() ) ) {
				throw new ClientException( ExistentRegistration );
		} else if( !this.inDB( newStudent ) ) {
			String msg = "UPDATE aluno SET " +
				"nome = \"" + newStudent.getName() + "\", " +
				"cpf = \"" + newStudent.getCpf() + "\", " +
				"telefone = \"" + newStudent.getFone() + "\", " +
				"email = \"" + newStudent.getEmail() + "\", " +
				"matricula = \"" + newStudent.getRegistration() + "\""+
				" WHERE " +
				"aluno.nome = \"" + oldStudent.getName() + "\" and " +
				"aluno.cpf = \"" + oldStudent.getCpf() + "\" and " +
				"aluno.telefone = \"" + oldStudent.getFone() + "\" and " +
				"aluno.email = \"" + oldStudent.getEmail() + "\" and " +
				"aluno.matricula = \"" + oldStudent.getRegistration() + "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new ClientException( existentStudent );
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
	public void deleteStudent( Student student ) throws SQLException, ClientException {
		if ( student == null ) {
			throw new ClientException( nullStudent );
		} else if ( this.inOtherDB( student ) ) {
			throw new ClientException( studentInUse );
		} else if ( this.inDB( student ) ) {
			this.updateQuery( "DELETE FROM aluno WHERE " +
				"aluno.nome = \"" + student.getName() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getFone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getRegistration() + "\";"
				);
		} else {
			throw new ClientException( noExistentStudent );
		}
	}

	
	/**
     * Captures the students
     * @return Vector - All the students
	 * @throws ClientException 
     */
	public Vector<Student> captureStudents() throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno;" ); 
		return student;
	}
	
	/**
     * Captures the students by their name.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByName( String nameValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno WHERE nome = " + "\"" + nameValue + "\";" );
		return student;
	}
	
	/**
     * Captures the students by their cpf.
     * @return Vector - Students
	 * @throws ClienteException 
     */
	public Vector<Student> searchByCpf( String cpfValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno WHERE cpf = " + "\"" + cpfValue + "\";" );
		return student;
	}
	
	/**
     * Captures the students by their registration
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByRegistration( String registrationValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno WHERE registration = " + "\"" + registrationValue + "\";" );
		return student;
	}
	
	/**
     * Captures the students by their e-mail.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByEmail( String emailValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno WHERE email = " + "\"" + emailValue + "\";" );
		return student;
	}
	
	/**
     * Captures the students by their phoneNumber.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByPhoneNumber( String phoneNumberValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM aluno WHERE phoneNumber = " + "\"" + phoneNumberValue + "\";" );
		return student;
	}
	
	
	//Metodos Privados
	/**
     * Searches for a student by a given query
     * @return Vector - Students
	 * @throws ClientException 
     */
	private Vector<Student> search( String query ) throws SQLException, ClientException, ClientException {
		Vector<Student> vet = new Vector<Student>();
		
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
		
		if( rs.next() ) {
			rs.close();
			pst.close();
			con.close();
			return true;
		} else {
			rs.close();
			pst.close();
			con.close();
			return false;
		}
	}
	
	/**
     * Verifies if the given student exists in database
     * @return Boolean - Existence of a student 
     */
	private boolean inDB( Student student ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + student.getName() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getFone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getRegistration() + "\";" );
		
		return select;
	}
	
	/**
     * Verifies if the student exists in database by his cpf
     * @return Boolean - Existence of a student 
     */
	private boolean inDBCpf( String CPFCode ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.cpf = \"" + CPFCode + "\";" );
		return select;
	}
	
	
	/**
     * Verifies if the student exists in database by his registration
     * @return Boolean - Existence of a student 
     */
	private boolean inDBMatricula( String registrationCode ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.matricula = \"" + registrationCode + "\";" );
		return select;
	}
	
	/**
     * Verifies if the student exists in database
     * @return Boolean - Existence of an student 
     */
	private boolean inOtherDB( Student student ) throws SQLException, ClientException {
		boolean select = this.inDBGeneric(
				"SELECT * FROM reserva_sala_aluno WHERE " +
				"id_aluno = (SELECT id_aluno FROM aluno WHERE " +
				"aluno.nome = \"" + student.getName() + "\" and " +
				"aluno.cpf = \"" + student.getCpf() + "\" and " +
				"aluno.telefone = \"" + student.getFone() + "\" and " +
				"aluno.email = \"" + student.getEmail() + "\" and " +
				"aluno.matricula = \"" + student.getRegistration() + "\");" );
		return select;
	}
	
	/**
     * Captures the next student resulted of the query made before 
     * @return Aluno - Student  
	 * @throws ClientException 
     */
	private Student fetchAluno( ResultSet resultSet ) throws ClientException, SQLException, ClientException {
		Student student = new Student( resultSet.getString( "nome" ), resultSet.getString( "cpf" ), resultSet.getString( "matricula" ),
				resultSet.getString( "telefone" ), resultSet.getString( "email" ) );
		return student;
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
