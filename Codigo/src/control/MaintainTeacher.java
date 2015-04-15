package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.TeacherDAO;
import exception.ClienteException;
import model.Teacher;

public class MaintainTeacher {
	
	private Vector < Teacher > professores_vet = new Vector < Teacher > ();
	
	//Singleton
	private static MaintainTeacher instance;
	
	
	private MaintainTeacher() {
	}
	
	/**
	 * Creates an instance of an teacher if it isn't already instantiated.
	 * @return Professor - A teacher
	 */
	public static MaintainTeacher getInstance() {
		if ( instance == null )
			instance = new MaintainTeacher();
		return instance;
	}
	
	
	/**
	 * Searches for a teacher by its name
	 * @return Professor - A teacher
	 */
	public Vector < Teacher > buscarNome ( String valor ) throws SQLException, ClienteException {
		return TeacherDAO.getInstance().searchByName( valor );
	}
	
	
	/**
	 * Searches for a teacher by its CPF
	 * @return Professor - A teacher
	 */
	public Vector < Teacher > buscarCpf( String valor ) throws SQLException, ClienteException {
		return TeacherDAO.getInstance().searchByCpf( valor );
	}
	
	
	/**
	 * Searches for a teacher by its enrollment
	 * @return Professor - A teacher
	 */
	public Vector < Teacher > buscarMatricula( String valor ) throws SQLException, ClienteException {
		return TeacherDAO.getInstance().searchByRegistration( valor );
	}
	
	
	/**
	 * Searches for a teacher by its e-mail
	 * @return Professor - A teacher
	 */
	public Vector < Teacher > buscarEmail( String valor ) throws SQLException, ClienteException {
		return TeacherDAO.getInstance().searchByEmail( valor );
	}
	
	
	/**
	 * Searches for a teacher by its telephone number
	 * @return Professor - A teacher
	 */
	public Vector < Teacher > buscarTelefone( String valor) throws SQLException, ClienteException {
		return TeacherDAO.getInstance().searchByPhoneNumber( valor );
	}	
	
	
	/**
	 * Captures all the teachers in database
	 * @return Vector - Teachers
	 */		
	public Vector < Teacher > getProfessores_vet() throws SQLException, ClienteException{
		this.professores_vet = TeacherDAO.getInstance().searchForAllTeachers();
		return this.professores_vet;
	}
	
	
	/**
	 * Inserts a new teacher in the database and its attributes
	 * @return void
	 */
	public void inserir( String nome, String cpf, String matricula,
			String telefone, String email) throws ClienteException, SQLException {
		Teacher prof = new Teacher(nome, cpf, matricula, telefone, email );
		TeacherDAO.getInstance().includeNewTeacher( prof );
		this.professores_vet.add( prof );
	}

	
	/**
	 * Changes a teacher attributes like name, CPF, enrollment and others
	 * @return void
	 */
	public void alterar( String nome, String cpf, String matricula,
			String telefone, String email, Teacher prof ) throws ClienteException, SQLException {
		Teacher prof_velho = new Teacher(
								prof.getNome(),
								prof.getCpf(),
								prof.getMatricula(),
								prof.getTelefone(),
								prof.getEmail());
		prof.setNome( nome );
		prof.setCpf( cpf );
		prof.setMatricula( matricula );
		prof.setTelefone( telefone );
		prof.setEmail( email );
		TeacherDAO.getInstance().modifyATeacher( prof_velho, prof );
	}

	
	/**
	 * Excludes a teacher from the database by its instance
	 * @return void
	 */
	public void excluir( Teacher professor ) throws SQLException, ClienteException {
		TeacherDAO.getInstance().excludeATeacher( professor );
		this.professores_vet.remove( professor );
	}

}
