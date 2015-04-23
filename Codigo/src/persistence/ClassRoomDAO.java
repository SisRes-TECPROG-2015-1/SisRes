package persistence;

import model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import exception.PatrimonyException;

public class ClassRoomDAO {

	
	private static final String classRoomAlreadyExisted = "Sala ja cadastrada."; // Attribute indicates if the room already exists
	private static final String classRoomDoesntExist = "Sala nao cadastrada."; // Attribute indicates if the room doesnt exists
	private static final String classRoomInUse = "Sala esta sendo utilizada em uma reserva."; // Indicates if the room is in use
	private static final String NullClassRoom = "Sala esta nula."; // Indicates the room is null 
	private static final String CodeAlreadyExist = "Sala com o mesmo codigo ja cadastrada."; // Indicates a room with the same code is already registered
	private static ClassRoomDAO instance;

	/**
	 * Empty Constructor
	 */
	private ClassRoomDAO() {
	}

	/**
     * Instantiates a SalaDAO if there is no instance of it.
     * @return SalaDAO - Classroom 
     */
	public static ClassRoomDAO getInstance() {
		if ( instance == null ) {
			instance = new ClassRoomDAO();
		}
		return instance;
	}

	
	
	/**
	 * This method includes a room
	 * @param room: 
	 * @throws SQLException
	 * @throws PatrimonyException
	 */
	public void includeARoom( Room classroom ) throws SQLException, PatrimonyException {
		if ( classroom == null ) {
			throw new PatrimonyException( NullClassRoom );
		} else if ( this.inDBCodigo( classroom.getCode() ) ) {
			throw new PatrimonyException( CodeAlreadyExist );
		}
		this.updateQuery( "INSERT INTO "
				+ "sala (codigo, descricao, capacidade) VALUES (" + "\""
				+ classroom.getCode() + "\", " + "\"" + classroom.getDescription()
				+ "\", " + classroom.getCapacity() + ");" );
	}

	/**
	 * This method modifies a room into the DB
	 * @param oldRoom: Room to be modified
	 * @param newRoom: Room will substitute the oldest
	 * @throws SQLException
	 * @throws PatrimonyException
	 */
	public void alterar( Room oldRoom, Room newRoom ) throws SQLException,
			PatrimonyException {
		if ( newRoom == null ) {
			throw new PatrimonyException( NullClassRoom );
		}
		if ( oldRoom == null ) {
			throw new PatrimonyException( NullClassRoom );
		}

		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst;

		if ( !this.inDB( oldRoom ) ) {
			throw new PatrimonyException( classRoomDoesntExist );
		} else if ( this.inOtherDB( oldRoom ) ) {
			throw new PatrimonyException( classRoomInUse );
		} else if ( !oldRoom.getCode().equals( newRoom.getCode() )
				&& this.inDBCodigo( newRoom.getCode() ) ) {
			throw new PatrimonyException( CodeAlreadyExist );
		}
		if ( !this.inDB( newRoom ) ) {
			String msg = "UPDATE sala SET " + "codigo = \""
					+ newRoom.getCode() + "\", " + "descricao = \""
					+ newRoom.getDescription() + "\", " + "capacidade = "
					+ newRoom.getCapacity() + " WHERE " + "sala.codigo = \""
					+ oldRoom.getCode() + "\" and " + "sala.descricao = \""
					+ oldRoom.getDescription() + "\" and "
					+ "sala.capacidade = " + oldRoom.getCapacity() + ";";
			con.setAutoCommit( false );
			pst = con.prepareStatement( msg );
			pst.executeUpdate();
			con.commit();
		} else {
			throw new PatrimonyException( classRoomAlreadyExisted );
		}

		pst.close();
		con.close();
	}

