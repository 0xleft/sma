import pymongo
import flask

client = pymongo.MongoClient("mongodb://localhost:27017/")
db = client["sma"]

api = flask.Blueprint("auth", __name__)

@api.route("/auth/register", methods=["POST"])
def register():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    if "phoneNumber" not in data or "publicKey" not in data or "secretString" not in data:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    phone_number = str(data["phoneNumber"])
    public_key = str(data["publicKey"])
    secret_string = str(data["secretString"])

    if db["users"].find_one({"phoneNumber": phone_number}) is not None:
        return flask.jsonify({"error": "User already exists"}), 400
    
    db["users"].insert_one({
        "phoneNumber": phone_number,
        "publicKey": public_key,
        "secretString": secret_string,
    })

    return flask.jsonify({"success": True})

@api.route("/auth/get_public_key", methods=["POST"])
def get_public_key():
    data = flask.request.get_json(silent=True, force=True)
    if data is None:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    if "phoneNumber" not in data:
        return flask.jsonify({"error": "Invalid request"}), 400
    
    phone_number = str(data["phoneNumber"])

    user = db["users"].find_one({"phoneNumber": phone_number})
    if user is None:
        return flask.jsonify({"error": "User does not exist"}), 400
    
    return flask.jsonify({"publicKey": user["publicKey"]})