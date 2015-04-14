package persistence;

import model.Professor;

import java.sql.*;
import java.util.Vector;

import exception.ClienteException;

public class TeacherDAO {

	// Mensagens
	private static final String existentTeacher = "O Professor ja esta cadastrado.";
	private static final String notExistentTeacher = "O Professor nao esta cadastrado.";
	private static final String nullTeacher = "O Professor esta nulo.";
	private static final String classroomInUseByTeacher = "Sala esta sendo utilizada em uma reserva.";
	private static final String existentCPF = "Ja existe um professor cadastrado com esse CPF.";
	private static final String existentRegistration = "Ja existe um professor cadastrado com essa matricula.";

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
	public void includeNewTeacher( Professor teacher ) throws SQLException, ClienteException {
		if ( teacher == null ) {
			throw new ClienteException( nullTeacher );
		} else if ( this.inDBCpf( teacher.getCpf() ) ) {
			throw new ClienteException( existentCPF );
		} else if ( this.inDBMatricula( teacher.getMatricula() ) ) {
			throw new ClienteException( existentRegistration );
		}
		this.updateQuery( "INSERT INTO "
				+ "professor (nome, cpf, telefone, email, matricula) VALUES ("
				+ "\"" + teacher.getNome() + "\", " + "\"" + teacher.getCpf()
				+ "\", " + "\"" + teacher.getTelefone() + "\", " + "\""
				+ teacher.getEmail() + "\", " + "\"" + teacher.getMatricula()
				+ "\"); " );
	}

