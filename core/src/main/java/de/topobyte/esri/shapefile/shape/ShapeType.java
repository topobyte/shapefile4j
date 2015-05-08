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

package de.topobyte.esri.shapefile.shape;

public enum ShapeType {

	NULL(0), //

	POINT(1), //
	POLYLINE(3), //
	POLYGON(5), //
	MULTIPOINT(8), //

	POINT_Z(11), //
	POLYLINE_Z(13), //
	POLYGON_Z(15), //
	MULTIPOINT_Z(18), //

	POINT_M(21), //
	POLYLINE_M(23), //
	POLYGON_M(25), //
	MULTIPOINT_M(28), //

	MULTIPATCH(31); //

	private int id;

	private ShapeType(int id)
	{
		this.id = id;
	}

	// parse

	public static ShapeType parse(final int tid)
	{
		for (ShapeType st : ShapeType.values()) {
			if (st.getId() == tid) {
				return st;
			}
		}
		return null;
	}

	// Getters

	/**
	 * Returns the shape type's numeric ID, as defined by the ESRI
	 * specification.
	 * 
	 * @return
	 */
	public int getId()
	{
		return this.id;
	}

}
