---
title: Linux Installation
layout: default
---

# Installing Protégé on Linux

Protégé {{site.version}} is distributed in the form of a ```tar.gz``` file from the Protege Website, and includes the 64-bit Java Runtime Environment (JRE). It's therefore not necessary to have Java pre-installed on your computer to run Protege.

To install Protégé:

1. Go to the [software page](http://protege.stanford.edu/software.php#desktop-protege) on the Protege website and click the **Download for Linux** button to download the tar.gz file to your machine. Alternatively, go to the [release page](https://github.com/protegeproject/protege-distribution/releases/tag/protege-{{site.version}}) and download the ```Protege-{{site.version}}-linux.tar.gz``` file.
2. Unpack the archive. To do this, either use your file explorer and double-click on the archive, and extract it to the desired location, or type in a terminal window:

     ```
     tar zxvf Protege-{{site.version}}-linux.tar.gz
     ```
3. Launch Protégé.  To launch Protege Desktop, double-click on the **protege** file in the ''Protege folder'' in the file explorer, or type **./protege** in a terminal window, once you have changed the directory to the Protege folder.
