from flask import Flask
from flask_mongoalchemy import MongoAlchemy

app = Flask(__name__)

app.config['MONGOALCHEMY_DATABASE'] = 'bhacks'
app.config['MONGOALCHEMY_CONNECTION_STRING'] = 'mongodb://mrsaicharan1:anksbro1@ds151293.mlab.com:51293/bhacks'

db = MongoAlchemy(app)

class User(db.Document):
    first_name = db.StringField()
    last_name = db.StringField()
    password = db.StringField()
    email = db.StringField()
    age = db.StringField()
    truth_factor = db.IntField()
    rp_level = db.IntField()
    facebook_verified = db.StringField()
    twitter_verified = db.StringField()
    