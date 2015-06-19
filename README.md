# Configurable-iText
This project makes creating and styling iText reports a matter of configuration instead of coding.

You can configure:

- how your data classes are turned into iText building blocks
  - using xml configuration (xsd available)
  - using annotations
- how the iText building blocks are styled
  - using a stylesheet (which you can build using a JavaFX GUI)

The library offers syntax independence for stylesheets, two built in syntaxes are provided: a fast specific syntax and JSON.

In a stylesheet you can define style, but also conditions that determine when to style.

The project offers a firebug like debug mode that helps you optimize styling.

