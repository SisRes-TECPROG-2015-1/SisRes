/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.diasReservas;

import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JFrame;

import model.Equipment;
import view.horariosReservas.HorariosReservaEquipamento;
import view.horariosReservas.HorariosReservaPatrimonio;
import control.MaintainEquipment;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class DiaReservaEquipamento extends DiaReservaPatrimonio {

    Equipment eq;

    public DiaReservaEquipamento( Frame parent, boolean modal, int indexEquipamento ) throws SQLException, PatrimonyException {
        super( parent, modal );
        eq = MaintainEquipment.getInstance().getEquipments().get( indexEquipamento );
    }
    
    
    // This method sets the action visible to the user
    @Override protected void visualizarAction( String data ) {
        HorariosReservaPatrimonio reserva = new HorariosReservaEquipamento( new JFrame(), true, data, this.eq );
        reserva.setVisible( true );
        reserva.setResizable( false );
    }
}
