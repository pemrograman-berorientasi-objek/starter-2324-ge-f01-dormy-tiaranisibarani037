package pbo.f01.model;

import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import java.util.Set;

@Entity
@Table(name = "students")
public class Student{

    @Id
    @Column(name = "nim", length = 225, nullable = false)
    private String nim;

    @Column(name = "name", length = 225, nullable = false)
    private String std_nama;

    @Column(name = "angkatan", nullable = false)
    private Integer year;

    @Column(name = "gender", length = 225, nullable = false)
    private String gender;

    @ManyToMany(targetEntity = Dorm.class, cascade = CascadeType.ALL)
    @JoinTable(name = "student_dorms", joinColumns = @JoinColumn(name = "dorm_nim", referencedColumnName = "nim"), 
    inverseJoinColumns = @JoinColumn(name = "dorm_name", referencedColumnName = "name"))

    private Set<Dorm> dorms;

    public Student(){
        //empty
    }

    public Student(String nim_, String std_nama_, Integer year_, String gender_){
        this.nim = nim_;
        this.std_nama = std_nama_;
        this.year = year_;
        this.gender = gender_;
    }

    public Student(String nim, String std_nama, Integer year, String gender, Set<Dorm> dorms){
        this.nim = nim;
        this.std_nama = std_nama;
        this.year = year;
        this.gender = gender;
        this.dorms = dorms;
    }

    public String getNim(){
        return nim;
    }

    public String getStd_Nama(){
        return std_nama;
    }

    public int getYear(){
        return year;
    }

    public String getGender(){
        return gender;
    }

    public Set<Dorm> getDorms(){
        return dorms;
    }

    public void setNim(String nim_){
        this.nim = nim_;
    }

    public void setStd_Nama(String std_nama_){
        this.std_nama = std_nama_;
    }

    public void setYear(int year_){
        this.year = year_;
    }

    public void setGender(String gender_){
        this.gender = gender_;
    }

    public void setDorms(Set<Dorm> dorms){
        this.dorms = dorms;
    }
    @Override
    public String toString() {
        return  nim + "|" + std_nama + "|" + year + "|" + gender ;

    }
}