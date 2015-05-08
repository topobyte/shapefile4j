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

import java.util.ArrayList;
import java.util.List;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.Field;

public class Database
{
	private List<Field> fields = new ArrayList<Field>();
	private List<Row> rows = new ArrayList<Row>();

	public Database(String filename)
	{
		try {
			DBF dbf = new DBF(filename, DBF.READ_ONLY);

			for (int i = 1; i <= dbf.getFieldCount(); ++i) {
				Field field = dbf.getField(i);
				fields.add(field);
			}

			for (int i = 1; i <= dbf.getRecordCount(); i++) {
				dbf.read();
				List<String> values = new ArrayList<String>();
				for (int f = 1; f <= dbf.getFieldCount(); ++f) {
					Field field = dbf.getField(f);
					values.add(field.get());
				}
				rows.add(new Row(values));
			}

			dbf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfColumns()
	{
		return fields.size();
	}

	public int getNumberOfRows()
	{
		return rows.size();
	}

	public Field getField(int index)
	{
		return fields.get(index);
	}

	public Row getRow(int index)
	{
		return rows.get(index);
	}
}
