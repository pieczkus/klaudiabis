#!/bin/bash

APP=klaudiabis

case "$1" in
	client)


        echo "Doing deploy client APP"

        scp -r app rpif1:~/www/$APP
        scp -r assets rpif1:~/www/$APP
        scp -r directives rpif1:~/www/$APP
        scp index.html rpif1:~/www/$APP
        ;;
    server)

        array=(rpia1 rpia2 rpia3)

        echo "Building server"

        cd server
        sbt clean assembly
        cd ..

        echo "Doing deploy to: ${#array[*]} servers"

        for item in ${array[*]}
            do
	            printf "Deploying to %s\n" $item
	            scp server/target/scala-2.11/server-assembly-1.0.jar ${item}:~/$APP/candidate/$APP.jar
        done
        ;;
    *)
        echo "Gunwo żeś Pan zdiplodokował"
        ;;
esac

