#!/bin/sh

# train phase
# java PerceptronTagger -mode train -train <train_file> -dev <dev_file>  -mdlpath <model_directory>
# mdlpath: the path in which the output model is written

# test phase
# java PerceptronTagger -mode test -in <test_file> -mdlpath <model_directory> [-outname <output_file>]
# mdlpath: the path from which the model is loaded
# outname: (optional) name of the output tagged file. The default name is "output"


java -Xmx4g PerceptronTagger -mode train -train train -dev dev -mdlpath model/
java -Xmx4g PerceptronTagger -mode test -in test -mdlpath model/  -outname output1
