package com.example.transport.Repositories;



import com.example.transport.entitie.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    // Trouver un parent par le nom de son enfant pour faciliter les recherches admin
    Optional<Parent> findByStudentsFirstName(String firstName);

    // Utile pour l'authentification mobile
    Optional<Parent> findByEmail(String email);
}
