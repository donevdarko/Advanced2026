package midterm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class FileNameExistsException extends Exception{
    public FileNameExistsException(IFile file, Folder folder){
        super(String.format("There is already a file named %s in the folder %s", file.getFileName(), folder.getFileName()));
    }
}

interface IFile{

    String getFileName();

    long getFileSize();
    String getFileInfo(int indent);
    void sortBySize();
    long findLargestFile();

}

class File implements IFile{
    private String name;
    private long size;

    public File(String name, long size) {
        this.size = size;
        this.name = name;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int indent) {
        return String.format("%sFile name: %10s File size: %10d\n", "\t".repeat(indent), name, size);
    }


    @Override
    public void sortBySize() {

    }

    @Override
    public long findLargestFile() {
        return size;
    }
}

class Folder implements IFile{
    private String name;
    private long size;
    List<IFile> files;

    public Folder(String name) {
        this.name = name;
        this.size = 0;
        this.files = new ArrayList<>();
    }

    void addFile (IFile file) throws FileNameExistsException {
        if (!files.isEmpty()){
            for (IFile s : files) if (s.getFileName().equals(file.getFileName())) throw new FileNameExistsException(file, this);
        }
        files.add(file);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        size = 0;
        for (IFile fs : files){
            size+=fs.getFileSize();
        }
        return size;
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name: %10s Folder size: %10d\n", "\t".repeat(indent), name, getFileSize()));
        for (IFile f : files) sb.append(f.getFileInfo(indent + 1));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files.sort(Comparator.comparingLong(IFile::getFileSize));
        for (IFile f : files) f.sortBySize();
    }

    @Override
    public long findLargestFile() {
        long largest = 0;
        if (files.isEmpty()) return 0;
        for (IFile fs : files) largest = Math.max(largest, fs.findLargestFile());
        return largest;
    }
}

class FileSystem{
    Folder root;

    public FileSystem() {
        this.root = new Folder("root");
    }

    void addFile (IFile file) throws FileNameExistsException{
        root.addFile(file);
    }

    long findLargestFile(){
        return root.findLargestFile();
    }

    void sortBySize(){
        root.sortBySize();
    }

    @Override
    public String toString() {
        return root.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());




    }
}