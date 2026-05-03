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
<a href="https://mvn.topobyte.de/de/topobyte/shapefile4j/0.1.2/">de.topobyte:shapefile4j:0.1.2</a>
</pre>

## Executables

This repository also ships a small set of command line tools from the
`util` module.

To build and install the executables, run:

```bash
./install.sh
```

This builds the distribution and runs the generated installer from
`util/build/setup/`.

The installer creates symlinks for the commands in:

```bash
$HOME/bin
```

Make sure that directory is part of your `PATH`, for example:

```bash
export PATH="$PATH:$HOME/bin"
```

The following commands are installed:

- `ShapefileInfo`
  show file sizes, record counts and bounding box information
- `ShapefileShowFields`
  print the DBF field definitions
- `ShapefileDumpData`
  print all rows from the associated DBF table
- `ShapefileToSmxCollection`
  convert all geometries plus DBF attributes into a directory of SMX files