	/**
	 * Method to modify a teacher into the database
	 * @param oldTeacher
	 * @param newTeacher
	 * @throws SQLException
	 * @throws ClienteException
	 */
	public void modifyATeacher( Professor oldTeacher, Professor newTeacher )
			throws SQLException, ClienteException {
		if ( oldTeacher == null ) {
			throw new ClienteException( nullTeacher );
		}
		if (newTeacher == null) {
			throw new ClienteException( nullTeacher );
		}

		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;

		if ( !this.inDB( oldTeacher ) ) {
			throw new ClienteException( notExistentTeacher );
		}
		if ( this.inOtherDB( oldTeacher ) ) {
			throw new ClienteException( classroomInUseByTeacher );
		}
		else if ( !oldTeacher.getCpf().equals( newTeacher.getCpf() ) 
				&& this.inDBCpf( newTeacher.getCpf() ) ) {
			throw new ClienteException( existentCPF );
		} else if ( !oldTeacher.getMatricula().equals( newTeacher.getMatricula() )
				&& this.inDBMatricula( newTeacher.getMatricula() ) ) {
			throw new ClienteException( existentRegistration );
		} else if ( !this.inDB( newTeacher ) ) {
			String msg = "UPDATE professor SET " + "nome = \""
					+ newTeacher.getNome() + "\", " + "cpf = \""
					+ newTeacher.getCpf() + "\", " + "telefone = \""
					+ newTeacher.getTelefone() + "\", " + "email = \""
					+ newTeacher.getEmail() + "\", " + "matricula = \""
					+ newTeacher.getMatricula() + "\"" + " WHERE "
					+ "professor.nome = \"" + oldTeacher.getNome() + "\" and "
					+ "professor.cpf = \"" + oldTeacher.getCpf() + "\" and "
					+ "professor.telefone = \"" + oldTeacher.getTelefone()
					+ "\" and " + "professor.email = \""
					+ oldTeacher.getEmail() + "\" and "
					+ "professor.matricula = \"" + oldTeacher.getMatricula()
					+ "\";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new ClienteException( existentTeacher );
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
	public void excludeATeacher( Professor teacher ) throws SQLException, ClienteException {
		if ( teacher == null ) {
			throw new ClienteException(nullTeacher);
		}
		if ( this.inOtherDB( teacher ) ) {
			throw new ClienteException( classroomInUseByTeacher );
		} else if ( this.inDB( teacher ) ) {
			this.updateQuery( "DELETE FROM professor WHERE "
					+ "professor.nome = \"" + teacher.getNome() + "\" and "
					+ "professor.cpf = \"" + teacher.getCpf() + "\" and "
					+ "professor.telefone = \"" + teacher.getTelefone()
					+ "\" and " + "professor.email = \"" + teacher.getEmail()
					+ "\" and " + "professor.matricula = \""
					+ teacher.getMatricula() + "\";" );
		} else {
			throw new ClienteException( notExistentTeacher );
		}
	}

	/**
     * Captures the teachers
     * @return Vector - All the teachers
     */
	public Vector<Professor> searchForAllTeachers() throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor;" );
	}

	/**
     * Captures the teachers by their name.
     * @return Vector - Teachers
     */
	public Vector<Professor> searchByName( String nameValue ) throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor WHERE nome = " + "\""
				+ nameValue + "\";" );
	}

	
	/**
     * Captures the teachers by their cpf.
     * @return Vector - Teachers
     */
	public Vector<Professor> searchByCpf( String CPFValue ) throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor WHERE cpf = " + "\""
				+ CPFValue + "\";" );
	}

	/**
     * Captures the teachers by their matricula
     * @return Vector - Teachers
     */	
	public Vector<Professor> searchByRegistration( String registrationValue ) throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor WHERE matricula = " + "\""
				+ registrationValue + "\";" );
	}

	/**
     * Captures the teachers by their e-mail.
     * @return Vector - Teachers
     */
	public Vector<Professor> searchByEmail( String emailValue ) throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor WHERE email = " + "\""
				+ emailValue + "\";" );
	}

	/**
     * Captures the teachers by their telephone.
     * @return Vector - Teachers
     */
	public Vector<Professor> searchByPhoneNumber( String phoneNumberValue ) throws SQLException,
			ClienteException {
		return this.searchByQuery( "SELECT * FROM professor WHERE telefone = " + "\""
				+ phoneNumberValue + "\";" );
	}

	
	//Metodos Privados
	 
	/**
     * Searches for a teacher by a given query
     * @return Vector - Teachers
     */
	private Vector<Professor> searchByQuery( String query ) throws SQLException,
			ClienteException {
		Vector<Professor> teacherArrayList = new Vector<Professor>();

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

		if ( !rs.next() ) {
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
     * Verifies if the given teacher exists in database
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDB( Professor prof ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "professor.nome = \"" + prof.getNome() + "\" and "
				+ "professor.cpf = \"" + prof.getCpf() + "\" and "
				+ "professor.telefone = \"" + prof.getTelefone() + "\" and "
				+ "professor.email = \"" + prof.getEmail() + "\" and "
				+ "professor.matricula = \"" + prof.getMatricula() + "\";" );
	}

	/**
     * Verifies if the teacher exists in database by his cpf
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDBCpf( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM professor WHERE " + "cpf = \""
				+ codigo + "\";" );
	}

	/**
     * Verifies if the teacher exists in database by his matricula
     * @return Boolean - Existence of a teacher 
     */
	private boolean inDBMatricula( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM professor WHERE "
				+ "matricula = \"" + codigo + "\";" );
	}

	/**
     * Verifies if the teacher exists in database
     * @return Boolean - Existence of an teacher 
     */
	private boolean inOtherDB( Professor prof ) throws SQLException {
		if ( this.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "id_professor = (SELECT id_professor FROM professor WHERE "
				+ "professor.nome = \"" + prof.getNome() + "\" and "
				+ "professor.cpf = \"" + prof.getCpf() + "\" and "
				+ "professor.telefone = \"" + prof.getTelefone() + "\" and "
				+ "professor.email = \"" + prof.getEmail() + "\" and "
				+ "professor.matricula = \"" + prof.getMatricula() + "\");" ) == false ) {
			if ( this.inDBGeneric( "SELECT * FROM reserva_equipamento WHERE "
					+ "id_professor = (SELECT id_professor FROM professor WHERE "
					+ "professor.nome = \"" + prof.getNome() + "\" and "
					+ "professor.cpf = \"" + prof.getCpf() + "\" and "
					+ "professor.telefone = \"" + prof.getTelefone()
					+ "\" and " + "professor.email = \"" + prof.getEmail()
					+ "\" and " + "professor.matricula = \""
					+ prof.getMatricula() + "\");" ) == false ) {
				return false;
			}
		}

		return true;
	}

	//Metodo pra buscar professor
	private Professor fetchTeacher( ResultSet rs ) throws ClienteException,
			SQLException {
		return new Professor( rs.getString( "nome" ), rs.getString( "cpf" ),
				rs.getString( "matricula" ), rs.getString( "telefone" ),
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
