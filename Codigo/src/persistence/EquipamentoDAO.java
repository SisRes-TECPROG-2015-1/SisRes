package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Equipamento;
import exception.PatrimonyException;

public class EquipamentoDAO {

    // Mensagens
    private static final String EQUIPAMENTO_JA_EXISTENTE = "Equipamento ja cadastrado.";
    private static final String EQUIPAMENTO_NAO_EXISTENTE = "Equipamento nao cadastrado.";
    private static final String EQUIPAMENTO_NULO = "Equipamento esta nulo.";
    private static final String EQUIPAMENTO_EM_USO = "Equipamento esta sendo utilizado em uma reserva.";
    private static final String CODIGO_JA_EXISTENTE = "Equipamento com o mesmo codigo ja cadastrado.";

    // Singleton
    private static EquipamentoDAO instance;

    private EquipamentoDAO() {
    }

    /**
     * Instantiates an EquipmentDAO if there is no instance of it.
     * @return EquipamentoDAO - Equipment 
     */
    public static EquipamentoDAO getInstance() {
        if ( instance == null ) {
            instance = new EquipamentoDAO();
        }
        return instance;
    }

    //Método para incluir equipamento
    public void incluir( Equipamento equipamento ) throws SQLException, PatrimonyException {
        if ( equipamento == null ) {
            throw new PatrimonyException( EQUIPAMENTO_NULO );
        } else if ( this.inDBCodigo( equipamento.getCodigo() ) ) {
            throw new PatrimonyException( CODIGO_JA_EXISTENTE );
        }
        else if ( !this.inDB( equipamento ) ) {
            this.updateQuery( "INSERT INTO " + "equipamento ( codigo, descricao ) VALUES ( " + "\"" + equipamento.getCodigo() + "\", "
                    + "\"" + equipamento.getDescricao() + "\" );" );
        }
    }
    
    //Método para alterar equipamento
    public void alterar( Equipamento old_equipamento, Equipamento new_equipamento ) throws SQLException, PatrimonyException {
        if ( old_equipamento == null ) {
            throw new PatrimonyException( EQUIPAMENTO_NULO );
        }
        if ( new_equipamento == null ) {
            throw new PatrimonyException( EQUIPAMENTO_NULO );
        }

        Connection con = FactoryConnection.getInstance().getConnection();
        PreparedStatement pst;

        if ( !this.inDB( old_equipamento ) ) {
            throw new PatrimonyException( EQUIPAMENTO_NAO_EXISTENTE );
        } else if ( this.inOtherDB( old_equipamento ) ) {
            throw new PatrimonyException( EQUIPAMENTO_EM_USO );
        } else if ( !new_equipamento.getCodigo().equals( old_equipamento.getCodigo() ) && this.inDBCodigo( new_equipamento.getCodigo() ) ) {
            throw new PatrimonyException( CODIGO_JA_EXISTENTE );
        } else if ( !this.inDB( new_equipamento ) ) {
            String msg = "UPDATE equipamento SET " + "codigo = \"" + new_equipamento.getCodigo() + "\", " + "descricao = \""
                    + new_equipamento.getDescricao() + "\"" + " WHERE " + "equipamento.codigo = \"" + old_equipamento.getCodigo()
                    + "\" and " + "equipamento.descricao = \"" + old_equipamento.getDescricao() + "\";";

            con.setAutoCommit( false );
            pst = con.prepareStatement( msg );
            pst.executeUpdate();
            con.commit();

            pst.close();

        } else {
            throw new PatrimonyException( EQUIPAMENTO_JA_EXISTENTE );
        }
        con.close();
    }

    //Metodo para excluir equipamento
    public void excluir( Equipamento equipamento ) throws SQLException, PatrimonyException {
        if ( equipamento == null ) {
            throw new PatrimonyException( EQUIPAMENTO_NULO );
        } else if ( this.inOtherDB( equipamento ) ) {
            throw new PatrimonyException( EQUIPAMENTO_EM_USO );
        }
        if ( this.inDB( equipamento ) ) {
            this.updateQuery( "DELETE FROM equipamento WHERE " + "equipamento.codigo = \"" + equipamento.getCodigo() + "\" and "
                    + "equipamento.descricao = \"" + equipamento.getDescricao() + "\";" );
        } else {
            throw new PatrimonyException( EQUIPAMENTO_NAO_EXISTENTE );
        }
    }

    /**
     * Captures all the equipments
     * @return Vector - All the equipments
     */
    public Vector<Equipamento> buscarTodos() throws SQLException, PatrimonyException {
        return this.buscar( "SELECT * FROM equipamento;" );
    }

    /**
     * Captures all the equipments by its identification code
     * @return Vector - Equipments
     */
    public Vector<Equipamento> buscarPorCodigo( String valor ) throws SQLException, PatrimonyException {
        return this.buscar( "SELECT * FROM equipamento WHERE codigo = " + "\"" + valor + "\";" );
    }

    /**
     * Captures all the equipments with the given description 
     * @return Vector - Equipments
     */
    public Vector<Equipamento> buscarPorDescricao( String valor ) throws SQLException, PatrimonyException {
        return this.buscar( "SELECT * FROM equipamento WHERE descricao = " + "\"" + valor + "\";" );
    }

    
    //Metodos Privados
    
    
    /**
     * Searches for an equipment by a given query
     * @return Vector - Equipments
     */
    private Vector<Equipamento> buscar( String query ) throws SQLException, PatrimonyException {
        Vector<Equipamento> vet = new Vector<Equipamento>();

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
     * Verifies if the equipment exists in database
     * @return Boolean - Existence of an equipment 
     */
    private boolean inDB( Equipamento e ) throws SQLException, PatrimonyException {
        return this.inDBGeneric( "SELECT * FROM equipamento WHERE " + "equipamento.codigo = \"" + e.getCodigo() + "\" and "
                + "equipamento.descricao = \"" + e.getDescricao() + "\";" );
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
    private boolean inOtherDB( Equipamento e ) throws SQLException {
        return this.inDBGeneric( "SELECT * FROM reserva_equipamento WHERE "
                + "id_equipamento = (SELECT id_equipamento FROM equipamento WHERE " + "equipamento.codigo = \"" + e.getCodigo()
                + "\" and " + "equipamento.descricao = \"" + e.getDescricao() + "\");" );
    }

    /**
     * Captures the next equipment resulted of the query made before 
     * @return Equipamento -  Equipment
     */
    private Equipamento fetchEquipamento( ResultSet rs ) throws PatrimonyException, SQLException {
        return new Equipamento( rs.getString( "codigo" ), rs.getString( "descricao" ) );
    }
    
    //Metodo para atualizar a query no banco
    private void updateQuery( String msg ) throws SQLException {
        Connection con = FactoryConnection.getInstance().getConnection();
        PreparedStatement pst = con.prepareStatement( msg );
        pst.executeUpdate();
        pst.close();
        con.close();
    }

}
