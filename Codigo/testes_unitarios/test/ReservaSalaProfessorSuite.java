package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.control.ManterResSalaProfessorTest;
import test.model.ReservaSalaProfessorTest;
import test.persistence.TeacherRoomReserveDAOTest;

@RunWith(Suite.class)
@SuiteClasses({ReservaSalaProfessorTest.class, TeacherRoomReserveDAOTest.class, ManterResSalaProfessorTest.class})
public class ReservaSalaProfessorSuite {

}
