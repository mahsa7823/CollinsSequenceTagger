#!/bin/sh

#java PerceptronTagger -train <train_file> -in <test_file> [-out <output_file>] [-fb] [-iter n]
#-out: optional, default output file name is "output"
#-fb: use this flag if you want to use forward-backward algorithm instead of viterbi
#-iter: number of iteration in the perceptron algorithm, default is 5.


# Only HMM features
java PerceptronTagger -in input -train train -out tagged_vi

java PerceptronTagger -in input -train train -out tagged_fb -fb


# All features
java PerceptronTagger -in input -train train -out tagged_vi_all -dig -suf -pref -next -prev -bprevnext
