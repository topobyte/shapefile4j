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

package de.topobyte.esri.shapefile.header;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.util.ISUtil;

public class ShapeFileHeader
{

	private static final int SHAPE_FILE_CODE = 9994;
	private static final int SHAPE_FILE_VERSION = 1000;

	private int fileCode;

	private int unused0;
	private int unused1;
	private int unused2;
	private int unused3;
	private int unused4;

	private int fileLength;
	private int version;
	private ShapeType shapeType;

	private double boxMinX;
	private double boxMinY;
	private double boxMaxX;
	private double boxMaxY;

	private double boxMinZ;
	private double boxMaxZ;

	private double boxMinM;
	private double boxMaxM;

	public ShapeFileHeader(final InputStream is)
			throws IOException, InvalidShapeFileException
	{

		try {
			this.fileCode = ISUtil.readBeInt(is);
			if (this.fileCode != SHAPE_FILE_CODE) {
				throw new InvalidShapeFileException(
						"Invalid shape file code. Found " + this.fileCode
								+ " but expected " + SHAPE_FILE_CODE + ".");
			}

			this.unused0 = ISUtil.readBeInt(is);
			this.unused1 = ISUtil.readBeInt(is);
			this.unused2 = ISUtil.readBeInt(is);
			this.unused3 = ISUtil.readBeInt(is);
			this.unused4 = ISUtil.readBeInt(is);

			this.fileLength = ISUtil.readBeInt(is);
			this.version = ISUtil.readLeInt(is);
			if (this.version != SHAPE_FILE_VERSION) {
				throw new InvalidShapeFileException(
						"Invalid shape file version. Found " + this.version
								+ " but expected " + SHAPE_FILE_VERSION + ".");
			}

			int shapeTypeId = ISUtil.readLeInt(is);
			this.shapeType = ShapeType.parse(shapeTypeId);
			if (this.shapeType == null) {
				throw new InvalidShapeFileException("Invalid shape file. "
						+ "The header's shape type has the invalid code "
						+ shapeTypeId + ".");
			}

			this.boxMinX = ISUtil.readLeDouble(is);
			this.boxMinY = ISUtil.readLeDouble(is);
			this.boxMaxX = ISUtil.readLeDouble(is);
			this.boxMaxY = ISUtil.readLeDouble(is);

			this.boxMinZ = ISUtil.readLeDouble(is);
			this.boxMaxZ = ISUtil.readLeDouble(is);

			this.boxMinM = ISUtil.readLeDouble(is);
			this.boxMaxM = ISUtil.readLeDouble(is);

		} catch (EOFException e) {
			throw new InvalidShapeFileException(
					"Unexpected end of stream. " + "The content is too short. "
							+ "It doesn't even have a complete header.");
		}
	}

	// Getters

	public int getFileCode()
	{
		return fileCode;
	}

	public int getUnused0()
	{
		return unused0;
	}

	public int getUnused1()
	{
		return unused1;
	}

	public int getUnused2()
	{
		return unused2;
	}

	public int getUnused3()
	{
		return unused3;
	}

	public int getUnused4()
	{
		return unused4;
	}

	public int getFileLength()
	{
		return fileLength;
	}

	public int getVersion()
	{
		return version;
	}

	public ShapeType getShapeType()
	{
		return shapeType;
	}

	public void setShapeType(ShapeType shapeType)
	{
		this.shapeType = shapeType;
	}

	public double getBoxMinX()
	{
		return boxMinX;
	}

	public double getBoxMinY()
	{
		return boxMinY;
	}

	public double getBoxMaxX()
	{
		return boxMaxX;
	}

	public double getBoxMaxY()
	{
		return boxMaxY;
	}

	public double getBoxMinZ()
	{
		return boxMinZ;
	}

	public double getBoxMaxZ()
	{
		return boxMaxZ;
	}

	public double getBoxMinM()
	{
		return boxMinM;
	}

	public double getBoxMaxM()
	{
		return boxMaxM;
	}

}
