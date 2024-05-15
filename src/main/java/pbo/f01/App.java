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

    //agar bisa berinteraksi dengan database h2 pda praktikum ini karena menggunakan JPA
    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static void main(String[] _args) {
        factory = Persistence.createEntityManagerFactory("dormy_pu");
        entityManager = factory.createEntityManager();

        Scanner input = new Scanner(System.in);
        cleanTables();

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
        // Memulai transaksi database
        entityManager.getTransaction().begin();
        try {
            // Membuat objek Dorm baru dengan parameter nama, jenis kelamin, dan kapasitas dari input
            Dorm dorm = new Dorm(tokens[1], tokens[2], tokens[3]);
            // Menyimpan objek Dorm ke dalam database
            entityManager.persist(dorm);
            // Mengonfirmasi transaksi (commit)
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Jika terjadi kesalahan, batalkan transaksi (rollback)
            entityManager.getTransaction().rollback();
            // Cetak detail kesalahan ke konsol
            e.printStackTrace();
        }
    }

    private static void addStudent(String[] tokens) {
        // Memulai transaksi database
        entityManager.getTransaction().begin();
        try {
            // Memeriksa apakah mahasiswa sudah ada di database
            List<Student> existingStudents = entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.nim = :nim AND s.std_nama = :std_nama AND s.year = :year AND s.gender = :gender", Student.class)
                .setParameter("nim", tokens[1]) // Menyetel parameter nim
                .setParameter("std_nama", tokens[2]) // Menyetel parameter nama mahasiswa
                .setParameter("year", Integer.parseInt(tokens[3])) // Menyetel parameter tahun
                .setParameter("gender", tokens[4]) // Menyetel parameter gender
                .getResultList();
    
            // Jika mahasiswa tidak ada, tambahkan ke database
            if (existingStudents.isEmpty()) {
                Student student = new Student();
                student.setNim(tokens[1]); // Menyetel nim mahasiswa
                student.setStd_Nama(tokens[2]); // Menyetel nama mahasiswa
                student.setYear(Integer.parseInt(tokens[3])); // Menyetel tahun masuk mahasiswa
                student.setGender(tokens[4]); // Menyetel gender mahasiswa
                entityManager.persist(student); // Menyimpan objek mahasiswa ke database
            }
    
            // Mengonfirmasi transaksi
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Jika terjadi kesalahan, batalkan transaksi
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    

    private static void assignStudent(String[] tokens) {
        // Memulai transaksi database
        entityManager.getTransaction().begin();
        try {
            // Mencari mahasiswa berdasarkan nim
            Student student = entityManager.find(Student.class, tokens[1]);
            // Mencari asrama berdasarkan nama asrama
            Dorm dorm = entityManager.find(Dorm.class, tokens[2]);
    
            // Memeriksa apakah mahasiswa atau asrama tidak ditemukan
            if (student == null || dorm == null) {
                entityManager.getTransaction().rollback(); // Membatalkan transaksi
                return; // Keluar dari metode
            }
    
            // Memeriksa apakah asrama penuh atau jenis kelamin mahasiswa tidak sesuai
            if (dorm.getStudents().size() >= Integer.parseInt(dorm.getCapacity()) || !student.getGender().equals(dorm.getGender())) {
                entityManager.getTransaction().rollback(); // Membatalkan transaksi
                return; // Keluar dari metode
            }
    
            // Menambahkan mahasiswa ke dalam daftar penghuni asrama
            dorm.getStudents().add(student);
            // Menambahkan asrama ke dalam daftar asrama mahasiswa
            student.getDorms().add(dorm);
            // Mengonfirmasi transaksi
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Jika terjadi kesalahan, batalkan transaksi
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    

    private static void displayAll() {
        // Membuat query untuk mengambil semua asrama dan mengurutkan berdasarkan nama asrama
        TypedQuery<Dorm> query = entityManager.createQuery("SELECT d FROM Dorm d ORDER BY d.dorm_nama ASC", Dorm.class);
        // Menyimpan hasil query ke dalam daftar asrama
        List<Dorm> dorms = query.getResultList();
    
        // Iterasi melalui setiap asrama
        for (Dorm dorm : dorms) {
            // Mencetak informasi asrama
            System.out.println(dorm.getNama() + "|" + dorm.getGender() + "|" + dorm.getCapacity() + "|" + dorm.getStudents().size());
            // Mengurutkan mahasiswa di asrama berdasarkan nama
            List<Student> sortedStudents = dorm.getStudents().stream()
                .sorted(Comparator.comparing(Student::getStd_Nama))
                .collect(Collectors.toList());
            // Iterasi melalui setiap mahasiswa di asrama
            for (Student student : sortedStudents) {
                // Mencetak informasi mahasiswa
                System.out.println(student.getNim() + "|" + student.getStd_Nama() + "|" + student.getYear());
            }
        }
    }
    

    private static void cleanTables(){
        // Query untuk menghapus semua data mahasiswa
        String deleteStudentJpql = "DELETE FROM Student c";
        // Query untuk menghapus semua data asrama
        String deleteDormJpql = "DELETE FROM Dorm g";
    
        // Memulai transaksi database
        entityManager.getTransaction().begin();
        // Menjalankan query penghapusan data mahasiswa
        entityManager.createQuery(deleteStudentJpql).executeUpdate();
        // Menjalankan query penghapusan data asrama
        entityManager.createQuery(deleteDormJpql).executeUpdate();
        entityManager.flush(); // Membersihkan cache persistence context
        // Mengonfirmasi transaksi
        entityManager.getTransaction().commit();
    }
}