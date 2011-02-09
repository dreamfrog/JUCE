/*
  ==============================================================================

   This file is part of the JUCE library - "Jules' Utility Class Extensions"
   Copyright 2004-10 by Raw Material Software Ltd.

  ------------------------------------------------------------------------------

   JUCE can be redistributed and/or modified under the terms of the GNU General
   Public License (Version 2), as published by the Free Software Foundation.
   A copy of the license is included in the JUCE distribution, or can be found
   online at www.gnu.org/licenses.

   JUCE is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
   A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  ------------------------------------------------------------------------------

   To release a closed-source product which uses JUCE, commercial licenses are
   available: visit www.rawmaterialsoftware.com/juce for more information.

  ==============================================================================
*/

package com.juce;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.ClipboardManager;
import com.juce.ComponentPeerView;


//==============================================================================
public class JuceAppActivity   extends Activity
{
    //==============================================================================
    static
    {
        System.loadLibrary ("juce_jni");
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        messageHandler = new android.os.Handler();

        viewHolder = new ViewHolder (this);
        setContentView (viewHolder);

        WindowManager wm = (WindowManager) getSystemService (WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        launchApp (getApplicationInfo().publicSourceDir,
                   getApplicationInfo().dataDir,
                   display.getWidth(), display.getHeight());
    }

    @Override
    protected void onDestroy()
    {
        quitApp();
        super.onDestroy();
    }

    //==============================================================================
    public native void launchApp (String appFile, String appDataDir,
                                  int screenWidth, int screenHeight);
    public native void quitApp();

    //==============================================================================
    public static void printToConsole (String s)
    {
        System.out.println (s);
    }

    //==============================================================================
    public native void deliverMessage (long value);
    private android.os.Handler messageHandler;

    public void postMessage (long value)
    {
        messageHandler.post (new MessageCallback (this, value));
    }

    class MessageCallback  implements java.lang.Runnable
    {
        public MessageCallback (JuceAppActivity app_, long value_)
        {
            app = app_;
            value = value_;
        }

        public void run()
        {
            app.deliverMessage (value);
        }

        private JuceAppActivity app;
        private long value;
    }

    //==============================================================================
    private ViewHolder viewHolder;

    public ComponentPeerView createNewView (boolean opaque)
    {
        ComponentPeerView v = new ComponentPeerView (this, opaque);
        viewHolder.addView (v);
        return v;
    }

    public void deleteView (ComponentPeerView view)
    {
        viewHolder.removeView (view);
    }

    class ViewHolder  extends ViewGroup
    {
        public ViewHolder (Context context)
        {
            super (context);
            setDescendantFocusability (ViewGroup.FOCUS_AFTER_DESCENDANTS);
            setFocusable (false);
        }

        protected void onLayout (boolean changed, int left, int top, int right, int bottom)
        {
        }
    }

    public void excludeClipRegion (android.graphics.Canvas canvas, float left, float top, float right, float bottom)
    {
        canvas.clipRect (left, top, right, bottom, android.graphics.Region.Op.DIFFERENCE);
    }

    //==============================================================================
    public String getClipboardContent()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService (CLIPBOARD_SERVICE);
        return clipboard.getText().toString();
    }

    public void setClipboardContent (String newText)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService (CLIPBOARD_SERVICE);
        clipboard.setText (newText);
    }

    //==============================================================================
    /*class PathGrabber  extends Path
    {
        public PathGrabber()
        {
            pathString = new StringBuilder();
        }

        @Override
        public void addPath (Path src)
        {
        }

        @Override
        public void addPath (Path src, float dx, float dy)
        {
        }

        @Override
        public void close()
        {
            pathString.append ('c');
        }

        @Override
        public void moveTo (float x, float y)
        {
            pathString.append ('m');
            pathString.append (String.valueOf (x));
            pathString.append (String.valueOf (y));
        }

        @Override
        public void lineTo (float x, float y)
        {
            pathString.append ('l');
            pathString.append (String.valueOf (x));
            pathString.append (String.valueOf (y));
        }

        @Override
        public void quadTo (float x1, float y1, float x2, float y2)
        {
            pathString.append ('q');
            pathString.append (String.valueOf (x1));
            pathString.append (String.valueOf (y1));
            pathString.append (String.valueOf (x2));
            pathString.append (String.valueOf (y2));
        }

        @Override
        public void cubicTo (float x1, float y1, float x2, float y2, float x3, float y3)
        {
            pathString.append ('b');
            pathString.append (String.valueOf (x1));
            pathString.append (String.valueOf (y1));
            pathString.append (String.valueOf (x2));
            pathString.append (String.valueOf (y2));
            pathString.append (String.valueOf (x3));
            pathString.append (String.valueOf (y3));
        }

        @Override
        public void reset()
        {
            rewind();
        }

        @Override
        public void rewind()
        {
            pathString.setLength (0);
        }

        public String getJucePath()
        {
            if (getFillType() == FillType.EVEN_ODD)
                return "z" + pathString.toString();
            else
                return "n" + pathString.toString();
        }

        private StringBuilder pathString;
    }*/

    public String createPathForGlyph (Paint paint, char c)
    {
        /*PathGrabber pg = new PathGrabber();
        paint.getTextPath (String.valueOf (c), 0, 1, 0, 0, pg);
        return pg.getJucePath();*/
        return "";
    }
}