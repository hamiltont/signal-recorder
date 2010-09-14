import cgi
import os
import exceptions

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.ext.db import GeoPt
from google.appengine.ext.webapp import template


# Contains the GSM specific info found at http://d.android.com/reference/android/telephony/SignalStrength.html
class GsmRecord(db.Model):
    location = db.GeoPtProperty()
    dateAdded = db.DateTimeProperty(auto_now_add=True)
    dateRecorded = db.DateTimeProperty()
    bitErrorRate = db.IntegerProperty()
    signalStrength = db.IntegerProperty()
    accuracy = db.FloatProperty()
    
    
class Upload(webapp.RequestHandler):

    def post(self):
        
        r = self.request;
        
        lat = r.get('lat')
        if lat == '':
            self.error("The '%s' parameter must be passed \
                       in order to upload a new value" % 'lat')
            return
        
        try:
            lat = float(lat)
        except exceptions.ValueError:
            self.error("Unable to convert parameter '%s' \
                       value '%s' to a float" % ('lat', lat))
            return
        
        lon = r.get('lon')
        if lon == '':
            self.error("The '%s' parameter must be passed \
                       in order to upload a new value" % 'lon')
            return
        
        try:
            lon = float(lon)
        except exceptions.ValueError:
            self.error("Unable to convert parameter '%s' \
                       value '%s' to a float" % ('lon', lon))
            return        
        
        
        accuracy = r.get('accuracy')
        if accuracy == '':
            self.error("The '%s' parameter must be passed \
                       in order to upload a new value" % 'accuracy')
            return
        
        try:
            accuracy = float(accuracy)
        except exceptions.ValueError:
            self.error("Unable to convert parameter '%s' \
                       value '%s' to a float" % ('accuracy', accuracy))
            return        

        
        record = GsmRecord()
        
        record.location = GeoPt(lat, lon)
        record.accuracy = accuracy
        
        
        
        self.response.out.write('Success');
        

    def get(self):
        self.post()
    
    def error(self, error_message):
        self.response.set_status(400)
        self.response.out.write('<h2>Error Message was: </h2>')
        self.response.out.write(error_message)
        
        self.response.out.write('<h3>Request URI</h3>')
        self.response.out.write(self.request.uri)
        self.response.out.write('<h3>Request Headers</h3>')
        self.response.out.write(self.request.headers)
        self.response.out.write('<h3>Request Body</h3>')
        self.response.out.write(self.request.body)
        
        
        
application = webapp.WSGIApplication(
    [('/upload', Upload)],
    debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
