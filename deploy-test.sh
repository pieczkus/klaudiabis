#!/bin/bash

APP=klaudiabis

scp -r app f1:~/www/$APP
scp -r assets f1:~/www/$APP
scp -r directives f1:~/www/$APP
scp index.html f1:~/www/$APP