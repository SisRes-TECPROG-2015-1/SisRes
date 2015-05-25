/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.horariosReservas;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import model.Equipment;
import model.Heritage;
import model.TeacherEquipmentReserve;
import view.reservasEquipamentos.FazerReservaEquipamentoView;
import view.reservasEquipamentos.ReservaEquipamentoView;
import control.MaintainEquipmentReservationByTeacher;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

/**
 * 
 * @author Parley
 */
public class HorariosReservaEquipamento extends HorariosReservaPatrimonio {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Equipment equipment;
    MaintainEquipmentReservationByTeacher instance;

    public HorariosReservaEquipamento (java.awt.Frame parent, boolean modal, String data, Equipment equipment ) {
        super( parent, modal, data, equipment );
        this.equipment = equipment;
    }

    /** This method fills a vector with the data informated by the user
     * @param object: object to be filled into the vector
     * @param index: number of indexes
     * @return: arrayList of String
     */
    protected Vector<String> fillDataVector( Object object, int index ) {
        Vector<String> nomesTabela = new Vector<String>();
        if ( object instanceof TeacherEquipmentReserve ) {
            TeacherEquipmentReserve r = ( TeacherEquipmentReserve ) object;
            if ( equipmentIsNotNull() && reservedEquipmentIsEqual( r ) ) {

                addNamesInTable( index, nomesTabela, r );
            }
        }

        return nomesTabela;

    }

	/**
	 * @param r
	 * @return
	 */
	private boolean reservedEquipmentIsEqual( TeacherEquipmentReserve r ) {
		return r.getEquipment().equals( this.equipment );
	}

	/**
	 * @return
	 */
	private boolean equipmentIsNotNull() {
		return this.equipment != null;
	}

	/**
	 * @param index
	 * @param nomesTabela
	 * @param r
	 */
	private void addNamesInTable( int index, Vector<String> nomesTabela,
			TeacherEquipmentReserve r ) {
		nomesTabela.add( String.valueOf( index ) );
		nomesTabela.add( r.getHour() );
		nomesTabela.add( r.getProfessor().getName() );
		nomesTabela.add( r.getProfessor().getRegistration() );
		nomesTabela.add( r.getEquipment().getCode() );
		nomesTabela.add( r.getEquipment().getDescription() );
	}

    @Override protected DefaultTableModel fillTable( Heritage equip ) {
        this.equipment = ( Equipment ) equip;
        DefaultTableModel table = new DefaultTableModel();
        instance = MaintainEquipmentReservationByTeacher.getInstance();
        try {
            addColumnsInTable( table );

            this.mes = Integer.parseInt( this.data.substring( 3, 5 ) );

            Vector<TeacherEquipmentReserve> v = instance.getClassroomReservesByMonth( mes );
            if ( v != null ){
                for ( int i = 0; i < v.size(); i++ ) {
                    table.addRow( fillDataVector( v.get( i ), i ) );

                }
	        }else{
	        	//do nothing
	        }

        } catch ( SQLException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName()).log( Level.SEVERE, null, ex );
        } catch ( PatrimonyException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( ClientException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( ReserveException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return table;

    }

	/**
	 * @param table
	 */
	private void addColumnsInTable( DefaultTableModel table ) {
		table.addColumn( "" );
		table.addColumn( "Hora:" );
		table.addColumn( "Nome" );
		table.addColumn( "Matricula" );
		table.addColumn( "Codigo Eqpt." );
		table.addColumn( "Descricao Eqpt." );
	}

    @Override protected void cancelarReservaAction( int index ) {
        try {
            int confirm = JOptionPane.showConfirmDialog( this,
                    "Deseja mesmo excluir Reserva?\n" + instance.getClassroomReservesByMonth( mes ).get( index ).toString(), "Excluir",
                    JOptionPane.YES_NO_OPTION );

            if ( confirm == JOptionPane.YES_OPTION ) {
                this.instance.excludeClassroomReserve( instance.getClassroomReservesByMonth( mes ).get( index ) );
                JOptionPane.showMessageDialog( this, "Reserva excluida com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                        null );
            }else{
            	//do nothing
            }

        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( ReserveException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

    @Override protected void reservarAction() {
        try {
            ReservaEquipamentoView reserva = new FazerReservaEquipamentoView( new JFrame(), true, this.equipment, this.data );
            reserva.setVisible( true );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( ReserveException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

    @Override protected void alterarAction( int index ) {
        /** 
         * This block was already commented and will be maintened to future revision.
         * 
         * try { index = Integer.parseInt((String)
         * this.reservasTable.getModel().getValueAt(index, 0));
         * ReservaEquipamentoView reserva = new
         * AlterarReservaEquipamentoView(new JFrame(), true, index, this.mes);
         * reserva.setVisible(true);
         * 
         * } catch (SQLException ex) { JOptionPane.showMessageDialog(this,
         * ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null); } catch
         * (PatrimonioException ex) { JOptionPane.showMessageDialog(this,
         * ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null); } catch
         * (ClienteException ex) { JOptionPane.showMessageDialog(this,
         * ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null); } catch
         * (ReservaException ex) { JOptionPane.showMessageDialog(this,
         * ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null); }
         */
    }
}
