/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.alteracoes;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import view.cadastros.CadastroCliente;
import control.MaintainTeacher;
import exception.ClienteException;

/**
 * 
 * @author Parley
 */
public class AlterarProfessor extends CadastroCliente {

    int index2 = 0;

    public AlterarProfessor(java.awt.Frame parent, boolean modal, int index) {
        super(parent, modal);
        this.setName("AlterarProfessor");
        this.cadastroBtn.setText("Alterar");
        this.cadastroBtn.setName("Alterar");
        this.index2 = index;

        try {
            this.nomeTxtField.setText(MaintainTeacher.getInstance().getProfessores_vet().get(index).getNome());
            this.emailTxtField.setText(MaintainTeacher.getInstance().getProfessores_vet().get(index).getEmail());
            this.telefoneTxtField.setText(MaintainTeacher.getInstance().getProfessores_vet().get(index).getTelefone());
            this.matriculaTxtField.setText(MaintainTeacher.getInstance().getProfessores_vet().get(index).getMatricula());
            this.cpfTxtField.setText(MaintainTeacher.getInstance().getProfessores_vet().get(index).getCpf());

        } catch (ClienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    // This method is the action to the cadastre button
    @Override public void cadastroAction() {
        try {
            MaintainTeacher.getInstance().alterar(nomeTxtField.getText(), cpfTxtField.getText(), matriculaTxtField.getText(),
                    telefoneTxtField.getText(), emailTxtField.getText(),
                    MaintainTeacher.getInstance().getProfessores_vet().get(index2));

            JOptionPane.showMessageDialog(this, "Cadastro alterado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE, null);
            this.setVisible(false);
        } catch (ClienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
    }
}
