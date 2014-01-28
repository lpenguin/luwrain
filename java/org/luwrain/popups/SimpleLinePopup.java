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

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class SimpleLinePopup implements Area, PopupClosingRequest
{
    public PopupClosing closing = new PopupClosing(this);
    private SingleLineEdit edit;
    private Object instance;
    private String name;
    private String prefix;
    private String text;
    private int pos;

    public SimpleLinePopup(Object instance,
			    String name,
			    String prefix,
			    String text)
    {
	this.instance = instance;
	this.name = name;
	this.prefix = prefix;
	if (this.prefix == null || this.prefix.isEmpty())
	    this.prefix = "???:";
	this.text = text;
	this.pos = prefix.length() + text.length() + 1;
	createEdit();
    }

    public int getLineCount()
    {
	return 1;
    }

    public String getLine(int index)
    {
	if (index != 0)
	    return new String();
	return prefix + " " + text;
    }

    public int getHotPointX()
    {
	return pos;
    }

    public int getHotPointY()
    {
	return 0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (event.isCommand() && !event.isModified())
	{
	    final int cmd = event.getCommand();

	    //Left;
	    if (cmd == KeyboardEvent.ARROW_LEFT)
	    {
		if (pos == 0)
		{
		    Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		    return true;
		}
		pos--;
		Dispatcher.onAreaNewHotPoint(this);
		if (pos < prefix.length())
		    Speech.sayLetter(prefix.charAt(pos)); else
		    if (pos == prefix.length())
			Speech.sayLetter(' '); else
			Speech.sayLetter(text.charAt(pos - prefix.length() - 1));
		return true;
	    }

	    //Right;
	    if (cmd == KeyboardEvent.ARROW_RIGHT)
	    {
		if (pos >= prefix.length() + text.length() + 1)
		{
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		    return true;
		}
		pos++;
		Dispatcher.onAreaNewHotPoint(this);
		if (pos < prefix.length())
		    Speech.sayLetter(prefix.charAt(pos)); else
		    if (pos == prefix.length())
			Speech.sayLetter(' '); else
			if (pos == prefix.length() + text.length() + 1)
			    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH); else
			    Speech.sayLetter(text.charAt(pos - prefix.length() - 1));
		return true;
	    }

	    //Home;
	    if (cmd == KeyboardEvent.HOME)
	    {
		pos = 0;
		if (prefix.isEmpty())
		    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH); else
		    Speech.sayLetter(prefix.charAt(0));
		Dispatcher.onAreaNewHotPoint(this);
		return true;
	    }

	    //End;
	    if (cmd == KeyboardEvent.END)
	    {
		pos = prefix.length() + text.length() + 1;
		Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		Dispatcher.onAreaNewHotPoint(this);
		return true;
	    }
	}

	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    closing.doOk();
	    return true;
	}

	if (pos < prefix.length() + 1)
	    return false;
	return edit.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return closing.onEnvironmentEvent(event);
    }

    public String getName()
    {
	return name;
    }

    private void createEdit()
    {
	final SimpleLinePopup thisArea = this;
	edit = new SingleLineEdit(new SingleLineEditModel(){
		private SimpleLinePopup area = thisArea;
		public String getLine()
		{
		    return area.text;
		}
		public void setLine(String text)
		{
		    area.text = text;
		    Dispatcher.onAreaNewContent(area);
		}
		public int getHotPointX()
		{
		    if (area.pos < area.prefix.length() + 1)
			return 0;
		    return area.pos - area.prefix.length() - 1;
		}
		public void setHotPointX(int value)
		{
		    area.pos = area.prefix.length() + value + 1;
		    Dispatcher.onAreaNewHotPoint(area);
		}
		public String getTabSeq()
		{
		    return "\t";//FIXME:
		}
	    });
    }

    public String getText()
    {
	return text;
    }

    public boolean onOk()
    {
	return true;
    }

    public boolean onCancel()
    {
	return true;
    }
}
