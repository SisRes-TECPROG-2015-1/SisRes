/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mainViews;

import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JOptionPane;



import view.alteracoes.ModifyStudent;
import view.cadastros.StudentCadastre;
import view.cadastros.ClientCadastre;
import control.MaintainStudent;
import exception.ClientException;

/**
 * 
 * @author Parley
 */
public class StudentView extends ClientView {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentView( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        this.setName( "AlunoView" );
    }

    /**
     *  This method obtains the iterator 
     */
    public Iterator getIterator() {
        try {
            return MaintainStudent.getInstance().getStudents().iterator();

        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
        return null;
    }

    /**
     *  This method is the action of the button that sets a new student to the room
     */
    @Override public void cadastrarAction() {

        ClientCadastre cadastrar = new StudentCadastre( new javax.swing.JFrame(), true );
        cadastrar.setResizable( false );
        cadastrar.setVisible( true );
        tabelaCliente.setModel( fillTable() );

    }

    /**
     *  This method is the action of the button to modify a client in the view 
    **/
    @Override public void alterarAction( int index ) {

        ModifyStudent alterar = new ModifyStudent( new javax.swing.JFrame(), true, index );
        alterar.setResizable( false );
        alterar.setVisible( true );
        this.tabelaCliente.setModel( fillTable() );
    }

    /**
     *  This method is the action of the button to exclude a client in the view
     */
    @Override public void excluirAction() {
        try {
            int index = this.tabelaCliente.getSelectedRow();
            if ( index < 0 ) {
                JOptionPane.showMessageDialog(this, "Selecione uma linha!", "Erro", JOptionPane.ERROR_MESSAGE, null);
                return;
            }else{
            	//do nothing
            }

            int confirm = JOptionPane.showConfirmDialog( this, "Deseja mesmo excluir Aluno: "
                    + MaintainStudent.getInstance().getStudents().get(index).getName() + "?"
                    , "Excluir", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION ) {
                MaintainStudent.getInstance().excludeStudent(
                		MaintainStudent.getInstance().getStudents().get(index) );
                JOptionPane.showMessageDialog( this, "Aluno excluido com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null );
            }else{
            	//do nothing
            }
            
            this.tabelaCliente.setModel( fillTable() );

        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null );
        }
    }

}