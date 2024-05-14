package pbo.f01;

/**
 * 12S22003 - Yohana Natalia Siahaan
 * 12S22037 - Tiarani Sibarani
 */

import pbo.f01.model.Dorm;
import pbo.f01.model.Student;

import java.util.Scanner;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class App {

    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static void main(String[] _args) {
        factory = Persistence.createEntityManagerFactory("dormy_pu");
        entityManager = factory.createEntityManager();

        Scanner input = new Scanner(System.in);
        cleanTable();

        while (input.hasNextLine()) {
            String str = input.nextLine();

            if (str.equals("---")) {
                break;
            } else {
                String[] tokens = str.split("#");
                String command = tokens[0];

                switch (command) {
                    case "dorm-add":
                        addDorm(tokens);
                        break;

                    case "student-add":
                        addStudent(tokens);
                        break;

                    case "assign":
                        assignStudent(tokens);
                        break;

                    case "display-all":
                        displayAll();
                        break;
                }
            }
        }

        input.close();
        entityManager.close();
        factory.close();
    }

    private static void addDorm(String[] tokens) {
        entityManager.getTransaction().begin();
        try {
            Dorm dorm = new Dorm(tokens[1], tokens[2], tokens[3]);
            entityManager.persist(dorm);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void addStudent(String[] tokens) {
        entityManager.getTransaction().begin();
        try {
            // Check if the student already exists in the database
            List<Student> existingStudents = entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.nim = :nim AND s.std_nama = :std_nama AND s.year = :year AND s.gender = :gender", Student.class)
                .setParameter("nim", tokens[1])
                .setParameter("std_nama", tokens[2])
                .setParameter("year", Integer.parseInt(tokens[3]))
                .setParameter("gender", tokens[4])
                .getResultList();
    
            // If the student does not exist, add them to the database
            if (existingStudents.isEmpty()) {
                Student student = new Student();
                student.setNim(tokens[1]);
                student.setStd_Nama(tokens[2]);
                student.setYear(Integer.parseInt(tokens[3]));
                student.setGender(tokens[4]);
                entityManager.persist(student);
            }
    
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void assignStudent(String[] tokens) {
        entityManager.getTransaction().begin();
        try {
            Student student = entityManager.find(Student.class, tokens[1]);
            Dorm dorm = entityManager.find(Dorm.class, tokens[2]);
    
            // Check if the student or dorm is null
            if (student == null || dorm == null) {
                //System.out.println("Student or Dorm not found.");
                entityManager.getTransaction().rollback();
                return;
            }
    
            // Check if the dorm is full or the student's gender does not match the dorm's gender
            if (dorm.getStudents().size() >= Integer.parseInt(dorm.getCapacity()) || !student.getGender().equals(dorm.getGender())) {
                //System.out.println("Dorm " + tokens[2] + " is either full or the student's gender does not match the dorm's gender.");
                entityManager.getTransaction().rollback();
                return;
            }
    
            dorm.getStudents().add(student);
            student.getDorms().add(dorm);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void displayAll() {
        TypedQuery<Dorm> query = entityManager.createQuery("SELECT d FROM Dorm d ORDER BY d.dorm_nama ASC", Dorm.class);
        List<Dorm> dorms = query.getResultList();
    
        for (Dorm dorm : dorms) {
            System.out.println(dorm.getNama() + "|" + dorm.getGender() + "|" + dorm.getCapacity() + "|" + dorm.getStudents().size());
            List<Student> sortedStudents = dorm.getStudents().stream()
                .sorted(Comparator.comparing(Student::getStd_Nama))
                .collect(Collectors.toList());
            for (Student student : sortedStudents) {
                System.out.println(student.getNim() + "|" + student.getStd_Nama() + "|" + student.getYear());
            }
        }
    }

    private static void cleanTable() {
        String deleteStudentsJPQL = "DELETE FROM Student";
        String deleteDormsJPQL = "DELETE FROM Dorm";

        entityManager.getTransaction().begin();
        try {
            int deletedStudents = entityManager.createQuery(deleteStudentsJPQL).executeUpdate();
            int deletedDorms = entityManager.createQuery(deleteDormsJPQL).executeUpdate();
            entityManager.getTransaction().commit();

            // System.out.println("Clean Tables: Students=" + deletedStudents);
            // System.out.println("Clean Tables: Dorms=" + deletedDorms);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}