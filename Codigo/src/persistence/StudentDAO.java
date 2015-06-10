package persistence;

import model.Student;

import java.sql.*;
import java.util.Vector;

import exception.ClientException;

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;




public class StudentDAO {

		private static final String existentStudent = "O student ja esta cadastrado."; // Indicates a student already registered
		private static final String nullStudent = "O student esta nulo."; // Indicates the student is null
		private static final String noExistentStudent = "O student nao esta cadastrado."; // Student not registered
		private static final String studentInUse = "Sala esta sendo utilizada em uma reserva."; // Student already registered into a room
		private static final String existentCPF = "Ja existe um student cadastrado com esse CPF."; // Already exist a student with this cpf
		private static final String ExistentRegistration = "Ja existe um student cadastrado com essa registration."; // Already exists a student with this registration
		static final Logger logger = LogManager.getLogger( StudentDAO.class.getName() );
		
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
	 * @param student: represents a student object
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void includeNewStudent( Student student ) throws SQLException, ClientException {
		if ( student == null ) {
			throw new ClientException(nullStudent);
		} else if ( this.inDBCpf( student.getCpf() ) ){
			throw new ClientException( existentCPF );
		} else if ( this.inDBregistration( student.getRegistration() ) ){
				throw new ClientException( ExistentRegistration );
		} else if ( !this.inDB( student ) ) {
			logger.trace( "Saving new user." );
			this.updateQuery( "INSERT INTO " +
					"student ( name, cpf, fone, email, registration ) VALUES (" +
					"\"" + student.getName() + "\", " +
					"\"" + student.getCpf()+ "\", " +
					"\"" + student.getFone() + "\", " +
					"\"" + student.getEmail() + "\", " +
					"\"" + student.getRegistration() + "\"); "
					);
			logger.trace( "User has been saved." );
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
		}else{
        	//do nothing
        }
				
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;
		
		if( !this.inDB( oldStudent ) ) {
			throw new ClientException( noExistentStudent );
		} else if ( this.inOtherDB( oldStudent ) ) {
			throw new ClientException( studentInUse );
		} else if ( !oldStudent.getCpf().equals( newStudent.getCpf() ) && this.inDBCpf( newStudent.getCpf() ) ){
			throw new ClientException( existentCPF );
		} else if ( !oldStudent.getRegistration().equals( newStudent.getRegistration() ) && this.inDBregistration( newStudent.getRegistration() ) ) {
				throw new ClientException( ExistentRegistration );
		} else if( !this.inDB( newStudent ) ) {
			logger.trace( "Starting to update student." );
			String msg = "UPDATE student SET " +
				"name = \"" + newStudent.getName() + "\", " +
				"cpf = \"" + newStudent.getCpf() + "\", " +
				"fone = \"" + newStudent.getFone() + "\", " +
				"email = \"" + newStudent.getEmail() + "\", " +
				"registration = \"" + newStudent.getRegistration() + "\""+
				" WHERE " +
				"student.name = \"" + oldStudent.getName() + "\" and " +
				"student.cpf = \"" + oldStudent.getCpf() + "\" and " +
				"student.fone = \"" + oldStudent.getFone() + "\" and " +
				"student.email = \"" + oldStudent.getEmail() + "\" and " +
				"student.registration = \"" + oldStudent.getRegistration() + "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
			logger.trace( "The student has been updated." );
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
			this.updateQuery( "DELETE FROM student WHERE " +
				"student.name = \"" + student.getName() + "\" and " +
				"student.cpf = \"" + student.getCpf() + "\" and " +
				"student.fone = \"" + student.getFone() + "\" and " +
				"student.email = \"" + student.getEmail() + "\" and " +
				"student.registration = \"" + student.getRegistration() + "\";"
				);
			logger.trace( "The student has been deleted." );
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
		Vector<Student> student = this.search( "SELECT * FROM student;" ); 
		logger.trace( "All the students have been got" );
		return student;
	}
	
	/**
     * Captures the students by their name.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByName( String nameValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM student WHERE name = " + "\"" + nameValue + "\";" );
		logger.trace( "The selected student has been got" );
		return student;
	}
	
	/**
     * Captures the students by their cpf.
     * @return Vector - Students
	 * @throws ClienteException 
     */
	public Vector<Student> searchByCpf( String cpfValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM student WHERE cpf = " + "\"" + cpfValue + "\";" );
		logger.trace( "The selected student has been got" );
		return student;
	}
	
	/**
     * Captures the students by their registration
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByRegistration( String registrationValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM student WHERE registration = " + "\"" + registrationValue + "\";" );
		logger.trace( "The selected student has been got" );
		return student;
	}
	
	/**
     * Captures the students by their e-mail.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByEmail( String emailValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM student WHERE email = " + "\"" + emailValue + "\";" );
		logger.trace( "The selected student has been got" );
		return student;
	}
	
	/**
     * Captures the students by their phoneNumber.
     * @return Vector - Students
	 * @throws ClientException 
     */
	public Vector<Student> searchByPhoneNumber( String phoneNumberValue ) throws SQLException, ClientException, ClientException {
		Vector<Student> student = this.search( "SELECT * FROM student WHERE fone = " + "\"" + phoneNumberValue + "\";" );
		logger.trace( "The selected student has been got" );
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
			vet.add( this.fetchstudent( rs ) );
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
		boolean select = this.inDBGeneric( "SELECT * FROM student WHERE " +
				"student.name = \"" + student.getName() + "\" and " +
				"student.cpf = \"" + student.getCpf() + "\" and " +
				"student.fone = \"" + student.getFone() + "\" and " +
				"student.email = \"" + student.getEmail() + "\" and " +
				"student.registration = \"" + student.getRegistration() + "\";" );
		logger.trace( "Got if student exists in the database" );
		return select;
	}
	
	/**
     * Verifies if the student exists in database by his cpf
     * @return Boolean - Existence of a student 
     */
	private boolean inDBCpf( String CPFCode ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM student WHERE " +
				"student.cpf = \"" + CPFCode + "\";" );
		logger.trace( "Got if student exists in the database" );
		return select;
	}
	
	
	/**
     * Verifies if the student exists in database by his registration
     * @return Boolean - Existence of a student 
     */
	private boolean inDBregistration( String registrationCode ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM student WHERE " +
				"student.registration = \"" + registrationCode + "\";" );
		
		logger.trace( "Got if student exists in the database" );
		return select;
	}
	
	/**
     * Verifies if the student exists in database
     * @return Boolean - Existence of an student 
     */
	private boolean inOtherDB( Student student ) throws SQLException, ClientException {
		boolean select = this.inDBGeneric(
				"SELECT * FROM reservation_room_student WHERE " +
				"id_student = (SELECT id_student FROM student WHERE " +
				"student.name = \"" + student.getName() + "\" and " +
				"student.cpf = \"" + student.getCpf() + "\" and " +
				"student.fone = \"" + student.getFone() + "\" and " +
				"student.email = \"" + student.getEmail() + "\" and " +
				"student.registration = \"" + student.getRegistration() + "\");" );
		logger.trace( "Got if student exists in the database" );
		return select;
	}
	
	/**
     * Captures the next student resulted of the query made before 
     * @return student - Student  
	 * @throws ClientException 
     */
	private Student fetchstudent( ResultSet resultSet ) throws ClientException, SQLException, ClientException {
		Student student = new Student( resultSet.getString( "name" ), resultSet.getString( "cpf" ), resultSet.getString( "registration" ),
				resultSet.getString( "fone" ), resultSet.getString( "email" ) );
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
