package edu.eci.arsw.blueprints.persistence;


import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int y;

    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private BlueprintEntity blueprint;

    // Getters
    public Long getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BlueprintEntity getBlueprint() {
        return blueprint;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setBlueprint(BlueprintEntity entity) {
        this.blueprint = entity;
    }
}