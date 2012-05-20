#!/bin/bash
nodes= 
input_filename= 
output_filename_org= 
output_filename_updv1=
output_filename_updv2=
packets=
segments=

while getopts n:i:o:p:q:c:s: opt
do
    #echo $opt
    case "$opt" in
      n)  nodes="$OPTARG";;
      i)  input_filename="$OPTARG";;
      o)  output_filename_org="$OPTARG";;
	  p)  output_filename_updv1="$OPTARG";;
	  q)  output_filename_updv2="$OPTARG";;
	  c)  packets="$OPTARG";;
	  s)  segments="$OPTARG";;
     \?)		# unknown flag
      	  echo >&6 \
	  "usage: $0 [-n] [nodes] [-i] [input filename] [-o] [output filename original] [-p] [output filename updatev1] [-q] [output filename updatev2][-c] [packets] [-s] [segments]"
	  exit 1;;
    esac
done
shift $((OPTIND - 1))

perl grep.pl $input_filename $nodes > .temp.txt
perl grepupd.pl $input_filename $nodes > .tempupd.txt
perl grepupdv2.pl $input_filename $nodes > .tempupd2.txt

awk -v packets=$packets -v segments=$segments 'BEGIN {sum = 0; sumi = 0; sumd = 0;} { sum = sum + $5; if($2 == "i") {sumi = sumi + $5;} if($2 == "d") {sumd = sumd + $5;}} END { printf("Orignial script total packet count: %d\n", (sum + (packets * segments) + segments)); printf("Original script interest packet count: %d\n", (sumi + (packets * segments) + segments)); printf("Original script data packet count: %d\n", sumd);}' .temp.txt	
awk -v packets=$packets -v segments=$segments 'BEGIN {sum = 0; sumi = 0; sumd = 0;} { sum = sum + $5; if($2 == "i") {sumi = sumi + $5;} if($2 == "d") {sumd = sumd + $5;}} END {printf("Updated script total packet count: %d\n", (sum + (packets * segments) + segments)); printf("Updated script interest packet count: %d\n", (sumi + (packets * segments) + segments)); printf("Updated script data packet count: %d\n", sumd);}' .tempupd.txt
awk -v packets=$packets -v segments=$segments 'BEGIN {sum = 0; sumi = 0; sumd = 0;} { sum = sum + $5; if($2 == "i") {sumi = sumi + $5;} if($2 == "d") {sumd = sumd + $5;}} END {printf("UpdatedV2 script total packet count: %d\n", (sum + (packets * segments) + segments)); printf("UpdatedV2 script interest packet count: %d\n", (sumi + (packets * segments) + segments)); printf("UpdatedV2 script data packet count: %d\n", sumd);}' .tempupd2.txt

#awk 'BEGIN {sum = 0;} { sum = sum + $2 * ($3/1500);} END { printf("Requests: %d\n", (sum));}' docs.web
#awk 'BEGIN {large = 0;} { if (large <= $3) {  };} END { printf("Average: %d\n", (sum/54273));}' docs.web

echo Total packets directly from trace file: 
cat $input_filename | grep 'ENQUEUE' | wc -l

echo Total interest packets directly from trace file: 
cat $input_filename | grep 'ENQUEUE' | grep 'i' |wc -l

echo Total data packets directly from trace file: 
cat $input_filename | grep 'ENQUEUE' | grep 'd' | wc -l

awk '{print $3, $4, $5, $6}' .temp.txt > .test1.txt
awk '{print $3, $4, $5, $6}' .tempupd.txt > .test2.txt
awk '{print $3, $4, $5, $6}' .tempupd2.txt > .test3.txt

sort -k1 -k2 -k3 -n .test1.txt > $output_filename_org
sort -k1 -k2 -k3 -n .test2.txt > $output_filename_updv1
sort -k1 -k2 -k3 -n .test3.txt > $output_filename_updv2

diff $output_filename_updv1 $output_filename_org > diff1_mt.txt
diff $output_filename_updv2 $output_filename_updv1 > diff2_mm2.txt
diff $output_filename_updv2 $output_filename_org > diff3_m2t.txt

rm .temp.txt
rm .tempupd.txt
rm .tempupd2.txt
rm .test1.txt
rm .test2.txt
rm .test3.txt



