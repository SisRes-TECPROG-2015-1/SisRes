package persistence;

import model.Sala;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import exception.PatrimonioException;

public class SalaDAO {

	// Mensagens
	private static final String SALA_JA_EXISTENTE = "Sala ja cadastrada.";
	private static final String SALA_NAO_EXISTENTE = "Sala nao cadastrada.";
	private static final String SALA_EM_USO = "Sala esta sendo utilizada em uma reserva.";
	private static final String SALA_NULA = "Sala esta nula.";
	private static final String CODIGO_JA_EXISTENTE = "Sala com o mesmo codigo ja cadastrada.";

	// Singleton
	private static SalaDAO instance;

	private SalaDAO() {
	}

	/**
     * Instantiates an SalaDAO if there is no instance of it.
     * @return SalaDAO - Classroom 
     */
	public static SalaDAO getInstance() {
		if ( instance == null ) {
			instance = new SalaDAO();
		}
		return instance;
	}

	
	
	//Metodo para incluir sala
	public void incluir( Sala sala ) throws SQLException, PatrimonioException {
		if ( sala == null ) {
			throw new PatrimonioException( SALA_NULA );
		} else if ( this.inDBCodigo( sala.getCodigo() ) ) {
			throw new PatrimonioException( CODIGO_JA_EXISTENTE );
		}
		this.updateQuery( "INSERT INTO "
				+ "sala (codigo, descricao, capacidade) VALUES (" + "\""
				+ sala.getCodigo() + "\", " + "\"" + sala.getDescricao()
				+ "\", " + sala.getCapacidade() + ");" );
	}

	//Metodo para alterar sala
	public void alterar( Sala old_sala, Sala new_sala ) throws SQLException,
			PatrimonioException {
		if ( new_sala == null ) {
			throw new PatrimonioException( SALA_NULA );
		}
		if ( old_sala == null ) {
			throw new PatrimonioException( SALA_NULA );
		}

		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;

		if ( !this.inDB( old_sala ) ) {
			throw new PatrimonioException( SALA_NAO_EXISTENTE );
		} else if ( this.inOtherDB( old_sala ) ) {
			throw new PatrimonioException( SALA_EM_USO );
		} else if ( !old_sala.getCodigo().equals( new_sala.getCodigo() )
				&& this.inDBCodigo( new_sala.getCodigo() ) ) {
			throw new PatrimonioException( CODIGO_JA_EXISTENTE );
		}
		if ( !this.inDB( new_sala ) ) {
			String msg = "UPDATE sala SET " + "codigo = \""
					+ new_sala.getCodigo() + "\", " + "descricao = \""
					+ new_sala.getDescricao() + "\", " + "capacidade = "
					+ new_sala.getCapacidade() + " WHERE " + "sala.codigo = \""
					+ old_sala.getCodigo() + "\" and " + "sala.descricao = \""
					+ old_sala.getDescricao() + "\" and "
					+ "sala.capacidade = " + old_sala.getCapacidade() + ";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new PatrimonioException( SALA_JA_EXISTENTE );
		}

		pst.close();
		con.close();
	}

	//Metodo para excluir sala
	public void excluir( Sala sala ) throws SQLException, PatrimonioException {
		if ( sala == null ) {
			throw new PatrimonioException( SALA_NULA );
		} else if ( this.inOtherDB( sala ) ) {
			throw new PatrimonioException( SALA_EM_USO );
		} else if ( this.inDB( sala ) ) {
			this.updateQuery( "DELETE FROM sala WHERE " + "sala.codigo = \""
					+ sala.getCodigo() + "\" and " + "sala.descricao = \""
					+ sala.getDescricao() + "\" and " + "sala.capacidade = "
					+ sala.getCapacidade() + ";" );
		} else {
			throw new PatrimonioException( SALA_NAO_EXISTENTE );
		}
	}

	
	/**
     * Captures all the classrooms
     * @return Vector - All the classrooms
     */
	public Vector<Sala> buscarTodos() throws SQLException, PatrimonioException {
		return this.buscar( "SELECT * FROM sala;" );
	}

	
	/**
     * Captures all the classrooms by its identification code
     * @return Vector - Classrooms
     */
	public Vector<Sala> buscarPorCodigo( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE codigo = " + "\"" + valor
				+ "\";" );
	}

	
	/**
     * Captures all the classrooms with the given description 
     * @return Vector - Classrooms
     */
	public Vector<Sala> buscarPorDescricao( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE descricao = " + "\""
				+ valor + "\";" );
	}

	/**
     * Captures all the classroomns with the given capacity value 
     * @return Vector - Classrooms
     */
	public Vector<Sala> buscarPorCapacidade( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE capacidade = " + valor
				+ ";" );
	}

	
	//Metodos Privados
	
	/**
     * Searches for classrooms by a given query
     * @return Vector - Classrooms
     */
	private Vector<Sala> buscar( String query ) throws SQLException,
			PatrimonioException {
		Vector<Sala> vet = new Vector<Sala>();

		Connection con = FactoryConnection.getInstance().getConnection();

		PreparedStatement pst = con.prepareStatement( query );
		ResultSet rs = pst.executeQuery();

		while ( rs.next() ) {
			vet.add( this.fetchSala( rs ) );
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
     * Verifies if the classroom exists in database
     * @return Boolean - Existence of a classroom 
     */
	private boolean inDB( Sala sala ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
				+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
				+ "sala.capacidade = " + sala.getCapacidade() + ";" );
	}

	
	/**
     * Verifies if the code exists in database
     * @return Boolean - Existence of a code
     */
	private boolean inDBCodigo( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + codigo + "\";" );
	}

	/**
     * Verifies if the classroom exists in database
     * @return Boolean - Existence of an classroom 
     */
	private boolean inOtherDB( Sala sala ) throws SQLException {
		if ( this.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "id_sala = ( SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
				+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
				+ "sala.capacidade = " + sala.getCapacidade() + " );" ) == false ) {
			if ( this.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
					+ "id_sala = ( SELECT id_sala FROM sala WHERE "
					+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
					+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
					+ "sala.capacidade = " + sala.getCapacidade() + " );" ) == false ) {
				return false;
			}
		}

		return true;
	}
	
	//Metodo para buscar Sala
	private Sala fetchSala( ResultSet rs ) throws PatrimonioException,
			SQLException {
		return new Sala( rs.getString( "codigo" ), rs.getString( "descricao" ),
				rs.getString( "capacidade" ) );
	}
	
	//Metodo para atualizar query no banco
	private void updateQuery( String msg ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
