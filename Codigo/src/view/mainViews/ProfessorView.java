/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mainViews;

import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import view.alteracoes.AlterarProfessor;
import view.cadastros.CadastroCliente;
import view.cadastros.CadastroProfessor;
import control.MaintainTeacher;
import exception.ClientException;

/**
 * 
 * @author Parley
 */
public class ProfessorView extends ClienteView {

    public ProfessorView( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        this.setName( "ProfessorView" );
    }

    /**
     * This method obtains an interator
     */
    public Iterator getIterator() {
        try {
            return MaintainTeacher.getInstance().getTeachers().iterator();

        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
        return null;
    }

    /**
     * This method is the action of the cadastre button
     */
    @Override public void cadastrarAction() {

        CadastroCliente cadastrar = new CadastroProfessor( new javax.swing.JFrame(), true );
        cadastrar.setResizable( false );
        cadastrar.setVisible( true );
        tabelaCliente.setModel( fillTable() );

    }

    /**
     * This method is the action of the modify button
     */
    @Override public void alterarAction( int index ) {

        AlterarProfessor alterar = new AlterarProfessor( new javax.swing.JFrame(), true, index );
        alterar.setResizable( false );
        alterar.setVisible( true );
        this.tabelaCliente.setModel( fillTable() );
    }

    /**
     * This method is the action of the exclude button
     */
    @Override public void excluirAction() {
        try {
            int index = this.tabelaCliente.getSelectedRow();
            if ( index < 0 ) {
                JOptionPane.showMessageDialog( this, "Selecione uma linha!", "Erro", JOptionPane.ERROR_MESSAGE, null );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog( this, "Deseja mesmo excluir Professor: "
                    + MaintainTeacher.getInstance().getTeachers().get(index).getName() + "?", "Excluir",
                    JOptionPane.YES_NO_OPTION );
            if ( confirm == JOptionPane.YES_OPTION ) {
                MaintainTeacher.getInstance().excludeTeacher( MaintainTeacher.getInstance().getTeachers().get(index) );
                JOptionPane.showMessageDialog( this, "Professor excluido com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                        null );
            }
            this.tabelaCliente.setModel( fillTable() );

        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }
}