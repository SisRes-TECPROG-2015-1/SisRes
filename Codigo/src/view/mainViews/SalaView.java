/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mainViews;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import model.Room;
import view.alteracoes.ModifyRoom;
import view.cadastros.HeritageCadastre;
import view.cadastros.ClassroomCadastre;
import view.diasReservas.ReservationDaysRoom;
import control.MaintainRoom;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class SalaView extends PatrimonioView {

    public SalaView( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        pesquisarLbl.setText( "Digite a sala desejada: " );
        this.setName( "SalaView" );
    }

    /**
     * This method fills a vector with the information about room reservation
     */
    protected Vector<String> fillDataVector( Room sala ) {

        if ( sala == null ) {
            return null;
        }

        Vector<String> nomesTabela = new Vector<String>();

        addNamesInTable( sala, nomesTabela );

        return nomesTabela;

    }

	/**
	 * @param sala
	 * @param nomesTabela
	 */
	private void addNamesInTable( Room sala, Vector<String> nomesTabela ) {
		nomesTabela.add( sala.getCode() );
        nomesTabela.add( sala.getDescription() );
        nomesTabela.add( sala.getCapacity() );
	}

    /**
     * This method fills a table with the rooms available
     */
    @Override protected DefaultTableModel fillTable() {
        try {
            DefaultTableModel table = new DefaultTableModel();

            Iterator<Room> i = MaintainRoom.getInstance().getRooms().iterator();

            addColumnsInTable( table );
            while ( i.hasNext() ) {
                Room sala = i.next();
                table.addRow( fillDataVector(sala) );
            }

            return table;

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }

        return null;
    }

	/**
	 * @param table
	 */
	private void addColumnsInTable( DefaultTableModel table ) {
		table.addColumn( "Codigo" );
		table.addColumn( "Nome" );
		table.addColumn( "Capacidade" );
	}

    /**
     * This method is the action of the cadastre button.
     */
    @Override protected void cadastrarAction() {
        HeritageCadastre cadastro = new ClassroomCadastre( new javax.swing.JFrame(), true );
        cadastro.setResizable( false );
        cadastro.setVisible( true );
        this.tabelaPatrimonio.setModel( fillTable() );
    }

    /**
     * This method is the action of the modify button.
     */
    @Override protected void alterarAction( int index ) {

        ModifyRoom alteracao = new ModifyRoom( new javax.swing.JFrame(), true, index );
        alteracao.setResizable( false );
        alteracao.setVisible( true );
        this.tabelaPatrimonio.setModel( fillTable() );
    }
    
    /**
     * This method is the action of the delete button.
     */
    @Override protected void excluirAction( int index ) {
        try {
            int confirm = JOptionPane
                    .showConfirmDialog( this, "Deseja mesmo excluir Sala: "
                            + MaintainRoom.getInstance().getRooms().get(index).getDescription() + "?", "Excluir",
                            JOptionPane.YES_NO_OPTION );

            if ( confirm == JOptionPane.YES_OPTION ) {
                MaintainRoom.getInstance().excludeRoom( MaintainRoom.getInstance().getRooms().get( index) );
                JOptionPane.showMessageDialog(this, "Sala excluida com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            }else{
            	//do nothing
            }
            
            this.tabelaPatrimonio.setModel( fillTable() );

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

    /**
     * This method is the action of the visualize button.
     */
    @Override protected void visualizarAction( int index ) {
        try {
            ReservationDaysRoom reserva = new ReservationDaysRoom( new javax.swing.JFrame(), true, index );
            reserva.setResizable( false );
            reserva.setVisible( true );
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
