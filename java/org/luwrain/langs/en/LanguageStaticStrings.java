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

package org.luwrain.langs.en;

import org.luwrain.core.Langs;

public class LanguageStaticStrings implements org.luwrain.core.LanguageStaticStrings
{
    public String getString(int id)
    {
	switch (id)
	{
	case Langs.SPACE:
	    return "Space";
	case Langs.TAB:
	    return "Tab";
	case Langs.BEGIN_OF_LINE:
	    return "Beginning of line";
	case Langs.END_OF_LINE:
	    return "End of line";
	case Langs.EMPTY_LINE:
	    return "Empty line";
	case Langs.THE_FIRST_LINE:
	    return "No text above";
	case Langs.THE_LAST_LINE:
	    return "No text below";
	case Langs.AREA_BEGIN:
	    return "Beginning of text";
	case Langs.AREA_END:
	    return "End of text";
	case Langs.TREE_AREA_BEGIN:
	    return "Begin of tree";
	case Langs.TREE_AREA_END:
	    return "End of tree";
	case Langs.LIST_AREA_BEGIN:
	    return "Beginning of list";
	case Langs.LIST_AREA_END:
	    return "End of list";
	case Langs.NO_REQUESTED_ACTION:
	    return "The entered action is unknown in the system";
	case Langs.NO_ACTIVE_AREA:
	    return "No active object";
	case Langs.APPLICATION_INTERNAL_ERROR:
	    return "Operation execution is interrupted due to internal application error";
	case Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP:
	    return "You have to close all popup windows before application exit";
	case Langs.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH:
	    return "No enough memory for application launch";
	case Langs.UNEXPECTED_ERROR_AT_APP_LAUNCH:
	    return "Launch is interrupted due to internal application error";
	case Langs.START_WORK_FROM_MAIN_MENU:
	    return "Start your work from the main menu!";
	case Langs.NO_LAUNCHED_APPS:
	    return "All applications are closed";
	case Langs.LIST_NO_ITEMS:
	    return "No items in list";
	case Langs.FONT_SIZE:
	    return "Font size:";
	default:
	    return "#Unknown string identifier?#";
	}
    }
}
