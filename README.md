# DLTree

DLTree is a tool for phylogeny reconstruction.
DLTree stands for `Dynamical Language Tree` which is the implementation
of an alignment-free algorithm to generate a dissimilarity matrix from
comparatively large collection of DNA or Amino Acid sequences,
preferably whole-genome data, for phylogenetic studies.

## News
The latest DLTree 1.1 includes fixes for issues identified in 1.0 as well as other enhancements and changes. The notable changes compared to 1.0 include:
1. 10000+ species are supproted.
2. Cutting data redundancy by MD5 is supported. 
You can test it using the command ./md5.sh xx.fasta. 
It only contains bases or residues, ignoring comments and the line break.

Updated the packaged version of the Tomcat Native Library to 1.2.16 to pick up the latest Windows binaries built with APR 1.6.3 and OpenSSL 1.0.2m

## Preparation
Java 1.6+ and megacc 6+

## Get started
1.configure conf/conf.properties
2.specify total available memory ./start.sh
3. chmod u+x ./start.sh mega/megacc
4.run ./start.sh
5.check result

## Reference
Qi Wu, Zu-Guo Yu and Jianyi Yang. (2017) DLTree: a web server for phylogeny reconstruction using dynamical language, 
Bioinformatics 33(14): 2214–2215, doi: 10.1093/bioinformatics/btx158.

## License
This software is free for non-commercial use. For commercial use,
a software agreement is required.

## Feedback
Welcome to send the email to wuqird@aliyun.com, if you have any question about the DLTree.
