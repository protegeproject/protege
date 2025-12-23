# Fix Protege 5.6.4 su Arch Linux / Java 17

Working with Protégé on Arch Linux with Java 17 dependencies installed, I incurred in errors which didn't allow me to call SPARQL Queries. While I could write or visualize the ontology,
it was impossible to me to call the Queries, and error messages were popping out:  
1) `IllegalAccessError` in NodeSerialiser, precisely ```
java.lang.IllegalAccessError: class org.coode.mdock.NodeSerialiser (in unnamed module @0x16b294e0) cannot access class com.sun.org.apache.xml.internal.serialize.OutputFormat (in module java.xml) because module java.xml does not export com.sun.org.apache.xml.internal.serialize to unnamed module```
4) `NoSuchMethodError` in RDFTranslator, specifically ```java.lang.NoSuchMethodError: void org.semanticweb.owlapi.rdf.model.AbstractTranslator.<init>(...)```

## Cause of the issue

These errors are **not** caused by an incorrect ontology, but are due to Java version incompatibilities on Arch Linux:

1) **IllegalAccessError:**  
  Java 9+ introduced the **Java Platform Module System (JPMS)**, restricting access to internal JVM classes.  
  `com.sun.org.apache.xml.internal.serialize.OutputFormat` is an internal class that was freely accessible in Java 8, but blocked in Java 17. Protege’s `NodeSerialiser` tries to access it directly, causing this error.

2) **NoSuchMethodError:**  
  Protege 5.6.4 is compiled against an older version of the OWL API. Arch Linux provides the latest OWL API version, where the constructor used by `RDFTranslator` no longer exists.  

## Temporary Fix

To work around the `IllegalAccessError` and run Protégé on Arch Linux / Java 17, add the following JVM flag:

```bash
java --add-exports java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED -jar protege.jar
--add-exports allows unnamed modules (like Protege) to access internal classes in java.xml.
```

This allows Protégé to start and execute SPARQL Queries without changing the source code.

## Permanent Script

```bash
#!/bin/bash
java --add-exports java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED -jar protege.jar
```
Make it ex.

```bash
chmod +x protege.sh
```

and now you can run Protègè by


```bash
./protege.sh
```

## Permanent Fix (Developer Solution)

For a long-term solution, developers should:
1) NodeSerialiser: Replace usage of com.sun.org.apache.xml.internal.serialize.OutputFormat with standard JAXP (javax.xml.transform) or external libraries like Apache Xerces.
2) RDFTranslator / OWL API: Update method calls or fix the OWL API version dependency to match the one Protege 5.6.4 was compiled against.

This ensures full compatibility with modern Java versions and avoids the need for JVM flags.












