import pymongo
import flask

client = pymongo.MongoClient("mongodb://localhost:27017/")
db = client["sma"]

api = flask.Blueprint("debug", __name__)

@api.route("/debug/get_users", methods=["GET"])
def get_users():
    users = db["users"].find()
    return flask.jsonify({
        "users": [{
            "phoneNumber": user["phoneNumber"],
            "publicKey": user["publicKey"],
            "secretString": user["secretString"],
        } for user in users]
    })

@api.route("/debug/get_messages", methods=["GET"])
def get_messages():
    messages = db["messages"].find()
    return flask.jsonify({
        "messages": [{
            "from": message["from"],
            "to": message["to"],
            "message": message["message"],
        } for message in messages]
    })