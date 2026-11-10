package midterm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class SubtitlesTest {
	public static void main(String[] args) {
		Subtitles subtitles = new Subtitles();
		int n = subtitles.loadSubtitles(System.in);
		System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
		subtitles.print();
		int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
		System.out.println(String.format("SHIFT FOR %d ms", shift));
		subtitles.shift(shift);
		System.out.println("+++++ SHIFTED SUBTITLES +++++");
		subtitles.print();
	}
}

// Вашиот код овде

class Subtitles{
	private ArrayList<Subtitle> subtitles_list;
	private static int count = 0;

    public Subtitles(){
		this.subtitles_list = new ArrayList<Subtitle>();
	}

	public int loadSubtitles(InputStream is){
		Scanner sc = new Scanner(is);
		while(sc.hasNextInt()){
			int id = sc.nextInt();
			sc.nextLine();
			String time = sc.nextLine();

			StringBuilder contentBuilder = new StringBuilder();
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				if (line.trim().isEmpty()) {
					break;
				}
				if (contentBuilder.length()>0){
					contentBuilder.append("\n");
				}
				contentBuilder.append(line);
			}
			subtitles_list.add(new Subtitle(id, time, contentBuilder.toString()));
			
			count++;
		}
		return count;
	}

	public void print(){
		for (Subtitle sub : subtitles_list){
			sub.print();
		}
	}
	public void shift(int shift){
		for (Subtitle sub : subtitles_list){
			sub.shift(shift);
		}
	}
}

class Subtitle{
	private int order_number;
	private String content;
	private int start;
	private int end;

	public void shift(int shift){
		this.start += shift;
		this.end += shift;
	}

	public Subtitle(int id, String time, String cont){
		this.order_number = id;
		this.content = cont;

		String start_time = time.split(" --> ")[0];
		String end_time = time.split(" --> ")[1];
		String hours = start_time.split(":")[0];
		String mins = start_time.split(":")[1];
		String secus = start_time.split(":")[2];
		String sec = secus.split(",")[0];
		String milis = secus.split(",")[1];

		this.start = Integer.parseInt(milis) + Integer.parseInt(sec)*1000 + Integer.parseInt(mins)*60*1000 + Integer.parseInt(hours)*60*60*1000;

		hours = end_time.split(":")[0];
		mins = end_time.split(":")[1];
		secus = end_time.split(":")[2];
		sec = secus.split(",")[0];
		milis = secus.split(",")[1];

		this.end = Integer.parseInt(milis) + Integer.parseInt(sec)*1000 + Integer.parseInt(mins)*60*1000 + Integer.parseInt(hours)*60*60*1000;
	}

	public void print(){
		System.out.println(order_number);
		int hours = start / 3600000;
		int minuts = (start % 3600000) / 60000;
		int second = (start % 60000) / 1000;
		int milis = start % 1000;

		String start_time = String.format("%02d:%02d:%02d,%03d", hours, minuts, second, milis);

		hours = end / 3600000;
		minuts = (end % 3600000) / 60000;
		second = (end % 60000) / 1000;
		milis = end % 1000;

		String end_time = String.format("%02d:%02d:%02d,%03d", hours, minuts, second, milis);

		String formatted = String.format("%s --> %s", start_time, end_time);
		System.out.println(formatted);
		System.out.println(content);
		System.out.println();
	}

}