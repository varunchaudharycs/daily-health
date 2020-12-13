from flask import request, Flask
from werkzeug.utils import secure_filename
from generate_graph import contact_tracing
from pyngrok import ngrok
import os

app = Flask(__name__)

DIR = os.path.dirname(os.path.realpath(__file__))


def start_ngrok():
    url = ngrok.connect(5000)
    print(' * Tunnel URL:', url)


@app.route('/', methods=['GET'])
def handle_request_init():
    return "Hello, World!"


@app.route('/contacts', methods=['POST'])
def handle_request_contacts():

    try:
        data = request.json
        print(f"Handling request of user ID {data['userid']} for date of {data['date']}")
        adj = contact_tracing(data['userid'], data['date'])
        return str(adj)

    except Exception as e:
        print(f"ERROR: Handling Contact Tracing request for user ID and date!", e)


@app.route('/db', methods=['POST'])
def handle_request_db():

    try:
        file = request.files['file']
        filename = secure_filename(file.filename)
        print(f"Handling request saving DB file - {filename}")
        path = os.path.join(DIR, filename)
        file.save(path)
        print(f'File saved - {path}')

        return path

    except Exception as e:
        print(f"ERROR: Handling save DB request!", e)


if __name__ == '__main__':

    start_ngrok()
    app.run(host="0.0.0.0", port=5000, debug=True)

