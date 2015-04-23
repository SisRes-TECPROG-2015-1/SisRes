package view.mainViews;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import model.Equipment;
import view.alteracoes.AlterarEquipamento;
import view.cadastros.CadastroEquipamento;
import view.diasReservas.DiaReservaEquipamento;
import control.MaintainEquipment;
import exception.PatrimonyException;

/**
 * 
 * @author Parley
 */
public class EquipamentoView extends PatrimonioView {

    public EquipamentoView( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        pesquisarLbl.setText( "Digite o eqpto. desejado: " );
        this.setTitle( "Equipamentos" );
        this.setName( "EquipamentoView" );
    }

    private Vector<String> fillDataVector( Equipment equipamento ) {

        if ( equipamento == null ) {
            return null;
        }

        Vector<String> nomesTabela = new Vector<String>();

        nomesTabela.add( equipamento.getCode() );
        nomesTabela.add( equipamento.getDescription() );

        return nomesTabela;

    }

    @Override protected DefaultTableModel fillTable() {
        try {
            DefaultTableModel table = new DefaultTableModel();

            Iterator<Equipment> i = control.MaintainEquipment.getInstance().getEquipments().iterator();

            table.addColumn( "Codigo" );
            table.addColumn( "Descricao" );

            while ( i.hasNext() ) {
                Equipment equipamento = i.next();
                table.addRow( fillDataVector( equipamento ) );
            }
            return table;

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch( NullPointerException ex ) {
            JOptionPane.showMessageDialog( this, ex.getLocalizedMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
        return null;
    }

    @Override protected void cadastrarAction() {
        CadastroEquipamento cadastro = new CadastroEquipamento( new javax.swing.JFrame(), true );
        cadastro.setResizable( false );
        cadastro.setVisible( true );
        this.tabelaPatrimonio.setModel( fillTable() );
    }

    @Override protected void alterarAction( int index ) {

        AlterarEquipamento alteracao = new AlterarEquipamento( new javax.swing.JFrame(), true, index );
        alteracao.setResizable( false );
        alteracao.setVisible( true);
        this.tabelaPatrimonio.setModel( fillTable() );

    }

    @Override protected void excluirAction( int index ) {

        try {
            int confirm = JOptionPane.showConfirmDialog( this, "Deseja mesmo excluir Equipamento: "
                    + MaintainEquipment.getInstance().getEquipments().get(index).getDescription() + "?", "Excluir",
                    JOptionPane.YES_NO_OPTION );

            if ( confirm == JOptionPane.YES_OPTION ) {
                MaintainEquipment.getInstance().excludeEquipment( MaintainEquipment.getInstance().getEquipments().get(index) );
                JOptionPane.showMessageDialog( this, "Equipamento excluido com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                        null );
            }
            this.tabelaPatrimonio.setModel( fillTable() );

        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( NullPointerException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }

    }

    @Override protected void visualizarAction( int index ) {
        try {
            DiaReservaEquipamento reserva = new DiaReservaEquipamento( new javax.swing.JFrame(), true, index );
            reserva.setResizable( false );
            reserva.setVisible( true );
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}
