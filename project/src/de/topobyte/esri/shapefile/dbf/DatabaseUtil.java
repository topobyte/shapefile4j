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

package de.topobyte.esri.shapefile.dbf;

import org.xBaseJ.fields.Field;

public class DatabaseUtil
{

	public static void printFields(Database database)
	{
		for (int k = 0; k < database.getNumberOfColumns(); k++) {
			Field field = database.getField(k);
			System.out.println("Field " + k + ": " + field.getName()
					+ ", type=" + field.getType() + ", len="
					+ field.getLength());
		}
	}

	public static void printRows(Database database)
	{
		for (int k = 0; k < database.getNumberOfRows(); k++) {
			System.out.println("Record " + k);
			Row row = database.getRow(k);
			for (int i = 0; i < database.getNumberOfColumns(); i++) {
				Field field = database.getField(i);
				String content = row.getValue(i);
				System.out.println(field.getName() + " = " + content.trim());
			}
		}
	}

}
