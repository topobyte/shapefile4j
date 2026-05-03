// Copyright 2026 Vladimir Alarcon, Sebastian Kürten
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

package de.topobyte.esri.shapefile.record;

import org.locationtech.jts.geom.Envelope;

import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.shape.ShapeType;

public class ShapeRecordInfo
{

	private Record record;
	private int recordNumber;
	private int contentLength;
	private ShapeType shapeType;
	private Envelope envelope;

	public ShapeRecordInfo(Record record, int recordNumber, int contentLength,
			ShapeType shapeType, Envelope envelope)
	{
		this.record = record;
		this.recordNumber = recordNumber;
		this.contentLength = contentLength;
		this.shapeType = shapeType;
		this.envelope = envelope;
	}

	public Record getRecord()
	{
		return record;
	}

	public int getRecordNumber()
	{
		return recordNumber;
	}

	public int getContentLength()
	{
		return contentLength;
	}

	public ShapeType getShapeType()
	{
		return shapeType;
	}

	public Envelope getEnvelope()
	{
		return envelope;
	}

}
