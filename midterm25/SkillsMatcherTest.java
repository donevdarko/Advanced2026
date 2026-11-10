package midterm25;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class SkillsMatcherTest {

    public static void wtf(Scanner in) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("input.txt"));
        while (in.hasNextLine()) {
            pw.println(in.nextLine());
        }
        pw.flush();
        pw.close();
        in.close();
    }

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        
        List<String> required = Arrays.stream(sc.nextLine().split(";"))
                .collect(Collectors.toList());

        
        wtf(sc);

        SkillsMatcher matcher = new SkillsMatcher(required);

        
        matcher.evaluateCandidates(new FileInputStream("input.txt"), System.out);
    }
}

class SkillsMatcher{
    private List<String> reqSkills;

    public SkillsMatcher(List<String> requiredSkills){
        this.reqSkills = new ArrayList<>(requiredSkills);
    }

    public void evaluateCandidates(InputStream is, OutputStream os){
        List<Candiate> cands = new ArrayList<>();
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] lineParts = line.trim().split("\\s+");
            String id = lineParts[0];
            List<String> skills = new ArrayList<>();
            for (int i = 1; i<lineParts.length; i++){
                skills.add(lineParts[i]);
            }
            cands.add(new Candiate(id, skills));
            cands.get(cands.size()-1).checkReqs(reqSkills);
        }

        PrintWriter pw = new PrintWriter(os);

        cands.stream()
            .sorted(Collections.reverseOrder())
            .forEach(pw::println);

        pw.flush();
            

    }
}

class Candiate implements Comparable<Candiate>{
    private String candId;
    private List<String> skills;
    private int reqs;

    public Candiate(String candId, List<String> skills){
        this.candId = candId;
        this.skills = skills;
    }

    public void checkReqs(List<String> reqSkills){
        int counter = 0;
        for(int i = 0; i<reqSkills.size(); i++){
            if (skills.contains(reqSkills.get(i))) counter ++;
        }
        this.reqs = counter;
    }

    public int getReqs(){
        return reqs;
    }

    @Override
    public int compareTo(Candiate arg0) {
        return Integer.compare(reqs, arg0.getReqs());
    }

    @Override 
    public String toString(){
        String formated = String.format("%s %d", candId, reqs);
        return formated;
    }

}