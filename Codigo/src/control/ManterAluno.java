package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.StudentDAO;
import exception.ClienteException;
import model.Aluno;

public class ManterAluno {
	
	private Vector <Aluno> alunos_vet = new Vector <Aluno> ();
	
	//Singlenton
	private static ManterAluno instance;
	
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
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarNome ( String valor ) throws SQLException, ClienteException {
		return StudentDAO.getInstance().searchByName(valor);
	}
	
	/**
	 * Searches for a student by its CPF
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarCpf ( String valor ) throws SQLException, ClienteException {
		return StudentDAO.getInstance().searchByCpf(valor);
	}
	
	
	/**
	 * Searches for a student by its enrollment
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarMatricula ( String valor ) throws SQLException, ClienteException {
		return StudentDAO.getInstance().searchByRegistration(valor);
	}
	
	
	/**
	 * Searches for a student by its e-mail
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarEmail ( String valor ) throws SQLException, ClienteException {
		return StudentDAO.getInstance().searchByEmail(valor);
	}
	
	/**
	 * Searches for a student by its telephone number
	 * @return Aluno - A student
	 */
	public Vector <Aluno> buscarTelefone ( String valor ) throws SQLException, ClienteException {
		return StudentDAO.getInstance().searchByPhoneNumber(valor);
	}
	
	
	/**
	 * Captures all the students in database
	 * @return Vector - students
	 */	
	public Vector <Aluno> getAluno_vet() throws SQLException, ClienteException {
		this.alunos_vet = StudentDAO.getInstance().captureStudents();
		return this.alunos_vet;
	}
	
	
	/**
	 * Inserts a new student in the database and its attributes
	 * @return void
	 */
	public void inserir ( String nome, String cpf, String matricula,
						String telefone, String email ) 
						throws ClienteException, SQLException {
		Aluno aluno = new Aluno ( nome, cpf, matricula, telefone, email );
		StudentDAO.getInstance().includeNewStudent(aluno);
		this.alunos_vet.add(aluno);
	}

	
	/**
	 * Changes a student attributes like name, CPF, enrollment and others
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
						StudentDAO.getInstance().modifyStudent(aluno_velho, aluno);
	}
	
	
	/**
	 * Excludes a student from the database by its isntance
	 * @return void
	 */
	public void excluir ( Aluno aluno ) throws SQLException, ClienteException {
		StudentDAO.getInstance().deleteStudent(aluno);
		this.alunos_vet.remove(aluno);
	}

}
