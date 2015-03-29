package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ProfessorDAO;
import exception.ClienteException;
import model.Professor;

public class ManterProfessor {
	
	private Vector < Professor > professores_vet = new Vector < Professor > ();
	
	//Singleton
	private static ManterProfessor instance;
	
	
	private ManterProfessor() {
	}
	
	/**
	 * Creates an instance of an teacher if it isn't already instantiated.
	 * @return Professor - A teacher
	 */
	public static ManterProfessor getInstance() {
		if ( instance == null )
			instance = new ManterProfessor();
		return instance;
	}
	
	
	/**
	 * Searches for a teacher by its name
	 * @return Professor - A teacher
	 */
	public Vector < Professor > buscarNome ( String valor ) throws SQLException, ClienteException {
		return ProfessorDAO.getInstance().buscarNome( valor );
	}
	
	
	/**
	 * Searches for a teacher by its CPF
	 * @return Professor - A teacher
	 */
	public Vector < Professor > buscarCpf( String valor ) throws SQLException, ClienteException {
		return ProfessorDAO.getInstance().buscarCpf( valor );
	}
	
	
	/**
	 * Searches for a teacher by its enrollment
	 * @return Professor - A teacher
	 */
	public Vector < Professor > buscarMatricula( String valor ) throws SQLException, ClienteException {
		return ProfessorDAO.getInstance().buscarMatricula( valor );
	}
	
	
	/**
	 * Searches for a teacher by its e-mail
	 * @return Professor - A teacher
	 */
	public Vector < Professor > buscarEmail( String valor ) throws SQLException, ClienteException {
		return ProfessorDAO.getInstance().buscarEmail( valor );
	}
	
	
	/**
	 * Searches for a teacher by its telephone number
	 * @return Professor - A teacher
	 */
	public Vector < Professor > buscarTelefone( String valor) throws SQLException, ClienteException {
		return ProfessorDAO.getInstance().buscarTelefone( valor );
	}	
	
	
	/**
	 * Captures all the teachers in database
	 * @return Vector - Teachers
	 */		
	public Vector < Professor > getProfessores_vet() throws SQLException, ClienteException{
		this.professores_vet = ProfessorDAO.getInstance().buscarTodos();
		return this.professores_vet;
	}
	
	
	/**
	 * Inserts a new teacher in the database and its attributes
	 * @return void
	 */
	public void inserir( String nome, String cpf, String matricula,
			String telefone, String email) throws ClienteException, SQLException {
		Professor prof = new Professor(nome, cpf, matricula, telefone, email );
		ProfessorDAO.getInstance().incluir( prof );
		this.professores_vet.add( prof );
	}

	
	/**
	 * Changes a teacher attributes like name, CPF, enrollment and others
	 * @return void
	 */
	public void alterar( String nome, String cpf, String matricula,
			String telefone, String email, Professor prof ) throws ClienteException, SQLException {
		Professor prof_velho = new Professor(
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
		ProfessorDAO.getInstance().alterar( prof_velho, prof );
	}

	
	/**
	 * Excludes a teacher from the database by its instance
	 * @return void
	 */
	public void excluir( Professor professor ) throws SQLException, ClienteException {
		ProfessorDAO.getInstance().excluir( professor );
		this.professores_vet.remove( professor );
	}

}
