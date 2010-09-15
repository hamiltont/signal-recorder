from gheatae import color_scheme, dot, tile, cache, provider
from gheatae.tile import Tile

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from os import environ

import logging

log = logging.getLogger('tile')

class Handler(webapp.RequestHandler):

  def respondError(self, message):
    """
    message is printed straight to output, so do not include sensitive info and
    feel free to include html (although it needs to be within <html><body>your
    message</body></html> minimally)

    Status code is set to 400, which is a bit presumptious of the cause of the
    error. Perhaps later this method will be expanded to accept different status
    codes
    """
    #self.response.headers["Content-Type"] = 'text/plain'
    self.response.set_status(400)
    self.response.out.write(message)