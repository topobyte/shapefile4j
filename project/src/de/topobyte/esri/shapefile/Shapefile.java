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

package de.topobyte.esri.shapefile;

import java.io.File;

public class Shapefile
{

	private String basePath;

	public Shapefile(String basePath)
	{
		this.basePath = basePath;
	}

	public File getIndexFile()
	{
		return new File(basePath + ".shx");
	}

	public File getDatabaseFile()
	{
		return new File(basePath + ".dbf");
	}

	public File getShapefileFile()
	{
		return new File(basePath + ".shp");
	}

}
