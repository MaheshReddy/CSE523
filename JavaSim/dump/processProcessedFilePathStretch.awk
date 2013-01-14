NR==FNR { 
    a[$1]=$1;
}
 
NR>FNR { 
    k=$3; 
    if (k in a) {
    	print;
    }
}