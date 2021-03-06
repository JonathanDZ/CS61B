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
└── .gitlet                     <==== All persistent data is stored within here
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
        ├── blob2 
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

### MergedCommit

Extend from Commit class. It saves a special commit node, which is generated after a merge. 

#### Fields

- String branchparent; (record its parent commit in the merged branch)

#### Methods

- gg

---

### Repository

The `Repository` class will set up all persistence.

#### Fields

1. pointersMap
2. Field 2

#### Methods

> Find a serious bug in the methods, the gitLet should track current branch, but it's not stored in statusLog, I should add a new variable currentBranch to track it.

- public void init();
  - setup `.gitlet` repository
  - create `initial commit`
  - create `master` and `HEAD` pointer
  - fail when `.gitlet` already exists, error with `A Gitlet version-control system already exists in the current directory.` message.
- private void setupPersistence();
- public static void add(String fileName);
  - check if the file exists (if not, print `File does not exist.`), if it is, remove it from `stagedForRemoval` area.
  - calculate sha1 code of the file, first check the `stagedForAddition` map to remove the older blob (if they are same, stop the process), then check the latest `filesMap` map to unstage the unchanged file (if the new blob is the same as last added blob, remove the file from `stagedForAddition`, then stop the process).
  - add the file to `stagedForAddition`, create a blob to save the content of the file.
  - edge case:
    - if a file is added to `stagedForAddition`, but later it is edited back to its original version then add it again, it should remove the file from `stagedForAddition`
    - if a file is added to `stagedForAddition`, but it is modified to another version and then add it again, it should remove the older saved blob and save the new blob.
    - if a file is added to `stagedForRemoval`, then change back to its original version, it should romve the file from `stagedForRemoval` map, or change to a new version, it should remove from `stagedForRemoval` and add to `stagedForAdditon`
- public static void commit(String commitMessage);
  - setup time & message, copy the parent commit, then link the parent and child
  - add or update the files in `stagedForAddition` area
  - remove the files in `stagedForRemoval` area
  - reset `stagedForAddition` and `stagedForRemoval` area
  - set `HEAD` pointer point to the new commit. (find a bug here!)
    - should also set current branch pointer points to new commit
  - edge cases:
    - if `stagedForAddition` and `stagedForRemoval` are both empty, print `No changes added to the commit`
- public static void rm(String fileName);
  - check the file if it is in `stagedForAddition` area, if it is, unstage the file, then return.
  - if the file is in current commit's `filesMap`, delete the file (if user has not deleted it), and stage it to `stagedForRemoval` area.
  - Otherwise, print the error message `No reason to remove the file.`
  - tips: use the `restrictedDelete` method in util to delete file's in `.gitlet` repository.
- public static void log();
  - iterate through all commits, print the commit (commit.toString()), and find its parent (commit.findParent())
  - edge cases:
    - should override mergedCommit's findParent method to get the right parent commit (?).
    - should override mergedCommit's toString method to show its merged parent commit.
- public static void checkout()
  - checkout(String briefCommitID, String fileName);
    - find the commit named by briefCommitID
    - put the file saved in the commit into the working directory (if the file already exists, overwrite the file)
    - edge cases:
      - if the file does not exist in the commit named by briefCommitID, print error `File does not exist in that commit.`
      - if the ID do not match any commit, print `No commit with that id exists.`
  - use `checkout [commit id] -- [file name]` to implement other possible cases
    - checkoutToHead();
      - set `commit id` to the commit pointed by `HEAD`
    - checkoutToBranch(String branchName);
      - if no branch with that name exists, print `No such branch exists.`
      - if branchName is the same as the branch which HEAD point to, print `No need to checkout the current branch.`
      - set `commit id` to the commit pointed by given branch
      - check if any file exists in the working directory which is not tracked by current commit, but will be changed by new commit. If it is, print `There is an untracked file in the way; delete it, or add and commit it first.` and exit.
      - iterate through all file in `filesMap` with basic checkout method, create or replace all tracked files
      - delete all files which are tracked in previous commit pointed by `HEAD`, but not tracked in the new commit pointed by `branchName`
      - point the `HEAD` pointer to the commit pointed by `branchName`
      - clean staging area
