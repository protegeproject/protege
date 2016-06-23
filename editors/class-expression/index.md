---
title: Editing Class Expressions
layout: default
---

# Class Expression Editing

Protege uses the [Manchester OWL Syntax]({{site.baseurl}}/manchester-owl-syntax) for working with class expressions.

Protege allows the user to create the full range of class expressions, wherever classes can be used in the interface:

* Classes: EquivalentTo, SubClassOf, DisjointClasses
* Properties: Domain, Range
* Individuals: Type

There are several inbuilt editors that hide the expression format from the user (such as the '''restriction creator''') that are useful when getting to know the tools, but they are not as powerful as the expression editor - for example, you can only select named fillers from the class hierarchy.

When familiar with the syntax, the expression editor can be a much faster way to create these class expressions (especially because of autocomplete).

## The Protege expression editor ##

To add a someValuesFrom restriction on a primitive class using the expression editor:
* select the class
* clicking the Add (+) next to the Superclasses section in the Class description view
* select the Class expression editor
* type "p some B" (where p and B are an object property and class respectively).

Protege will check that the expression is well formed before the value can be accepted and provides warning messages if not.


## Autocompletion

<figure>
  <img src="{{site.baseurl}}/assets/editors/class-expression/Auto-completion.png" style="max-width: 660px"/>
  <figcaption>Auto-completion, which is activated by pressing CTRL+Space.  In this case the user has typed in "'cellular response to co" and then pressed CTRL+Space.  They are offered the possible completions in the popup list.</figcaption>
</figure>

Auto-completion can be used to search for entities or ensure the correct spelling of an entity in the ontology.

To activate auto-completion press '''Ctrl-space''' (or '''tab''').  If the preceding syntax is valid auto-completion will be activated for the current word.  If there is only one completion choice available, the auto-completer will silently complete the word.  If there is more than one choice available then a popup will be displayed with the possible completions.  The up and down arrow keys on the keyboard can be used to select the appropriate completion and then Enter/Return can be used to select the completion.

Auto-completion also works for keywords such as ```some```, ```only``` etc. and works for punctuation such as brackets.  

## Expression completion

When you have entered an expression, it will be remembered by Protege for the rest of the session.

If you start another editor and you type something that matches a previously typed expression then this match will be suggested in the editor and highlighted (see right).

You can now:

* Continue to type over the top of the suggested content (using autocomplete if you wish). It will disappear as soon as you deviate from the suggestion
* Move to the end of the suggested content for further additions or editing by pressing '''right''' or '''down'''
* Delete all of the suggested content by pressing '''backspace''' (of course it will reappear if what you are typing continues to match)
* Accept the dialog using the suggested content by pressing '''Ctrl-return'''

Protege will remember the expression as you typed it and will use this to render in the rest of the interface for the remainder of the session.
