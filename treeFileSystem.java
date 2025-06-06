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
    public String content;

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
            this.content = "";
        }

    }

}

class FileSystem {
    private FileNode root;
    private FileNode current;
    public List<String> currentPath = new ArrayList<>();
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

        currentPath.add("/");

    }

    public String getCurrentPath(){
        String pathToShow = "";
        for(String path : currentPath){
            pathToShow += path;
        }

        return pathToShow;
    }

    public boolean isCreationAllow(String filename, boolean isFile){
        for(FileNode file : current.child){
            if(file.name.equals(filename)){
                if(file.isFile == isFile){
                    System.out.print(isFile ? "[File] - " : "[Folder] - ");
                    System.out.println(filename + " already exists!");
                    return false;
                }else{
                    return true;
                }
            }
        }
        

        return true;
    }

    public void write(String filename) {
        for (FileNode file : current.child) {
            if (file.isFile && file.name.equals(filename)) {
                Scanner sc = new Scanner(System.in);
                boolean isContinue = true;
                StringBuilder sb = new StringBuilder();
                String userInputContent;
                while (isContinue) {
                    userInputContent = sc.nextLine();
                    if (userInputContent.equals(":wq")) {
                        isContinue = false;
                        break;
                    }

                    sb.append(userInputContent).append(System.lineSeparator());
                }

                file.content = sb.toString();
                System.out.println("Content saved to " + file.name);
                return;
            }

        }

        System.out.println("File " + filename + " not found!");
    }

    public void cat(String filename) {
        for (FileNode file : current.child) {
            if (file.isFile && file.name.equals(filename)) {
                System.out.println(file.content);
                return;
            }
        }

        System.out.println("File " + filename + " not found!");
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

    public void rm(String name, boolean isFile) {
        for(FileNode file : current.child){
            if(file.name.equals(name) && file.isFile == isFile){
                current.child.remove(file);
                System.out.println(file.name + " is removed!");
                save();
                return;
            }
        }

        System.out.println("Filename: " + name + " not found! Delete failed!");
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
        if(isCreationAllow(name, false)){
            current.child.add(new FileNode(name, false, current));
            save();
        }
        
    }

    public void touch(String name) {
        if(isCreationAllow(name, true)){
            current.child.add(new FileNode(name, true, current));
            save();
        }
        
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
                currentPath.removeLast();
                current = current.parent;
            }
            
            return;
        }

        for (FileNode files : current.child) {
            if (files.name.equals(name) && !files.isFile) {
                current = files;
                currentPath.add(current.name + "/");
                return;
            }

        }

        System.out.println("Folder Not Found!");

    }

    public List<String> searchInCurrentDir(String name){
        List<String> founds = new ArrayList<>();
        for(FileNode file : current.child){
            if(file.name.equals(name)){
                String prefix = file.isFile ? "[File] - " : "[Folder] - ";
                founds.add(prefix + file.name);
            }
        }

        return founds;
    }

}

public class treeFileSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Simple File System!");
        FileSystem fs = new FileSystem();
        boolean isContinue = true;
        while (isContinue) {
            System.out.print(fs.getCurrentPath() + " >> ");
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
                case "write":
                    if (command.length > 1) {
                        fs.write(command[1]);
                        fs.save();
                    } else {
                        System.out.println("Usage: writer <filename>");
                    }
                    break;
                case "cat":
                    if (command.length > 1) {
                        fs.cat(command[1]);
                    } else {
                        System.out.println("Usage: cat <filename>");
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
                case "searchHere":
                    if(command.length > 1){
                        List<String> searchResult = fs.searchInCurrentDir(command[1]);
                        if(searchResult.size() > 0){
                            System.out.println("Result found: " + searchResult.size());
                            System.out.println(searchResult);
                        }else{
                            System.out.println(command[1] + " not found in this dir!");
                        }
                    }else{
                        System.out.println("Usage: searchHere <name>");
                    }
                    break;
                case "rm":
                    if(command.length > 2){
                        fs.rm(command[1], Boolean.parseBoolean(command[2]));
                    }else{
                        System.out.println("Usage: rm <name> <boolean: isFile?>");
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
