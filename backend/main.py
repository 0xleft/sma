import pymongo
import flask
import hashlib

client = pymongo.MongoClient("mongodb://localhost:27017/")
db = client["sma"]

api = flask.Blueprint("sma", __name__)

@api.route("/api/sma/messages", methods=["GET"])
def get_messages():

    phone_number = flask.request.args.get("phoneNumber")
    secret_string = flask.request.headers.get("secretString")

    user = db["users"].find_one({"phoneNumber": phone_number, "secretString": hashlib.sha256(secret_string.encode()).hexdigest()})
    if user is None:
        return flask.jsonify({"error": "Invalid credentials"}), 401
    
    messages = db["messages"].find({"to": phone_number})

    messages_out = [{
        "from": message["from"],
        "message": message["message"]
    } for message in messages]

    db["messages"].delete_many({"to": phone_number})

    return flask.jsonify({"messages": messages_out})

@api.route("/api/sma/messages/send", methods=["POST"])
def send_message():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 401
    
    if "secretString" not in data or "phoneNumber" not in data or "message" not in data or "to" not in data:
        return flask.jsonify({"error": "Invalid request"}), 402
    
    secret_string = str(data["secretString"])
    phone_number = str(data["phoneNumber"])
    message = str(data["message"])
    to_number = str(data["to"])

    user = db["users"].find_one({"phoneNumber": phone_number, "secretString": hashlib.sha256(secret_string.encode()).hexdigest()})
    if user is None:
        return flask.jsonify({"error": "Invalid credentials"}), 403
    
    db.messages.insert_one({
        "from": phone_number,
        "to": to_number,
        "message": message,
    })

    return flask.jsonify({"success": True})

@api.route("/api/sma/auth/register", methods=["POST"])
def register():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 401
    
    if "phoneNumber" not in data or "publicKey" not in data or "secretString" not in data:
        return flask.jsonify({"error": "Invalid request"}), 402
    
    phone_number = str(data["phoneNumber"])
    public_key = str(data["publicKey"])
    secret_string = str(data["secretString"])

    if db["users"].find_one({"phoneNumber": phone_number}) is not None:
        return flask.jsonify({"error": "User already exists"}), 403
    
    db["users"].insert_one({
        "phoneNumber": phone_number,
        "publicKey": public_key,
        "secretString": hashlib.sha256(secret_string.encode()).hexdigest(),
    })

    return flask.jsonify({"success": True})

@api.route("/api/sma/auth/get_public_key", methods=["GET"])
def get_public_key():
    phone_number = flask.request.args.get("phoneNumber")

    user = db["users"].find_one({"phoneNumber": phone_number})
    if user is None:
        return flask.jsonify({"error": "User does not exist"}), 200
    
    return flask.jsonify({"publicKey": user["publicKey"]})