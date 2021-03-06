from gheatae.handler import Handler
from gheatae.point import DataPoint

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api.datastore_types import GeoPt

from os import environ

import datetime
import logging
import time

__docs__ = \
"""
<html><body>
<p>
This page is basically for uploading new data to the gheat database. It only
accepts get.
</p>

Proper URI format is in the form
<blockquote>www.host.com/path_to_send_data/command/latitude/longitude/timeO\
ccurred/weight/range.cmd
</blockquote>

For this server, an example would be
<blockquote><a href="http://localhost:8084/gheat/data/add/37.123345/-86.5/1/1/1.cmd">
localhost:8084/gheat/data/add/37.123345/-86.5/1/1/1.cmd</a>
</blockquote>

<h4>Acceptable Parameter Values</h4>
<ul>
    <li>command<br>'add', 'del', 'upd'. However, only add is implemented
    for now, the other two will throw an error</li>
    <li>latitude / longitude <br>These should both be convertable to floats</li>
    <li>timeOccurred<br>This needs to be an integer, and it is parsed using
    datetime.datetime.fromtimestamp(), but other than that I don't know what
    format this is in </li>
    <li>weight<br>Needs to convert to int, but I don't know what it means</li>
    <li>range<br>Needs to convert to int, but I don't know what it means</li>
</ul>

<p>
You can always append ?docs=1 to the end of your URL to get the docs
for this file.
</p>

<br>TODO: Add in appropriate possibilities for each of the parameters

</html></body>
"""

log = logging.getLogger('tile')

class Data(Handler):

    def get(self):
        if '1' == self.request.get('docs'):
            self.response.out.write(__docs__)
            return;

        log.info("Running Data:GET...")
        st = time.clock()
        path = environ['PATH_INFO']

        if False == path.endswith('.cmd'):
            self.customError("URL must end with '.cmd'")
            return

        raw = path[:-4] # strip .cmd
        try:
            assert raw.count('/') >= 6, "There must be at least 6 slashes\
                ('/') in the URL. The URL of %s only has %d /'s" % \
                (raw, raw.count('/'))

            cmd, lat, lng, timeOccurred, weight, range = raw.split('/')[-6:]

            assert cmd in ('add', 'del', 'upd'), "The command parameter must be\
                 one of add, del, or upd. The passed value of '%s' is invalid" \
                 % cmd
            assert cmd == "add", "The only command parameter currently \
                supported is 'add'"
            assert float(lat), "The latitude must convert to a float. The passed\
                 value of '%s' does not" % lat
            assert float(lng), "The longitude must convert to a float. The passed\
                 value of '%s' does not" % lng
            assert datetime.datetime.fromtimestamp(int(timeOccurred)), "The time \
                must allow datetime.datetime.fromtimestamp(time). The passed \
                time of '%s' does not" % timeOccurred

            assert int(weight), "The weight must convert to an int. The passed\
                 value of '%s' does not" % weight
            assert int(range), "The range must convert to an int. The passed\
                 value of '%s' does not" % range
        except AssertionError, err:
            log.error(err.args[0])
            self.customError(err)
            return

        if cmd == "add":
            # Actually add the data specified
            lat = float(lat)
            lng = float(lng)
            location = GeoPt(lat, lng)
            timeOccurred = datetime.datetime.fromtimestamp(int(timeOccurred))
            weight = int(weight)
            range = int(range)

            new_data = DataPoint(location=location, time=timeOccurred, \
                                 weight=weight, range=range)
            new_data.update_location()  # Updates the geocell properties of the
                                        # location. Seems like this could go in
                                        # the __init__ of DataPoint
            new_data.put()
            log.info("Data Stored")
            self.response.out.write("Success")

        log.info("Start-End: %2.2f" % (time.clock() - st))

    def customError(self, message):
        err = """<html><body>
            <h2>Error</h2>
            <p>%s</p>
            <p>
            You can always append ?docs=1 to the end of your URL to get <a
            href='%s%s'>the docs for this file</a>.
            </body></html>
            """ % (str(message), environ['PATH_INFO'], '?docs=1')
        self.respondError(err)

class ClearAll(Handler):
    def get(self):
        from google.appengine.ext import db
        from gheatae.point import DataPoint
        from google.appengine.ext.db import GeoPt

        dps = DataPoint.all();
        for dp in dps:
            dp.delete()



application = webapp.WSGIApplication(
    [('.*', Data)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()