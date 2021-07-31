package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Doctors")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Doctor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "Name", nullable = false)
    @NonNull private String name;

    @Override
    public String toString() {
        return  name;
    }

}
