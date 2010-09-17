"""
This file declares the interface that a port-specific provider needs to implement.

The Provider provided here does nothing, as we cannot make assumptions about what
kind of persistent storage is available e.g. can we use the file system/
database? This is an implementation of the strategy pattern.

The core files of the gheat code should never have know know exactly how the
provider works, they should only know that the provider has some available
methods for retrieving data. This files declares the methods to override

A standard gheat port has a file structure like:
$PORT_ROOT
    /$GHEAT_ROOT
    some_port_specific_file.py
    some_port_specific_file.py
    some_port_specific_file.py

TODO - Create a generic dummy provider that will return new copies of some data
    type

TODO - This object uses x and y as parameters. I should figure out what these
    are for.

TODO - Modify the gheat core files and add in checks inside of the tile.py file's
Tile object's __init__ method. If either tile.cache or tile.provider are null
then print a nice error indicating that gheat was not ported correctly
"""


class Provider(object):

  def __init__(self):
    pass

  def get_data(self, layer, x, y):
    pass
