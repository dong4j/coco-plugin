#!/bin/bash
# 本地测试使用, 因为没有 coco 用户

if [[ ! "$1" ]] ;then
    bin/server.sh -T -r local &
else
    bin/server.sh -T -r $1 &
fi

