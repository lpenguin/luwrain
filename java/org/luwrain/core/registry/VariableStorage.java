/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core.registry;

import java.util.ArrayList;
import java.sql.*;

public class VariableStorage
{
    private Connection con;

    public VariableStorage(Connection con)
    {
	this.con = con;
    }

    public boolean exists(Path path) throws SQLException
    {
	if (path == null)
	    return false;
	if (path.isDir())
	{
	    if (!path.isValidAbsoluteDir())
		return false;
return followPathToDir(path) != null;
	} else
	{
	    if (path.isValidAbsoluteValue())
		return false;
return followPathToValue(path) != null;
	}
    }

    public VariableValue getValue(Path path) throws SQLException
    {
	if (path == null || path.isDir() || !path.isValidAbsoluteValue())
	    return null;
	return followPathToValue(path);
    }

    public boolean rename(Path path, String newName) throws SQLException
    {
	if (path == null || newName == null || newName.trim().isEmpty())
	    return false;
	if (path.isDir())
	{
	    if (!path.isValidAbsoluteDir())
		return false;
	    VariableDirectory dir = followPathToDir(path);
	    if (dir == null)
		return false;
	    dir.name = newName;
	    return dir.update();
	} else
	{
	    if (!path.isValidAbsoluteValue())
		return false;
	    VariableValue value = followPathToValue(path);
	    if (value == null)
		return false;
	    value.name = newName;
	    return value.update();
	}
    }

    public boolean addDir(Path path) throws SQLException
    {
	if (path == null || !path.isValidAbsoluteDir() || path.isRootDir())
	    return false;
	VariableDirectory parentDir = followPathToDir(path.getParentOfDir());
	if (parentDir == null)
	    return false;
	VariableDirectory dir = new VariableDirectory(con);
	dir.parentId = parentDir.id;
	dir.name = path.getLastDirItem();
	return dir.insert();
    }

    public boolean addValue(Path path) throws SQLException
    {
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	VariableDirectory parentDir = followPathToDir(path);
	VariableValue value = new VariableValue(con); 
	value.dirId = parentDir.id;
	value.name = path.getValueName();
	return value.insert();
    }

    public boolean remove(Path path) throws SQLException
    {
	if (path == null)
	    return false;
	if (path.isDir())
	{
	    if (!path.isValidAbsoluteDir() || path.isRootDir())
		return false;
	    VariableDirectory dir = followPathToDir(path);
	    if (dir == null)
		return false;
	    return dir.delete();
	} else
	{
	    if (!path.isValidAbsoluteValue())
		return false;
	    VariableValue value = followPathToValue(path);
	    if (value == null)
		return false;
	    return value.delete();
	}
    }

    public VariableDirectory[] getSubdirs(Path path) throws SQLException
    {
	if (path == null || !path.isValidAbsoluteDir())
	    return null;
	VariableDirectory dir = followPathToDir(path);
	if (dir == null)
	    return null;
	return dir.selectAllSubdirs();
    }

    public VariableValue[] getValues(Path path) throws SQLException
    {
	if (path == null || !path.isValidAbsoluteDir())
	    return null;
	VariableDirectory dir = followPathToDir(path);
	if (dir == null)
	    return null;
	return VariableValue.selectAllInDir(con, dir.id);
    }

    private VariableDirectory followPathToDir(Path path) throws SQLException
    {
	if (path == null || !path.isAbsolute())
	    return null;
	VariableDirectory dir = VariableDirectory.selectRootDir(con);
	if (dir == null)
	    return null;
	for(String s: path.getDirItems())
	{
	    dir = dir.selectSubdirByName(s);
	    if (dir == null)
		return null;
	}
	return dir;
    }

    private VariableValue followPathToValue(Path path) throws SQLException
    {
	if (path == null || !path.isAbsolute() || path.getValueName().trim().isEmpty())
	    return null;
	VariableDirectory dir = followPathToDir(path);
	if (dir == null)
	    return null;
	return VariableValue.selectByName(con, dir.id, path.getValueName());
    }
}
