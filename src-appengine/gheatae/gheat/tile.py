import color_scheme
from dot import dot
from cache import Cache
from provider import Provider
from pngcanvas import PNGCanvas
import consts

import gmerc

import logging
import math

log = logging.getLogger(consts.MAIN_LOG)

"""
This is an implementation of the strategy pattern. The core of the gheat code
should never have know know exactly how the provider and the cache work, they
should only know that the cache has some available methods for saving and
retrieving data, and that the provider has some available methods for providing
data

A standard gheat port has a file structure like:
$PORT_ROOT
    /$GHEAT_ROOT
    port_files.py
    port_files.py
    port_files.py

These two values are typically set in one of the port-specific files, allowing
that specific port to choose how the provider and the cache are implemented for
that system. In gheat-ae, tile.cache and tile.provider are set in
$PORT_ROOT/tile.py

Additionally, the interfaces that the port-specific files need to include are
defined in $GHEAT_ROOT/cache.py and $GHEAT_ROOT/provider.py
"""
provider = Provider()
cache = Cache()

LEVEL_MAX = 300

cache_levels = []

for i in range(LEVEL_MAX):
  cache_levels.append(int(((-(pow(float(i) - LEVEL_MAX, 2))/LEVEL_MAX) + LEVEL_MAX) / LEVEL_MAX * 255))

