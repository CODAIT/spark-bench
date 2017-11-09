---
layout: page
title: Contributing to Spark-Bench
permalink: /contributing/
---

Thank you for your interest in contributing to Spark-Bench!

## Fork The Project

In order to contribute code to Spark-Bench, you will need to first fork the project.
Github provides lots of excellent documentation: <https://help.github.com/articles/fork-a-repo/>

## Getting Your Dev Environment Setup
Follow the instructions for cloning and compiling Spark-Bench in the [compilation guide](../developers-guide/building_spark-bench.md)

## Finding a Starter Issue
There are many issues marked in Github as `help wanted`. Usually these are easy to medium difficulty.
If any interest you, feel free to pick it up directly or get in touch with the main Spark-Bench devs to
ask questions about it :)

## Setup Your Feature Branch
From the master branch in your fork, create a new branch with a
short but descriptive name for what your changes will be. For example,
if you're doing updates to the documentation site, you may want
to name your branch `doc-updates`. How you structure branches in your fork is totally
up to you!

## Making Your Pull Request
You've made your code changes, you've tested them locally, 
you're almost ready to contribute your changes back! Woo!!

First, you'll need to touch up your branch and commit messages using git.

A good pull request has just 1 commit with a well-formatted commit message.
It is up to date with the current master, and can be easily rebased onto the 
main master branch with no merge commits.

There are a variety of ways to squash all the commits in your branch down to one.
We'll describe just one way here, but there are many others.

### Syncing Master
Let's assume that you have a fork under your own username on Github, and that you've
setup the main SparkTC/spark-bench repo 
[as a remote](https://help.github.com/articles/configuring-a-remote-for-a-fork/) named `upstream`,
like so:

```bash
$ git remote -v
origin  git@github.com:ecurtin/spark-bench.git (fetch)
origin  git@github.com:ecurtin/spark-bench.git (push)
upstream        git@github.com:SparkTC/spark-bench.git (fetch)
upstream        git@github.com:SparkTC/spark-bench.git (push)
```

A best practice is to keep your master branch in your repo free of any changes so that
you can easily sync it with the upstream repo, and to do all of your development work
in a separate branch.

Let's say you have two branches, `master` which is free of any changes, and `doc-updates`
which has some new changes to contribute.

First, make sure your local copy of `master` is up to date.
```bash
git checkout master
git pull upstream master
git push origin master
```
This pulls any new changes from SparkTC/spark-bench into your local repo, 
then pushes them up to your fork which in this case is ecurtin/spark-bench.

### Rebasing Your Changes On Top Of Master
Now let's go back to the branch with your changes
```bash
git checkout doc-updates
```

If you're inexperienced with squashing and rebasing, it might be good
to use a copy of your branch in case something goes wrong.
```bash
git checkout -b rebasing-is-fun
```

We're going to rebase the changes in this branch _on top of_ the changes in master
```bash
git rebase master
```

### Squashing Your Commits
Now that all our changes are on top of the latest updates from master, we want to squash
all the commits you've made while working on your feature into just one commit.
Again, there are _many_ ways to do this, this is just one.

Let's say you have three commits that you want to get down to just one.
```bash
git rebase -i HEAD~3
```

This will open up a file in Vim or whatever default editor you have set for git
with a list of your commits in chronological order.

Beside each commit will be the word `pick`. Using your text editor, keep the first
commit as `pick` but change the second two to `s` or `squash`.
Save and close this file and git will run the process. Then it will give you a chance
to rewrite your commit message.

### Formatting Your Commit Message

Your commit message should have a subject that is 50 characters or less
followed by a body that is line-wrapped at 72 characters.

For instructions, examples, and more information about formatting 
a great commit message, see [this blog post](http://blog.ssanj.net/posts/2015-10-22-git-commit-message-format.html)

### Sync Your New Changes
Now that your changes are on your branch, push them up to your fork on Github.
```bash
git push origin
```

### Create Your PR on Github

Github has lots of [excellent documentation](https://help.github.com/articles/creating-a-pull-request-from-a-fork/) 
for this if you're unfamiliar with the process.

## Don't Be Shy!
If you're a first-time open source contributor, we are SO glad that you're here!
Please feel free to reach out to the main Spark-Bench devs for questions and for help with 
getting your contribution into Spark-Bench 🙂