	/**
	 * This method excludes a room into the database
	 * @param room: Room wich will be excluded
	 * @throws SQLException
	 * @throws PatrimonyException
	 */
	public void excludeRoom( Room room ) throws SQLException, PatrimonyException {
		if ( room == null ) {
			throw new PatrimonyException( NullClassRoom );
		} else if ( this.inOtherDB( room ) ) {
			throw new PatrimonyException( classRoomInUse );
		} else if ( this.inDB( room ) ) {
			this.updateQuery( "DELETE FROM sala WHERE " + "sala.codigo = \""
					+ room.getCode() + "\" and " + "sala.descricao = \""
					+ room.getDescription() + "\" and " + "sala.capacidade = "
					+ room.getCapacity() + ";" );
		} else {
			throw new PatrimonyException( classRoomDoesntExist );
		}
	}

	
	/**
     * Captures all the classrooms
     * @return Vector - All the classrooms
     */
	public Vector<Room> searchAll() throws SQLException, PatrimonyException {
		return this.searchByQuery( "SELECT * FROM sala;" );
	}

	
	/**
     * Captures all the classrooms by its identification code
     * @return Vector - Classrooms
     */
	public Vector<Room> searchByCode( String roomCode ) throws SQLException,
			PatrimonyException {
		return this.searchByQuery( "SELECT * FROM sala WHERE codigo = " + "\"" + roomCode
				+ "\";" );
	}

	
	/**
     * Captures all the classrooms with the given description 
     * @return Vector - Classrooms
     */
	public Vector<Room> searchByDescription( String roomDescription ) throws SQLException,
			PatrimonyException {
		return this.searchByQuery( "SELECT * FROM sala WHERE descricao = " + "\""
				+ roomDescription + "\";" );
	}

	/**
     * Captures all the classroomns with the given capacity value 
     * @return Vector - Classrooms
     */
	public Vector<Room> searchByCapacity( String capacityValue ) throws SQLException,
			PatrimonyException {
		return this.searchByQuery( "SELECT * FROM sala WHERE capacidade = " + capacityValue
				+ ";" );
	}
	
	
	/**
     * Searches for classrooms by a given query
     * @return Vector - Classrooms
     */
	private Vector<Room> searchByQuery( String query ) throws SQLException,
			PatrimonyException {
		Vector<Room> vet = new Vector<Room>();

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
	private boolean inDB( Room classRoom ) throws SQLException {
		return this.inDBGeneric( "SELECT * FROM sala WHERE "
				+ "sala.codigo = \"" + classRoom.getCode() + "\" and "
				+ "sala.descricao = \"" + classRoom.getDescription() + "\" and "
				+ "sala.capacidade = " + classRoom.getCapacity() + ";" );
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
	private boolean inOtherDB( Room classRoom ) throws SQLException {
		if ( this.inDBGeneric( "SELECT * FROM reserva_sala_professor WHERE "
				+ "id_sala = ( SELECT id_sala FROM sala WHERE "
				+ "sala.codigo = \"" + classRoom.getCode() + "\" and "
				+ "sala.descricao = \"" + classRoom.getDescription() + "\" and "
				+ "sala.capacidade = " + classRoom.getCapacity() + " );" ) == false ) {
			if ( this.inDBGeneric( "SELECT * FROM reserva_sala_aluno WHERE "
					+ "id_sala = ( SELECT id_sala FROM sala WHERE "
					+ "sala.codigo = \"" + classRoom.getCode() + "\" and "
					+ "sala.descricao = \"" + classRoom.getDescription() + "\" and "
					+ "sala.capacidade = " + classRoom.getCapacity() + " );" ) == false ) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * This method puts the result of the search of a room into an object
	 * @param resultSetObject: object get the database information
	 * @return: Sala object
	 * @throws PatrimonyException
	 * @throws SQLException
	 */
	private Room fetchSala( ResultSet resultSetObject ) throws PatrimonyException,
			SQLException {
		return new Room( resultSetObject.getString( "codigo" ), resultSetObject.getString( "descricao" ),
				resultSetObject.getString( "capacidade" ) );
	}
	
	/**
	 * This method updates a query
	 * @param message
	 * @throws SQLException
	 */
	private void updateQuery( String message ) throws SQLException {
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( message );
		pst.executeUpdate();
		pst.close();
		con.close();
	}

}
