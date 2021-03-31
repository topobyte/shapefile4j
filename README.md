# shapefile4j

This work is based on the Java ESRI Shape File Reader which is
distributed under the terms of the Apache License Version 2.0.

The source code has been copied from Sourceforge in July 2014
http://sourceforge.net/projects/javashapefilere/

## Why another shapefile library?

There's the library `org.geotools:gt-shapefile` which can be used for
working with shapefiles. If you don't want to pull in 12Mb of transitive
dependencies but rather work with a rather small library of a few dozen
kB in size, this library could be a better choice. This library has a
bunch of dependencies itself, but they accumulate to roughly 2Mb in size.

## Library

We provide access to the artifacts via our own Maven repository:

<https://mvn.topobyte.de>

The package is available at these coordinates:

<pre>
<a href="https://mvn.topobyte.de/de/topobyte/shapefile4j/0.1.0/">de.topobyte:shapefile4j:0.1.0</a>
</pre>
