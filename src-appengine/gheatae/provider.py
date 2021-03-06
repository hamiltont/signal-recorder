from gheatae.gheat.provider import Provider
from gheatae.gheat          import consts
from gheatae.point          import DataPoint

from gheatae.geo import geotypes

from google.appengine.api.datastore_types import GeoPt

import logging

log = logging.getLogger('tile')

#cache_georanges = [ [], ] * consts.MAX_ZOOM
#cache_zoom_step = [ [], ] * consts.MAX_ZOOM

#for zoom in range(consts.MAX_ZOOM):
#  width, height = gmerc.ll2px(-90, 180, zoom)
#  numcols = int(math.ceil(width / 256.0))
#  numrows = int(math.ceil(height / 256.0))
#  cache_zoom_step[zoom] = ( 180. / numrows, 360. / numcols )
#  cache_georanges[zoom] = [ [], ] * numrows
#  for y in range(numrows):
#    cache_georanges[zoom][y] = [ [], ] * numcols
#    for x in range(numcols):
#      cache_georanges[zoom][y][x] = ( 180. / numrows * y - 90, 360. / numcols * x - 180 )


class DBProvider(Provider):

  def get_data(self, zoom, layer, **extras):
    """
    Extras will contain these keys:
        - lat_north: The north-most latitude of the bounding box
        - lng_west: The west-most longitude of the bounding box
        - range_lat: The range of the bounding box latitude
        - range_lng: The range of the bounding box longitude
    """
    lat_north = extras['lat_north']
    lng_west = extras['lng_west']
    range_lat = extras['range_lat']
    range_lng = extras['range_lng']

    #log.info("GeoRange: (%6.4f, %6.4f) ZoomStep: (%6.4f, %6.4f)" % (lat_north, lng_west, range_lat, range_lng))
    #log.info("Range: (%6.4f - %6.4f), (%6.4f - %6.4f)" % (min(90, max(-90, lat_north + range_lat)), lat_north, min(180, max(-180, lng_west + range_lng)), lng_west))


    return DataPoint.bounding_box_fetch(
        DataPoint.all(),
        geotypes.Box(min(90, max(-90, lat_north + range_lat)),
            min(180, max(-180, lng_west + range_lng)),
            lat_north,
            lng_west),
        max_results=1000, )
