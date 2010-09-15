from gheatae.gheatae import color_scheme, dot, tile, cache, provider
from gheatae.gheatae.tile import Tile
from gheatae.gheatae import consts
from gheatae import handler

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from os import environ

import logging
import time


__docs__  = \
"""
<html><body>
This page is basically for requesting tile images.

Proper URI format is in the form
<blockquote>www.host.com/path_to_retrieve_tiles/desired_layer/zoom_level/y-value,x-value.png</blockquote>
For this server, an example would be
<blockquote>signalrecorder.appspot.com/gheat/tile/classic/4/4,6.png</blockquote>
You can always append ?docs=1 to the end of your URL to get the docs
for this file.
<br>TODO: Add in appropriate possibilities for each of the parameters
<ul><li>zoom_level can be 0 <= zoom_level <= 30</li></ul>
</html></body>
"""

log = logging.getLogger('tile')

# Set the cache and provider to an app engine-specific cache and provider we
# created
tile.cache = cache.Cache()
#tile.provider = provider.DBProvider()
tile.provider = provider.DummyProvider()

class GetTile(handler.Handler):

    def get(self):
        if '1' == self.request.get('docs'):
            self.response.out.write(__docs__)
            return;
        log.info("Running GetTile:GET...")
        st = time.clock()
        path = environ['PATH_INFO']

        log.debug("Path:" + path)
        if False == path.endswith('.png'):
            self.customError("Invalid path, needs to end with .png")
            return

        raw = path[:-4] # everything except .png
        try:
            assert raw.count('/') >= 3, \
                "%d slashes('/') found in '%s', need at least 3" % \
                (raw.count('/'), raw)

            """ TODO The layer is not being used right now (at all, not just here)
                     I should try to figure out what it was intended for
            """
            layer, zoom, yx = raw.split('/')[-3:]
#              assert color_scheme in color_schemes, ("bad color_scheme: "
#                                                    + color_scheme
#                                                     )
            assert yx.count(',') == 1, "%d commas(',') found in '%s', \
                can only have 1" % (yx.count(','), yx)

            y, x = yx.split(',')

            assert zoom.isdigit() and x.isdigit() and y.isdigit(), "One of \
                these values is not a digit. All of them need to be digits.\
                <br /> x: %s, y: %s, zoom_level: %s" % (x,y,zoom)

            zoom = int(zoom)
            x = int(x)
            y = int(y)
            assert 0 <= zoom <= (consts.MAX_ZOOM - 1), "Requested zoom_Level \
                was: %d. This is not appropriate" % zoom

        except AssertionError, err:
            log.error(err.args[0])
            self.customError(err)
            return

        #    color_scheme = color_schemes[color_scheme]
        #try:
        tile = Tile(layer, zoom, x, y)
        log.info("Start-B1: %2.2f" % (time.clock() - st))
        #    except Exception, err:
        #      self.respondError(err)
        #      raise err
        #      return

        self.response.headers['Content-Type'] = "image/png"

        log.info("Building or retrieving image...")
        img_data = tile.image_out()
        log.info("Start-B2: %2.2f" % (time.clock() - st))

        log.info("Writing out image...")
        self.response.out.write(img_data)
        log.info("Start-End: %2.2f" % (time.clock() - st))

    def customError(self, message):
        err = """<html><body>
            <h2>Error</h2>
            Proper URI format is in the form
            <blockquote>www.host.com/path_to_retrieve_tiles/desired_layer/zoom_level/y-value,x-value.png</blockquote>
            For this server, an example would be
            <blockquote>signalrecorder.appspot.com/gheat/tile/classic/4/4,6.png</blockquote>
            You can always append ?docs=1 to the end of your URL to get the docs
            for this file. <br>See <a href='%s%s'>the docs for this page</a><br>
            <br>TODO: Add in appropriate possibilities for each of the parameters
            <ul><li>zoom_level can be %d <= zoom_level <= %d</li></ul>
            <h3>Specific Error Message</h3>""" % (environ['PATH_INFO'], \
                                                  '?docs=1',0, consts.MAX_ZOOM)
        err =  err + str(message)
        err += "</body></html>"
        self.respondError(err)



application = webapp.WSGIApplication(
    [('/.*', GetTile)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()