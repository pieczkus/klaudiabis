#!/bin/bash

ps -ef | grep  "[k]laudiabis.jar" | awk '{ print $2 }' | xargs kill
