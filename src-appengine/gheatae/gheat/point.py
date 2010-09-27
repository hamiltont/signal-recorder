"""
This file declares the interface that a port-specific point needs to implement.

The Point provided here always returns fake values for lat / lon. This is
an implementation of the strategy pattern.

The core files of the gheat code should never have know know exactly how the
point works, they should only know that the point has some available methods
for retrieving data. This files declares the methods to overwrite

A standard gheat port has a file structure like:
$PORT_ROOT
    /$GHEAT_ROOT
    some_port_specific_file.py
    some_port_specific_file.py
    some_port_specific_file.py

If you are creating a port, and want to see if gheat works at all, consider
using the default provider, as it will use the Point object declared in this
file

"""

import logging
import consts

class Point(object):
  def __init__(self, lat, lon):
    self._was_user_warned = False
    self._warning_message = """This is the default Point object. It provides
        only fake data and should be overridden with a point of some sort before
        gheat is used in production. """

    self.lat = lat
    self.lon = lon
    pass

  def get_lat(self):
    return self.lat

  def get_lon(self):
    return self.lon
