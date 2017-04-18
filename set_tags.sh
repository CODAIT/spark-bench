#!/bin/bash

MASTER_BRANCH="master"
DEVELOP_BRANCH="develop"


# Are we on the right branch?
if [ "$TRAVIS_BRANCH" = "$MASTER_BRANCH" ] || [ "$TRAVIS_BRANCH" = "$DEVELOP_BRANCH" ]; then

  # Is this not a Pull Request?git stat
  if [ "$TRAVIS_PULL_REQUEST" = false ]; then

    # Is this not a build which was triggered by setting a new tag?
    if [ -z "$TRAVIS_TAG" ]; then
      echo -e "Starting to tag commit.\n"

      git config --global user.email "travis@travis-ci.org"
      git config --global user.name "Travis"

      # Add tag and push to master.
      git tag -a v${TRAVIS_BUILD_NUMBER} -m "Travis build $TRAVIS_BUILD_NUMBER pushed a tag."
      git push origin --tags
      git fetch origin

      echo -e "Done magic with tags.\n"
  fi
  fi
fi