import cgi
import os

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class Landing(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""
            <html><body>
            This is the landing page. It's pretty boring right now.<br />
            <br />
            See <a href="/gheat/tile?docs=1">tile</a> or
            <a href="/gheat/data?docs=1">data</a>

            </body></html>
            """)


application = webapp.WSGIApplication(
    [('/.*', Landing)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
