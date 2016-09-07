| **master** | [![Build Status](https://travis-ci.org/jayhorn/jayhorn.svg?branch=master)](https://travis-ci.org/jayhorn/jayhorn) | [![Coverage Status](https://coveralls.io/repos/jayhorn/jayhorn/badge.svg?branch=master&service=github)](https://coveralls.io/github/jayhorn/jayhorn?branch=master) | [![Coverity Scan](https://scan.coverity.com/projects/6013/badge.svg)](https://scan.coverity.com/projects/6013) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/28a133096dce4f4396f27459af39afd4)](https://www.codacy.com/app/martinschaef/jayhorn?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jayhorn/jayhorn&amp;utm_campaign=Badge_Grade) |
| --- | --- | --- | --- | --- |
| **devel** | [![Build Status](https://travis-ci.org/jayhorn/jayhorn.svg?branch=devel)](https://travis-ci.org/jayhorn/jayhorn) | [![Coverage Status](https://coveralls.io/repos/jayhorn/jayhorn/badge.svg?branch=devel&service=github)](https://coveralls.io/github/jayhorn/jayhorn?branch=devel) | | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/28a133096dce4f4396f27459af39afd4)](https://www.codacy.com/app/martinschaef/jayhorn?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jayhorn/jayhorn&amp;utm_campaign=Badge_Grade) | 


<img src="http://jayhorn.github.io/jayhorn/images/rhino.png" height=100> Java and Horn clauses

JayHorn is a software model checking tool for Java. JayHorn tries to find a proof that certain bad states in a Java program are never reachable. These bad states are specified by adding runtime assertions (where some assertions may be generated, e.g., that an object reference must not be Null before being accessed). 


JayHorn tries to err on the side of precision that is, when it is not able to proof that an assertion always holds, it will claim that the assertion may be violated (this is called soundness). JayHorn is currently sound (modulo bugs) for Java that use a single thread, have no dynamic class loading, and do not perform complex operations in static initializers.

For information on how to download and run JayHorn check [our website](http://jayhorn.github.io/jayhorn/). For information on how JayHorn is implemented check
[our JayHorn development blog](http://jayhorn.github.io/jayhorn/jekyll/2016/08/01/model-checking-java/).


Join the chat [![Join the chat at https://gitter.im/jayhorn/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/jayhorn/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Waffel.io [![Stories in Ready](https://badge.waffle.io/jayhorn/jayhorn.svg?label=ready&title=Ready)](http://waffle.io/jayhorn/jayhorn)
[![Throughput Graph](https://graphs.waffle.io/jayhorn/jayhorn/throughput.svg)](https://waffle.io/jayhorn/jayhorn/metrics/throughput)
