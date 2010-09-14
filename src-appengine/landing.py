import cgi
import os

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class Landing(webapp.RequestHandler):
    def get(self):
        self.response.out.write("This is the landing page. It's pretty boring right now")
        
        
application = webapp.WSGIApplication(
    [('/.*', Landing)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
