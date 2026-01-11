package com.example.transport.Repositories;

import com.example.transport.entitie.Penalty;
import com.example.transport.entitie.enumeration.PenaltyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findByParentId(Long parentId);
    List<Penalty> findByStatus(PenaltyStatus status);
}
