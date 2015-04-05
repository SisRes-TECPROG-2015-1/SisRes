package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.AlunoDAO;
import exception.ClienteException;
import model.Aluno;

/**
 * Equipment and rooms reservation system
 * ManterAluno class with methods to accomplish CRUD students
 */
public class ManterAluno {
	
	/**
	 * Creating a vector of students 
	 */
	private Vector <Aluno> alunos_vet = new Vector <Aluno> ();
	
	//This attribute is an instance of a ManterAluno type
	private static ManterAluno instance;
	
	/**
	 * Empty constructor
	 */
	private ManterAluno () {
	}
	
		
	/**
	 * Creates an instance of a student if it isn't already instantiated.
	 * @return - Aluno - A student
	 */
	public static ManterAluno getInstance () {
		if ( instance == null )
		instance = new ManterAluno();
		return instance;
	}
	
	/**
	 * Searches for a student by its name
	 * Parameter valor of type string used to bring information from AlunoDao class
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarNome ( String valor ) throws SQLException, ClienteException { 
		return AlunoDAO.getInstance().buscarNome(valor);
	}
	
	/**
	 * Searches for a student by its CPF
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarCpf ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarCpf(valor);
	}
	
	
	/**
	 * Searches for a student by its enrollment
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarMatricula ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarMatricula(valor);
	}
	
	
	/**
	 * Searches for a student by its e-mail
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarEmail ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarEmail(valor);
	}
	
	/**
	 * Searches for a student by its telephone number
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarTelefone ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarTelefone(valor);
	}
	
	
	/**
	 * Captures all the students in database
	 * @return Vector - students
	 */	
	public Vector <Aluno> getAluno_vet() throws SQLException, ClienteException {
		this.alunos_vet = AlunoDAO.getInstance().buscarTodos();
		return this.alunos_vet;
	}
	
	
	/**
	 * Inserts a new student in the database and its attributes
	 * Parameters nome, cpf, matricula, telefone, email and aluno used to bring informations for create a student
	 * @return void
	 */
	public void inserir ( String nome, String cpf, String matricula,
						String telefone, String email ) 
						throws ClienteException, SQLException {
		Aluno aluno = new Aluno ( nome, cpf, matricula, telefone, email );
		AlunoDAO.getInstance().incluir(aluno);
		this.alunos_vet.add(aluno);
	}

	
	/**
	 * Changes a student attributes like name, CPF, enrollment and others
	 * Parameters nome, cpf, matricula, telefone, email and aluno used to bring informations from AlunoDAOfor update an old student
	 * @return void
	 */
	public void alterar ( String nome, String cpf, String matricula,
						String telefone, String email, Aluno aluno ) 
						throws ClienteException, SQLException {
		Aluno aluno_velho = new Aluno (
						aluno.getNome(),
						aluno.getCpf(),
						aluno.getMatricula(),
						aluno.getTelefone(),
						aluno.getEmail());
						aluno.setNome(nome);
						aluno.setCpf(cpf);
						aluno.setMatricula(matricula);
						aluno.setTelefone(telefone);
						aluno.setEmail(email);
						AlunoDAO.getInstance().alterar(aluno_velho, aluno);
	}
	
	
	/**
	 * Excludes a student from the database by its isntance
	 * @return void
	 */
	public void excluir ( Aluno aluno ) throws SQLException, ClienteException {
		AlunoDAO.getInstance().excluir(aluno);
		this.alunos_vet.remove(aluno);
	}

}
