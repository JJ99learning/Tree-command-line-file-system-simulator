# Tree Command-Line File System Simulator

A simple command-line file system simulator implemented in Java using a tree data structure. This program supports basic file system operations such as creating folders and files, navigating directories, listing contents, and saving/restoring the file system state between sessions using JSON serialization.

---

## Features

- Create directories (`mkdir <foldername>`)
- Create files (`touch <filename>`)
- List contents of current directory (`ls`)
- Change directory (`cd <foldername>` or `cd ..` to go up)
- Save the file system structure to a JSON file on exit
- Load file system structure from JSON file on start (persistent data)
- Simple and intuitive command-line interface

---

## How It Works

- Internally uses a **tree data structure** (`FileNode`) to represent directories and files.
- Each node stores its name, whether it is a file or folder, and its children (for folders).
- The `FileSystem` class manages the current directory and provides commands.
- Uses **Jackson** JSON library to save and load the file system structure to `filesystem.json`.

---

## Prerequisites

- Java JDK 8 or higher
- [Jackson Databind](https://github.com/FasterXML/jackson-databind) libraries added to your classpath (for JSON serialization):

  - jackson-core-x.x.x.jar
  - jackson-databind-x.x.x.jar
  - jackson-annotations-x.x.x.jar

---

## How to Compile and Run

1. **Compile the code** (make sure your `libs/` folder contains Jackson JARs):

   ```bash
   javac -cp "libs/*" -d bin MasterLearning/treeFileSystem.java
   ```

2. **Run the code**

   _Window_

   ```bash
   java -cp "libs/*;bin" MasterLearning.treeFileSystem
   ```

   _Mac/Linux_

   ```bash
   java -cp "libs/*:bin" MasterLearning.treeFileSystem
   ```

## 🚧 Future Improvements

The following features and fixes are planned for the next stage of development:

### ✅ Bug Fixes

- [x] Fixed incorrect `return` logic in the `cd()` method to properly loop through all folder names before deciding if the folder is not found.

### 🔧 Functional Enhancements

- [ ] **Save to Real .txt Files**  
      Each file's content will be written to a real `.txt` file upon saving, simulating a real filesystem environment more closely.

- [x] **Improve `cat()` Feedback**  
      Add error output if a requested file does not exist (e.g., `File <name> not found!`) to improve user experience and feedback.

- [x] **Prevent Duplicate File/Folder Names**  
      Add validation in both `mkdir` and `touch` to prevent creating multiple files or folders with the same name under the same directory.

- [x] **Display Current Path in Prompt**  
      Show the full path (e.g., `/`, `/folder1/subfolder/`) in the shell prompt to give users clear context of their current directory.

- [x] **Add `rm` to remove folder/file**  
      Enable user to remove unwanted folder and file.

- [x] **Add `search` to search sepcific folder or file in current dir**  
      Enable user to search for folder or file in current dir.
---

These improvements aim to enhance the realism, usability, and clarity of the CLI-based file system.
