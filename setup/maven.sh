#!/bin/sh

mvn install:install-file \
    -Dfile=libs/support-v4-r18.jar \
    -DgroupId=com.google.android \
    -DartifactId=support-v4 \
    -Dversion=r18 \
    -Dpackaging=jar