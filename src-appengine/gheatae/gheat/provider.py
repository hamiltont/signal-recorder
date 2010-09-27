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

import logging
from logging import StreamHandler
import consts
from point import Point
import sys

class Provider(object):

  def __init__(self):
    warning_message = """This is the default Provider object. It provides
        no data at all and should be overridden with a provider of some sort
        before gheat is used in production. Without overriding this you will
        never see a heatmap"""
    log = logging.getLogger(consts.MAIN_LOG)

    log.warn(warning_message)
    pass


  def get_data(self, zoom, layer, **extras):
    """
    Extras will contain these keys:
        - lat_north: The north-most latitude of the bounding box
        - lng_west: The west-most longitude of the bounding box
        - range_lat: The range of the bounding box latitude
        - range_lng: The range of the bounding box longitude
    """

    p = Point(extras["lat_north"], extras["lng_west"])
    p2 = Point(extras["lat_north"], extras["lng_west"])
    return p, p2
