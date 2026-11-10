package midterm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;

class InvalidCanvasException extends Exception {
    public InvalidCanvasException(String canvasId, double maxArea) {
        super(String.format("Canvas %s has a shape with area larger than %.2f", canvasId, maxArea));
    }
}

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);
    }
}

class ShapesApplication{
    double maxArea;
    List<Canvas> canvases;

    public ShapesApplication(double maxArea){
        this.maxArea = maxArea;
        this.canvases = new ArrayList<>();
    }

    public void readCanvases (InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = br.lines().collect(Collectors.toList());

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try{
                canvases.add(new Canvas(line, maxArea));
            } catch (InvalidCanvasException e){
                System.out.println(e.getMessage());
            }
        }
    }

    void printCanvases(OutputStream os) {
    PrintWriter pw = new PrintWriter(os);

    canvases.stream()
            .sorted(Comparator.comparing(Canvas::getTotalArea).reversed())
            .forEach(pw::println);

    pw.flush();
    }
}

class Canvas{
    private String canvasId;
    private List<Shape> shapes;

    public Canvas(){
        this.shapes = new ArrayList<>();
        this.canvasId = "0";
    }

    public Canvas(String line, double maxArea) throws InvalidCanvasException{
        String[] parts = line.split("\\s+");
        this.shapes = new ArrayList<>();
        this.canvasId = parts[0];
        for (int i = 1; i<parts.length; i++){
            String type = parts[i++];
            double len = Double.parseDouble(parts[i]);

            Shape s = type.equals("S") ? new Square(type, len) : new Circle(type, len);

            if (s.getArea() > maxArea){
                throw new InvalidCanvasException(canvasId, maxArea);
            }

            shapes.add(s);
        }
    }

    public double getTotalArea() {
    return shapes.stream().mapToDouble(Shape::getArea).sum();
}

private long countType(String t) {
    return shapes.stream().filter(s -> s.type.equals(t)).count();
}

@Override
public String toString() {

    DoubleSummaryStatistics stats = shapes.stream()
            .mapToDouble(Shape::getArea)
            .summaryStatistics();

    return String.format("%s %d %d %d %.2f %.2f %.2f",
            canvasId,
            shapes.size(),
            countType("C"),
            countType("S"),
            stats.getMin(),
            stats.getMax(),
            stats.getAverage());
    }
}

abstract class Shape{
    protected String type;
    protected double len;
    
    public Shape(String type, double len){

        this.type = type;
        this.len = len;
    }

    public abstract double getArea();
}

class Square extends Shape{
    public Square(String type, double len){
        super(type, len);
    }

    @Override
    public double getArea() {
        return len * len;
    }
}

class Circle extends Shape{
    public Circle(String type, double len){
        super(type, len);
    }

    @Override
    public double getArea() {
        return len * len * Math.PI;
    }
}