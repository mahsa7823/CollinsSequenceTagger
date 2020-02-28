Sequence tagger -- Discriminative Training for Hidden Markov Models with Averaged Perceptron
============================================================================================

This repository is an implementation of the discriminative training framework for sequence labeling introduced by Collins<sup>+</sup> (2002). This framework is an extension of the HMM sequence tagger to a log-linear model for combination of weighted features, perceptron training with weight averaging, and the Viterbi algorithm for decoding.

This tagger can be used for any sequence labeling task including part-of-speech tagging, named entity recognition, chunking, and shallow parsing. As reported for part-of-speech tagging, this tagger achieves state-of-the-art perfomance of 97.1\% accuracy.

+ Michael Collins. 2002. Discriminative training methods for Hidden Markov Models: theory and experiments with Perceptron algorithms. In EMNLP, pages 1–8.

The inputs (train, dev, test) should be in a two-column tab separated format such as the following example. Test file can either be in a two-column format or a single column of just word sequences.

Ms.     NNP
Haag    NNP
plays   VBZ
Elianti NNP
.       .

Rolls-Royce     NNP
Motor   NNP
Cars    NNPS
Inc.    NNP
said    VBD
...

The training terminates and the model is written into the disk if there is no more
improvement on the dev set after 5 consecutive iterations. The output is in the same
format as the input.

Compile
-------
javac PerceptronTagger.java

Run
---
java PerceptronTagger -train example/train.txt  -dev example/dev.txt -mdlpath model/

java PerceptronTagger -mode test  -in example/test.txt -outname output.txt -mdlpath model/


Publications
------------

If you use this tagger, please cite the following paper:

Mahsa Yarmohammadi. 2014. Discriminative training with Perceptron algorithm for POS tagging task. Technical Report CSLU-2014-001, Center for Spoken Language Understanding, Oregon Health & Science University.

Some work that have used this tagger:

+ Kyle Gorman, and Steven Bedrick. 2019. We need to talk about standard splits. In ACL, pages. 2786-2791.
+ Mahsa Yarmohammadi. 2016. Incremental Segmentation and Annotation Strategies for Real-time Natural Language Processing Applications. PhD dissertation, Oregon Health & Science University.
+ Masoud Rouhizadeh, Emily Prud’Hommeaux, Jan Van Santen, and Richard Sproat. 2015. Measuring idiosyncratic interests in children with autism. In ACL.
