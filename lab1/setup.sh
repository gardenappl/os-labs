#!/bin/sh

for i in 0 1 2 3 4 5; do
	mkfifo "$i.in"
	mkfifo "$i.out"
done
