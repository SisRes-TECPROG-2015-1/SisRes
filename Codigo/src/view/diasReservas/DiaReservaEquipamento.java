/**
* Equipment and Rooms Reservation System
* This file contains the action for the registration of a equipment reserve for a given date.
*/

package view.diasReservas;

import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JFrame;

import model.Equipamento;
import view.horariosReservas.HorariosReservaEquipamento;
import view.horariosReservas.HorariosReservaPatrimonio;
import control.ManterEquipamento;
import exception.PatrimonioException;

/**
 * 
 * @author Parley
 */
public class DiaReservaEquipamento extends DiaReservaPatrimonio {

    Equipamento eq;

    public DiaReservaEquipamento( Frame parent, boolean modal, int indexEquipamento ) throws SQLException, PatrimonioException {
        super( parent, modal );
        eq = ManterEquipamento.getInstance().getEquipamento_vet().get( indexEquipamento );
    }
    
    
    // This method sets the action visible to the user
    @Override protected void visualizarAction( String data ) {
        HorariosReservaPatrimonio reserva = new HorariosReservaEquipamento( new JFrame(), true, data, this.eq );
        reserva.setVisible( true );
        reserva.setResizable( false );
    }
}
