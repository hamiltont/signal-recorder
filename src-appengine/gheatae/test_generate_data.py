from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api.urlfetch import fetch
from google.appengine.api.urlfetch import DownloadError
import logging
import time

log = logging.getLogger('test_tile')

class Test(webapp.RequestHandler):

  def get(self):
    log.info("Running...")

    lat = 39.81447
    lon = -98.565388

    self.response.out.write("<html><body>")
    for i in range(0, 10):
        lat += 0.005
        url = "http://localhost:8084/gheat/data/add/%f/%f/%d/100/100.cmd" % \
              (lat, lon, int(time.time()))
        self.response.out.write("Requesting %s . . ." % url)
        try:
            response = fetch(url, deadline=1)
            self.response.out.write("Done<br />")
        except DownloadError:
            self.response.out.write("Failed<br />")


application = webapp.WSGIApplication(
   [('.*', Test)],
   debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()
