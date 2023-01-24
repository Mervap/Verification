# Verifier

CTL on subformulas verifier

![Test](https://github.com/Mervap/Verification/actions/workflows/verifier_test.yml/badge.svg)

## Links

* [Test Coverage](https://mervap.github.io/Verification/verifier/coverage/)
* [Static analysis report](https://mervap.github.io/Verification/verifier/analysis/)


## How to use

1. Build using `./gradlew shadowJar`. It will create `verifier.jar` file in project root.
   The latest version is already built, and you can find it there.
2. Run using `java -jar verifier.jar <file with model> <file with ctl formula> [<output file>]`


The example for sample from the assignment
```shell
java -jar verifier.jar src/test/kotlin/samples/model.xml src/test/kotlin/samples/formula.txt src/test/kotlin/samples/res.txt

```

We did not support feature of making counter examples for failing formulas, because we needed to support Fair CTL with a little bit different semantics and more rules, than original CTL


Sources:
* [textbook](https://books.ifmo.ru/file/pdf/805.pdf) The textbook on Verification
* [parser-generator](https://github.com/h0tk3y/better-parse) Lib with support of parser generators in kotlin
* []
