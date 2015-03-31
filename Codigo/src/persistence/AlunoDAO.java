package persistence;

import model.Aluno;

import java.sql.*;
import java.util.Vector;

import exception.ClienteException;

public class AlunoDAO {

	//Mensagens
		private static final String ALUNO_JA_EXISTENTE = "O Aluno ja esta cadastrado.";
		private static final String ALUNO_NULO = "O Aluno esta nulo.";
		private static final String ALUNO_NAO_EXISTENTE = "O Aluno nao esta cadastrado.";
		private static final String ALUNO_EM_USO = "Sala esta sendo utilizada em uma reserva.";
		private static final String CPF_JA_EXISTENTE = "Ja existe um aluno cadastrado com esse CPF.";
		private static final String MATRICULA_JA_EXISTENTE = "Ja existe um aluno cadastrado com essa matricula.";
	
	//Singleton
		private static AlunoDAO instance;
		private AlunoDAO() {
		}
		public static AlunoDAO getInstance() {
			if( instance == null ) {
				instance = new AlunoDAO();
			}
			return instance;
		}
	//
	
	//Metodo para incluir aluno	
	public void incluir( Aluno aluno ) throws SQLException, ClienteException {
		if ( aluno == null ) {
			throw new ClienteException(ALUNO_NULO);
		} else if ( this.inDBCpf( aluno.getCpf() ) ){
			throw new ClienteException( CPF_JA_EXISTENTE );
		} else if ( this.inDBMatricula( aluno.getMatricula() ) ){
				throw new ClienteException( MATRICULA_JA_EXISTENTE );
		} else if ( !this.inDB( aluno ) ) {
			this.updateQuery( "INSERT INTO " +
					"aluno ( nome, cpf, telefone, email, matricula ) VALUES (" +
					"\"" + aluno.getNome() + "\", " +
					"\"" + aluno.getCpf()+ "\", " +
					"\"" + aluno.getTelefone() + "\", " +
					"\"" + aluno.getEmail() + "\", " +
					"\"" + aluno.getMatricula() + "\"); "
					);
		} else {
			throw new ClienteException( ALUNO_JA_EXISTENTE );
		}
	}
	
	//Metodo para alterar aluno
	public void alterar( Aluno aluno_velho, Aluno aluno_novo ) throws SQLException, ClienteException {
		if( aluno_velho == null ) {
			throw new ClienteException( ALUNO_NULO );
		}
		if( aluno_novo == null ) {
			throw new ClienteException( ALUNO_NULO );
		}
		
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;
		
		if( !this.inDB( aluno_velho ) ) {
			throw new ClienteException( ALUNO_NAO_EXISTENTE );
		} else if ( this.inOtherDB( aluno_velho ) ) {
			throw new ClienteException( ALUNO_EM_USO );
		} else if ( !aluno_velho.getCpf().equals( aluno_novo.getCpf() ) && this.inDBCpf( aluno_novo.getCpf() ) ){
			throw new ClienteException( CPF_JA_EXISTENTE );
		} else if ( !aluno_velho.getMatricula().equals( aluno_novo.getMatricula() ) && this.inDBMatricula( aluno_novo.getMatricula() ) ) {
				throw new ClienteException( MATRICULA_JA_EXISTENTE );
		} else if( !this.inDB( aluno_novo ) ) {
			String msg = "UPDATE aluno SET " +
				"nome = \"" + aluno_novo.getNome() + "\", " +
				"cpf = \"" + aluno_novo.getCpf() + "\", " +
				"telefone = \"" + aluno_novo.getTelefone() + "\", " +
				"email = \"" + aluno_novo.getEmail() + "\", " +
				"matricula = \"" + aluno_novo.getMatricula() + "\""+
				" WHERE " +
				"aluno.nome = \"" + aluno_velho.getNome() + "\" and " +
				"aluno.cpf = \"" + aluno_velho.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno_velho.getTelefone() + "\" and " +
				"aluno.email = \"" + aluno_velho.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno_velho.getMatricula() + "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new ClienteException( ALUNO_JA_EXISTENTE );
		}

		pst.close();
		con.close();
	}
	
