package persistence;

import model.Teacher;

import java.sql.*;
import java.util.Vector;

import exception.ClientException;

// Importing Log4J2 classes

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class TeacherDAO {

	// Mensagens
	private static final String existentTeacher = "O teacher ja esta cadastrado.";
	private static final String notExistentTeacher = "O teacher nao esta cadastrado.";
	private static final String nullTeacher = "O teacher esta nulo.";
	private static final String classroomInUseByTeacher = "Sala esta sendo utilizada em uma reserva.";
	private static final String existentCPF = "Ja existe um teacher cadastrado com esse CPF.";
	private static final String existentRegistration = "Ja existe um teacher cadastrado com essa registration.";

	static final Logger logger = LogManager.getLogger( TeacherDAO.class.getName() );
	
	// Singleton
	private static TeacherDAO instance;

	private TeacherDAO() {
	}

	/**
	 * Create an instance if its null
	 * @return: instance of TeacherDAO
	 */
	public static TeacherDAO getInstance() {
		if ( instance == null ) {
			instance = new TeacherDAO();
		}else{
        	//do nothing
        }
		
		return instance;
	}

	//
	
	/**
	 * Method to insert a teacher into the database
	 * @param teacher
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void includeNewTeacher( Teacher teacher ) throws SQLException, ClientException {
		if ( teacher == null ) {
			throw new ClientException( nullTeacher );
		} else if ( this.inDBCpf( teacher.getCpf() ) ) {
			throw new ClientException( existentCPF );
		} else if ( this.inDBregistration( teacher.getRegistration() ) ) {
			throw new ClientException( existentRegistration );
		}else{
        	//do nothing
        }
		
		logger.trace( "Saving new teacher." );
		this.updateQuery( "INSERT INTO "
				+ "teacher (name, cpf, fone, email, registration) VALUES ("
				+ "\"" + teacher.getName() + "\", " + "\"" + teacher.getCpf()
				+ "\", " + "\"" + teacher.getFone() + "\", " + "\""
				+ teacher.getEmail() + "\", " + "\"" + teacher.getRegistration()
				+ "\"); " );
		logger.trace( "User has been saved." );
	}

	/**
	 * Method to modify a teacher into the database
	 * @param oldTeacher
	 * @param newTeacher
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void modifyATeacher( Teacher oldTeacher, Teacher newTeacher )
			throws SQLException, ClientException {
		if ( oldTeacher == null || newTeacher == null) {
			throw new ClientException( nullTeacher );
		}
		
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;

		if ( !this.inDB( oldTeacher ) ) {
			throw new ClientException( notExistentTeacher );
		}else{
        	//do nothing
        }
		
		if ( this.inOtherDB( oldTeacher ) ) {
			throw new ClientException( classroomInUseByTeacher );
		}else if ( !oldTeacher.getCpf().equals( newTeacher.getCpf() ) 
				&& this.inDBCpf( newTeacher.getCpf() ) ) {
			throw new ClientException( existentCPF );
		} else if ( !oldTeacher.getRegistration().equals( newTeacher.getRegistration() )
				&& this.inDBregistration( newTeacher.getRegistration() ) ) {
			throw new ClientException( existentRegistration );
		} else if ( !this.inDB( newTeacher ) ) {
			String msg = "UPDATE teacher SET " + "name = \""
					+ newTeacher.getName() + "\", " + "cpf = \""
					+ newTeacher.getCpf() + "\", " + "fone = \""
					+ newTeacher.getFone() + "\", " + "email = \""
					+ newTeacher.getEmail() + "\", " + "registration = \""
					+ newTeacher.getRegistration() + "\"" + " WHERE "
					+ "teacher.name = \"" + oldTeacher.getName() + "\" and "
					+ "teacher.cpf = \"" + oldTeacher.getCpf() + "\" and "
					+ "teacher.fone = \"" + oldTeacher.getFone()
					+ "\" and " + "teacher.email = \""
					+ oldTeacher.getEmail() + "\" and "
					+ "teacher.registration = \"" + oldTeacher.getRegistration()
					+ "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new ClientException( existentTeacher );
		}

		pst.close();
		con.close();
	}

	/**
	 * Method to exclude a teacher into the database
	 * @param teacher
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void excludeATeacher( Teacher teacher ) throws SQLException, ClientException {
		
		if ( teacher == null ) {
			throw new ClientException(nullTeacher);
		}else{
        	//do nothing
        }
		
		if ( this.inOtherDB( teacher ) ) {
			throw new ClientException( classroomInUseByTeacher );
		} else if ( this.inDB( teacher ) ) {
			
			logger.trace( "Deleting an existent teacher" );
			this.updateQuery( "DELETE FROM teacher WHERE "
					+ "teacher.name = \"" + teacher.getName() + "\" and "
					+ "teacher.cpf = \"" + teacher.getCpf() + "\" and "
					+ "teacher.fone = \"" + teacher.getFone()
					+ "\" and " + "teacher.email = \"" + teacher.getEmail()
					+ "\" and " + "teacher.registration = \""
					+ teacher.getRegistration() + "\";" );
			
			logger.trace( "Teacher deleted." );
		} else {
			throw new ClientException( notExistentTeacher );
		}
	}

	/**
     * Captures the teachers
     * @return Vector - All the teachers
	 * @throws ClientException 
     */
	public Vector<Teacher> searchForAllTeachers() throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher;" );
        return teacher;
	}

	/**
     * Captures the teachers by their name.
     * @return Vector - Teachers
	 * @throws ClientException 
     */
	public Vector<Teacher> searchByName( String nameValue ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher WHERE name = " + "\""
				+ nameValue + "\";" );
		return teacher;
	}

	
	/**
     * Captures the teachers by their cpf.
     * @return Vector - Teachers
	 * @throws ClientException 
     */
	public Vector<Teacher> searchByCpf( String CPFValue ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher WHERE cpf = " + "\""
				+ CPFValue + "\";" );
		return teacher;
	}

	/**
     * Captures the teachers by their registration
     * @return Vector - Teachers
	 * @throws ClientException 
     */	
	public Vector<Teacher> searchByRegistration( String registrationValue ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher WHERE registration = " + "\""
				+ registrationValue + "\";" );
		return teacher;
	}

	/**
     * Captures the teachers by their e-mail.
     * @return Vector - Teachers
	 * @throws ClientException 
     */
	public Vector<Teacher> searchByEmail( String emailValue ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher WHERE email = " + "\""
				+ emailValue + "\";" );
		return teacher;
	}

	/**
     * Captures the teachers by their telephone.
     * @return Vector - Teachers
	 * @throws ClientException 
     */
	public Vector<Teacher> searchByPhoneNumber( String phoneNumberValue ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacher = this.searchByQuery( "SELECT * FROM teacher WHERE fone = " + "\""
				+ phoneNumberValue + "\";" );
		return teacher;
	}

	
	//Metodos Privados
	 
	/**
     * Searches for a teacher by a given query
     * @return Vector - Teachers
	 * @throws ClientException 
     */
	private Vector<Teacher> searchByQuery( String query ) throws SQLException,
			ClientException, ClientException {
		Vector<Teacher> teacherArrayList = new Vector<Teacher>();

		Connection connection = FactoryConnection.getInstance().getConnection();

		PreparedStatement preparedStatementObject = connection.prepareStatement( query );
		ResultSet resultSet = preparedStatementObject.executeQuery();

		while ( resultSet.next() ) {
			teacherArrayList.add( this.fetchTeacher( resultSet ) );
		}

		preparedStatementObject.close();
		resultSet.close();
		connection.close();
		return teacherArrayList;
	}

	/**
     * Verifies if there is a query
     * @return Boolean - Existence of a query
     */
	private boolean inDBGeneric( String query ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( query );
		ResultSet rs = pst.executeQuery();

		if ( rs.next() ) {
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
     * Verifies if the given teacher exists in database
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDB( Teacher prof ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM teacher WHERE "
				+ "teacher.name = \"" + prof.getName() + "\" and "
				+ "teacher.cpf = \"" + prof.getCpf() + "\" and "
				+ "teacher.fone = \"" + prof.getFone() + "\" and "
				+ "teacher.email = \"" + prof.getEmail() + "\" and "
				+ "teacher.registration = \"" + prof.getRegistration() + "\";" ); 
		return select;
	}

	/**
     * Verifies if the teacher exists in database by his cpf
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDBCpf( String codigo ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM teacher WHERE " + "cpf = \""
				+ codigo + "\";" ); 
		return select;
	}

	/**
     * Verifies if the teacher exists in database by his registration
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDBregistration( String codigo ) throws SQLException {
		boolean select = this.inDBGeneric( "SELECT * FROM teacher WHERE "
				+ "registration = \"" + codigo + "\";" );
		return select;
	}

	/**
     * Verifies if the teacher exists in database
     * @return Boolean - Existence of an teacher 
     */
	private boolean inOtherDB( Teacher prof ) throws SQLException {
		if ( this.inDBGeneric( "SELECT * FROM reservation_room_teacher WHERE "
				+ "id_teacher = (SELECT id_teacher FROM teacher WHERE "
				+ "teacher.name = \"" + prof.getName() + "\" and "
				+ "teacher.cpf = \"" + prof.getCpf() + "\" and "
				+ "teacher.fone = \"" + prof.getFone() + "\" and "
				+ "teacher.email = \"" + prof.getEmail() + "\" and "
				+ "teacher.registration = \"" + prof.getRegistration() + "\");" ) == false ) {
			if ( this.inDBGeneric( "SELECT * FROM reservation_equipment WHERE "
					+ "id_teacher = (SELECT id_teacher FROM teacher WHERE "
					+ "teacher.name = \"" + prof.getName() + "\" and "
					+ "teacher.cpf = \"" + prof.getCpf() + "\" and "
					+ "teacher.fone = \"" + prof.getFone()
					+ "\" and " + "teacher.email = \"" + prof.getEmail()
					+ "\" and " + "teacher.registration = \""
					+ prof.getRegistration() + "\");" ) == false ) {
				return false;
			}else{
            	//do nothing
            }
		}else{
        	//do nothing
        }

		return true;
	}

	//Metodo pra buscar teacher
	private Teacher fetchTeacher( ResultSet rs ) throws ClientException,
			SQLException, ClientException {
		return new Teacher( rs.getString( "name" ), rs.getString( "cpf" ),
				rs.getString( "registration" ), rs.getString( "fone" ),
				rs.getString( "email" ) );
	}

	//Metodo pra atualizar a query no banco
	private void updateQuery( String msg ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
