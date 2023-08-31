import flask
import pymongo
import os
import waitress
import hashlib
import time
from blueprints import auth, message, debug

app = flask.Flask(__name__)

app.secret_key = os.urandom(10000)

client = pymongo.MongoClient("mongodb://mongodb:27017/")
db = client["main"]

if __name__ == "__main__":
    app.register_blueprint(auth.api)
    app.register_blueprint(message.api)
    app.register_blueprint(debug.api)

    waitress.serve(app, port=5000)