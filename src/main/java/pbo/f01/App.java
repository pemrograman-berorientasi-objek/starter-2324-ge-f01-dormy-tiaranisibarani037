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
            Student student = new Student(tokens[1], tokens[2], Integer.parseInt(tokens[3]), tokens[4]);
            entityManager.persist(student);
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

            if (dorm.getStudents().size() >= Integer.parseInt(dorm.getCapacity())) {
                System.out.println("Dorm " + tokens[2] + " sudah penuh.");
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
        TypedQuery<Dorm> query = entityManager.createQuery("SELECT d FROM Dorm d ORDER BY d.dorm_nama", Dorm.class);
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

            System.out.println("Clean Tables: Students=" + deletedStudents);
            System.out.println("Clean Tables: Dorms=" + deletedDorms);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
