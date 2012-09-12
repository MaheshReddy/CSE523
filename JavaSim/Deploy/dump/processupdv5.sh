#!/bin/bash
nodes= 
input_filename= 
output_filename= 

while getopts n:i:o: opt
do
    #echo $opt
    case "$opt" in
      n)  nodes="$OPTARG";;
      i)  input_filename="$OPTARG";;
      o)  output_filename="$OPTARG";;
     \?)		# unknown flag
      	  echo >&2 \
	  "usage: $0 [-n] [nodes] [-i] [input filename] [-o] [output filename]"
	  exit 1;;
    esac
done
shift $((OPTIND - 1))

perl grepupdv5.pl $input_filename > .temp.txt
sort -k1 -n .temp.txt > $output_filename
rm .temp.txt

