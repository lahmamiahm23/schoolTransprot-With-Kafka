package com.example.transport.Repositories;

import com.example.transport.entitie.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByParentId(Long parentId);

    Optional<Student> findFirstByParentId(Long parentId);

    List<Student> findByVehicleId(Long vehicleId);

    int countByVehicleId(Long vehicleId);
}
