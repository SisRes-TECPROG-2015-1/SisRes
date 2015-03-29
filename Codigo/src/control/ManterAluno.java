package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.AlunoDAO;
import exception.ClienteException;
import model.Aluno;

public class ManterAluno {
	
	private Vector <Aluno> alunos_vet = new Vector <Aluno> ();
	
	//Singlenton
	private static ManterAluno instance;
	
	private ManterAluno () {
	}
	
		
	/**
	 * Creates an instance of a Studant if it isn't allready instanciated.
	 * @return - Aluno - A Studant
	 */
	public static ManterAluno getInstance () {
		if ( instance == null )
		instance = new ManterAluno ();
		return instance;
	}
	
	/**
	 * Searches for a Studant by its name
	 * @return Aluno - A Studant
	 */
	public Vector <Aluno> buscarNome ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarNome(valor);
	}
	
	/**
	 * Searches for a Studant by its CPF
	 * @return Aluno - A Studant
	 */
	public Vector <Aluno> buscarCpf ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarCpf(valor);
	}
	
	
	/**
	 * Searches for a Studant by its enrollment
	 * @return Aluno - A Studant
	 */
	public Vector <Aluno> buscarMatricula ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarMatricula(valor);
	}
	
	
	/**
	 * Searches for a Studant by its e-mail
	 * @return Aluno - A Studant
	 */
	public Vector <Aluno> buscarEmail ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarEmail(valor);
	}
	
	/**
	 * Searches for a Studant by its telphone number
	 * @return Aluno - A Studant
	 */
	public Vector <Aluno> buscarTelefone ( String valor ) throws SQLException, ClienteException {
		return AlunoDAO.getInstance().buscarTelefone(valor);
	}
	
	
	/**
	 * Captures all the Studants in database
	 * @return Vector - Studants
	 */	
	public Vector <Aluno> getAluno_vet() throws SQLException, ClienteException {
		this.alunos_vet = AlunoDAO.getInstance().buscarTodos();
		return this.alunos_vet;
	}
	
	
	/**
	 * Inserts a new Studant in the database and its atributes
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
	 * Alterates a Studant atributes like name, CPF, enrollment and others
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
	 * Excludes a Studant from the database by its intance
	 * @return void
	 */
	public void excluir ( Aluno aluno ) throws SQLException, ClienteException {
		AlunoDAO.getInstance().excluir(aluno);
		this.alunos_vet.remove(aluno);
	}

}
