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

import  org.luwrain.core.Log;
import java.sql.SQLException;

public class Registry implements XmlReaderOutput
{
    public static final int INVALID = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    private Directory root = new Directory("");
    private VariableStorage storage;

    public boolean initWithConfFiles(String[] confFiles)
    {
	if (confFiles == null)
	    return false;
	for(String s: confFiles)
	{
	    if (s == null || s.isEmpty())
		continue;
	    Log.debug("init", "reading configuration file:" + s);
	    try {
		XmlReader reader = new XmlReader(s, this);
		reader.readFile();
	    }
	    catch(Exception e)
	    {
		Log.error("registry", "error while the config reading:" + e.getMessage());
	    return false;
	    }
	}
	return true;
    }

    public boolean initWithJdbc(java.sql.Connection jdbcCon)
    {
	if (jdbcCon == null)
	    return false;
	this.storage = new VariableStorage(jdbcCon);
	return true;
    }

    public boolean hasValue(String pathStr)
    {
	return getTypeOf(pathStr) != INVALID;
    }

    public int getTypeOf(String pathStr)
    {
	Value value = findValue(pathStr);
	return value != null?value.type:INVALID;
    }

    public int getInteger(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == INTEGER)?value.intValue:0;
    }

    public String getString(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == STRING)?value.strValue:"";
    }

    public boolean getBool(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == BOOLEAN)?value.boolValue:false;
    }

    public boolean setString(String pathStr, String value)
    {
	//FIXME:
	return false;
    }

    public boolean setStaticString(String pathStr, String value)
    {
	if (pathStr == null || pathStr.isEmpty() || value == null)
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = STRING;
	v.strValue = value;
	dir.setValue(v);
	return true;
    }

    public boolean setInteger(String pathStr, int value)
    {
	//FIXME:
	return false;
    }

    public boolean setStaticInteger(String pathStr, int value)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = INTEGER;
	v.intValue = value;
	dir.setValue(v);
	return true;
    }

    public boolean setBoolean(String pathStr, boolean value)
    {
	//FIXME:
	return false;
    }

    public boolean setStaticBoolean(String pathStr, boolean value)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = BOOLEAN;
	v.boolValue = value;
	dir.setValue(v);
	return true;
    }

    public Directory findDirectoryForXmlReader(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return null;
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null)
	return null;
	return ensureStaticDirectoryExists(path);
    }

    public boolean onNewXmlValue(Directory dir,
			      String valueName,
			      int type,
			      String valueStr)
    {
	if (valueName == null || valueName.trim().isEmpty() ||
	    dir == null || valueStr == null)
	    return false;
	Value value = new Value();
	value.type = type;
	value.name = valueName;
	switch(type)
	{
	case STRING:
	    value.strValue = valueStr;
	    break;
	case BOOLEAN:
	    if (valueStr.equals("TRUE") || valueStr.equals("True") || valueStr.equals("true"))
		value.boolValue = true; else 
		if (valueStr.equals("FALSE") || valueStr.equals("False") || valueStr.equals("false"))
		    value.boolValue = false; else
		    return false;
	    break;
	case INTEGER:
	    value.intValue = Integer.parseInt(valueStr);//FIXME:Error handling;
	    break;
	default:
	    return false;
	}
	dir.setValue(value);
	return true;
    }

    private Value findStaticValue(Path path)
    {
	if (path == null || !path.isValidAbsoluteValue())
	    return null;
	Directory dir = root;
	if (dir == null)
	    return null;
	for(String s:path.getDirItems())
	{
	    dir = dir.getSubdir(s);
	    if (dir == null)
		return null;
	}
	return dir.getValue(path.getValueName());
    }

    private VariableValue findVariableValue(Path path)
    {
	if (path == null || !path.isValidAbsoluteValue())
	    return null;
	try {
	    return storage != null?storage.getValue(path):null;
	}
	catch (SQLException e)
	{
	    Log.error("registry", "jdbc:" + e.getMessage());
	    return null;
	}
    }

    private Value findValue(Path path)
    {
	Value value = findStaticValue(path);
	if (value != null)
	    return value;
	return findVariableValue(path);
    }

    private Value findValue(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return null;
	Path path = PathParser.parse(pathStr);
	return findValue(path);
    }

    private Directory ensureStaticDirectoryExists(Path path)
    {
	if (path == null || !path.isAbsolute())
	    return null;
	Directory dir = root;
	if (dir == null)
	    dir = root = new Directory("");
	for(String s: path.getDirItems())
	{
	    if (s.trim().isEmpty())
		continue;
	    Directory d = dir.getSubdir(s);
	    if (d == null)
	    {
		d = new Directory(s);
		dir.addSubdir(d);
	    }
	    dir = d;
	}
	return dir;
    }
}
