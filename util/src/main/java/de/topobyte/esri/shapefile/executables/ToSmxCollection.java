// Copyright 2014 Vladimir Alarcon, Sebastian Kürten
//
// This file is part of shapefile4j.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package de.topobyte.esri.shapefile.executables;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.xBaseJ.fields.Field;

import de.topobyte.esri.shapefile.Shapefile;
import de.topobyte.esri.shapefile.ShapefileAccess;
import de.topobyte.esri.shapefile.dbf.Database;
import de.topobyte.esri.shapefile.dbf.Row;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.simplemapfile.core.EntityFile;
import de.topobyte.simplemapfile.xml.SmxFileWriter;

public class ToSmxCollection
{

	public static void main(String[] args)
	{
		if (args.length != 2) {
			System.out.println("usage: " + ToSmxCollection.class.getSimpleName()
					+ " <shapefile> <output directory>");
			System.exit(1);
		}

		String pathInput = Util.normalizeBasePath(args[0]);
		String pathOutput = args[1];

		File dir = new File(pathOutput);
		dir.mkdirs();

		if (!(dir.exists() && dir.isDirectory() && dir.canWrite())) {
			System.out.println("Unable to write output directory for writing");
			System.exit(1);
		}

		if (dir.listFiles().length > 0) {
			System.out.println("Directory is not empty");
			System.exit(1);
		}

		try {
			convertAll(pathInput, dir);
		} catch (InvalidShapeFileException | IOException e) {
			System.out.println("Error while processing shape data ("
					+ e.getClass().getSimpleName() + "): " + e.getMessage());
			System.exit(1);
		}
	}

	private static void convertAll(String pathInput, File dir)
			throws InvalidShapeFileException, IOException
	{
		Shapefile shapefile = new Shapefile(pathInput);
		ShapefileAccess sa = new ShapefileAccess(shapefile);

		Database database = sa.getDatabase();

		List<Record> records = sa.getRecords();

		int digits = 1;
		double n = Math.log10(records.size());
		if (!Double.isInfinite(n) && !Double.isNaN(n)) {
			digits = (int) Math.ceil(n);
		}

		String pattern = String.format("object-%%0%dd.smx", digits);

		int i = 0;
		for (Record record : records) {
			Geometry geometry = sa.getGeometry(record);

			EntityFile entityFile = new EntityFile();
			entityFile.setGeometry(geometry);
			Row row = database.getRow(i);
			for (int k = 0; k < database.getNumberOfColumns(); k++) {
				Field field = database.getField(k);
				String value = row.getValue(k).trim();
				entityFile.addTag(field.getName(), value);
			}
			File file = new File(dir, String.format(pattern, i));
			try {
				SmxFileWriter.write(entityFile, file);
			} catch (Exception e) {
				System.out.println("Error while writing output file '"
						+ file.getAbsolutePath() + "' ("
						+ e.getClass().getSimpleName() + "): "
						+ e.getMessage());
			}
			i++;
		}
	}

}
