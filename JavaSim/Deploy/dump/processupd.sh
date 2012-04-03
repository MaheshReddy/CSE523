input_filename= 
output_filename= 

while getopts n:i:o: opt
do
    #echo $opt
    case "$opt" in
      i)  input_filename="$OPTARG";;
      o)  output_filename="$OPTARG";;
      \?)		# unknown flag
      	  echo >&2 \
	  "usage: $0 [-n] [nodes] [-i] [input filename] [-o] [output filename]"
	  exit 1;;
    esac
done
shift $((OPTIND - 1))

perl grepupd.pl $input_filename > .temp.txt
sort -k1 -n .temp.txt > $output_filename
rm .temp.txt

