nodes= 
input_filename= 
output_filename_their= 
output_filename_my=
packets=
segments=

while getopts n:i:o:p:c:s: opt
do
    #echo $opt
    case "$opt" in
      n)  nodes="$OPTARG";;
      i)  input_filename="$OPTARG";;
      o)  output_filename_their="$OPTARG";;
	  p)  output_filename_my="$OPTARG";;
	  c)  packets="$OPTARG";;
	  s)  segments="$OPTARG";;
      \?)		# unknown flag
      	  echo >&5 \
	  "usage: $0 [-n] [nodes] [-i] [input filename] [-o] [output filename their] [-p] [output filename my] [-c] [packets] [-s] [segments]"
	  exit 1;;
    esac
done
shift $((OPTIND - 1))

perl grep.pl $input_filename $nodes > .temp.txt
perl grepupd.pl $input_filename $nodes > .tempupd.txt

awk '{print $3, $4, $5, $6}' .temp.txt > .test1.txt
awk '{print $3, $4, $5, $6}' .tempupd.txt > .test2.txt

sort -k1 -k2 -k3 -n .test1.txt > $output_filename_their
sort -k1 -k2 -k3 -n .test2.txt > $output_filename_my

diff $output_filename_my $output_filename_their > diff.txt

awk -v packets=$packets -v segments=$segments 'BEGIN {sum = 0} {sum = sum + $3} END {printf("Their script count: %d\n", (sum + (packets * segments) + segments))}' $output_filename_their
awk -v packets=$packets -v segments=$segments 'BEGIN {sum = 0.0} {sum = sum + $3} END {printf("My script count: %d\n", (sum + (packets * segments) + segments))}' $output_filename_my

cat $input_filename | grep 'ENQUEUE' | wc -l

rm .temp.txt
rm .tempupd.txt
rm .test1.txt
rm .test2.txt



