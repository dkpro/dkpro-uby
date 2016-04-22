# DKPro Uby
DKPro Uby is a Java framework for creating and accessing sense-linked lexical resources in accordance with the UBY-LMF lexicon model, an instantiation of the ISO standard Lexicon Markup Framework (LMF). The software library includes the following modules:

  * an implementation of the UBY-LMF lexicon model
  * tools for the conversion of a variety of lexical resources to the standard-compliant format UBY-LMF
  * tools for computing pairwise alignments between resources
  * tools for creating and accessing UBY-LMF compliant databases, also within UIMA pipelines

## Learning UBY
Using UBY requires solid knowledge of lexical resources, such as WordNet and FrameNet and their underlying concepts. 
For instance, you should be able to tell the difference between a sense and a synset. 

Learn these foundations and how to use UBY by walking through a detailed tutorial on *Linked Lexical Resources*, see https://dkpro.github.io/dkpro-uby/


## Download a UBY database
We provide dumps of UBY databases for download: http://uby.ukp.informatik.tu-darmstadt.de/uby. 
Get more information on https://dkpro.github.io/dkpro-uby/

## How to Cite
 * *Lexicon Data and Code* 
   * If you use UBY or the software provided here, please cite this EACL 2012 paper: http://www.ukp.tu-darmstadt.de/fileadmin/user_upload/Group_UKP/publikationen/2012/uby_eacl2012_cameraready.pdf, BibTeX http://www.ukp.tu-darmstadt.de/publications/details/?no_cache=1&tx_bibtex_pi1%5Bpub_id%5D=TUD-CS-2012-0023#  
 * *Lexicon Model* 
   * If you use UBY-LMF, please cite this LREC 2012 paper: http://www.ukp.tu-darmstadt.de/fileadmin/user_upload/Group_UKP/publikationen/2012/LREC2012_ubyLMFcamera-Ready.pdf, BibTeX: http://www.ukp.tu-darmstadt.de/publications/details/?no_cache=1&tx_bibtex_pi1%5Bpub_id%5D=TUD-CS-2012-0045#

## License Information
 * *Lexicon Data* Apart from !GermaNet and IMSlex which are licensed under an academic research license, all resources in UBY are available under open licenses, requiring either attribution or both, attribution and share alike.
 * *Lexicon Model* The lexicon model UBY-LMF is available under the CC BY-SA 3.0 license http://creativecommons.org/licenses/by-sa/3.0/de/deed.en CC BY-SA 3.0 license.
 * *Code License* While the UBY-API module and most conversion modules are available under the Apache Software License (ASL) version 2, there are a few conversion modules that are GPL licensed. It must be pointed out that while the component's source code itself is licensed under the ASL or GPL, individual components might make use of third-party libraries or products that are not licensed under the ASL or GPL, such as LGPL libraries or libraries which are free for research but may not be used in commercial scenarios. Please make sure that you are aware of the third party licenses and respect them. 


----
This project was initiated under the auspices of Prof. Dr. Iryna Gurevych, Ubiquitous Knowledge Processing Lab (UKP), Technische Universität Darmstadt.
We are grateful for the generous financial support from the Volkswagen Foundation and the German Research Foundation (DFG).

Please post any questions or suggestions to the UBY Developers Group: http://groups.google.com/group/uby-developers or to the UBY Users Group: http://groups.google.com/group/uby-users.
