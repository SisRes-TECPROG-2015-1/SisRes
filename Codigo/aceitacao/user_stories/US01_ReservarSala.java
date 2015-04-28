package user_stories;

import java.awt.Dimension;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.Aluno;
import model.Teacher;
import model.StudentRoomReserve;
import model.TeacherRoomReserve;
import model.Sala;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import persistence.StudentDAO;
import persistence.TeacherDAO;
import persistence.StudentRoomReserveDAO;
import persistence.TeacherRoomReserveDAO;
import persistence.ClassRoomDAO;
import view.Main2;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

/**
 * US1 T�tulo: Reservar sala. Como cliente (aluno/professor), Eu quero reservar
 * uma sala Para que eu possa usufruir, sempre que necess�rio, do espa�o
 * dispon�vel na FGA.
 * 
 * Cen�rio 1: Professor deseja reservar sala dispon�vel. Dado que o professor
 * est� cadastrado, E a sala est� cadastrada, E a sala est� dispon�vel, Quando o
 * usu�rio solicitar a reserva da sala pelo professor, Ent�o o sistema reserva a
 * sala, E informar que a reserva foi realizada com sucesso.
 * 
 * Cen�rio 2: Aluno deseja reservar sala dispon�vel. Dado que um aluno possui
 * cadastro, E a sala est� cadastrada, E a sala est� dispon�vel, Quando o
 * usu�rio solicitar a reserva pelo aluno, Ent�o o sistema reserva a sala, E
 * informar que a reserva foi realizada com sucesso.
 * 
 * Cen�rio 3: Professor deseja reservar sala j� reservada por aluno. Dado que o
 * professor est� cadastrado, E a sala est� cadastrada, E a sala est� reservada
 * por um aluno, Quando o usu�rio solicitar a reserva da sala pelo professor,
 * Ent�o o sistema cancela a reserva feita pelo aluno, E o sistema reserva a
 * sala pelo professor, E informar que a reserva foi realizada com sucesso.
 * 
 * 
 * Cen�rio 4: Professor deseja reservar sala reservada por professor Dado que o
 * professor est� cadastrado, E a sala est� cadastrada, E a sala j� est�
 * reservada por um professor, Quando o usu�rio solicitar a reserva da sala pelo
 * professor, Ent�o o sistema dever� negar a reserva, E o sistema deve informar
 * que a sala est� indispon�vel para o dia e hor�rio escolhido. E o sistema n�o
 * deve substituir a reserva.
 */

public class US01_ReservarSala {

    private FrameFixture window;
    private Robot robot;
    private Sala sala;
    private TeacherRoomReserve reservaProf;
    private StudentRoomReserve reservaAluno;
    private Aluno aluno;
    private Teacher prof;
    private DialogFixture dialog;
    private int index;
    private int indexReserva;
    private String data;

    // private int index;

