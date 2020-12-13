# Contact Tracing
**Prerequisite- DB uploaded to server**

**Steps to start server**
- Packages: flask, pyngrok, werkzeug
- python server.py
- Note HTTP URL: Tunnel_URL
- hit GET endpoint for testing: <Tunnel_URL>/
- hit POST endpoint for saving DB(attached file): <Tunnel_URL>/db
- hit POST endpoint for getting contact graph: <Tunnel_URL>/contacts