package pbo.f01.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "dorms")
public class Dorm {
    
    @Id
    @Column(name = "Name", length = 225, nullable = false)
    private String dorm_nama;

    @Column(name = "capacity", length = 225, nullable = false)
    private String capacity;

    @Column(name = "gender", length = 225, nullable = false)
    private String gender;

    @ManyToMany(mappedBy = "dorms", cascade = CascadeType.ALL)
    private Set<Student> students;

    public Dorm(){
        //
    }
    
    public Dorm(String dorm_nama_, String capacity_, String gender_){
        this.dorm_nama = dorm_nama_;
        this.capacity = capacity_;
        this.gender = gender_;
    }

    public Dorm(String dorm_nama_, String capacity_, String gender_, Set<Student> students){
        this.dorm_nama = dorm_nama_;
        this.capacity = capacity_;
        this.gender = gender_;
        this.students = students;
    }

    public String getNama(){
        return dorm_nama;
    }

    public String getCapacity(){
        return capacity;
    }

    public String getGender(){
        return gender;
    }

    public Set<Student> getStudents(){
        return students;
    }

    public void setNama(String dorm_nama_){
        this.dorm_nama = dorm_nama_;
    }

    public void setCapacity(String capacity_){
        this.capacity = capacity_;
    }

    public void setGender(String gender_){
        this.gender = gender_;
    }

    public void setStudents(Set<Student> students){
        this.students = students;
    }

    @Override
    public String toString(){
        return dorm_nama + "|" + capacity + "|" + gender;
    }

}
