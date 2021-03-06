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

//TODO:Popups without known applications;

import org.luwrain.core.events.*;
import org.luwrain.mmedia.*;

class ScreenContentManager
{
    public static final int NO_APPLICATIONS = 0;
    public static final int EVENT_NOT_PROCESSED = 1;
    public static final int EVENT_PROCESSED = 2;

    private ApplicationRegistry applications;
    private PopupManager popups;
    private Application systemApp;
    private boolean activePopup = false;

    public ScreenContentManager(ApplicationRegistry applications,
				PopupManager popups,
Application systemApp)
    {
	this.applications = applications;
	this.popups = popups;
	this.systemApp = systemApp;
    }

    public  int onKeyboardEvent(KeyboardEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onKeyboardEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea == null && hasProperPopup())
	{
	    activePopup = true;
	    activeArea = popups.getAreaOfLastPopup();
	}
	if (activeArea != null)
	    return activeArea.onKeyboardEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	return NO_APPLICATIONS;
    }

    public  int onEnvironmentEvent(EnvironmentEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onEnvironmentEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea == null && hasProperPopup())
	{
	    activePopup = true;
	    activeArea = popups.getAreaOfLastPopup();
	}
	if (activeArea != null)
	    return activeArea.onEnvironmentEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	return NO_APPLICATIONS;
    }

    public boolean setPopupAreaActive()
    {
	if (!hasProperPopup())
	    return false;
	activePopup = true;
	return true;
    }

    public boolean setPopupInactive()
    {
	if (applications.getActiveAreaOfActiveApp() == null)
	    return !hasProperPopup();
	activePopup = false;
	return false;
    }

    public boolean isPopupAreaActive()
    {
	if (!hasProperPopup())
	{
	    activePopup = false;
	    return false;
	}
	return activePopup;
    }

    public void updatePopupState()
    {
	if (activePopup)
	{
	    if (!hasProperPopup())
		activePopup = false;
	} else
	{
	    if (applications.getActiveAreaOfActiveApp() == null && hasProperPopup())
		activePopup = true;
	}
    }

    public Area getActiveArea()
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup();
	    activePopup = false;
	}
return applications.getActiveAreaOfActiveApp();
    }

    public void activateNextArea()
    {
	Area activeArea = getActiveArea();
	if (activeArea == null)
	    return;
	Object[] objs = getWindows().getObjects();
	Window[] windows = new Window[objs.length];
	for(int i = 0;i < objs.length;++i)
	    windows[i] = (Window)objs[i];
	if (windows == null || windows.length <= 0)
	{
	    activePopup = hasProperPopup();
	    return;
	}
	int index;
	for(index = 0;index < windows.length;index++)
	    if (windows[index].area == activeArea)
		break;
	index++;
	if (index >= windows.length)
	    index = 0;
	activePopup = windows[index].popup;
	if (!activePopup)
	{
	    applications.setActiveAreaOfApp(windows[index].app, windows[index].area);
	    applications.setActiveApp(windows[index].app);
	}
    }

    TileManager getWindows()
    {
	TileManager windows = new TileManager();
	Application[] visibleApps = applications.getVisibleApps();
	windows.createHorizontally(visibleApps);
	for(int i = 0;i < visibleApps.length;i++)
	    windows.replace(visibleApps[i], constructWindowLayoutOfApp(visibleApps[i]));
	if (hasProperPopup())
	{
	    Window popupWindow = new Window(popups.getAppOfLastPopup(), popups.getAreaOfLastPopup(), popups.getPositionOfLastPopup());
	    switch(popupWindow.popupPlace)
	    {
	    case PopupManager.BOTTOM:
		windows.addBottom(popupWindow);
		break;
	    case PopupManager.TOP:
		windows.addTop(popupWindow);
		break;
	    case PopupManager.LEFT:
		windows.addLeftSide(popupWindow);
		break;
	    case PopupManager.RIGHT:
		windows.addRightSide(popupWindow);
		break;
	    }
	}
	return windows;
    }

    private boolean hasProperPopup()
    {
	return popups.hasPopups() && (applications.isVisibleApp(popups.getAppOfLastPopup()) || popups.getAppOfLastPopup() == systemApp);
    }

    private TileManager constructWindowLayoutOfApp(Application app)
    {
	if (app == null)
	    return null;
	AreaLayout layout = app.getAreasToShow();
	TileManager tiles = new TileManager();
	switch(layout.getType())
	{
	case AreaLayout.SINGLE:
	    tiles.createSingle(new Window(app, layout.getArea1()));
	    break;
	case AreaLayout.LEFT_RIGHT:
	    tiles.createLeftRight(new Window(app, layout.getArea1()),
				  new Window(app, layout.getArea2()));
	    break;
	case AreaLayout.TOP_BOTTOM:
	    tiles.createTopBottom(new Window(app, layout.getArea1()),
				  new Window(app, layout.getArea2()));
	    break;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    tiles.createLeftTopBottom(new Window(app, layout.getArea1()),
				      new Window(app, layout.getArea2()),
				      new Window(app, layout.getArea3()));
	    break;
	case AreaLayout.LEFT_RIGHT_BOTTOM:
	    tiles.createLeftRightBottom(new Window(app, layout.getArea1()),
					new Window(app, layout.getArea2()),
					new Window(app, layout.getArea3()));
	    break;
	}
	return tiles;
    }
}
