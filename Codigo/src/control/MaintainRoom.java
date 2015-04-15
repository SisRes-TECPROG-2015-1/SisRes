package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ClassRoom;
import exception.PatrimonyException;
import model.Room;

public class MaintainRoom {

	private Vector < Room > room = new Vector < Room >();
	
	//Singleton
	private static MaintainRoom instance;
	
	private MaintainRoom() {
	}
	
	
	/**
	 * Creates an instance of a classroom if it isn't already instantiated.
	 * @return ManterSala - A classroom
	 */	
	public static MaintainRoom getInstance() {
		if ( instance == null )
			instance = new MaintainRoom();
		return instance;
	}
	
	
	/**
	 * Captures all the classrooms in the database.
	 * @return Vector - All classrooms
	 */	
	public Vector < Room > getRooms() throws SQLException, PatrimonyException {
		this.room = ClassRoom.getInstance().searchAll();
		return this.room;
	}

	
	/**
	 * Inserts a new classroom and its attributes.
	 */
	public void insertRooms( String code, String description, String capacity ) throws PatrimonyException, SQLException {
		Room room = new Room( code, description, capacity );
		ClassRoom.getInstance().includeARoom( room );
		this.room.add( room );
	}


	/**
	 * Changes a classroom attributes.
	 */
	public void changeRoom( String code, String description, String capacity, Room room) throws PatrimonyException, SQLException {
		Room old_room = new Room( room.getCode(), room.getDescription(),
								room.getCapacity());
		room.setCode( code );
		room.setDescription( description );
		room.setCapacity( capacity );
		ClassRoom.getInstance().alterar( old_room, room );
	}

	
	/**
	 * Excludes a classroom from the database.
	 */
	public void excludeRoom( Room room ) throws SQLException, PatrimonyException {
		ClassRoom.getInstance().excludeTeacher( room );
		this.room.remove( room );
	}

}
