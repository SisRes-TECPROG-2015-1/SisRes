package control;

import java.sql.SQLException;
import java.util.Vector;

import model.Equipamento;
import model.Professor;
import model.ReservaEquipamentoProfessor;
import persistence.ResEquipamentoProfessorDAO;
import exception.ClienteException;
import exception.PatrimonioException;
import exception.ReservaException;

/**
 * Equipment and rooms reservation system
 * ManterResEquipamentoProfessor class with methods to accomplish CRUD reserves of equipments by a teacher
 */
public class ManterResEquipamentoProfessor {
	
	/**
	 * Creating a vector of reserves of equipments by a teacher
	 */
    private Vector < Object > rev_equipamento_professor_vet = new Vector < Object >();

    //This attribute is an instance of a ManterResEquipamentoProfessor type
    private static ManterResEquipamentoProfessor instance;

    /**
	 * Empty constructor
	 */
    private ManterResEquipamentoProfessor() {
    }

    
    /**
	 * Creates an instance of an equipment reserve for a teacher if it isn't already instantiated.
	 * @return - ManterResEquipamentoProfessor - Equipment reserve
	 */
    public static ManterResEquipamentoProfessor getInstance() {
        if ( instance == null )
            instance = new ManterResEquipamentoProfessor();
        return instance;
    }


    /**
     * Captures the equipment reserves for teachers in the hour searched.
     * Parameter hora used to bring information about reserve's hour
     * @return Vector - Equipment reserves for teachers
     */
    public Vector < ReservaEquipamentoProfessor > getReservasHora( String hora ) throws SQLException, PatrimonioException,
            ClienteException, ReservaException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorHora( hora );
    }

    /**
     * Captures the equipments reserves for teachers which had reserved a equipment in the month searched.
     * Parameter mes used to bring information about reserve's mes
     * @return Vector - Equipments reserves for teachers
     */
    public Vector < ReservaEquipamentoProfessor > getReservasMes( int mes ) throws SQLException, PatrimonioException, ClienteException,
            ReservaException {
        return ResEquipamentoProfessorDAO.getInstance().buscarPorMes( mes );
    }

    
    /**
     * Lists all the equipments reserves for teachers.
     * @return Vector - equipments reserves for teachers
     */
    public Vector < Object > getResEquipamentoProfessor_vet() throws SQLException, ClienteException, PatrimonioException,
            ReservaException {
        this.rev_equipamento_professor_vet = ResEquipamentoProfessorDAO.getInstance().buscarTodos();
        return this.rev_equipamento_professor_vet;
    }

    
    /**
     * Reserves an equipment to a teacher to a date and hour.
     * Parameters equipamento, prof, data and hour used to create a reserves of equipments by a teacher.
     */
    public void inserir( Equipamento equipamento, Professor prof, String data, String hora ) throws SQLException, ReservaException {
        ReservaEquipamentoProfessor reserva = new ReservaEquipamentoProfessor( data, hora, equipamento, prof );
        ResEquipamentoProfessorDAO.getInstance().incluir( reserva );
        this.rev_equipamento_professor_vet.add( reserva );
    }

    
    /**
     * Changes the equipments reserve.
     * Parameters finalidade and reserva used to bring information for upadate the reserves of equipments by a teacher
     */
    public void alterar( String finalidade, ReservaEquipamentoProfessor reserva ) throws SQLException, ReservaException {
        ReservaEquipamentoProfessor reserva_old = new ReservaEquipamentoProfessor( reserva.getData(), reserva.getHora(),
                reserva.getEquipamento(), reserva.getProfessor());
        ResEquipamentoProfessorDAO.getInstance().alterar( reserva_old, reserva );

    }

    
    /**
     * Excludes a equipment reserve.
     */
    public void excluir( ReservaEquipamentoProfessor reserva ) throws SQLException, ReservaException {
        ResEquipamentoProfessorDAO.getInstance().excluir( reserva );
        this.rev_equipamento_professor_vet.remove( reserva );
    }

}
