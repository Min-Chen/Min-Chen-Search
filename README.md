CS 212 Projects
=================================================

This repository contains the project files for CS 212 Software Development at the University of San Francisco. This includes test files, input files, and expected output files.

## Table of Contents ##

- [Specifications](#specifications)
  - [Project 1 Inverted Index](specifications/Project 1 Inverted Index.md)
  - [Project 2 Partial Search](specifications/Project 2 Partial Search.md)
  - [Project 3 Multithreading](specifications/Project 3 Multithreading.md)
  - [Project 4 Web Crawler](specifications/Project 4 Web Crawler.md)
  - [Project 5 Search Engine](specifications/Project 5 Search Engine.md)
- [Initial Setup](#initial-setup)
- [Submission](#submission)
- [Testing](#testing)
  - [Running JUnit Tests Locally](#running-junit-tests-locally)
  - [Running JUnit Tests Remotely](#running-junit-tests-remotely)
- [Code Review](#code-review)
  - [Code Review Appointments](#code-review-appointments)
  - [Code Review Process](#code-review-process)
  - [Resubmission Process](#resubmission-process)

## Specifications ##

The specifications for each project are provided in the `specifications` subdirectory:

- [Project 1 Inverted Index](specifications/Project 1 Inverted Index.md)
- [Project 2 Partial Search](specifications/Project 2 Partial Search.md)
- [Project 3 Multithreading](specifications/Project 3 Multithreading.md)
- [Project 4 Web Crawler](specifications/Project 4 Web Crawler.md)
- [Project 5 Search Engine](specifications/Project 5 Search Engine.md)

Each project builds on the last. You must pass each project before moving on to the next.

## Initial Setup ##

Everyone will have a private GitHub repository created by the instructor with the following format:

```
fall2014-username-projects
```

where `username` is your USF Connect username.

Initial project files, including input files, expected output files, project specifications, and test files, will be pushed directly to your project repository by the instructor. To setup your Eclipse project with these files, follow these steps:

1. Use the "Import Projects from Git" feature using the Clone URL for your repository. You will need to enter your GitHub username and password under the "Authentication" settings.

2. Each project will have its own branch. To start, you will need to create a `project1` branch. Right-click your project, select "Team > Switch To... > New Branch" from the popup menu. Enter `project1` as the name (all lowercase with no spaces).

3. Commit and push your changes. Make sure your changes are showing up in the [correct branch](https://help.github.com/articles/viewing-branches-in-your-repository) in GitHub. You can also make sure this is the [default branch](https://help.github.com/articles/setting-the-default-branch) in your repository settings.

:warning: When you pass code review for a project, you will be instructed to create a branch for the next project. Please do **NOT** create those branches until that time!

## Submission ##

If your Eclipse project is properly setup and connected to your GitHub repository, submitting your project involves committing and pushing your local changes to the remote repository. You can check your GitHub repository online to verify your submission.

## Testing ##

You should perform your own testing while you develop the project. The code provided only tests your final result, and may not be suitable for testing your project during development. To see which test files are relevant for each project, look at the test suites provided in the `Project#Test.java` files. For example, the `Project1Test.java` file references `ProjectTest.java` and `IndexTest.java`.  You must pass all of the tests in those two files for project 1.

#### Running JUnit Tests Locally ####

If your Eclipse project is properly setup, you should be able to open the appropriate test file and click the "Run" button.

You will be prompted to select which set of tests you wish to run. For example, if you open `IndexTest.java` and click "Run", you can either run all of the unit tests in the file by selecting "IndexTest", or alternatively run only the argument-related unit tests by selecting "IndexArgumentTest - IndexTest" instead.

You can run an individual test by selecting the test method, right-clicking, and selecting "Run As > JUnit Test" from the popup menu. This is helpful for debugging an individual test that is failing.

#### Running JUnit Tests Remotely ####

Before you may sign up for code review, you must verify you are passing the project unit tests by running the `project` script from the lab computers. Login remotely to a lab computer and run the following:

```
/home/public/cs212/test/project [-u csuser] [-g gituser] [-r results] ##
 -u specifies your cs username (optional)
 -g specifies your github username (optional)
 -r specifies where to save the test results (optional)
 ## is the project number to test (between 1 and 4)
```

For example, consider the following:

```
/home/public/cs212/test/project -u sjengle -g sjengle@cs.usfca.edu -r ~/results.txt 1
```

This will test project `1` for CS username `sjengle` using `sjengle@cs.usfca.edu` to login to GitHub, and will save the output to a file `~/results.txt`.

If your GitHub username is the same as your CS username and you do not want to save the results, you can simply run the following instead:

```
/home/public/cs212/test/project 1
```

This will test project `1` for the currently logged in CS user, assuming the GitHub username is the same.

## Code Review ##

Once you have verified your project is properly submitted and passing the necessary JUnit tests, you may sign up for code review. Each code review is 20 minutes, and will be held in the instructor's office.

#### Code Review Appointments ####

To sign up for a code review, please follow these setups:

- Make sure you are passing 100% of the unit tests when running the `project` script from the lab computers.

- In Github, [create a new issue](https://help.github.com/articles/creating-an-issue) titled "Project # Verification" where `#` is the project you want verified.

- Next, [assign the issue](https://help.github.com/articles/assigning-issues-and-pull-requests-to-other-github-users) to the teacher assistant (username `wwchiang`).

- Wait for the issue to be closed by the teacher assistant.

If your project was properly verified, the TA will provide a link to sign up for a code review in the issue comments. Otherwise, you will need to figure out why your project is not passing the unit tests and repeat this process.

:memo: You can see all of the issues that you have created or have been assigned in the [Issues Dashboard](https://github.com/dashboard/issues/) on GitHub.

**:warning: If you sign up for code review but you are not passing the `project` script on the lab computers, your project score will be deducted 5 points.**

#### Code Review Process ####

During the code review, the instructor will discuss your code design with you and make suggestions for improvement. These will be provided either as `// TODO` comments directly in your code or as open "Issues" in GitHub. *You may remove these comments and close the issues after addressing them.*

After the review, your project will be given a `PASS`, `WARN`, or `FAIL` status:

- The `PASS` status signifies you passed the code review and may move on to the next project. Congratulations!

- The `WARN` status signifies you passed the code review and may move on to the next project, but must address some lingering issues before submitting the next project.

- The `FAIL` status signifies you did not pass the code review, and must resubmit. The resubmission process is outlined below.

  **:persevere: Do not fret!** This is expected to happen at least once per project. If you can pass the first code review, then you may not be in the right class!

:hourglass: Each code review is 20 minutes. If we do not have a chance to review your entire project within this time frame, it is likely you will need another code review. As such, make sure you address any easy fixes such as proper formatting and commenting **prior** to the code review to avoid wasting time.

:warning: Remember to [pull any changes](https://help.github.com/articles/fetching-a-remote#pull) made during code review to your local repository! Otherwise, you will end up with [merge conflicts](https://help.github.com/articles/resolving-a-merge-conflict-from-the-command-line) that may be difficult to resolve.

#### Resubmission Process ####

Everyone is expected to resubmit each project 1 to 3 times after their first code review, depending on the project. To resubmit your project:

1. Make sure you have addressed *all* of the issues opened by the instructor during the last code review. (You may close issues as you address them.)

2. Make sure you have updated your project repository on GitHub.

3. Make sure you re-run the `project` script on the lab computers.

4. Sign up for another code review appointment using the process outlined above.

:memo: For the most part, project resubmissions will not negatively impact your project grade. However, if you ignore an open issue or sign up for code review before you are passing the necessary tests, your final project score will be docked 5 points for each offense.
