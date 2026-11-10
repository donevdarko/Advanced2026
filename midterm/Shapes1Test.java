package midterm;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;

class Element{
    private String canvas_id;
    private ArrayList<Integer> sizes;
    private int totalPerimeter;

    public Element(String canvas_id, ArrayList<Integer> sizes){
        this.canvas_id = canvas_id;
        this.sizes = sizes;
        this.totalPerimeter = lilKodak();
    }

    public int lilKodak(){
        int total = 0;
        for (Integer side : sizes){
            total += side*4;
        }
        return total;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String formatted = String.format("%s %d %d", canvas_id, sizes.size(), totalPerimeter);
        return formatted;
    }
}

class ShapesApplication{
    private ArrayList<Element> elements;
    static int count = 0;

    public ShapesApplication(){
        elements = new ArrayList<>();
    }

    // public int readCanvases (InputStream inputStream){
    //     Scanner sc = new Scanner(inputStream);
    //     while(sc.hasNext()){
    //         String canvas_id = sc.next();
    //         ArrayList<Integer> sizes = new ArrayList<>();
    //         while(sc.hasNextInt()){
    //             int size = sc.nextInt();
    //             sizes.add(size);
    //             count++;
    //         }
    //         elements.add(new Element(canvas_id, sizes));
    //     }
    //     return count;
    // }

    public int readCanvases (InputStream inputStream){
        Scanner sc = new Scanner(inputStream);
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String canvas_id = line.split(" ")[0];
            ArrayList<Integer> sizes = new ArrayList<>();
            for(int i = 1; i<line.split(" ").length; i++){
                int size = Integer.parseInt(line.split(" ")[i]);
                sizes.add(size);
                count++;
            }
            elements.add(new Element(canvas_id, sizes));
        }
        return count;
    }



    public void printLargestCanvasTo (OutputStream outputStream){
        Element best = elements.get(0);
        for (Element kodak : elements){
            if (kodak.lilKodak() > best.lilKodak()){
                best = kodak;
            }
        }
        PrintWriter pw = new PrintWriter(outputStream);
        pw.println(best);
        pw.flush();
    }
}

public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}