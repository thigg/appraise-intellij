# appraise intellij
This is a draft of an intellij plugin to use [git-appraise](https://github.com/google/git-appraise) with intellij.

_This is a very early draft_. Feel free to contribute, or take this code or just the idea, build your own and let me know.

## What is git-appraise?
git appraise is a federated protocol for creating pull requests.

Currently your comments in code reviews are mostly based on specific platforms e.g. gitea, gitlab, github, youtrack...

This protocol aims to make comments on pull requests platform independent.
You write them in your IDE, they get committed to git, can be read by your repo, displayed, others can view them in
their ide of choice etc...

## What can this plugin do?
 - if you open a diff in intellij you can write comments into the diff

Missing:
 - handle multiple pull requests, no need for starting via cli
 - read comments
 - ...

## License
See [license.md](license.md)