    private void dataAtual() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        this.data = formatador.format(date);
    }

    @Before public void setUp() throws PatrimonyException, SQLException, ClienteException, ReserveException {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        robot.settings().delayBetweenEvents(5);

        window = new FrameFixture(robot, new Main2());
        window.show(new Dimension(900, 500)); // shows the frame to test

        sala = new Sala("code", "Sala para testes de aceitacao", "123");
        ClassRoomDAO.getInstance().includeARoom(sala);

        prof = new Teacher("Professor Teste", "658.535.144-40", "110038096", "9211-2144", "teste incluir repetido");
        TeacherDAO.getInstance().includeNewTeacher(prof);

        aluno = new Aluno("Aluno Teste", "658.535.144-40", "110038096", "9211-2144", "teste incluir repetido");
        StudentDAO.getInstance().includeNewStudent(aluno);

        dataAtual();

        index = ClassRoomDAO.getInstance().searchAll().size() - 1;
        indexReserva = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).size() - 1;

        window.button("Sala").click();
        dialog = window.dialog("SalaView");
    }

    @After public void tearDown() throws SQLException, PatrimonyException, ClienteException, ReserveException {
        if (reservaProf != null)
            TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(reservaProf);
        if (reservaAluno != null)
            StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reservaAluno);
        if (sala != null)
            ClassRoomDAO.getInstance().excludeRoom(sala);
        if (aluno != null)
            StudentDAO.getInstance().deleteStudent(aluno);
        if (prof != null)
            TeacherDAO.getInstance().excludeATeacher(prof);
        window.cleanUp();
    }

    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test public void testCenario1Professor() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("professorRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("23:59");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("Reserva feita com sucesso");
        fazerReservaSalaView.optionPane().okButton().click();

        indexReserva = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).size() - 1;
        reservaProf = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).get(indexReserva);
    }

    @Test public void testCenario1ProfessorCpfInvalido() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("professorRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("65853514440");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.optionPane().requireMessage(
                "Professor nao Cadastrado. Digite o CPF correto ou cadastre o professor desejado");
        fazerReservaSalaView.optionPane().okButton().click();
        reservaProf = null;
    }

    @Test public void testProfessorHoraAnterior() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("professorRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("00:00");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("A hora escolhida ja passou.");
        fazerReservaSalaView.optionPane().okButton().click();
        
    }

    @Test public void testCenario2Aluno() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("alunoRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("23:59");
        fazerReservaSalaView.button("VerificarCadeirasButton").click();
        fazerReservaSalaView.textBox("Quantidade de Cadeiras Reservadas").enterText("123");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("Reserva feita com sucesso");
        fazerReservaSalaView.optionPane().okButton().click();

        indexReserva = StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay(data).size() - 1;
        reservaAluno = StudentRoomReserveDAO.getInstance().getAllStudentReservedRooms().lastElement();
    }

    @Test public void testCenario2AlunoCpfInvalido() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("alunoRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("65853514440");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.optionPane().requireMessage("Aluno nao Cadastrado. Digite o CPF correto ou cadastre o aluno desejado");
        fazerReservaSalaView.optionPane().okButton().click();
        reservaProf = null;
    }

    @Test public void testCenario2AlunoHoraAnterior() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("alunoRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("00:00");
        fazerReservaSalaView.button("VerificarCadeirasButton").click();
        fazerReservaSalaView.textBox("Quantidade de Cadeiras Reservadas").enterText("123");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("A hora escolhida ja passou.");
        fazerReservaSalaView.optionPane().okButton().click();

    }

    @Test public void testCenario2AlunoCadeirasIndisponiveis() throws SQLException, ClienteException, PatrimonyException, ReserveException {
                
        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("alunoRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("00:00");
        fazerReservaSalaView.button("VerificarCadeirasButton").click();
        fazerReservaSalaView.textBox("Quantidade de Cadeiras Reservadas").enterText("1234");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("A sala nao possui este numero de cadeiras para reservar.");
        fazerReservaSalaView.optionPane().okButton().click();

    }

    
    @Test public void testCenario3() throws SQLException, ClienteException, PatrimonyException, ReserveException {

        reservaAluno = new StudentRoomReserve(data, "23:59", sala, "abc", sala.getCapacidade(), aluno);
        StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reservaAluno);

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("professorRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("23:59");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("Reserva feita com sucesso");
        fazerReservaSalaView.optionPane().okButton().click();

        indexReserva = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).size() - 1;
        reservaProf = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).get(indexReserva);
        reservaAluno = null;
    }

    
    @Test public void testCenario3AlunoReserva() throws SQLException, ClienteException, PatrimonyException, ReserveException {
        Aluno aluno2 = new Aluno("Aluno Teste", "382.808.446-00", "110", "", "abc");
        StudentDAO.getInstance().includeNewStudent(aluno2);

        StudentRoomReserve reservaAluno2 = new StudentRoomReserve(data, "23:59", sala, "abc", "100", aluno2);
        StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reservaAluno2);

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("alunoRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("23:59");
        fazerReservaSalaView.button("VerificarCadeirasButton").click();
        fazerReservaSalaView.textBox("Quantidade de Cadeiras Reservadas").enterText("23");
        fazerReservaSalaView.button("Reservar").click();

        fazerReservaSalaView.optionPane().requireMessage("Reserva feita com sucesso");
        fazerReservaSalaView.optionPane().okButton().click();

        indexReserva = StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay(data).size() - 1;
        reservaAluno = StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay(data).get(indexReserva);
        
        StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reservaAluno2);
        StudentDAO.getInstance().deleteStudent(aluno2);        
    }

    
    @Test public void testCenario4() throws SQLException, ClienteException, ReserveException, PatrimonyException {

        reservaProf = new TeacherRoomReserve(data, "23:59", sala, "abc", prof);
        TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reservaProf);

        dialog.table("tabelaPatrimonio").selectRows(index);
        dialog.button("Visualizar Horarios").click();

        DialogFixture diaReservaSala = dialog.dialog("DiaReservaSala");
        diaReservaSala.button("VisualizarButton").click();

        DialogFixture horarioReservaSala = dialog.dialog("HorarioReservaSala");
        horarioReservaSala.button("ReservarButton").click();

        DialogFixture fazerReservaSalaView = dialog.dialog("FazerReservaSalaView");
        fazerReservaSalaView.radioButton("professorRadioButton").click();
        fazerReservaSalaView.textBox("CPF").enterText("658.535.144-40");
        fazerReservaSalaView.button("BuscarCpfButton").click();
        fazerReservaSalaView.textBox("Finalidade").enterText("aula");
        fazerReservaSalaView.textBox("Hora").enterText("23:59");
        fazerReservaSalaView.button("Reservar").click();

        indexReserva = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).size() - 1;
        reservaProf = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay(data).get(indexReserva);
        reservaAluno = null;

        fazerReservaSalaView.optionPane().requireMessage("A Sala esta reservada no mesmo dia e horario.");
        fazerReservaSalaView.optionPane().okButton().click();

    }

}
