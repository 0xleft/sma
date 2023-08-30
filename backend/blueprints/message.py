import pymongo
import flask

client = pymongo.MongoClient("mongodb://localhost:27017/")
db = client["main"]

api = flask.Blueprint("messages", __name__)

@api.route("/messages", methods=["POST"])
def get_messages():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    if "secretString" not in data or "phoneNumber" not in data:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    secret_string = str(data["secretString"])
    phone_number = str(data["phoneNumber"])

    user = db["users"].find_one({"phoneNumber": phone_number, "secretString": secret_string})
    if user is None:
        return flask.jsonify({"error": "Invalid credentials"}), 400
    
    messages = db["messages"].find({"to": phone_number})
    db["messages"].delete_many({"to": phone_number})

    return flask.jsonify({
        "messages": [{
            "from": message["from"],
            "to": message["to"],
            "message": message["message"],
        } for message in messages]
    })

@api.route("/messages/send", methods=["POST"])
def send_message():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    if "secretString" not in data or "phoneNumber" not in data or "message" not in data or "to" not in data:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    secret_string = str(data["secretString"])
    phone_number = str(data["phoneNumber"])
    message = str(data["message"])
    to_number = str(data["to"])

    user = db["users"].find_one({"phoneNumber": phone_number, "secretString": secret_string})
    if user is None:
        return flask.jsonify({"error": "Invalid credentials"}), 400
    
    db.messages.insert_one({
        "from": phone_number,
        "to": to_number,
        "message": message,
    })

    return flask.jsonify({"success": True})