class Tile(object):
  """Typical usage of the tile object involves creation via the constructor,
  and then calling of Tile.image_out() to get the raw data

  __Properties__
  layer seems to be an arbitrary string
  zoom, x, y are all provided by the Google Maps query on gheat
  color_scheme is created dynamically. It provides a mapping from every possible
        'heat' value to a specific color

  width, height seem to be the number of pixels specifying the dimensions of
        this image
  numcols, numrows are the number of pixels in a single row or single column
        when trying to equally divide 256 rows and 256 columns
  zoom_step has something to do with the numrows & numcols
  georange is ???
  """

  def __init__(self, layer, zoom, x, y):
    log.info("Initializing tile");
    self.layer = layer
    self.zoom = zoom
    self.x = x
    self.y = y
    self.color_scheme = color_scheme.cyan_red

    # attempt to get a cached object
    self.tile_dump = self.__get_cached_image()
    if not self.tile_dump:
      log.info("No tile was found, creating one from the input data")

      # Get the bounds of this tile
      self.width, self.height = gmerc.ll2px(-90, 180, self.zoom)
      self.numcols = int(math.ceil(self.width / 256.0))
      self.numrows = int(math.ceil(self.height / 256.0))
      self.zoom_step = [ 180. / self.numrows, 360. / self.numcols ]
      self.georange = ( min(90, max(-90, 180. / self.numrows * y - 90)), min(180, max(-180, 360. / self.numcols * x - 180 )))

      # Extra info for provider.get_data
      extras = {"lat_north": self.georange[0],
                "lng_west": self.georange[1],
                "range_lat": self.zoom_step[0],
                "range_lng": self.zoom_step[1]}

      # Get the points and start plotting data
      log.info("Getting the tile points from the provider")
      data = provider.get_data(self.zoom, self.layer, **extras)

      log.info("Plotting the tile")
      self.tile_img = self.plot_image(data)

  def plot_image(self, points):
    """Given an array of points, returns the image data. Initially creates an
    empty array. Then, for each point the image for that point is retrieved. The
    image for a point it called the dot for that point. There are various 'dot'
    images available in progressively larger sizes. The image data in the
    appropriate dot image is added to the empty array at the appropriate
    position (retrieved from the point). This occurs for each point, effectively
    creating a single array of image data that contains all the merged points"""

    space_level = self.__create_empty_space()
    for point in points:
      self.__merge_point_in_space(space_level, point)

    log.info("Done merging points, converting data into png");
    return self.convert_image(space_level)

  def __merge_point_in_space(self, space_level, point):
    log.debug("Merging a point")
    # By default, multiply per color point
    dot_levels, x_off, y_off = self.get_dot(point)

    y_min = max(y_off, 0)
    y_max = min(y_off + len(dot_levels[0]), len(space_level))

    for y in range(y_off, y_off + len(dot_levels)):
      if y < 0 or y >= len(space_level):
        continue
      for x in range(x_off, x_off + len(dot_levels[0])):
        if x < 0 or x >= len(space_level[0]):
          continue
        dot_level = dot_levels[y_off - y][x_off - x]
        if dot_level <= 0.:
          continue
        space_level[y][x] += dot_level

  def convert_image(self, space_level):
    """Given an array containing the pixel-level infromation regarding coverage,
    this function first builds a color scheme array to correspond to different
    coverage values, and then fills in a PNG image with the corresponding color
    values for each coverage value. Essentially this function transforms coverage
    info into color info"""

    log.info("Creating png from coverage information")
    tile = PNGCanvas(len(space_level[0]), len(space_level), bgcolor=[0xff,0xff,0xff,0])

    log.debug("Creating color scheme")
    color_scheme = []
    for i in range(LEVEL_MAX):
      color_scheme.append(self.color_scheme.canvas[cache_levels[i]][0])
    log.debug("Color scheme created")

    for y in xrange(len(space_level[0])):
      for x in xrange(len(space_level[0])):
        tile.canvas[y][x] = color_scheme[int(space_level[y][x])]

    log.info("png created");
    return tile

  def get_dot(self, point):
    """Given a point, this returns the dot that matches that zoom level and the
    x/y offset of that dot. I think this is the offset within the image space
    e.g. within the pixels of the image"""

    #from random import random, Random
    #rdm = Random()
    #return dot[20], rdm.randint(-20, 260), rdm.randint(-20, 260)
    cur_dot = dot[self.zoom]
    y_off = int(math.ceil((-1 * self.georange[0] + point.get_lat()) / self.zoom_step[0] * 256. - len(cur_dot) / 2))
    x_off = int(math.ceil((-1 * self.georange[1] + point.get_lon()) / self.zoom_step[1] * 256. - len(cur_dot[0]) / 2))
    """
    log.info("lat, lng  dist_lng, dist_lng  Y_off, X_off:
            (%6.4f, %6.4f) (%6.4f, %6.4f) (%4d, %4d)" %
            (
                point.location.lat, point.location.lon,
                (-1 * self.georange[0] + point.location.lat) / self.zoom_step[0] * 256,
                (-1 * self.georange[1] + point.location.lon) / self.zoom_step[1] * 256,
                y_off,
                x_off)
            )
    """
    return cur_dot, x_off, y_off

  def __create_empty_space(self):
    log.info("Creating an array of empty image data")
    space = []
    for i in range(256):
      space.append( [0.] * 256 )
    return space

  def __get_cached_image(self):
    log.info("Getting cached image")
    log.info("Cache is apparently %s" % str(cache))
    if cache.is_available(self.layer, self.x, self.y):
      log.info("Cached image is available, returning get_image")
      return cache.get_image(self.layer, self.x, self.y)
    log.info("Cached image is not available")
    return None

  def __cache_image(self, tile_dump):
    return cache.store_image(self.layer, self.x, self.y, tile_dump)

  def image_out(self):
    """This method returns the image data or throws an exception if there was a
    problem. It attempts to check for a generated image. If one exists, it dumps
    the image data and attempts to cache the image. If no image dump is found,
    then the image was neither retrieved from cache or generated, and therefore
    an error has occurred"""
    log.info("Image data requested")
    if self.tile_img:
      log.info("Image was generated. Attempting to dump and cache")
      self.tile_dump = self.tile_img.dump()
      # attempt to cache this
      self.__cache_image(self.tile_dump)

    if self.tile_dump:
      log.info("Returning image")
      return self.tile_dump
    else:
      raise Exception("Failure in generation of image.")

  def __str__(self):
    s = "Gheat Tile: (x, y) = (%d, %d); (zoom, step) = (%d, [%f, %f]); (rows, cols) = (%d, %d)" \
    % (self.x, self.y,
     self.zoom, self.zoom_step[0], self.zoom_step[1],
     self.numrows, self.numcols)

    return s


# Adds the ability to run this file standalone for testing
if __name__ == '__main__':
    from logging import StreamHandler
    from logging import Formatter
    import sys

    log = logging.getLogger(consts.MAIN_LOG)
    log.setLevel(logging.DEBUG)

    if len(log.handlers) == 0:
        handler = StreamHandler(sys.stdout)
        log.addHandler(handler)
        handler.setLevel(logging.DEBUG)
        handler.setFormatter(Formatter("%(module)s.%(funcName)s() - %(message)s"))
        log.info("Added stream handler")

    log.info("Testing tile.py")

    t = Tile('classic', 5, 1,3)

    log.info("Tile is '%s'" % t)

    t.image_out()
