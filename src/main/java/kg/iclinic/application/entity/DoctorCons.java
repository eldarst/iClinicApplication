package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Doctors_Cons")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class DoctorCons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "Name", nullable = false)
    @NonNull private String name;

    @Column(name = "First_Cons", nullable = false)
    @NonNull private int firstCons;

    @Column(name = "Rep_Cons", nullable = false)
    @NonNull private int repCons;

    @Column(name = "For_Clinic", nullable = false)
    @NonNull private int forClinic;

    @Override
    public String toString() {
        return  name;
    }
}
