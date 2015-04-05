/**
* Equipment and Rooms Reservation System
* This file contains the methods to maintain a room
*/

package control;

import java.sql.SQLException;
import java.util.Vector;

import persistence.SalaDAO;
import exception.PatrimonioException;
import model.Sala;

public class ManterSala {

	private Vector < Sala > salas_vet = new Vector < Sala >();
	
	//Singleton
	private static ManterSala instance;
	
	private ManterSala() {
	}
	
	
	/**
	 * Creates an instance of a classroom if it isn't already instantiated.
	 * @return ManterSala - A classroom
	 */	
	public static ManterSala getInstance() {
		if ( instance == null )
			instance = new ManterSala();
		return instance;
	}
	
	
	/**
	 * Captures all the classrooms in the database.
	 * @return Vector - All classrooms
	 */	
	public Vector < Sala > getSalas_vet() throws SQLException, PatrimonioException {
		this.salas_vet = SalaDAO.getInstance().buscarTodos();
		return this.salas_vet;
	}

	
	/**
	 * Inserts a new classroom and its attributes.
	 */
	public void inserir( String codigo, String descricao, String capacidade ) throws PatrimonioException, SQLException {
		Sala sala = new Sala( codigo, descricao, capacidade );
		SalaDAO.getInstance().incluir( sala );
		this.salas_vet.add( sala );
	}


	/**
	 * Changes a classroom attributes.
	 */
	public void alterar( String codigo, String descricao, String capacidade, Sala sala) throws PatrimonioException, SQLException {
		Sala old_sala = new Sala( sala.getCodigo(), sala.getDescricao(),
								sala.getCapacidade());
		sala.setCodigo( codigo );
		sala.setDescricao( descricao );
		sala.setCapacidade( capacidade );
		SalaDAO.getInstance().alterar( old_sala, sala );
	}

	
	/**
	 * Excludes a classroom from the database.
	 */
	public void excluir( Sala sala ) throws SQLException, PatrimonioException {
		SalaDAO.getInstance().excluir( sala );
		this.salas_vet.remove( sala );
	}

}
