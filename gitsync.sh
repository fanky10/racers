#!/bin/sh
SCRIPTNAME=customgit.sh
stash_save(){
 git stash save "localStash"
}
stash_pop(){
 git stash pop
}
git_pull(){
 echo "pulling data"
 git pull
}
git_push(){
 echo "pushing data"
 git push
}
git_merge(){
 echo "merging"
 git mergetool
}
case "$1" in
 push)
  # do the pushing
  stash_save
  git_push
  stash_pop
 ;;
 pull)
  #do the pulling 
  stash_save
  git_pull
  stash_pop
 ;;
 merge)
  #do the merging x)
  git_merge
 ;;
 sync)
  stash_save
  git_pull
  git_push
  stash_pop  
 ;;
 *)
  echo "Usage: $SCRIPTNAME {push | pull | sync}"
  exit 1;
 ;;
esac

exit 0;
