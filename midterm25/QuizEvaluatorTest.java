package midterm25;


import java.io.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class QuizAttempt implements Comparable<QuizAttempt>{
    private String index;
    private List<String> myAnswers;
    private int counter;

    public QuizAttempt(String index, List<String> myAnswers){
        this.index = index;
        this.myAnswers = new ArrayList<>(myAnswers);
        this.counter = 0;
    }

    public void checkAttempt(List<String> corrects){
        for(int i = 0; i<myAnswers.size(); i++){
            if(myAnswers.get(i).compareTo(corrects.get(i)) == 0){
                counter++;
            }
        }
    }

    public String toString(){
        String formated = String.format("%s %d", this.index, this.counter);
        return formated;
    }

    @Override
    public int compareTo(QuizAttempt arg0) {
        return Integer.compare(this.counter, arg0.counter);
    }

    
}

class QuizGroup{

    private List<QuizAttempt> attempts;

    public QuizGroup(){
        this.attempts = new ArrayList<>();
    }

    public void addAttempt(QuizAttempt at){
        attempts.add(at);
    }

    public void checkAttempts(List<String> corrects){
        for(QuizAttempt at : attempts){
            at.checkAttempt(corrects);
        }
    }

    public void printTS(OutputStream os){
        PrintWriter out = new PrintWriter(os);
        attempts.stream()
            .sorted(Collections.reverseOrder())
            .forEach(out::println);

        out.flush();
    }
}

class QuizEvaluator{
    private List<String> correctAnswers;

    public QuizEvaluator(List<String> correctAnswers){
        this.correctAnswers = new ArrayList<>(correctAnswers);
    }

    public void evaluateQuizAttempts(InputStream is, OutputStream os){
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = br.lines().collect(Collectors.toList());
        QuizGroup currentGroup = new QuizGroup();
        for (String line : lines){
            if (line.trim().isEmpty()) continue;
            try{
                String[] inputs = line.split(" ");
                String index = inputs[0];
                List<String> answers = new ArrayList<>();
                for(int i=1; i<inputs.length; i++){
                    answers.add(inputs[i]);
                }

                currentGroup.addAttempt(new QuizAttempt(index, answers));

            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }

        currentGroup.checkAttempts(correctAnswers);

        currentGroup.printTS(os);



        


        
    }
}

public class QuizEvaluatorTest {

    public static void wtf (Scanner in) throws FileNotFoundException {
        /*
        Read all lines from scanner and write them to a file input.txt
        * */
        PrintWriter pw = new PrintWriter(new FileOutputStream("input.txt"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            pw.println(line);
        }

        in.close();
        pw.flush();
    }

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        List<String> correct = Arrays.stream(sc.nextLine().split(";")).collect(Collectors.toList());

        wtf(sc);

        QuizEvaluator qe = new QuizEvaluator(correct);

        qe.evaluateQuizAttempts(new FileInputStream("input.txt"), System.out);
    }
}
