from geo.geomodel import GeoModel
from google.appengine.ext import db

class DataPoint(GeoModel):
    """
    Simple extension of GeoModel. This is mainly to separate the concerns of the
    GeoModel from the concerns of the gheat library e.g GeoModel does not care
    about time, weight, or range. Only gheat does
    """
    time = db.DateTimeProperty()
    weight = db.IntegerProperty()
    range = db.IntegerProperty()

    def get_lat(self):
        return self.location.lat

    def get_lon(self):
        return self.location.lon

