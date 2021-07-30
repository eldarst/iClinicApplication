package kg.iclinic.application.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Doctors")
@Data
public class Doctor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Override
    public String toString() {
        return  name;
    }
}
