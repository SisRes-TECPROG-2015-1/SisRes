package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.ClassRoom;
import exception.PatrimonyException;
import model.Sala;

public class MaintainRoom {

	private Vector < Sala > salas_vet = new Vector < Sala >();
	
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
	public Vector < Sala > getSalas_vet() throws SQLException, PatrimonyException {
		this.salas_vet = ClassRoom.getInstance().searchAll();
		return this.salas_vet;
	}

	
	/**
	 * Inserts a new classroom and its attributes.
	 */
	public void inserir( String codigo, String descricao, String capacidade ) throws PatrimonyException, SQLException {
		Sala sala = new Sala( codigo, descricao, capacidade );
		ClassRoom.getInstance().includeARoom( sala );
		this.salas_vet.add( sala );
	}


	/**
	 * Changes a classroom attributes.
	 */
	public void alterar( String codigo, String descricao, String capacidade, Sala sala) throws PatrimonyException, SQLException {
		Sala old_sala = new Sala( sala.getCodigo(), sala.getDescricao(),
								sala.getCapacidade());
		sala.setCodigo( codigo );
		sala.setDescricao( descricao );
		sala.setCapacidade( capacidade );
		ClassRoom.getInstance().alterar( old_sala, sala );
	}

	
	/**
	 * Excludes a classroom from the database.
	 */
	public void excluir( Sala sala ) throws SQLException, PatrimonyException {
		ClassRoom.getInstance().excludeRoom( sala );
		this.salas_vet.remove( sala );
	}

}
