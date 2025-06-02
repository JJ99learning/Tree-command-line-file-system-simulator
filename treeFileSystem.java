// Run the program using "java -cp "libs/*;bin" MasterLearning.treeFileSystem"
package MasterLearning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.io.*;

import javax.security.sasl.SaslServer;

@JsonInclude(JsonInclude.Include.NON_NULL)
class FileNode {
    public String name;
    public boolean isFile;

    @JsonIgnore // Prevent infinite recursion when saving
    public FileNode parent;
    public List<FileNode> child;

    public FileNode() {
    }

    public FileNode(String name, boolean isFile, FileNode parent) {
        this.name = name;
        this.isFile = isFile;
        this.parent = parent;

        if (!isFile) {
            this.child = new ArrayList<>();
        }

    }

}

class FileSystem {
    private FileNode root;
    private FileNode current;

    private static final String SAVE_FILE = "filesystem.json";

    FileSystem() {
        File file = new File(SAVE_FILE);

        if (file.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                root = mapper.readValue(file, FileNode.class);
                setParents(root, null);
                current = root;
                System.out.println("File system loaded!");
            } catch (Exception e) {
                System.out.println("Failed to load file system...");
                root = new FileNode("/", false, null);
                current = root;
            }
        } else {
            root = new FileNode("/", false, null);
            current = root;
        }

    }

    public void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SAVE_FILE), root);

            System.out.println("File system saved!");
        } catch (Exception e) {
            System.out.println("Failed to save file system...");
        }
    }

    private void setParents(FileNode node, FileNode parent) {
        node.parent = parent;
        if (node.child != null) {
            for (FileNode childNode : node.child) {
                setParents(childNode, node);
            }
        }
    }

    public void mkdir(String name) {
        current.child.add(new FileNode(name, false, current));
    }

    public void touch(String name) {
        current.child.add(new FileNode(name, true, current));
    }

    public void ls() {
        for (FileNode files : current.child) {
            if (files.isFile) {
                System.out.print("[File] - " + files.name + " ");
            } else {
                System.out.print("[Folder] - " + files.name + " ");
            }
        }
        System.out.println();
    }

    public void cd(String name) {
        if (name.equals("..")) {
            if (current.parent != null) {
                current = current.parent;
            }

            return;
        }

        for (FileNode files : current.child) {
            if (files.name.equals(name) && !files.isFile) {
                current = files;
            }

            return;
        }

        System.out.println("Folder Not Found!");

    }

}

public class treeFileSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Simple File System!");
        FileSystem fs = new FileSystem();
        boolean isContinue = true;
        while (isContinue) {
            System.out.print(">> ");
            String[] command = sc.nextLine().split(" ");
            switch (command[0]) {
                case "mkdir":
                    if (command.length > 1) {
                        fs.mkdir(command[1]);
                    } else {
                        System.out.println("Usage: mkdir <foldername>");
                    }
                    break;
                case "touch":
                    if (command.length > 1) {
                        fs.touch(command[1]);
                    } else {
                        System.out.println("Usage: touch <filename>");
                    }
                    break;
                case "ls":
                    fs.ls();
                    break;
                case "cd":
                    if (command.length > 1) {
                        fs.cd(command[1]);
                    } else {
                        System.out.println("Usage: cd <foldername>");
                    }
                    break;
                case "exit":
                    fs.save();
                    System.out.println("Exiting...");
                    sc.close();
                    isContinue = false;
                    break;
                default:
                    System.out.println("Command not found!");
            }

        }

    }
}
