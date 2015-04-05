package persistence;

import model.Sala;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import exception.PatrimonioException;

public class SalaDAO {


	private static final String SALA_JA_EXISTENTE = "Sala ja cadastrada."; // Attribute indicates room already exists
	private static final String SALA_NAO_EXISTENTE = "Sala nao cadastrada."; // Attribute indicates room doesnt exists
	private static final String SALA_EM_USO = "Sala esta sendo utilizada em uma reserva."; //Attribute indicates if the room is beeing used
	private static final String SALA_NULA = "Sala esta nula."; // Attribute indicates if the room is null
	private static final String CODIGO_JA_EXISTENTE = "Sala com o mesmo codigo ja cadastrada."; //Attribute indicates the room code already exists 

	// This attribute is an instance of a SalaDAO type 
	private static SalaDAO instance;

	// Empty constructor
	private SalaDAO() {
	}

	// instance getter
	public static SalaDAO getInstance() {
		if ( instance == null ) {
			instance = new SalaDAO();
		}
		return instance;
	}
	
     /**
	 *  This methods includes a room into the database
	 */
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

	/**
	 * 	This method modifies a room information in database
	 * @param old_sala: indicates the room which will be modified
	 * @param new_sala: indicates the new room will replace the oldest
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
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

	/**
	 * 	This method excludes a room into the database
	 * @param sala: indicates the room wich will be excluded
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
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
	 *  This method returns all the rooms into an arraylist
	 * @return: arraylist of Rooms
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
	public Vector<Sala> buscarTodos() throws SQLException, PatrimonioException {
		return this.buscar( "SELECT * FROM sala;" );
	}

	
	/**
	 *  This method makes a search by code and returns a list of the founded results
	 * @param valor: indicates the room code to be searched
	 * @return: arraylist of rooms
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
	public Vector<Sala> buscarPorCodigo( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE codigo = " + "\"" + valor
				+ "\";" );
	}

	/**
	 *  This method makes a search by description into the database and returns 
	 *  a list of founded results
	 * @param valor
	 * @return: arraylist of rooms
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
	public Vector<Sala> buscarPorDescricao( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE descricao = " + "\""
				+ valor + "\";" );
	}

	/**
	 * This method makes a search by capacity into the database and returns 
	 * a list of founded results
	 * @param valor
	 * @return: arraylist of rooms
	 * @throws SQLException
	 * @throws PatrimonioException
	 */
	public Vector<Sala> buscarPorCapacidade( String valor ) throws SQLException,
			PatrimonioException {
		return this.buscar( "SELECT * FROM sala WHERE capacidade = " + valor
				+ ";" );
	}

	
	/**
	 * This method makes a search into the database
	 * @param query 
	 * @return: arraylist of rooms
	 * @throws SQLException
	 * @throws PatrimonioException
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
	 * This method closes a connection to the database
	 * @param query: indicates a query to be transported into the statement
	 * @return: boolean if the connection has closed or not.
	 * @throws SQLException
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
	 * This method is a procedure to search for rooms with the parameters code, description and
	 * capacity
	 * @param sala: indicates a room to be found into the database
	 * @return: boolean that indicates if the search completed.
	 * @throws SQLException
	 */
	private boolean inDB( Sala sala ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + sala.getCodigo() + "\" and "
				+ "sala.descricao = \"" + sala.getDescricao() + "\" and "
				+ "sala.capacidade = " + sala.getCapacidade() + ";" );
	}

	/**
	 * This method is a procedure to search for rooms with the parameter code 
	 * @param codigo: indicates the code of the room
	 * @return: boolean indicating the complete status of the search
	 * @throws SQLException
	 */
	private boolean inDBCodigo( String codigo ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + codigo + "\";" );
	}

	/**
	 * This method is a procedure to search for rooms with the room id, room code,
	 * room description and capacity
	 * @param sala: indicates the room
	 * @return: boolean indicating the complete status of the search
	 * @throws SQLException
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
	
	/**
	 * This method creates a new room with the parameters founded by the search
	 * @param rs: indicates a resultSet
	 * @return: A room object
	 * @throws PatrimonioException
	 * @throws SQLException
	 */
	private Sala fetchSala( ResultSet rs ) throws PatrimonioException,
			SQLException {
		return new Sala( rs.getString( "codigo" ), rs.getString( "descricao" ),
				rs.getString( "capacidade" ) );
	}
	
	/**
	 *  This method updates a query
	 * @param msg: represents the query message
	 * @throws SQLException
	 */
	private void updateQuery( String msg ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
