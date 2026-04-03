import urllib.request
import urllib.error
import json
import random

BASE_URL = "http://10.192.136.244:8080/api"

def request(endpoint, method="POST", body=None, token=None):
    url = f"{BASE_URL}{endpoint}"
    req = urllib.request.Request(url, method=method)
    if body:
        req.add_header("Content-Type", "application/json")
        data = json.dumps(body).encode('utf-8')
    else:
        # If POSTing without a body, some servers expect empty body
        if method == "POST":
            req.add_header("Content-Length", "0")
        data = None
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    try:
        response = urllib.request.urlopen(req, data=data)
        res_body = response.read().decode('utf-8')
        if res_body:
            return response.status, json.loads(res_body)
        return response.status, {}
    except urllib.error.HTTPError as e:
        res_body = e.read().decode('utf-8')
        try:
            return e.code, json.loads(res_body)
        except:
            return e.code, res_body

print("--- TESTING TWO-USER FLOW ---")

# User 1: The Owner
owner_email = f"owner_{random.randint(1000,9999)}@example.com"
status, data = request("/auth/register", method="POST", body={
    "name": "Card Owner", "email": owner_email, "password": "pass", "roles": ["OWNER"]
})
owner_token = data.get("token")
print("OWNER LOGIN Token:", owner_token[:10] + "...")

# Owner Adds Card
status, card_data = request("/cards", method="POST", body={
    "bankName": "ICICI", "cardType": "CREDIT", "available": True
}, token=owner_token)
card_id = card_data["id"]
print("OWNER ADDED CARD ID:", card_id)

# User 2: The Buyer
buyer_email = f"buyer_{random.randint(1000,9999)}@example.com"
status, data = request("/auth/register", method="POST", body={
    "name": "Card Buyer", "email": buyer_email, "password": "pass", "roles": ["BUYER"]
})
buyer_token = data.get("token")
print("\nBUYER LOGIN Token:", buyer_token[:10] + "...")

# Buyer Searches Cards (POST)
status, search_data = request("/cards/search", method="POST", token=buyer_token)
print("BUYER SEARCH CARDS STATUS:", status)

# Buyer Requests Order on Owner's Card
status, order_data = request("/orders/request", method="POST", body={
    "cardId": card_id, "amount": 2500.0, "commission": 60.0
}, token=buyer_token)
print("BUYER REQUESTS ORDER STATUS:", status, order_data)
