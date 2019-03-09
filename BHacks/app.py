
from flask import Flask, jsonify, request, redirect, session, url_for
# from flask_ngrok import run_with_ngrok
from flask_dance.consumer import OAuth2ConsumerBlueprint
from flask_dance.contrib.twitter import make_twitter_blueprint, twitter
import bcrypt
from models import User
from requests_oauthlib import OAuth2Session
from requests_oauthlib.compliance_fixes import facebook_compliance_fix

app = Flask(__name__)
twitter_blueprint = make_twitter_blueprint(api_key='uQyFwItPPYK9EpXsurufWm52O', api_secret='lpe1StRf9QXhn8hsu2EQfh9KFwAd8hGMmZi7qHzpxZYfwSqh7Y')
app.register_blueprint(twitter_blueprint, url_prefix='/twitter_login')


app.secret_key = "<ISHOULDBESOMETHING>"


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

@app.route('/twitter_login_verify', methods=['POST', 'GET'])
def twitter_login_verify():
    if not twitter.authorized:
        return redirect(url_for('twitter.login'))
    account_info = twitter.get('account/settings.json')

    if account_info.ok:
        account_info_json = account_info.json()

        return '<h1>Your Twitter name is @{}'.format(account_info_json['screen_name'])

    return '<h1>Request failed!</h1>'

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