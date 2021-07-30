package kg.iclinic.application.dao;

import kg.iclinic.application.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByName(String name);
    boolean existsByName(String name);
}
