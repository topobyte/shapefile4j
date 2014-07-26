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

import de.topobyte.esri.shapefile.Shapefile;
import de.topobyte.esri.shapefile.ShapefileAccess;
import de.topobyte.esri.shapefile.dbf.Database;
import de.topobyte.esri.shapefile.dbf.DatabaseUtil;

public class ShowFields
{
	public static void main(String[] args)
	{
		if (args.length != 1) {
			System.out.println("usage: " + ShowFields.class.getSimpleName()
					+ " <shapefile>");
			System.exit(1);
		}

		String filename = args[0];
		Shapefile shapefile = new Shapefile(filename);
		ShapefileAccess sa = new ShapefileAccess(shapefile);

		Database database = sa.getDatabase();
		DatabaseUtil.printFields(database);
	}
}
