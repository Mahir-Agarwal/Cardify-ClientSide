import urllib.request
import urllib.error
import json
import random

BASE_URL = "http://10.231.255.244:8080/api"

def request(endpoint, method="GET", body=None, token=None):
    url = f"{BASE_URL}{endpoint}"
    req = urllib.request.Request(url, method=method)
    if body:
        req.add_header("Content-Type", "application/json")
        data = json.dumps(body).encode('utf-8')
    else:
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
        # if res_body can be parsed as JSON, return it
        try:
            return e.code, json.loads(res_body)
        except:
            return e.code, res_body

print("--- API END-TO-END VERIFICATION ---")

# 1. REGISTER
email = f"test_{random.randint(1000,9999)}@example.com"
status, data = request("/auth/register", method="POST", body={
    "name": "Mahir Aggarwal",
    "email": email,
    "password": "securepassword123",
    "roles": ["BUYER", "OWNER"]
})
print("REGISTER:", status, data)
token = data.get("token")

if token:
    print("\n[+] Token retrieved successfully.")
    
    # 2. LOGIN
    status, login_data = request("/auth/login", method="POST", body={
        "email": email,
        "password": "securepassword123"
    })
    print("\nLOGIN:", status, login_data)
    
    # 3. ADD CARD
    status, card_data = request("/cards", method="POST", body={
        "bankName": "HDFC",
        "cardType": "CREDIT",
        "available": True
    }, token=token)
    print("\nADD CARD:", status, card_data)
    
    if "id" in card_data:
        card_id = card_data["id"]
        
        # 4. SEARCH CARDS
        status, search_data = request("/cards/search", method="GET", token=token)
        print("\nSEARCH CARDS:", status, search_data)
        
        # 5. REQUEST ORDER
        status, order_data = request("/orders/request", method="POST", body={
            "cardId": card_id,
            "amount": 1000.0,
            "commission": 50.0
        }, token=token)
        print("\nREQUEST ORDER:", status, order_data)
        
        if "id" in order_data:
            order_id = order_data["id"]
            
            # 6. ACCEPT ORDER
            status, accept_data = request(f"/orders/{order_id}/accept", method="PUT", token=token)
            print("\nACCEPT ORDER:", status, accept_data)
            
            # 7. PAY ORDER
            status, pay_data = request(f"/orders/{order_id}/pay", method="POST", token=token)
            print("\nPAY ORDER:", status, pay_data)
            
            # 8. CHAT
            status, chat_data = request(f"/chat/{order_id}", method="POST", body={
                "receiverId": 2,
                "message": "Hi, did you place the order?"
            }, token=token)
            print("\nSEND CHAT:", status, chat_data)
            
            # 9. GET CHAT
            status, chat_hist = request(f"/chat/{order_id}", method="GET", token=token)
            print("\nCHAT HISTORY:", status, chat_hist)
    else:
        print("Card ID not found in response, stopping flow.")
else:
    print("[-] Registration failed or returned no token.")
