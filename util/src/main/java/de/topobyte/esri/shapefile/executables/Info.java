// Copyright 2026 Sebastian Kürten
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.locationtech.jts.geom.Envelope;

import de.topobyte.esri.shapefile.ShapeIndexReader;
import de.topobyte.esri.shapefile.ShapeRecordInfoReader;
import de.topobyte.esri.shapefile.Shapefile;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.header.ShapeFileHeader;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.record.ShapeRecordInfo;
import de.topobyte.various.utils.SizeFormatter;

public class Info
{

	public static void main(String[] args)
			throws IOException, InvalidShapeFileException
	{
		if (args.length != 1) {
			System.out.println(
					"usage: " + Info.class.getSimpleName() + " <shapefile>");
			System.exit(1);
		}

		String filename = Util.normalizeBasePath(args[0]);
		Shapefile shapefile = new Shapefile(filename);

		List<Record> records;
		ShapeFileHeader indexHeader;
		try (InputStream input = new FileInputStream(
				shapefile.getIndexFile())) {
			ShapeIndexReader reader = new ShapeIndexReader(input);
			reader.read();
			indexHeader = reader.getHeader();
			records = reader.getRecords();
		}

		int shapefileRecordCount = 0;
		ShapeFileHeader shapefileHeader;
		Envelope computedEnvelope = new Envelope();
		try (InputStream input = new FileInputStream(
				shapefile.getShapefileFile())) {
			ShapeRecordInfoReader reader = new ShapeRecordInfoReader(input,
					records);
			shapefileHeader = reader.getHeader();
			ShapeRecordInfo info;
			while ((info = reader.next()) != null) {
				shapefileRecordCount++;
				Envelope envelope = info.getEnvelope();
				if (envelope != null) {
					computedEnvelope.expandToInclude(envelope);
				}
			}
		}

		System.out.println("shp: " + formatFile(shapefile.getShapefileFile()));
		System.out.println("shx: " + formatFile(shapefile.getIndexFile()));
		System.out.println("dbf: " + formatFile(shapefile.getDatabaseFile()));
		System.out.println("records in index: " + records.size());
		System.out
				.println("records in shapefile scan: " + shapefileRecordCount);
		System.out.println(
				"header bbox: " + formatEnvelope(toEnvelope(shapefileHeader)));
		System.out.println("scan bbox:   " + formatEnvelope(computedEnvelope));
		System.out.println("shape type: " + indexHeader.getShapeType());
	}

	private static String formatFile(File file)
	{
		if (!file.exists()) {
			return file.getName() + ": missing";
		}
		return file.getName() + ": "
				+ new SizeFormatter().format(file.length());
	}

	private static Envelope toEnvelope(ShapeFileHeader header)
	{
		return new Envelope(header.getBoxMinX(), header.getBoxMaxX(),
				header.getBoxMinY(), header.getBoxMaxY());
	}

	private static String formatEnvelope(Envelope envelope)
	{
		return String.format("%f,%f:%f,%f", envelope.getMinX(),
				envelope.getMaxY(), envelope.getMaxX(), envelope.getMinY());
	}

}
