# Analyzer

![Test](https://github.com/Mervap/Verification/actions/workflows/analyzer_test.yml/badge.svg)

This analyzer is designed to analyze projects in `Java`.

## Links

* [Test Coverage](https://mervap.github.io/Verification/analyzer/coverage/)
* [Static analysis report](https://mervap.github.io/Verification/analyzer/analysis/)

## How to use 

1. Build using `./gradlew shadowJar`. It will create `analyzer.jar` file in project root.
The latest version is already built, and you can found it there.
2. Run using `java -jar analyzer.jar <ARGUMENTS>`

Supported arguments:

* `-h`/`--help` — show help message
* `ast FILEPATH` — prints `AST` for given java file
* `analyze PROJECTDIR` — prints information about founded problems for all java files in project. 
**Limitations:** at least one file should be in right directory (according to package) and should not contain parser errors.

You can found sample project in `sample-project` directory.
```shell
java -jar analyzer.jar analyze sample-project
```

## Stack

* [javaparser](https://github.com/javaparser/javaparser). This lib provides parser for java files and utilities t
traverse `AST`.
* [kotlinx-kover](https://github.com/Kotlin/kotlinx-kover). Gradle plugin for Kotlin code coverage
* [Qodana](https://www.jetbrains.com/qodana/). Platform for static analysis for a bunch of popular languages 