	//Metodo para excluir aluno
	public void excluir( Aluno aluno ) throws SQLException, ClienteException {
		if ( aluno == null ) {
			throw new ClienteException( ALUNO_NULO );
		}
		else if ( this.inOtherDB( aluno ) ) {
			throw new ClienteException( ALUNO_EM_USO );
		} else if ( this.inDB( aluno ) ) {
			this.updateQuery( "DELETE FROM aluno WHERE " +
				"aluno.nome = \"" + aluno.getNome() + "\" and " +
				"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno.getTelefone() + "\" and " +
				"aluno.email = \"" + aluno.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno.getMatricula() + "\";"
				);
		} else {
			throw new ClienteException( ALUNO_NAO_EXISTENTE );
		}
	}

	
	/**
     * Captures the students
     * @return Vector - All the students
     */
	public Vector<Aluno> buscarTodos() throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno;" );
	}
	
	/**
     * Captures the students by their name.
     * @return Vector - Students
     */
	public Vector<Aluno> buscarNome( String valor ) throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno WHERE nome = " + "\"" + valor + "\";" );
	}
	
	/**
     * Captures the students by their cpf.
     * @return Vector - Students
     */
	public Vector<Aluno> buscarCpf( String valor ) throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno WHERE cpf = " + "\"" + valor + "\";" );
	}
	
	/**
     * Captures the students by their matricula
     * @return Vector - Students
     */
	public Vector<Aluno> buscarMatricula( String valor ) throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno WHERE matricula = " + "\"" + valor + "\";" );
	}
	
	/**
     * Captures the students by their e-mail.
     * @return Vector - Students
     */
	public Vector<Aluno> buscarEmail( String valor ) throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno WHERE email = " + "\"" + valor + "\";" );
	}
	
	/**
     * Captures the students by their telephone.
     * @return Vector - Students
     */
	public Vector<Aluno> buscarTelefone( String valor ) throws SQLException, ClienteException {
		return this.buscar( "SELECT * FROM aluno WHERE telefone = " + "\"" + valor + "\";" );
	}
	
	
	//Metodos Privados
	/**
     * Searches for a student by a given query
     * @return Vector - Students
     */
	private Vector<Aluno> buscar( String query ) throws SQLException, ClienteException {
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
	private boolean inDB( Aluno aluno ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + aluno.getNome() + "\" and " +
				"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno.getTelefone() + "\" and " +
				"aluno.email = \"" + aluno.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno.getMatricula() + "\";" );
	}
	
	/**
     * Verifies if the student exists in database by his cpf
     * @return Boolean - Existence of a student 
     */
	private boolean inDBCpf( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.cpf = \"" + codigo + "\";" );
	}
	
	
	/**
     * Verifies if the student exists in database by his matricula
     * @return Boolean - Existence of a student 
     */
	private boolean inDBMatricula( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM aluno WHERE " +
				"aluno.matricula = \"" + codigo + "\";" );
	}
	
	/**
     * Verifies if the student exists in database
     * @return Boolean - Existence of an student 
     */
	private boolean inOtherDB( Aluno aluno ) throws SQLException, ClienteException {
		return this.inDBGeneric(
				"SELECT * FROM reserva_sala_aluno WHERE " +
				"id_aluno = (SELECT id_aluno FROM aluno WHERE " +
				"aluno.nome = \"" + aluno.getNome() + "\" and " +
				"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno.getTelefone() + "\" and " +
				"aluno.email = \"" + aluno.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno.getMatricula() + "\");" );
	}
	
	/**
     * Captures the next student resulted of the query made before 
     * @return Aluno - Student  
     */
	private Aluno fetchAluno( ResultSet rs ) throws ClienteException, SQLException {
		return new Aluno( rs.getString( "nome" ), rs.getString( "cpf" ), rs.getString( "matricula" ),
				rs.getString( "telefone" ), rs.getString( "email" ) );
	}
	
	//Metodo para atualizar a query
	private void updateQuery( String msg ) throws SQLException {
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
