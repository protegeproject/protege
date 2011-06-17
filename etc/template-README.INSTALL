
First of all, use the ant scripts to build this project.  The Protege
team currently only uses the maven scripts to run the nightly tests.
For most projects the ant scripts run with no changes.  The only thing
that needs to be done to run the Protege ant scripts is to set the
PROTEGE_HOME environment variable.  Some more information on this can
be found at the web page:

     http://protegewiki.stanford.edu/wiki/ConfiguringAntBuildFiles

The ant install target will create the plugin and install it in
Protege. The ant jar target will create the plugin in the build
directory.

Troubleshooting:

Missing protege libraries:

Sometimes an ant script that depends on other plugins will generate a
"Missing protege libraries" message.  This means that the copy of
Protege pointed to by the PROTEGE_HOME environment variable is missing
some plugin that is needed by this ant script.  To find out which
library is missing, run the ant script with the -v option (ant -v
install).  There will be an exception at the bottom.  Scroll back
through the error messages until you find a message like this:

    [available] Unable to find file /tmp/Protege/plugins/org.protege.editor.owl.jar

This is telling you what file is missing.  More information is
available at 

     http://protegewiki.stanford.edu/wiki/ConfiguringAntBuildFiles#Missing_protege_libraries
