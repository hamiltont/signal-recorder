'''
Created on Nov 11, 2009

@author: ddelima

@contributors
    Hamilton Turner <hamiltont@gmail.com>
    Added catch-all ability
'''

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from gheatae import tile
from gheatae.data import Data

class CatchAll(webapp.RequestHandler):
    def get(self):
        self.response.out.write("This is the catch all page for gheat-ae. \
                                You probably don't want to be here<br /><br />")

        self.response.out.write('<h3>Request URI</h3>')
        self.response.out.write(self.request.uri)
        self.response.out.write('<h3>Request Headers</h3>')
        self.response.out.write(self.request.headers)
        self.response.out.write('<h3>Request Body</h3>')
        self.response.out.write(self.request.body)


    def post(self):
        self.get()


application = webapp.WSGIApplication(
   [('.*', CatchAll)],
   debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()
