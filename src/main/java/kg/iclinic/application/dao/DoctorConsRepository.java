package kg.iclinic.application.dao;

import kg.iclinic.application.entity.Doctor;
import kg.iclinic.application.entity.DoctorCons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorConsRepository extends JpaRepository<DoctorCons, Long> {
    Doctor findByName(String name);
    boolean existsByName(String name);
}
