package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Equipment;
import model.Student;
import exception.PatrimonyException;

public class EquipmentDAO {

    private static final String existentEquipment = "Equipamento ja cadastrado.";
    private static final String noExistentEquipment = "Equipamento nao cadastrado.";
    private static final String nullEquipment = "Equipamento esta nulo.";
    private static final String inUseEquipment = "Equipamento esta sendo utilizado em uma reserva.";
    private static final String existentCode = "Equipamento com o mesmo codigo ja cadastrado.";

    // Singleton
    private static EquipmentDAO instance;

    private EquipmentDAO() {
    }

    /**
     * Instantiates an EquipmentDAO if there is no instance of it.
     * @return EquipamentoDAO - Equipment 
     */
    public static EquipmentDAO getInstance() {
        if ( instance == null ) {
            instance = new EquipmentDAO();
        }
        return instance;
    }

    /**
     * Method includes a new equipment into the database
     * @param equipament: 
     * @throws SQLException
     * @throws PatrimonyException
     */
    public void includeEquipment( Equipment equipment ) throws SQLException, PatrimonyException {
        if ( equipment == null ) {
            throw new PatrimonyException( nullEquipment );
        } else if ( this.inDBCodigo( equipment.getCode() ) ) {
            throw new PatrimonyException( existentCode );
        }
        else if ( !this.inDB( equipment ) ) {
            this.updateQuery( "INSERT INTO " + "equipamento ( codigo, descricao ) VALUES ( " + "\"" + equipment.getCode() + "\", "
                    + "\"" + equipment.getDescription() + "\" );" );
        }
    }
    
    /**
     * This method modifies an equipment into the database
     * @param old_equipamento: refers to the equipment will be modified
     * @param new_equipamento: refers to the equipment will substitute the actual
     * @throws SQLException
     * @throws PatrimonyException
     */
    /**
     * @param old_equipment
     * @param new_equipment
     * @throws SQLException
     * @throws PatrimonyException
     */
    public void modifyEquipment( Equipment old_equipment, Equipment new_equipment ) throws SQLException, PatrimonyException {
        if ( old_equipment == null || new_equipment == null) {
            throw new PatrimonyException( nullEquipment );
        }

        Connection con = FactoryConnection.getInstance().getConnection();
        PreparedStatement pst;

        if ( !this.inDB( old_equipment ) ) {
            throw new PatrimonyException( noExistentEquipment );
        } else if ( this.inOtherDB( old_equipment ) ) {
            throw new PatrimonyException( inUseEquipment );
        } else if ( !new_equipment.getCode().equals( old_equipment.getCode() ) && this.inDBCodigo( new_equipment.getCode() ) ) {
            throw new PatrimonyException( existentCode );
        } else if ( !this.inDB( new_equipment ) ) {
            String msg = "UPDATE equipamento SET " + "codigo = \"" + new_equipment.getCode() + "\", " + "descricao = \""
                    + new_equipment.getDescription() + "\"" + " WHERE " + "equipamento.codigo = \"" + old_equipment.getCode()
                    + "\" and " + "equipamento.descricao = \"" + old_equipment.getDescription() + "\";";

            con.setAutoCommit( false );
            pst = con.prepareStatement( msg );
            pst.executeUpdate();
            con.commit();

            pst.close();

        } else {
            throw new PatrimonyException( existentEquipment );
        }
        con.close();
    }

    /**
     * Method delete an equipment into the database.
     * @param equipamento: equipment to be deleted
     * @throws SQLException
     * @throws PatrimonyException
     */
    public void excludeEquipment( Equipment equipment ) throws SQLException, PatrimonyException {
        if ( equipment == null ) {
            throw new PatrimonyException( nullEquipment );
        } else if ( this.inOtherDB( equipment ) ) {
            throw new PatrimonyException( inUseEquipment );
        }
        if ( this.inDB( equipment ) ) {
            this.updateQuery( "DELETE FROM equipamento WHERE " + "equipamento.codigo = \"" + equipment.getCode() + "\" and "
                    + "equipamento.descricao = \"" + equipment.getDescription() + "\";" );
        } else {
            throw new PatrimonyException( noExistentEquipment );
        }
    }

    /**
     * Captures all the equipments
     * @return Vector - All the equipments
     */
    public Vector<Equipment> searchForAll() throws SQLException, PatrimonyException {
    	Vector<Equipment> equipment = this.search( "SELECT * FROM equipamento;" );
        return equipment;
    }

    /**
     * Captures all the equipments by its identification code
     * @return Vector - Equipments
     */
    public Vector<Equipment> searchByCode( String value ) throws SQLException, PatrimonyException {
    	Vector<Equipment> equipment = this.search( "SELECT * FROM equipamento WHERE codigo = " + "\"" + value + "\";" );
        return equipment;
    }

    /**
     * Captures all the equipments with the given description 
     * @return Vector - Equipments
     */
    public Vector<Equipment> searchByDescription( String value ) throws SQLException, PatrimonyException {
    	Vector<Equipment> equipment = this.search( "SELECT * FROM equipamento WHERE descricao = " + "\"" + value + "\";" );
        return equipment;
    }

    
    //Metodos Privados
    
    
    /**
     * Searches for an equipment by a given query
     * @return Vector - Equipments
     */
    private Vector<Equipment> search( String query ) throws SQLException, PatrimonyException {
        Vector<Equipment> vet = new Vector<Equipment>();

        Connection con = FactoryConnection.getInstance().getConnection();

        PreparedStatement pst = con.prepareStatement( query );
        ResultSet rs = pst.executeQuery();

        while ( rs.next() ) {
            vet.add( this.fetchEquipamento( rs ) );
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
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        if ( rs.next() ) {
            rs.close();
            pst.close();
            con.close();
            return true;
        } else {
            rs.close();
            pst.close();
            con.close();
            return false;
        }
    }

    /**
     * Verifies if the equipment exists in database
     * @return Boolean - Existence of an equipment 
     */
    private boolean inDB( Equipment e ) throws SQLException, PatrimonyException {
        return this.inDBGeneric( "SELECT * FROM equipamento WHERE " + "equipamento.codigo = \"" + e.getCode() + "\" and "
                + "equipamento.descricao = \"" + e.getDescription() + "\";" );
    }

    /**
     * Verifies if the code exists in database
     * @return Boolean - Existence of a code
     */
    private boolean inDBCodigo( String codigo ) throws SQLException {
        return this.inDBGeneric( "SELECT * FROM equipamento WHERE " + "codigo = \"" + codigo + "\";" );
    }

    /**
     * Verifies if the equipment exists in database
     * @return Boolean - Existence of an equipment 
     */
    private boolean inOtherDB( Equipment e ) throws SQLException {
        return this.inDBGeneric( "SELECT * FROM reserva_equipamento WHERE "
                + "id_equipamento = (SELECT id_equipamento FROM equipamento WHERE " + "equipamento.codigo = \"" + e.getCode()
                + "\" and " + "equipamento.descricao = \"" + e.getDescription() + "\");" );
    }

    /**
     * Captures the next equipment resulted of the query made before 
     * @return Equipamento -  Equipment
     */
    private Equipment fetchEquipamento( ResultSet rs ) throws PatrimonyException, SQLException {
        return new Equipment( rs.getString( "codigo" ), rs.getString( "descricao" ) );
    }
    
    /**
     * This method updates a query into the db
     * @param msg
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
