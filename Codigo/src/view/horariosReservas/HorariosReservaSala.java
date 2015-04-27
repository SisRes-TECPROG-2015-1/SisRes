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

import model.Heritage;
import model.Room;
import model.StudentRoomReserve;
import model.TeacherRoomReserve;
import view.reservasSalas.AlterarReservaAlunoSalaView;
import view.reservasSalas.AlterarReservaProfSalaView;
import view.reservasSalas.FazerReservaSalaView;
import view.reservasSalas.ReservaSalaView;
import control.MaintainClassroomReservationByStudent;
import control.MaintainClassroomReservationByTeacher;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

/**
 * 
 * @author Parley
 */
public class HorariosReservaSala extends HorariosReservaPatrimonio {

    MaintainClassroomReservationByStudent instanceAluno;
    MaintainClassroomReservationByTeacher instanceProf;
    Room room;

    public HorariosReservaSala( java.awt.Frame parent, boolean modal, String data, Room sala ) {
        super( parent, modal, data, sala );
        this.room = sala;
        this.setName( "HorarioReservaSala" );
    }

    // This method fills a vector with the data informated by the user
    protected Vector<String> fillDataVector( Object o, int index ) {
        Vector<String> nomesTabela = new Vector<String>();
        if ( o instanceof StudentRoomReserve ) {
            StudentRoomReserve r = ( StudentRoomReserve ) o;
            if ( this.room != null && ( r.getRoom().equals( this.room ) ) ) {
                addNamesInTableStudentReserve( index, nomesTabela, r );
            }
        } else if ( o instanceof TeacherRoomReserve ) {
            TeacherRoomReserve r = ( TeacherRoomReserve ) o;
            if (this.room != null && ( r.getRoom().equals( this.room ) ) ) {

                addNamesInTableTeacherReserve( index, nomesTabela, r );
            }
        }

        return nomesTabela;

    }

	/**
	 * @param index
	 * @param nomesTabela
	 * @param r
	 */
	private void addNamesInTableTeacherReserve( int index,
			Vector<String> nomesTabela, TeacherRoomReserve r ) {
		nomesTabela.add( String.valueOf( index ) );
		nomesTabela.add( "Professor" );
		nomesTabela.add( r.getHour() );
		nomesTabela.add( r.getTeacher().getName() );
		nomesTabela.add( r.getTeacher().getRegistration() );
		nomesTabela.add( r.getFinality() );
		nomesTabela.add( r.getRoom().getCode() );
		nomesTabela.add( r.getRoom().getDescription() );
		nomesTabela.add( r.getRoom().getCapacity() );
		nomesTabela.add( r.getRoom().getCapacity() );
	}

	/**
	 * @param index
	 * @param nomesTabela
	 * @param r
	 */
	private void addNamesInTableStudentReserve( int index, Vector<String> nomesTabela,
			StudentRoomReserve r ) {
		nomesTabela.add( String.valueOf( index ) );
		nomesTabela.add( "Aluno" );
		nomesTabela.add( r.getHour() );
		nomesTabela.add( r.getStudent().getName() );
		nomesTabela.add( r.getStudent().getRegistration() );
		nomesTabela.add( r.getFinality() );
		nomesTabela.add( r.getRoom().getCode() );
		nomesTabela.add( r.getRoom().getDescription() );
		nomesTabela.add( r.getReservedChairs() );
		nomesTabela.add( r.getRoom().getCapacity() );
	}

