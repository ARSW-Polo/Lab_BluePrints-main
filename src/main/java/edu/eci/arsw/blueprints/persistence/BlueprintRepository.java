package edu.eci.arsw.blueprints.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlueprintRepository extends JpaRepository<BlueprintEntity, Long> {

    Optional<BlueprintEntity> findByAuthorAndName(String author, String name);

    List<BlueprintEntity> findByAuthor(String author);
}