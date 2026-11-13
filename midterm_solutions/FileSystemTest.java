package midterm_solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class FileNameExistsException extends Exception {
    public FileNameExistsException(String fileName, String folderName) {
        super(String.format("There is already a file named %s in the folder %s", fileName, folderName));
    }
}

interface IFile {
    String getFileName();

    long getFileSize();

    String getFileInfo(int indent);   // the "???" — how deep we are, so we know how many tabs to print

    void sortBySize();

    long findLargestFile();
}

class File implements IFile {
    private final String name;
    private final long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
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
        return String.format("%sFile name: %10s File size: %10d\n", "    ".repeat(indent), name, size);
    }

    @Override
    public void sortBySize() {
        // a plain file has nothing to sort
    }

    @Override
    public long findLargestFile() {
        return size;
    }
}

class Folder implements IFile {
    private final String name;
    private final List<IFile> files;

    public Folder(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    public void addFile(IFile file) throws FileNameExistsException {
        for (IFile f : files) {
            if (f.getFileName().equals(file.getFileName())) {
                throw new FileNameExistsException(file.getFileName(), name);
            }
        }
        files.add(file);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        long total = 0;
        for (IFile f : files) total += f.getFileSize();   // recursive for subfolders
        return total;
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name: %10s Folder size: %10d\n", "    ".repeat(indent), name, getFileSize()));
        for (IFile f : files) sb.append(f.getFileInfo(indent + 1));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files.sort(Comparator.comparingLong(IFile::getFileSize));
        for (IFile f : files) f.sortBySize();   // sort every subfolder too
    }

    @Override
    public long findLargestFile() {
        long largest = 0;
        for (IFile f : files) largest = Math.max(largest, f.findLargestFile());
        return largest;
    }
}

class FileSystem {
    private final Folder rootDirectory;

    public FileSystem() {
        rootDirectory = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        rootDirectory.addFile(file);
    }

    public long findLargestFile() {
        return rootDirectory.findLargestFile();
    }

    public void sortBySize() {
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        //file reading from input

        Scanner sc = new Scanner(System.in);

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
