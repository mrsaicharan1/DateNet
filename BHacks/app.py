
from flask import Flask, jsonify, request, redirect, session, url_for
# from flask_ngrok import run_with_ngrok
from flask_dance.consumer import OAuth2ConsumerBlueprint
from flask_dance.contrib.twitter import make_twitter_blueprint, twitter
import bcrypt
from models import User
from requests_oauthlib import OAuth2Session
from requests_oauthlib.compliance_fixes import facebook_compliance_fix

app = Flask(__name__)
# run_with_ngrok(app) 
twitter_blueprint = make_twitter_blueprint(
    api_key='uQyFwItPPYK9EpXsurufWm52O', api_secret='lpe1StRf9QXhn8hsu2EQfh9KFwAd8hGMmZi7qHzpxZYfwSqh7Y')
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
    account_info = twitter.get('account/verify_credentials.json')

    if account_info.ok:
        session['first_name'] = 'shr'
        account_info_json = account_info.json()
        user = User.query.filter_by(first_name=session['first_name']).first()
        user.twitter_verified = 'Verified'
        user.truth_factor += 20
        user.save()
        return '<h1>Your Twitter account details are {}'.format(account_info_json)

    return '<h1>Request failed!</h1>'

@app.route('/fake_detector', methods=['POST', 'GET'])
def fake_detector():
	if not twitter.authorized:
		return redirect(url_for('twitter_login_verify'))
	else:
		account_info = twitter.get('account/verify_credentials.json')
		# print(account_info.json())
		account_info = account_info.json()
		account_details = [str(account_info['id']), account_info['id_str'],
						   account_info['screen_name'], account_info['description'],
						   str(account_info['url']),
						   str(account_info['followers_count']), str(account_info['friends_count']), str(account_info['listed_count']), str(account_info['created_at']),
						   str(account_info['favourites_count']), str(account_info['verified']), 
						   str(account_info['statuses_count']), str(account_info['lang']), 
						   str(account_info['status']), str(account_info['default_profile']),
						   str(account_info['default_profile_image']), 
						   str(account_info['has_extended_profile']), str(account_info['name'])]
		return "\t".join(account_details)	

@app.route('/signup', methods=['POST', 'GET'])
def signup():
	if request.method == 'POST':
		print(request.get_json())
		first_name = request.get_json()['first_name']
		last_name = request.get_json()['last_name']
		email = request.get_json()['email']
		age = request.get_json()['age']
		hashed_password = bcrypt.hashpw(
		    request.get_json()['password'].encode('utf-8'), bcrypt.gensalt(10))
		user = User.query.filter_by(email=email).first() or None

		if user is None:
			user_data = User(first_name=request.get_json()['first_name'], last_name=request.get_json()['last_name'], email=email,
	 							age=str(request.get_json()['age']), password=hashed_password.decode('utf-8'), rp_level=1, facebook_verified='No', twitter_verified='No', truth_factor=int(20))  # storing the hashed password in the collection
			user_data.save()
			return jsonify({'result': 'Success'})
		else:
			return jsonify({'result': 'Error! Email already registered'})
	return jsonify({'decode': 'get12332122'})


@app.route('/login', methods=['POST', 'GET'])
def login():
	if request.method == 'POST':
			user = User.query.filter_by(email=request.form['email']).first()
			if bcrypt.hashpw(request.form['password'].encode('utf-8'), user.password.encode('utf-8')) == user.password.encode('utf-8'):
				session['email'] = request.form['email']
				return jsonify({'user': user})
			else:
				return jsonify({'result': 'No such user'})
	return jsonify({'decode': 'getffffffff'})


if __name__ == '__main__':
    app.run(debug=True)
