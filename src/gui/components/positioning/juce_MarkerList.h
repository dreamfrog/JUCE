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

#ifndef __JUCE_MARKERLIST_JUCEHEADER__
#define __JUCE_MARKERLIST_JUCEHEADER__

#include "../../../containers/juce_ValueTree.h"
#include "../positioning/juce_RelativeCoordinate.h"


//==============================================================================
/**
    Holds a set of named marker points along a one-dimensional axis.

    This class is used to store sets of X and Y marker points in components.
    @see Component::getMarkers().
*/
class JUCE_API  MarkerList
{
public:
    //==============================================================================
    /** Creates an empty marker list. */
    MarkerList();
    /** Creates a copy of another marker list. */
    MarkerList (const MarkerList& other);
    /** Copies another marker list to this one. */
    MarkerList& operator= (const MarkerList& other);
    /** Destructor. */
    ~MarkerList();

    //==============================================================================
    /** Represents a marker in a MarkerList. */
    class JUCE_API  Marker
    {
    public:
        /** Creates a copy of another Marker. */
        Marker (const Marker& other);
        /** Creates a Marker with a given name and position. */
        Marker (const String& name, const RelativeCoordinate& position);

        /** The marker's name. */
        String name;

        /** The marker's position. */
        RelativeCoordinate position;

        /** Returns true if both the names and positions of these two markers match. */
        bool operator== (const Marker&) const throw();
        /** Returns true if either the name or position of these two markers differ. */
        bool operator!= (const Marker&) const throw();
    };

    //==============================================================================
    /** Returns the number of markers in the list. */
    int getNumMarkers() const throw();

    /** Returns one of the markers in the list, by its index. */
    const Marker* getMarker (int index) const throw();

    /** Returns a named marker, or 0 if no such name is found.
        Note that name comparisons are case-sensitive.
    */
    const Marker* getMarker (const String& name) const throw();

    /** Sets the position of a marker.

        If the name already exists, then the existing marker is moved; if it doesn't exist, then a
        new marker is added.
    */
    void setMarker (const String& name, const RelativeCoordinate& position);

    /** Deletes the marker at the given list index. */
    void removeMarker (int index);

    /** Deletes the marker with the given name. */
    void removeMarker (const String& name);

    /** Returns true if all the markers in these two lists match exactly. */
    bool operator== (const MarkerList& other) const throw();
    /** Returns true if not all the markers in these two lists match exactly. */
    bool operator!= (const MarkerList& other) const throw();

    //==============================================================================
    /**
        A class for receiving events when changes are made to a MarkerList.

        You can register a MarkerList::Listener with a MarkerList using the MarkerList::addListener()
        method, and it will be called when markers are moved, added, or deleted.

        @see MarkerList::addListener, MarkerList::removeListener
    */
    class JUCE_API  Listener
    {
    public:
        /** Destructor. */
        virtual ~Listener() {}

        /** Called when something in the given marker list changes. */
        virtual void markersChanged (MarkerList* markerList) = 0;

        /** Called when the given marker list is being deleted. */
        virtual void markerListBeingDeleted (MarkerList* markerList);
    };

    /** Registers a listener that will be called when the markers are changed. */
    void addListener (Listener* listener);

    /** Deregisters a previously-registered listener. */
    void removeListener (Listener* listener);

    /** Synchronously calls markersChanged() on all the registered listeners. */
    void markersHaveChanged();

    //==============================================================================
    /** Forms a wrapper around a ValueTree that can be used for storing a MarkerList. */
    class ValueTreeWrapper
    {
    public:
        ValueTreeWrapper (const ValueTree& state);

        ValueTree& getState() throw()       { return state; }
        int getNumMarkers() const;
        const ValueTree getMarkerState (int index) const;
        const ValueTree getMarkerState (const String& name) const;
        bool containsMarker (const ValueTree& state) const;
        const MarkerList::Marker getMarker (const ValueTree& state) const;
        void setMarker (const MarkerList::Marker& marker, UndoManager* undoManager);
        void removeMarker (const ValueTree& state, UndoManager* undoManager);

        void applyTo (MarkerList& markerList);
        void readFrom (const MarkerList& markerList, UndoManager* undoManager);

        static const Identifier markerTag, nameProperty, posProperty;

    private:
        ValueTree state;
    };

private:
    //==============================================================================
    OwnedArray<Marker> markers;
    ListenerList <Listener> listeners;

    JUCE_LEAK_DETECTOR (MarkerList);
};


#endif   // __JUCE_MARKERLIST_JUCEHEADER__