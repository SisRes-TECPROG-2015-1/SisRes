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

import model.Sala;
import view.alteracoes.AlterarSala;
import view.cadastros.CadastroPatrimonio;
import view.cadastros.CadastroSala;
import view.diasReservas.DiaReservaSala;
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
    protected Vector<String> fillDataVector( Sala sala ) {

        if ( sala == null ) {
            return null;
        }

        Vector<String> nomesTabela = new Vector<String>();

        nomesTabela.add( sala.getCodigo() );
        nomesTabela.add( sala.getDescricao() );
        nomesTabela.add( sala.getCapacidade() );

        return nomesTabela;

    }

    /**
     * This method fills a table with the rooms available
     */
    @Override protected DefaultTableModel fillTable() {
        try {
            DefaultTableModel table = new DefaultTableModel();

            Iterator<Sala> i = MaintainRoom.getInstance().getRooms().iterator();

            table.addColumn( "Codigo" );
            table.addColumn( "Nome" );
            table.addColumn( "Capacidade" );
            while ( i.hasNext() ) {
                Sala sala = i.next();
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
     * This method is the action of the cadastre button.
     */
    @Override protected void cadastrarAction() {
        CadastroPatrimonio cadastro = new CadastroSala( new javax.swing.JFrame(), true );
        cadastro.setResizable( false );
        cadastro.setVisible( true );
        this.tabelaPatrimonio.setModel( fillTable() );
    }

    /**
     * This method is the action of the modify button.
     */
    @Override protected void alterarAction( int index ) {

        AlterarSala alteracao = new AlterarSala( new javax.swing.JFrame(), true, index );
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
                            + MaintainRoom.getInstance().getRooms().get(index).getDescricao() + "?", "Excluir",
                            JOptionPane.YES_NO_OPTION );

            if ( confirm == JOptionPane.YES_OPTION ) {
                MaintainRoom.getInstance().excludeRoom( MaintainRoom.getInstance().getRooms().get( index) );
                JOptionPane.showMessageDialog(this, "Sala excluida com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
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
            DiaReservaSala reserva = new DiaReservaSala( new javax.swing.JFrame(), true, index );
            reserva.setResizable( false );
            reserva.setVisible( true );
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