    // This method fill a table with the information of the room
    @Override protected DefaultTableModel fillTable( Heritage sala ) {
        this.room = ( Room ) sala;
        DefaultTableModel table = new DefaultTableModel();
        instanceAluno = MaintainClassroomReservationByStudent.getInstance();
        instanceProf = MaintainClassroomReservationByTeacher.getInstance();
        addColumnsInTable( table );

        this.mes = Integer.parseInt( this.data.substring( 3, 5 ) );

        try {
            Vector v = instanceProf.getRoomByDate( this.data );

            if ( v != null )
                for ( int i = 0; i < v.size(); i++ ) {
                    Vector<String> linha = fillDataVector( v.get(i), i );
                    if ( !linha.isEmpty() )
                        table.addRow( linha );

                }
            v.clear();

            v = instanceAluno.getRoomReservesByDate( this.data );
            if ( v != null )
                for ( int i = 0; i < v.size(); i++ ) {
                    Vector<String> linha = fillDataVector( v.get(i), i );
                    if ( !linha.isEmpty() )
                        table.addRow( linha );

                }

        } catch ( SQLException ex) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( PatrimonyException ex ) {
            Logger.getLogger(HorariosReservaPatrimonio.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( ClientException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName() ).log(Level.SEVERE, null, ex );
        } catch ( ReserveException ex ) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName()).log(Level.SEVERE, null, ex );
        } catch ( NullPointerException ex) {
            Logger.getLogger( HorariosReservaPatrimonio.class.getName()).log(Level.SEVERE, null, ex );
        }

        return table;

    }

	/**
	 * @param table
	 */
	private void addColumnsInTable( DefaultTableModel table ) {
		table.addColumn( "" );
        table.addColumn( "Tipo:" );
        table.addColumn( "Hora:" );
        table.addColumn( "Nome" );
        table.addColumn( "Matricula" );
        table.addColumn( "Finalidade" );
        table.addColumn( "Codigo da Sala" );
        table.addColumn( "Descricao da Sala" );
        table.addColumn( "Reservadas" );
        table.addColumn( "Capacidade" );
	}

    // This method is the action of the buttom to cancel the booking of the room
    @Override protected void cancelarReservaAction( int index ) {
        try {
            String tipoCliente = ( String ) this.reservasTable.getModel().getValueAt( index, 1 );
            index = Integer.parseInt( ( String ) this.reservasTable.getModel().getValueAt( index, 0 ) );
            if ( tipoCliente.equals( "Aluno" ) ) {
                int confirm = JOptionPane.showConfirmDialog( this,
                        "Deseja mesmo excluir Reserva?\n" + instanceAluno.getRoomReservesByDate( data ).get( index ).toString(), "Excluir",
                        JOptionPane.YES_NO_OPTION );

                if ( confirm == JOptionPane.YES_OPTION ) {
                    this.instanceAluno.excludeRoom( instanceAluno.getRoomReservesByDate( data ).get( index ) );
                    JOptionPane.showMessageDialog( this, "Reserva excluida com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                            null );
                }
            } else if ( tipoCliente.equals( "Professor" ) ) {
                int confirm = JOptionPane.showConfirmDialog( this,
                        "Deseja mesmo excluir Reserva?\n" + instanceProf.getRoomByDate( data ).get( index ).toString(), "Excluir",
                        JOptionPane.YES_NO_OPTION );

                if ( confirm == JOptionPane.YES_OPTION ) {
                    this.instanceProf.excludeReserve( instanceProf.getRoomByDate( data ).get( index ) );
                    JOptionPane.showMessageDialog( this, "Reserva excluida com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE,
                            null );
                }
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

    // This method is the action of the button to make the booking of the room
    @Override protected void reservarAction() {
        try {
            ReservaSalaView reserva = new FazerReservaSalaView(new JFrame(), true, room, this.data);
            reserva.setVisible(true);
        } catch ( SQLException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch ( PatrimonyException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch ( ClientException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        } catch ( ReserveException ex ) {
            JOptionPane.showMessageDialog( this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE, null);
        }
    }

    // This method is the action of the button to modify the booking
    @Override protected void alterarAction( int index ) {
        try {
            String tipoCliente = ( String ) this.reservasTable.getModel().getValueAt( index, 1 );
            index = Integer.parseInt( ( String ) this.reservasTable.getModel().getValueAt( index, 0 ) );
            if ( tipoCliente.equals( "Aluno" ) ) {
                ReservaSalaView reserva = new AlterarReservaAlunoSalaView( new JFrame(), true, index, this.data );
                reserva.setVisible( true );
            } else if ( tipoCliente.equals( "Professor") ) {
                ReservaSalaView reserva = new AlterarReservaProfSalaView( new JFrame(), true, index, this.data );
                reserva.setVisible( true );
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
}