package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final BlueprintRepository repository;

    public PostgresBlueprintPersistence(BlueprintRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {

        if (repository.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent()) {
            throw new BlueprintPersistenceException("Blueprint already exists");
        }

        BlueprintEntity entity = toEntity(bp);
        repository.save(entity);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        BlueprintEntity entity = repository.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException("Not found"));

        return toModel(entity);
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<BlueprintEntity> list = repository.findByAuthor(author);

        if (list.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints");
        }

        return list.stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return repository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Blueprint addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        BlueprintEntity entity = repository.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name));

        PointEntity point = new PointEntity();
        point.setX(x);
        point.setY(y);
        point.setBlueprint(entity);
        entity.getPoints().add(point);

        BlueprintEntity savedEntity = repository.save(entity);
        return toModel(savedEntity);
    }

    // --- Mappers ---
    private BlueprintEntity toEntity(Blueprint bp) {
        BlueprintEntity entity = new BlueprintEntity();
        entity.setAuthor(bp.getAuthor());
        entity.setName(bp.getName());

        List<PointEntity> points = bp.getPoints().stream().map(p -> {
            PointEntity pe = new PointEntity();
            pe.setX(p.getX());
            pe.setY(p.getY());
            pe.setBlueprint(entity);
            return pe;
        }).toList();

        entity.setPoints(points);
        return entity;
    }

    private Blueprint toModel(BlueprintEntity entity) {
        List<Point> points = entity.getPoints().stream()
                .map(p -> new Point(p.getX(), p.getY()))
                .toList();

        return new Blueprint(entity.getAuthor(), entity.getName(), points);
    }
}
