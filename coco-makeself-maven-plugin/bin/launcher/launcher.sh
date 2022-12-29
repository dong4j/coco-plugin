#!/bin/bash

chown -R coco:coco ./

if [[ ! "$1" ]] ;then
    su coco -c "bin/server.sh -r prod -t"
else
    su coco -c "bin/server.sh -r $1 -t"
fi