- public static void globalLog();
  - Use `plainFilenamesIn()` method to list all file name in the `.gitlet/Commits` directory.
  - Iterate through the list to deserialize all commits and print them.
- public static void find(String message);
  - Use `plainFilenamesIn()` method to list all file name in the `.gitlet/Commits` directory.
  - Iterate through the list to deserialize all commits and find the commit with required message.
  - Edge case: If no such commit exists, print the error message `Found no commit with that message.`
- public static void status();
  - Deserialize `statusLog`
  - Print branches:
    - read what HEAD point at
    - print `=== Branches ===\n`
    - print every branch, if the branch point to the same commit with HEAD pointer, add a `*` in the front.
    - print `\n`
  - Print Staged Files:
    - print `=== Staged Files ===`
    - print the key of `stagedForAddition` map
    - print `\n`
  - Print Removed Files:
    - print `=== Removed Files ===`
    - print the key of `stagedForRemoval` map
    - print `\n`
  - Print `=== Modifications Not Staged For Commit ===`
  - Print `\n`
  - Print `=== Untracked Files ===`
  - Print `\n`
- public static void branch(String branchName);
  - deserialize `statusLog`
  - add a new key `branchName` in `pointerMap`, value is the commit HEAD points to.
  - edge case: if `branchName` already exist, print error `A branch with that name already exists.`
- public static void rmBranch(String branchName);
  - deserialize `statusLog`
  - delete branchName in pointerMap
  - edge case:
    - if branchName doesn't exist, throw error `A branch with that name does not exist.`
    - if branchName is currentBranch, throw error `Cannot remove the current branch.`
- public static void reset(String briefCommitID);
  - This command should work exactly like `checkoutToBranch` method with slight changes.
  - should also change current branch point to reset commit id, set it in pointerMap (currentBranch -> commitID)

``` txt
new commit: ####/####/
old commit:     /####/####
        *create /change/ delete
    *[error if exists]
```

---

- public static void merge()
  - how to find latest common ancestor?
    - create two stacks to save all previous commits of master and branch pointers, and pop commits one by one to compare their common ancestor
  - how to setup conflict?
    - read fileA and fileB as two string, concatenate follow things:
    - `<<<<<<< HEAD\n` + fileA + `=======\n` + fileB + `>>>>>>>`
    - write the new string back to new file and its blob.
  - assume merge branch to master
    - create a new `merge commit`
    - find latest common ancestor(LCA), like above said.
    - put all keys in the commit.fileMap of LCA, master, and branch into one set named `allFileSet`
    - iterate through the `allFileSet`:
      - first get the blob of LCA
        - if in master or branch only one match the blob sha1 name
          - set the file in new commit to the changed blob
        - if they both changed
          - check if the master file is the same as branch file, if it is, set the file in new commit to the changed blob
          - otherwise, set `conflict`
      - if the file doesn't exist in LCA:
        - if the file only exist in one commit of master and branch
          - set the file in new commit to the changed blob
        - if the file both exists in master and branch
          - if the two files are the same, set the file in new commit to the changed blob.
          - otherwise, set `conflict`
      - compare file in master and file in new commit, if it changed, stage the change (it can be addition or removal)
      - commit the `merge commit` with message `Merged [given branch name] into [current branch name].`
      - if merge encountered a conflict, print `Encountered a merge conflict.`

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

> Todo: The step by step guide to finish this complicate task.

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

---

## Bug fix

- add a new `currentBranch` variable in `statusLog` to track what current branch is.
  - add a currentBranch variable in `statusLog`
  - set currentBranch to master in init method.
  - set currentBranch point to new commit in commit method
  - use currentBranch to decide whether add * before branch name in status method.
  - set currentBranch to branchName in checkoutToBranch method.
- find a bug in checkoutToBranch method, `checkoutFilesMap` will delete it elements, when its soft copy delete some element, that will make the program fail to find the value of some key in `checkoutFilesMap`
  - I changed the order of the code, to make sure the program search the element in the first place, and then manipulate the map itself.


