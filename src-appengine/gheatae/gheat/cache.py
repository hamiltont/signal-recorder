
"""
This file declares the interface that a port-specific cache needs to implement.

The Cache provided here does nothing, as we cannot make assumptions about what
kind of persistent storage is available e.g. can we use the file system? A
database? This is an implementation of the strategy pattern.

The core files of the gheat code should never have know know exactly how the
cache works, they should only know that the cache has some available methods
for saving and retrieving data. This files declares the methods to overwrite

A standard gheat port has a file structure like:
$PORT_ROOT
    /$GHEAT_ROOT
    some_port_specific_file.py
    some_port_specific_file.py
    some_port_specific_file.py

If you are creating a port, and want to see if gheat works at all, consider
setting tile.cache = Cache() (where Cache() is the object declared in this file)
This will simply diable all caching. It's slow, but you can see if gheat works

TODO - This object uses layer, x, y as parameters all over. I should figure out
what these are for. Layer is a string (seems like any arbitrary string will do?)
that seems to correspond to a color_scheme, as defined by gheat

TODO - Modify the gheat core files and add in checks inside of the tile.py file's
Tile object's __init__ method. If either tile.cache or tile.provider are null
then print a nice exception indicating that gheat was not ported correctly
"""

class Cache(object):

  def __init__(self):
    pass

  def is_available(self, layer, x, y):
    return False

  def get_image(self, layer, x, y):
    return None

  def store_image(self, layer, x, y, image_dump):
    return None