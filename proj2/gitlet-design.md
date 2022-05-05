# Gitlet Design Document

**Name**: Jonathan Dee

## Some Important Guideline

Make sure all pointers are represented by hash code `Map`. (Because of serialization)

- `pointerMap`: master -> commit (hash code)
- `Commits`: commit parent -> last commit (hash code)
- `Commits`: FileName -> blob (hash code)

## Question to Solve

Which method should write in repository class? And which method should write in commit class? How could I make the logic easier to decide which to which?

## Persistence

> Todo: Define what the repository actually look like.

```text
CWD                             <==== Whatever the current working directory is.
└── .gitlet                     <==== All persistant data is stored within here
    ├── Status Area (contains a single file named statusLog)
    |   └── statusLog                   
    |       ├── pointerMap(master/head)               
    |       ├── Staged for addition                
    |       └── Staged for removal
    ├── Commits                   
    |   ├── commit0 (named with SHA-1)               
    |   └── commit1
    └── Blobs                    
        ├── blob1 (named with SHA-1)               
        ├── bolb2 
        ├── ...
        └── blobN
```

The `Repository` class will set up all persistence.

The `Commit` class will handle serialization of `Commit` object.

The `StatusLog` class tracks all status of gitlet repository

- Branches (by tracking `pointerMap` area)
- Staged Files (by tracking `Staged for addition` area)
- Removed Files (by tracking `Staged for removal` area)
- Modifications Not Staged For commit (?)
- Untracked Files (?)

All class file should be named by its hash code except `statusLog`:

- under `Status Area` directory there should be only one file named `statusLog`
- under `Commits` directory there should be several files named by its hash code, and each one represents a single commit
- under `Blobs` directory there should be several files named by its hash code, and each one represents a version of a saved file.

---

## Classes and Data Structures

> Todo: Figure out:
>
> - which class each method (command) belongs to
> - which directory the class store in
> - what data is stored in a class.

---

### Main

Driver class for Gitlet, a subset of the Git version-control system.

#### Fields

No instance variable

#### Methods

1. `public static void validateNumArgs(String cmd, String[] args, int n)`: Checks the number of arguments versus the expected number, throws a RuntimeException if they do not match.

---

### Commit

Represents a gitlet commit object. The `Commit` class will handle serialization of `Commit` object.

What a commit contain:

- hash code
- timestamp (Date)
- commit message
- another parent's hash code (if merge happened)

Pointer which track some important position

> Can I use a `TreeMap` to track the branch name and the commit it point to?

- Master
- Head
- other branches
- ......

#### Fields (Instance Variables)

> maybe set all instance variables to private and write get methods, and write a good constructor

1. message
2. timestamp
3. `filesMap`: a `TreeMap` which track files and their related blobs (represent with SHA-1 hash code).
4. parent commit (as hash code)

#### Methods

- public void saveCommit();

---

### Repository

The `Repository` class will set up all persistence.

#### Fields

1. pointersMap
2. Field 2

#### Methods

- public void init();
  - setup `.gitlet` repository
  - create `initial commit`
  - create `master` and `HEAD` pointer
  - fail when `.gitlet` already exists, error with `A Gitlet version-control system already exists in the current directory.` message.
- private void setupPersistence();
  

---

### statusLog

The `statusLog` class tracks all status of gitlet repository

#### Fields

1. pointersMap
2. Field 2

#### Methods

- public void saveStatus();
- public void setPointer(String pointer, String SHA1);
  - set pointer point to a file named by its hash code.

---

## Algorithms

>Todo: Try to write some useful helper methods, and put them here, or write explanation for methods above
---

## Order of Completion

> Todo: The step by step guide to finish this complicated task.

Base on the features we we want to support, we can divide our one big task to several levels of functionalities that we want to implement:

- basic feature:
  - init a repository
  - add a file
  - make a commit
  - check gitlet log to see a new commit
  - extra: make gitlet status to show the right message
    - just make sure the `Staged Files` part do the right thing, since there are only `add` and `commit` command change the repository's status.
    - maybe just read the content in `staged for addtion` to show `Staged Files` part? (and the same for `Staged for removal`) 

---

## Individual Method Analysis

### gitlet.Main init

Create a `.gitlet` repository to save copies of files.

Three main parts inside Gitlet repository: `Status Area`, `Commits` and `Blobs`.

The content of files is stored in `Blobs`.

### gitlet.Main add

What `add` command really do is register a file to `Staged for addition`, and create a blob to store the content of the file. Then write in `Staged for addition` that "Next commit should save a different verison of the file, and the content is in the blob I just created"

Edge case:

- add new file that not been changed
  - check the file's content (hash code), if it's the same as last commit, don't add it to `Staging Area`

### gitlet.Main commit

1. clone the parent commit (read the file with the parent commit hash code).
2. change the commit message and timestamp.
3. add files in `Staged for addition` area, then link them to their blobs. (after that, reset `Staged for additon` area)
    - if a file is already tracked, then just change the link.
4. define the parent of the new commit.
5. move the current branch pointer and the Head pointer to the new created commit.
