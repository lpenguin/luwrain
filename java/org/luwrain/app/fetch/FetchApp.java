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

package org.luwrain.app.fetch;

import org.luwrain.core.*;

public class FetchApp implements Application, Actions
{
    private Object instance;
    private StringConstructor stringConstructor;
    private FetchArea fetchArea;
    private FetchThread fetchThread ;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("fetch");
	if (o == null)
	{
	    Log.error("fetch", "no string constructor for fetch application");
	    return false;
	}
	stringConstructor = (StringConstructor)o;
	fetchArea = new FetchArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public void launchFetching()
    {
	if (fetchThread != null && !fetchThread.done)
	{
	    Luwrain.message(stringConstructor.processAlreadyRunning());
	    return;
	}
	fetchArea.clear();
	fetchThread = new FetchThread(stringConstructor, fetchArea);
	Thread t = new Thread(fetchThread);
	t.start();
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(fetchArea);
    }

    public void close()
    {
	if (fetchThread != null && !fetchThread.done)
	{
	    Luwrain.message(stringConstructor.processNotFinished());
	    return;
	}
	Luwrain.closeApp(instance);
    }
}
