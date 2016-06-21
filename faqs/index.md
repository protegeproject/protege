---
title: Frequently Asked Questions
layout: default
---
## Protege Desktop Frequently Asked Questions ##

<span style="color:red; font-weight:bold;">This FAQ is specific to Protege Desktop, versions 4, 5 and above.</span>

If you're working with older versions of Protege, you might find the following alternative FAQs helpful:

* [[Protege-OWL_3_FAQ|Protege-OWL 3.x FAQ]]
* [Protege-Frames FAQ](http://protege.stanford.edu/doc/faq.html )
* [Protege file encoding FAQ](http://protege.stanford.edu/doc/file_encodings.html)


### How do I install Protege Desktop? ###
Protege Desktop is [http://protege.stanford.edu/products.php#desktop-protege available for download] from the main Protege website.  If you're new to Protege, we ask that you read about the different installation options currently available to determine which best suits your needs:

* '''InstallyAnywhere platform independent installer program''' - this is the recommended method of installation for beginners and also for users that want a "double-clickable" executable file.  The primary reason we recommend this method for beginners is that Protege Desktop requires a 1.6 version of the Java Virtual Machine to be present.  The installer makes it very easy for you to install the correct version of the Java VM - or - if you already have Java installed, you can indicate the location during the install process.  The installer also provides an executable file that you can double-click to launch Protege Desktop, e.g., "Protege.exe" on Windows, etc.

* '''ZIP file''' - we provide a ZIP file for more advanced users that are familiar with how to make sure the proper version of Java is present.  This is also a convenient installation for users that do not require an executable file to launch Protege Desktop.

* '''Application bundle file''' - this is a new offering in Protege 4.1 and above that we hope will be an improvement for OS X users.

### Where can I find user documentation for Protege Desktop? ###
Please refer to the [[Protege4UserDocs#Protege-OWL_Editor|Protege documentation page]] on this wiki for a list of available documentation.

### Where do I ask questions and report bugs? ###
Please post comments, questions, and bug reports on the [http://mailman.stanford.edu/mailman/listinfo/protege-user protege-user mailing list].  Note that you must be subscribed to the list in order to post messages.  If you have difficulties subscribing or unsubscribing from protege-user, please contact the [mailto:protege-user-owner@lists.stanford.edu list owners].

### Where can I look at a list known bugs and feature requests? ###

Visit our issue tracker on GitHub: https://github.com/protegeproject/protege/issues?state=open.

### Why do I get a message about a damaged installer file on the Mac? ###
OS X users might see the following error message when trying to install Protege Desktop:


[[Image:Mac_damaged_installer_error_msg.png]]


This is a misleading error message from Apple's [http://support.apple.com/kb/ht5290 Gatekeeper] software.  By default, Gatekeeper is configured to only allow download and installation of applications from the Mac App Store and identified developers.  The Protege Desktop software does not yet carry "identified developer" status.  To successfully install Protege Desktop, navigate to Apple menu | System Preferences… | Security & Privacy | General tab, and select "Anywhere" from "Allow applications downloaded from".

### Why am I getting "An error related to DOT has occurred" when trying to use the OWLViz plug-in? ###
If you see this error when trying to use OWLViz, it means that you have not completed some of the necessary steps to configure this plug-in.  Complete documentation for fixing this error is available in the [http://protegewiki.stanford.edu/index.php/OWLViz#Troubleshooting troubleshooting section] of the OWLViz documentation.

### How do I change the name of an entity (class, property, individual) in my ontology? ###
Select the Refactor | Change entity URI... menu item.  In the resulting Change entity URI dialog box, enter the new name in the text box, and click the OK button.

### Why does my ontology contain classes named Error1, Error2, ...? ###
See the [[Protege4ErrorClasses|Error Classes]] page for a description of this new OWL API feature.

### How do I edit/use SWRL rules in Protege ###
* Create a new tab (Window -> Views -> Create new tab...) called something like Rules, SWRL or whatever you prefer [OPTIONAL]
* Select the Window -> Views -> Ontology views -> Rules menu and drop the "Rules" view in a tab (for example the one created in the previous step)
* Different reasoners have different level of support for rules. Pellet has the best support for SWRL rules. If you don't see Pellet listed in your Reasoner menu, install it (Open Files -> Preferences, select Plugins tab, press on "Check for downloads now" button, select and install Pellet)  
* In the Reasoner menu select the the Pellet reasoner [IMPORTANT]
* View/Create/Edit/Delete SWRL rules in the "Rules" view
* Start (Reasoner -> Start reasoner) or synchronize (Reasoner -> Synchronize reasoner) the reasoner (you can also do it with CTRL+R or COMMAND+R on Mac)
* See results of the execurted rules in you other views
