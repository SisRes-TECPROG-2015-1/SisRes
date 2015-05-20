package control;

import java.sql.SQLException;
import java.util.Vector;


import persistence.TeacherDAO;
import exception.ClientException;
import model.Teacher;

//Importing Log4J2 classes 
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MaintainTeacher {
	
	private Vector < Teacher > teacher_vet = new Vector < Teacher > ();
	
	static final Logger logger = LogManager.getLogger( MaintainTeacher.class.getName() );
	
	//Singleton
	private static MaintainTeacher instance;
	
	
	private MaintainTeacher() {
	}
	
	/**
	 * Creates an instance of an teacher if it isn't already instantiated.
	 * @return Vector - A teacher
	 */
	public static MaintainTeacher getInstance() {
		if ( instance == null )
			logger.trace( "There is any teacher." );
			instance = new MaintainTeacher();
			logger.trace( "A new teacher is just instantiated" );
		return instance;
	}
	
	
	/**
	 * Searches for a teacher by its name
	 * @return Vector - A teacher
	 */
	public Vector < Teacher > getTeachersByName ( String teacherName ) throws SQLException, ClientException {
		return TeacherDAO.getInstance().searchByName( teacherName );
	}
	
	
	/**
	 * Searches for a teacher by its CPF
	 * @return Vector - A teacher
	 */
	public Vector < Teacher > getTeachersByCpf( String teacherCpf ) throws SQLException, ClientException {
		return TeacherDAO.getInstance().searchByCpf( teacherCpf );
	}
	
	
	/**
	 * Searches for a teacher by its registration
	 * @return Vector - A teacher
	 */
	public Vector < Teacher > getTeachersByRegistration( String teacherRegistration ) throws SQLException, ClientException {
		return TeacherDAO.getInstance().searchByRegistration( teacherRegistration );
	}
	
	
	/**
	 * Searches for a teacher by its e-mail
	 * @return Vector - A teacher
	 */
	public Vector < Teacher > getTeachersByEmail( String teacherEmail ) throws SQLException, ClientException {
		return TeacherDAO.getInstance().searchByEmail( teacherEmail );
	}
	
	
	/**
	 * Searches for a teacher by its telephone number
	 * @return Vector - A teacher
	 */
	public Vector < Teacher > getTeachersByTelephone( String teacherTelephone) throws SQLException, ClientException {
		return TeacherDAO.getInstance().searchByPhoneNumber( teacherTelephone );
	}	
	
	
	/**
	 * Captures all the teachers in database
	 * @return Vector - Teachers
	 */		
	public Vector < Teacher > getTeachers() throws SQLException, ClientException{
		this.teacher_vet = TeacherDAO.getInstance().searchForAllTeachers();
		return this.teacher_vet;
	}
	
	
	/**
	 * Inserts a new teacher in the database and its attributes
	 * @return void
	 */
	public void insertNewTeacher( String name, String cpf, String registration,
			String telephone, String email) throws ClientException, SQLException {
		Teacher prof = new Teacher(name, cpf, registration, telephone, email );
		TeacherDAO.getInstance().includeNewTeacher( prof );
		this.teacher_vet.add( prof );
	}

	
	/**
	 * Changes a teacher attributes like name, CPF, enrollment and others
	 * @return void
	 */
	public void changeTeacher( String name, String cpf, String registration,
			String telephone, String email, Teacher teacher ) throws ClientException, SQLException {
		Teacher old_teacher = new Teacher(
								teacher.getName(),
								teacher.getCpf(),
								teacher.getRegistration(),
								teacher.getFone(),
								teacher.getEmail());
		teacher.setNome( name );
		teacher.setCpf( cpf );
		teacher.setRegistration( registration );
		teacher.setTelefone( telephone );
		teacher.setEmail( email );
		TeacherDAO.getInstance().modifyATeacher( old_teacher, teacher );
	}

	
	/**
	 * Excludes a teacher from the database by its instance
	 * @return void
	 */
	public void excludeTeacher( Teacher teacher ) throws SQLException, ClientException {
		TeacherDAO.getInstance().excludeATeacher( teacher );
		this.teacher_vet.remove( teacher );
	}

}
