
from flask import Flask, jsonify, request, redirect, session
# from flask_ngrok import run_with_ngrok
from flask_dance.consumer import OAuth2ConsumerBlueprint
import bcrypt
from models import User
from requests_oauthlib import OAuth2Session
from requests_oauthlib.compliance_fixes import facebook_compliance_fix

app = Flask(__name__)
# run_with_ngrok(app)
app.secret_key = "<ISHOULDBESOMETHING>"
# batman_example = OAuth2ConsumerBlueprint(
#     "batman-example", __name__,
#     client_id="642784736153535",
#     client_secret="2c8b9d3b20909049d361b2e180f6b464",
#     base_url="https://graph.facebook.com",
#     authorization_url="https://www.facebook.com/dialog/oauth",
#     token_url="https://graph.facebook.com/oauth/access_token",
# )
# app.register_blueprint(batman_example, url_prefix="/login")
client_id = '642784736153535'
client_secret="2c8b9d3b20909049d361b2e180f6b464"
authorization_base_url="https://www.facebook.com/dialog/oauth"
token_url = 'https://graph.facebook.com/oauth/access_token'
redirect_uri = 'http://localhost:5000/callback'

facebook = OAuth2Session(client_id, redirect_uri=redirect_uri)
facebook = facebook_compliance_fix(facebook)


tasks = [
    {
        'id': 1,
        'title': u'Buy groceries',
        'description': u'Milk, Cheese, Pizza, Fruit, Tylenol', 
        'done': False
    },
    {
        'id': 2,
        'title': u'Learn Python',
        'description': u'Need to find a good Python tutorial on the web', 
        'done': False
    }
]
@app.route('/bhacks/<int:task_id>', methods=['GET'])
def get_tasks(task_id):
	print(task_id)
	return jsonify({'tasks': tasks})

@app.route('/facebook_login_verify', methods=['POST', 'GET'])
def facebook_login_verify():
	facebook = OAuth2Session(client_id, redirect_uri=redirect_uri)
	authorization_url, state = facebook.authorization_url(authorization_base_url)
	print ('Please go here and authorize,', authorization_url)
	session['oauth_state'] = state
	return redirect(authorization_url)
	# redirect_response = raw_input('http://localhost:5000/dashboard')
	# facebook.fetch_token(token_url, client_secret=client_secret, authorization_response=redirect_response)
	# r = facebook.get('https://graph.facebook.com/me?')
	# print(r.content)

@app.route('/callback', methods=['GET'])
def dashboard():
	facebook = OAuth2Session(client_id, state=session['oauth_state'])
	token = facebook.fetch_token(token_url, client_secret=client_secret,authorization_response=request.url)
	session['oauth_token'] = token
	return redirect(url_for('.profile'))

@app.route("/profile", methods=["GET"])
def profile():
    """Fetching a protected resource using an OAuth 2 token.
    """
    facebook = OAuth2Session(client_id, token=session['oauth_token'])
    return jsonify(facebook.get('https://graph.facebook.com/me?').json())

@app.route('/twitter_login_verify', methods=['POST', 'GET'])
def twitter_login_verify():
	pass


@app.route('/signup', methods=['POST', 'GET'])
def signup():
	if request.method=='POST':
		print(request.get_json())
		first_name = request.get_json()['first_name']
		last_name = request.get_json()['last_name']
		email = request.get_json()['email']
		age = request.get_json()['age']
		hashed_password = bcrypt.hashpw(request.get_json()['password'].encode('utf-8'), bcrypt.gensalt(10)) 
		user = User.query.filter_by(email=email).first() or None
		
		if user is None:
			user_data= User(first_name=request.get_json()['first_name'],last_name=request.get_json()['last_name'], email=email,
	 							age=str(request.get_json()['age']), password=hashed_password.decode('utf-8'), rp_level=1, facebook_verified='No', twitter_verified='No',truth_factor=int(20))# storing the hashed password in the collection
			user_data.save()
			return jsonify({'result' : 'Success'})
		else:
			return jsonify({'result' : 'Error! Email already registered'})
	return jsonify({'decode': 'get12332122'})

if __name__ == '__main__':
    app.run()