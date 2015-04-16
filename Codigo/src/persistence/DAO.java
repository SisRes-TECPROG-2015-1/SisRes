package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public abstract class DAO {
	//Esta classe nao sera testada diretamente.
		
	/**
	 * The vector obtained in this method have to be converted into the
	 * vector of the type will be used.
	 * @throws ClientException 
	 * */

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected Vector search(String query) throws SQLException, ClientException, 
													PatrimonyException, ReserveException, ClientException {
		Vector vet = new Vector();
		
		Connection con =  FactoryConnection.getInstance().getConnection();
		
		PreparedStatement pst = con.prepareStatement( query );
		ResultSet rs = pst.executeQuery();
		
		while ( rs.next() ) {
			vet.add( this.fetch( rs ) );
		}
		
		pst.close();
		rs.close();
		con.close();
		return vet;
	}
	
	
	//Metodo privado para checar se o registro esta no banco.
	/**
	 * This method checks if there is a register into the database
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	protected boolean inDBGeneric( String query ) throws SQLException {
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
	 * Function used into search method, implemented into others DAO class
	 * @throws ClientException 
	 * */
	protected abstract Object fetch( ResultSet rs ) throws SQLException, ClientException,
														PatrimonyException, ReserveException, ClientException;
	
	
	/**
	 * Este metodo eh utilizado para Incluir e Excluir algum registro do
	 * banco, dependendo da query.
	 * */
	protected void executeQuery( String msg ) throws SQLException {
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();		
		pst.close();
		con.close();
	}
	
	
	/**
	 * This method is used to update a query into the database
	 * @param msg
	 * @throws SQLException
	 */
	protected void updateQuery( String msg ) throws SQLException {
		Connection con =  FactoryConnection.getInstance().getConnection();
		con.setAutoCommit( false );
		PreparedStatement pst = con.prepareStatement( msg );
		pst.executeUpdate();
		con.commit();
		pst.close();
		con.close();
	}
}
