package edu.eci.arsw.blueprints.model;

public record Point(int x, int y) {
    public int getX() {
        return  x;
    }
    public int getY(){
        return y;
    }
}