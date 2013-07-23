#!/bin/sh

mvn install:install-file \
    -Dfile=libs/support-v4-r13.jar \
    -DgroupId=com.google.android \
    -DartifactId=support-v4 \
    -Dversion=r13 \
    -Dpackaging=jar