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

public enum PartType {

	TRIANGLE_STRIP(0), //
	TRIANGLE_FAN(1), //
	OUTER_RING(2), //
	INNER_RING(3), //
	FIRST_RING(4), //
	RING(5); //

	private int id;

	private PartType(int id)
	{
		this.id = id;
	}

	// parse

	public static PartType parse(final int tid)
	{
		for (PartType st : PartType.values()) {
			if (st.getId() == tid) {
				return st;
			}
		}
		return null;
	}

	// Getters

	/**
	 * Returns the part type's numeric ID, as defined by the ESRI specification.
	 * 
	 * @return
	 */
	public int getId()
	{
		return this.id;
	}

}
