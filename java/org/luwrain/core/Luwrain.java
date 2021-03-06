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

package org.luwrain.core;

//No input data checking here, everything in Environment class

import java.io.File;

public class Luwrain
{
    static private Environment environment;

    public static boolean setEnvironmentObject(Environment e)
    {
	if (e == null)
	    return false;
	if (environment != null)
	{
	    Log.error("environment", "environment object for the Luwrain class is already assigned, somebody tries to set another one and that is suspicious");
	    return false;
	}
	environment = e;
	return true;
    }

    static public void enqueueEvent(Event e)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.enqueueEvent(e);
    }

    static public void launchApp(Application app)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.launchApp(app);
    }

static public     void closeApp(Object instance)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.closeApp(instance);
    }

    //Not for popup areas, only standard areas of applications;
    //Introduces new area in contrast with onAreaNewContent, onAreaNewHotPoint and onAreaNewName  
    static public void setActiveArea(Object instance, Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.setActiveArea(instance, area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewHotPoint(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewHotPoint(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewContent(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewContent(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewName(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewName(area);
    }

    //May return -1 if area is not shown on the screen;
    static public int getAreaVisibleHeight(Area area)
    {
	if (!checkEnvironmentInstance())
	    return -1;
	return environment.getAreaVisibleHeight(area);
    }

    static public void quit()
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.quit();
    }

    static public void message(String text)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.message(text);
    }

    /**
     * @param name The desired popup name, can be null if default value is required
     * @param prefix The desired input prefix, can be null if default value is required
     * @param defaultValue The desired default value, can be null to use the user home directory path
     */
    static public File openPopup(Object instance,
				 String name,
				 String prefix,
				 File defaultValue)
    {
	if (!checkEnvironmentInstance())
	    return null;
	return environment.openPopup(instance, name, prefix, defaultValue);
    }

    static public void openFile(String fileName)
    {
	if (!checkEnvironmentInstance())
	    return;
	String[] s = new String[1];
	s[0] = fileName;
	environment.openFiles(s);
    }

    static public void openFiles(String[] fileNames)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.openFiles(fileNames);
    }

    static public void popup(Popup popup)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.popup(popup);
    }


    static public org.luwrain.core.registry.Registry getRegistry()
    {
	if (!checkEnvironmentInstance())
	    return null;
	return environment.getRegistry();
    }

    public static org.luwrain.pim.PimManager getPimManager()
    {
	if (!checkEnvironmentInstance())
	    return null;
	return environment.getPimManager();
    }

    public static void setClipboard(String[] value)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.setClipboard(value);
    }

    public static String[] getClipboard()
    {
	if (!checkEnvironmentInstance())
	    return null;
	return environment.getClipboard();
    }

    static private boolean checkEnvironmentInstance()
    {
	if (environment != null)
	    return true;
	Log.error("environment", "no default environment object, probably Luwrain was not properly initialized");
	return false;
    }
}
