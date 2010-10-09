import logging
import time
import logging
import sys
from logging import StreamHandler
import httplib


if __name__ == "__main__":
    log = logging.getLogger("a")
    log.setLevel(logging.DEBUG)

    if len(log.handlers) == 0:
        handler = StreamHandler(sys.stdout)
        log.addHandler(handler)
        handler.setLevel(logging.DEBUG)
        log.info("Added stream handler")

    log.info("Running...")

    # Centers on Virginia Tech campus
    """lat = 37.22267
    lon = -80.42198

    import random

    for i in range(0, 200):
      x = random.random()
      y = random.random()
      conn = httplib.HTTPConnection('localhost', 8084)
      if i < 100:
        conn.request("GET", "/gheat/data/add/%f/%f/1/1/4.cmd" % (lat + x, lon + y))
      else:
        conn.request("GET", "/gheat/data/add/%f/%f/1/1/4.cmd" % (lat - x, lon - y))
      resp = conn.getresponse()
      if resp.status == 200:
        log.info("Request successful");
      else:
        log.warn("Request failed with code %d and message %s" % (resp.status, resp.reason))
    """

    lat = -90
    lon = -180

    for cur_lon in range(-180, 180, 20):
        for cur_lat in range(-90, 90, 10):
            conn = httplib.HTTPConnection('localhost', 8087)
            conn.request("GET", "/gheat/data/add/%f/%f/1/1/4.cmd" % (cur_lat, cur_lon))
            resp = conn.getresponse()
            if resp.status == 200:
                log.info("Request successful with (%d, %d)" % (cur_lat, cur_lon));
            else:
                log.warn("Request failed with code %d and message %s" % (resp.status, resp.reason))




    #url = "http://localhost:8084/gheat/data/add/%f/%f/%d/100/100.cmd" % \
    #    (lat, lon, int(time.time()))
    #response = fetch(url, deadline=1)
